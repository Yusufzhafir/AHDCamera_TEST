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
package com.example.core.streamers.file

import android.content.Context
import com.example.core.internal.muxers.ts.TSMuxer
import com.example.core.internal.muxers.ts.data.TsServiceInfo
import com.example.core.internal.utils.extensions.defaultTsServiceInfo
import com.example.core.listeners.OnErrorListener
import java.io.File

/**
 * A [BaseAudioOnlyFileStreamer] that sends only microphone frames to a TS [File].
 *
 * @param context application context
 * @param tsServiceInfo MPEG-TS service description
 * @param initialOnErrorListener initialize [OnErrorListener]
 */
class AudioOnlyTsFileStreamer(
    context: Context,
    tsServiceInfo: TsServiceInfo = context.defaultTsServiceInfo,
    initialOnErrorListener: OnErrorListener? = null
) : BaseAudioOnlyFileStreamer(
    context = context,
    muxer = TSMuxer().apply { addService(tsServiceInfo) },
    initialOnErrorListener = initialOnErrorListener
)