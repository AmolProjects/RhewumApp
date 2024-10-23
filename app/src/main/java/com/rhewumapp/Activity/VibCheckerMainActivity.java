package com.rhewumapp.Activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rhewumapp.Activity.Adapter.VibCheckerAdapter;
import com.rhewumapp.Activity.Fragments.PsdFragment;
import com.rhewumapp.Activity.Fragments.SummeryFragment;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;
import com.rhewumapp.databinding.ActivityVibCheckerMainBinding;

import java.util.List;

public class VibCheckerMainActivity extends DrawerBaseActivity {
    private TabLayout tab_layout;
    private ViewPager2 viewPager;
    private VibCheckerAdapter vibCheckerAdapter;
    TextView txtBack;
    ImageView imgBack;

    ActivityVibCheckerMainBinding activityVibCheckerMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
                // Reset the background for all tabs
                for (int i = 0; i < tab_layout.getTabCount(); i++) {
                    View tab = ((ViewGroup) tab_layout.getChildAt(0)).getChildAt(i);
                    tab.setBackgroundResource(R.color.white);
                }

                // Set background for the selected tab
                View selectedTab = ((ViewGroup) tab_layout.getChildAt(0)).getChildAt(position);
                selectedTab.setBackgroundResource(R.color.header_backgrounds);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                handleMaxAcceleration();
                handleMaxFrequency();
                handleDisplacementData();
                handleFrequencyMagnitudeData();
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
        imgBack=findViewById(R.id.imges_backss);
        tab_layout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.viewPager);
    }
}