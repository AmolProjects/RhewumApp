package com.rhewumapp.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;

public class WebsiteActivity extends DrawerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_website);
        // Create an Intent to open the web browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.rhewum.com/"));
        startActivity(browserIntent);
        finish();
    }
}