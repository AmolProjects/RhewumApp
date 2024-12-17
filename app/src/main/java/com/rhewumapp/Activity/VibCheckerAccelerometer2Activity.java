package com.rhewumapp.Activity;

import static com.rhewumapp.Activity.MeshConveterData.Utils.getCurrentDateTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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

import com.rhewumapp.Activity.VibcheckerGraph.PlotView;
import com.rhewumapp.Activity.database.RawSensorDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;
import com.rhewumapp.databinding.ActivityVibCheckerAccelerometer2Binding;

import org.jtransforms.fft.DoubleFFT_1D;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VibCheckerAccelerometer2Activity extends DrawerBaseActivity {
    private SensorEventListener sensorEventListener;
    static private final int SAMPLING_PERIOD_MICROS = 10000;
    static private final int PLOT_BUFFER_MILLIS = 5000;
    private static final float SAMPLING_RATE = 1000.0f; // Example: 50 Hz
    private static final float NS2S = 1.0f / 1000000000.0f; // Nanoseconds to seconds

    // Declare a List to store sensor data
    private List<double[]> sensorData = new ArrayList<>();
    private static final int DATA_BATCH_SIZE = 15; // Set a size threshold to process

    // store senor data for x,y,z axis for the frequency

    private static final String PREF_NAME = "CounterPrefs";
    private static final String COUNTER_KEY = "counter";
    private SharedPreferences sharedPreferences;
    private int counter;

    /*private final List<Float> xAxis = new ArrayList<>();
    private final List<Float> yAxis = new ArrayList<>();
    private final List<Float> zAxis = new ArrayList();*/
    private final List<Double> timeStamps = new ArrayList<>();


    private final List<Float> xDisplacement = new ArrayList<>();
    private final List<Float> yDisplacement = new ArrayList<>();
    private final List<Float> zDisplacement = new ArrayList<>();
    private long lastInsertTime = 0; // Track last insert time
    TextView txtBack, txtResults, txt_fiveSecond, txt_zeroDelay,
            txt_onLpFilter, txt_Filter, txtZ, txtx, txty,
            txt_content, txtZeroDelay, txt_fivesSecond,
            txtArchive;
    LinearLayout llPlots;
    ImageView imgBack;
    Button bt_vib_start, bt_vib_reset;
    boolean startFlag = true;
    RhewumDbHelper dbHelper;
    Bundle bundle;
    private Handler handler = new Handler(Looper.getMainLooper());
    private float[] buffer;
    private int indexInBuffer;
    private PlotView pvPlot;
    private boolean isSensorRunning = false;
    private CountDownTimer countdownTimer;
    // Variables to store max values
    private float maxX = Float.MIN_VALUE;
    ;
    private float maxY = Float.MIN_VALUE;
    private float maxZ = Float.MIN_VALUE;
    // frequency magnitude
    private final List<Float> xMagnitudes = new ArrayList<Float>();
    private final List<Float> yMagnitudes = new ArrayList<Float>();
    private final List<Float> zMagnitudes = new ArrayList<Float>();
    private long lastTimestamp = 0;
    private boolean applyLowPassFilter = true; // Flag to toggle filter
    private boolean zeroDelayFlag = false; // Flag to toggle filter
    private Runnable stopSensorRunnable;
    private boolean applyZeros = false;
    private static final float ALPHA = 0.8f;
    // Variables to store the gravity and linear acceleration components
    private final float[] gravity = new float[3];
    private ImageView imgDirection, image_forward;
    private Handler uiHandler;
    private HandlerThread handlerThread;
    float ax, ay, az;
    long timeInterval;
    private Handler backgroundHandler;
    ActivityVibCheckerAccelerometer2Binding activityVibCheckerAccelerometer2Binding;
    private int delay = 5;
    private static final int SAMPLE_RATE = 50; // Number of samples per second
    private static final int BUFFER_SIZE = SAMPLE_RATE * 5; // 5 seconds of data
    private long lastUpdateTime = 0;
    String measurement_date;


    private int currentTimerValue = 5; // Initialize with the default timer value
    float meanAccelerationX, meanAccelerationY, meanAccelerationZ;
    float peakFrequencysX, peakFrequencysY, peakFrequencysZ,maxXFrequency,maxYFrequency,maxZFrequency,displacementAmplitudesX, displacementAmplitudesY, displacementAmplitudesZ;


    private boolean isSummarySaved = false;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        activityVibCheckerAccelerometer2Binding = ActivityVibCheckerAccelerometer2Binding.inflate(getLayoutInflater());
        setContentView(activityVibCheckerAccelerometer2Binding.getRoot());
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        // Load counter value
        counter = sharedPreferences.getInt(COUNTER_KEY, 0);

        initObjects();
        initHandlers();
        initGUI();

        // click on the previous back screen
        imgBack.setOnClickListener(view -> {
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


        bt_vib_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startFlag) {
                    // Use SavedStateViewModelFactory to provide the ViewModel
                    hideTheUi();
                    initGUI();
                    counter++;
                    Log.e("Counter", "counter is:::::::::" + counter);
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
                onPause();

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

                    delay = 0;
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

        //added new
        txt_onLpFilter.setOnClickListener(v -> {
            // Toggle filter state
            updateLowPassFilterUI(); // Update UI based on new state
        });


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
        // Get ViewModel
        txtBack = findViewById(R.id.txtBack);
        dbHelper = new RhewumDbHelper(VibCheckerAccelerometer2Activity.this);
        txt_fiveSecond = findViewById(R.id.txt_fiveSecond);
        txtResults = findViewById(R.id.txtResults);
        txtZ = findViewById(R.id.txtZ);
        txtx = findViewById(R.id.txtx);
        txty = findViewById(R.id.txty);
        txt_content = findViewById(R.id.txt_content);
        txt_zeroDelay = findViewById(R.id.txt_zeroDelay);
        txt_onLpFilter = findViewById(R.id.txt_onLpFilter);
        txt_Filter = findViewById(R.id.txt_Filter);
        txt_fivesSecond = findViewById(R.id.txt_fivesSecond);
        imgBack = findViewById(R.id.imges_back);
        imgDirection = findViewById(R.id.img_direction);
        image_forward = findViewById(R.id.image_forward);
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

    // next to the accelerometer screen
    private void nextToScreen() {
        uiHandler.post(() -> {
            bt_vib_start.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this, R.color.header_backgrounds));
            bt_vib_start.setText(R.string.start);
        });

        // save the data for max acceleration x.....
        measurement_date = getCurrentDateTime();
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
                dbHelper.insertVibCheckerData(meanAccelerationX, meanAccelerationY, meanAccelerationZ, maxXFrequency, maxYFrequency, maxZFrequency,
                        displacementAmplitudesX, displacementAmplitudesY, displacementAmplitudesZ,
                        currentTimerValue, delay, measurement_date, serializedBuffer, new RawSensorDao());
                isSummarySaved = true;
                Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
                Log.d("DBInsert", "Data inserted successfully with timer value: " + serializedBuffer.length);
            } else {
                Log.e("DBInsertError", "Serialized buffer is null, skipping database insert.");
            }
        });

        Intent intent = new Intent(VibCheckerAccelerometer2Activity.this, VibCheckerMainActivity.class);
        // send the data for acceleration
        intent.putExtra("accelerationMax_X", meanAccelerationX);
        intent.putExtra("accelerationMax_Y", meanAccelerationY);
        intent.putExtra("accelerationMax_Z", meanAccelerationZ);

        //send data for amplititude
        intent.putExtra("displacementAmplitudeX", displacementAmplitudesX);
        intent.putExtra("displacementAmplitudeY", displacementAmplitudesY);
        intent.putExtra("displacementAmplitudeZ", displacementAmplitudesZ);


        intent.putExtra("sensor_data", buffer);
        //  Log.d("DEBUG", "Buffer values: " + Arrays.toString(buffer));

        intent.putExtra("timer", delay);
        intent.putExtra("measurement_date", measurement_date);
        // Log.d("VibChecker", "Max X: " + maxX + " Max Y: " + maxY + " Max Z: " + maxZ + "Timer" +delay + "measurement_date" +measurement_date);
        // send the dominant frequency

        intent.putExtra("Frequency_X", maxXFrequency);
        //   Log.d("DebugDebug", "IFrequency_X: " + peakFrequencyX );

        intent.putExtra("Frequency_Y", maxYFrequency);
        // Log.d("Debug", "IFrequency_Y: " + peakFrequencyY );

        intent.putExtra("Frequency_Z", maxZFrequency);
        // Log.d("Debug", "IFrequency_Z: " + peakFrequencyZ );

        // send the frequency magnitude
        bundle.putSerializable("Frequency_xMagnitudes", (Serializable) xMagnitudes);
        bundle.putSerializable("Frequency_yMagnitudes", (Serializable) yMagnitudes);
        bundle.putSerializable("Frequency_zMagnitudes", (Serializable) zMagnitudes);

        // send the data for displacement
        bundle.putSerializable("displacement_dataX", (Serializable) xDisplacement);
        bundle.putSerializable("displacement_dataY", (Serializable) yDisplacement);
        bundle.putSerializable("displacement_dataZ", (Serializable) zDisplacement);
        intent.putExtra("BUNDLE", bundle);

        // dbHelper.insertSensorData(xData,yData,zData);
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
       // xAxis.clear();
        //yAxis.clear();
        //zAxis.clear();
        timeStamps.clear();
        sensorData.clear();
        xMagnitudes.clear();
        yMagnitudes.clear();
        zMagnitudes.clear();

        //Toast.makeText(getApplicationContext(), "onResume Called Accerometer2", Toast.LENGTH_SHORT).show();

    }
    // activity is on pause state

    @Override
    protected void onPause() {
        super.onPause();
        stopSensor();
        stopTimer();
        resetData();
        // Save counter value to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(COUNTER_KEY, counter);
        editor.apply();

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
            lastInsertTime = lastUpdateTime; // Initialize the lastInsertTime

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
        if (elapsedTime < (1000 / 120)) {
            return; // Skip if too soon since the last update
        }
        lastUpdateTime = currentTime;


        // ::::::::::::::::::::
        // Record timestamp and acceleration values
      //  xAxis.add((float) event.values[0]);
       // yAxis.add((float) event.values[1]);
        //zAxis.add((float) event.values[2]);

        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];

        // Apply low-pass filter if enabled
        if (applyLowPassFilter) {
            gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * ax;
            gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * ay;
            gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * az;
            ax -= gravity[0];
            ay -= gravity[1];
            az -= gravity[2];
        }

        // Collect data for calculation
        sensorData.add(new double[]{ax, ay, az});

        // Check if the batch size has been reached
        if (sensorData.size() >= DATA_BATCH_SIZE) {
            calculateMeanAcceleration(sensorData); // Call your method to process the data
            //sensorData.clear(); // Clear the list for new batch
        }

        // Calculate time interval since the last insertion
        timeInterval = currentTime - lastInsertTime;
        timeStamps.add((double) timeInterval);

        // Update UI and database
        onSensorData(ax, ay, az);
        uiHandler.post(() -> pvPlot.invalidate()); // Redraw the plot
        dbHelper.insertSensorsData(getCurrentDateTime(), timeInterval, ax, ay, az, counter);
    }

    // calculate the mean
    private void calculateMeanAcceleration(List<double[]> rawData) {
        // Step 1: Extract raw values
        double[] xRaw = rawData.stream().mapToDouble(row -> row[0]).toArray();
        double[] yRaw = rawData.stream().mapToDouble(row -> row[1]).toArray();
        double[] zRaw = rawData.stream().mapToDouble(row -> row[2]).toArray();
        double[] timeArray = timeStamps.stream().mapToDouble(Double::floatValue).toArray();

        Log.i("Debug Data", "First 10 X Values: " + Arrays.toString(Arrays.copyOfRange(xRaw, 0, 10)));
        Log.i("Debug Data", "First 10 Y Values: " + Arrays.toString(Arrays.copyOfRange(yRaw, 0, 10)));
        Log.i("Debug Data", "First 10 Z Values: " + Arrays.toString(Arrays.copyOfRange(zRaw, 0, 10)));

        // Step 2: Calculate mean
        double meanX = Arrays.stream(xRaw).average().orElse(0.0);
        double meanY = Arrays.stream(yRaw).average().orElse(0.0);
        double meanZ = Arrays.stream(zRaw).average().orElse(0.0);

        Log.i("Debug Data", "X Mean: " + meanX + ", Y Mean: " + meanY + ", Z Mean: " + meanZ);

        // Step 3: Normalize data
        double[] normalizedX = Arrays.stream(xRaw).map(x -> x - meanX).toArray();
        double[] normalizedY = Arrays.stream(yRaw).map(y -> y - meanY).toArray();
        double[] normalizedZ = Arrays.stream(zRaw).map(z -> z - meanZ).toArray();

        Log.i("Debug Data", "First 10 Normalized X Values: " + Arrays.toString(Arrays.copyOfRange(normalizedX, 0, 10)));
        Log.i("Debug Data", "First 10 Normalized Y Values: " + Arrays.toString(Arrays.copyOfRange(normalizedY, 0, 10)));
        Log.i("Debug Data", "First 10 Normalized Z Values: " + Arrays.toString(Arrays.copyOfRange(normalizedZ, 0, 10)));

        // Step 4: Calculate RMS for mean acceleration
        meanAccelerationX = (float) Math.sqrt(Arrays.stream(normalizedX).map(x -> x * x).average().orElse(0.0));
        meanAccelerationY = (float) Math.sqrt(Arrays.stream(normalizedY).map(y -> y * y).average().orElse(0.0));
        meanAccelerationZ = (float) Math.sqrt(Arrays.stream(normalizedZ).map(z -> z * z).average().orElse(0.0));

        Log.i("Debug Data", "Mean Acceleration [m/s²] X " + meanAccelerationX);
        Log.i("Debug Data", "Mean Acceleration [m/s²] Y: " + meanAccelerationY);
        Log.i("Debug Data", "Mean Acceleration [m/s²] Z: " + meanAccelerationZ);


        // >>>>>>>>>>>>>>>>>> calculate the frequency ::::::::>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
      //  double[] timeArray = timeStamps.stream().mapToDouble(Double::floatValue).toArray();
       // double[] time = rawData.stream().mapToDouble(row -> row[3]).toArray();
        // Normalize data
        double[] xNorm = normalizeData(xRaw);
        double[] yNorm = normalizeData(yRaw);
        double[] zNorm = normalizeData(zRaw);
        double[] normalizedTime = Arrays.stream(timeArray).map(t -> t - timeArray[0]).toArray();
        // Interpolate data
//      double[] newTime = linspace(10, 5000, 500);
        double[] newTime = linspace(0, normalizedTime[normalizedTime.length - 1], 500);
        double baseTime = timeArray[0];
//      double[] normalizedTime = normalizeTime(time);
//      double[] newTime = linspace(0, normalizedTime[normalizedTime.length - 1], 500);

//
        double[] xInterp = interpolateData(timeArray, xNorm);
        double[] yInterp = interpolateData(timeArray, yNorm);
        double[] zInterp = interpolateData(timeArray, zNorm);

//      double[] xInterp = interpolate(time, xNorm, newTime);
//      double[] yInterp = interpolate(time, yNorm, newTime);
//      double[] zInterp = interpolate(time, zNorm, newTime);

        Log.i("Debug Data", "Interpolated X Data: " + Arrays.toString(xInterp));
        Log.i("Debug Data", "Interpolated Y Data: " + Arrays.toString(yInterp));
        Log.i("Debug Data", "Interpolated Z Data: " + Arrays.toString(zInterp));

        // Perform FFT and PSD calculations
        double timestep = newTime[1] - newTime[0];

        Log.i("Debug Data", "Timestep: " + timestep);
        double[] xResults = calculateFFTAndFindMax(xInterp, 0.01);
        double[] yResults = calculateFFTAndFindMax(yInterp, 0.01);
        double[] zResults = calculateFFTAndFindMax(zInterp, 0.01);


        peakFrequencysX= (float) xResults[1];
        peakFrequencysY= (float) yResults[1];
        peakFrequencysZ= (float) zResults[1];
        displacementAmplitudesX=(float)xResults[0];
        displacementAmplitudesY=(float)yResults[0];
        displacementAmplitudesZ=(float)zResults[0];


        Log.i("Debug Data", "Max Frequency [Hz]: X=" + peakFrequencysX + ", Y=" + peakFrequencysY + ", Z=" + peakFrequencysZ);
        Log.i("Debug Data", "Amplitude [mm]: X=" + displacementAmplitudesX + ", Y=" + displacementAmplitudesY + ", Z=" + displacementAmplitudesZ);

        xMagnitudes.add(peakFrequencysX);
        yMagnitudes.add(peakFrequencysY);
        zMagnitudes.add(peakFrequencysZ);


        Log.e("VibCheckerAccerlometer","XMagnitude Value is:::<<"+xMagnitudes.size());
        Log.e("VibCheckerAccerlometer","YMagnitude Value is:::"+yMagnitudes.size());
        Log.e("VibCheckerAccerlometer","ZMagnitude Value is:::"+zMagnitudes.size());

         maxXFrequency = Collections.max(xMagnitudes);
         maxYFrequency = Collections.max(yMagnitudes);
         maxZFrequency = Collections.max(zMagnitudes);

        Log.e("MaxFrequency","MaxFrequency:::::::X"+maxXFrequency);
        Log.e("MaxFrequency","MaxFrequency:::::::Y"+maxYFrequency);
        Log.e("MaxFrequency","MaxFrequency:::::::Z"+maxZFrequency);

    }



    // Normalize Data
    private double[] normalizeData(double[] data) {
        double mean = Arrays.stream(data).average().orElse(0);
        return Arrays.stream(data).map(value -> value - mean).toArray();
    }

    private double[] calculateFFTAndFindMax(double[] data, double timestep) {
        int n = data.length;
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        double[] fftData = Arrays.copyOf(data, 2 * n); // Zero-padding
        fft.realForward(fftData);

        double[] psd = new double[n / 2];
        double[] freqs = linspace(0, 1 / (2 * timestep), n / 2);

        double maxPsd = 0;
        double maxFreq = 0;

        for (int i = 0; i < n / 2; i++) {
            double real = fftData[2 * i];
            double imag = fftData[2 * i + 1];
            psd[i] = 2 * Math.sqrt(real * real + imag * imag) / n; // Compute amplitude
            if (psd[i] > maxPsd) {
                maxPsd = psd[i];
                maxFreq = freqs[i];
            }
        }
        return new double[]{maxPsd, maxFreq};
    }


    // Linspace
    private double[] linspace(double start, double end, int num) {
        double[] result = new double[num];
        double step = (end - start) / (num - 1);
        for (int i = 0; i < num; i++) {
            result[i] = start + i * step;
        }
        return result;
    }

    private double[] interpolateData(double[] time, double[] data) {
        // Number of points for interpolation
        int numPoints = 500; // You can set the desired number of points (e.g., 500)
        double[] interpolatedTime = new double[numPoints];
        double[] interpolatedData = new double[numPoints];

        // Generate evenly spaced time array for interpolation
        double minTime = time[0];
        double maxTime = time[time.length - 1];
        double step = (maxTime - minTime) / (numPoints - 1);

        for (int i = 0; i < numPoints; i++) {
            interpolatedTime[i] = minTime + i * step;
        }

        // Linear interpolation for each new time point
        for (int i = 0; i < numPoints; i++) {
            interpolatedData[i] = linearInterpolation(time, data, interpolatedTime[i]);
        }

        return interpolatedData;
    }

    // Helper method to perform linear interpolation
    private double linearInterpolation(double[] time, double[] data, double targetTime) {
        for (int i = 0; i < time.length - 1; i++) {
            if (targetTime >= time[i] && targetTime <= time[i + 1]) {
                double t1 = time[i];
                double t2 = time[i + 1];
                double d1 = data[i];
                double d2 = data[i + 1];
                // Perform linear interpolation
                return d1 + (d2 - d1) * (targetTime - t1) / (t2 - t1);
            }
        }
        // If targetTime is out of bounds, return the closest value
        return (targetTime <= time[0]) ? data[0] : data[data.length - 1];
    }


    // Linear Interpolation
    private double[] interpolate(double[] time, double[] values, double[] newTime) {
        int n = time.length;
        double[] interpolated = new double[newTime.length];

        // Normalize time arrays
        double[] normalizedTime = Arrays.stream(time).map(t -> t - time[0]).toArray();
        double[] normalizedNewTime = Arrays.stream(newTime).map(t -> t - newTime[0]).toArray(); // Fix normalization mismatch

        // Log normalized values
        Log.i("Interpolating", "Original Time: " + Arrays.toString(time));
        Log.i("Interpolating", "Normalized Time: " + Arrays.toString(normalizedTime));
        Log.i("Interpolating", "Original New Time: " + Arrays.toString(newTime));
        Log.i("Interpolating", "Normalized New Time: " + Arrays.toString(normalizedNewTime));
        Log.i("Interpolating", "Original Values: " + Arrays.toString(values));

        for (int i = 0; i < normalizedNewTime.length; i++) {
            double t = normalizedNewTime[i];

            if (t <= normalizedTime[0]) {
                interpolated[i] = values[0];
                Log.i("Interpolating", "t = " + t + " is before the first time point. Using value: " + values[0]);
            } else if (t >= normalizedTime[n - 1]) {
                interpolated[i] = values[n - 1];
                Log.i("Interpolating", "t = " + t + " is after the last time point. Using value: " + values[n - 1]);
            } else {
                boolean intervalFound = false;
                for (int j = 0; j < n - 1; j++) {
//                    Log.i("Interpolating", "Checking Interval: [" + normalizedTime[j] + ", " + normalizedTime[j + 1] + "]");
                    if (t >= normalizedTime[j] && t <= normalizedTime[j + 1]) {
                        double t1 = normalizedTime[j];
                        double t2 = normalizedTime[j + 1];
                        double v1 = values[j];
                        double v2 = values[j + 1];

                        // Interpolate
                        interpolated[i] = v1 + (v2 - v1) * (t - t1) / (t2 - t1);

                        Log.i("Interpolating", "t = " + t + " -> Using Interval [" + t1 + ", " + t2 + "] with Values [" + v1 + ", " + v2 + "] -> Interpolated Value: " + interpolated[i]);
                        intervalFound = true;
                        break;
                    }
                }
                if (!intervalFound) {
                    Log.e("Interpolating", "No interval found for t = " + t);
                }
            }
        }

        Log.i("Interpolating", "Final Interpolated Data (First 10): " + Arrays.toString(Arrays.copyOfRange(interpolated, 0, Math.min(interpolated.length, 10))));
        return interpolated;
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
  // :::::::::::::::::::::::::
/*
    private void processSensorData() {
        // Convert List to array
        double[] timeArray = timeStamps.stream().mapToDouble(Float::floatValue).toArray();
        double[] xArray = xAxis.stream().mapToDouble(Float::doubleValue).toArray();
        double[] yArray = yAxis.stream().mapToDouble(Float::doubleValue).toArray();
        double[] zArray = zAxis.stream().mapToDouble(Float::doubleValue).toArray();

        // Sampling rate estimation (e.g., average time difference)
        double samplingRate = 1000.0 / ((timeArray[timeArray.length - 1] - timeArray[0]) / timeArray.length);
        Log.e("SamplingRate","SamplingRate is::"+samplingRate);
        // Call the method
        analyzeAccelerationData(timeArray, xArray, yArray, zArray, samplingRate);
    }

    // ::::::::::::::::::::
    public void analyzeAccelerationData(double[] time, double[] xAxis, double[] yAxis, double[] zAxis, double samplingRate) {
        // Step 1: Normalize data
        double[] normalizedX = normalizeData(xAxis);
        double[] normalizedY = normalizeData(yAxis);
        double[] normalizedZ = normalizeData(zAxis);

        // Step 2: Interpolate data (optional, if irregular time intervals)
        double[] interpolatedX = interpolateData(time, normalizedX);
        double[] interpolatedY = interpolateData(time, normalizedY);
        double[] interpolatedZ = interpolateData(time, normalizedZ);

        // Step 3: Calculate Mean Acceleration
         meanAccelerationX = (float) calculateMeanAcceleration(normalizedX);
         meanAccelerationY = (float) calculateMeanAcceleration(normalizedY);
         meanAccelerationZ = (float) calculateMeanAcceleration(normalizedZ);

        // Step 4: Calculate Peak Frequency and Amplitude using FFT
         peakFrequencysX = (float) findPeakFrequency(interpolatedX, samplingRate);
         xMagnitudes.add(peakFrequencysX);
         peakFrequencysY = (float) findPeakFrequency(interpolatedY, samplingRate);
         yMagnitudes.add(peakFrequencysY);
         peakFrequencysZ = (float) findPeakFrequency(interpolatedZ, samplingRate);
         zMagnitudes.add(peakFrequencysZ);

        // Amplitudes
        DoubleFFT_1D fftX = new DoubleFFT_1D(interpolatedX.length);
        double[] fftDataX = new double[interpolatedX.length * 2];
        System.arraycopy(interpolatedX, 0, fftDataX, 0, interpolatedX.length);
        fftX.realForwardFull(fftDataX);
         displacementAmplitudesX = (float) findAmplitude(fftDataX, (int) (peakFrequencysX * interpolatedX.length / samplingRate));

        DoubleFFT_1D fftY = new DoubleFFT_1D(interpolatedY.length);
        double[] fftDataY = new double[interpolatedY.length * 2];
        System.arraycopy(interpolatedY, 0, fftDataY, 0, interpolatedY.length);
        fftY.realForwardFull(fftDataY);

        displacementAmplitudesY  = (float) findAmplitude(fftDataY, (int) (peakFrequencysY * interpolatedY.length / samplingRate));

        DoubleFFT_1D fftZ = new DoubleFFT_1D(interpolatedZ.length);
        double[] fftDataZ = new double[interpolatedZ.length * 2];
        System.arraycopy(interpolatedZ, 0, fftDataZ, 0, interpolatedZ.length);
        fftZ.realForwardFull(fftDataZ);
        displacementAmplitudesZ= (float) findAmplitude(fftDataZ, (int) (peakFrequencysZ * interpolatedZ.length / samplingRate));



        // Display results
        System.out.println("Mean Acceleration (X): " + String.format(Locale.US, "%.1f", meanAccelerationX));
        System.out.println("Mean Acceleration (Y): " + String.format(Locale.US, "%.1f", meanAccelerationY));
        System.out.println("Mean Acceleration (Z): " + String.format(Locale.US, "%.1f", meanAccelerationZ));

        System.out.println("Peak Frequency (X): " + String.format(Locale.US, "%.1f", peakFrequencysX));
        System.out.println("Peak Frequency (Y): " + String.format(Locale.US, "%.1f", peakFrequencysY));
        System.out.println("Peak Frequency (Z): " + String.format(Locale.US, "%.1f", peakFrequencysZ));

        System.out.println("Amplitude (X): " + String.format(Locale.US, "%.1f", displacementAmplitudesX));
        System.out.println("Amplitude (Y): " + String.format(Locale.US, "%.1f", displacementAmplitudesY));
        System.out.println("Amplitude (Z): " + String.format(Locale.US, "%.1f", displacementAmplitudesZ));
    }

    private double[] normalizeData(double[] data) {
        double mean = calculateMean(data);
        double[] normalizedData = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            normalizedData[i] = data[i] - mean;
        }
        return normalizedData;
    }

    private double calculateMean(double[] data) {
        double sum = 0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.length;
    }

    private double[] interpolateData(double[] time, double[] data) {
        int numPoints = xAxis.size(); // Number of points for interpolation
        double[] interpolatedTime = new double[numPoints];
        double minTime = time[0];
        double maxTime = time[time.length - 1];
        double step = (maxTime - minTime) / (numPoints - 1);

        // Generate interpolated time array
        for (int i = 0; i < numPoints; i++) {
            interpolatedTime[i] = minTime + i * step;
        }

        double[] interpolatedData = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            interpolatedData[i] = linearInterpolation(time, data, interpolatedTime[i]);
        }

        return interpolatedData;
    }

    private double linearInterpolation(double[] time, double[] data, double targetTime) {
        for (int i = 0; i < time.length - 1; i++) {
            if (time[i] <= targetTime && time[i + 1] >= targetTime) {
                double t1 = time[i];
                double t2 = time[i + 1];
                double d1 = data[i];
                double d2 = data[i + 1];
                return d1 + (targetTime - t1) * (d2 - d1) / (t2 - t1);
            }
        }
        return 0; // Default in case targetTime is out of bounds
    }
    private double calculateMeanAcceleration(double[] data) {
        double sum = 0;
        for (double value : data) {
            sum += value * value;
        }
        return Math.sqrt(sum / data.length);
    }
    private double findPeakFrequency(double[] data, double samplingRate) {
        int n = data.length;

        // Perform FFT
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        double[] fftData = new double[n * 2];
        System.arraycopy(data, 0, fftData, 0, n);
        fft.realForwardFull(fftData);

        // Compute magnitude spectrum
        double[] magnitudes = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            double real = fftData[2 * i];
            double imag = fftData[2 * i + 1];
            magnitudes[i] = Math.sqrt(real * real + imag * imag);
        }

        // Find the index of the maximum magnitude
        int peakIndex = 0;
        double maxMagnitude = 0;
        for (int i = 0; i < magnitudes.length; i++) {
            if (magnitudes[i] > maxMagnitude) {
                maxMagnitude = magnitudes[i];
                peakIndex = i;
            }
        }

        // Calculate the peak frequency
        return peakIndex * samplingRate / n;
    }
    *//*private double findAmplitude(double[] fftData, int frequencyIndex) {
        double real = fftData[2 * frequencyIndex];
        double imag = fftData[2 * frequencyIndex + 1];
        return Math.sqrt(real * real + imag * imag);
    }*//*

    private double findAmplitude(double[] fftData, int frequencyIndex) {
        if (frequencyIndex < 0 || 2 * frequencyIndex + 1 >= fftData.length) {
            Log.e("AmplitudeCalculation", "Invalid frequencyIndex: " + frequencyIndex + ", fftData length: " + fftData.length);
            return 0; // Return 0 or some default value in case of invalid index
        }
        double real = fftData[2 * frequencyIndex];
        double imag = fftData[2 * frequencyIndex + 1];
        return Math.sqrt(real * real + imag * imag);
    }

    //added new2
    private float getMaxAmplitudes(float[] magnitudes) {
        float maxAmplitude = 0;
        for (float magnitude : magnitudes) {
            maxAmplitude = Math.max(maxAmplitude, magnitude);
        }
        return maxAmplitude;
    }

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
    *//*The function uses the formula derived from physics for sinusoidal motion:
    A=a(2πf)2
    A=(2πf)2a​

    Where:
    AA is the displacement amplitude (in meters, converted to mm in the code).
    aa is the peak acceleration (in m/s²).
    ff is the frequency (in Hz).
            2πf2πf is the angular velocity (ωω).*//*
    private float calculateDisplacementAmplitude(float maxAmplitude, float frequency) {
        if (frequency > 0) { // Avoid division by zero
            return (maxAmplitude / (float) Math.pow(2 * Math.PI * frequency, 2)) * 1000; // In mm
        }
        return 0;
    }*/

    //     reset the sensor and data
    private void resetData() {
        clearBuffer();
        // Reset the buffer
        buffer = new float[3 * PLOT_BUFFER_MILLIS * 1000 / SAMPLING_PERIOD_MICROS];
        indexInBuffer = 0;
        clearBuffer();

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
       // Log.d("SensorData", "X: " + x + ", Y: " + y + ", Z: " + z);
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
                uiHandler.post(() -> txt_fivesSecond.setText(Html.fromHtml("Timer<br>" + millisUntilFinished / 1000)+ " sec"));

            }

            @Override
            public void onFinish() {
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