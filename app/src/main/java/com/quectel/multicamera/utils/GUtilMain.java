package com.quectel.multicamera.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaMuxer;

import com.quectel.multicamera.QCarRecorderVideoPathDemo;
import com.quectel.multicamera.exceptions.ErrorHandler;
import com.quectel.qcarapi.stream.QCarCamera;
import com.quectel.qcarapi.util.QCarLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GUtilMain {

    // Log tag
    private static String TAG = "GUtil";

    // Debug switch
    private static boolean GLOBAL_DEBUG = false;

    // Define video bitrate
    public static int BITRATE_4M = 4 * 1024 * 1024;
    public static int BITRATE_2M = 2 * 1024 * 1024;
    public static int BITRATE_1M = 1024 * 1024;
    public static int BITRATE_512K = 1024 * 1024;
    //private static int BITRATE_DEFAULT = 1024 * 1024;

    private static Context qContext = null;

    // parameters for video recoding
    private static RecorderParams recorderParams = null;
    // parameters for video previewing
    private static PreviewParams previewParams = null;

    private static QCarRecorderVideoPathDemo qrvpDemo = null;
    private static ErrorHandler qeHandler = null;
    private static Map<Integer, QCarCamera> qCarCameraMap = new ConcurrentHashMap<Integer, QCarCamera>();
    public static int MEDIA_OUTPUT_FORMAT_TS = 2 ;

    public static SharedPreferences mSharedPreferences = null;
    public static SharedPreferences.Editor mEditor = null;

    public static Context getqContext() {
        return qContext;
    }

    public static void setqContext(Context qContext) {
        GUtilMain.qContext = qContext;
        if (mSharedPreferences == null) {
            mSharedPreferences = GUtilMain.qContext.getSharedPreferences("multi_camera_data", Context.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
        }
    }

    // get the Threshold for segmenting video
    public static int getSegmentSizeWithPosition(int position) {
        if (position == 0) {
            return 50 * 1024 * 1024;
        } else if (position == 1) {
            return 100 * 1024 * 1024;
        } else if (position == 2) {
            return 200 * 1024 * 1024;
        } else if (position == 3) {
            return 1*60*1000; // time : 1min = 60s = 60000ms
        }else if (position == 4) {
            return 3*60*1000;
        }else if (position == 5) {
            return 5*60*1000;
        }else {
            return 50 * 1024 * 1024;
        }
    }

    // get the type for segmenting video
    public static int getSegmentFlag(int position) {
        if (0 == position || 1 == position || 2 == position) {
            return 0;  // 1 means Segment recorded video by Size
        } else if (3 == position || 4 == position || 5 == position) {
            return 1; // 2 means Segment recorded video by time
        } else{
            return 0; // default is 1
        }
    }

    // create struct RecorderParams for recorderParams
    public static RecorderParams getRecorderParams() {
        if (recorderParams == null) {
            recorderParams = new RecorderParams();
        }

        return recorderParams;
    }

    // create struct PreviewParams for previewParams
    public static PreviewParams getPreviewParams() {
        if (previewParams == null) {
            previewParams= new PreviewParams();
        }

        return previewParams;
    }

    // get the mine type for video codec
    public static String getMinetypeWithCodecPosition(int position) {
        if (position == 0) {
            return "video/avc";
        } else if (position == 1) {
            return "video/hevc";
        }

        return "video/hevc";
    }

    // get output format for video
    public static int getOutputFormatWidthContainerPosition(int position) {
        if (position == 0) {
            return MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4;  // mp4
        } else if (position == 1) {
            return MEDIA_OUTPUT_FORMAT_TS; //Android7 该值是2  Android9该值是4 一定要注意
        }

        return MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4; // default is mp4
    }

    public static String removeTempInPath(String path) {
        return path.replace("/temp", "");
    }

    public static synchronized QCarCamera getQCamera(int csiphy_num) {
        if (csiphy_num >= 3) {
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " csiphy_num need small than 3");
            return null;
        }

        QCarCamera qCarCamera = qCarCameraMap.get(csiphy_num);
        if (qCarCamera == null) {
            qCarCamera = new QCarCamera(csiphy_num);
            qCarCameraMap.put(csiphy_num, qCarCamera);
        }

        return qCarCamera;
    }

    public static synchronized int removeQCamera(int csiphy_num) {
        if (csiphy_num >= 3 || csiphy_num < 0) {
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " csiphy_num need larger than 0 and small than 3");
            return -1;
        }

        QCarCamera qCarCamera = qCarCameraMap.get(csiphy_num);
        if (qCarCamera != null) {
            qCarCameraMap.remove(csiphy_num, qCarCamera);
        }

        return 0;
    }

    public static QCarRecorderVideoPathDemo getQRVPDemo() {
        if ( qrvpDemo == null) {
            qrvpDemo = new QCarRecorderVideoPathDemo();
        }
        return qrvpDemo;
    }

    public static ErrorHandler getErrorHandler() {
        if (qeHandler == null) {
            qeHandler = new ErrorHandler();
        }
        return qeHandler;
    }

}
