/*
 * Copyright (C) 2021 Thibault B.
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
package com.example.core.internal.muxers.ts.packets

import com.example.core.internal.data.Frame
import com.example.core.internal.muxers.IMuxerListener
import com.example.core.internal.muxers.ts.data.Stream
import com.example.core.internal.muxers.ts.descriptors.AdaptationField
import com.example.core.internal.muxers.ts.packets.Pes.StreamId.Companion.fromMimeType
import com.example.core.internal.utils.TimeUtils
import com.example.core.internal.utils.extensions.isAudio
import com.example.core.internal.utils.extensions.isVideo

class Pes(
    muxerListener: IMuxerListener? = null,
    val stream: Stream,
    private val hasPcr: Boolean,
) : TS(muxerListener, stream.pid) {
    fun write(frame: Frame) {
        val programClockReference = if (hasPcr) {
            TimeUtils.currentTime()
        } else {
            null
        }
        val adaptationField = AdaptationField(
            discontinuityIndicator = stream.discontinuity,
            randomAccessIndicator = frame.isKeyFrame,
            programClockReference = programClockReference
        )

        val header = PesHeader(
            streamId = fromMimeType(stream.config.mimeType).value,
            payloadLength = frame.buffer.remaining(),
            pts = frame.pts,
            dts = frame.dts
        )

        write(frame.buffer, adaptationField.toByteBuffer(), header.toByteBuffer(), true, frame.pts)
    }

    enum class StreamId(val value: Short) {
        PRIVATE_STREAM_1(0xbd.toShort()),
        AUDIO_STREAM_0(0xc0.toShort()),
        VIDEO_STREAM_0(0xe0.toShort()),
        METADATA_STREAM(0xfc.toShort()),
        EXTENDED_STREAM(0xfd.toShort());

        companion object {
            fun fromMimeType(mimeType: String): StreamId {
                return when {
                    mimeType.isVideo -> {
                        VIDEO_STREAM_0
                    }
                    mimeType.isAudio -> {
                        AUDIO_STREAM_0
                    }
                    else -> {
                        METADATA_STREAM
                    }
                }
            }
        }
    }
}