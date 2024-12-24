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
package com.example.core.streamers.settings

import com.example.core.internal.encoders.AudioMediaCodecEncoder
import com.example.core.internal.encoders.VideoMediaCodecEncoder
import com.example.core.internal.sources.IAudioSource
import com.example.core.streamers.interfaces.settings.IAudioSettings
import com.example.core.streamers.interfaces.settings.IBaseStreamerSettings
import com.example.core.streamers.interfaces.settings.IVideoSettings

open class BaseStreamerSettings(
    override val audio: BaseStreamerAudioSettings,
    override val video: BaseStreamerVideoSettings
) : IBaseStreamerSettings {
    constructor(
        audioSource: IAudioSource?,
        audioEncoder: AudioMediaCodecEncoder?,
        videoEncoder: VideoMediaCodecEncoder?
    ) : this(
        BaseStreamerAudioSettings(audioSource, audioEncoder),
        BaseStreamerVideoSettings(videoEncoder)
    )
}

class BaseStreamerVideoSettings(private val videoEncoder: VideoMediaCodecEncoder?) :
    IVideoSettings {
    /**
     * Get/set video bitrate.
     * Do not set this value if you are using a bitrate regulator.
     */
    override var bitrate: Int
        /**
         * @return video bitrate in bps
         */
        get() = videoEncoder?.bitrate ?: 0
        /**
         * @param value video bitrate in bps
         */
        set(value) {
            videoEncoder?.let { it.bitrate = value }
        }
}

class BaseStreamerAudioSettings(
    private val audioSource: IAudioSource?,
    private val audioEncoder: AudioMediaCodecEncoder?
) :
    IAudioSettings {
    /**
     * Get/set audio bitrate.
     * Do not set this value if you are using a bitrate regulator.
     */
    override var bitrate: Int
        /**
         * @return audio bitrate in bps
         */
        get() = audioEncoder?.bitrate ?: 0
        /**
         * @param value audio bitrate in bps
         */
        set(value) {
            audioEncoder?.let { it.bitrate = value }
        }

    /**
     * Get/set audio mute
     */
    override var isMuted: Boolean
        /**
         * @return [Boolean.true] if audio is muted, [Boolean.false] if audio is running.
         */
        get() = audioSource?.isMuted ?: true
        /**
         * @param value [Boolean.true] to mute audio, [Boolean.false]to unmute audio.
         */
        set(value) {
            audioSource?.isMuted = value
        }
}