package com.rhewumapp.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
    TextView txtBack, txtResults;
    ImageView imgBacks;
    float XMaxAcceleration;
    static float amplitudeX,amplitudeY,amplitudeZ;

    ActivityVibCheckerMainBinding activityVibCheckerMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_vib_checker_main);
        activityVibCheckerMainBinding = ActivityVibCheckerMainBinding.inflate(getLayoutInflater());
        setContentView(activityVibCheckerMainBinding.getRoot());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.header_backgrounds));
        initObjects();

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                VibCheckerMainActivity.this.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });
        imgBacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                VibCheckerMainActivity.this.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });

        txtResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VibCheckerMainActivity.this, VibChekerArchiveActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

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
                    handleFrequencyMagnitudeData();
                    handleMaxAmplitude();

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
                handleFrequencyMagnitudeData();
            }
        });
    }



    private void handleMaxAcceleration() {
        // max acceleration x
        XMaxAcceleration = SummeryFragment.maxXAcceleration();
        float YMaxAcceleration = SummeryFragment.maxYAcceleration();
        float ZMaxAcceleration = SummeryFragment.maxZAcceleration();
        String timer = SummeryFragment.timer();

        PsdFragment.xMaxAcceleration(XMaxAcceleration);
        PsdFragment.yMaxAcceleration(YMaxAcceleration);
        PsdFragment.zMaxAcceleration(ZMaxAcceleration);
    }


    private void handleMaxAmplitude() {
        // max amplitude x,y,z
        amplitudeX = SummeryFragment.maxXAmplitude();
        amplitudeY = SummeryFragment.maxYAmplitude();
        amplitudeZ = SummeryFragment.maxZAmplitude();

        PsdFragment.xMaxAmplitude(amplitudeX);
        PsdFragment.yMaxAmplitude(amplitudeY);
        PsdFragment.zMaxAmplitude(amplitudeZ);
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

    private void handleFrequencyMagnitudeData() {
        // max dominant magnitude frequency
        List<Float> xMagnitudeFrequency = SummeryFragment.xFrequencyMagnitude();
        Log.e("Magnitude x","Magnitude X"+xMagnitudeFrequency.size());
        List<Float> yMagnitudeFrequency = SummeryFragment.yFrequencyMagnitude();
        Log.e("Magnitude y","Magnitude Y"+yMagnitudeFrequency.size());
        List<Float> zMagnitudeFrequency = SummeryFragment.zFrequencyMagnitude();
        Log.e("Magnitude z","Magnitude Z"+zMagnitudeFrequency.size());

        PsdFragment.xUpdateMagnitudeFrequency(xMagnitudeFrequency);
        PsdFragment.yUpdateMagnitudeFrequency(yMagnitudeFrequency);
        PsdFragment.zUpdateMagnitudeFrequency(zMagnitudeFrequency);
    }


    private void initObjects() {
        txtBack = findViewById(R.id.txtBack);
        imgBacks = findViewById(R.id.imges_back);
        tab_layout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);
        txtResults = findViewById(R.id.txtResults);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}