package com.rhewum.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.rhewum.Activity.MeshConveterData.Utils;
import com.rhewum.DrawerBaseActivity;
import com.rhewum.R;
import com.rhewum.databinding.ActivityFlashesBinding;



public class VibFlashActivity extends DrawerBaseActivity implements View.OnClickListener{
    private Button freq16;
    private Button freq20;
    private Button freq25;
    private Button freq30;
    private Button freq50;
    private Button freq60;
    private Button freqMinus;
    private Button freqPlus;
    private TextView txtBack,activity_mesh_info_tv;
    private EditText freqTv;
    private TextView hertzTv;
    private  Button activity_vib_flash_start_btn;
    private static final String TAG = VibFlashActivity.class.getName();
    private CameraManager cameraManager;
    private String cameraId;
    private Handler handler;
    private boolean isFlashOn = false;
    private boolean isBlinking = false;
    private Runnable blinkRunnable;
    ImageView imgBack,activity_mesh_info_iv;
    private boolean isRunning = false;

//    ActivityVibFlashesBinding activityVibFlashesBinding;
    ActivityFlashesBinding activityFlashesBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//       setContentView(R.layout.activity_flashes);
        activityFlashesBinding = ActivityFlashesBinding.inflate(getLayoutInflater());
       setContentView(activityFlashesBinding.getRoot());
        Utils.setFontFamily("fonts/heebo.ttf");
      //  setContentView(R.layout.activity_vib_flash);
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
        this.freqTv.setOnClickListener(this);
        activity_mesh_info_tv.setOnClickListener(this);
        activity_mesh_info_iv.setOnClickListener(this);

        // continuous increment when button hold by user
        freqPlus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isRunning = true; // Start the flag for continuous changes
                blinkRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String charSequence = freqTv.getText().toString();
                        if (!charSequence.equals("100.0")) {
                            freqTv.setText(Utils.getIncreasedValue(charSequence));
                            resetOtherButtons("");
                            if (isRunning) {
                                handler.postDelayed(this, 100); // Change the text every 100 milliseconds
                            }
                        }
                    }
                };
                handler.post(blinkRunnable); // Start the runnable
                return true;
            }
        });

       // Stop the continuous update when the button is released
        freqPlus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    isRunning = false; // Stop updating when the long press is released
                }
                return false;
            }

        });

        // continuous decrement when button hold by user
        freqMinus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isRunning = true; // Start the flag for continuous changes
                blinkRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String charSequence2 = freqTv.getText().toString();
                        if (!charSequence2.equals("10.0")) {
                            freqTv.setText(Utils.getDecreasedValue(charSequence2));
                            resetOtherButtons("");
                            if (isRunning) {
                                handler.postDelayed(this, 100); // Change the text every 100 milliseconds
                            }
                        }
                    }
                };
                handler.post(blinkRunnable); // Start the runnable
                return true;
            }
        });

        // Stop the continuous update when the button is released
        freqMinus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    isRunning = false; // Stop updating when the long press is released
                }
                return false;
            }

        });

        // click and go on info screen
        activity_mesh_info_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(VibFlashActivity.this, VibFlashInfoActivity.class));
            }
        });
        activity_mesh_info_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VibFlashActivity.this, VibFlashInfoActivity.class));

            }
        });

        // Open the keyboard when the TextView (EditText) is clicked
        freqTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard(freqTv);
            }
        });

        this.freqTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Get the updated text after the change
                String updatedText = editable.toString();
                // Now you can use the updatedText value
                // For example: Log the updated text or update a variable
                System.out.println("Updated Text: " + updatedText);
            }
        });

        this.activity_vib_flash_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBlinking) {
                    stopBlinking();
                } else {
                    startBlinking(Double.parseDouble(freqTv.getText().toString()),5);
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
        this.freqTv = (EditText) findViewById(R.id.activity_vib_flash_frequency_tv);
        this.hertzTv = (TextView) findViewById(R.id.activity_vib_flash_hertz_tv);
        this.freq16 = (Button) findViewById(R.id.activity_vib_flash_freq_16);
        this.freq20 = (Button) findViewById(R.id.activity_vib_flash_freq_20);
        this.freq25 = (Button) findViewById(R.id.activity_vib_flash_freq_25);
        this.freq30 = (Button) findViewById(R.id.activity_vib_flash_freq_30);
        this.freq50 = (Button) findViewById(R.id.activity_vib_flash_freq_50);
        this.freq60 = (Button) findViewById(R.id.activity_vib_flash_freq_60);
        this.txtBack = (TextView) findViewById(R.id.txtBack);
        imgBack=findViewById(R.id.imgBack);
        this.activity_vib_flash_start_btn=findViewById(R.id.activity_vib_flash_start_btn);
        activity_mesh_info_tv=findViewById(R.id.activity_mesh_info_tv);
        activity_mesh_info_iv=findViewById(R.id.activity_mesh_info_iv);
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
    private void startBlinking(double frequency, int i) {
        long period = (long) (1000 / frequency / 2); // Half period for ON and OFF
        long endTime = System.currentTimeMillis() + 10 * 1000L;
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
    // Show the keyboard programmatically
    private void showKeyboard(View view) {
        freqTv.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
