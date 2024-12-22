package com.quectel.multicamera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quectel.qcarapi.stream.QCarCamera;
import com.quectel.qcarapi.util.QCarLog;


public class SurfaceViewFragment extends Fragment {
    private static final String TAG = "PreviewFragment";
    private SurfaceView preview;
    private SurfaceHolder surfaceHolder;
    private int mChannel;
    private QCarCamera qCarCamera;
    private int mIsPreview;
    private int preWidth;
    private int preHeight;
    private ImageButton mTop, mBottom, mLeft, mRight;
    private LinearLayout mMark, mRange;
    private int x,y;
    private Button mCalCom, mEntryCom;
    private LinearLayout mDirection;

    private ViewGroup.MarginLayoutParams mlp;

    private Handler mHandler;
    private int mType;

    private DefineLinearLayout myLayout;
    public SurfaceViewFragment() {

    }

    @SuppressLint("ValidFragment")
    public SurfaceViewFragment(QCarCamera qCarCamera, int channel, int ispreview) {
        this.qCarCamera = qCarCamera;
        this.mChannel = channel;
        this.mIsPreview = ispreview;
    }

    @SuppressLint("ValidFragment")
    public SurfaceViewFragment(QCarCamera qCarCamera, int channel, int ispreview, Handler handler) {
        this.qCarCamera = qCarCamera;
        this.mChannel = channel;
        this.mIsPreview = ispreview;
        mHandler = handler;
    }

    @SuppressLint("ValidFragment")
    public SurfaceViewFragment(QCarCamera qCarCamera, int channel, int ispreview, Handler handler, int type) {
        this.qCarCamera = qCarCamera;
        this.mChannel = channel;
        this.mIsPreview = ispreview;
        mHandler = handler;
        mType = type;
    }

    public void setPreviewSize(int width, int height) {
        preWidth = width;
        preHeight = height;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (qCarCamera != null) {
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "get csi num "+qCarCamera.getCsiNum()+" channel "+mChannel+" IsPreview "+mIsPreview);
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "get csi num "+qCarCamera.setFpsLogDebug(mChannel,1)+" channel "+mChannel+" IsPreview "+mIsPreview);
        }
    }

    public void setVisible(boolean visible){
        if (visible) {
            preview.setVisibility(View.VISIBLE);
        }else {
            preview.setVisibility(View.INVISIBLE);
        }
    }

    public void setADASEnable(boolean enable){
        myLayout.setADASEnable(enable);
    }

    public void setLineEnable(boolean enable){
        myLayout.setLineEnable(enable);
    }

    public void setPointLane2(int[] lane){
        myLayout.setPointLane2(lane);
    }

    public void setPointLane1(int[] lane){
        myLayout.setPointLane1(lane);
    }

    public void setRectanglePara0(int[] array){
        myLayout.setRectanglePara0(array);
    }

    public void setDMSEnable(boolean enable){
        myLayout.setDMSEnable(enable);
    }

    public void setFacePointEnable(boolean enable){
        myLayout.setFacePointEnable(enable);
    }

    public void setDmsFacePointArray(int[] array){
        myLayout.setDmsFacePointArray(array);
    }

    public void setCalibrationMode(boolean mode){
        mMark.setVisibility(mode?View.VISIBLE:View.GONE);
        mCalCom.setVisibility(mode?View.VISIBLE:View.GONE);
        mDirection.setVisibility(mode?View.VISIBLE:View.GONE);
    }

    public void setIdentityEntryMode(boolean mode){
        mEntryCom.setVisibility(mode?View.VISIBLE:View.GONE);
        mRange.setVisibility(mode?View.VISIBLE:View.GONE);
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_surface_view, container, false);

        myLayout = rootView.findViewById(R.id.line);

        mCalCom = rootView.findViewById(R.id.cal_com);
        mCalCom.setOnClickListener(new BtnClick());

        mEntryCom = rootView.findViewById(R.id.entry_com);
        mEntryCom.setOnClickListener(new BtnClick());

        mTop = rootView.findViewById(R.id.top);
        mBottom = rootView.findViewById(R.id.bottom);
        mLeft = rootView.findViewById(R.id.left);
        mRight = rootView.findViewById(R.id.right);

        mDirection = rootView.findViewById(R.id.direction);
        mMark = rootView.findViewById(R.id.mark);
        mlp = (ViewGroup.MarginLayoutParams) mMark.getLayoutParams();
        x = mMark.getLeft();
        y = mMark.getTop();

        //身份录入红框
        mRange = rootView.findViewById(R.id.range);

        preview = (SurfaceView) rootView.findViewById(R.id.preview);
        surfaceHolder = preview.getHolder();

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onCreateView set csi num "+qCarCamera.getCsiNum()+" channel "+mChannel+" IsPreview "+mIsPreview);
                if(mIsPreview == 1) {
                    qCarCamera.startPreview( mChannel,holder.getSurface(), preWidth, preHeight,QCarCamera.YUV420_NV21);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "surfaceDestroyed: mCsiphyNum = " + qCarCamera.getCsiNum() + ", mChannel = " + mChannel);
                if(mIsPreview == 1) {
                    qCarCamera.stopPreview( mChannel);
                    try{
                        Thread.sleep(100);
                    }catch(Exception e){

                    }
                }
            }
        });

        mTop.setOnClickListener(new BtnClick());
        mBottom.setOnClickListener(new BtnClick());
        mLeft.setOnClickListener(new BtnClick());
        mRight.setOnClickListener(new BtnClick());

        return rootView;
    }

    private class BtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mlp = (ViewGroup.MarginLayoutParams) mMark.getLayoutParams();
            if (v == mTop){
                mlp.topMargin = (y-=8);
            }else if (v == mBottom){
                mlp.topMargin = (y+=8);
            }else if (v == mLeft){
                mlp.leftMargin = (x-=8);
            }else if (v == mRight){
                mlp.leftMargin = (x+=8);
            }
            mMark.setLayoutParams(mlp);

            if (v == mCalCom){
                setCalibrationMode(false);
                Message msg = mHandler.obtainMessage();
                msg.what = mType==0?5:17;
                mHandler.sendMessage(msg);
//                System.out.println("zyz --> x="+x+", y="+y);
            }
            if (v == mEntryCom){
                setIdentityEntryMode(false);
                Message msg = mHandler.obtainMessage();
                msg.what = 27;
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStop() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onDestroy");
        super.onDestroy();
    }
}
