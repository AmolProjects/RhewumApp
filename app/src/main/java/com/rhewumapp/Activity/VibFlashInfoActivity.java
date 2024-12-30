package com.rhewumapp.Activity;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.rhewumapp.R;

public class VibFlashInfoActivity extends AppCompatActivity {
    TextView activity_mesh_info_back_tv,activity_mesh_info_norm_tv;
    ImageView activity_mesh_info_back_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vib_flash_info);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //  setContentView(R.layout.activity_vib_flash);
        // Set the status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.header_backgrounds));
        initObjects();


        // click on back
        activity_mesh_info_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });
        // click on the back
        activity_mesh_info_back_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });


    }
    private void initObjects(){
        activity_mesh_info_back_iv=findViewById(R.id.activity_mesh_info_back_iv);
        activity_mesh_info_back_tv=findViewById(R.id.activity_mesh_info_back_tv);
        activity_mesh_info_norm_tv=findViewById(R.id.activity_mesh_info_norm_tv);
    }
}