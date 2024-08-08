package com.rhewum.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rhewum.Activity.Adapter.VibCheckerAdapter;
import com.rhewum.Activity.Fragments.PsdFragment;
import com.rhewum.Activity.Fragments.SummeryFragment;
import com.rhewum.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class VibCheckerMainActivity extends AppCompatActivity {
    private TabLayout tab_layout;
    private ViewPager2 viewPager;
    private VibCheckerAdapter vibCheckerAdapter;
    TextView txtBack;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vib_checker_main);
        initObjects();

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                VibCheckerMainActivity.this.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                VibCheckerMainActivity.this.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });
        vibCheckerAdapter = new VibCheckerAdapter(this);
        viewPager.setAdapter(vibCheckerAdapter);

        new TabLayoutMediator(tab_layout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Summary");
                        break;
                    case 1:
                        tab.setText("PSD");
                        break;
                }
            }
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1) { // When SecondFragment is selected
                    // x displacement data ,y and z
                    List<Float> listDisplacementX =SummeryFragment.getDataListX();
                    List<Float> listDisplacementY =SummeryFragment.getDataListY();
                    List<Float> listDisplacementZ =SummeryFragment.getDataListZ();
                    // x magnitude frequency,y and z in the form of list return
                    List<Float>  xMagnitudeFrequency=SummeryFragment.xFrequencyMagnitude();
                    List<Float>  yMagnitudeFrequency=SummeryFragment.yFrequencyMagnitude();
                    List<Float>  zMagnitudeFrequency=SummeryFragment.zFrequencyMagnitude();
                    // put x,y,z dominant frequency
                    PsdFragment.xUpdateMagnitudeFrequency(xMagnitudeFrequency);
                    PsdFragment.yUpdateMagnitudeFrequency(yMagnitudeFrequency);
                    PsdFragment.zUpdateMagnitudeFrequency(zMagnitudeFrequency);
                    // put x,y,z displacement list data
                    PsdFragment.updateDataX(listDisplacementX);
                    PsdFragment.updateDataY(listDisplacementY);
                    PsdFragment.updateDataZ(listDisplacementZ);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }


    private void initObjects(){
        txtBack=findViewById(R.id.txtBack);
        imgBack=findViewById(R.id.imgBack);
        tab_layout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.viewPager);
    }
}