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
package com.example.core.streamers.helpers

import android.content.Context
import android.util.Range
import android.util.Size
import com.example.core.internal.muxers.IMuxerHelper
import com.example.core.internal.muxers.IVideoMuxerHelper
import com.example.core.internal.muxers.flv.FlvMuxerHelper
import com.example.core.internal.muxers.mp4.MP4MuxerHelper
import com.example.core.internal.muxers.ts.TSMuxerHelper
import com.example.core.internal.utils.av.video.DynamicRangeProfile
import com.example.core.streamers.bases.BaseCameraStreamer
import com.example.core.utils.get10BitSupportedProfiles
import com.example.core.utils.getCameraFpsList
import com.example.core.utils.getCameraOutputStreamSizes

/**
 * Configuration helper for [BaseCameraStreamer].
 * It wraps supported values from MediaCodec, Camera and TS Muxer.
 */
class CameraStreamerConfigurationHelper(muxerHelper: IMuxerHelper) :
    StreamerConfigurationHelper(muxerHelper) {
    companion object {
        val flvHelper = CameraStreamerConfigurationHelper(FlvMuxerHelper())
        val tsHelper = CameraStreamerConfigurationHelper(TSMuxerHelper())
        val mp4Helper = CameraStreamerConfigurationHelper(MP4MuxerHelper())
    }

    override val video = VideoCameraStreamerConfigurationHelper(muxerHelper.video)
}

class VideoCameraStreamerConfigurationHelper(muxerHelper: IVideoMuxerHelper) :
    VideoStreamerConfigurationHelper(muxerHelper) {
    /**
     * Get camera resolutions that are supported by the encoder.
     *
     * @param context application context
     * @param mimeType video encoder mime type
     * @return list of resolutions
     */
    fun getSupportedResolutions(context: Context, mimeType: String): List<Size> {
        val pair = super.getSupportedResolutions(mimeType)
        val codecSupportedWidths = pair.first
        val codecSupportedHeights = pair.second

        return context.getCameraOutputStreamSizes().filter {
            codecSupportedWidths.contains(it.width) && codecSupportedHeights.contains(it.height)
        }
    }

    /**
     * Get supported framerate for a [BaseCameraStreamer].
     *
     * @param context application context
     * @param mimeType video encoder mime type
     * @param cameraId camera id
     * @return list of framerates
     */
    fun getSupportedFramerates(
        context: Context,
        mimeType: String,
        cameraId: String
    ): List<Range<Int>> {
        val encoderFpsRange = super.getSupportedFramerate(mimeType)
        return context.getCameraFpsList(cameraId).filter { encoderFpsRange.contains(it) }
    }

    /**
     * Get supported 8-bit and 10-bit profiles for a [BaseCameraStreamer].
     *
     * @param context application context
     * @param mimeType video encoder mime type
     * @param cameraId camera id
     * @return list of profiles
     */
    fun getSupportedAllProfiles(
        context: Context,
        mimeType: String,
        cameraId: String
    ): List<Int> {
        val supportedDynamicRangeProfiles = context.get10BitSupportedProfiles(cameraId)

        // If device doesn't support 10-bit, return all supported 8-bit profiles
        return super.getSupportedAllProfiles(mimeType).filter {
            supportedDynamicRangeProfiles.contains(
                DynamicRangeProfile.fromProfile(
                    mimeType,
                    it
                ).dynamicRange
            )
        }
    }
}