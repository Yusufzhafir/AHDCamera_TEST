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
import com.example.core.logger.Logger

/**
 * A fake endpoint for test purpose.
 */
class FakeEndpoint : IEndpoint {
    override fun configure(config: Int) {
        Logger.d(TAG, "configure called with bitrate = $config")
    }

    override fun write(packet: Packet) {
        Logger.d(TAG, "write called (packet size = ${packet.buffer.remaining()})")
    }

    override suspend fun startStream() {
        Logger.d(TAG, "startStream called")
    }
    
    override suspend fun stopStream() {
        Logger.d(TAG, "stopStream called")
    }

    override fun release() {
        Logger.d(TAG, "release called")
    }

    companion object {
        private const val TAG = "FakeEndpoint"
    }
}