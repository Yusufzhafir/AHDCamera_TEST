package com.quectel.multicamera;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Keep;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.quectel.multicamera.utils.LanguageUtil;
import com.quectel.multicamera.utils.SpUtil;
import com.quectel.qcarapi.util.QCarLog;

@Keep
public class RecordService extends Service {
    private String TAG = "RecordService";
    private NotificationChannel notificationChannel;
    public static final String CHANNEL_ID = "com.quectel.multicamera.RecordService";
    public static final String CHANNEL_NAME = "com.quectel.multicamera";

    //client 可以通过Binder获取Service实例
    public class RecordBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }

    private RecordBinder recordBinder = new RecordBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return recordBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "RecordService - onUnbind - from = ");
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = SpUtil.getInstance(newBase).getString(SpUtil.LANGUAGE);
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, language));
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel= new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }

        Intent Intent_pre = new Intent("com.quectel.multicamera.RecordService.onclick");
        PendingIntent pendIntent_click = PendingIntent.getBroadcast(this, 0, Intent_pre, 0);
        Notification notification= new Notification.Builder(this, CHANNEL_ID).setChannelId(CHANNEL_ID)
                .setTicker("Nature")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.recording))
                .setContentIntent(pendIntent_click)
                .setContentText(getString(R.string.click_stop))
                .build();
        notification.flags|= Notification.FLAG_NO_CLEAR;
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}
