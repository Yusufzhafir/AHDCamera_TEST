package com.quectel.multicamera.callbacks;

import com.quectel.qcarapi.cb.IQCarAudioDataCB;
import com.quectel.qcarapi.util.QCarLog;

public class AudioChannelDataCallback implements IQCarAudioDataCB {
    private String TAG = "AudioChannelDataCB";
    @Override
    public void onAudioChannelStream(int channel, byte[] pBuf, int dataLen) {
        QCarLog.d(QCarLog.LOG_MODULE_APP,  TAG, " onAudioChannelStream channel = " + channel + " dataLen = " + dataLen);
    }
}
