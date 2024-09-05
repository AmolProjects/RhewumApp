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
import com.rhewum.DrawerBaseActivity;
import com.rhewum.R;
import com.rhewum.databinding.ActivityVibCheckerMainBinding;
import com.rhewum.databinding.ActivityVibSonicBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class VibCheckerMainActivity extends DrawerBaseActivity {
    private TabLayout tab_layout;
    private ViewPager2 viewPager;
    private VibCheckerAdapter vibCheckerAdapter;
    TextView txtBack;
    ImageView imgBack;

    ActivityVibCheckerMainBinding activityVibCheckerMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_vib_checker_main);
        activityVibCheckerMainBinding = ActivityVibCheckerMainBinding.inflate(getLayoutInflater());
        setContentView(activityVibCheckerMainBinding.getRoot());
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
                if (position == 1) { // When SecondFragment is selected and send the data
                    // max acceleration x
                    handleMaxAcceleration();
                    handleMaxFrequency();
                    handleDisplacementData();
                    handleFrequencyMagnitudeData();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private void handleMaxAcceleration() {
        // max acceleration x
        float XMaxAcceleration = SummeryFragment.maxXAcceleration();
        float YMaxAcceleration = SummeryFragment.maxYAcceleration();
        float ZMaxAcceleration = SummeryFragment.maxZAcceleration();

        PsdFragment.xMaxAcceleration(XMaxAcceleration);
        PsdFragment.yMaxAcceleration(YMaxAcceleration);
        PsdFragment.zMaxAcceleration(ZMaxAcceleration);
    }

    private void handleMaxFrequency() {
        // max dominant frequency
        float XMaxFrequency = SummeryFragment.dominantXFrequency();
        float YMaxFrequency = SummeryFragment.dominantYFrequency();
        float ZMaxFrequency = SummeryFragment.dominantZFrequency();

        PsdFragment.xMaxFrequency(XMaxFrequency);
        PsdFragment.yMaxFrequency(YMaxFrequency);
        PsdFragment.zMaxFrequency(ZMaxFrequency);
    }

    private void handleDisplacementData() {
        // max Displacement
        List<Float> listDisplacementX = SummeryFragment.getDataListX();
        List<Float> listDisplacementY = SummeryFragment.getDataListY();
        List<Float> listDisplacementZ = SummeryFragment.getDataListZ();

        PsdFragment.updateDataX(listDisplacementX);
        PsdFragment.updateDataY(listDisplacementY);
        PsdFragment.updateDataZ(listDisplacementZ);
    }

    private void handleFrequencyMagnitudeData() {
        // max dominant magnitude frequency
        List<Float> xMagnitudeFrequency = SummeryFragment.xFrequencyMagnitude();
        List<Float> yMagnitudeFrequency = SummeryFragment.yFrequencyMagnitude();
        List<Float> zMagnitudeFrequency = SummeryFragment.zFrequencyMagnitude();

        PsdFragment.xUpdateMagnitudeFrequency(xMagnitudeFrequency);
        PsdFragment.yUpdateMagnitudeFrequency(yMagnitudeFrequency);
        PsdFragment.zUpdateMagnitudeFrequency(zMagnitudeFrequency);
    }





    private void initObjects(){
        txtBack=findViewById(R.id.txtBack);
        imgBack=findViewById(R.id.imgBack);
        tab_layout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.viewPager);
    }
}