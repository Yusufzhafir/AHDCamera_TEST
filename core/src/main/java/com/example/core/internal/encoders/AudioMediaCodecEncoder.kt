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
package com.example.core.internal.encoders

import com.example.core.data.AudioConfig
import com.example.core.listeners.OnErrorListener


class AudioMediaCodecEncoder(
    encoderListener: IEncoderListener,
    override val onInternalErrorListener: OnErrorListener,
) :
    MediaCodecEncoder<AudioConfig>(encoderListener) {
    override var bitrate: Int
        get() = super.bitrate
        set(_) {
            throw UnsupportedOperationException("Can't set audio bitrate")
        }
}