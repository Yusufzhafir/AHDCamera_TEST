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
import com.example.core.internal.muxers.mp4.MP4Muxer
import com.example.core.listeners.OnErrorListener
import java.io.File

/**
 * A [BaseAudioOnlyFileStreamer] that sends only microphone frames to a MP4 [File].
 *
 * @param context application context
 * @param initialOnErrorListener initialize [OnErrorListener]
 */
class AudioOnlyMp4FileStreamer(
    context: Context,
    initialOnErrorListener: OnErrorListener? = null
) : BaseAudioOnlyFileStreamer(
    context = context,
    muxer = MP4Muxer(),
    initialOnErrorListener = initialOnErrorListener
)