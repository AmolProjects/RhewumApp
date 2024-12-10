package com.rhewumapp.Activity.MeshConveterData;

import android.os.Build;

public class Device {
    private static int orientation;

    public static void load() {
        if (Build.BRAND.toLowerCase().contains("generic")) {
            orientation = 0;
        } else {
            orientation = 90;
        }
    }

    public static int getOrientation() {
        return orientation;
    }
}
