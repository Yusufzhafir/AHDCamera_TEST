package com.quectel.multicamera.dialog;

//import android.ai.SystemAlg;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.quectel.multicamera.R;
import com.quectel.multicamera.utils.ConfigMgrUtils;
import com.quectel.multicamera.utils.GUtilMain;
import com.quectel.multicamera.utils.PreviewParams;

import java.io.IOException;

public class DMSConfigDialog extends AlertDialog {
    private Context mContext;
    private Handler mHandler;
    private PreviewParams pParams ;
//    private static String mPath;
    private int width, height;

    private static final String EVENT_SMOKE = "SMOKE_TYPE";
    private static final String EVENT_CALL = "CALL_TYPE";
    private static final String EVENT_YAWN = "YAWN_TYPE";
    private static final String EVENT_NO_ALIGNMENT = "NO_ALIGNMENT_TYPE";
    private static final String EVENT_CAMERA_SHIELD = "CAMERA_SHIELD_TYPE";
    private static final String EVENT_ABSENT_DRIVER = "ABSENT_DRIVER_TYPE";
    private static final String EVENT_EYES_MASKED = "EYES_MASKED_TYPE";
    private static final String EVENT_MOUTH_MASKED = "MOUTH_MASKED_TYPE";
    private static final String EVENT_EYE_CLOSING = "EYE_CLOSING_TYPE";
    private static final String EVENT_SWINGING_LEFT = "SWINGING_LEFT_TYPE";
    private static final String EVENT_SWINGING_RIGHT = "SWINGING_RIGHT_TYPE";
    private static final String EVENT_HEAD_UP = "HEAD_UP_TYPE";
    private static final String EVENT_HEAD_DOWN = "HEAD_DOWN_TYPE";
    private static final String DSM_WARN_FRAME = "DsmWarnFrame";
    private static final String DSM_ABORM_INTER_FRAME = "DsmAbormInterFrame";
    private static final int ABORM_INTERVAL_TIME_FRAME = 24;
    private static final int WARN_TIME_FRAME = 24;
    private static final int CUM_TIME = 20;
    private static final int INTERVAL_NEXT_TIME = 30;

    private Switch vDMS, vIdentity, vSmoke, vCall, vYawn, /*vNoAlignment,*/ vCameraShield, vAbsentDriver, vEyesMasked, vMouthMasked, vEyesClosing, vSwingingLeft, vSwingingRight, vHeadUp, vHeadDown;

    private Button cancel_button, ok_button, identity_button;

    private EditText dms_open_speed_edit, eIdentityCheckInterval, eSmokeCumTime, eSmokeIntervalNextTime, eCallCumTime, eCallIntervalNextTime,
                        eYawnCumTime, eYawnIntervalNextTime, /*eSeatCumTime, eSeatIntervalNextTime,*/
                        eCameraCumTime, eCameraIntervalNextTime, eAbnormalDriverCumTime, eAbnormalDriverIntervalNextTime,
                        eEyesMaskedCumTime, eEyesMaskedIntervalNextTime, eMouthMaskedCumTime, eMouthMaskedIntervalNextTime,
                        eEyesCloseCumTime, eEyesCloseIntervalNextTime, eLeftCumTime, eLeftIntervalNextTime,
                        eRightCumTime, eRightIntervalNextTime, eUpCumTime, eUpIntervalNextTime, eDownCumTime, eDownIntervalNextTime, sim_speed_edit, second_warn_open_speed_edit;

    private Spinner speed_mode_spinner;


    public DMSConfigDialog(Context context, int width, int height, Handler handler) {
        super(context);
        pParams = GUtilMain.getPreviewParams();
        mContext = context;
        mHandler = handler;
        this.width = width;
        this.height = height;
//        mPath = SystemAlg.getDMSConfigFilePath();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setCancelable(false);
        setContentView(R.layout.dms_config);

        vDMS = findViewById(R.id.dms_switch);
        vSmoke = findViewById(R.id.smoke_switch);
        vIdentity = findViewById(R.id.identity_switch);
        vCall = findViewById(R.id.call_switch);
        vYawn = findViewById(R.id.yawn_switch);
//        vNoAlignment = findViewById(R.id.no_alignment_switch);
        vCameraShield = findViewById(R.id.camera_shield_switch);
        vAbsentDriver = findViewById(R.id.absent_driver_switch);
        vEyesMasked = findViewById(R.id.eyes_masked_switch);
        vMouthMasked = findViewById(R.id.mouth_masked_switch);
        vEyesClosing = findViewById(R.id.eye_closing_switch);
        vSwingingLeft = findViewById(R.id.swinging_left_switch);
        vSwingingRight = findViewById(R.id.swinging_right_switch);
        vHeadUp = findViewById(R.id.head_up_switch);
        vHeadDown = findViewById(R.id.head_down_switch);

        dms_open_speed_edit = findViewById(R.id.dms_open_speed);
        eIdentityCheckInterval = findViewById(R.id.dms_identity_interval);
        eSmokeCumTime = findViewById(R.id.dms_smoke_cum_time);
        eSmokeIntervalNextTime = findViewById(R.id.dms_smoke_interval_next_time);
        eCallCumTime = findViewById(R.id.dms_call_cum_time);
        eCallIntervalNextTime = findViewById(R.id.dms_call_interval_next_time);
        eYawnCumTime = findViewById(R.id.dms_yawn_cum_time);
        eYawnIntervalNextTime = findViewById(R.id.dms_yawn_interval_next_time);
//        eSeatCumTime = findViewById(R.id.dms_seat_cum_time);
//        eSeatIntervalNextTime = findViewById(R.id.dms_seat_interval_next_time);
        eCameraCumTime = findViewById(R.id.dms_camera_cum_time);
        eCameraIntervalNextTime = findViewById(R.id.dms_camera_interval_next_time);
        eAbnormalDriverCumTime = findViewById(R.id.dms_abnormal_driver_cum_time);
        eAbnormalDriverIntervalNextTime = findViewById(R.id.dms_abnormal_driver_interval_next_time);
        eEyesMaskedCumTime = findViewById(R.id.dms_eyes_masked_cum_time);
        eEyesMaskedIntervalNextTime = findViewById(R.id.dms_eyes_masked_interval_next_time);
        eMouthMaskedCumTime = findViewById(R.id.dms_mouth_masked_cum_time);
        eMouthMaskedIntervalNextTime = findViewById(R.id.dms_mouth_masked_interval_next_time);
        eEyesCloseCumTime = findViewById(R.id.dms_eyes_close_cum_time);
        eEyesCloseIntervalNextTime = findViewById(R.id.dms_eyes_close_interval_next_time);
        eLeftCumTime = findViewById(R.id.dms_left_cum_time);
        eLeftIntervalNextTime = findViewById(R.id.dms_left_interval_next_time);
        eRightCumTime = findViewById(R.id.dms_right_cum_time);
        eRightIntervalNextTime = findViewById(R.id.dms_right_interval_next_time);
        eUpCumTime = findViewById(R.id.dms_up_cum_time);
        eUpIntervalNextTime = findViewById(R.id.dms_up_interval_next_time);
        eDownCumTime = findViewById(R.id.dms_down_cum_time);
        eDownIntervalNextTime = findViewById(R.id.dms_down_interval_next_time);
        sim_speed_edit = findViewById(R.id.sim_speed);
        second_warn_open_speed_edit = findViewById(R.id.second_warn_open_speed);

        cancel_button = findViewById(R.id.cancel);
        ok_button = findViewById(R.id.ok);
        identity_button = findViewById(R.id.identity_entry);

        cancel_button.setOnClickListener(new ButtonClickEvent());
        ok_button.setOnClickListener(new ButtonClickEvent());
        identity_button.setOnClickListener(new ButtonClickEvent());

        speed_mode_spinner = findViewById(R.id.spinner0);
        speed_mode_spinner.setSelection(pParams.getSpeedMode());
        speed_mode_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sim_speed_edit.setEnabled(position==1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sim_speed_edit.setText(pParams.getSimulationSpeed()+"");
        sim_speed_edit.setEnabled(pParams.getSpeedMode()==1);

        second_warn_open_speed_edit.setText(pParams.getSecondWarnOpenSpeed()+"");

        vDMS.setChecked(pParams.getDMSEnable());
        vDMS.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vSmoke.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vIdentity.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vCall.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vYawn.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
//        vNoAlignment.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vCameraShield.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vAbsentDriver.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vEyesMasked.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vMouthMasked.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vEyesClosing.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vSwingingLeft.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vSwingingRight.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vHeadUp.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        vHeadDown.setOnCheckedChangeListener(new SwitchCheckChangeEvent());

        if (pParams.getDMSOpenSpeed() < 0){
//            pParams.setDMSOpenSpeed(SystemAlg.getDmsVel());
        }
        dms_open_speed_edit.setText(pParams.getDMSOpenSpeed()+"");
//        int ret = SystemAlg.setDmsVel(pParams.getDMSOpenSpeed());
//        if (ret != 0){
//            Toast.makeText(mContext, mContext.getString(R.string.dms_open_speed_failed), Toast.LENGTH_SHORT).show();
//        }
//        System.out.println("zyz --> MvSetDsmVel ret = "+ret);

        vIdentity.setChecked(pParams.getIdentityEnable());
        vIdentity.setEnabled(!pParams.getDMSEnable());
        identity_button.setEnabled(!vIdentity.isChecked());
        eIdentityCheckInterval.setText(pParams.getIdentityCheckInterval()+"");
        eIdentityCheckInterval.setEnabled(!pParams.getDMSEnable() && !vIdentity.isChecked());
        try {
//            String value = ConfigMgrUtils.readCfgValue(mPath, EVENT_SMOKE, "null");
//            System.out.println("zyz --> value --> "+value);
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vSmoke.setChecked(value.equals("1"));
//                vSmoke.setEnabled(!pParams.getDMSEnable());
//                eSmokeCumTime.setEnabled(!pParams.getDMSEnable() && !vSmoke.isChecked());
//                eSmokeIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vSmoke.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_SMOKE, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eSmokeCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_SMOKE, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eSmokeIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//
//            //EVENT_CALL
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_CALL, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vCall.setChecked(value.equals("1"));
//                vCall.setEnabled(!pParams.getDMSEnable());
//                eCallCumTime.setEnabled(!pParams.getDMSEnable() && !vCall.isChecked());
//                eCallIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vCall.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_CALL, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eCallCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_CALL, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eCallIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//
//            //EVENT_YAWN
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_YAWN, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vYawn.setChecked(value.equals("1"));
//                vYawn.setEnabled(!pParams.getDMSEnable());
//                eYawnCumTime.setEnabled(!pParams.getDMSEnable() && !vYawn.isChecked());
//                eYawnIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vYawn.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_YAWN, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eYawnCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_YAWN, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eYawnIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }

            //EVENT_NO_ALIGNMENT
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_NO_ALIGNMENT, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vNoAlignment.setChecked(value.equals("1"));
//                vNoAlignment.setEnabled(!pParams.getDMSEnable());
//                eSeatCumTime.setEnabled(!pParams.getDMSEnable() && !vNoAlignment.isChecked());
//                eSeatIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vNoAlignment.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_NO_ALIGNMENT, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eSeatCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_NO_ALIGNMENT, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eSeatIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }

            //EVENT_CAMERA_SHIELD
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_CAMERA_SHIELD, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vCameraShield.setChecked(value.equals("1"));
//                vCameraShield.setEnabled(!pParams.getDMSEnable());
//                eCameraCumTime.setEnabled(!pParams.getDMSEnable() && !vCameraShield.isChecked());
//                eCameraIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vCameraShield.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_CAMERA_SHIELD, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eCameraCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_CAMERA_SHIELD, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eCameraIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//
//            //EVENT_ABSENT_DRIVER
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_ABSENT_DRIVER, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vAbsentDriver.setChecked(value.equals("1"));
//                vAbsentDriver.setEnabled(!pParams.getDMSEnable());
//                eAbnormalDriverCumTime.setEnabled(!pParams.getDMSEnable() && !vAbsentDriver.isChecked());
//                eAbnormalDriverIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vAbsentDriver.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_ABSENT_DRIVER, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eAbnormalDriverCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_ABSENT_DRIVER, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eAbnormalDriverIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//
//            //EVENT_EYES_MASKED
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_EYES_MASKED, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vEyesMasked.setChecked(value.equals("1"));
//                vEyesMasked.setEnabled(!pParams.getDMSEnable());
//                eEyesMaskedCumTime.setEnabled(!pParams.getDMSEnable() && !vEyesMasked.isChecked());
//                eEyesMaskedIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vEyesMasked.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_EYES_MASKED, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eEyesMaskedCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_EYES_MASKED, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eEyesMaskedIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//
//            //EVENT_MOUTH_MASKED
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_MOUTH_MASKED, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vMouthMasked.setChecked(value.equals("1"));
//                vMouthMasked.setEnabled(!pParams.getDMSEnable());
//                eMouthMaskedCumTime.setEnabled(!pParams.getDMSEnable() && !vMouthMasked.isChecked());
//                eMouthMaskedIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vMouthMasked.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_MOUTH_MASKED, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eMouthMaskedCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_MOUTH_MASKED, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eMouthMaskedIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//
//            //EVENT_EYE_CLOSING
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_EYE_CLOSING, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vEyesClosing.setChecked(value.equals("1"));
//                vEyesClosing.setEnabled(!pParams.getDMSEnable());
//                eEyesCloseCumTime.setEnabled(!pParams.getDMSEnable() && !vEyesClosing.isChecked());
//                eEyesCloseIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vEyesClosing.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_EYE_CLOSING, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eEyesCloseCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_EYE_CLOSING, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eEyesCloseIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//
//            //EVENT_SWINGING_LEFT
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_SWINGING_LEFT, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vSwingingLeft.setChecked(value.equals("1"));
//                vSwingingLeft.setEnabled(!pParams.getDMSEnable());
//                eLeftCumTime.setEnabled(!pParams.getDMSEnable() && !vSwingingLeft.isChecked());
//                eLeftIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vSwingingLeft.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_SWINGING_LEFT, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eLeftCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_SWINGING_LEFT, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eLeftIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//
//            //EVENT_SWINGING_RIGHT
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_SWINGING_RIGHT, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vSwingingRight.setChecked(value.equals("1"));
//                vSwingingRight.setEnabled(!pParams.getDMSEnable());
//                eRightCumTime.setEnabled(!pParams.getDMSEnable() && !vSwingingRight.isChecked());
//                eRightIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vSwingingRight.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_SWINGING_RIGHT, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eRightCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_SWINGING_RIGHT, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eRightIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//
//            //EVENT_HEAD_UP
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_HEAD_UP, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vHeadUp.setChecked(value.equals("1"));
//                vHeadUp.setEnabled(!pParams.getDMSEnable());
//                eUpCumTime.setEnabled(!pParams.getDMSEnable() && !vHeadUp.isChecked());
//                eUpIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vHeadUp.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_HEAD_UP, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eUpCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_HEAD_UP, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eUpIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//
//            //EVENT_HEAD_DOWN
//            value = ConfigMgrUtils.readCfgValue(mPath, EVENT_HEAD_DOWN, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                vHeadDown.setChecked(value.equals("1"));
//                vHeadDown.setEnabled(!pParams.getDMSEnable());
//                eDownCumTime.setEnabled(!pParams.getDMSEnable() && !vHeadDown.isChecked());
//                eDownIntervalNextTime.setEnabled(!pParams.getDMSEnable() && !vHeadDown.isChecked());
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_WARN_FRAME, EVENT_HEAD_DOWN, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/WARN_TIME_FRAME;
//                eDownCumTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
//            value = ConfigMgrUtils.readCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_HEAD_DOWN, "null");
//            if ((!value.equals("null"))&& (!value.equals(""))) {
//                float sec = Float.parseFloat(value)/ABORM_INTERVAL_TIME_FRAME;
//                eDownIntervalNextTime.setText(sec+"");
//            }else {
//                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
//                this.dismiss();
//            }
        } catch (Exception e) {
            System.out.println("zyz --> "+e);
            e.printStackTrace();
        }
    }

    public void setDMSChecked(boolean enable){
        vDMS.setChecked(enable);
    }

    public void updateGpsModeState(){
        speed_mode_spinner.setSelection(pParams.getSpeedMode());
        sim_speed_edit.setEnabled(pParams.getSpeedMode()==1);
        sim_speed_edit.setText(pParams.getSimulationSpeed()+"");
        second_warn_open_speed_edit.setText(pParams.getSecondWarnOpenSpeed()+"");
    }

    private class SwitchCheckChangeEvent implements Switch.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == vDMS){
//                pParams.setDMSEnable(isChecked);
                vIdentity.setEnabled(!isChecked);
                identity_button.setEnabled(!vIdentity.isChecked() && !isChecked);
                eIdentityCheckInterval.setEnabled(!vIdentity.isChecked() && !isChecked);
                vSmoke.setEnabled(!isChecked);
                eSmokeCumTime.setEnabled(!vSmoke.isChecked() && !isChecked);
                eSmokeIntervalNextTime.setEnabled(!vSmoke.isChecked() && !isChecked);
                vCall.setEnabled(!isChecked);
                eCallCumTime.setEnabled(!vCall.isChecked() && !isChecked);
                eCallIntervalNextTime.setEnabled(!vCall.isChecked() && !isChecked);
                vYawn.setEnabled(!isChecked);
                eYawnCumTime.setEnabled(!vYawn.isChecked() && !isChecked);
                eYawnIntervalNextTime.setEnabled(!vYawn.isChecked() && !isChecked);
//                vNoAlignment.setEnabled(!isChecked);
//                eSeatCumTime.setEnabled(!vNoAlignment.isChecked() && !isChecked);
//                eSeatIntervalNextTime.setEnabled(!vNoAlignment.isChecked() && !isChecked);
                vCameraShield.setEnabled(!isChecked);
                eCameraCumTime.setEnabled(!vCameraShield.isChecked() && !isChecked);
                eCameraIntervalNextTime.setEnabled(!vCameraShield.isChecked() && !isChecked);
                vAbsentDriver.setEnabled(!isChecked);
                eAbnormalDriverCumTime.setEnabled(!vAbsentDriver.isChecked() && !isChecked);
                eAbnormalDriverIntervalNextTime.setEnabled(!vAbsentDriver.isChecked() && !isChecked);
                vEyesMasked.setEnabled(!isChecked);
                eEyesMaskedCumTime.setEnabled(!vEyesMasked.isChecked() && !isChecked);
                eEyesMaskedIntervalNextTime.setEnabled(!vEyesMasked.isChecked() && !isChecked);
                vMouthMasked.setEnabled(!isChecked);
                eMouthMaskedCumTime.setEnabled(!vMouthMasked.isChecked() && !isChecked);
                eMouthMaskedIntervalNextTime.setEnabled(!vMouthMasked.isChecked() && !isChecked);
                vEyesClosing.setEnabled(!isChecked);
                eEyesCloseCumTime.setEnabled(!vEyesClosing.isChecked() && !isChecked);
                eEyesCloseIntervalNextTime.setEnabled(!vEyesClosing.isChecked() && !isChecked);
                vSwingingLeft.setEnabled(!isChecked);
                eLeftCumTime.setEnabled(!vSwingingLeft.isChecked() && !isChecked);
                eLeftIntervalNextTime.setEnabled(!vSwingingLeft.isChecked() && !isChecked);
                vSwingingRight.setEnabled(!isChecked);
                eRightCumTime.setEnabled(!vSwingingRight.isChecked() && !isChecked);
                eRightIntervalNextTime.setEnabled(!vSwingingRight.isChecked() && !isChecked);
                vHeadUp.setEnabled(!isChecked);
                eUpCumTime.setEnabled(!vHeadUp.isChecked() && !isChecked);
                eUpIntervalNextTime.setEnabled(!vHeadUp.isChecked() && !isChecked);
                vHeadDown.setEnabled(!isChecked);
                eDownCumTime.setEnabled(!vHeadDown.isChecked() && !isChecked);
                eDownIntervalNextTime.setEnabled(!vHeadDown.isChecked() && !isChecked);
            }else if (buttonView == vIdentity){
                identity_button.setEnabled(!isChecked);
                eIdentityCheckInterval.setEnabled(!isChecked);
            }else if (buttonView == vSmoke){
                eSmokeCumTime.setEnabled(!isChecked);
                eSmokeIntervalNextTime.setEnabled(!isChecked);
            }else if (buttonView == vCall){
                eCallCumTime.setEnabled(!isChecked);
                eCallIntervalNextTime.setEnabled(!isChecked);
            }else if (buttonView == vYawn){
                eYawnCumTime.setEnabled(!isChecked);
                eYawnIntervalNextTime.setEnabled(!isChecked);
            }
//            else if (buttonView == vNoAlignment){
//                eSeatCumTime.setEnabled(!isChecked);
//                eSeatIntervalNextTime.setEnabled(!isChecked);
//            }
            else if (buttonView == vCameraShield){
                eCameraCumTime.setEnabled(!isChecked);
                eCameraIntervalNextTime.setEnabled(!isChecked);
            }else if (buttonView == vAbsentDriver){
                eAbnormalDriverCumTime.setEnabled(!isChecked);
                eAbnormalDriverIntervalNextTime.setEnabled(!isChecked);
            }else if (buttonView == vEyesMasked){
                eEyesMaskedCumTime.setEnabled(!isChecked);
                eEyesMaskedIntervalNextTime.setEnabled(!isChecked);
            }else if (buttonView == vMouthMasked){
                eMouthMaskedCumTime.setEnabled(!isChecked);
                eMouthMaskedIntervalNextTime.setEnabled(!isChecked);
            }else if (buttonView == vEyesClosing){
                eEyesCloseCumTime.setEnabled(!isChecked);
                eEyesCloseIntervalNextTime.setEnabled(!isChecked);
            }else if (buttonView == vSwingingLeft){
                eLeftCumTime.setEnabled(!isChecked);
                eLeftIntervalNextTime.setEnabled(!isChecked);
            }else if (buttonView == vSwingingRight){
                eRightCumTime.setEnabled(!isChecked);
                eRightIntervalNextTime.setEnabled(!isChecked);
            }else if (buttonView == vHeadUp){
                eUpCumTime.setEnabled(!isChecked);
                eUpIntervalNextTime.setEnabled(!isChecked);
            }else if (buttonView == vHeadDown){
                eDownCumTime.setEnabled(!isChecked);
                eDownIntervalNextTime.setEnabled(!isChecked);
            }
        }
    }

    private IdentityEntryDialog mIdentityEntryDialog = null;
    private class ButtonClickEvent implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (v == ok_button){
                if (vIdentity.isChecked()) {
                    if (!pParams.getDMSIsFaceEntry()) {
                        Toast.makeText(mContext, mContext.getString(R.string.dms_face_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (eIdentityCheckInterval.getText().toString().equals("") || Integer.parseInt(eIdentityCheckInterval.getText().toString())<0 || Integer.parseInt(eIdentityCheckInterval.getText().toString())>600){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_identity_check_interval_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dms_open_speed_edit.getText().toString().equals("") || Float.parseFloat(dms_open_speed_edit.getText().toString())<0 || Float.parseFloat(dms_open_speed_edit.getText().toString())>200){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_open_speed_out), Toast.LENGTH_SHORT).show();
                    return;
                }
//                int ret = SystemAlg.setDmsVel(Float.parseFloat(dms_open_speed_edit.getText().toString()));
//                if (ret != 0){
//                    Toast.makeText(mContext, mContext.getString(R.string.dms_open_speed_failed), Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if (eSmokeCumTime.getText().toString().equals("") || Float.parseFloat(eSmokeCumTime.getText().toString())<0 || Float.parseFloat(eSmokeCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eSmokeIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eSmokeIntervalNextTime.getText().toString())<0 || Float.parseFloat(eSmokeIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eCallCumTime.getText().toString().equals("") || Float.parseFloat(eCallCumTime.getText().toString())<0 || Float.parseFloat(eCallCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eCallIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eCallIntervalNextTime.getText().toString())<0 || Float.parseFloat(eCallIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eYawnCumTime.getText().toString().equals("") || Float.parseFloat(eYawnCumTime.getText().toString())<0 || Float.parseFloat(eYawnCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eYawnIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eYawnIntervalNextTime.getText().toString())<0 || Float.parseFloat(eYawnIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (eSeatCumTime.getText().toString().equals("") || Float.parseFloat(eSeatCumTime.getText().toString())<0 || Float.parseFloat(eSeatCumTime.getText().toString())>CUM_TIME){
//                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (eSeatIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eSeatIntervalNextTime.getText().toString())<0 || Float.parseFloat(eSeatIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
//                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if (eCameraCumTime.getText().toString().equals("") || Float.parseFloat(eCameraCumTime.getText().toString())<0 || Float.parseFloat(eCameraCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eCameraIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eCameraIntervalNextTime.getText().toString())<0 || Float.parseFloat(eCameraIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eAbnormalDriverCumTime.getText().toString().equals("") || Float.parseFloat(eAbnormalDriverCumTime.getText().toString())<0 || Float.parseFloat(eAbnormalDriverCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eAbnormalDriverIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eAbnormalDriverIntervalNextTime.getText().toString())<0 || Float.parseFloat(eAbnormalDriverIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eEyesMaskedCumTime.getText().toString().equals("") || Float.parseFloat(eEyesMaskedCumTime.getText().toString())<0 || Float.parseFloat(eEyesMaskedCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eEyesMaskedIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eEyesMaskedIntervalNextTime.getText().toString())<0 || Float.parseFloat(eEyesMaskedIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eMouthMaskedCumTime.getText().toString().equals("") || Float.parseFloat(eMouthMaskedCumTime.getText().toString())<0 || Float.parseFloat(eMouthMaskedCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eMouthMaskedIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eMouthMaskedIntervalNextTime.getText().toString())<0 || Float.parseFloat(eMouthMaskedIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eEyesCloseCumTime.getText().toString().equals("") || Float.parseFloat(eEyesCloseCumTime.getText().toString())<0 || Float.parseFloat(eEyesCloseCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eEyesCloseIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eEyesCloseIntervalNextTime.getText().toString())<0 || Float.parseFloat(eEyesCloseIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eLeftCumTime.getText().toString().equals("") || Float.parseFloat(eLeftCumTime.getText().toString())<0 || Float.parseFloat(eLeftCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eLeftIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eLeftIntervalNextTime.getText().toString())<0 || Float.parseFloat(eLeftIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eRightCumTime.getText().toString().equals("") || Float.parseFloat(eRightCumTime.getText().toString())<0 || Float.parseFloat(eRightCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eRightIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eRightIntervalNextTime.getText().toString())<0 || Float.parseFloat(eRightIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eUpCumTime.getText().toString().equals("") || Float.parseFloat(eUpCumTime.getText().toString())<0 || Float.parseFloat(eUpCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eUpIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eUpIntervalNextTime.getText().toString())<0 || Float.parseFloat(eUpIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eDownCumTime.getText().toString().equals("") || Float.parseFloat(eDownCumTime.getText().toString())<0 || Float.parseFloat(eDownCumTime.getText().toString())>CUM_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_cum_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eDownIntervalNextTime.getText().toString().equals("") || Float.parseFloat(eDownIntervalNextTime.getText().toString())<0 || Float.parseFloat(eDownIntervalNextTime.getText().toString())>INTERVAL_NEXT_TIME){
                    Toast.makeText(mContext, mContext.getString(R.string.dms_interval_next_time_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                //SPEED
                String value = second_warn_open_speed_edit.getText().toString();
                if (value.equals("") || Integer.parseInt(value)<0 || Integer.parseInt(value)>250){
                    Toast.makeText(mContext, mContext.getString(R.string.second_warn_open_speed_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                value = sim_speed_edit.getText().toString();
                if (!value.isEmpty()){
                    int speed = Integer.parseInt(value);
                    if (speed<0 || speed>200){
                        Toast.makeText(mContext, mContext.getString(R.string.sim_speed_out), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                try {
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_SMOKE, vSmoke.isChecked()?"1":"0");
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_CALL, vCall.isChecked()?"1":"0");
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_YAWN, vYawn.isChecked()?"1":"0");
////                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_NO_ALIGNMENT, vNoAlignment.isChecked()?"1":"0");
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_CAMERA_SHIELD, vCameraShield.isChecked()?"1":"0");
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_ABSENT_DRIVER, vAbsentDriver.isChecked()?"1":"0");
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_EYES_MASKED, vEyesMasked.isChecked()?"1":"0");
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_MOUTH_MASKED, vMouthMasked.isChecked()?"1":"0");
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_EYE_CLOSING, vEyesClosing.isChecked()?"1":"0");
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_SWINGING_LEFT, vSwingingLeft.isChecked()?"1":"0");
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_SWINGING_RIGHT, vSwingingRight.isChecked()?"1":"0");
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_HEAD_UP, vHeadUp.isChecked()?"1":"0");
//                    ConfigMgrUtils.writeCfgValue(mPath, EVENT_HEAD_DOWN, vHeadDown.isChecked()?"1":"0");
//
//                    int frame = (int)(Float.parseFloat(eSmokeCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_SMOKE, frame+"");
//                    frame = (int)(Float.parseFloat(eSmokeIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_SMOKE, frame+"");
//
//                    frame = (int)(Float.parseFloat(eCallCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_CALL, frame+"");
//                    frame = (int)(Float.parseFloat(eCallIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_CALL, frame+"");
//
//                    frame = (int)(Float.parseFloat(eYawnCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_YAWN, frame+"");
//                    frame = (int)(Float.parseFloat(eYawnIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_YAWN, frame+"");
//
////                    frame = (int)(Float.parseFloat(eSeatCumTime.getText().toString())*WARN_TIME_FRAME);
////                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_NO_ALIGNMENT, frame+"");
////                    frame = (int)(Float.parseFloat(eSeatIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
////                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_NO_ALIGNMENT, frame+"");
//
//                    frame = (int)(Float.parseFloat(eCameraCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_CAMERA_SHIELD, frame+"");
//                    frame = (int)(Float.parseFloat(eCameraIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_CAMERA_SHIELD, frame+"");
//
//                    frame = (int)(Float.parseFloat(eAbnormalDriverCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_ABSENT_DRIVER, frame+"");
//                    frame = (int)(Float.parseFloat(eAbnormalDriverIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_ABSENT_DRIVER, frame+"");
//
//                    frame = (int)(Float.parseFloat(eEyesMaskedCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_EYES_MASKED, frame+"");
//                    frame = (int)(Float.parseFloat(eEyesMaskedIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_EYES_MASKED, frame+"");
//
//                    frame = (int)(Float.parseFloat(eMouthMaskedCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_MOUTH_MASKED, frame+"");
//                    frame = (int)(Float.parseFloat(eMouthMaskedIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_MOUTH_MASKED, frame+"");
//
//                    frame = (int)(Float.parseFloat(eEyesCloseCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_EYE_CLOSING, frame+"");
//                    frame = (int)(Float.parseFloat(eEyesCloseIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_EYE_CLOSING, frame+"");
//
//                    frame = (int)(Float.parseFloat(eLeftCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_SWINGING_LEFT, frame+"");
//                    frame = (int)(Float.parseFloat(eLeftIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_SWINGING_LEFT, frame+"");
//
//                    frame = (int)(Float.parseFloat(eRightCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_SWINGING_RIGHT, frame+"");
//                    frame = (int)(Float.parseFloat(eRightIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_SWINGING_RIGHT, frame+"");
//
//                    frame = (int)(Float.parseFloat(eUpCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_HEAD_UP, frame+"");
//                    frame = (int)(Float.parseFloat(eUpIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_HEAD_UP, frame+"");
//
//                    frame = (int)(Float.parseFloat(eDownCumTime.getText().toString())*WARN_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_WARN_FRAME, EVENT_HEAD_DOWN, frame+"");
//                    frame = (int)(Float.parseFloat(eDownIntervalNextTime.getText().toString())*ABORM_INTERVAL_TIME_FRAME);
//                    ConfigMgrUtils.writeCfgValue(mPath, DSM_ABORM_INTER_FRAME, EVENT_HEAD_DOWN, frame+"");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, mContext.getString(R.string.write_conf_err), Toast.LENGTH_SHORT).show();
                    return;
                }

                pParams.setSecondWarnOpenSpeed(Integer.parseInt(second_warn_open_speed_edit.getText().toString()));
                pParams.setSimulationSpeed(Integer.parseInt(sim_speed_edit.getText().toString()));
                pParams.setSpeedMode(speed_mode_spinner.getSelectedItemPosition());
                Message msg1 = mHandler.obtainMessage();
                msg1.what = 19;
                mHandler.sendMessage(msg1);

                pParams.setDMSOpenSpeed(Float.parseFloat(dms_open_speed_edit.getText().toString()));
                pParams.setDMSEnable(vDMS.isChecked());
                pParams.setIdentityEnable(vIdentity.isChecked());
                pParams.setIdentityCheckInterval(Integer.parseInt(eIdentityCheckInterval.getText().toString()));
                Message msg = mHandler.obtainMessage();
                msg.what = 25;
                mHandler.sendMessage(msg);
                DMSConfigDialog.this.hide();
            }else if (v == cancel_button){
                DMSConfigDialog.this.hide();
            }else if (identity_button == v){
                showIdentityDialog();
                DMSConfigDialog.this.hide();
            }
        }
    }

    public void showIdentityDialog(){
        if (mIdentityEntryDialog == null){
            mIdentityEntryDialog = new IdentityEntryDialog(mContext, 720, 720, mHandler);
        }
        mIdentityEntryDialog.show();
    }

    public void setIdentityFaceInfoLine(){
        mIdentityEntryDialog.setFaceInfo();
    }
}
