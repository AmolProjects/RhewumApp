package com.rhewumapp.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
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

import com.rhewumapp.Activity.MeshConveterData.Utils;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;
import com.rhewumapp.databinding.ActivityFlashesBinding;


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
    private TextView hertzTv,activity_mesh_info_iv;
    private  Button activity_vib_flash_start_btn;
    private static final String TAG = VibFlashActivity.class.getName();
    private CameraManager cameraManager;
    private String cameraId;
    private Handler handler;
    private boolean isFlashOn = false;
    private boolean isBlinking = false;
    private Runnable blinkRunnable;
    ImageView image_vib;
    ImageView images;
    private boolean isRunning = false;

    //    ActivityVibFlashesBinding activityVibFlashesBinding;
    ActivityFlashesBinding activityFlashesBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

        activityFlashesBinding = ActivityFlashesBinding.inflate(getLayoutInflater());
        setContentView(activityFlashesBinding.getRoot());
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
        activity_mesh_info_iv.setOnClickListener(this);
        this.images.setOnClickListener(this);
        this.image_vib.setOnClickListener(this);

        freqPlus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isRunning = true; // Start the flag for continuous changes

                blinkRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // Get the current input in the freqTv
                        String input = freqTv.getText().toString().trim();

                        // Replace the comma with a dot to parse the input as a number
                        input = input.replace(",", ".");

                        try {
                            // Parse the input as a double
                            double frequency = Double.parseDouble(input);

                            // Increment the frequency by 0.1 for continuous change
                            frequency += 0.1;

                            // Ensure the frequency doesn't go beyond a maximum limit (if applicable)
                            if (frequency > 100.0) {
                                frequency = 100.0; // Set max limit to 100.0
                            }

                            // Format the frequency with the correct number of decimal places (1 decimal point in this case)
                            String newFrequency = String.format("%.1f", frequency);

                            // If the original input had a comma, replace the dot with a comma to preserve the original format
                            if (freqTv.getText().toString().contains(",")) {
                                newFrequency = newFrequency.replace(".", ",");
                            }

                            // Set the new incremented value to the TextView
                            freqTv.setText(newFrequency);
                            freqTv.setSelection(freqTv.getText().length()); // Set cursor to the end of the text

                            // Reset other buttons or UI elements if needed
                            resetOtherButtons(""); // Adjust this according to your use case

                            // Keep running the runnable if the flag is still true
                            if (isRunning) {
                                handler.postDelayed(this, 100); // Change the text every 100 milliseconds
                            }

                        } catch (NumberFormatException e) {
                            // Handle any invalid input gracefully
                            freqTv.setText("Invalid input");
                        }
                    }
                };

                // Start the continuous increment when long-click is detected
                handler.post(blinkRunnable);

                return true; // Return true to indicate that the long-click event has been handled
            }
        });



        freqPlus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {

                    isRunning = false; // Stop updating when the long press is released
                }
                return false;
            }

        });


        freqMinus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isRunning = true; // Start the flag for continuous changes
                blinkRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String charSequence2 = freqTv.getText().toString().replace(",", "."); // Replace comma with dot for parsing

                        if (!charSequence2.equals("1.0")) { // Ensure it doesn't go below 10.0
                            // Decrease the frequency value
                            String decreasedValue = Utils.getDecreasedValue(charSequence2);

                            // If the original input contained a comma, preserve it in the output
                            if (freqTv.getText().toString().contains(",")) {
                                decreasedValue = decreasedValue.replace(".", ",");
                            }

                            // Set the decreased value and move cursor to the end
                            freqTv.setText(decreasedValue);
                            freqTv.setSelection(freqTv.getText().length());

                            resetOtherButtons(""); // Reset other buttons if necessary

                            // Keep running the decrement operation if the flag is still true
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
        activity_mesh_info_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VibFlashActivity.this, VibFlashInfoActivity.class));

            }
        });
        image_vib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VibFlashActivity.this, VibFlashInfoActivity.class));
            }
        });


        freqTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed during text changes
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();

                // Remove the listener to prevent it from being triggered recursively
                freqTv.removeTextChangedListener(this);

                // Check if the input contains more than one dot or comma
                int dotCount = input.length() - input.replace(".", "").length();
                int commaCount = input.length() - input.replace(",", "").length();

                // If more than one dot or comma exists, trim the input to allow only one
                if (dotCount > 1 || commaCount > 1) {
                    input = input.substring(0, input.length() - 1); // Remove the last entered character
                    freqTv.setText(input);
                    freqTv.setSelection(input.length()); // Set cursor to the end
                }

                // Limit input to two decimal places
                if (input.contains(".")) {
                    int index = input.indexOf(".");
                    if (input.length() - index - 1 > 2) {
                        input = input.substring(0, index + 3); // Keep only 2 digits after the decimal
                        freqTv.setText(input);
                        freqTv.setSelection(input.length()); // Set cursor to the end
                    }
                } else if (input.contains(",")) {
                    int index = input.indexOf(",");
                    if (input.length() - index - 1 > 2) {
                        input = input.substring(0, index + 3); // Keep only 2 digits after the decimal
                        freqTv.setText(input);
                        freqTv.setSelection(input.length()); // Set cursor to the end
                    }
                }

                // Ensure the input value is greater than or equal to 1
                try {
                    double frequency = Double.parseDouble(input.replace(",", "."));
                    if (frequency < 1) {
                        input = "1"; // Set minimum value to 1
                        freqTv.setText(input);
                        freqTv.setSelection(input.length()); // Set cursor to the end
                    }
                } catch (NumberFormatException e) {
                    // Handle case where the input is not a valid number
                    input = "1"; // Set to the minimum valid input
                    freqTv.setText(input);
                    freqTv.setSelection(input.length()); // Set cursor to the end
                }

                // Re-add the listener after making changes
                freqTv.addTextChangedListener(this);
            }
        });






        this.activity_vib_flash_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputValue = freqTv.getText().toString().replace(',', '.'); // Replace comma with dot
                if (isBlinking) {
                    stopBlinking();
                } else {
                    try {
                        double frequency = Double.parseDouble(inputValue);
                        startBlinking(frequency, 5);
                        freqTv.requestFocus();


                    } catch (NumberFormatException e) {
                        Toast.makeText(VibFlashActivity.this, "Please enter a valid frequency.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            String input = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend);

            // Allow comma or dot for decimal separation
            if (input.contains(".")) {
                // Ensure only one decimal point
                if (input.indexOf('.') != input.lastIndexOf('.')) {
                    return "";
                }
            }

            if (input.contains(",")) {
                // Ensure only one comma
                if (input.indexOf(',') != input.lastIndexOf(',')) {
                    return "";
                }
            }

            // Ensure valid numbers
            try {
                double value = Double.parseDouble(input.replace(",", "."));
                if (value > 100) {
                    return ""; // Prevent input if greater than 100
                }
            } catch (NumberFormatException nfe) {
                // Let invalid numbers show error through TextWatcher
            }

            return null; // Allow valid input
        };

        freqTv.setFilters(new InputFilter[]{filter});
    }


    //changes 8 oct
    public void onClick(View view) {
        // Stop blinking if currently blinking
        if (isBlinking) {
            stopBlinking();
        }

        if (view.equals(this.freqPlus)) {
            // Get the current frequency value and replace comma with dot for processing
            String charSequence = this.freqTv.getText().toString().replace(",", ".");

            if (!charSequence.equals("100.0")) {  // Max limit check
                String increasedValue = Utils.getIncreasedValue(charSequence);  // Increase the frequency

                // If the original input had a comma, replace the dot with a comma in the result
                if (this.freqTv.getText().toString().contains(",")) {
                    increasedValue = increasedValue.replace(".", ",");
                }

                // Set the updated value and move cursor to the end
                this.freqTv.setText(increasedValue);
                this.freqTv.setSelection(freqTv.getText().length());

                resetOtherButtons("");  // Reset other buttons if needed
            }

        } else if (view.equals(this.freqMinus)) {
            // Get the current frequency value and replace comma with dot for processing
            String charSequence = this.freqTv.getText().toString().replace(",", ".");

            if (!charSequence.equals("1.0")) {  // Min limit check
                String decreasedValue = Utils.getDecreasedValue(charSequence);  // Decrease the frequency

                // If the original input had a comma, replace the dot with a comma in the result
                if (this.freqTv.getText().toString().contains(",")) {
                    decreasedValue = decreasedValue.replace(".", ",");
                }

                // Set the updated value and move cursor to the end
                this.freqTv.setText(decreasedValue);
                this.freqTv.setSelection(freqTv.getText().length());

                resetOtherButtons("");  // Reset other buttons if needed
            }

        } else if (view.equals(this.txtBack)) {
            // Handle back button press
            finish();

        } else if (view.equals(this.images)) {
            finish();
        } else {
            Object tag = view.getTag();
            if (tag != null) {
                // Set the TextView with the value from the tag
                String obj = tag.toString();
                this.freqTv.setText(obj);
                resetOtherButtons(obj);
            } else {
                // Handle if no tag is assigned to the view (you can show a message or handle it as needed)
                // Toast.makeText(this, "No tag assigned to the view", Toast.LENGTH_SHORT).show();
            }
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
       this.images=(ImageView)findViewById(R.id.images);
        this.activity_vib_flash_start_btn=findViewById(R.id.activity_vib_flash_start_btn);
        this.image_vib=(ImageView)findViewById(R.id.image_vib);

        activity_mesh_info_iv=(TextView) findViewById(R.id.activity_mesh_info_iv);
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
        activity_vib_flash_start_btn.setText("Stop");


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
        activity_vib_flash_start_btn.setText("Start");
        activity_vib_flash_start_btn.setBackgroundColor(VibFlashActivity.this.getResources().getColor(R.color.header_backgrounds));

        try {
            cameraManager.setTorchMode(cameraId, false); // Ensure the flash is turned off
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // Adjust regex if needed
    }
    private void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    hideSoftInput(v);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }


}