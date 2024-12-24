package com.quectel.multicamera.utils;

import android.util.Log;

import java.io.File;

public class PreviewParams {
    private int previewNum = 4;
    private int previewMaxNum = 6;
    private int csiNum = 2; //default n4(CSI0)
    private int width = 1280;
    private int width1 = 1280;
    private int width2 = 1280;
    private int width3 = 1280;
    private int width4 = 1280;
    private int width5 = 1280;
    private int width6 = 1280;
    private int height = 720;
    private int height1 = 720;
    private int height2 = 720;
    private int height3 = 720;
    private int height4 = 720;
    private int height5 = 720;
    private int height6 = 720;
    private int psCsiPosition = 0;
    private int psCsiPosition1 = 0;
    private int psCsiPosition2 = 0;
    private int psCsiPosition3 = 0;
    private int psCsiPosition4 = 0;
    private int psCsiPosition5 = 0;
    private int psCsiPosition6 = 0;

    private int cameraType1 = 0, cameraType2 = 0, cameraType3 = 0, cameraType4 = 0, cameraType5 = 0, cameraType6 = 0;

    public int getCameraType(int channel) {
        if (channel > 6 || channel < 1)
            return -1;
        switch (channel) {
            case 1:
                cameraType1 = GUtilMain.mSharedPreferences.getInt("pre_camera_type1", cameraType1);
                return cameraType1;
            case 2:
                cameraType2 = GUtilMain.mSharedPreferences.getInt("pre_camera_type2", cameraType2);
                return cameraType2;
            case 3:
                cameraType3 = GUtilMain.mSharedPreferences.getInt("pre_camera_type3", cameraType3);
                return cameraType3;
            case 4:
                cameraType4 = GUtilMain.mSharedPreferences.getInt("pre_camera_type4", cameraType4);
                return cameraType4;
            case 5:
                cameraType5 = GUtilMain.mSharedPreferences.getInt("pre_camera_type5", cameraType5);
                return cameraType5;
            case 6:
                cameraType6 = GUtilMain.mSharedPreferences.getInt("pre_camera_type6", cameraType6);
                return cameraType6;
            default:
                return -1;
        }
    }

    public void setCameraType(int type, int channel) {
        if (channel > 6 || channel < 1) {
            System.out.println("zyz --> channel value is out of range !");
            return;
        }
        switch (channel) {
            case 1:
                cameraType1 = type;
                GUtilMain.mEditor.putInt("pre_camera_type1", cameraType1);
                break;
            case 2:
                cameraType2 = type;
                GUtilMain.mEditor.putInt("pre_camera_type2", cameraType2);
                break;
            case 3:
                cameraType3 = type;
                GUtilMain.mEditor.putInt("pre_camera_type3", cameraType3);
                break;
            case 4:
                cameraType4 = type;
                GUtilMain.mEditor.putInt("pre_camera_type4", cameraType4);
                break;
            case 5:
                cameraType5 = type;
                GUtilMain.mEditor.putInt("pre_camera_type5", cameraType5);
                break;
            case 6:
                cameraType6 = type;
                GUtilMain.mEditor.putInt("pre_camera_type6", cameraType6);
                break;
        }
        GUtilMain.mEditor.commit();
    }

    private int displayStyle = 0;

    public void setDisplayStyle(int style) {
        displayStyle = style;
        GUtilMain.mEditor.putInt("pre_display_style", displayStyle);
        GUtilMain.mEditor.commit();
    }

    public int getDisplayStyle() {
        displayStyle = GUtilMain.mSharedPreferences.getInt("pre_display_style", displayStyle);
        return displayStyle;
    }

    public int getWidth(int channel) {
        if (channel > 6 || channel < 1)
            return -1;
        switch (channel) {
            case 1:
                width1 = GUtilMain.mSharedPreferences.getInt("pre_width1", width1);
                return width1;
            case 2:
                width2 = GUtilMain.mSharedPreferences.getInt("pre_width2", width2);
                return width2;
            case 3:
                width3 = GUtilMain.mSharedPreferences.getInt("pre_width3", width3);
                return width3;
            case 4:
                width4 = GUtilMain.mSharedPreferences.getInt("pre_width4", width4);
                return width4;
            case 5:
                width5 = GUtilMain.mSharedPreferences.getInt("pre_width5", width5);
                return width5;
            case 6:
                width6 = GUtilMain.mSharedPreferences.getInt("pre_width6", width6);
                return width6;
            default:
                return -1;
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight(int channel) {
        if (channel > 6 || channel < 1)
            return -1;
        switch (channel) {
            case 1:
                height1 = GUtilMain.mSharedPreferences.getInt("pre_height1", height1);
                return height1;
            case 2:
                height2 = GUtilMain.mSharedPreferences.getInt("pre_height2", height2);
                return height2;
            case 3:
                height3 = GUtilMain.mSharedPreferences.getInt("pre_height3", height3);
                return height3;
            case 4:
                height4 = GUtilMain.mSharedPreferences.getInt("pre_height4", height4);
                return height4;
            case 5:
                height5 = GUtilMain.mSharedPreferences.getInt("pre_height5", height5);
                return height5;
            case 6:
                height6 = GUtilMain.mSharedPreferences.getInt("pre_height6", height6);
                return height6;
            default:
                return -1;
        }
    }


    public void setHeight(int height) {
        this.height = height;
    }

    public int getInput2TypeNum() {
        psCsiPosition4 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position4", psCsiPosition4);
        psCsiPosition5 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position5", psCsiPosition5);
        psCsiPosition6 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position6", psCsiPosition6);
        cameraType4 = GUtilMain.mSharedPreferences.getInt("pre_camera_type4", cameraType4);
        cameraType5 = GUtilMain.mSharedPreferences.getInt("pre_camera_type5", cameraType5);
        cameraType6 = GUtilMain.mSharedPreferences.getInt("pre_camera_type6", cameraType6);
        int ch0 = psCsiPosition4 == 2 ? (cameraType4 == 0 ? 1 : 0) : (psCsiPosition4 == 0 ? (cameraType4 == 0 ? 2 : 3) : (cameraType4 == 0 ? 4 : 5));
        int ch1 = psCsiPosition5 == 2 ? (cameraType5 == 0 ? 1 : 0) : (psCsiPosition5 == 0 ? (cameraType5 == 0 ? 2 : 3) : (cameraType5 == 0 ? 4 : 5));
        int ch2 = psCsiPosition6 == 2 ? (cameraType6 == 0 ? 1 : 0) : (psCsiPosition6 == 0 ? (cameraType6 == 0 ? 2 : 3) : (cameraType6 == 0 ? 4 : 5));

//        System.out.println("zyz --> type1 = "+(216 + 36*ch0 + 6*ch1 + ch2));
        return 216 + 36 * ch0 + 6 * ch1 + ch2;
    }

    public int getInput1TypeNum() {
        psCsiPosition1 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position1", psCsiPosition1);
        psCsiPosition2 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position2", psCsiPosition2);
        psCsiPosition3 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position3", psCsiPosition3);
        cameraType1 = GUtilMain.mSharedPreferences.getInt("pre_camera_type1", cameraType1);
        cameraType2 = GUtilMain.mSharedPreferences.getInt("pre_camera_type2", cameraType2);
        cameraType3 = GUtilMain.mSharedPreferences.getInt("pre_camera_type3", cameraType3);
        int ch0 = psCsiPosition1 == 2 ? (cameraType1 == 0 ? 1 : 0) : (psCsiPosition1 == 0 ? (cameraType1 == 0 ? 2 : 3) : (cameraType1 == 0 ? 4 : 5));
        int ch1 = psCsiPosition2 == 2 ? (cameraType2 == 0 ? 1 : 0) : (psCsiPosition2 == 0 ? (cameraType2 == 0 ? 2 : 3) : (cameraType2 == 0 ? 4 : 5));
        int ch2 = psCsiPosition3 == 2 ? (cameraType3 == 0 ? 1 : 0) : (psCsiPosition3 == 0 ? (cameraType3 == 0 ? 2 : 3) : (cameraType3 == 0 ? 4 : 5));
//        System.out.println("zyz --> type0 = "+(36*ch0 + 6*ch1 + ch2));
        return 36 * ch0 + 6 * ch1 + ch2;
    }


    public void setPsPosition1(int psPosition) {
        this.psCsiPosition1 = psPosition;
        GUtilMain.mEditor.putInt("pre_ps_csi_position1", this.psCsiPosition1);
        if (psPosition == 0) {
            width1 = 1280;
            height1 = 720;
        } else if (psPosition == 1) {
            width1 = 1920;
            height1 = 1080;
        } else if (psPosition == 2 && cameraType1 == 0) {
            width1 = 720;
            height1 = 576;
        } else if (psPosition == 2 && cameraType1 == 1) {
            width1 = 720;
            height1 = 480;
        }
        GUtilMain.mEditor.putInt("pre_width1", width1);
        GUtilMain.mEditor.putInt("pre_height1", height1);
        GUtilMain.mEditor.commit();
    }

    public void setPsPosition2(int psPosition) {
        this.psCsiPosition2 = psPosition;
        GUtilMain.mEditor.putInt("pre_ps_csi_position2", this.psCsiPosition2);
        if (psPosition == 0) {
            width2 = 1280;
            height2 = 720;
        } else if (psPosition == 1) {
            width2 = 1920;
            height2 = 1080;
        } else if (psPosition == 2 && cameraType2 == 0) {
            width2 = 720;
            height2 = 576;
        } else if (psPosition == 2 && cameraType2 == 1) {
            width2 = 720;
            height2 = 480;
        }
        GUtilMain.mEditor.putInt("pre_width2", width2);
        GUtilMain.mEditor.putInt("pre_height2", height2);
        GUtilMain.mEditor.commit();
    }

    public void setPsPosition3(int psPosition) {
        this.psCsiPosition3 = psPosition;
        GUtilMain.mEditor.putInt("pre_ps_csi_position3", this.psCsiPosition3);
        if (psPosition == 0) {
            width3 = 1280;
            height3 = 720;
        } else if (psPosition == 1) {
            width3 = 1920;
            height3 = 1080;
        } else if (psPosition == 2 && cameraType3 == 0) {
            width3 = 720;
            height3 = 576;
        } else if (psPosition == 2 && cameraType3 == 1) {
            width3 = 720;
            height3 = 480;
        }
        GUtilMain.mEditor.putInt("pre_width3", width3);
        GUtilMain.mEditor.putInt("pre_height3", height3);
        GUtilMain.mEditor.commit();
    }

    public void setPsPosition4(int psPosition) {
        this.psCsiPosition4 = psPosition;
        GUtilMain.mEditor.putInt("pre_ps_csi_position4", this.psCsiPosition4);
        if (psPosition == 0) {
            width4 = 1280;
            height4 = 720;
        } else if (psPosition == 1) {
            width4 = 1920;
            height4 = 1080;
        } else if (psPosition == 2 && cameraType4 == 0) {
            width4 = 720;
            height4 = 576;
        } else if (psPosition == 2 && cameraType4 == 1) {
            width4 = 720;
            height4 = 480;
        }
        GUtilMain.mEditor.putInt("pre_width4", width4);
        GUtilMain.mEditor.putInt("pre_height4", height4);
        GUtilMain.mEditor.commit();

    }

    public void setPsPosition5(int psPosition) {
        this.psCsiPosition5 = psPosition;
        GUtilMain.mEditor.putInt("pre_ps_csi_position5", this.psCsiPosition5);
        if (psPosition == 0) {
            width5 = 1280;
            height5 = 720;
        } else if (psPosition == 1) {
            width5 = 1920;
            height5 = 1080;
        } else if (psPosition == 2 && cameraType5 == 0) {
            width5 = 720;
            height5 = 576;
        } else if (psPosition == 2 && cameraType5 == 1) {
            width5 = 720;
            height5 = 480;
        }
        GUtilMain.mEditor.putInt("pre_width5", width5);
        GUtilMain.mEditor.putInt("pre_height5", height5);
        GUtilMain.mEditor.commit();
    }

    public void setPsPosition6(int psPosition) {
        this.psCsiPosition6 = psPosition;
        GUtilMain.mEditor.putInt("pre_ps_csi_position6", this.psCsiPosition6);
        if (psPosition == 0) {
            width6 = 1280;
            height6 = 720;
        } else if (psPosition == 1) {
            width6 = 1920;
            height6 = 1080;
        } else if (psPosition == 2 && cameraType6 == 0) {
            width6 = 720;
            height6 = 576;
        } else if (psPosition == 2 && cameraType6 == 1) {
            width6 = 720;
            height6 = 480;
        }
        GUtilMain.mEditor.putInt("pre_width6", width6);
        GUtilMain.mEditor.putInt("pre_height6", height6);
        GUtilMain.mEditor.commit();
    }

    public int getPsPosition(int channel) {
        if (channel > 6 || channel < 1)
            return -1;
        switch (channel) {
            case 1:
                psCsiPosition1 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position1", psCsiPosition1);
//                System.out.println("zyz --> get psCsiPosition1 --> "+psCsiPosition1);
                return psCsiPosition1;
            case 2:
                psCsiPosition2 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position2", psCsiPosition2);
//                System.out.println("zyz --> get psCsiPosition2 --> "+psCsiPosition2);
                return psCsiPosition2;
            case 3:
                psCsiPosition3 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position3", psCsiPosition3);
//                System.out.println("zyz --> get psCsiPosition3 --> "+psCsiPosition3);
                return psCsiPosition3;
            case 4:
                psCsiPosition4 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position4", psCsiPosition4);
//                System.out.println("zyz --> get psCsiPosition4 --> "+psCsiPosition4);
                return psCsiPosition4;
            case 5:
                psCsiPosition5 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position5", psCsiPosition5);
//                System.out.println("zyz --> get psCsiPosition5 --> "+psCsiPosition5);
                return psCsiPosition5;
            case 6:
                psCsiPosition6 = GUtilMain.mSharedPreferences.getInt("pre_ps_csi_position6", psCsiPosition6);
//                System.out.println("zyz --> get psCsiPosition6 --> "+psCsiPosition6);
                return psCsiPosition6;
            default:
                return -1;
        }

    }

    public int getPsPosition() {
        psCsiPosition = GUtilMain.mSharedPreferences.getInt("+", psCsiPosition);
        return psCsiPosition;
    }

    private boolean n41Exist = new File("/dev/n41").exists();

    public boolean isN41Exist() {
//        return new File("/dev/n41").exists();
        return n41Exist;
//        return false;
    }

    public int getPreviewNum() {
        previewNum = GUtilMain.mSharedPreferences.getInt("pre_num", previewNum);
//        System.out.println("zyz --> previewNum = "+previewNum);
        return previewNum > previewMaxNum ? previewMaxNum : previewNum;
    }

    public void setPreviewNum(int previewNum) {
        this.previewNum = previewNum;
        GUtilMain.mEditor.putInt("pre_num", this.previewNum);
        GUtilMain.mEditor.commit();
    }

    public int getCsiNum() {
        csiNum = GUtilMain.mSharedPreferences.getInt("pre_csi_num", csiNum);
        return csiNum;
    }

    public void setCsiNum(int csiNum) {
        this.csiNum = csiNum;
        GUtilMain.mEditor.putInt("pre_csi_num", this.csiNum);
        GUtilMain.mEditor.commit();
        //restore default value
//        previewNum = 4;
//        psPosition = 0;
//        width = 1280;
//        height = 720;
    }


    ////////////////////////////////ADAS///////////////////////////////////////////////////
    private float 	fCarLen = -1f;              //车长，单位mm
    private float 	fCarWidth = -1f;            //车宽(两个轮胎外侧之间的距离)，单位mm
    private float 	fRefCenter = -1f;           //相机与车辆中心之间的距离(从驾驶室往外看，左正右负)，单位mm
    private float 	fRefTop = -1f;              //相机到前保险杠距离，单位mm
    private float	fDisLen2Tyre = -1f;		  //镜头和前轮胎之间的距离，单位mm,镜头前方为正向
    private float 	fCameraHeight = -1f;		  //相机距离地面高度，单位mm
    private float   fCameraFocus = (float) 6.0;         //相机焦距，单位mm
    private float   fCameraDx = (float) 0.0042;
    private int     pointX = -1;
    private int     pointY = -1;
    private boolean calADASFlag = false;
    private boolean adasEnable = false;
    private int speedMode = 0;
    private int simulationSpeed = 60;
    private int secondWarnOpenSpeed = 60;
    private boolean gpioEnable = false;
    public boolean getADASCalFlag(){
        calADASFlag = GUtilMain.mSharedPreferences.getBoolean("adas_cal_flag", calADASFlag);
        return calADASFlag;
    }
    public float getCarLen(){
        fCarLen = GUtilMain.mSharedPreferences.getFloat("adas_car_len", fCarLen);
        return fCarLen;
    }
    public float getCarWidth(){
        fCarWidth = GUtilMain.mSharedPreferences.getFloat("adas_car_width", fCarWidth);
        return fCarWidth;
    }
    public float getRefCenter(){
        fRefCenter = GUtilMain.mSharedPreferences.getFloat("adas_ref_center", fRefCenter);
        return fRefCenter;
    }
    public float getRefTop(){
        fRefTop = GUtilMain.mSharedPreferences.getFloat("adas_ref_top", fRefTop);
        return fRefTop;
    }
    public float getDisLen2Tyre(){
        fDisLen2Tyre = GUtilMain.mSharedPreferences.getFloat("adas_dis_len_2_tyre", fDisLen2Tyre);
        return fDisLen2Tyre;
    }
    public float getCameraHeight(){
        fCameraHeight = GUtilMain.mSharedPreferences.getFloat("adas_camera_height", fCameraHeight);
        return fCameraHeight;
    }
    public float getCameraFocus(){
        fCameraFocus = GUtilMain.mSharedPreferences.getFloat("adas_camera_focus", fCameraFocus);
        return fCameraFocus;
    }
    public float getCameraDx(){
        fCameraDx = GUtilMain.mSharedPreferences.getFloat("adas_camera_dx", fCameraDx);
        return fCameraDx;
    }
    public int getPointX(){
        pointX = GUtilMain.mSharedPreferences.getInt("adas_point_x", pointX);
        return pointX;
    }
    public int getPointY(){
        pointY = GUtilMain.mSharedPreferences.getInt("adas_point_y", pointY);
        return pointY;
    }
    public boolean getADASEnable(){
        adasEnable = GUtilMain.mSharedPreferences.getBoolean("adas_enable", adasEnable);
        return adasEnable;
    }
    public int getSpeedMode(){
        speedMode = GUtilMain.mSharedPreferences.getInt("adas_speed_mode", speedMode);
        return speedMode;
    }
    public int getSimulationSpeed(){
        simulationSpeed = GUtilMain.mSharedPreferences.getInt("adas_simulation_speed", simulationSpeed);
        return simulationSpeed;
    }
    public boolean getGpioEnable(){
        gpioEnable = GUtilMain.mSharedPreferences.getBoolean("adas_gpio_enable", gpioEnable);
        return gpioEnable;
    }
    public int getSecondWarnOpenSpeed(){
        secondWarnOpenSpeed = GUtilMain.mSharedPreferences.getInt("adas_second_warn_open_speed", secondWarnOpenSpeed);
        return secondWarnOpenSpeed;
    }

    public void setADASCalFlag(boolean cal){
        calADASFlag = cal;
        GUtilMain.mEditor.putBoolean("adas_cal_flag", calADASFlag);
        GUtilMain.mEditor.commit();
    }
    public void setCarLen(float len){
        fCarLen = len;
        GUtilMain.mEditor.putFloat("adas_car_len", fCarLen);
        GUtilMain.mEditor.commit();
    }
    public void setCarWidth(float len){
        fCarWidth = len;
        GUtilMain.mEditor.putFloat("adas_car_width", fCarWidth);
        GUtilMain.mEditor.commit();
    }
    public void setRefCenter(float len){
        fRefCenter = len;
        GUtilMain.mEditor.putFloat("adas_ref_center", fRefCenter);
        GUtilMain.mEditor.commit();
    }
    public void setRefTop(float len){
        fRefTop = len;
        GUtilMain.mEditor.putFloat("adas_ref_top", fRefTop);
        GUtilMain.mEditor.commit();
    }
    public void setDisLen2Tyre(float len){
        fDisLen2Tyre = len;
        GUtilMain.mEditor.putFloat("adas_dis_len_2_tyre", fDisLen2Tyre);
        GUtilMain.mEditor.commit();
    }
    public void setCameraHeight(float len){
        fCameraHeight = len;
        GUtilMain.mEditor.putFloat("adas_camera_height", fCameraHeight);
        GUtilMain.mEditor.commit();
    }
    public void setCameraFocus(float len){
        fCameraFocus = len;
        GUtilMain.mEditor.putFloat("adas_camera_focus", fCameraFocus);
        GUtilMain.mEditor.commit();
    }
    public void setCameraDx(float len){
        fCameraDx = len;
        GUtilMain.mEditor.putFloat("adas_camera_dx", fCameraDx);
        GUtilMain.mEditor.commit();
    }
    public void setADASEnable(boolean enable){
        adasEnable = enable;
        GUtilMain.mEditor.putBoolean("adas_enable", adasEnable);
        GUtilMain.mEditor.commit();
    }
    public void setSpeedMode(int mode){
        speedMode = mode;
        GUtilMain.mEditor.putInt("adas_speed_mode", speedMode);
        GUtilMain.mEditor.commit();
    }
    public void setSimulationSpeed(int speed){
        simulationSpeed = speed;
        GUtilMain.mEditor.putInt("adas_simulation_speed", simulationSpeed);
        GUtilMain.mEditor.commit();
    }
    public void setGpioEnable(boolean enable){
        gpioEnable = enable;
        GUtilMain.mEditor.putBoolean("adas_gpio_enable", gpioEnable);
        GUtilMain.mEditor.commit();
    }
    public void setSecondWarnOpenSpeed(int speed){
        secondWarnOpenSpeed = speed;
        GUtilMain.mEditor.putInt("adas_second_warn_open_speed", secondWarnOpenSpeed);
        GUtilMain.mEditor.commit();
    }

    //////////////////////////////////////// DMS ////////////////////////////////////////
    private boolean dmsEnable = false;
    private float dmsOpenSpeed = -1;
    private boolean isFaceEntry = false;
    private String image0, image1, image2, image3, image4, image5, image6, image7;
    private int faceNumber = 0;
    private final String identityPictureName = "identity_data";
    private boolean identityEnable = false;
    private int identityCheckInterval = 10;
    public String getIdentityPictureName(){
        return identityPictureName;
    }
    public String getImageName(int num){
        switch (num){
            case 0:
                image0 = GUtilMain.mSharedPreferences.getString("dms_identity_name_0", image0);
                return image0;
            case 1:
                image1 = GUtilMain.mSharedPreferences.getString("dms_identity_name_1", image1);
                return image1;
            case 2:
                image2 = GUtilMain.mSharedPreferences.getString("dms_identity_name_2", image2);
                return image2;
            case 3:
                image3 = GUtilMain.mSharedPreferences.getString("dms_identity_name_3", image3);
                return image3;
            case 4:
                image4 = GUtilMain.mSharedPreferences.getString("dms_identity_name_4", image4);
                return image4;
            case 5:
                image5 = GUtilMain.mSharedPreferences.getString("dms_identity_name_5", image5);
                return image5;
            case 6:
                image6 = GUtilMain.mSharedPreferences.getString("dms_identity_name_6", image6);
                return image6;
            case 7:
                image7 = GUtilMain.mSharedPreferences.getString("dms_identity_name_7", image7);
                return image7;
        }
        return null;
    }
    public boolean getDMSEnable(){
        dmsEnable = GUtilMain.mSharedPreferences.getBoolean("dms_enable", dmsEnable);
        return dmsEnable;
    }
    public float getDMSOpenSpeed(){
        dmsOpenSpeed = GUtilMain.mSharedPreferences.getFloat("dms_open_speed", dmsOpenSpeed);
        return dmsOpenSpeed;
    }
    public boolean getDMSIsFaceEntry(){
        isFaceEntry = GUtilMain.mSharedPreferences.getBoolean("dms_face_entry", isFaceEntry);
        return isFaceEntry;
    }
    public int getFaceNumber(){
        faceNumber = GUtilMain.mSharedPreferences.getInt("dms_face_number", faceNumber);
        return faceNumber;
    }
    public boolean getIdentityEnable(){
        identityEnable = GUtilMain.mSharedPreferences.getBoolean("dms_identity_enable", identityEnable);
        return identityEnable;
    }
    public int getIdentityCheckInterval(){
        identityCheckInterval = GUtilMain.mSharedPreferences.getInt("dms_identity_check_interval", identityCheckInterval);
        return identityCheckInterval;
    }

    public void setImageName(int num, String str){
        switch (num){
            case 0:
                image0 = str;
                GUtilMain.mEditor.putString("dms_identity_name_0", image0);
                GUtilMain.mEditor.commit();
                break;
            case 1:
                image1 = str;
                GUtilMain.mEditor.putString("dms_identity_name_1", image1);
                GUtilMain.mEditor.commit();
            case 2:
                image2 = str;
                GUtilMain.mEditor.putString("dms_identity_name_2", image2);
                GUtilMain.mEditor.commit();
                break;
            case 3:
                image3 = str;
                GUtilMain.mEditor.putString("dms_identity_name_3", image3);
                GUtilMain.mEditor.commit();
                break;
            case 4:
                image4 = str;
                GUtilMain.mEditor.putString("dms_identity_name_4", image4);
                GUtilMain.mEditor.commit();
                break;
            case 5:
                image5 = str;
                GUtilMain.mEditor.putString("dms_identity_name_5", image5);
                GUtilMain.mEditor.commit();
                break;
            case 6:
                image6 = str;
                GUtilMain.mEditor.putString("dms_identity_name_6", image6);
                GUtilMain.mEditor.commit();
                break;
            case 7:
                image7 = str;
                GUtilMain.mEditor.putString("dms_identity_name_7", image7);
                GUtilMain.mEditor.commit();
                break;
        }
    }
    public void setDMSEnable(boolean enable){
        dmsEnable = enable;
        GUtilMain.mEditor.putBoolean("dms_enable", dmsEnable);
        GUtilMain.mEditor.commit();
    }
    public void setDMSOpenSpeed(float speed){
        dmsOpenSpeed = speed;
        GUtilMain.mEditor.putFloat("dms_open_speed", dmsOpenSpeed);
        GUtilMain.mEditor.commit();
    }
    public void setDMSIsFaceEntry(boolean enable){
        isFaceEntry = enable;
        GUtilMain.mEditor.putBoolean("dms_face_entry", isFaceEntry);
        GUtilMain.mEditor.commit();
    }
    public void setFaceNumber(int num){
        faceNumber = num;
        GUtilMain.mEditor.putInt("dms_face_number", faceNumber);
        GUtilMain.mEditor.commit();
    }
    public void setIdentityEnable(boolean enable){
        identityEnable = enable;
        GUtilMain.mEditor.putBoolean("dms_identity_enable", identityEnable);
        GUtilMain.mEditor.commit();
    }
    public void setIdentityCheckInterval(int s){
        identityCheckInterval = s;
        GUtilMain.mEditor.putInt("dms_identity_check_interval", identityCheckInterval);
        GUtilMain.mEditor.commit();
    }


    ///////////////////////////////////////////////////BSD///////////////////////////////////////////////////
    private float 	fFirstWarnDistance = -1f;           //一级报警距离(单位:mm)
    private float 	fSecondWarnDistance = -1f;          //二级报警距离(单位:mm)
    private float 	fThirdWarnDistance = -1f;           //三级报警距离(单位:mm)
    private float 	fFrontWarnDistance = -1f;           //前方报警距离(单位:mm)
    private boolean calBSDFlag = false;
    public boolean getBSDCalFlag(){
        calBSDFlag = GUtilMain.mSharedPreferences.getBoolean("bsd_cal_flag", calBSDFlag);
        return calBSDFlag;
    }
    public float getFirstWarnDistance(){
        fFirstWarnDistance = GUtilMain.mSharedPreferences.getFloat("first_warn_d", fFirstWarnDistance);
        return fFirstWarnDistance;
    }
    public float getSecondWarnDistance(){
        fSecondWarnDistance = GUtilMain.mSharedPreferences.getFloat("second_warn_d", fSecondWarnDistance);
        return fSecondWarnDistance;
    }
    public float getThirdWarnDistance(){
        fThirdWarnDistance = GUtilMain.mSharedPreferences.getFloat("third_warn_d", fThirdWarnDistance);
        return fThirdWarnDistance;
    }
    public float getFrontWarnDistance(){
        fFrontWarnDistance = GUtilMain.mSharedPreferences.getFloat("front_warn_d", fFrontWarnDistance);
        return fFrontWarnDistance;
    }

    public void setBSDCalFlag(boolean cal){
        calBSDFlag = cal;
        GUtilMain.mEditor.putBoolean("bsd_cal_flag", calBSDFlag);
        GUtilMain.mEditor.commit();
    }
    public void setFirstWarnDistance(float d){
        fFirstWarnDistance = d;
        GUtilMain.mEditor.putFloat("first_warn_d", fFirstWarnDistance);
        GUtilMain.mEditor.commit();
    }
    public void setSecondWarnDistance(float d){
        fSecondWarnDistance = d;
        GUtilMain.mEditor.putFloat("second_warn_d", fSecondWarnDistance);
        GUtilMain.mEditor.commit();
    }
    public void setThirdWarnDistance(float d){
        fThirdWarnDistance = d;
        GUtilMain.mEditor.putFloat("third_warn_d", fThirdWarnDistance);
        GUtilMain.mEditor.commit();
    }
    public void setFrontWarnDistance(float d){
        fFrontWarnDistance = d;
        GUtilMain.mEditor.putFloat("front_warn_d", fFrontWarnDistance);
        GUtilMain.mEditor.commit();
    }

    public void setPointX(int x){
        pointX = x;
        GUtilMain.mEditor.putInt("adas_point_x", pointX);
        GUtilMain.mEditor.commit();
    }
    public void setPointY(int y){
        pointY = y;
        GUtilMain.mEditor.putInt("adas_point_y", pointY);
        GUtilMain.mEditor.commit();
    }
}
