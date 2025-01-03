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
package com.example.core.internal.events

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.core.error.StreamPackError
import com.example.core.listeners.OnErrorListener

abstract class EventHandler {
    private val eventHandler by lazy {
        EventHandler()
    }

    protected abstract val onInternalErrorListener: OnErrorListener

    fun reportError(error: StreamPackError) {
        val msg = eventHandler.obtainMessage(MSG_ERROR, error)
        eventHandler.sendMessage(msg)
    }

    companion object {
        private const val MSG_ERROR = 0
    }

    open inner class EventHandler(
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper()
    ) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_ERROR -> onInternalErrorListener.onError(
                    msg.obj as StreamPackError
                )
            }
        }
    }
}