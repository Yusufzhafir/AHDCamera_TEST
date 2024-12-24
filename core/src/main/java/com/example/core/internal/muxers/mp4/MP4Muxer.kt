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
package com.example.core.internal.muxers.mp4

import com.example.core.data.Config
import com.example.core.internal.data.Frame
import com.example.core.internal.data.Packet
import com.example.core.internal.muxers.IMuxer
import com.example.core.internal.muxers.IMuxerListener
import com.example.core.internal.muxers.mp4.boxes.FileTypeBox
import com.example.core.internal.muxers.mp4.boxes.MovieFragmentRandomAccessBox
import com.example.core.internal.muxers.mp4.boxes.TrackFragmentRandomAccessBox
import com.example.core.internal.muxers.mp4.models.AbstractMovieBoxFactory
import com.example.core.internal.muxers.mp4.models.DefaultMP4SegmenterFactory
import com.example.core.internal.muxers.mp4.models.MP4Segmenter
import com.example.core.internal.muxers.mp4.models.MP4SegmenterFactory
import com.example.core.internal.muxers.mp4.models.MovieBoxFactory
import com.example.core.internal.muxers.mp4.models.MovieFragmentBoxFactory
import com.example.core.internal.muxers.mp4.models.Segment
import com.example.core.internal.muxers.mp4.models.Track
import com.example.core.internal.orientation.ISourceOrientationProvider
import com.example.core.internal.utils.TimeUtils
import com.example.core.internal.utils.extensions.isAudio
import com.example.core.internal.utils.extensions.isVideo
import java.nio.ByteBuffer

class MP4Muxer(
    initialListener: IMuxerListener? = null,
    private val timescale: Int = DEFAULT_TIMESCALE,
    private val segmenterFactory: MP4SegmenterFactory = DefaultMP4SegmenterFactory()
) : IMuxer {
    override val helper = MP4MuxerHelper()
    override var sourceOrientationProvider: ISourceOrientationProvider? = null

    override var listener: IMuxerListener? = initialListener
    private val tracks = mutableListOf<Track>()
    private val hasAudio: Boolean
        get() = tracks.any { it.config.mimeType.isAudio }
    private val hasVideo: Boolean
        get() = tracks.any { it.config.mimeType.isVideo }

    private var currentSegment: Segment? = null
    private var segmenter: MP4Segmenter? = null

    private var dataOffset: Long = 0
    private var sequenceNumber = DEFAULT_SEQUENCE_NUMBER

    override fun encode(frame: Frame, streamPid: Int) {
        synchronized(this) {
            if (segmenter!!.mustWriteSegment(frame)) {
                writeSegment()
            }
            currentSegment!!.add(frame, streamPid)
        }
    }

    override fun addStreams(streamsConfig: List<Config>): Map<Config, Int> {
        val newTracks = mutableListOf<Track>()
        streamsConfig.forEach { config ->
            val track = Track(getNewId(), config, timescale)
            newTracks.add(track)
            tracks.add(track)
        }

        val streamMap = mutableMapOf<Config, Int>()
        newTracks.forEach { streamMap[it.config] = it.id }
        return streamMap
    }

    override fun startStream() {
        writeBuffer(FileTypeBox().toByteBuffer())
        currentSegment = createNewSegment(MovieBoxFactory(timescale))
        segmenter = segmenterFactory.build(hasAudio, hasVideo)
    }

    override fun stopStream() {
        writeSegment(createNewFragment = false)
        writeMfraIfNeeded()
        sequenceNumber = DEFAULT_SEQUENCE_NUMBER
        dataOffset = 0
        currentSegment = null
        segmenter = null
        tracks.clear()
    }

    override fun release() {
    }

    private fun getNewId(): Int {
        val currentIds = tracks.map { it.id }
        (1..Int.MAX_VALUE).forEach {
            if (!currentIds.contains(it)) {
                return it
            }
        }

        throw IndexOutOfBoundsException("No empty ID left")
    }

    private fun createNewSegment(movieBoxFactory: AbstractMovieBoxFactory): Segment {
        return Segment(
            tracks,
            movieBoxFactory
        ) { buffer -> writeBuffer(buffer) }
    }

    private fun writeSegment(
        createNewFragment: Boolean = true,
    ) {
        currentSegment?.let { segment ->
            if (!segment.hasData) {
                return
            }

            segment.write(dataOffset)

            tracks.forEach { track ->
                track.syncSamples.add(
                    Track.SyncSample(
                        time = segment.getFirstTimestamp(track.id),
                        isFragment = segment.isFragment,
                        moofOffset = dataOffset
                    )
                )
            }
        }

        if (createNewFragment) {
            currentSegment = createNewSegment(MovieFragmentBoxFactory(sequenceNumber++))
        }
    }

    private fun writeMfraIfNeeded() {
        val tfras = tracks.filter {
            it.syncSamples.isNotEmpty()
        }.map { track ->
            // first sync is `moov` offset
            val firstTimestamp = track.firstTimestamp
            TrackFragmentRandomAccessBox(
                id = track.id,
                entries = track.syncSamples.filter { it.isFragment }.map { syncSample ->
                    TrackFragmentRandomAccessBox.Entry(
                        time = (syncSample.time - firstTimestamp) * track.timescale / TimeUtils.TIME_SCALE,
                        moofOffset = syncSample.moofOffset
                    )
                }
            )
        }
        if (tfras.isEmpty()) {
            return
        }
        val mfra = MovieFragmentRandomAccessBox(tfras)
        writeBuffer(mfra.toByteBuffer())
    }

    private fun writeBuffer(buffer: ByteBuffer) {
        val size = buffer.remaining()
        val packet = Packet(buffer, 0)
        listener?.let {
            it.onOutputFrame(packet)
            dataOffset += size
        }
    }

    companion object {
        private const val DEFAULT_SEQUENCE_NUMBER = 1
        private const val DEFAULT_TIMESCALE = TimeUtils.TIME_SCALE
    }
}