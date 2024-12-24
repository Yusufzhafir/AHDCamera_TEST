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
import com.example.core.internal.sources.camera.CameraSource
import com.example.core.streamers.bases.BaseCameraStreamer
import com.example.core.streamers.interfaces.settings.IBaseCameraStreamerSettings
import com.example.core.utils.CameraSettings


/**
 * Get the base camera settings ie all settings available for [BaseCameraStreamer].
 */
class BaseCameraStreamerSettings(
    audioSource: IAudioSource?,
    private val cameraSource: CameraSource,
    audioEncoder: AudioMediaCodecEncoder?,
    videoEncoder: VideoMediaCodecEncoder?
) : BaseStreamerSettings(audioSource, audioEncoder, videoEncoder), IBaseCameraStreamerSettings {
    /**
     * Get the camera settings (focus, zoom,...).
     */
    override val camera: CameraSettings
        get() = cameraSource.settings
}