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
package com.example.core.internal.muxers.ts.utils

import com.example.core.internal.data.SrtPacket
import com.example.core.internal.muxers.IMuxerListener

open class TSOutputCallback(var listener: IMuxerListener? = null) {
    protected fun writePacket(packet: SrtPacket) {
        packet.buffer.rewind()
        listener?.onOutputFrame(packet)
    }
}