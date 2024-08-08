package com.rhewum.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.rhewum.Activity.MeshConveterData.Utils;
import com.rhewum.R;

public class VibFlashActivity extends AppCompatActivity implements View.OnClickListener{
    private Button freq16;
    private Button freq20;
    private Button freq25;
    private Button freq30;
    private Button freq50;
    private Button freq60;
    private Button freqMinus;
    private Button freqPlus;
    private TextView freqTv,txtBack;
    private TextView hertzTv;
    private  Button activity_vib_flash_start_btn;
    private static final String TAG = VibFlashActivity.class.getName();
    private CameraManager cameraManager;
    private String cameraId;
    private Handler handler;
    private boolean isFlashOn = false;
    private boolean isBlinking = false;
    private Runnable blinkRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setFontFamily("fonts/heebo.ttf");
        setContentView(R.layout.activity_vib_flash);
        // Set the status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.header_backgrounds));
        setUpViews();
        this.freqMinus.setOnClickListener(this);
        this.freqPlus.setOnClickListener(this);
        this.freq16.setOnClickListener(this);
        this.freq20.setOnClickListener(this);
        this.freq25.setOnClickListener(this);
        this.freq30.setOnClickListener(this);
        this.freq50.setOnClickListener(this);
        this.freq60.setOnClickListener(this);
        this.txtBack.setOnClickListener(this);
        this.activity_vib_flash_start_btn.setOnClickListener(this);

        this.freqTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        this.activity_vib_flash_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBlinking) {
                    stopBlinking();
                } else {
                    startBlinking(Double.parseDouble(freqTv.getText().toString()));
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.equals(this.freqPlus)) {
            String charSequence = this.freqTv.getText().toString();
            if (!charSequence.equals("100.0")) {
                this.freqTv.setText(Utils.getIncreasedValue(charSequence));
                resetOtherButtons("");
            }
        } else if (view.equals(this.freqMinus)) {
            String charSequence2 = this.freqTv.getText().toString();
            if (!charSequence2.equals("10.0")) {
                this.freqTv.setText(Utils.getDecreasedValue(charSequence2));
                resetOtherButtons("");
            }
        } else if (view.equals(this.txtBack)) {
          //  this.txtBack.setChecked(false);
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        } else {
            String obj = view.getTag().toString();
            this.freqTv.setText(obj);
            resetOtherButtons(obj);
        }

    }
    private void setUpViews() {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];  // Assume the first camera has a flash
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler = new Handler(Looper.getMainLooper()); // Initialize the Handler
        this.freqMinus = (Button) findViewById(R.id.activity_vib_flash_freq_minus);
        this.freqPlus = (Button) findViewById(R.id.activity_vib_flash_freq_plus);
        this.freqTv = (TextView) findViewById(R.id.activity_vib_flash_frequency_tv);
        this.hertzTv = (TextView) findViewById(R.id.activity_vib_flash_hertz_tv);
        this.freq16 = (Button) findViewById(R.id.activity_vib_flash_freq_16);
        this.freq20 = (Button) findViewById(R.id.activity_vib_flash_freq_20);
        this.freq25 = (Button) findViewById(R.id.activity_vib_flash_freq_25);
        this.freq30 = (Button) findViewById(R.id.activity_vib_flash_freq_30);
        this.freq50 = (Button) findViewById(R.id.activity_vib_flash_freq_50);
        this.freq60 = (Button) findViewById(R.id.activity_vib_flash_freq_60);
        this.txtBack = (TextView) findViewById(R.id.txtBack);
        this.activity_vib_flash_start_btn=findViewById(R.id.activity_vib_flash_start_btn);
        this.freq16.setText(Html.fromHtml("16 &frac23;"));
        this.freqMinus.setText(Html.fromHtml("&minus;"));

    }
    private void resetOtherButtons(String str) {
        if (str.equals("16.7")) {
            this.freq16.setBackgroundResource(R.drawable.round_box_blue);
            this.freq20.setBackgroundResource(R.drawable.round_box_grey);
            this.freq25.setBackgroundResource(R.drawable.round_box_grey);
            this.freq30.setBackgroundResource(R.drawable.round_box_grey);
            this.freq50.setBackgroundResource(R.drawable.round_box_grey);
            this.freq60.setBackgroundResource(R.drawable.round_box_grey);
        } else if (str.equals("20")) {
            this.freq20.setBackgroundResource(R.drawable.round_box_blue);
            this.freq16.setBackgroundResource(R.drawable.round_box_grey);
            this.freq25.setBackgroundResource(R.drawable.round_box_grey);
            this.freq30.setBackgroundResource(R.drawable.round_box_grey);
            this.freq50.setBackgroundResource(R.drawable.round_box_grey);
            this.freq60.setBackgroundResource(R.drawable.round_box_grey);
        } else if (str.equals("25")) {
            this.freq25.setBackgroundResource(R.drawable.round_box_blue);
            this.freq16.setBackgroundResource(R.drawable.round_box_grey);
            this.freq20.setBackgroundResource(R.drawable.round_box_grey);
            this.freq30.setBackgroundResource(R.drawable.round_box_grey);
            this.freq50.setBackgroundResource(R.drawable.round_box_grey);
            this.freq60.setBackgroundResource(R.drawable.round_box_grey);
        } else if (str.equals("30")) {
            this.freq30.setBackgroundResource(R.drawable.round_box_blue);
            this.freq16.setBackgroundResource(R.drawable.round_box_grey);
            this.freq20.setBackgroundResource(R.drawable.round_box_grey);
            this.freq25.setBackgroundResource(R.drawable.round_box_grey);
            this.freq50.setBackgroundResource(R.drawable.round_box_grey);
            this.freq60.setBackgroundResource(R.drawable.round_box_grey);
        } else if (str.equals("50")) {
            this.freq50.setBackgroundResource(R.drawable.round_box_blue);
            this.freq16.setBackgroundResource(R.drawable.round_box_grey);
            this.freq20.setBackgroundResource(R.drawable.round_box_grey);
            this.freq25.setBackgroundResource(R.drawable.round_box_grey);
            this.freq30.setBackgroundResource(R.drawable.round_box_grey);
            this.freq60.setBackgroundResource(R.drawable.round_box_grey);
        } else if (str.equals("60")) {
            this.freq60.setBackgroundResource(R.drawable.round_box_blue);
            this.freq16.setBackgroundResource(R.drawable.round_box_grey);
            this.freq20.setBackgroundResource(R.drawable.round_box_grey);
            this.freq25.setBackgroundResource(R.drawable.round_box_grey);
            this.freq30.setBackgroundResource(R.drawable.round_box_grey);
            this.freq50.setBackgroundResource(R.drawable.round_box_grey);
        } else {
            this.freq16.setBackgroundResource(R.drawable.round_box_grey);
            this.freq20.setBackgroundResource(R.drawable.round_box_grey);
            this.freq25.setBackgroundResource(R.drawable.round_box_grey);
            this.freq30.setBackgroundResource(R.drawable.round_box_grey);
            this.freq50.setBackgroundResource(R.drawable.round_box_grey);
            this.freq60.setBackgroundResource(R.drawable.round_box_grey);
        }
    }
    @SuppressLint("SetTextI18n")
    private void startBlinking(double frequency) {
        long period = (long) (1000 / frequency / 2); // Half period for ON and OFF
        long endTime = System.currentTimeMillis() + 5 * 1000L;
        isBlinking = true;

        activity_vib_flash_start_btn.setBackgroundColor(Color.RED);
        activity_vib_flash_start_btn.setText("Stop Flash");


        blinkRunnable = new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() < endTime && isBlinking) {
                    isFlashOn = !isFlashOn;
                    try {
                        cameraManager.setTorchMode(cameraId, isFlashOn);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    handler.postDelayed(this, period);
                } else {
                    stopBlinking();
                }
            }
        };
        handler.post(blinkRunnable);
    }
    @SuppressLint("SetTextI18n")
    private void stopBlinking() {
        isBlinking = false;
        handler.removeCallbacks(blinkRunnable);
        activity_vib_flash_start_btn.setText("Start Flash");
        activity_vib_flash_start_btn.setBackgroundColor(VibFlashActivity.this.getResources().getColor(R.color.header_backgrounds));

        try {
            cameraManager.setTorchMode(cameraId, false); // Ensure the flash is turned off
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}