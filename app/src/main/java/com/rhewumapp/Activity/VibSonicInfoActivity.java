package com.rhewumapp.Activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rhewumapp.R;

public class VibSonicInfoActivity extends AppCompatActivity {
 ImageView imgBack;
 TextView textBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vib_sonic_info);
        initObjects();
        // click on back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });
        textBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });
    }

    private void initObjects() {
        imgBack=findViewById(R.id.activity_mesh_info_back_iv);
        textBack=findViewById(R.id.activity_mesh_info_back_tv);
    }
}