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
package com.example.core.internal.endpoints

import com.example.core.internal.data.Packet
import com.example.core.internal.utils.extensions.toByteArray
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class FileWriter : IEndpoint {
    var file: File? = null
        set(value) {
            outputStream = if (value != null) {
                FileOutputStream(value, false)
            } else {
                outputStream?.close()
                null
            }
            field = value
        }

    var outputStream: OutputStream? = null

    override suspend fun startStream() {
        if (outputStream == null) {
            throw UnsupportedOperationException("Set a file before trying to write it")
        }
    }

    override fun configure(config: Int) {} // Nothing to configure

    override fun write(packet: Packet) {
        outputStream?.write(packet.buffer.toByteArray())
            ?: throw UnsupportedOperationException("Set a file before trying to write it")
    }

    override suspend fun stopStream() {
        outputStream?.close()
    }

    override fun release() {
    }
}