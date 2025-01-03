/*
 * Copyright (C) 2022 Thibault B.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.core.internal.muxers.flv

import com.example.core.data.Config
import com.example.core.internal.data.Frame
import com.example.core.internal.data.Packet
import com.example.core.internal.data.PacketType
import com.example.core.internal.muxers.IMuxer
import com.example.core.internal.muxers.IMuxerListener
import com.example.core.internal.muxers.flv.tags.AVTagsFactory
import com.example.core.internal.muxers.flv.tags.FlvHeader
import com.example.core.internal.muxers.flv.tags.OnMetadata
import com.example.core.internal.orientation.ISourceOrientationProvider
import com.example.core.internal.utils.extensions.isAudio
import com.example.core.internal.utils.extensions.isVideo

class FlvMuxer(
    override var listener: IMuxerListener? = null,
    initialStreams: List<Config>? = null,
    private val writeToFile: Boolean,
) : IMuxer {
    override val helper = FlvMuxerHelper()
    private val streams = mutableListOf<Stream>()
    private val hasAudio: Boolean
        get() = streams.any { it.config.mimeType.isAudio }
    private val hasVideo: Boolean
        get() = streams.any { it.config.mimeType.isVideo }
    private var startUpTime: Long? = null
    private var hasFirstFrame = false

    init {
        initialStreams?.let { config -> streams.addAll(config.map { Stream(it) }) }
    }

    override var sourceOrientationProvider: ISourceOrientationProvider? = null

    override fun encode(frame: Frame, streamPid: Int) {
        if (!hasFirstFrame) {
            // Wait for first frame
            if (hasVideo) {
                // Expected first video key frame
                if (frame.isVideo && frame.isKeyFrame) {
                    startUpTime = frame.pts
                    hasFirstFrame = true
                } else {
                    // Drop
                    return
                }
            } else {
                // Audio only
                startUpTime = frame.pts
                hasFirstFrame = true
            }
        }
        if (frame.pts < startUpTime!!) {
            return
        }

        frame.pts -= startUpTime!!
        val stream = streams[streamPid]
        val sendHeader = stream.sendHeader
        stream.sendHeader = false
        val flvTags = AVTagsFactory(frame, stream.config, sendHeader).build()
        flvTags.forEach {
            listener?.onOutputFrame(
                Packet(
                    it.write(), frame.pts, if (frame.isVideo) {
                        PacketType.VIDEO
                    } else {
                        PacketType.AUDIO
                    }
                )
            )
        }
    }

    override fun addStreams(streamsConfig: List<Config>): Map<Config, Int> {
        val streamMap = mutableMapOf<Config, Int>()
        streams.addAll(streamsConfig.map { Stream(it) })
        requireStreams()
        streams.forEachIndexed { index, stream -> streamMap[stream.config] = index }
        return streamMap
    }

    override fun startStream() {
        // Header
        if (writeToFile) {
            listener?.onOutputFrame(
                Packet(
                    FlvHeader(hasAudio, hasVideo).write(),
                    0
                )
            )
        }

        // Metadata
        listener?.onOutputFrame(
            Packet(
                OnMetadata.fromConfigs(streams.map { it.config }, sourceOrientationProvider)
                    .write(),
                0
            )
        )
    }

    override fun stopStream() {
        startUpTime = null
        hasFirstFrame = false
        streams.clear()
    }

    override fun release() {
        // Nothing to release
    }

    /**
     * Check that there shall be no more than one audio and one video stream
     */
    private fun requireStreams() {
        val audioStreams = streams.filter { it.config.mimeType.isAudio }
        require(audioStreams.size <= 1) { "Only one audio stream is supported by FLV but got $audioStreams" }
        val videoStreams = streams.filter { it.config.mimeType.isVideo }
        require(videoStreams.size <= 1) { "Only one video stream is supported by FLV but got $videoStreams" }
    }

    private data class Stream(val config: Config) {
        var sendHeader = true
    }
}
