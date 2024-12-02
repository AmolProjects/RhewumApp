package com.rhewumapp.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.rhewumapp.Activity.MeshConveterData.Utils;
import com.rhewumapp.Activity.VibcheckerGraph.PlotView;
import com.rhewumapp.Activity.database.RawDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.Activity.database.VibCheckerSummaryDao;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;
import com.rhewumapp.databinding.ActivityVibCheckerAccelerometer2Binding;

import org.jtransforms.fft.FloatFFT_1D;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VibCheckerAccelerometer2Activity extends DrawerBaseActivity {
    private SensorEventListener sensorEventListener;
    static private final int SAMPLING_PERIOD_MICROS = 10000;
    static private final int PLOT_BUFFER_MILLIS = 5000;
    private static final float SAMPLING_RATE = 1000.0f; // Example: 50 Hz
    private static final float NS2S = 1.0f / 1000000000.0f; // Nanoseconds to seconds
    // store senor data for x,y,z axis for the frequency
    private final ArrayList<Float> xData = new ArrayList<>();
    private final ArrayList<Float> yData = new ArrayList<>();
    private final ArrayList<Float> zData = new ArrayList<>();
    private final List<Float> xVelocity = new ArrayList<>();
    private final List<Float> yVelocity = new ArrayList<>();
    private final List<Float> zVelocity = new ArrayList<>();
    private final List<Float> xDisplacement = new ArrayList<>();
    private final List<Float> yDisplacement = new ArrayList<>();
    private final List<Float> zDisplacement = new ArrayList<>();
    TextView txtBack,txtResults, txt_fiveSecond, txt_zeroDelay,
            txt_onLpFilter,txt_Filter, txtZ, txtx, txty,
            txt_content, txtZeroDelay, txt_fivesSecond,
            txtArchive;
    LinearLayout llPlots;
    ImageView imgBack;
    Button bt_vib_start, bt_vib_reset;
    boolean startFlag = true;

    RhewumDbHelper dbHelper;
    // dominant frequency
    float xDominantFrequency;
    float yDominantFrequency;
    float zDominantFrequency;
    Bundle bundle;
    private Handler handler = new Handler(Looper.getMainLooper());
    private float[] buffer;
    private int indexInBuffer;
    private PlotView pvPlot;
    private boolean isSensorRunning = false;
    private CountDownTimer countdownTimer;
    // Variables to store max values
    private float maxX = Float.NEGATIVE_INFINITY;;
    private float maxY = Float.MIN_VALUE;
    private float maxZ = Float.MIN_VALUE;
    // frequency magnitude
    private List<Float> xMagnitudes = new ArrayList<>();
    private List<Float> yMagnitudes = new ArrayList<>();
    private List<Float> zMagnitudes = new ArrayList<>();
    private long lastTimestamp = 0;
    private boolean applyLowPassFilter = true; // Flag to toggle filter
    private boolean  zeroDelayFlag  = false; // Flag to toggle filter
    private Runnable stopSensorRunnable;
    private boolean applyZeros=false;
    private static final float ALPHA = 0.8f;
    // Variables to store the gravity and linear acceleration components
    private float[] gravity = new float[3];
    private ImageView imgDirection,image_forward;
    private Handler uiHandler;
    private HandlerThread handlerThread;
    private Handler backgroundHandler;
    ActivityVibCheckerAccelerometer2Binding activityVibCheckerAccelerometer2Binding;
    private long lastShakeTime = 0;
    private static final int SHAKE_THRESHOLD = 100; // Threshold for shake detection
    private int  delay = 5 ;
    private static final int SAMPLE_RATE = 50; // Number of samples per second
    private static final int BUFFER_SIZE = SAMPLE_RATE * 5; // 5 seconds of data
    private long lastUpdateTime = 0;
    String measurement_date;

    private static final float MIN_FREQUENCY = 5.0f;
    private static final float MAX_FREQUENCY = 70.0f;

    private int currentTimerValue = 5; // Initialize with the default timer value

    private float peakFrequencyX,peakFrequencyY,peakFrequencyZ,
            displacementAmplitudeX,displacementAmplitudeY,displacementAmplitudeZ;

    private List<RawDao> rawDataBuffer = new ArrayList<>();
    private boolean isSummarySaved = false;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        activityVibCheckerAccelerometer2Binding = ActivityVibCheckerAccelerometer2Binding.inflate(getLayoutInflater());
        setContentView(activityVibCheckerAccelerometer2Binding.getRoot());
        initObjects();
        initHandlers();
        initGUI();

        // click on the previous back screen
        imgBack.setOnClickListener(view ->{
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        });
        // click on the previous back screen
        txtBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        });

        // click on info image
        imgDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the custom dialog layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_image, null);

                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(VibCheckerAccelerometer2Activity.this);
                builder.setView(dialogView)
                        .setCancelable(true); // Makes the dialog dismissible

                // Display the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

       /* txtResults.setOnClickListener(view -> {
            if (startFlag) {
                hideTheUi();
                initGUI();

                if (zeroDelayFlag) {
                    // Start immediately if zero delay is selected
                    startCountdown();
                    startSensor();
                    resetMaxValues();
                    startFlag = false;
                    bt_vib_start.setText("Results");  // Reset button text
                } else {
                    // Start countdown before capturing data
                    new CountDownTimer(3000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            bt_vib_start.setText("Starting in: " + millisUntilFinished / 1000 + " sec");
                        }

                        public void onFinish() {
                            bt_vib_start.setText("Results");
                            startCountdown();
                            startSensor();
                            resetMaxValues();
                            startFlag = false;
                        }
                    }.start();
                }
            } else {
                nextToScreen();
            }

        });*/


        // click on zero delay secondui
        txt_zeroDelay.setOnClickListener(v -> {
            initGUI();
            zeroDelayFlag = !zeroDelayFlag;  // Toggle the flag
            if (zeroDelayFlag) {
                stopTimer();
                startFlag = true;
                stopSensor();

                resetData();
                bt_vib_start.setText(R.string.start);
                txt_zeroDelay.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this, R.color.header_backgrounds));
                bt_vib_start.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this, R.color.header_backgrounds));
            } else {
                txt_zeroDelay.setBackgroundResource(R.drawable.vibchecker_draw);
                stopTimer();
                startFlag = true;
                stopSensor();

                resetData();
                bt_vib_start.setText(R.string.start);
                bt_vib_start.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this, R.color.header_backgrounds));
            }

        });


        bt_vib_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startFlag) {
                    hideTheUi();
                    initGUI();

                    //aaded new
                    // Turn on Low Pass Filter by default
//                    applyLowPassFilter = true;
//                    updateLowPassFilterUI();

                    if (zeroDelayFlag) {
                        // Start immediately if zero delay is selected
                        startCountdown();
                        startSensor();
                        resetMaxValues();
                        startFlag = false;
                        bt_vib_start.setText("Results");  // Reset button text
                    } else {
                        // Start countdown before capturing data
                        new CountDownTimer(5000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                bt_vib_start.setText("Starting in: " + millisUntilFinished / 1000 + " sec");
                            }

                            public void onFinish() {
                                bt_vib_start.setText("Results");
                                startCountdown();
                                startSensor();
                                resetMaxValues();
                                startFlag = false;
                            }
                        }.start();
                    }
                } else {

                    // Stop and proceed to the next screen
                    if (countdownTimer != null) {
                        countdownTimer.cancel(); // Stop the timer
                        countdownTimer = null;
                    }
                    nextToScreen(); // Proceed to the next screen
                }

            }
        });

        // click on zero delay
        txt_zeroDelay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                initGUI();
                zeroDelayFlag = !zeroDelayFlag;  // Toggle the flag
//                applyZeros = !applyZeros;
                String messages = zeroDelayFlag ? "Delay\n0 sec " : "Delay\n5 sec ";
                SpannableString spannableMessage = new SpannableString(messages);

                // Make "ON" or "Off" bold based on the filter state
                if (zeroDelayFlag) {

                    delay = 0 ;
                    txt_zeroDelay.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this, R.color.header_backgrounds));
                    spannableMessage.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 4, // Bold "ON"
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                } else {
                    delay = 5;
                    txt_zeroDelay.setBackgroundResource(R.drawable.vibchecker_draw);
                    spannableMessage.setSpan(
                            new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                            0, 4, // Bold "Off"
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
                // Set the styled text to txt_Filter
                txtZeroDelay.setText(spannableMessage);
            }
        });


        image_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VibCheckerAccelerometer2Activity.this, VibChekerArchiveActivity.class);
                startActivity(intent);
            }
        });

        txtArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VibCheckerAccelerometer2Activity.this, VibChekerArchiveActivity.class);
                startActivity(intent);
            }
        });

        // click on onLpFilter
      /*  txt_onLpFilter.setOnClickListener(v -> {
            // Toggle the applyLowPassFilter state
            applyLowPassFilter = !applyLowPassFilter;

            // Define the message based on the filter state
            String message = applyLowPassFilter ? "Filter\nON" : "Filter\nOFF";
            SpannableString spannableMessage = new SpannableString(message);

            // Make "ON" or "Off" bold based on the filter state
            if (applyLowPassFilter) {

                txt_onLpFilter.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this, R.color.header_backgrounds));
                spannableMessage.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 2, // Bold "ON"
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE

                );
            } else {
                txt_onLpFilter.setBackgroundResource(R.drawable.vibchecker_draw);
                spannableMessage.setSpan(
                        new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                        0, 3, // Bold "Off"
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
            // Set the styled text to txt_Filter
            txt_Filter.setText(spannableMessage);


            // Show a toast message
            // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });*/

        //added new
        txt_onLpFilter.setOnClickListener(v -> {
           // Toggle filter state
            updateLowPassFilterUI(); // Update UI based on new state
        });


        // click on the reset button
        bt_vib_reset.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                stopTimer();
                startFlag = true;
                stopSensor();

                resetData();
                bt_vib_start.setText(R.string.start);

                bt_vib_start.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this, R.color.header_backgrounds));
                txt_zeroDelay.setBackgroundResource(R.drawable.vibchecker_draw);

                zeroDelayFlag = false;
                String messages = zeroDelayFlag ? "Delay\n0 sec " : "Delay\n5 sec ";
                SpannableString spannableDelayMessage = new SpannableString(messages);

                // Make "ON" or "Off" bold based on the filter state
                if (zeroDelayFlag) {

                    delay = 0;
                    txt_zeroDelay.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this, R.color.header_backgrounds));
                    spannableDelayMessage.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 4, // Bold "ON"
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                } else {
                    delay = 0;
                    txt_zeroDelay.setBackgroundResource(R.drawable.vibchecker_draw);
                    spannableDelayMessage.setSpan(
                            new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                            0, 4, // Bold "Off"
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
                // Set the styled text to txt_Filter
                txtZeroDelay.setText(spannableDelayMessage);

                // Turn off the low-pass filter
                applyLowPassFilter = false;
//                String message = applyLowPassFilter ? "Filter\nOFF" : "Filter\nON";
//                SpannableString spannableMessage = new SpannableString(message);
//                // Update the text and background of txt_onLpFilter to reflect the "off" state
//                txt_onLpFilter.setBackgroundResource(R.drawable.vibchecker_draw);
//                spannableMessage.setSpan(
//                        new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
//                        0, 3, // Bold "Off"
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                );
//                //
//                txt_Filter.setText(spannableMessage);
//                //  Toast.makeText(VibCheckerAccelerometer2Activity.this,"Reset data !!",Toast.LENGTH_SHORT).show();
//            }
                updateLowPassFilterUI();
            }
        });


    }

    // Helper method to update the Low Pass Filter UI
    private void updateLowPassFilterUI() {
        applyLowPassFilter = !applyLowPassFilter;
        String message = applyLowPassFilter ? "Filter\nON" : "Filter\nOFF";
        SpannableString spannableMessage = new SpannableString(message);

        if (applyLowPassFilter) {
//            txt_onLpFilter.setText("Filter ON");
            txt_onLpFilter.setBackgroundResource(R.drawable.vibchecker_draw);
            spannableMessage.setSpan(
                    new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    7, 9, // Bold "ON"
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        } else {
//            txt_onLpFilter.setText("Filter OFF");

            txt_onLpFilter.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this, R.color.header_backgrounds));
            spannableMessage.setSpan(
                    new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    7, 10, // Bold "OFF"
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        txt_Filter.setText(spannableMessage);
    }





    // initialise the object
    private void initObjects() {
        txtBack = findViewById(R.id.txtBack);
        dbHelper = new RhewumDbHelper(VibCheckerAccelerometer2Activity.this);
        txt_fiveSecond = findViewById(R.id.txt_fiveSecond);
        txtResults=findViewById(R.id.txtResults);
        txtZ = findViewById(R.id.txtZ);
        txtx = findViewById(R.id.txtx);
        txty = findViewById(R.id.txty);
        txt_content = findViewById(R.id.txt_content);
        txt_zeroDelay = findViewById(R.id.txt_zeroDelay);
        txt_onLpFilter = findViewById(R.id.txt_onLpFilter);
        txt_Filter=findViewById(R.id.txt_Filter);
        txt_fivesSecond = findViewById(R.id.txt_fivesSecond);
        imgBack = findViewById(R.id.imges_back);
        imgDirection=findViewById(R.id.img_direction);
       image_forward=findViewById(R.id.image_forward);
        txtZeroDelay = findViewById(R.id.txtZeroDelay);
        bt_vib_start = findViewById(R.id.bt_vib_start);
        bt_vib_reset = findViewById(R.id.bt_vib_reset);
        txtArchive = findViewById(R.id.txtArchive);
        txtx.setVisibility(View.INVISIBLE);
        txty.setVisibility(View.INVISIBLE);
        txtZ.setVisibility(View.INVISIBLE);

        bundle = new Bundle();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.header_backgrounds));
    }
    private void initHandlers() {
        uiHandler = new Handler(Looper.getMainLooper());
        handlerThread = new HandlerThread("SensorThread");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    private String takeScreenShot() {
        this.llPlots.setDrawingCacheEnabled(true);
        this.llPlots.buildDrawingCache(true);
        Bitmap createBitmap = Bitmap.createBitmap(this.llPlots.getDrawingCache());
        this.llPlots.setDrawingCacheEnabled(false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        createBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, 0);


    }


    // next to the accelerometer screen
    private void nextToScreen() {
        uiHandler.post(() -> {
            bt_vib_start.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this, R.color.header_backgrounds));
            bt_vib_start.setText(R.string.start);
        });
        // save the data for max acceleration x.....
        measurement_date = Utils.getCurrentDateTime();

//        uiHandler.post(() -> {
//           dbHelper.insertVibCheckerData(maxX,maxY,maxZ,peakFrequencyX,peakFrequencyY,peakFrequencyZ,displacementAmplitudeX,displacementAmplitudeY,displacementAmplitudeZ,currentTimerValue,delay,measurement_date);
//            isSummarySaved=true;
//           Log.d("TimerValue","currentTimerValue " +currentTimerValue);
//
//        });

        // Serialize the buffer
        byte[] serializedBuffer;
        if (buffer != null) {
            serializedBuffer = serializeBuffer(buffer);
            if (serializedBuffer == null) {
                Log.e("SerializationError", "Failed to serialize buffer");
            }
        } else {
            serializedBuffer = null;
            Log.e("BufferError", "Buffer is null");
        }
        //added 29Nov
        // Insert into database
        uiHandler.post(() -> {
            if (serializedBuffer != null) {
                dbHelper.insertVibCheckerData(
                        maxX, maxY, maxZ,
                        peakFrequencyX, peakFrequencyY, peakFrequencyZ,
                        displacementAmplitudeX, displacementAmplitudeY, displacementAmplitudeZ,
                        currentTimerValue, delay, measurement_date, serializedBuffer
                );
                isSummarySaved = true;
                Log.d("DBInsert", "Data inserted successfully with timer value: " + serializedBuffer.length);
            } else {
                Log.e("DBInsertError", "Serialized buffer is null, skipping database insert.");
            }
        });

        Intent intent = new Intent(VibCheckerAccelerometer2Activity.this, VibCheckerMainActivity.class);
        // send the data for acceleration
        intent.putExtra("accelerationMax_X", maxX);
        intent.putExtra("accelerationMax_Y", maxY);
        intent.putExtra("accelerationMax_Z", maxZ);

        //send data for amplititude
        intent.putExtra("displacementAmplitudeX", displacementAmplitudeX);
        intent.putExtra("displacementAmplitudeY", displacementAmplitudeY);
        intent.putExtra("displacementAmplitudeZ", displacementAmplitudeZ);


        intent.putExtra("sensor_data", buffer);
        Log.d("DEBUG", "Buffer values: " + Arrays.toString(buffer));

        intent.putExtra("timer",delay);
        intent.putExtra("measurement_date",measurement_date);
        Log.d("VibChecker", "Max X: " + maxX + " Max Y: " + maxY + " Max Z: " + maxZ + "Timer" +delay + "measurement_date" +measurement_date);
        // send the dominant frequency

        intent.putExtra("Frequency_X", peakFrequencyX);
        Log.d("DebugDebug", "IFrequency_X: " + peakFrequencyX );

        intent.putExtra("Frequency_Y", peakFrequencyY);
        Log.d("Debug", "IFrequency_Y: " + peakFrequencyY );

        intent.putExtra("Frequency_Z", peakFrequencyZ);
        Log.d("Debug", "IFrequency_Z: " + peakFrequencyZ );

        // send the frequency magnitude
        bundle.putSerializable("Frequency_xMagnitudes", (Serializable) xMagnitudes);
        bundle.putSerializable("Frequency_yMagnitudes", (Serializable) yMagnitudes);
        bundle.putSerializable("Frequency_zMagnitudes", (Serializable) zMagnitudes);

        // send the data for displacement
        bundle.putSerializable("displacement_dataX", (Serializable) xDisplacement);
        bundle.putSerializable("displacement_dataY", (Serializable) yDisplacement);
        bundle.putSerializable("displacement_dataZ", (Serializable) zDisplacement);
        intent.putExtra("BUNDLE", bundle);
        Log.d("VibChecker", "Max X: " + maxX + " Max Y: " + maxY + " Max Z: " + maxZ);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        startFlag = true;
    }

    // hide the ui screen text content ...etc
    private void hideTheUi() {
        uiHandler.post(() -> {
            txt_content.setVisibility(View.INVISIBLE);
            txtx.setVisibility(View.VISIBLE);
            txty.setVisibility(View.VISIBLE);
            txtZ.setVisibility(View.VISIBLE);
            bt_vib_start.setBackgroundColor(Color.RED);
            bt_vib_start.setText(R.string.results);
        });
    }

    // activity is destroy in the stack
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSensor();
        stopTimer();
        resetData();


    }

    // activity is resume state
    @Override
    protected void onResume() {
        super.onResume();
        xData.clear();
        yData.clear();
        zData.clear();
        xDisplacement.clear();
        yDisplacement.clear();
        zDisplacement.clear();
        // Toast.makeText(getApplicationContext(),"onResume Called",Toast.LENGTH_SHORT).show();

    }
    // activity is on pause state

    @Override
    protected void onPause() {
        super.onPause();
        stopSensor();
        stopTimer();
        resetData();
        // Toast.makeText(getApplicationContext(),"onPause Called",Toast.LENGTH_SHORT).show();

    }

    private void initGUI() {
        uiHandler.post(() -> {
            llPlots = findViewById(R.id.vibrationLineChart);
            llPlots.removeAllViews();

            // buffer will contain data of all 3 channels (x, y, z)
            buffer = new float[3 * PLOT_BUFFER_MILLIS * 1000 / SAMPLING_PERIOD_MICROS];
            indexInBuffer = 0;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            pvPlot = new PlotView(this);
            pvPlot.setBuffer(buffer);
            pvPlot.setNumChannels(3);
            pvPlot.setChannel(0); // Not used but kept for compatibility
            pvPlot.setBackgroundColor(0xFFFFFF);
            pvPlot.setColor(0, R.color.black); // black for X
            pvPlot.setColor(1, 0xFFFF0000); // Red for Y
            pvPlot.setColor(2, 0xFF0000FF); // Blue for Z
            pvPlot.setLineWidth(3);
            llPlots.addView(pvPlot, params);

            // Start a 5-second countdown to stop graph updates
            stopGraphAfterDelay(5000);  // 5000 milliseconds = 5 seconds
        });
    }

    private void stopGraphAfterDelay(int delayMillis) {
        new Handler().postDelayed(() -> {
            // Stop updating the graph after the delay
            if (pvPlot != null) {
                pvPlot.stopPlotting(); // This method will stop the graph from rendering
            }
        }, delayMillis); // Delay for 5 seconds
    }

    // start the sensor
    private void startSensor() {
        if (!isSensorRunning) {
            initSensor();
            // Duration to draw the graph in milliseconds
            // USER_TIME_DRAW_DURATION_MILLIS = 6000;
            stopSensorRunnable = this::stopSensor;
            // Duration to draw the graph in milliseconds
            int USER_TIME_DRAW_DURATION_MILLIS = 5000;
            handler.postDelayed(stopSensorRunnable, USER_TIME_DRAW_DURATION_MILLIS);
            isSensorRunning = true;

        }
    }

    // start the init sensor
    private void initSensor() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                backgroundHandler.post(() -> {
                    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        handleSensorChanged(event);

                    }
                });

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(sensorEventListener, accelerometer, SAMPLING_PERIOD_MICROS);
    }

    private void handleSensorChanged(SensorEvent event) {
        if (lastTimestamp == 0) {
            lastTimestamp = event.timestamp;
            lastUpdateTime = System.currentTimeMillis();

            // Initialize gravity with the first sensor values
            gravity[0] = event.values[0];
            gravity[1] = event.values[1];
            gravity[2] = event.values[2];
            return;
        }

        // Current time to track real time for plotting
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastUpdateTime;

        // Ensure the sensor data is processed at the SAMPLE_RATE frequency
        //change for 135 to 120
        if (elapsedTime < (1000 /120)) {
            return; // Skip if too soon since the last update
        }
        lastUpdateTime = currentTime;

        // Extract accelerometer values
        float ax = event.values[0];
        float ay = event.values[1];
        float az = event.values[2];
             Log.d("SensorValues","Sensor X " +ax +"Sensor Y " +ay +"Sensor Z" +az);

             //added to database
        // Add to buffer
        RawDao summaryDao = new RawDao();
        summaryDao.xRawValues = ax;
        summaryDao.yRawValues = ay;
        summaryDao.zRawValues = az;
        rawDataBuffer.add(summaryDao);

        // Insert only when buffer is full (e.g., every 100 entries)
        if (rawDataBuffer.size() >= 100) {
            try {
                dbHelper.getRawDao().callBatchTasks(() -> {
                    for (RawDao raw : rawDataBuffer) {
                        dbHelper.getRawDao().create(raw);
                    }
                    return null;
                });
                rawDataBuffer.clear();
                Log.d("DB_INSERT", "Batch data inserted successfully");
            } catch (Exception e) {
                Log.e("DB_INSERT", "Error inserting batch data", e);
            }
        }

        // Detect shake
        // detectShake(event.values[0], event.values[1], event.values[2]);

        // Apply low-pass filter if enabled
        if (applyLowPassFilter) {
            gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * ax;
            gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * ay;
            gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * az;
            ax -= gravity[0];
            ay -= gravity[1];
            az -= gravity[2];
        }

        //added new method
        // Subtract mean value from the data (step 2)
        // Validate and calculate mean only if buffers are non-empty
//        if (!xData.isEmpty() && !yData.isEmpty() && !zData.isEmpty()) {
//            float meanX = calculateMean(xData);
//            float meanY = calculateMean(yData);
//            float meanZ = calculateMean(zData);
//
//            ax -= meanX;
//            ay -= meanY;
//            az -= meanZ;

//            Log.d("MeanSubtraction", "Means: meanX=" + meanX + ", meanY=" + meanY + ", meanZ=" + meanZ);
//            Log.d("MeanSubtraction", "Normalized values: ax=" + ax + ", ay=" + ay + ", az=" + az);
//        } else {
//            Log.w("MeanSubtraction", "Skipping mean calculation: Data buffers are empty.");
//        }



        // Add sensor data to the buffer (circular buffer)
        if (xData.size() >= 156) {
            xData.remove(0); // Remove oldest data if buffer is full
            yData.remove(0);
            zData.remove(0);
        }

        xData.add(ax);
        yData.add(ay);
        zData.add(az);

        // updateDirection(ax, ay, az);

        // Call your method to update the UI with the new data
        onSensorData(ax, ay, az);
        uiHandler.post(() -> pvPlot.invalidate()); // Redraw the plot

        // Process FFT more frequently
        if (xData.size() > 56) {
            processFFT();  // Process FFT regularly while collecting data
        }


        //for psd
//        // Update last timestamp for calculating velocity and displacement (optional)
        float dt = (event.timestamp - lastTimestamp) * NS2S; // Time difference in seconds
        lastTimestamp = event.timestamp;

        // Optional: Calculate velocity and displacement here if needed
        float vx = (xVelocity.isEmpty() ? 0 : xVelocity.get(xVelocity.size() - 1)) + ax * dt;
        float vy = (yVelocity.isEmpty() ? 0 : yVelocity.get(yVelocity.size() - 1)) + ay * dt;
        float vz = (zVelocity.isEmpty() ? 0 : zVelocity.get(zVelocity.size() - 1)) + az * dt;

        xVelocity.add(vx);
        yVelocity.add(vy);
        zVelocity.add(vz);

        // Integrate velocity to get displacement
        float dx = (xDisplacement.isEmpty() ? 0 : xDisplacement.get(xDisplacement.size() - 1)) + vx * dt;
        float dy = (yDisplacement.isEmpty() ? 0 : yDisplacement.get(yDisplacement.size() - 1)) + vy * dt;
        float dz = (zDisplacement.isEmpty() ? 0 : zDisplacement.get(zDisplacement.size() - 1)) + vz * dt;

        xDisplacement.add(dx);
        yDisplacement.add(dy);
        zDisplacement.add(dz);
    }

    //added new method
    // Calculate mean of the data
    public float calculateMean(List<Float> data) {
        if (data == null || data.isEmpty()) {
            return 0; // Handle empty list gracefully
        }
        float sum = 0;
        for (float value : data) {
            sum += value;
        }
        return sum / data.size();
    }



    // stop the sensor
    private void stopSensor() {
        if (isSensorRunning) {
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(sensorEventListener);
            // handlerThread.quitSafely();
            isSensorRunning = false;
        }
    }


    private float calculateFrequencyFromPeakAcceleration(float peakAcceleration) {
        final float g = 9.81f; // Acceleration due to gravity in m/s²
        return peakAcceleration / g; // Frequency in Hz
    }

    //You need the fft calculation to determine the peak
    //frequency and the amplitude/displacement
    //fourier transform
//    public void processFFT() {
//        float[] xArray = convertListToArray(xData);
//        float[] yArray = convertListToArray(yData);
//        float[] zArray = convertListToArray(zData);
//        FFTProcessor fftProcessor = new FFTProcessor();
//
//        //added new method
//        // Apply Hanning window to data
//        applyHanningWindow(xArray);
//        applyHanningWindow(yArray);
//        applyHanningWindow(zArray);
//
//        // Calculate magnitudes for the frequency
////        xMagnitudes = fftProcessor.processFFT(xArray);
////        yMagnitudes = fftProcessor.processFFT(yArray);
////        zMagnitudes = fftProcessor.processFFT(zArray);
//
//        //added new2
//        // Perform FFT and convert List<Float> to ArrayList<Float>
//        ArrayList<Float> xMagnitudes = new ArrayList<>(fftProcessor.processFFT(xArray));
//        ArrayList<Float> yMagnitudes = new ArrayList<>(fftProcessor.processFFT(yArray));
//        ArrayList<Float> zMagnitudes = new ArrayList<>(fftProcessor.processFFT(zArray));
//
//    //added new2
//        // Convert ArrayList<Float> to float[]
//        // Convert ArrayList<Float> to float[]
//        float[] xMagnitudesArray = new float[xMagnitudes.size()];
//        for (int i = 0; i < xMagnitudes.size(); i++) {
//            xMagnitudesArray[i] = xMagnitudes.get(i);
//        }
//
//        float[] yMagnitudesArray = new float[yMagnitudes.size()];
//        for (int i = 0; i < yMagnitudes.size(); i++) {
//            yMagnitudesArray[i] = yMagnitudes.get(i);
//        }
//
//        float[] zMagnitudesArray = new float[zMagnitudes.size()];
//        for (int i = 0; i < zMagnitudes.size(); i++) {
//            zMagnitudesArray[i] = zMagnitudes.get(i);
//        }
//
//
//        //added new 2
//        // Calculate timestep
////        float timeStep = 1.0f / SAMPLING_RATE; // Assumes uniform sampling interval
//        //added new 2
//        // Calculate frequency array
////        int N = xMagnitudesArray.length / 2 + 1;
//        int N = xMagnitudesArray.length ;
//        if (N == 0) return; // Avoid processing if there's no data
//        //added for crash
//        int N_half = N / 2 + 1;  // Half the size for real-valued FFT result
//        float[] frequencies = new float[N];
//        if (N_half > 0)
//
//            for (int i = 0; i < N_half; i++) {
//                frequencies[i] = i * (1.0f / SAMPLING_RATE) / N; // Frequency bins
//            }
//
////        float[] frequencies = new float[N_half];
////
////        for (int i = 0; i < N_half; i++) {
//////            frequencies[i] = i * (1.0f / timeStep) / (2 * N); // Equivalent to linspace
////            frequencies[i] = i * (1.0f / timeStep) / N; // Frequency bins
////        }
//
////        // Calculate dominant frequencies from FFT magnitudes
////        xDominantFrequency = fftProcessor.getDominantFrequency(xMagnitudes, SAMPLING_RATE) / 10.0f;
////        Log.d("VibChecker", "xDominantFrequencyFFT: " + xDominantFrequency);
////        yDominantFrequency = fftProcessor.getDominantFrequency(yMagnitudes, SAMPLING_RATE) / 10.0f;
////        Log.d("VibChecker", "yDominantFrequencyFFT: " + yDominantFrequency);
////        zDominantFrequency = fftProcessor.getDominantFrequency(zMagnitudes, SAMPLING_RATE) / 10.0f;
////        Log.d("VibChecker", "zDominantFrequencyFFT: " + zDominantFrequency);
//
//        //added new method
//        // Filter out frequencies outside 5-70 Hz range
////        filterFrequencies(xMagnitudes);
////        filterFrequencies(yMagnitudes);
////        filterFrequencies(zMagnitudes);
//
//        //added new 2
//        // Apply frequency filter (5–70 Hz)
//        // Apply frequency filter on the magnitudes
//        applyFrequencyFilter(xMagnitudesArray, frequencies, N_half);
//        applyFrequencyFilter(yMagnitudesArray, frequencies, N_half);
//        applyFrequencyFilter(zMagnitudesArray, frequencies, N_half);
//
//        // Calculate peak accelerations
//        float peakX = getMaxAcceleration(xArray);
//        Log.d("VibChecker", "Peak X: " + peakX + " Hz");
//        float peakY = getMaxAcceleration(yArray);
//        Log.d("VibChecker", "Peak Y: " + peakY + " Hz");
//
//        float peakZ = getMaxAcceleration(zArray);
//        Log.d("VibChecker", "Peak z: " + peakZ + " Hz");
//        // Calculate frequencies from peak accelerations
//        float frequencyFromPeakX = calculateFrequencyFromPeakAcceleration(peakX);
//        float frequencyFromPeakY = calculateFrequencyFromPeakAcceleration(peakY);
//        float frequencyFromPeakZ = calculateFrequencyFromPeakAcceleration(peakZ);
//
//        // Log the frequencies calculated from peak accelerations
//        Log.d("VibChecker", "Frequency from Peak X: " + frequencyFromPeakX + " Hz");
//        Log.d("VibChecker", "Frequency from Peak Y: " + frequencyFromPeakY + " Hz");
//        Log.d("VibChecker", "Frequency from Peak Z: " + frequencyFromPeakZ + " Hz");
//
//        //added new2
//        // Calculate maximum frequency and amplitude
//        float maxAmplitudeX = getMaxAmplitude(xMagnitudesArray);
//        float maxAmplitudeY = getMaxAmplitude(yMagnitudesArray);
//        float maxAmplitudeZ = getMaxAmplitude(zMagnitudesArray);
//
//        //added fro crash
//        // Safely get the peak frequency by checking array bounds
//        int maxIndexX = getMaxIndex(xMagnitudesArray);
//        Log.d("Debug", "maxIndexX: " + maxIndexX + ", N_half: " + N_half + ", Frequencies Length: " + frequencies.length);
//
//        int maxIndexY = getMaxIndex(yMagnitudesArray);
//        int maxIndexZ = getMaxIndex(zMagnitudesArray);
//
//        // Make sure the index is within bounds of the array
//// Validate maxIndexX against N_half
//        if (maxIndexX >= 0 && maxIndexX < N) {
//            float peakFrequencyX = frequencies[maxIndexX];
//            Log.d("Debug", "Peak Frequency X: " + peakFrequencyX + " Hz");
//
//            // Check maxAmplitudeX
//            Log.d("Debug", "Max Amplitude X: " + maxAmplitudeX);
//
//            // Calculate displacement amplitude
//            float displacementAmplitudeX = calculateDisplacementAmplitude(maxAmplitudeX, peakFrequencyX);
//            Log.d("Debug", "Displacement Amplitude X: " + displacementAmplitudeX + " mm");
//        } else {
//            Log.e("Error", "maxIndexX out of bounds. maxIndexX: " + maxIndexX + ", N_half: " + N_half);
//        }
//
//        if (maxIndexY < N_half) {
//            float peakFrequencyY = frequencies[maxIndexY];
//            float displacementAmplitudeY = calculateDisplacementAmplitude(maxAmplitudeY, peakFrequencyY);
//            Log.d("VibChecker", "Peak Frequency Y: " + peakFrequencyY + " Hz, Displacement Amplitude Y: " + displacementAmplitudeY + " mm");
//        }
//
//        if (maxIndexZ < N_half) {
//            float peakFrequencyZ = frequencies[maxIndexZ];
//            float displacementAmplitudeZ = calculateDisplacementAmplitude(maxAmplitudeZ, peakFrequencyZ);
//            Log.d("VibChecker", "Peak Frequency Z: " + peakFrequencyZ + " Hz, Displacement Amplitude Z: " + displacementAmplitudeZ + " mm");
//
//        }
//
//
//        //
//
////        float peakFrequencyX = frequencies[getMaxIndex(xMagnitudesArray)];
////        float peakFrequencyY = frequencies[getMaxIndex(yMagnitudesArray)];
////        float peakFrequencyZ = frequencies[getMaxIndex(zMagnitudesArray)];
//
//        // Log the maximum frequencies and amplitudes
////        Log.d("VibChecker", "Max Frequency X: " + peakFrequencyX + " Hz, Amplitude: " + maxAmplitudeX);
////        Log.d("VibChecker", "Max Frequency Y: " + peakFrequencyY + " Hz, Amplitude: " + maxAmplitudeY);
////        Log.d("VibChecker", "Max Frequency Z: " + peakFrequencyZ + " Hz, Amplitude: " + maxAmplitudeZ);
//
//
//        //added new2
//        // Apply interpolation to achieve equal time-step spacing
////        interpolateAccelerationData(xData, yData, zData);
//    }

    public void processFFT() {
        float[] xArray = convertListToArray(xData);
        float[] yArray = convertListToArray(yData);
        float[] zArray = convertListToArray(zData);
        FFTProcessor fftProcessor = new FFTProcessor();

        // Apply Hanning window to data
        applyHanningWindow(xArray);
        applyHanningWindow(yArray);
        applyHanningWindow(zArray);

        xMagnitudes = fftProcessor.processFFT(xArray);
        yMagnitudes = fftProcessor.processFFT(yArray);
          zMagnitudes = fftProcessor.processFFT(zArray);

        // Perform FFT and convert List<Float> to ArrayList<Float>
        ArrayList<Float> xMagnitudes = new ArrayList<>(fftProcessor.processFFT(xArray));
        ArrayList<Float> yMagnitudes = new ArrayList<>(fftProcessor.processFFT(yArray));
        ArrayList<Float> zMagnitudes = new ArrayList<>(fftProcessor.processFFT(zArray));

        // Convert ArrayList<Float> to float[]
        float[] xMagnitudesArray = convertToArray(xMagnitudes);
        Log.d("Debug", "xMagnitudesArray: " + Arrays.toString(xMagnitudesArray));

        float[] yMagnitudesArray = convertToArray(yMagnitudes);
        float[] zMagnitudesArray = convertToArray(zMagnitudes);

        int N = xMagnitudesArray.length;
        if (N == 0) {
            Log.e("Error", "No data for FFT processing.");
            return;
        }

        // Half the size for real-valued FFT result
        int N_half = N / 2 + 1;
        float[] frequencies = new float[N_half];


        for (int i = 0; i < N_half; i++) {
            frequencies[i] = i * (SAMPLING_RATE / (float) N);
        }
        Log.d("Debug", "Frequencies: " + Arrays.toString(frequencies));


        // Apply frequency filter (5–70 Hz)
        applyFrequencyFilter(xMagnitudesArray, frequencies, N_half);
        applyFrequencyFilter(yMagnitudesArray, frequencies, N_half);
        applyFrequencyFilter(zMagnitudesArray, frequencies, N_half);

        // Safely calculate peak values and amplitudes
         peakFrequencyX = calculatePeakFrequency(xMagnitudesArray, frequencies, N_half, "X");
         peakFrequencyY = calculatePeakFrequency(yMagnitudesArray, frequencies, N_half, "Y");
         peakFrequencyZ = calculatePeakFrequency(zMagnitudesArray, frequencies, N_half, "Z");

        // Prevent overwriting peakFrequencyX
        if (peakFrequencyX > 0) {
            float maxAmplitudeX = getMaxAmplitude(xMagnitudesArray);
             displacementAmplitudeX = calculateDisplacementAmplitude(maxAmplitudeX, peakFrequencyX);
            Log.d("Debug", "Peak Frequency X: " + peakFrequencyX + " Hz, Displacement Amplitude X: " + displacementAmplitudeX + " mm");
        } else {
            Log.e("Error", "Invalid Peak Frequency X: " + peakFrequencyX + ". Ensure data is correct.");
        }

        if (peakFrequencyY > 0) {
            float maxAmplitudeY = getMaxAmplitude(yMagnitudesArray);
             displacementAmplitudeY = calculateDisplacementAmplitude(maxAmplitudeY, peakFrequencyY);
            Log.d("VibChecker", "Peak Frequency Y: " + peakFrequencyY + " Hz, Displacement Amplitude Y: " + displacementAmplitudeY + " mm");
        }

        if (peakFrequencyZ > 0) {
            float maxAmplitudeZ = getMaxAmplitude(zMagnitudesArray);
             displacementAmplitudeZ = calculateDisplacementAmplitude(maxAmplitudeZ, peakFrequencyZ);
            Log.d("VibChecker", "Peak Frequency Z: " + peakFrequencyZ + " Hz, Displacement Amplitude Z: " + displacementAmplitudeZ + " mm");
        }
    }

    /**
     * Calculate the peak frequency and log details, preventing overwriting.
     */
    private float calculatePeakFrequency(float[] magnitudes, float[] frequencies, int N_half, String axis) {
        int maxIndex = getMaxIndex(magnitudes);

        // Validate maxIndex
        if (maxIndex >= 0 && maxIndex < N_half) {
            float peakFrequency = frequencies[maxIndex];
            Log.d("Debug", "Peak Frequency " + axis + ": " + peakFrequency + " Hz");
            return peakFrequency;
        } else {
            Log.e("Error", "Invalid maxIndex for " + axis + ": " + maxIndex + ", N_half: " + N_half);
            return 0; // Return 0 instead of -1 for clarity
        }
    }


    /**
     * Convert ArrayList<Float> to float[].
     */
    private float[] convertToArray(ArrayList<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }




    //added new2
//    private void interpolateAccelerationData(ArrayList<Float> xData, ArrayList<Float> yData, ArrayList<Float> zData) {
//        float[] Timems = new float[xData.size()];
//        float[] accelo_x = new float[xData.size()];
//        float[] accelo_y = new float[yData.size()];
//        float[] accelo_z = new float[zData.size()];
//
//        // Convert ArrayLists to arrays for interpolation
//        for (int i = 0; i < xData.size(); i++) {
//            Timems[i] = i * (1.0f / SAMPLING_RATE);
//            accelo_x[i] = xData.get(i);
//            accelo_y[i] = yData.get(i);
//            accelo_z[i] = zData.get(i);
//        }
//
//        // Interpolation (Spline Interpolation)
//        int numNewPoints = 8192;
//        float[] Timems_new = new float[numNewPoints];
//
//        for (int i = 0; i < numNewPoints; i++) {
//            Timems_new[i] = (i + 1) * (60.0f / numNewPoints); // New time step
//        }
//
//        // Spline interpolation
//        InterpolatedUnivariateSpline fx = new InterpolatedUnivariateSpline(Timems, accelo_x, 3);
//        float[] accelo_x_new = fx(Timems_new);
//
//        // Similarly interpolate for yData and zData
//        InterpolatedUnivariateSpline fy = new InterpolatedUnivariateSpline(Timems, accelo_y, 3);
//        float[] accelo_y_new = fy(Timems_new);
//
//        InterpolatedUnivariateSpline fz = new InterpolatedUnivariateSpline(Timems, accelo_z, 3);
//        float[] accelo_z_new = fz(Timems_new);
//
//        // Update xData, yData, zData with interpolated values (optionally)
//    }


    //added new2
    private int getMaxIndex2(float[] magnitudes) {
        int maxIndex = 0;
        for (int i = 1; i < magnitudes.length; i++) {
            if (magnitudes[i] > magnitudes[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    //added fro crash
    private int getMaxIndex(float[] magnitudes) {
        int maxIndex = 0;
        float maxValue = magnitudes[0];
        for (int i = 1; i < magnitudes.length; i++) {
            if (magnitudes[i] > maxValue) {
                maxValue = magnitudes[i];
                maxIndex = i;
            }
        }
        return maxIndex;

    }


    //added new2
    /*The function uses the formula derived from physics for sinusoidal motion:
    A=a(2πf)2
    A=(2πf)2a​

    Where:
    AA is the displacement amplitude (in meters, converted to mm in the code).
    aa is the peak acceleration (in m/s²).
    ff is the frequency (in Hz).
            2πf2πf is the angular velocity (ωω).*/
    private float calculateDisplacementAmplitude(float maxAmplitude, float frequency) {
        if (frequency > 0) { // Avoid division by zero
            return (maxAmplitude / (float) Math.pow(2 * Math.PI * frequency, 2)) * 1000; // In mm
        }
        return 0;
    }

    //added new2
    private float getMaxAmplitude(float[] magnitudes) {
        float maxAmplitude = 0;
        for (float magnitude : magnitudes) {
            maxAmplitude = Math.max(maxAmplitude, magnitude);
        }
        return maxAmplitude;
    }

    //added new2
    private void applyFrequencyFilter(float[]magnitudes, float[] frequencies, int N) {
        for (int i = 0; i < N; i++) {
            if (frequencies[i] < 5 || frequencies[i] > 70) {
                magnitudes[i] = 0; // Set magnitudes outside the range to 0
            }
        }
    }

    //added new2
    // Helper method to convert ArrayList<Float> to float[]
    private float[] convertListToFloatArray(ArrayList<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }


    //added new method
    // Filter frequencies outside the 5-70 Hz range (step 5)
    private void filterFrequencies(List<Float> magnitudes) {
        int n = magnitudes.size();
        for (int i = 0; i < n; i++) {
            float frequency = i * SAMPLING_RATE / n;
            if (frequency < MIN_FREQUENCY || frequency > MAX_FREQUENCY) {
                magnitudes.set(i, 0f); // Set frequencies outside range to 0
            }
        }
    }

    //added new method
    // Apply Hanning window to the data (step 4)
    private void applyHanningWindow(float[] data) {
        int n = data.length;
        for (int i = 0; i < n; i++) {
            data[i] *= 0.5 * (1 - Math.cos(2 * Math.PI * i / (n - 1))); // Hanning window
        }
    }

    // Method to get the maximum acceleration from an array
    private float getMaxAcceleration(float[] array) {
        float max = 0;
        for (float value : array) {
            if (Math.abs(value) > max) {
                max = Math.abs(value);
            }
        }
        return max;
    }


    //     reset the sensor and data
    private void resetData() {
        clearBuffer();

        // Reset the buffer
        buffer = new float[3 * PLOT_BUFFER_MILLIS * 1000 / SAMPLING_PERIOD_MICROS];
        indexInBuffer = 0;

        // Clear the plot view if pvPlot is initialized
        if (pvPlot != null) {
            pvPlot.setBuffer(buffer);
            pvPlot.postInvalidate();
        } else {
            Log.w("VibCheckerAccelerometer2Activity", "pvPlot is null in resetData");
        }

        // Remove any pending stop sensor callbacks
        handler.removeCallbacks(stopSensorRunnable);

        // Ensure sensor is stopped
        stopSensor();
    }

    private void clearBuffer() {
        if (buffer != null) {
            System.out.println("Before clearing: " + Arrays.toString(buffer));
            Arrays.fill(buffer, 0.0f);
            System.out.println("After clearing: " + Arrays.toString(buffer));
        }
    }

    private void onSensorData(float x, float y, float z) {
        Log.d("SensorData", "X: " + x + ", Y: " + y + ", Z: " + z);
        // Update max values
        if (x > maxX) maxX = x;
        if (y > maxY) maxY = y;
        if (z > maxZ) maxZ = z;
        // plots accept +/- 1.0 range
        // so we scale to have the displayed range of +/- 10 m/s²:
        x /= 10;
        y /= 10;
        z /= 10;



        // Only update the buffer if there's space
        if (indexInBuffer < buffer.length) {
            buffer[indexInBuffer++] = x;
            buffer[indexInBuffer++] = y;
            buffer[indexInBuffer++] = z;
        } else {
            // Optionally, handle the situation when buffer is full
            indexInBuffer = 0; // Reset or use circular logic
        }
        //pvPlot.postInvalidate();
        // Ensure index wraps around
        indexInBuffer %= buffer.length;

        // Notify the UI thread to update the plot
        uiHandler.post(() -> pvPlot.invalidate());
    }

    private void startCountdown() {
        // Stop any existing timer
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null; // Nullify the countdownTimer after canceling
        }
        countdownTimer = new CountDownTimer(5000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                String timer;
                currentTimerValue=(int) (millisUntilFinished / 1000); // Track remaining time
                // uiHandler.post(() -> txt_fivesSecond.setText(Html.fromHtml(String.valueOf("Timer"+"\n"+millisUntilFinished / 1000))));
                uiHandler.post(() -> txt_fivesSecond.setText(Html.fromHtml("Timer<br>" + millisUntilFinished / 1000)+ " sec"));
                ;

            }

            @Override
            public void onFinish() {
                // txt_fivesSecond.setText("0s\ntime");
                currentTimerValue = 5; // Timer finished
                txt_fivesSecond.setText("Timer\n0 sec");
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.cancel(); // Stop the timer
        }
        txt_fivesSecond.setText("Timer\n5 sec");
    }

    private void resetMaxValues() {
        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;
        maxZ = Float.MIN_VALUE;
    }

    private float[] convertListToArray(ArrayList<Float> dataList) {
        float[] dataArray = new float[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i] = dataList.get(i);
        }
        return dataArray;
    }

    public static class FFTProcessor {
        public List<Float> processFFT(float[] vectors) {
            int length = vectors.length;
            float[] input = new float[length * 2]; // Allocate space for complex numbers

            // Fill the real part and set the imaginary part to 0
            for (int i = 0; i < length; i++) {
                input[2 * i] = vectors[i]; // Real part
                input[2 * i + 1] = 0;      // Imaginary part
            }

            // Perform FFT
            FloatFFT_1D fftlib = new FloatFFT_1D(length);
            fftlib.complexForward(input);

            // Calculate magnitudes
            float[] outputData = new float[length / 2 + 1]; // Magnitudes array
            for (int i = 0; i < length / 2 + 1; i++) {
                outputData[i] = (float) Math.sqrt(input[2 * i] * input[2 * i] + input[2 * i + 1] * input[2 * i + 1]);
            }

            // Convert to List<Float> for easier use in Android
            List<Float> output = new ArrayList<>();
            for (float f : outputData) {
                output.add(f);
            }

            return output;
        }

        public float getDominantFrequency(List<Float> magnitudes, float samplingRate) {
            int maxIndex = 0;
            float maxMagnitude = -1;
            for (int i = 0; i < magnitudes.size(); i++) {
                if (magnitudes.get(i) > maxMagnitude) {
                    maxMagnitude = magnitudes.get(i);
                    maxIndex = i;
                }
            }

            // Calculate the dominant frequency
            return maxIndex * samplingRate / magnitudes.size();
            // Corrected formula to map index to frequency
//            return maxIndex * samplingRate / (2 * (magnitudes.size() - 1));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //added new 29Nov
    //added for sending data to
    private byte[] serializeBuffer(float[] buffer) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(buffer);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}