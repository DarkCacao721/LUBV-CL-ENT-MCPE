package com.lubv.mobile;

public class NativeHacks {
    static { System.loadLibrary("lubv-native"); }
    public static native void init();
    public static native void enableHack(String name);
    public static native void disableHack(String name);
    public static native boolean isMinecraftRunning();
}
