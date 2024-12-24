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
package com.example.extension_rtmp.streamers

import android.content.Context
import com.example.extension_rtmp.internal.endpoints.RtmpProducer
import com.example.core.internal.muxers.flv.FlvMuxer
import com.example.core.internal.muxers.flv.tags.video.ExtendedVideoTag
import com.example.core.listeners.OnConnectionListener
import com.example.core.listeners.OnErrorListener
import com.example.core.streamers.live.BaseCameraLiveStreamer

/**
 * A [BaseCameraLiveStreamer] that sends microphone and camera frames to a remote RTMP device.
 *
 * @param context application context
 * @param enableAudio [Boolean.true] to capture audio. False to disable audio capture.
 * @param initialOnErrorListener initialize [OnErrorListener]
 * @param initialOnConnectionListener initialize [OnConnectionListener]
 */
class CameraRtmpLiveStreamer(
    context: Context,
    enableAudio: Boolean = true,
    initialOnErrorListener: OnErrorListener? = null,
    initialOnConnectionListener: OnConnectionListener? = null
) : BaseCameraLiveStreamer(
    context = context,
    enableAudio = enableAudio,
    muxer = FlvMuxer(writeToFile = false),
    endpoint = RtmpProducer(),
    initialOnErrorListener = initialOnErrorListener,
    initialOnConnectionListener = initialOnConnectionListener
) {
    private val rtmpProducer = endpoint as RtmpProducer
    override suspend fun connect(url: String) {
        require(videoConfig != null) {
            "Video config must be set before connecting to send the video codec in the connect message"
        }
        val codecMimeType = videoConfig!!.mimeType
        if (ExtendedVideoTag.isSupportedCodec(codecMimeType)) {
            rtmpProducer.supportedVideoCodecs = listOf(codecMimeType)
        }
        rtmpProducer.supportedVideoCodecs = listOf(videoConfig!!.mimeType)
        super.connect(url)
    }
}
