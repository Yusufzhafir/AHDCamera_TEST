package com.quectel.multicamera;

public class NativeGpio {
    public interface GpioInterruptCallback{
        void onNewValue(int value);
    }
    public static native void readGpio(String path, GpioInterruptCallback callback);

    static{
        System.loadLibrary("gpio");
    }
}