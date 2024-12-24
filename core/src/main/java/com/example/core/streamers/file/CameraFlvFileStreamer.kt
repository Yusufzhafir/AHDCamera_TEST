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
package com.example.core.streamers.file

import android.content.Context
import com.example.core.internal.muxers.flv.FlvMuxer
import com.example.core.listeners.OnErrorListener
import java.io.File

/**
 * A [BaseCameraFileStreamer] that sends microphone and video frames to a FLV [File].
 *
 * @param context application context
 * @param enableAudio [Boolean.true] to capture audio. False to disable audio capture.
 * @param initialOnErrorListener initialize [OnErrorListener]
 */
class CameraFlvFileStreamer(
    context: Context,
    enableAudio: Boolean = true,
    initialOnErrorListener: OnErrorListener? = null
) : BaseCameraFileStreamer(
    context = context,
    muxer = FlvMuxer(writeToFile = true),
    enableAudio = enableAudio,
    initialOnErrorListener = initialOnErrorListener
)