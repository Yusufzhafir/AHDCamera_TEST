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
package com.example.core.streamers.bases

import android.content.Context
import com.example.core.internal.endpoints.IEndpoint
import com.example.core.internal.muxers.IMuxer
import com.example.core.internal.sources.AudioSource
import com.example.core.listeners.OnErrorListener

/**
 * A [BaseStreamer] that sends only microphone frames.
 *
 * @param context application context
 * @param muxer a [IMuxer] implementation
 * @param endpoint a [IEndpoint] implementation
 * @param initialOnErrorListener initialize [OnErrorListener]
 */
open class BaseAudioOnlyStreamer(
    context: Context,
    muxer: IMuxer,
    endpoint: IEndpoint,
    initialOnErrorListener: OnErrorListener? = null
) : BaseStreamer(
    context = context,
    videoSource = null,
    audioSource = AudioSource(),
    muxer = muxer,
    endpoint = endpoint,
    initialOnErrorListener = initialOnErrorListener
)