package com.quectel.multicamera;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.location.Location;
import android.location.LocationProvider;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.core.data.AudioConfig;
import com.example.core.data.VideoConfig;
import com.example.core.error.StreamPackError;
import com.example.core.internal.encoders.MediaCodecHelper;
import com.example.core.listeners.OnConnectionListener;
import com.example.core.listeners.OnErrorListener;
import com.example.core.streamers.bases.BaseScreenRecorderStreamer;
import com.example.core.streamers.interfaces.ILiveStreamer;
import com.example.core.streamers.live.BaseScreenRecorderLiveStreamer;
import com.example.extension_rtmp.services.ScreenRecorderRtmpLiveService;
import com.example.extension_rtmp.streamers.ScreenRecorderRtmpLiveStreamer;
import com.quectel.multicamera.dialog.ADASConfigDialog;
import com.quectel.multicamera.dialog.CalibrationDialog;
import com.quectel.multicamera.dialog.DMSConfigDialog;
import com.quectel.multicamera.dialog.SettingsDialog;
import com.quectel.multicamera.utils.FileUtils;
import com.quectel.multicamera.utils.GUtilMain;
import com.quectel.multicamera.utils.IntQueueUtils;
import com.quectel.multicamera.utils.LanguageUtil;
import com.quectel.multicamera.utils.LocationUtils;
import com.quectel.multicamera.utils.PreviewParams;
import com.quectel.multicamera.utils.RecorderParams;
import com.quectel.multicamera.utils.ShellUtils;
import com.quectel.multicamera.utils.SpUtil;
import com.quectel.multicamera.utils.VoldReceiver;
import com.quectel.qcarapi.cb.IQCarCamInStatusCB;
import com.quectel.qcarapi.helper.QCarCamInDetectHelper;
import com.quectel.qcarapi.image.QCarJpeg;
import com.quectel.qcarapi.image.QCarPicWriter;
import com.quectel.qcarapi.osd.QCarOsd;
import com.quectel.qcarapi.stream.QCarAudio;
import com.quectel.qcarapi.stream.QCarCamera;
import com.quectel.qcarapi.util.QCarLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements IQCarCamInStatusCB {
    private static String TAG = "MainActivity";

    ////////////////////////////////////////////////ADAS////////////////////////////////////////////////
    private ADASDetectThread mADASDetectThread = null;
    private boolean firstStartADAS = true;
    private CalibrationDialog mADASCalibrationDialog = null;
    private ADASConfigDialog mADASConfigDialog = null;
    private boolean isCalCall = false;
    private ByteBuffer by_adas = ByteBuffer.allocate(1280 * 720 * 3);
    // 7寸 22 23   10寸  44 45
    private String CMD_READ_LEFT_GPIO;
    private String CMD_READ_RIGHT_GPIO;
    //GPS速度值
    private double mGpsSpeed = 0;
    private String mSpeedMode = "GPS";
    //语音播报用
    private BeepManager mBeepManager = null;
    //语音播放控制线程
    private playManagerThread mplayManagerThread = null;
    private boolean ADAS_ENABLE = false;

    ////////////////////////////////////////////////DMS////////////////////////////////////////////////
    private final float FACE_CONTRAST_COEFFICIENT = 0.36f;
    private String mPath;
    private boolean firstStartDMS = true;
    private DMSDetectThread mDMSDetectThread = null;
    private FaceVerityThread mFaceVerityThread = null;
    private DMSConfigDialog mDMSConfigDialog = null;
    private ByteBuffer by_dms = ByteBuffer.allocate(1280 * 720 * 3);
    private ByteBuffer by = ByteBuffer.allocate(1280 * 720 * 3);
    private AlertDialog mConformDialog = null;
    private boolean faceReset = false;//当面部丢失之后即重启检测驾驶员身份
    private boolean DMS_ENABLE = false;

    ////////////////////////////////////////////////BSD////////////////////////////////////////////////
    private boolean firstStartBSD = true;
    private BSDDetectThread mBSDDetectThread = null;
    private AlertDialog mBSDDialog = null;
    private Button mStart, mCalibration;
    private CalibrationDialog mBSDCalibrationDialog = null;
    private ByteBuffer by_bsd = ByteBuffer.allocate(1280 * 720 * 3);
    private boolean BSD_ENABLE = false;

    public int[] isPreviw = new int[6];

    public static int CAMERANUM = 4;
    public static int csi1InputType = 0;
    public static int csi2InputType = 4;

    /*****Record Service && Vold Receiver******/
    private VoldReceiver vReceiver;  // 广播接收器，接受SD卡插拔状态信息
    private RecordService service = null;
    private boolean isBind = false;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBind = true;
            RecordService.RecordBinder myBinder = (RecordService.RecordBinder) binder;
            service = myBinder.getService();
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "ActivityA - onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "ActivityA - onServiceDisconnected");
        }
    };
    /*****Record Service && Vold Receiver******/

    private boolean m_bTakePicFinshFlag = true;
    private QCarCamInDetectHelper detectInsert;

    public static int csi1phy_num = -1;
    public static int csi2phy_num = -1;

    private PreviewParams pParams = GUtilMain.getPreviewParams();
    private RecorderParams rParams = GUtilMain.getRecorderParams();

    /******Main OSD: preview stream && recoder Stream*****/
    private boolean bOsdFlag = false;
    /******Main OSD: preview stream && recoder Stream*****/

    public static int i = 0;

    private static void addGPUTask(int taskNum) {
        for (int i = 0; i < taskNum; i++) {
            new Thread() {
                float f1 = 27.177177797f;
                float f2 = 172.14038401131414f;
                float f3 = 13.1231231f;

                @Override
                public void run() {
                    while (true) {
                        float f = f1 * f2 * f3;
                    }
                }
            }.start();
        }
    }

    static {
        System.loadLibrary("mmqcar_qcar_jni");
        if (Build.VERSION.SDK_INT == 28) {
            GUtilMain.MEDIA_OUTPUT_FORMAT_TS = 4;
        } else if (Build.VERSION.SDK_INT == 25) {
            GUtilMain.MEDIA_OUTPUT_FORMAT_TS = 2;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.quectel.multicamera.RecordService.onclick")) {
                isRecord = false;
                RecoderInstance.getInstance().stopRecorder();
                stopRecordService();
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        GUtilMain.setqContext(newBase);
        String language = SpUtil.getInstance(newBase).getString(SpUtil.LANGUAGE);
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, language));
    }

    private static SurfaceViewFragment instance_1, instance_2, instance_3, instance_4, instance_5, instance_6;
    private LinearLayout layout1, layout2, layout3;
    private FrameLayout frame1_0, frame1_1, frame1_2, frame1_3, frame2_0, frame2_1;
    private boolean isShow1_0 = false, isShow1_1 = false, isShow1_2 = false, isShow1_3 = false, isShow2_0 = false, isShow2_1 = false, isShow2_2 = false, isShow2_3 = false;
    private int displayStyle = 0;
    private boolean isRecord = false;
    private int opencsi0_nums, opencsi1_nums;
    private Button dms_button = null, adas_button = null, bsd_button = null, take_pic_button = null, start_lock_video_button = null, settings_button = null, switch_default_button = null;

    //主界面按键显示控制
    private boolean button_visible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏

        Log.d("qwe", "onCreate: " + getMachineMark());
        if (getMachineMark().split("\\.")[0].equals("001_04")) { //7寸
            CMD_READ_LEFT_GPIO = "cat /sys/class/gpio/gpio23/value";
            CMD_READ_RIGHT_GPIO = "cat /sys/class/gpio/gpio22/value";
        } else {
            CMD_READ_LEFT_GPIO = "cat /sys/class/gpio/gpio44/value";
            CMD_READ_RIGHT_GPIO = "cat /sys/class/gpio/gpio45/value";
        }
        if (!(LocationUtils.isGpsEnabled(MainActivity.this) && LocationUtils.isLocationEnabled(MainActivity.this))) {
            showGpsConfirmDialog();
        }

        //注册gps
        LocationUtils.register(MainActivity.this, 500, 0, new LocationUtils.OnLocationChangeListener() {
            @Override
            public void getLastKnownLocation(Location location) {

            }

            @Override
            public void onLocationChanged(Location location) {
                System.out.println("zyz --> speed --> " + location.getSpeed());
                if (pParams.getSpeedMode() == 0)
                    mGpsSpeed = location.getSpeed() * 3.6;//mk/h
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
//                            System.out.println("zyz --> onStatusChanged --> 当前GPS状态为可见状态");
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
//                            System.out.println("zyz --> onStatusChanged --> 当前GPS状态为服务区外状态");
                        mGpsSpeed = 0;
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                            System.out.println("zyz --> onStatusChanged --> 当前GPS状态为暂停服务状态");
                        mGpsSpeed = 0;
                        break;
                }
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.quectel.multicamera.RecordService.onclick");
        registerReceiver(mReceiver, filter, Context.RECEIVER_VISIBLE_TO_INSTANT_APPS | Context.RECEIVER_NOT_EXPORTED);

        pParams = GUtilMain.getPreviewParams();
        pParams.setDMSEnable(false);
        pParams.setADASEnable(false);
        pParams.setDMSIsFaceEntry(pParams.getFaceNumber() != 0);
        displayStyle = pParams.getDisplayStyle();
        mSpeedMode = pParams.getSpeedMode() == 0 ? "GPS" : "SIMULATION";
        if (pParams.getSpeedMode() == 0) {
            mGpsSpeed = 0;
        } else {
            mGpsSpeed = pParams.getSimulationSpeed();
        }

        if (!pParams.isN41Exist())
            pParams.setDisplayStyle(0);
        displayStyle = pParams.getDisplayStyle();

        if (displayStyle == 0)
            setContentView(R.layout.activity_main_2_2);
        else if (displayStyle == 1)
            setContentView(R.layout.activity_main_3_3);

        layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout2 = (LinearLayout) findViewById(R.id.layout2);
        layout3 = (LinearLayout) findViewById(R.id.layout3);

        frame1_0 = (FrameLayout) findViewById(R.id.container1_0);
        frame1_1 = (FrameLayout) findViewById(R.id.container1_1);
        frame1_2 = (FrameLayout) findViewById(R.id.container1_2);
        frame1_3 = (FrameLayout) findViewById(R.id.container1_3);
        frame2_0 = (FrameLayout) findViewById(R.id.container2_0);
        frame2_1 = (FrameLayout) findViewById(R.id.container2_1);

        dms_button = (Button) findViewById(R.id.dms_detect);
        adas_button = (Button) findViewById(R.id.adas_detect);
        bsd_button = (Button) findViewById(R.id.bsd_detect);
        take_pic_button = (Button) findViewById(R.id.takePic);
        start_lock_video_button = (Button) findViewById(R.id.startLockVideo);
        settings_button = (Button) findViewById(R.id.settings);
        switch_default_button = (Button) findViewById(R.id.switch_default);
        dms_button.setEnabled(false);
        adas_button.setEnabled(false);
        bsd_button.setEnabled(false);

        mBeepManager = new BeepManager(MainActivity.this);
        mHandler = new DialogHandler();

//        InitAIOperation initAIOperation = new InitAIOperation();
//        initAIOperation.start();

        mplayManagerThread = new playManagerThread(20);
        mplayManagerThread.start();

        initd();
    }

    private String getMachineMark() {
        String machineMark = "Unknown";
        machineMark = getInfoByProStr("ro.machine.mark");
        if ("".equals(machineMark)
                || machineMark == null) {
            machineMark = "Unknown";
        }
        return machineMark;
    }

    private String getInfoByProStr(String propName) {
        String infoStr = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            infoStr = (String) (get.invoke(c, propName, "null"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (infoStr == null || "".equals(infoStr)) {
            infoStr = "null";
        }
        return infoStr;
    }

    private class DMSDetectThread extends Thread {
        private boolean alive = true;
        private boolean isFace = false;

        @Override
        public void run() {

            Object ret = null;

            byte[] data;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dms_button.setEnabled(true);
                    dms_button.setText(getString(R.string.stop_dms));
                    instance_2.setDMSEnable(true);
                }
            });
            pParams.setDMSEnable(true);
            while (true) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                by_dms.clear();

                if (!alive) {
                    System.out.println("zyz --> Stop dms thread !");
                    break;
                }

                if (GUtilMain.getQCamera(csi1phy_num) == null) {
                    System.out.println("zyz --> Not found !");
                    break;
                }
                synchronized (MainActivity.this) {
                    ret = GUtilMain.getQCamera(csi1phy_num).getSubFrameInfo(1, by_dms);
                }
                if (ret == null) {
                    System.out.println("zyz --> DMSDetectThread --> get null !!!!!!");
                    break;
                } else {
                    data = decodeValue(by_dms);
                    float res;
//                    res = SystemAlg.doDMS(data, (float) mGpsSpeed);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dms_button.setEnabled(true);
                    dms_button.setText(getString(R.string.start_dms));
                    instance_2.setDMSEnable(false);
                }
            });
            pParams.setDMSEnable(false);
            return;
        }

        public void setAlive(boolean alive) {
            this.alive = alive;
        }
    }

    /**
     * ByteBuffer转byte[]
     *
     * @param bytes
     * @return byte[]格式数据
     */
    public byte[] decodeValue(ByteBuffer bytes) {
        int len = bytes.limit() - bytes.position();
        byte[] bytes1 = new byte[len];
        bytes.get(bytes1);
        return bytes1;
    }

    private class FaceVerityThread extends Thread {
        private ByteBuffer by_dms0 = ByteBuffer.allocate(1280 * 720 * 3);
        private int delay;
        private int cumulativeTimes = 0;
        private boolean isFaceExist = true;
        private String path;
        private byte[] data;
        private Object ret;

        public FaceVerityThread(int delay) {
            this.delay = delay * 1000;
        }

        @Override
        public void run() {
            File file;
//            SystemAlg.initFaceFeaturePara();
            //读取本地文件，导入人脸特征
            for (int i = 0; i < pParams.getFaceNumber(); i++) {
                path = getFilesDir().getAbsolutePath() + File.separator + pParams.getIdentityPictureName() + i;
                file = new File(path);
                if (file.exists()) {
                    try {
                        data = FileUtils.toByteArray(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Read " + path + " failed !!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
//                    System.out.println("zyz --> data len --> "+data.length+", file len --> "+file.length());
//                    if (!SystemAlg.readTargetFaceData(i, data, (int) file.length())) {
//                        final int num = i;
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), "identity" + num + getString(R.string.dms_verity_face_faile), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
                } else {
                    System.out.println("zyz --> file " + file.getName() + " is lost !!!");
                    break;
                }
            }

            //查询csi0是否打开
            if (GUtilMain.getQCamera(csi1phy_num) == null) {
                System.out.println("zyz --> faceVerityRunnable --> Not found !");
                Toast.makeText(getApplicationContext(), getString(R.string.dms_verity_face_faile), Toast.LENGTH_SHORT).show();
                return;
            }
            while (true) {
                by_dms0.clear();
//                System.out.println("zyz --> start verity face !!!");
                if (isFaceExist && pParams.getIdentityEnable() && pParams.getDMSEnable()) {
                    //获取dms这一路一帧子码流并压缩成文件jpg
                    synchronized (MainActivity.this) {
                        ret = GUtilMain.getQCamera(csi1phy_num).getSubFrameInfo(1, by_dms0);
                    }
                    if (ret == null) {
//                        System.out.println("zyz --> faceVerityRunnable --> get null !!!!!!");
                        Toast.makeText(getApplicationContext(), getString(R.string.dms_verity_face_faile), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        byte[] data = decodeValue(by_dms0);
                        if (QCarJpeg.jpegEncoderToFile(data, QCarJpeg.QUEC_YCBCR_SP, QCarJpeg.QUEC_H2V2, 1280, 720,
                                ("/data/alg/temp.jpg").getBytes(), 90, 3000) != 0) {
                            System.out.println("zyz --> faceVerityRunnable --> Cannot encord jpeg picture !!!");
                            Toast.makeText(getApplicationContext(), getString(R.string.dms_verity_face_faile), Toast.LENGTH_SHORT).show();
//                            return;
                        }
//                        boolean res = SystemAlg.verityFaceFeature(FACE_CONTRAST_COEFFICIENT, pParams.getFaceNumber());
//                        System.out.println("zyz --> verityFaceFeature --> " + res);
//                        if (!res) {
//                            if ((cumulativeTimes++) >= 2) {
//                                cumulativeTimes = 0;
//                                mplayManagerThread.addMusic(0x1000);
//                            }
//                        } else {
//                            cumulativeTimes = 0;
//                        }
                    }
                } else {
//                    System.out.println("zyz --> isFaceExist="+isFaceExist+", pParams.getIdentityEnable()="+pParams.getIdentityEnable());
                    cumulativeTimes = 0;
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setFaceExist(boolean exist) {
            isFaceExist = exist;
        }

        public void setCumulativeTimes(int times) {
            cumulativeTimes = times;
        }
    }

    public void initd() {
        frame1_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frame1_0.setEnabled(false);
                if (isShow1_0) {
                    isShow1_0 = false;
                    instance_2.setVisible(false);
                    instance_3.setVisible(false);
                    if (pParams.isN41Exist()) instance_4.setVisible(false);
                    frame1_1.setVisibility(View.GONE);
                    frame1_2.setVisibility(View.GONE);
                    frame1_3.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    if (displayStyle != 0) {
                        instance_5.setVisible(false);
                        instance_6.setVisible(false);
                        frame2_0.setVisibility(View.GONE);
                        frame2_1.setVisibility(View.GONE);
                        layout3.setVisibility(View.GONE);
                    }
                    instance_1.setLineEnable(true);
                } else {
                    isShow1_0 = true;
                    instance_2.setVisible(true);
                    instance_3.setVisible(true);
                    if (pParams.isN41Exist()) instance_4.setVisible(true);
                    frame1_1.setVisibility(View.VISIBLE);
                    frame1_2.setVisibility(View.VISIBLE);
                    frame1_3.setVisibility(View.VISIBLE);
                    if (displayStyle != 0) {
                        instance_5.setVisible(true);
                        instance_6.setVisible(true);
                        frame2_0.setVisibility(View.VISIBLE);
                        frame2_1.setVisibility(View.VISIBLE);
                        layout3.setVisibility(View.VISIBLE);
                    }
                    layout2.setVisibility(View.VISIBLE);
                    instance_1.setLineEnable(false);
                }
                frame1_0.setEnabled(true);
            }
        });

        frame1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frame1_1.setEnabled(false);
                if (isShow1_1) {
                    isShow1_1 = false;
                    instance_1.setVisible(false);
                    instance_3.setVisible(false);
                    if (pParams.isN41Exist()) instance_4.setVisible(false);
                    frame1_0.setVisibility(View.GONE);
                    frame1_2.setVisibility(View.GONE);
                    frame1_3.setVisibility(View.GONE);
                    if (displayStyle != 0) {
                        instance_5.setVisible(false);
                        instance_6.setVisible(false);
                        frame2_0.setVisibility(View.GONE);
                        frame2_1.setVisibility(View.GONE);
                        layout3.setVisibility(View.GONE);
                    }
                    layout2.setVisibility(View.GONE);
                    instance_2.setFacePointEnable(true);
                } else {
                    isShow1_1 = true;
                    instance_1.setVisible(true);
                    instance_3.setVisible(true);
                    if (pParams.isN41Exist()) instance_4.setVisible(true);
                    frame1_0.setVisibility(View.VISIBLE);
                    frame1_2.setVisibility(View.VISIBLE);
                    frame1_3.setVisibility(View.VISIBLE);
                    if (displayStyle != 0) {
                        instance_5.setVisible(true);
                        instance_6.setVisible(true);
                        frame2_0.setVisibility(View.VISIBLE);
                        frame2_1.setVisibility(View.VISIBLE);
                        layout3.setVisibility(View.VISIBLE);
                    }
                    layout2.setVisibility(View.VISIBLE);
                    instance_2.setFacePointEnable(false);
                }
                frame1_1.setEnabled(true);
            }
        });

        frame1_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frame1_2.setEnabled(false);
                if (isShow1_2) {
                    isShow1_2 = false;
                    instance_1.setVisible(false);
                    instance_2.setVisible(false);
                    if (pParams.isN41Exist()) instance_4.setVisible(false);
                    frame1_0.setVisibility(View.GONE);
                    frame1_1.setVisibility(View.GONE);
                    frame1_3.setVisibility(View.GONE);
                    if (displayStyle != 0) {
                        instance_5.setVisible(false);
                        instance_6.setVisible(false);
                        frame2_0.setVisibility(View.GONE);
                        frame2_1.setVisibility(View.GONE);
                        layout3.setVisibility(View.GONE);
                    }
                    layout1.setVisibility(View.GONE);
                } else {
                    isShow1_2 = true;
                    instance_1.setVisible(true);
                    instance_2.setVisible(true);
                    if (pParams.isN41Exist()) instance_4.setVisible(true);
                    frame1_0.setVisibility(View.VISIBLE);
                    frame1_1.setVisibility(View.VISIBLE);
                    frame1_3.setVisibility(View.VISIBLE);
                    if (displayStyle != 0) {
                        instance_5.setVisible(true);
                        instance_6.setVisible(true);
                        frame2_0.setVisibility(View.VISIBLE);
                        frame2_1.setVisibility(View.VISIBLE);
                        layout3.setVisibility(View.VISIBLE);
                    }
                    layout1.setVisibility(View.VISIBLE);
                }
                frame1_2.setEnabled(true);
            }
        });

        frame1_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pParams.isN41Exist())
                    return;
                frame1_3.setEnabled(false);
                if (isShow1_3) {
                    isShow1_3 = false;
                    instance_1.setVisible(false);
                    instance_2.setVisible(false);
                    instance_3.setVisible(false);
                    frame1_0.setVisibility(View.GONE);
                    frame1_1.setVisibility(View.GONE);
                    frame1_2.setVisibility(View.GONE);
                    if (displayStyle != 0) {
                        instance_5.setVisible(false);
                        instance_6.setVisible(false);
                        frame2_0.setVisibility(View.GONE);
                        frame2_1.setVisibility(View.GONE);
                        layout3.setVisibility(View.GONE);
                    }
                    layout1.setVisibility(View.GONE);
                } else {
                    isShow1_3 = true;
                    instance_1.setVisible(true);
                    instance_2.setVisible(true);
                    instance_3.setVisible(true);
                    frame1_0.setVisibility(View.VISIBLE);
                    frame1_1.setVisibility(View.VISIBLE);
                    frame1_2.setVisibility(View.VISIBLE);
                    if (displayStyle != 0) {
                        instance_5.setVisible(true);
                        instance_6.setVisible(true);
                        frame2_0.setVisibility(View.VISIBLE);
                        frame2_1.setVisibility(View.VISIBLE);
                        layout3.setVisibility(View.VISIBLE);
                    }
                    layout1.setVisibility(View.VISIBLE);
                }
                frame1_3.setEnabled(true);
            }
        });

        frame2_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frame2_0.setEnabled(false);
                if (isShow2_0) {
                    isShow2_0 = false;
                    instance_1.setVisible(false);
                    instance_2.setVisible(false);
                    instance_3.setVisible(false);
                    instance_4.setVisible(false);
                    instance_6.setVisible(false);
                    frame1_0.setVisibility(View.GONE);
                    frame1_1.setVisibility(View.GONE);
                    frame1_2.setVisibility(View.GONE);
                    frame1_3.setVisibility(View.GONE);
                    frame2_1.setVisibility(View.GONE);
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                } else {
                    isShow2_0 = true;
                    instance_1.setVisible(true);
                    instance_2.setVisible(true);
                    instance_3.setVisible(true);
                    instance_4.setVisible(true);
                    instance_6.setVisible(true);
                    frame1_0.setVisibility(View.VISIBLE);
                    frame1_1.setVisibility(View.VISIBLE);
                    frame1_2.setVisibility(View.VISIBLE);
                    frame1_3.setVisibility(View.VISIBLE);
                    frame2_1.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.VISIBLE);
                }
                frame2_0.setEnabled(true);
            }
        });

        frame2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frame2_1.setEnabled(false);
                if (isShow2_1) {
                    isShow2_1 = false;
                    instance_1.setVisible(false);
                    instance_2.setVisible(false);
                    instance_3.setVisible(false);
                    instance_4.setVisible(false);
                    instance_5.setVisible(false);
                    frame1_0.setVisibility(View.GONE);
                    frame1_1.setVisibility(View.GONE);
                    frame1_2.setVisibility(View.GONE);
                    frame1_3.setVisibility(View.GONE);
                    frame2_0.setVisibility(View.GONE);
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                } else {
                    isShow2_1 = true;
                    instance_1.setVisible(true);
                    instance_2.setVisible(true);
                    instance_3.setVisible(true);
                    instance_4.setVisible(true);
                    instance_5.setVisible(true);
                    frame1_0.setVisibility(View.VISIBLE);
                    frame1_1.setVisibility(View.VISIBLE);
                    frame1_2.setVisibility(View.VISIBLE);
                    frame1_3.setVisibility(View.VISIBLE);
                    frame2_0.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.VISIBLE);
                }
                frame2_1.setEnabled(true);
            }
        });

        //Set record number
        if (displayStyle == 0) {
            rParams.setRecorderNums((rParams.getRecordState(1) ? 1 : 0)
                    + (rParams.getRecordState(2) ? 1 : 0)
                    + (rParams.getRecordState(3) ? 1 : 0)
                    + (rParams.getRecordState(4) ? 1 : 0));
        } else if (displayStyle == 1) {
            rParams.setRecorderNums((rParams.getRecordState(1) ? 1 : 0)
                    + (rParams.getRecordState(2) ? 1 : 0)
                    + (rParams.getRecordState(3) ? 1 : 0)
                    + (rParams.getRecordState(4) ? 1 : 0)
                    + (rParams.getRecordState(5) ? 1 : 0)
                    + (rParams.getRecordState(6) ? 1 : 0));
        }


        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "预览分辨率选择" + getIntent().getIntExtra("size1", 0));
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "录像分辨率选择" + getIntent().getIntExtra("size2", 0));
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "预览个数" + pParams.getPreviewNum());
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "录像个数" + rParams.getRecorderNums());

        csi1InputType = -1;
        csi2InputType = -1;
        csi1phy_num = -1;
        csi2phy_num = -1;

        for (i = 0; i < 6; i++) {
            if (pParams.getPreviewNum() <= CAMERANUM) {
                if (i < pParams.getPreviewNum())
                    isPreviw[i] = 1;
                else
                    isPreviw[i] = 0;
            } else {
                if (i < CAMERANUM) {
                    isPreviw[i] = 1;
                } else if ((i - CAMERANUM) >= 0 && (i - CAMERANUM) < CAMERANUM && ((i - 4) < (pParams.getPreviewNum() - CAMERANUM))) {
                    isPreviw[i] = 1;
                } else {
                    isPreviw[i] = 0;
                }
            }

            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " recorder nums = " + rParams.getRecorderNums() + ", isPreviw[" + i + "] =" + isPreviw[i]);
        }
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "preview channel 1 width " + pParams.getWidth(1) + " height " + pParams.getHeight(1));
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "preview channel 2 width " + pParams.getWidth(2) + " height " + pParams.getHeight(2));
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "preview channel 3 width " + pParams.getWidth(3) + " height " + pParams.getHeight(3));
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "preview channel 4 width " + pParams.getWidth(4) + " height " + pParams.getHeight(4));
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "preview channel 5 width " + pParams.getWidth(5) + " height " + pParams.getHeight(5));
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "preview channel 6 width " + pParams.getWidth(6) + " height " + pParams.getHeight(6));
        csi1phy_num = 0;
        csi1InputType = pParams.getInput1TypeNum();
//        System.out.println("zyz --> csi1InputType --> "+csi1InputType);

        if (pParams.isN41Exist()) {
            if (displayStyle == 0) {
                if (pParams.getPreviewNum() <= 2) {
                    opencsi0_nums = pParams.getPreviewNum();
                    opencsi1_nums = 0;
                } else {
                    opencsi0_nums = 2;
                    opencsi1_nums = pParams.getPreviewNum() - 2;
                }
            } else {
                if (pParams.getPreviewNum() <= 3) {
                    opencsi0_nums = pParams.getPreviewNum();
                    opencsi1_nums = 0;
                } else {
                    opencsi0_nums = 3;
                    opencsi1_nums = pParams.getPreviewNum() - 3;
                }
            }
        } else {
            opencsi0_nums = pParams.getPreviewNum();
            opencsi1_nums = 0;
        }
        openInitCamera(csi1phy_num, opencsi0_nums, csi1InputType);
        addCameraDetect(csi1phy_num, opencsi0_nums);

        if (pParams.isN41Exist()) {
            csi2phy_num = 1;
            csi2InputType = pParams.getInput2TypeNum();
//            System.out.println("zyz --> csi2InputType --> "+csi2InputType);

            openInitCamera(csi2phy_num, opencsi1_nums, csi2InputType);
            addCameraDetect(csi2phy_num, opencsi1_nums);
        } else {
            rParams.setRecorderNums(Math.min(rParams.getRecorderNums(), 3));
            pParams.setPreviewNum(Math.min(pParams.getPreviewNum(), 3));
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " Could not found /dev/n41, opened failed !");
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " recorder nums = " + rParams.getRecorderNums() + " Previw nums = " + pParams.getPreviewNum());
        }

        detectInsert.startDetectThread(); // 启动热插拔检测线程
//        注册异常回调
        addMainOsd();
        startShow();
        startMirrorImage();
        if (rParams.getRecordState()) {
            if (getStoragePath(MainActivity.this, true) != null && getStoragePath(MainActivity.this, true).contains("storage"))
                new Handler().postDelayed(mStartRecordService, 1000);
            else
                Toast.makeText(getApplicationContext(), getString(R.string.sdcard_disable), Toast.LENGTH_SHORT).show();
        }
        initStream();
    }

    private ActivityResultLauncher<String> requestAudioPermissionsLauncher;
    private ActivityResultLauncher<Intent> getContentLauncher;

    private void initStream() {
        // Initialize the audio permissions launcher
        requestAudioPermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                    } else {
                        Intent screenRecorderIntent = BaseScreenRecorderStreamer.Companion.createScreenRecorderIntent(this);
                        getContentLauncher.launch(screenRecorderIntent);
                    }
                }
        );

        // Initialize the getContent launcher
        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        ScreenRecorderRtmpLiveService.Companion.launch(
                                        this,
                                DemoScreenRecorderRtmpLiveService.class,
                                true,
                                streamer -> {
                                    this.streamer = streamer;
                                    this.streamer.setActivityResult(result);
                                    try {
                                        configureAndStart();
                                        moveTaskToBack(true);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error while starting streamer", e);
                                    }
                                    return null;
                                },
                                name -> null
                        );
                    }
                }
        );

        requestAudioPermissionsLauncher.launch(android.Manifest.permission.RECORD_AUDIO);
        // Initialize streamer with error and connection listeners
    }
    private void configureAndStart(){
        streamer = new ScreenRecorderRtmpLiveStreamer(getApplicationContext(), true, new OnErrorListener() {
            @Override
            public void onError(@NonNull StreamPackError error) {
                Log.e("StreamError", "Error Code: " + error.getClass() + ", Message: " + error.getMessage());
                toast("An error occurred: " + error.getMessage());
            }
        }, new OnConnectionListener() {
            @Override
            public void onLost(@NonNull String message) {
                Log.e("StreamConnection", "Connection lost: " + message);
                toast("Connection lost: " + message);
            }

            @Override
            public void onFailed(@NonNull String message) {
                Log.e("StreamConnection", "Connection failed: " + message);
                toast("Connection failed: " + message);
            }

            @Override
            public void onSuccess() {
                Log.d("StreamConnection", "Connection successful");
                toast("Connection successful");
            }
        });

        // Retrieve device refresh rate to calculate FPS
        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
        int deviceRefreshRate = (int) display.getRefreshRate();
        int fps = MediaCodecHelper.Video.INSTANCE.getFramerateRange("video/avc").contains(deviceRefreshRate)
                ? deviceRefreshRate
                : 30;

        // Configure video settings
        VideoConfig videoConfig = new VideoConfig();
        streamer.configure(videoConfig);

        // Configure audio settings if permission is granted
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            AudioConfig audioConfig = new AudioConfig();
            streamer.configure(audioConfig);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1001);
            return;
        }

        // Start the streaming process
        new Thread(() -> {
            try {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                streamer.startStreamFromJava("rtmp://45.32.115.43/live/ahd");
            } catch (Exception e) {
                Log.e("StreamError", "Streaming failed", e);
                toast("Streaming failed: " + e.getMessage());
            }
        }).start();
    }

    private void toast(String message) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 获取SD卡路径
     *
     * @param mContext
     * @param is_removable SD卡是否可移除，不可移除的是内置SD卡，可移除的是外置SD卡
     * @return
     */
    public static String getStoragePath(Context mContext, boolean is_removable) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;

        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Method getState = storageVolumeClazz.getMethod("getState");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                String state = (String) getState.invoke(storageVolumeElement);

                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);

                if (is_removable == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Runnable mStartRecordService = new Runnable() {
        @Override
        public void run() {
            startVideoRecoder();
            isRecord = true;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    private void openInitCamera(int csiNum, int inputNum, int inputType) {
        int count = 0;
        QCarCamera qCarCamera = GUtilMain.getQCamera(csiNum);
        while (count < 10) {
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " csiNum = " + csiNum + " inputNum = " + inputNum + " inputType = " + inputType);
            int ret = qCarCamera.cameraOpen(inputNum, inputType);
            if (ret == 0) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " Open csi " + csiNum + " Success");
                break;
            } else {
                count++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                QCarLog.e(QCarLog.LOG_MODULE_APP, TAG, " Open Failed, cameraOpen csi " + csiNum + " return = " + ret);
            }
        }
        qCarCamera.registerOnErrorCB(GUtilMain.getErrorHandler());
    }

    // 初始化Camera热插拔状态检测函数
    private void addCameraDetect(int csiNum, int inputNum) {
        QCarCamInDetectHelper.InputParam inputParam = new QCarCamInDetectHelper.InputParam();
        inputParam.detectTime = 1000;  // 800ms
        inputParam.inputNum = inputNum;
        inputParam.qCarCamera = GUtilMain.getQCamera(csiNum);
        detectInsert = QCarCamInDetectHelper.getInstance(this);
        detectInsert.setInputParam(inputParam);
    }

    private boolean mDisconnect0 = false, mDisconnect1 = false;

    @Override
    public void statusCB(int csi_num, int channel_num, int detectResult, boolean isInsert) {
        // 返回热插拔状态
//        if (mDisconnect0 && csi_num==0 && channel_num==0 && detectResult==0 && isInsert && isPreview){
//            mDisconnect0 = false;
//            MainActivity.this.runOnUiThread(mReConnect);
//        }else if ((!mDisconnect0) && csi_num==0 && channel_num==0 && detectResult==0 && (!isInsert) && isPreview){
//            mDisconnect0 = true;
//        }else if (mDisconnect1 && csi_num==1 && channel_num==0 && detectResult==0 && isInsert && isPreview){
//            mDisconnect1 = false;
//            MainActivity.this.runOnUiThread(mReConnect);
//        }else if ((!mDisconnect1) && csi_num==1 && channel_num==0 && detectResult==0 && (!isInsert) && isPreview){
//            mDisconnect1 = true;
//        }
    }

    private BaseScreenRecorderLiveStreamer streamer;

    private void addMainOsd() {
        new Thread(new Runnable() {
            Date date;
            String dateStr, dateStr2;
            String format_en = "yyyy-MM-dd HH: mm: ss";
            String format_ch = "yyyy-MM-dd HH时:mm分:ss秒";

            SimpleDateFormat sdf = new SimpleDateFormat(SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? format_ch : format_en);

            int tIndex0_1 = -1;
            int tIndex1_1 = -1;
            String font_path = "/system/fonts/Song.ttf";
            int font_size = 4;
            QCarOsd qCarOsd0, qCarOsd1;
            String speedShow;
            int sIndex0_0 = -1, sIndex0_1 = -1, sIndex0_2 = -1, sIndex1_0 = -1;

            @Override
            public void run() {
                qCarOsd0 = new QCarOsd();
                qCarOsd1 = new QCarOsd();
                qCarOsd0.initOsd(font_path.getBytes(), font_size);
                qCarOsd1.initOsd(font_path.getBytes(), font_size);
                qCarOsd0.setOsdColor(72, 170, 120);
                qCarOsd1.setOsdColor(72, 170, 120);
//                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

                if (csi1phy_num >= 0) {
                    GUtilMain.getQCamera(csi1phy_num).setMainOsd(qCarOsd0);
                }

                if (csi2phy_num >= 0) {
                    GUtilMain.getQCamera(csi2phy_num).setMainOsd(qCarOsd1);
                }
                bOsdFlag = true;
                for (int i = 0; i < pParams.getPreviewNum(); i++) {
                    if (i < CAMERANUM) {
                        if (displayStyle == 0) {
                            if (pParams.isN41Exist()) {
                                if (i < 2)
                                    qCarOsd0.setOsd(i, ((i + 1) + " " + getString(R.string.channel)).getBytes(), -1, 500, 48);
                                else
                                    qCarOsd1.setOsd(i - 2, ((i + 1) + " " + getString(R.string.channel)).getBytes(), -1, 500, 48);
                            } else {
                                qCarOsd0.setOsd(i, ((i + 1) + " " + getString(R.string.channel)).getBytes(), -1, 500, 48);
                            }
                        } else if (displayStyle == 1) {
                            if (i < 3)
                                qCarOsd0.setOsd(i, ((i + 1) + " " + getString(R.string.channel)).getBytes(), -1, 500, 48);
                            else
                                qCarOsd1.setOsd(i - 3, ((i + 1) + " " + getString(R.string.channel)).getBytes(), -1, 500, 48);
                        }
                    } else {
                        qCarOsd1.setOsd(i - 3, ((i + 1) + " " + getString(R.string.channel)).getBytes(), -1, 500, 48);
                    }
                }

                while (bOsdFlag) {
                    date = new Date();
                    dateStr = sdf.format(date);
                    tIndex0_1 = qCarOsd0.setOsd(-1, dateStr.getBytes(), tIndex0_1, 32, 48);
                    tIndex1_1 = qCarOsd1.setOsd(-1, dateStr.getBytes(), tIndex1_1, 32, 48);
                    speedShow = String.format(Locale.ENGLISH, "%s: %.0f KM/H", mSpeedMode, mGpsSpeed);//mSpeedMode+": "+mGpsSpeed+" KM/H";
                    if (displayStyle == 0) {
                        sIndex0_0 = qCarOsd0.setOsd(0, speedShow.getBytes(), sIndex0_0, SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? 800 : 680, 48);
                        sIndex0_1 = qCarOsd0.setOsd(1, speedShow.getBytes(), sIndex0_1, SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? 800 : 680, 48);
                        sIndex1_0 = qCarOsd1.setOsd(0, speedShow.getBytes(), sIndex1_0, SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? 800 : 680, 48);
                    } else {
                        sIndex0_0 = qCarOsd0.setOsd(0, speedShow.getBytes(), sIndex0_0, SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? 800 : 680, 48);
                        sIndex0_1 = qCarOsd0.setOsd(1, speedShow.getBytes(), sIndex0_1, SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? 800 : 680, 48);
                        sIndex0_2 = qCarOsd0.setOsd(2, speedShow.getBytes(), sIndex0_2, SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? 800 : 680, 48);
                    }
                    try {
                        Thread.sleep(480); // 时间字幕刷新频率
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }

                    /*if (++count > 50 && count < 100) {
                        QCarOsd.disableOsd();
                    } else if (count > 100 && count < 150) {
                        QCarOsd.enableOsd();
                    }*/

                }

                qCarOsd0.deinitOsd();
                qCarOsd1.deinitOsd();
            }
        }).start();
    }

    public void startCollision(View view) {
        RecoderInstance.getInstance().startCollision();

        Toast.makeText(getApplicationContext(), "Start Collision", Toast.LENGTH_SHORT).show();
    }

    public void startLockVideo(View view) {
        if (isRecord) {
            RecoderInstance.getInstance().startLockVideo();
            Toast.makeText(getApplicationContext(), "Start LockVideo", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Please record video first", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        if (displayStyle == 0) {
            QCarCamera instanceOneCamera = GUtilMain.getQCamera(csi1phy_num);
            instance_1 = new SurfaceViewFragment(instanceOneCamera, 0, isPreviw[0], mHandler);
            instance_1.setPreviewSize(pParams.getWidth(1), pParams.getHeight(1));

            instance_2 = new SurfaceViewFragment(GUtilMain.getQCamera(csi1phy_num), 1, isPreviw[1], mHandler);
            instance_2.setPreviewSize(pParams.getWidth(2), pParams.getHeight(2));

            if (pParams.isN41Exist()) {
                instance_3 = new SurfaceViewFragment(GUtilMain.getQCamera(csi2phy_num), 0, isPreviw[2]);
                instance_3.setPreviewSize(pParams.getWidth(4), pParams.getHeight(4));
                instance_4 = new SurfaceViewFragment(GUtilMain.getQCamera(csi2phy_num), 1, isPreviw[3]);
                instance_4.setPreviewSize(pParams.getWidth(5), pParams.getHeight(5));
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container1_0, instance_1)
                        .add(R.id.container1_1, instance_2)
                        .add(R.id.container1_2, instance_3)
                        .add(R.id.container1_3, instance_4)
                        .commit();
            } else {
                instance_3 = new SurfaceViewFragment(GUtilMain.getQCamera(csi1phy_num), 2, isPreviw[2]);
                instance_3.setPreviewSize(pParams.getWidth(3), pParams.getHeight(3));
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container1_0, instance_1)
                        .add(R.id.container1_1, instance_2)
                        .add(R.id.container1_2, instance_3)
                        .commit();
            }
        } else {
            instance_1 = new SurfaceViewFragment(GUtilMain.getQCamera(csi1phy_num), 0, isPreviw[0], mHandler);
            instance_1.setPreviewSize(pParams.getWidth(1), pParams.getHeight(1));

            instance_2 = new SurfaceViewFragment(GUtilMain.getQCamera(csi1phy_num), 1, isPreviw[1], mHandler);
            instance_2.setPreviewSize(pParams.getWidth(2), pParams.getHeight(2));

            instance_3 = new SurfaceViewFragment(GUtilMain.getQCamera(csi1phy_num), 2, isPreviw[2]);
            instance_3.setPreviewSize(pParams.getWidth(3), pParams.getHeight(3));

            instance_4 = new SurfaceViewFragment(GUtilMain.getQCamera(csi2phy_num), 0, isPreviw[3]);
            instance_4.setPreviewSize(pParams.getWidth(4), pParams.getHeight(4));

            instance_5 = new SurfaceViewFragment(GUtilMain.getQCamera(csi2phy_num), 1, isPreviw[4]);
            instance_5.setPreviewSize(pParams.getWidth(5), pParams.getHeight(5));

            instance_6 = new SurfaceViewFragment(GUtilMain.getQCamera(csi2phy_num), 2, isPreviw[5]);
            instance_6.setPreviewSize(pParams.getWidth(6), pParams.getHeight(6));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container1_0, instance_1)
                    .add(R.id.container1_1, instance_2)
                    .add(R.id.container1_2, instance_3)
                    .add(R.id.container1_3, instance_4)
                    .add(R.id.container2_0, instance_5)
                    .add(R.id.container2_1, instance_6)
                    .commit();

        }
    }

    public void startShow() {
        initView();
    }

    public void startVideoRecoder() {

        if (rParams.getRecorderNums() > 0) {
            startRecordService();
            RecoderInstance.getInstance().initRecoder(csi1phy_num, csi2phy_num);
            RecoderInstance.getInstance().startRecorder();
        }
    }

    private void startRecordService() {
        Intent recordService = new Intent(GUtilMain.getqContext(), RecordService.class);
        startService(recordService);
        registerStopVoldReceiver();
    }

    private void stopRecordService() {
        Intent recordService = new Intent(GUtilMain.getqContext(), RecordService.class);
        stopService(recordService);

        unRegisterStopVoldReceiver();
    }

    private void closeCamera() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "closeCamera csi1phy_num = " + csi1phy_num + " csi2phy_num = " + csi2phy_num);
        Thread camCloseThread0 = null;
        Thread camCloseThread1 = null;

        if (csi1phy_num >= 0) {  //N4配置
            camCloseThread0 = new Thread(new Runnable() {
                @Override
                public void run() {
                    GUtilMain.getQCamera(csi1phy_num).cameraClose();  // 关闭ais_server，必须保证最后关闭
                    GUtilMain.getQCamera(csi1phy_num).release();  // 关闭ais_server，必须保证最后关闭
                    GUtilMain.removeQCamera(csi1phy_num);
                }
            });
            camCloseThread0.start();  // 关闭ais_server，必须保证最后关闭
        }

        if (csi2phy_num >= 0) {
            camCloseThread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    GUtilMain.getQCamera(csi2phy_num).cameraClose(); // 关闭ais_server，必须保证最后关闭
                    GUtilMain.getQCamera(csi2phy_num).release();  // 关闭ais_server，必须保证最后关闭
                    GUtilMain.removeQCamera(csi2phy_num);
                }
            });
            camCloseThread1.start();  // 关闭ais_server，必须保证最后关闭
        }

        try {
            if (camCloseThread0 != null) {
                camCloseThread0.join();
            }
            if (camCloseThread1 != null) {
                camCloseThread1.join();
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private void registerStopVoldReceiver() {
        vReceiver = new VoldReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);

        filter.addDataScheme("file"); //必须加上该条，否则无法接收命令
        GUtilMain.getqContext().registerReceiver(vReceiver, filter);

        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " register media mounted and eject receiver");
    }

    private void unRegisterStopVoldReceiver() {
        if (vReceiver != null) {
            GUtilMain.getqContext().unregisterReceiver(vReceiver);
            vReceiver = null;
        }
    }


    static int nMirrorCount = -1;

    public void startMirrorImage() {
        nMirrorCount++;
        if (nMirrorCount >= 10000)
            nMirrorCount = 0;
        for (i = 0; i < 4; i++) {
            if (csi1phy_num >= 0) {
//                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "startMirrorImage csi1phy_num = " + csi1phy_num + "i = " + i + " isMirror = " + nMirrorCount % 2); //CAMERANUM 一路CAMERA的最大个数
                GUtilMain.getQCamera(csi1phy_num).setPreviewMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi1phy_num).setPreviewStreamMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi1phy_num).setVideoStreamMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi1phy_num).setSubStreamMirror(i, rParams.getVideoMirror());
            }

            if (csi2phy_num >= 0) {
//                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "startMirrorImage csi2phy_num = " + csi2phy_num + "i = " + i + " isMirror = " + nMirrorCount % 2); //CAMERANUM 一路CAMERA的最大个数
                GUtilMain.getQCamera(csi2phy_num).setPreviewMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi2phy_num).setPreviewStreamMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi2phy_num).setVideoStreamMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi2phy_num).setSubStreamMirror(i, rParams.getVideoMirror());
            }
        }

    }

    //消息钩子处理
    private class DialogHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 3) {
                adas_button.setVisibility(View.GONE);
                dms_button.setVisibility(View.GONE);
                bsd_button.setVisibility(View.GONE);
                take_pic_button.setVisibility(View.GONE);
                start_lock_video_button.setVisibility(View.GONE);
                settings_button.setVisibility(View.GONE);
                switch_default_button.setVisibility(View.GONE);
                instance_1.setCalibrationMode(true);
                isShow1_0 = false;
                instance_2.setVisible(false);
                instance_3.setVisible(false);
                instance_4.setVisible(false);
                layout1.setVisibility(View.VISIBLE);
                frame1_0.setVisibility(View.VISIBLE);
                instance_1.setVisible(true);
                frame1_1.setVisibility(View.GONE);
                frame1_2.setVisibility(View.GONE);
                frame1_3.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                if (displayStyle != 0) {
                    instance_5.setVisible(false);
                    instance_6.setVisible(false);
                    frame2_0.setVisibility(View.GONE);
                    frame2_1.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                }
                mADASConfigDialog.hide();
                return;
            } else if (msg.what == 5) {//ADAS标定坐标完成调用
                adas_button.setVisibility(View.VISIBLE);
                dms_button.setVisibility(View.VISIBLE);
                bsd_button.setVisibility(View.VISIBLE);
                take_pic_button.setVisibility(View.VISIBLE);
                start_lock_video_button.setVisibility(View.VISIBLE);
                settings_button.setVisibility(View.VISIBLE);
                switch_default_button.setVisibility(View.VISIBLE);
                isShow1_0 = true;
                instance_2.setVisible(true);
                instance_3.setVisible(true);
                instance_4.setVisible(true);
                frame1_1.setVisibility(View.VISIBLE);
                frame1_2.setVisibility(View.VISIBLE);
                frame1_3.setVisibility(View.VISIBLE);
                if (displayStyle != 0) {
                    instance_5.setVisible(true);
                    instance_6.setVisible(true);
                    frame2_0.setVisibility(View.VISIBLE);
                    frame2_1.setVisibility(View.VISIBLE);
                    layout3.setVisibility(View.VISIBLE);
                }
                layout2.setVisibility(View.VISIBLE);
                pParams.setPointX(640 + instance_1.getX());
                pParams.setPointY(360 + instance_1.getY());
                instance_1.setLineEnable(false);
                instance_2.setFacePointEnable(false);
//                System.out.println("zyz --> instance_1.getX() --> "+instance_1.getX());
//                System.out.println("zyz --> instance_1.getY() --> "+instance_1.getY());
//                System.out.println("zyz -> point --> x="+(640+instance_1.getX())+", y="+(360+instance_1.getY()));
                mADASCalibrationDialog.show();
                return;
            } else if (msg.what == 7) {
                if (isCalCall) {
                    isCalCall = false;
                    mADASConfigDialog.show();
                } else {
                    mADASConfigDialog.setADASChecked(firstStartADAS);
                }
                firstStartADAS = true;
                adas_button.setEnabled(true);
                pParams.setADASEnable(false);
                return;
            } else if (msg.what == 9) {//ADAS标定完成调用
//                int ret = SystemAlg.calibrationADAS(pParams.getCarLen(), pParams.getCarWidth(), pParams.getRefCenter(), pParams.getRefTop(), pParams.getDisLen2Tyre(), pParams.getCameraHeight(), pParams.getPointX(), pParams.getPointY());
//                System.out.println("zyz --> calibrationADAS ret = "+ret);
                adas_button.setEnabled(true);
//                if (ret != 0) {
//                    Toast.makeText(MainActivity.this, getString(R.string.adas_cal_warn), Toast.LENGTH_SHORT).show();
//                    firstStartADAS = true;
//                    pParams.setADASCalFlag(false);
//                    return;
//                }
                pParams.setADASCalFlag(true);

                if (isCalCall) {
                    isCalCall = false;
                    mADASConfigDialog.show();
                    return;
                }

                if (pParams.getADASEnable()) {
                    //启动ADAS检测线程
//                    System.out.println("zyz --> getADASEnable 9 start adas thread !!!");
                    mADASDetectThread = null;
                    mADASDetectThread = new ADASDetectThread();
                    mADASDetectThread.start();
                    adas_button.setEnabled(false);
                }
                return;
            } else if (msg.what == 11) {
                adas_button.setVisibility(View.GONE);
                dms_button.setVisibility(View.GONE);
                bsd_button.setVisibility(View.GONE);
                take_pic_button.setVisibility(View.GONE);
                start_lock_video_button.setVisibility(View.GONE);
                settings_button.setVisibility(View.GONE);
                switch_default_button.setVisibility(View.GONE);
                instance_3.setCalibrationMode(true);
                isShow1_2 = false;
                instance_1.setVisible(false);
                instance_2.setVisible(false);
                instance_4.setVisible(false);
                layout1.setVisibility(View.VISIBLE);
                frame1_0.setVisibility(View.VISIBLE);
                instance_1.setVisible(true);
                frame1_0.setVisibility(View.GONE);
                frame1_1.setVisibility(View.GONE);
                frame1_3.setVisibility(View.GONE);
                if (displayStyle != 0) {
                    instance_5.setVisible(false);
                    instance_6.setVisible(false);
                    frame2_0.setVisibility(View.GONE);
                    frame2_1.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                }
                layout1.setVisibility(View.GONE);
                return;
            } else if (msg.what == 13) {
                firstStartBSD = true;
                bsd_button.setEnabled(true);
                return;
            } else if (msg.what == 15) {//BSD标定完成调用
//                int ret = SystemAlg.calibrationBSD(pParams.getCameraHeight(), pParams.getCameraFocus(), pParams.getCameraDx(), pParams.getPointX(), pParams.getPointY(), pParams.getFirstWarnDistance(), pParams.getSecondWarnDistance(), pParams.getThirdWarnDistance(), pParams.getFrontWarnDistance());
//                System.out.println("zyz --> calibrationBSD ret = "+ret);
                bsd_button.setEnabled(true);
//                if (ret != 0) {
//                    Toast.makeText(MainActivity.this, getString(R.string.bsd_cal_warn), Toast.LENGTH_SHORT).show();
//                    firstStartBSD = true;
//                    pParams.setBSDCalFlag(false);
//                    return;
//                }
                pParams.setBSDCalFlag(true);
                return;
            } else if (msg.what == 17) {//BSD标定坐标完成后调用
                adas_button.setVisibility(View.VISIBLE);
                dms_button.setVisibility(View.VISIBLE);
                bsd_button.setVisibility(View.VISIBLE);
                take_pic_button.setVisibility(View.VISIBLE);
                start_lock_video_button.setVisibility(View.VISIBLE);
                settings_button.setVisibility(View.VISIBLE);
                switch_default_button.setVisibility(View.VISIBLE);
                isShow1_2 = true;
                instance_1.setVisible(true);
                instance_2.setVisible(true);
                instance_4.setVisible(true);
                frame1_0.setVisibility(View.VISIBLE);
                frame1_1.setVisibility(View.VISIBLE);
                frame1_3.setVisibility(View.VISIBLE);
                if (displayStyle != 0) {
                    instance_5.setVisible(true);
                    instance_6.setVisible(true);
                    frame2_0.setVisibility(View.VISIBLE);
                    frame2_1.setVisibility(View.VISIBLE);
                    layout3.setVisibility(View.VISIBLE);
                }
                layout1.setVisibility(View.VISIBLE);
                pParams.setPointX(640 + instance_1.getX());
                pParams.setPointY(360 + instance_1.getY());
                instance_1.setLineEnable(false);
                instance_2.setFacePointEnable(false);
                mBSDCalibrationDialog.show();
                return;
            } else if (msg.what == 19) {//设置速度显示模式，设置模拟速度值
                if (pParams.getSpeedMode() == 0) {
                    mSpeedMode = "GPS";
                    mGpsSpeed = 0;
                    if (!(LocationUtils.isGpsEnabled(MainActivity.this) && LocationUtils.isLocationEnabled(MainActivity.this))) {
                        showGpsConfirmDialog();
                    }
                } else {
                    mSpeedMode = "SIMULATION";
                    mGpsSpeed = pParams.getSimulationSpeed();
                }
                if (mADASConfigDialog != null)
                    mADASConfigDialog.updateGpsModeState();
                if (mDMSConfigDialog != null)
                    mDMSConfigDialog.updateGpsModeState();
//                System.out.println("zyz --> mSpeedMode --> "+mSpeedMode);
                return;
            } else if (msg.what == 21) {//ADAS确定按键，启动ADAS
//                int ret = SystemAlg.loadADASWarnConfig();
//                System.out.println("zyz --> loadAdasWarnConfig ret = " + ret);
//                if (ret != 0) {
//                    Toast.makeText(MainActivity.this, getString(R.string.adas_load_conf_err), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                ret = SystemAlg.loadDMSConfig();
////                System.out.println("zyz --> loadDMSConfig ret = " + ret);
//                if (ret != 0) {
//                    Toast.makeText(MainActivity.this, getString(R.string.dms_load_conf_err), Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (!pParams.getADASEnable()) {
                    return;
                }
                if (firstStartADAS) {
                    firstStartADAS = false;
                    GUtilMain.getQCamera(csi1phy_num).setSubStreamSize(0, 1280, 720);
                    GUtilMain.getQCamera(csi1phy_num).startSubStream(0);
                    if (!pParams.getADASCalFlag()) {
                        startCalibration();
                        return;
                    }
                }

                //启动ADAS检测线程
                mADASDetectThread = null;
                mADASDetectThread = new ADASDetectThread();
                mADASDetectThread.start();
                adas_button.setEnabled(false);
                return;
            } else if (msg.what == 23) {//开启adas标定
                startCalibration();
                isCalCall = true;
                return;
            } else if (msg.what == 25) {//DMS配置确定按键
//                int ret = SystemAlg.loadDMSConfig();
//                System.out.println("zyz --> loadDMSConfig ret = " + ret);
//                if (ret != 0) {
//                    Toast.makeText(MainActivity.this, getString(R.string.dms_load_conf_err), Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if (pParams.getDMSEnable()) {
                    mDMSDetectThread = null;
                    mDMSDetectThread = new DMSDetectThread();
                    mDMSDetectThread.start();
                    dms_button.setEnabled(false);
                }
            } else if (msg.what == 27) {//DMS身份录入按键，人脸采集完成回调，调用线程显示原来调用界面
                //显示主界面按键
                adas_button.setVisibility(View.VISIBLE);
                dms_button.setVisibility(View.VISIBLE);
                bsd_button.setVisibility(View.VISIBLE);
                take_pic_button.setVisibility(View.VISIBLE);
                start_lock_video_button.setVisibility(View.VISIBLE);
                settings_button.setVisibility(View.VISIBLE);
                switch_default_button.setVisibility(View.VISIBLE);

                //缩小DMS通道操作（防止人脸描点不显示问题）
                isShow1_1 = true;
                instance_1.setVisible(true);
                instance_3.setVisible(true);
                instance_4.setVisible(true);
                frame1_0.setVisibility(View.VISIBLE);
                frame1_2.setVisibility(View.VISIBLE);
                frame1_3.setVisibility(View.VISIBLE);
                if (displayStyle != 0) {
                    instance_5.setVisible(true);
                    instance_6.setVisible(true);
                    frame2_0.setVisibility(View.VISIBLE);
                    frame2_1.setVisibility(View.VISIBLE);
                    layout3.setVisibility(View.VISIBLE);
                }
                layout2.setVisibility(View.VISIBLE);

                //获取dms这一路一帧子码流并压缩成文件jpg
                if (GUtilMain.getQCamera(csi1phy_num) == null) {
                    System.out.println("zyz --> Not found !");
                    Toast.makeText(getApplicationContext(), getString(R.string.dms_identity_entry_faile), Toast.LENGTH_SHORT).show();
                    mDMSConfigDialog.show();
                    return;
                }

                by.clear();
                Object ret = null;
                synchronized (MainActivity.this) {
                    ret = GUtilMain.getQCamera(csi1phy_num).getSubFrameInfo(1, by);
                }
                if (ret == null) {
                    System.out.println("zyz --> DialogHandler --> get null !!!!!!");
                    Toast.makeText(getApplicationContext(), getString(R.string.dms_identity_entry_faile), Toast.LENGTH_SHORT).show();
                    mDMSConfigDialog.show();
                    return;
                } else {
                    mPath = getFilesDir().getAbsolutePath() + File.separator + pParams.getIdentityPictureName() + pParams.getFaceNumber();
                    if (QCarJpeg.jpegEncoderToFile(decodeValue(by), QCarJpeg.QUEC_YCBCR_SP, QCarJpeg.QUEC_H2V2, 1280, 720,
                            mPath.getBytes(), 90, 3000) != 0) {
                        System.out.println("zyz --> Cannot encord jpeg picture !!!");
                        Toast.makeText(getApplicationContext(), getString(R.string.dms_identity_entry_faile), Toast.LENGTH_SHORT).show();
                        mDMSConfigDialog.showIdentityDialog();
                        return;
                    }
                    mConformDialog = new AlertDialog.Builder(MainActivity.this).create();
                    mConformDialog.show();
                    mConformDialog.getWindow().setContentView(R.layout.conform_face);
                    WindowManager.LayoutParams params = mConformDialog.getWindow().getAttributes();
                    params.width = 1000;
                    params.height = 700;
                    mConformDialog.getWindow().setAttributes(params);
                    mConformDialog.setCancelable(false);
                    mConformDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new File(mPath).delete();
                            mDMSConfigDialog.showIdentityDialog();
                            mConformDialog.dismiss();
                            mConformDialog = null;
                        }
                    });
                    mConformDialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            byte[] data;
                            try {
                                data = FileUtils.toByteArray(mPath);
//                                if (!SystemAlg.readTargetFaceData(pParams.getFaceNumber(), data, data.length)) {
//                                    SystemAlg.clearFaceFeature(pParams.getFaceNumber());
//                                    System.out.println("zyz --> ConformDialog -->Failed to added face feature to array !!!");
//                                    Toast.makeText(getApplicationContext(), getString(R.string.identity_verity_face_null), Toast.LENGTH_SHORT).show();
//                                    mDMSConfigDialog.showIdentityDialog();
//                                    mConformDialog.dismiss();
//                                    mConformDialog = null;
//                                    return;
//                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("zyz --> ConformDialog --> get add identity data failed !!!");
//                                SystemAlg.clearFaceFeature(pParams.getFaceNumber());
                                Toast.makeText(getApplicationContext(), getString(R.string.identity_image_file_data_error), Toast.LENGTH_SHORT).show();
                                mDMSConfigDialog.showIdentityDialog();
                                mConformDialog.dismiss();
                                mConformDialog = null;
                                return;
                            }

                            pParams.setFaceNumber(pParams.getFaceNumber() + 1);
                            mDMSConfigDialog.setIdentityFaceInfoLine();
                            mDMSConfigDialog.showIdentityDialog();
                            mConformDialog.dismiss();
                            mConformDialog = null;
                        }
                    });
                    Bitmap bitmap = getLoacalBitmap(mPath);
                    ImageView imageView = mConformDialog.findViewById(R.id.image);
                    imageView.setImageBitmap(bitmap);
                }
                return;
            } else if (msg.what == 29) {//启动身份录入，放大dms这一路
                //屏蔽主界面按键
                adas_button.setVisibility(View.GONE);
                dms_button.setVisibility(View.GONE);
                bsd_button.setVisibility(View.GONE);
                take_pic_button.setVisibility(View.GONE);
                start_lock_video_button.setVisibility(View.GONE);
                settings_button.setVisibility(View.GONE);
                switch_default_button.setVisibility(View.GONE);
                instance_2.setIdentityEntryMode(true);
                //放大操作
                isShow1_1 = false;
                instance_1.setVisible(false);
                instance_3.setVisible(false);
                instance_4.setVisible(false);
                layout1.setVisibility(View.VISIBLE);
                frame1_1.setVisibility(View.VISIBLE);
                instance_2.setVisible(true);
                frame1_0.setVisibility(View.GONE);
                frame1_2.setVisibility(View.GONE);
                frame1_3.setVisibility(View.GONE);
                if (displayStyle != 0) {
                    instance_5.setVisible(false);
                    instance_6.setVisible(false);
                    frame2_0.setVisibility(View.GONE);
                    frame2_1.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                }
                layout2.setVisibility(View.GONE);
                instance_2.setFacePointEnable(false);
                return;
            } else if (msg.what == 31) {
                mDMSConfigDialog.show();
                return;
            }

            if (msg.what == 0) {
                settingAble = true;
                detectInsert.stopDetectThread();  //关闭热插拔检测线程
                bOsdFlag = false;
                RecoderInstance.getInstance().stopRecorder();
                onDestroy();
            } else if (msg.what == 0x01) {//向左车道偏离报警
                if (pParams.getGpioEnable()) {
                    ShellUtils.CommandResult res;
                    res = ShellUtils.execCommand(CMD_READ_LEFT_GPIO, false);
//                System.out.println("zyz --> left --> result = "+res.result+", success --> "+res.successMsg);
                    if (res.result == 0) {
                        if (res.successMsg.equals("1")) {
                            return;
                        }
                    }
                }
                mplayManagerThread.addMusic(msg.what);
            } else if (msg.what == 0x02) {//向右车道偏离报警
                if (pParams.getGpioEnable()) {
                    ShellUtils.CommandResult res;
                    res = ShellUtils.execCommand(CMD_READ_RIGHT_GPIO, false);
//                System.out.println("zyz --> right --> result = "+res.result+", success --> "+res.successMsg);
                    if (res.result == 0) {
                        if (res.successMsg.equals("1")) {
                            return;
                        }
                    }
                }
                mplayManagerThread.addMusic(msg.what);
            } else if (msg.what == 0x04 //前车碰撞报警
                    || msg.what == 0x08 //行人碰撞报警
                    || msg.what == 0x10 //车距监测报警
                    || msg.what == 0x20 //人行横道检测报警
                    || msg.what == 0x40 //右侧盲区报警
                    || msg.what == 0x80 //抽烟
                    || msg.what == 0x100 //打电话
                    || msg.what == 0x200 //打哈欠
                    || msg.what == 0x400 //未对准(偏离座位)
                    || msg.what == 0x800 //摄像头遮挡
                    || msg.what == 0x2000 //异常驾驶(检测不到脸)
                    || msg.what == 0x4000 //阻断型墨镜
                    || msg.what == 0x8000 //嘴部遮挡
                    || msg.what == 0x10000 //闭眼
                    || msg.what == 0x20000 //分神驾驶(东张)
                    || msg.what == 0x40000 //分神驾驶(西望)
                    || msg.what == 0x80000 //分神驾驶(仰望)
                    || msg.what == 0x100000 //分神驾驶(低头)
                    || msg.what == 0x200000 //低速前车碰撞报警
                    || msg.what == 0x400000 //前车启动提醒
            ) {
//                System.out.printf("zyz --> msg.what --> %x\n", msg.what);
                mplayManagerThread.addMusic(msg.what);
            } else if (msg.what == 0x500000) {
                int[] point = (int[]) msg.obj;
                if (point[0] == 0) {
                    if (!faceReset) {
                        faceReset = true;
                        mFaceVerityThread.setFaceExist(false);
//                        mHandler.removeCallbacks(StartFaceDelectRunnable);
//                        System.out.println("zyz --> removeCallbacks !!!");
                    }
                } else {
                    if (faceReset) {
                        faceReset = false;
                        mFaceVerityThread.setFaceExist(true);
//                        mHandler.postDelayed(StartFaceDelectRunnable, 2000);
//                        System.out.println("zyz --> postDelayed --> 2s");
                    }
                }
                instance_2.setDmsFacePointArray(point);
            } else if (msg.what == 0x700000) {
                int[] point = (int[]) msg.obj;
                instance_1.setRectanglePara0(point);
            } else if (msg.what == 0x900000) {//车道线1描点
                int[] point = (int[]) msg.obj;
//                System.out.println("zyz0 --> point[0]="+point[0]+", point[1]="+point[1]+", point[2]="+point[2]+", point[3]="+point[3]);
                instance_1.setPointLane1(point);
            } else if (msg.what == 0x990000) {//车道线2描点
                int[] point = (int[]) msg.obj;
//                System.out.println("zyz1 --> point[0]="+point[0]+", point[1]="+point[1]+", point[2]="+point[2]+", point[3]="+point[3]);
                instance_1.setPointLane2(point);
            }
        }
    }


    private boolean settingAble = false;
    private SettingsDialog mSettingsDialog = null;
    private DialogHandler mHandler = null;

    public void setActivity(View view) {
        if (mHandler == null)
            mHandler = new DialogHandler();
        if (mSettingsDialog == null) {
            mSettingsDialog = new SettingsDialog(this, 1280, 800, mHandler);
        }
        mSettingsDialog.show();
    }

    private boolean switchAble = false;

    public void setSwitch(View view) {
        switchAble = true;
        detectInsert.stopDetectThread();  //关闭热插拔检测线程
        bOsdFlag = false;
        RecoderInstance.getInstance().stopRecorder();
        onDestroy();
    }

    static int nCount = -1;

    public void setAudioMute(View view) {
        Button item_bt = (Button) view;
        nCount++;
        if (nCount >= 10000)
            nCount = 0;
        if (0 == nCount % 2) {
            item_bt.setText(getString(R.string.open_mute));
            QCarAudio.getInstance().setMute(true);
        } else {
            item_bt.setText(getString(R.string.mute));
            QCarAudio.getInstance().setMute(false);
        }

        Toast.makeText(getApplicationContext(), "Audio Mute", Toast.LENGTH_SHORT).show();
    }

    /***************************************************主界面按键区***************************************************/
    //主界面adas按键
    public void adasDetect(View view) {
        if (!ADAS_ENABLE) {
            Toast.makeText(getApplicationContext(), getString(R.string.adas_disable), Toast.LENGTH_SHORT).show();
            return;
        }
        //执行关闭ADAS操作
        if (pParams.getADASEnable()) {
            mADASDetectThread.setAlive(false);
            adas_button.setEnabled(false);
            pParams.setADASEnable(false);
            mADASConfigDialog.setADASChecked(false);
            return;
        }
        //打开ADAS需通过ADAS配置
        if (mADASConfigDialog == null) {
            mADASConfigDialog = new ADASConfigDialog(MainActivity.this, 1280, 720, mHandler);
        }
        mADASConfigDialog.show();
    }

    //主界面dms按键
    public void dmsDetect(View view) {
        if (!DMS_ENABLE) {
            Toast.makeText(getApplicationContext(), getString(R.string.dms_disable), Toast.LENGTH_SHORT).show();
            return;
        }
        //首次点击需先初始化子码流
        if (firstStartDMS) {
            firstStartDMS = false;
            GUtilMain.getQCamera(csi1phy_num).setSubStreamSize(1, /*pParams.getWidth(2)*/1280, /*pParams.getHeight(2)*/720);
            GUtilMain.getQCamera(csi1phy_num).startSubStream(1);
        }
        //执行关闭DMS操作
        if (pParams.getDMSEnable()) {
            mFaceVerityThread.setCumulativeTimes(0);
            mDMSDetectThread.setAlive(false);
            pParams.setDMSEnable(false);
            mDMSConfigDialog.setDMSChecked(false);
            dms_button.setEnabled(false);
            return;
        }
        //打开DMS需通过DMS配置
        if (mDMSConfigDialog == null) {
            mDMSConfigDialog = new DMSConfigDialog(MainActivity.this, 1280, 720, mHandler);
        }
        mDMSConfigDialog.show();
    }

    //主界面bsd按键
    public void bsdDetect(View view) {
        if (!BSD_ENABLE) {
            Toast.makeText(getApplicationContext(), getString(R.string.bsd_disable), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bsd_button.getText().equals(getString(R.string.start_bsd))) {
            mBSDDetectThread.setAlive(false);
            bsd_button.setEnabled(false);
            return;
        }
        mBSDDialog = new AlertDialog.Builder(MainActivity.this).create();
        mBSDDialog.show();
        mBSDDialog.getWindow().setContentView(R.layout.secondary_button);
        setDialog(mBSDDialog, 500, 120);
        mStart = mBSDDialog.findViewById(R.id.start);
        mCalibration = mBSDDialog.findViewById(R.id.calibration);

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBSDDialog.dismiss();
                mBSDDialog = null;
                if (firstStartBSD) {
                    firstStartBSD = false;
                    GUtilMain.getQCamera(displayStyle == 0 ? csi2phy_num : csi1phy_num).setSubStreamSize(displayStyle == 0 ? 0 : 2, 1280, 720);
                    GUtilMain.getQCamera(displayStyle == 0 ? csi2phy_num : csi1phy_num).startSubStream(displayStyle == 0 ? 0 : 2);
                    if (!pParams.getBSDCalFlag()) {
                        if (mBSDCalibrationDialog == null)
                            mBSDCalibrationDialog = new CalibrationDialog(MainActivity.this, 1, 1280, 720, mHandler);
                        mBSDCalibrationDialog.show();
                        bsd_button.setEnabled(false);
                        return;
                    }
                }

                //启动BSD检测线程
                mBSDDetectThread = null;
                mBSDDetectThread = new BSDDetectThread();
                mBSDDetectThread.start();
                bsd_button.setEnabled(false);
            }
        });

        mCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBSDDialog.dismiss();
                mBSDDialog = null;
                if (mBSDCalibrationDialog == null)
                    mBSDCalibrationDialog = new CalibrationDialog(MainActivity.this, 1, 1280, 720, mHandler);
                mBSDCalibrationDialog.show();
                bsd_button.setEnabled(false);

            }
        });
    }

    public synchronized void startTakePic(View view) {
        if (getStoragePath(MainActivity.this, true) != null && getStoragePath(MainActivity.this, true).contains("storage")) {
            if (m_bTakePicFinshFlag == false) {
                return;
            }
            m_bTakePicFinshFlag = false;
            //如果开启录像，拍录像分辨率的照片，如果没有开启录像，拍预览分辨率的照片
            Vector<QCarPicWriter> qCarPicWriterVector = new Vector<>();
            QCarPicWriter jpegEncoder;
            if (csi1phy_num >= 0) {
                for (int i = 0; i < opencsi0_nums; i++) {
                    jpegEncoder = new QCarPicWriter(this, GUtilMain.getQCamera(csi1phy_num),
                            i,
                            rParams.getWidth(0, i),
                            rParams.getHeight(0, i),
                            -1);
                    jpegEncoder.startJpegEncoderThread();
                    qCarPicWriterVector.add(jpegEncoder);
                }
            }

            if (csi2phy_num >= 0) {
                for (int i = 0; i < opencsi1_nums; i++) {
                    jpegEncoder = new QCarPicWriter(this, GUtilMain.getQCamera(csi2phy_num),
                            i,
                            rParams.getWidth(1, i),
                            rParams.getHeight(1, i),
                            -1);
                    jpegEncoder.startJpegEncoderThread();
                    qCarPicWriterVector.add(jpegEncoder);
                }
            }

            for (QCarPicWriter qCarPicWriter : qCarPicWriterVector) {
                qCarPicWriter.waitJpenEncorderEnd();
            }
            //连续快速点击拍照，会导致崩溃，主要是Toast显示的问题，上一个Toast没有显示完，这个一个Toast又在显示，会导致崩溃
            Toast.makeText(getApplicationContext(), "Take picture End ", Toast.LENGTH_SHORT).show();
            m_bTakePicFinshFlag = true;
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.sdcard_disable), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPause() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onStop");
        super.onStop();  //会执行previewFragment stop
    }

    private boolean isBack = false;

    @Override
    public void onBackPressed() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " onBackPressed");
        super.onBackPressed();
        isBack = true;
        mplayManagerThread.stopThread();

        synchronized (this) {
            try {
                wait(300);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        detectInsert.stopDetectThread();  //关闭热插拔检测线程
        bOsdFlag = false;

        RecoderInstance.getInstance().stopRecorder();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onDestroy() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onDestroy");
        super.onDestroy();
        closeCamera();
        stopRecordService();
        unregisterReceiver(mReceiver);

        LocationUtils.unregister();
//        SystemAlg.detectUnInit();

        if (mSettingsDialog != null) {
            mSettingsDialog.dismiss();
            mSettingsDialog = null;
        }
        if (settingAble) {
            settingAble = false;
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        if (switchAble) {
            switchAble = false;
            Intent intent = new Intent(MainActivity.this, BackCamera.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private class ADASDetectThread extends Thread {
        private boolean alive = true;
        private float speed = 0;

        @Override
        public void run() {

            Object ret = null;

            byte[] data;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adas_button.setEnabled(true);
                    adas_button.setText(getString(R.string.stop_adas));
                    instance_1.setADASEnable(true);
                }
            });
            pParams.setADASEnable(true);
            while (true) {
                try {
                    Thread.sleep(90);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                by_adas.clear();

                if (!alive) {
                    System.out.println("zyz --> Stop adas thread !");
                    break;
                }

                if (GUtilMain.getQCamera(csi1phy_num) == null) {
                    System.out.println("zyz --> Not found !");
                    break;
                }
                ret = GUtilMain.getQCamera(csi1phy_num).getSubFrameInfo(0, by_adas);
                if (ret == null) {
                    System.out.println("zyz --> ADASDetectThread --> get null !!!!!!");
                    break;
                } else {
                    data = decodeValue(by_adas);
//                    SystemAlg.doADAS(data, (float) mGpsSpeed);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adas_button.setEnabled(true);
                    adas_button.setText(getString(R.string.start_adas));
                    instance_1.setADASEnable(false);
                }
            });
            pParams.setADASEnable(false);
        }

        public void setAlive(boolean alive) {
            this.alive = alive;
        }

        public void setSpeed(float s) {
            speed = s;
        }
    }

    /***************************************************线程区***************************************************/
    //播放管理线程
    private class playManagerThread extends Thread {
        private int simp = 0;
        private IntQueueUtils listMusic = null;
        private boolean alive = true;

        public playManagerThread(int m) {
            listMusic = new IntQueueUtils(m);
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                if (!alive) {
                    return;
                }
                if (mBeepManager.isPlayingBeep()) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                if (listMusic.isEmpty()) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                simp = listMusic.peekFront();
                if (simp == 0x01 || simp == 0x02) {//向左车道偏离报警
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_ldw : R.raw.event_ldw_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_ldw : R.raw.event_2_ldw_english));
                } else if (simp == 0x04) {//前车碰撞报警
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_fcw : R.raw.event_fcw_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_fcw : R.raw.event_2_fcw_english));
                } else if (simp == 0x08) {//行人碰撞报警
                    mBeepManager.playBeepSoundAndVibrate(SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_pcw : R.raw.event_pcw_english);
                } else if (simp == 0x10) {//车距监测报警
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_hmw : R.raw.event_hmw_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_hmw : R.raw.event_2_hmw_english));
                } else if (simp == 0x20) {//人行横道检测报警
                    mBeepManager.playBeepSoundAndVibrate(SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_cross_walk : R.raw.event_cross_walk_english);
                } else if (simp == 0x40) {//右侧盲区报警
                    mBeepManager.playBeepSoundAndVibrate(SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_blind : R.raw.event_blind_english);
                } else if (simp == 0x80) {//抽烟
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_smoke : R.raw.event_smoke_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_smoke : R.raw.event_2_smoke_english));
                } else if (simp == 0x100) {//打电话
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_call : R.raw.event_call_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_call : R.raw.event_2_call_english));
                } else if (simp == 0x200) {//打哈欠
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_yawn : R.raw.event_yawn_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_yawn : R.raw.event_2_yawn_english));
                } else if (simp == 0x400) {//未对准(偏离座位)
                    mBeepManager.playBeepSoundAndVibrate(SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_no_alignment : R.raw.event_no_alignment_english);
                } else if (simp == 0x800) {//摄像头遮挡
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_camera_shield : R.raw.event_camera_shield_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_camera_shield : R.raw.event_2_camera_shield_english));
                } else if (simp == 0x1000) {//身份异常(不是原来的驾驶员)
                    mBeepManager.playBeepSoundAndVibrate(SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_abnormal_identity : R.raw.event_abnormal_identity_english);
                } else if (simp == 0x1001) {//身份识别正确(是原来的驾驶员)
                    mBeepManager.playBeepSoundAndVibrate(SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_driver_identification_successful : R.raw.event_driver_identification_successful_english);
                } else if (simp == 0x2000) {//异常驾驶(检测不到脸)
                    mBeepManager.playBeepSoundAndVibrate(SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_absent_driver : R.raw.event_2_absent_driver_english);
                } else if (simp == 0x4000) {//阻断型墨镜
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_eyes_masked : R.raw.event_eyes_masked_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_eyes_masked : R.raw.event_2_eyes_masked_english));
                } else if (simp == 0x8000) {//嘴部遮挡
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_mouth_masked : R.raw.event_mouth_masked_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_mouth_masked : R.raw.event_2_mouth_masked_english));
                } else if (simp == 0x10000) {//闭眼
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_eye_closing : R.raw.event_eye_closing_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_eye_closing : R.raw.event_2_eye_closing_english));
                } else if (simp == 0x20000 || simp == 0x40000 || simp == 0x80000 || simp == 0x100000) {//分神驾驶(东张)
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_carefull : R.raw.event_carefull_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_carefull : R.raw.event_2_carefull_english));
                } else if (simp == 0x200000) {//低速前车碰撞报警
                    mBeepManager.playBeepSoundAndVibrate(mGpsSpeed < pParams.getSecondWarnOpenSpeed() ? (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_fcw : R.raw.event_fcw_english) : (SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_2_fcw : R.raw.event_2_fcw_english));
                } else if (simp == 0x400000) {//前车启动提醒
                    mBeepManager.playBeepSoundAndVibrate(SpUtil.getInstance(MainActivity.this).getString(SpUtil.LANGUAGE).equals("ch") ? R.raw.event_follow_front : R.raw.event_follow_front_english);
                }
            }
        }

        public void addMusic(int m) {
            if (!listMusic.isFull()) {
                if (!listMusic.inquire(m))
                    listMusic.insert(m);
            }
        }

        public void stopThread() {
            alive = false;
        }
    }

    private class BSDDetectThread extends Thread {
        private boolean alive = true;

        @Override
        public void run() {

            Object ret = null;

            byte[] data;
            System.out.println("zyz --> Start bsd thread !");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bsd_button.setEnabled(true);
                    bsd_button.setText(getString(R.string.stop_bsd));
                }
            });
            while (true) {
                try {
                    Thread.sleep(90);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                by_bsd.clear();

                if (!alive) {
                    System.out.println("zyz --> Stop bsd thread !");
                    break;
                }

                //当显示2-2形式的时候，bsd处于第3路， 实际使用的是csi2的第1路，所以使用displayStyle做判断
                if (GUtilMain.getQCamera(displayStyle == 0 ? csi1phy_num : csi2phy_num) == null) {
                    System.out.println("zyz --> Not found !");
                    break;
                }
                ret = GUtilMain.getQCamera(displayStyle == 0 ? csi2phy_num : csi1phy_num).getSubFrameInfo(displayStyle == 0 ? 0 : 2, by_bsd);
                if (ret == null) {
                    System.out.println("zyz --> bsd camera get null !!!!!!");
                    break;
                } else {
                    data = decodeValue(by_bsd);
//                    SystemAlg.doBSD(data, (float) mGpsSpeed);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bsd_button.setEnabled(true);
                    bsd_button.setText(getString(R.string.start_bsd));
                }
            });
            return;
        }

        public void setAlive(boolean alive) {
            this.alive = alive;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (ADAS_ENABLE) adas_button.setVisibility(button_visible ? View.GONE : View.VISIBLE);
            if (DMS_ENABLE) dms_button.setVisibility(button_visible ? View.GONE : View.VISIBLE);
            if (BSD_ENABLE) bsd_button.setVisibility(button_visible ? View.GONE : View.VISIBLE);
            take_pic_button.setVisibility(button_visible ? View.GONE : View.VISIBLE);
            start_lock_video_button.setVisibility(button_visible ? View.GONE : View.VISIBLE);
            settings_button.setVisibility(button_visible ? View.GONE : View.VISIBLE);
            switch_default_button.setVisibility(button_visible ? View.GONE : View.VISIBLE);
            button_visible = !button_visible;
        }
        return super.onKeyUp(keyCode, event);
    }


    /**
     * GPS功能打开开关
     */
    private void showGpsConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("GPS")
                .setMessage(getString(R.string.gps_confirm))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocationUtils.openGpsSettings(MainActivity.this);
                    }
                }).show();
    }

    private void startCalibration() {
        if (mADASCalibrationDialog == null)
            mADASCalibrationDialog = new CalibrationDialog(MainActivity.this, 0, 1280, 720, mHandler);
        mADASCalibrationDialog.show();
        adas_button.setEnabled(false);
    }

    /**
     * 加载本地图片
     *
     * @param url 本地图片路径
     * @return Bitmap格式图片数据
     */
    private Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setDialog(AlertDialog dialog, int width, int height) {
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = width;
        params.height = height;
        dialog.getWindow().setAttributes(params);
    }
}
