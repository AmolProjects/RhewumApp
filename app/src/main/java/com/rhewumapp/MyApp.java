package com.rhewumapp;

import android.app.Application;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        overrideFont("SERIF", "font/handle_go.ttf"); // Apply font to all TextView elements
    }

    private void overrideFont(String fontToReplace, String customFontFileNameInAssets) {
        try {
            Typeface customFontTypeface = Typeface.createFromAsset(getAssets(), customFontFileNameInAssets);
            Field defaultFontTypefaceField = Typeface.class.getDeclaredField(fontToReplace);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}