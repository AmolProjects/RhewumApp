package com.rhewumapp.Activity.MeshConveterData;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;

public class ResponsiveAndroidBars {
    public static void setNotificationBarColor(Activity activity, int i, boolean z) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            if (z) {
                window.getDecorView().getWindowInsetsController().setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            } else {
                window.getDecorView().getWindowInsetsController().setSystemBarsAppearance(0, 0);
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            View decorView = window.getDecorView();
            if (z) {
                decorView.setSystemUiVisibility(8192);
            } else {
                decorView.setSystemUiVisibility(0);
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
//            window.clearFlags(PagedChannelRandomAccessSource.DEFAULT_TOTAL_BUFSIZE);
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(i);
        }
    }

    public static void setNavigationBarColor(Activity activity, int i, boolean z, boolean z2) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            if (z) {
                window.getDecorView().getWindowInsetsController().setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
            } else {
                window.getDecorView().getWindowInsetsController().setSystemBarsAppearance(0, 0);
            }
        } else if (Build.VERSION.SDK_INT >= 26) {
            View decorView = window.getDecorView();
            if (z && z2) {
                decorView.setSystemUiVisibility(8208);
            } else if (z) {
                decorView.setSystemUiVisibility(16);
            } else {
                decorView.setSystemUiVisibility(0);
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
//            window.clearFlags(PagedChannelRandomAccessSource.DEFAULT_TOTAL_BUFSIZE);
            window.addFlags(Integer.MIN_VALUE);
            window.setNavigationBarColor(i);
        }
    }
}
