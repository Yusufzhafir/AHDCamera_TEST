package com.quectel.multicamera

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

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.extension_rtmp.services.ScreenRecorderRtmpLiveService
import kotlinx.coroutines.runBlocking


class DemoScreenRecorderRtmpLiveService : ScreenRecorderRtmpLiveService(
    notificationId = 0x4568,
    channelId = "io.github.thibaultbee.streampack.screenrecorder.demo.rtmp",
    channelNameResourceId = R.string.app_name
) {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        streamer?.let {
            if (intent.action == "STOP") {
                runBlocking {
                    streamer?.stopStream()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onConnectionSuccessNotification(): Notification {
        val intent =
            Intent(
                this,
                DemoScreenRecorderRtmpLiveService::class.java
            ).setAction("STOP")
        val stopIntent =
            PendingIntent.getService(this, 5678, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(notificationIconResourceId)
            .setContentTitle("Live in progress")
            .build()
    }
}