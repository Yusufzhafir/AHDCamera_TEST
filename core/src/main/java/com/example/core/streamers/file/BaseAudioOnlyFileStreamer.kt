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

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.example.core.internal.endpoints.FileWriter
import com.example.core.internal.muxers.IMuxer
import com.example.core.listeners.OnErrorListener
import com.example.core.streamers.bases.BaseAudioOnlyStreamer
import com.example.core.streamers.bases.BaseCameraStreamer
import com.example.core.streamers.bases.BaseStreamer
import com.example.core.streamers.interfaces.IFileStreamer
import java.io.File
import java.io.OutputStream

/**
 * A [BaseStreamer] that sends only microphone frames to a [File].
 *
 * @param context application context
 * @param muxer a [IMuxer] implementation
 * @param initialOnErrorListener initialize [OnErrorListener]
 */
open class BaseAudioOnlyFileStreamer(
    context: Context,
    muxer: IMuxer,
    initialOnErrorListener: OnErrorListener? = null
) : BaseAudioOnlyStreamer(
    context = context,
    muxer = muxer,
    endpoint = FileWriter(),
    initialOnErrorListener = initialOnErrorListener
), IFileStreamer {
    private val fileWriter = endpoint as FileWriter

    /**
     * Get/Set file.
     * To set an [OutputStream] instead, see [outputStream].
     *
     * @see [outputStream]
     */
    override var file: File?
        /**
         * Get registered [File].
         *
         * @return file where to write the stream
         */
        get() = fileWriter.file
        /**
         * Set [File].
         *
         * @param value [File] where to write the stream
         */
        set(value) {
            fileWriter.file = value
        }

    /**
     * Get/Set outputStream.
     * To set an [File] instead, see [file].
     *
     * @see file
     */
    override var outputStream: OutputStream?
        /**
         * Get registered [OutputStream].
         *
         * @return file where to write the stream
         */
        get() = fileWriter.outputStream
        /**
         * Set [OutputStream].
         *
         * @param value [OutputStream] to write the stream
         */
        set(value) {
            fileWriter.outputStream = value
        }

    /**
     * Same as [BaseCameraStreamer.startStream] with RequiresPermission annotation for
     * Manifest.permission.WRITE_EXTERNAL_STORAGE.
     */
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO])
    override suspend fun startStream() = super.startStream()
}