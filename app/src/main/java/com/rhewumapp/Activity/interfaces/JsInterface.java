package com.rhewumapp.Activity.interfaces;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JsInterface {
    Context mContext;

    // Instantiate the interface and set the context
    public JsInterface(Context c) {
        mContext = c;
    }

    // Show a toast from the web page
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }
}
