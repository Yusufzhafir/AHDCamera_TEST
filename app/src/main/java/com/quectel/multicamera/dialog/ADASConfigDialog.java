package com.quectel.multicamera.dialog;

import android.ai.SystemAlg;
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

public class ADASConfigDialog extends AlertDialog {
    private PreviewParams pParams ;

    private static String mPath, mDMSPath;

    private static final int ABORM_INTERVAL_TIME_FRAME = 24;

    private Switch adas_switch, dep_switch, gpio_switch, front_col_switch, distance_switch;
    private EditText dep_open_speed_edit, front_col_open_speed_edit, distance_open_speed_edit, sim_speed_edit, hmw_open_thres_times_edit, dep_warn_interval_times_edit, ttc_thres_times_edit, hmw_warn_interval_times_edit, fcw_warn_interval_times_edit, second_warn_open_speed_edit;
    private Button calibration_button, set_sim_speed_button, cancel_button, ok_button;
    private Spinner speed_mode_spinner;

    private Context mContext;
    private Handler mHandler;

    private static final String LDW_NAME = "LdwOn";
    private static final String FCW_NAME = "FcwOn";
    private static final String HMW_NAME = "HmwOn";
    private static final String LDW_VEL = "LdwVel";
    private static final String FCW_VEL = "FcwVel";
    private static final String HMW_VEL = "HmwVel";
    private static final String HMW_TIME = "HmwTime";
    private static final String FCW_DELAT2 = "FcwDelat2";
    private static final String HMW_TYPE = "HMW_TYPE";
    private static final String FCW_TYPE = "FCW_TYPE";
    private static final String LDW_LEFT_TYPE = "LDW_LEFT_TYPE";
    private static final String LDW_RIGHT_TYPE = "LDW_RIGHT_TYPE";
    private static final String DSM_ABORM_INTER_FRAME = "DsmAbormInterFrame";

    private int width, height;

    public ADASConfigDialog(Context context, int width, int height, Handler handler) {
        super(context);
        pParams = GUtilMain.getPreviewParams();
        mContext = context;
        mHandler = handler;
        this.width = width;
        this.height = height;
        mPath = SystemAlg.getADASWarnParaFilePath();
        mDMSPath = SystemAlg.getDMSConfigFilePath();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setCancelable(false);
        setContentView(R.layout.adas_config);

        adas_switch = findViewById(R.id.adas_switch);
        dep_switch = findViewById(R.id.dep_switch);
        gpio_switch = findViewById(R.id.gpio_switch);
        front_col_switch = findViewById(R.id.front_col_switch);
        distance_switch = findViewById(R.id.distance_switch);

        dep_open_speed_edit = findViewById(R.id.dep_open_speed);
        front_col_open_speed_edit = findViewById(R.id.front_col_open_speed);
        distance_open_speed_edit = findViewById(R.id.distance_open_speed);
        sim_speed_edit = findViewById(R.id.sim_speed);

        dep_warn_interval_times_edit = findViewById(R.id.dep_warn_interval);
        hmw_open_thres_times_edit = findViewById(R.id.hmw_open_thres_times);
        ttc_thres_times_edit = findViewById(R.id.ttc_thres_times);
        hmw_warn_interval_times_edit = findViewById(R.id.hmw_warn_interval_times);
        fcw_warn_interval_times_edit = findViewById(R.id.fcw_warn_interval_times);
        second_warn_open_speed_edit = findViewById(R.id.second_warn_open_speed);

        calibration_button = findViewById(R.id.calibration);
        set_sim_speed_button = findViewById(R.id.set_sim_speed);
        cancel_button = findViewById(R.id.cancel);
        ok_button = findViewById(R.id.ok);

        speed_mode_spinner = findViewById(R.id.spinner0);

        adas_switch.setChecked(pParams.getADASEnable());
        gpio_switch.setChecked(pParams.getGpioEnable());
        gpio_switch.setEnabled(!pParams.getADASEnable());
        second_warn_open_speed_edit.setText(pParams.getSecondWarnOpenSpeed()+"");
        try {
            String value = ConfigMgrUtils.readCfgValue(mPath, LDW_NAME, "null");
            if ((!value.equals("null"))&& (!value.equals(""))) {
                dep_switch.setChecked(value.equals("true"));
                dep_switch.setEnabled(!pParams.getADASEnable());
            }else {
                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }

            value = ConfigMgrUtils.readCfgValue(mPath, FCW_NAME, "null");
            if ((!value.equals("null"))&& (!value.equals(""))) {
                front_col_switch.setChecked(value.equals("true"));
                front_col_switch.setEnabled(!pParams.getADASEnable());
            }else {
                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }

            value = ConfigMgrUtils.readCfgValue(mPath, HMW_NAME, "null");
            if ((!value.equals("null"))&& (!value.equals(""))) {
                distance_switch.setChecked(value.equals("true"));
                distance_switch.setEnabled(!pParams.getADASEnable());
            }else {
                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }

            value = ConfigMgrUtils.readCfgValue(mPath, LDW_VEL, "null");
            if ((!value.equals("null"))&& (!value.equals(""))) {
                dep_open_speed_edit.setText(value);
                dep_open_speed_edit.setEnabled(!pParams.getADASEnable() && !dep_switch.isChecked());
            }else {
                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }

            value = ConfigMgrUtils.readCfgValue(mPath, FCW_VEL, "null");
            if ((!value.equals("null"))&& (!value.equals(""))) {
                front_col_open_speed_edit.setText(value);
                front_col_open_speed_edit.setEnabled(!pParams.getADASEnable() && !front_col_switch.isChecked());
            }else {
                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }

            value = ConfigMgrUtils.readCfgValue(mPath, HMW_VEL, "null");
            if ((!value.equals("null"))&& (!value.equals(""))) {
                distance_open_speed_edit.setText(value);
                distance_open_speed_edit.setEnabled(!pParams.getADASEnable() && !distance_switch.isChecked());
            }else {
                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }

            value = ConfigMgrUtils.readCfgValue(mPath, HMW_TIME, "null");
//            System.out.println("zyz --> "+HMW_TIME+" --> "+value);
            if ((!value.equals("null"))&& (!value.equals(""))) {
                hmw_open_thres_times_edit.setText(value);
                hmw_open_thres_times_edit.setEnabled(!pParams.getADASEnable());
            }else {
                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }

            value = ConfigMgrUtils.readCfgValue(mPath, FCW_DELAT2, "null");
//            System.out.println("zyz --> "+FCW_DELAT2+" --> "+value);
            if ((!value.equals("null"))&& (!value.equals(""))) {
                ttc_thres_times_edit.setText(value);
                ttc_thres_times_edit.setEnabled(!pParams.getADASEnable());
            }else {
                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }

            value = ConfigMgrUtils.readCfgValue(mDMSPath, DSM_ABORM_INTER_FRAME, HMW_TYPE, "null");
//            System.out.println("zyz --> "+HMW_TYPE+" --> "+value);
            if ((!value.equals("null"))&& (!value.equals(""))) {
                int sec = Integer.parseInt(value);
                hmw_warn_interval_times_edit.setText((sec/ABORM_INTERVAL_TIME_FRAME)+"");
                hmw_warn_interval_times_edit.setEnabled(!pParams.getADASEnable());
            }else {
                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }

            value = ConfigMgrUtils.readCfgValue(mDMSPath, DSM_ABORM_INTER_FRAME, FCW_TYPE, "null");
//            System.out.println("zyz --> "+FCW_TYPE+" --> "+value);
            if ((!value.equals("null"))&& (!value.equals(""))) {
                int sec = Integer.parseInt(value);
                fcw_warn_interval_times_edit.setText((sec/ABORM_INTERVAL_TIME_FRAME)+"");
                fcw_warn_interval_times_edit.setEnabled(!pParams.getADASEnable());
            }else {
                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }

            value = ConfigMgrUtils.readCfgValue(mDMSPath, DSM_ABORM_INTER_FRAME, LDW_LEFT_TYPE, "null");
//            System.out.println("zyz --> "+FCW_TYPE+" --> "+value);
            if ((!value.equals("null"))&& (!value.equals(""))) {
                int sec = Integer.parseInt(value);
                dep_warn_interval_times_edit.setText((sec/ABORM_INTERVAL_TIME_FRAME)+"");
                dep_warn_interval_times_edit.setEnabled(!pParams.getADASEnable() && !dep_switch.isChecked());
            }else {
                Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }

//            System.out.println("zyz1 --> LdwOn value --> "+value);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, mContext.getString(R.string.read_conf_err), Toast.LENGTH_SHORT).show();
            this.dismiss();
        }
        sim_speed_edit.setText(pParams.getSimulationSpeed()+"");
        sim_speed_edit.setEnabled(pParams.getSpeedMode()==1);
        set_sim_speed_button.setEnabled(pParams.getSpeedMode()==1);

        adas_switch.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        dep_switch.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        front_col_switch.setOnCheckedChangeListener(new SwitchCheckChangeEvent());
        distance_switch.setOnCheckedChangeListener(new SwitchCheckChangeEvent());

        set_sim_speed_button.setOnClickListener(new ButtonClickEvent());
        cancel_button.setOnClickListener(new ButtonClickEvent());
        ok_button.setOnClickListener(new ButtonClickEvent());
        calibration_button.setOnClickListener(new ButtonClickEvent());

        calibration_button.setEnabled(!pParams.getADASEnable());

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
    }

    public void setADASChecked(boolean enable){
        adas_switch.setChecked(enable);
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
            if (buttonView == adas_switch){
                calibration_button.setEnabled(!isChecked);
                dep_switch.setEnabled(!isChecked);
                gpio_switch.setEnabled(!isChecked);
                front_col_switch.setEnabled(!isChecked);
                distance_switch.setEnabled(!isChecked);

                dep_open_speed_edit.setEnabled(!isChecked && !dep_switch.isChecked());
                front_col_open_speed_edit.setEnabled(!isChecked && !front_col_switch.isChecked());
                distance_open_speed_edit.setEnabled(!isChecked && !distance_switch.isChecked());

                hmw_open_thres_times_edit.setEnabled(!isChecked);
                ttc_thres_times_edit.setEnabled(!isChecked);
                hmw_warn_interval_times_edit.setEnabled(!isChecked);
                fcw_warn_interval_times_edit.setEnabled(!isChecked);
                dep_warn_interval_times_edit.setEnabled(!isChecked && !dep_switch.isChecked());
            }else if (buttonView == dep_switch){
                dep_open_speed_edit.setEnabled(!isChecked);
                dep_warn_interval_times_edit.setEnabled(!isChecked);
            }else if (buttonView == front_col_switch){
                front_col_open_speed_edit.setEnabled(!isChecked);
            }else if (buttonView == distance_switch){
                distance_open_speed_edit.setEnabled(!isChecked);
            }
        }
    }

    private class ButtonClickEvent implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (v == ok_button){
                if (dep_open_speed_edit.getText().toString().equals("") || Integer.parseInt(dep_open_speed_edit.getText().toString())<0 || Integer.parseInt(dep_open_speed_edit.getText().toString())>200){
                    Toast.makeText(mContext, mContext.getString(R.string.ldw_speed_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (front_col_open_speed_edit.getText().toString().equals("") || Integer.parseInt(front_col_open_speed_edit.getText().toString())<0 || Integer.parseInt(front_col_open_speed_edit.getText().toString())>200){
                    Toast.makeText(mContext, mContext.getString(R.string.fcw_speed_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (distance_open_speed_edit.getText().toString().equals("") || Integer.parseInt(distance_open_speed_edit.getText().toString())<0 || Integer.parseInt(distance_open_speed_edit.getText().toString())>200){
                    Toast.makeText(mContext, mContext.getString(R.string.hmw_speed_out), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (hmw_open_thres_times_edit.getText().toString().equals("") || Float.parseFloat(hmw_open_thres_times_edit.getText().toString())<0 || Float.parseFloat(hmw_open_thres_times_edit.getText().toString())>4){
                    Toast.makeText(mContext, mContext.getString(R.string.hmw_open_thres_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ttc_thres_times_edit.getText().toString().equals("") || Float.parseFloat(ttc_thres_times_edit.getText().toString())<0 || Float.parseFloat(ttc_thres_times_edit.getText().toString())>5){
                    Toast.makeText(mContext, mContext.getString(R.string.ttc_thres_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (hmw_warn_interval_times_edit.getText().toString().equals("") || Integer.parseInt(hmw_warn_interval_times_edit.getText().toString())<0 || Integer.parseInt(hmw_warn_interval_times_edit.getText().toString())>20){
                    Toast.makeText(mContext, mContext.getString(R.string.hmw_warn_interval_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fcw_warn_interval_times_edit.getText().toString().equals("") || Integer.parseInt(fcw_warn_interval_times_edit.getText().toString())<0 || Integer.parseInt(fcw_warn_interval_times_edit.getText().toString())>20){
                    Toast.makeText(mContext, mContext.getString(R.string.fcw_warn_interval_out), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dep_warn_interval_times_edit.getText().toString().equals("") || Integer.parseInt(dep_warn_interval_times_edit.getText().toString())<0 || Integer.parseInt(dep_warn_interval_times_edit.getText().toString())>20){
                    Toast.makeText(mContext, mContext.getString(R.string.dep_warn_interval_out), Toast.LENGTH_SHORT).show();
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
                    ConfigMgrUtils.writeCfgValue(mPath, LDW_NAME, dep_switch.isChecked()?"true":"false");
                    ConfigMgrUtils.writeCfgValue(mPath, FCW_NAME, front_col_switch.isChecked()?"true":"false");
                    ConfigMgrUtils.writeCfgValue(mPath, HMW_NAME, distance_switch.isChecked()?"true":"false");

                    ConfigMgrUtils.writeCfgValue(mPath, LDW_VEL, dep_open_speed_edit.getText().toString());
                    ConfigMgrUtils.writeCfgValue(mPath, FCW_VEL, front_col_open_speed_edit.getText().toString());
                    ConfigMgrUtils.writeCfgValue(mPath, HMW_VEL, distance_open_speed_edit.getText().toString());

                    ConfigMgrUtils.writeCfgValue(mPath, HMW_TIME, hmw_open_thres_times_edit.getText().toString());
                    ConfigMgrUtils.writeCfgValue(mPath, FCW_DELAT2, ttc_thres_times_edit.getText().toString());

                    int sec = Integer.parseInt(hmw_warn_interval_times_edit.getText().toString());
                    ConfigMgrUtils.writeCfgValue(mDMSPath, DSM_ABORM_INTER_FRAME, HMW_TYPE, (sec*ABORM_INTERVAL_TIME_FRAME)+"");
                    sec = Integer.parseInt(fcw_warn_interval_times_edit.getText().toString());
                    ConfigMgrUtils.writeCfgValue(mDMSPath, DSM_ABORM_INTER_FRAME, FCW_TYPE, (sec*ABORM_INTERVAL_TIME_FRAME)+"");

                    sec = Integer.parseInt(dep_warn_interval_times_edit.getText().toString());
                    ConfigMgrUtils.writeCfgValue(mDMSPath, DSM_ABORM_INTER_FRAME, LDW_LEFT_TYPE, (sec*ABORM_INTERVAL_TIME_FRAME)+"");
                    ConfigMgrUtils.writeCfgValue(mDMSPath, DSM_ABORM_INTER_FRAME, LDW_RIGHT_TYPE, (sec*ABORM_INTERVAL_TIME_FRAME)+"");
                } catch (IOException e) {
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

                pParams.setADASEnable(adas_switch.isChecked());
                pParams.setGpioEnable(gpio_switch.isChecked());
                Message msg = mHandler.obtainMessage();
                msg.what = 21;
                mHandler.sendMessage(msg);

                ADASConfigDialog.this.hide();
            }else if (v == cancel_button){
                ADASConfigDialog.this.hide();
            }else if (v == calibration_button){
                Message msg = mHandler.obtainMessage();
                msg.what = 23;
                mHandler.sendMessage(msg);
                ADASConfigDialog.this.hide();
            }
        }
    }
}
