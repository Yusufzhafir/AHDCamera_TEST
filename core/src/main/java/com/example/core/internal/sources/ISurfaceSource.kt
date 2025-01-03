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
package com.example.core.internal.sources

import android.view.Surface
import com.example.core.data.VideoConfig
import com.example.core.internal.interfaces.Configurable
import com.example.core.internal.interfaces.Releaseable
import com.example.core.internal.interfaces.Streamable

interface ISurfaceSource : Streamable, Configurable<VideoConfig>, Releaseable {
    /**
     * The offset between source capture time and MONOTONIC clock. It is used to synchronize video
     * with audio. It is only useful for camera source.
     */
    val timestampOffset: Long

    /**
     * Set surface where capture source will render its frame.
     */
    var encoderSurface: Surface?
}