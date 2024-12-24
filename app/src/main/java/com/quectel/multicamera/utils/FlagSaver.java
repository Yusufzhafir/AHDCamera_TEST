package com.quectel.multicamera.utils;

public class FlagSaver {
    public static boolean isRestart = false;

    public static void setRestartState(boolean b){
        isRestart = b;
    }

    public static boolean getRestartState(){
        return isRestart;
    }
}
