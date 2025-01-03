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
package com.example.core.streamers.interfaces

import android.Manifest
import androidx.annotation.RequiresPermission
import com.example.core.data.AudioConfig
import com.example.core.data.VideoConfig
import com.example.core.error.StreamPackError
import com.example.core.listeners.OnErrorListener
import com.example.core.streamers.helpers.IConfigurationHelper
import com.example.core.streamers.interfaces.settings.IBaseStreamerSettings

interface IStreamer {
    /**
     * Listener that reports streamer error.
     * Supports only one listener.
     */
    var onErrorListener: OnErrorListener?

    /**
     * Access configuration helper.
     */
    val helper: IConfigurationHelper

    /**
     * Access extended streamer settings.
     */
    val settings: IBaseStreamerSettings

    /**
     * Configures only audio settings.
     *
     * @param audioConfig Audio configuration to set
     *
     * @throws [StreamPackError] if configuration can not be applied.
     * @see [release]
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun configure(audioConfig: AudioConfig)

    /**
     * Configures only video settings.
     *
     * @param videoConfig Video configuration to set
     *
     * @throws [StreamPackError] if configuration can not be applied.
     * @see [release]
     */
    fun configure(videoConfig: VideoConfig)

    /**
     * Configures both video and audio settings.
     *
     * @param audioConfig Audio configuration to set
     * @param videoConfig Video configuration to set
     *
     * @throws [StreamPackError] if configuration can not be applied.
     * @see [release]
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun configure(audioConfig: AudioConfig, videoConfig: VideoConfig)

    /**
     * Starts audio/video stream.
     *
     * To avoid creating an unresponsive UI, do not call on main thread.
     *
     * @see [stopStream]
     */
    suspend fun startStream()

    /**
     * Stops audio/video stream.
     *
     * @see [startStream]
     */
    suspend fun stopStream()

    /**
     * Clean and reset the streamer.
     *
     * @see [configure]
     */
    fun release()
}