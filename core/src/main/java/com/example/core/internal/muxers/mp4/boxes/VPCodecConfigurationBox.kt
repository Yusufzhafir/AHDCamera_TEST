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
package com.example.core.internal.muxers.mp4.boxes

import com.example.core.internal.utils.av.video.vpx.VPCodecConfigurationRecord
import java.nio.ByteBuffer

class VPCodecConfigurationBox(private val config: VPCodecConfigurationRecord) :
    FullBox("vpcC", 1, 0) {
    override val size: Int = super.size + config.size

    override fun write(output: ByteBuffer) {
        super.write(output)
        config.write(output)
    }
}