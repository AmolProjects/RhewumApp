package com.rhewum.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rhewum.Activity.MeshConveterData.Utils;
import com.rhewum.R;

public class VibFlashInfoActivity extends AppCompatActivity {
    TextView activity_mesh_info_back_tv;
    ImageView activity_mesh_info_back_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vib_flash_info);
        Utils.setFontFamily("fonts/heebo.ttf");
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
            }
        });
        // click on the back
        activity_mesh_info_back_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    private void initObjects(){
        activity_mesh_info_back_iv=findViewById(R.id.activity_mesh_info_back_iv);
        activity_mesh_info_back_tv=findViewById(R.id.activity_mesh_info_back_tv);
    }
}