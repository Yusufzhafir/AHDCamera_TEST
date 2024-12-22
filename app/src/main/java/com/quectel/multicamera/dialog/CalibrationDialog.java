package com.quectel.multicamera.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quectel.multicamera.R;
import com.quectel.multicamera.utils.GUtilMain;
import com.quectel.multicamera.utils.PreviewParams;

public class CalibrationDialog extends AlertDialog {
    private Handler mHandler;
    private Button mCancel = null;
    private Button mOk = null;
    private PreviewParams pParams ;
    private Context mContext;
    private int mType;
    private int width,height;
    private float fCarLen,fCarWidth,fRefCenter,fRefTop,fDisLen2Tyre,fCameraHeight,fCameraFocus,fCameraDx,fFirstWarnDistance,fSecondWarnDistance,fThirdWarnDistance,fFrontWarnDistance;
    private TextView mTitle;
    private EditText vCarLen,vCarWidth,vRefCenter,vRefTop,vDisLen2Tyre,vCameraHeight,vCameraFocus,vCameraDx,vFirstWarnDistance,vSecondWarnDistance,vThirdWarnDistance,vFrontWarnDistance;
    private LinearLayout adas_car_len,adas_car_width,adas_ref_center,adas_ref_top,adas_dis_len_tyre,bsd_first_warn_d,bsd_second_warn_d,bsd_third_warn_d,bsd_front_warn_d,focus,dx;
    private boolean calFlag = false;

    public CalibrationDialog(Context context, int type, int width, int height, Handler handler) {
        super(context);
        mContext = context;

        mType = type;

        this.width = width;
        this.height = height;

        mHandler = handler;
        pParams = GUtilMain.getPreviewParams();
    }

    private Button mStartCal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setCancelable(false);
        setContentView(R.layout.calibration_set);

        mTitle = findViewById(R.id.title);
        mTitle.setText(mType==0?mContext.getString(R.string.adas_para_config):mContext.getString(R.string.bsd_para_config));

        adas_car_len = findViewById(R.id.adas_car_len);
        adas_car_width = findViewById(R.id.adas_car_width);
        adas_ref_center = findViewById(R.id.adas_ref_center);
        adas_ref_top = findViewById(R.id.adas_ref_top);
        adas_dis_len_tyre = findViewById(R.id.adas_dis_len_tyre);
        bsd_first_warn_d = findViewById(R.id.bsd_first_warn_d);
        bsd_second_warn_d = findViewById(R.id.bsd_second_warn_d);
        bsd_third_warn_d = findViewById(R.id.bsd_third_warn_d);
        bsd_front_warn_d = findViewById(R.id.bsd_front_warn_d);
        focus = findViewById(R.id.focus);
        dx = findViewById(R.id.dx);

        adas_car_len.setVisibility(mType==0?View.VISIBLE:View.GONE);
        adas_car_width.setVisibility(mType==0?View.VISIBLE:View.GONE);
        adas_ref_center.setVisibility(mType==0?View.VISIBLE:View.GONE);
        adas_ref_top.setVisibility(mType==0?View.VISIBLE:View.GONE);
        adas_dis_len_tyre.setVisibility(mType==0?View.VISIBLE:View.GONE);
        bsd_first_warn_d.setVisibility(mType==0?View.GONE:View.VISIBLE);
        bsd_second_warn_d.setVisibility(mType==0?View.GONE:View.VISIBLE);
        bsd_third_warn_d.setVisibility(mType==0?View.GONE:View.VISIBLE);
        bsd_front_warn_d.setVisibility(mType==0?View.GONE:View.VISIBLE);

        focus.setVisibility(mType==0?View.GONE:View.VISIBLE);
        dx.setVisibility(mType==0?View.GONE:View.VISIBLE);

        vCarLen = findViewById(R.id.car_len);
        vCarWidth = findViewById(R.id.car_width);
        vRefCenter = findViewById(R.id.ref_center);
        vRefTop = findViewById(R.id.ref_top);
        vDisLen2Tyre = findViewById(R.id.dis_len_tyre);
        vCameraHeight = findViewById(R.id.camera_height);
        vCameraFocus = findViewById(R.id.camera_focus);
        vCameraDx = findViewById(R.id.camera_dx);
        vFirstWarnDistance = findViewById(R.id.first_warn_d);
        vSecondWarnDistance = findViewById(R.id.second_warn_d);
        vThirdWarnDistance = findViewById(R.id.third_warn_d);
        vFrontWarnDistance = findViewById(R.id.front_warn_d);

        if (mType == 0) {
            vCarLen.setText(pParams.getCarLen()+"");
            vCarWidth.setText(pParams.getCarWidth()+"");
            vRefCenter.setText(pParams.getRefCenter()+"");
            vRefTop.setText(pParams.getRefTop()+"");
            vDisLen2Tyre.setText(pParams.getDisLen2Tyre()+"");
        }else if (mType == 1){
            vFirstWarnDistance.setText(pParams.getFirstWarnDistance()+"");
            vSecondWarnDistance.setText(pParams.getSecondWarnDistance()+"");
            vThirdWarnDistance.setText(pParams.getThirdWarnDistance()+"");
            vFrontWarnDistance.setText(pParams.getFrontWarnDistance()+"");
        }

        vCameraHeight.setText(pParams.getCameraHeight()+"");
        vCameraFocus.setText(pParams.getCameraFocus()+"");
        vCameraDx.setText(pParams.getCameraDx()+"");


        mStartCal = findViewById(R.id.cal_point_xy);
        mStartCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalibrationDialog.this.hide();
                Message msg = mHandler.obtainMessage();
                msg.what = mType==0?3:11;
                mHandler.sendMessage(msg);
                calFlag = true;
            }
        });

        mCancel = findViewById(R.id.cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalibrationDialog.this.hide();
                Message msg = mHandler.obtainMessage();
                msg.what = mType==0?7:13;
                mHandler.sendMessage(msg);
            }
        });

        mOk = findViewById(R.id.ok);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType == 0) {
                    if (vCarLen.getText().toString().equals("-1.0")
                            || vCarLen.getText().toString().equals("")
                            || vCarWidth.getText().toString().equals("-1.0")
                            || vCarWidth.getText().toString().equals("")
                            || vRefCenter.getText().toString().equals("-1.0")
                            || vRefCenter.getText().toString().equals("")
                            || vRefTop.getText().toString().equals("-1.0")
                            || vRefTop.getText().toString().equals("")
                            || vDisLen2Tyre.getText().toString().equals("-1.0")
                            || vDisLen2Tyre.getText().toString().equals("0")
                            || vCameraHeight.getText().toString().equals("-1.0")
                            || vCameraHeight.getText().toString().equals("")
                            || vCameraFocus.getText().toString().equals("-1.0")
                            || vCameraFocus.getText().toString().equals("")
                            || vCameraDx.getText().toString().equals("-1.0")
                            || vCameraDx.getText().toString().equals("")
                    ) {
                        Toast.makeText(mContext, mContext.getString(R.string.blank_warn), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fCarLen = Float.parseFloat(vCarLen.getText().toString());
                    fCarWidth = Float.parseFloat(vCarWidth.getText().toString());
                    fRefCenter = Float.parseFloat(vRefCenter.getText().toString());
                    fRefTop = Float.parseFloat(vRefTop.getText().toString());
                    fDisLen2Tyre = Float.parseFloat(vDisLen2Tyre.getText().toString());
                    if (fCarLen<2000f || fCarLen>10000f){
                        Toast.makeText(mContext,mContext.getString(R.string.car_len_warn),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (fCarWidth<1200 || fCarWidth>3000){
                        Toast.makeText(mContext,mContext.getString(R.string.car_width_warn),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (fRefCenter<0 || fRefCenter>600){
                        Toast.makeText(mContext,mContext.getString(R.string.ref_center_warn),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (fRefTop<0 || fRefTop>3000){
                        Toast.makeText(mContext,mContext.getString(R.string.ref_top_warn),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (fDisLen2Tyre<-2000 || fDisLen2Tyre>2000){
                        Toast.makeText(mContext,mContext.getString(R.string.dis_len_tyre_warn),Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else {
                    if (vCameraHeight.getText().toString().equals("-1.0")
                            || vCameraHeight.getText().toString().equals("")
                            || vCameraFocus.getText().toString().equals("-1.0")
                            || vCameraFocus.getText().toString().equals("")
                            || vCameraDx.getText().toString().equals("-1.0")
                            || vCameraDx.getText().toString().equals("")
                            || vFirstWarnDistance.getText().toString().equals("-1.0")
                            || vFirstWarnDistance.getText().toString().equals("")
                            || vSecondWarnDistance.getText().toString().equals("-1.0")
                            || vSecondWarnDistance.getText().toString().equals("")
                            || vThirdWarnDistance.getText().toString().equals("-1.0")
                            || vThirdWarnDistance.getText().toString().equals("")
                            || vFrontWarnDistance.getText().toString().equals("-1.0")
                            || vFrontWarnDistance.getText().toString().equals("")
                    ) {
                        Toast.makeText(mContext, mContext.getString(R.string.blank_warn), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fFirstWarnDistance = Float.parseFloat(vFirstWarnDistance.getText().toString());
                    fSecondWarnDistance = Float.parseFloat(vSecondWarnDistance.getText().toString());
                    fThirdWarnDistance = Float.parseFloat(vSecondWarnDistance.getText().toString());
                    fFrontWarnDistance = Float.parseFloat(vSecondWarnDistance.getText().toString());

                    if (fFirstWarnDistance<0 || fDisLen2Tyre>3000){
                        Toast.makeText(mContext,mContext.getString(R.string.bsd_first_warn_t),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (fSecondWarnDistance<0 || fSecondWarnDistance>6000){
                        Toast.makeText(mContext,mContext.getString(R.string.bsd_second_warn_t),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (fThirdWarnDistance<0 || fThirdWarnDistance>9000){
                        Toast.makeText(mContext,mContext.getString(R.string.bsd_third_warn_t),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (fFrontWarnDistance<0 || fFrontWarnDistance>15000){
                        Toast.makeText(mContext,mContext.getString(R.string.bsd_front_warn_t),Toast.LENGTH_SHORT).show();
                        return;
                    }

                }


                fCameraHeight = Float.parseFloat(vCameraHeight.getText().toString());
                fCameraFocus = Float.parseFloat(vCameraFocus.getText().toString());
                fCameraDx = Float.parseFloat(vCameraDx.getText().toString());

                if (fCameraHeight<0 || fCameraHeight>3000){
                    Toast.makeText(mContext,mContext.getString(R.string.camera_height_warn),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fCameraFocus<0 || fCameraFocus>100){
                    Toast.makeText(mContext,mContext.getString(R.string.camera_focus_warn),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fCameraDx<0 || fCameraDx>10){
                    Toast.makeText(mContext,mContext.getString(R.string.camera_dx_warn),Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!calFlag){
                    Toast.makeText(mContext,mContext.getString(R.string.point_warn),Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pParams.getPointX()<322 || pParams.getPointX()>950 || pParams.getPointY()<182 || pParams.getPointY()>530){
                    Toast.makeText(mContext,mContext.getString(R.string.point_out_warn),Toast.LENGTH_SHORT).show();
                    return;
                }

                CalibrationDialog.this.hide();


//                System.out.println("zyz --> fCarLen="+fCarLen);
//                System.out.println("zyz --> fCarWidth="+fCarWidth);
//                System.out.println("zyz --> fRefCenter="+fRefCenter);
//                System.out.println("zyz --> fRefTop="+fRefTop);
//                System.out.println("zyz --> fDisLen2Tyre="+fDisLen2Tyre);
//                System.out.println("zyz --> fCameraHeight="+fCameraHeight);
//                System.out.println("zyz --> fCameraFocus="+fCameraFocus);
//                System.out.println("zyz --> fCameraDx="+fCameraDx);


                if (mType == 0){
                    pParams.setCarLen(fCarLen);
                    pParams.setCarWidth(fCarWidth);
                    pParams.setRefCenter(fRefCenter);
                    pParams.setRefTop(fRefTop);
                    pParams.setDisLen2Tyre(fDisLen2Tyre);
                }else if (mType == 1){
                    pParams.setFirstWarnDistance(fFirstWarnDistance);
                    pParams.setSecondWarnDistance(fSecondWarnDistance);
                    pParams.setThirdWarnDistance(fThirdWarnDistance);
                    pParams.setFrontWarnDistance(fFrontWarnDistance);
                }

                pParams.setCameraHeight(fCameraHeight);
                pParams.setCameraFocus(fCameraFocus);
                pParams.setCameraDx(fCameraDx);

                Message msg = mHandler.obtainMessage();
                msg.what = mType==0?9:15;
                mHandler.sendMessage(msg);
            }
        });
    }
}
