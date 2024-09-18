package com.rhewum.Activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rhewum.Activity.MeshConveterData.Utils;
import com.rhewum.Activity.MeshConveterData.ResponsiveAndroidBars;
import com.rhewum.DrawerBaseActivity;
import com.rhewum.R;

public class MeshInfoActivity extends DrawerBaseActivity {
    ConstraintLayout activity_mesh_info_back_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setFontFamily("fonts/heebo.ttf");
        setContentView(R.layout.activity_mesh_info2);

        ResponsiveAndroidBars.setNotificationBarColor(this, getResources().getColor(R.color.header_backgrounds), false);
        ResponsiveAndroidBars.setNavigationBarColor(this, getResources().getColor(R.color.grey_background), false, false);
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