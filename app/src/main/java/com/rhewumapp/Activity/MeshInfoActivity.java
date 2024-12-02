package com.rhewumapp.Activity;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rhewumapp.Activity.MeshConveterData.Utils;
import com.rhewumapp.Activity.MeshConveterData.ResponsiveAndroidBars;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;

public class MeshInfoActivity extends DrawerBaseActivity {
    ConstraintLayout activity_mesh_info_back_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesh_info2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        ResponsiveAndroidBars.setNotificationBarColor(this, getResources().getColor(R.color.header_backgrounds), false);
        ResponsiveAndroidBars.setNavigationBarColor(this, getResources().getColor(R.color.header_backgrounds), false, false);
        activity_mesh_info_back_layout=findViewById(R.id.activity_mesh_info_back_layout);
        activity_mesh_info_back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });
        ((TextView) findViewById(R.id.activity_mesh_info_tyler)).setText(Html.fromHtml("TYLER&reg :"));
    }
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}