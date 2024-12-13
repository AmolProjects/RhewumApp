package com.rhewumapp.Activity;

import static com.rhewumapp.Activity.MeshConveterData.Utils.getCurrentDateTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.rhewumapp.Activity.Pojo.CounterViewModel;
import com.rhewumapp.Activity.VibcheckerGraph.PlotView;
import com.rhewumapp.Activity.database.RawDao;
import com.rhewumapp.Activity.database.RawSensorDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;
import com.rhewumapp.databinding.ActivityVibCheckerAccelerometer2Binding;

import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.FloatFFT_1D;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

    private List<Float> xAxis = new ArrayList<>();
    private List<Float> yAxis = new ArrayList<>();
    private List<Float> zAxis = new ArrayList<>();
    private List<Float> timeStamps = new ArrayList<>();


    private final List<Float> xVelocity = new ArrayList<>();
    private final List<Float> yVelocity = new ArrayList<>();
    private final List<Float> zVelocity = new ArrayList<>();
    private final List<Float> xDisplacement = new ArrayList<>();
    private final List<Float> yDisplacement = new ArrayList<>();
    private final List<Float> zDisplacement = new ArrayList<>();
    private long lastInsertTime = 0; // Track last insert time
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
    private float maxX = Float.MIN_VALUE;;
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
    float ax,ay,az;
    private Handler backgroundHandler;

    ActivityVibCheckerAccelerometer2Binding activityVibCheckerAccelerometer2Binding;
    private long lastShakeTime = 0;
    private static final int SHAKE_THRESHOLD = 100; // Threshold for shake detection
    private int  delay = 5 ;
    private static final int SAMPLE_RATE = 50; // Number of samples per second
    private static final int BUFFER_SIZE = SAMPLE_RATE * 5; // 5 seconds of data
    private long lastUpdateTime = 0;
    String measurement_date;
    private int counter = 0;
    private CounterViewModel counterViewModel;
   // private List<float[]> accDataBuffer = new ArrayList<>();

    private static final float MIN_FREQUENCY = 5.0f;
    private static final float MAX_FREQUENCY = 70.0f;

    private int currentTimerValue = 5; // Initialize with the default timer value
    float meanAccelerationX,meanAccelerationY,meanAccelerationZ;
    float peakFrequencysX,peakFrequencysY,peakFrequencysZ;



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
                    counterViewModel.incrementCounter();

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
        counterViewModel = new ViewModelProvider(this).get(CounterViewModel.class);

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
                dbHelper.insertVibCheckerData(meanAccelerationX, meanAccelerationY, meanAccelerationZ, peakFrequencysX, peakFrequencysY, peakFrequencysZ,
                        displacementAmplitudeX, displacementAmplitudeY, displacementAmplitudeZ,
                        currentTimerValue, delay, measurement_date, serializedBuffer,new RawSensorDao()
                );
                isSummarySaved = true;
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
        intent.putExtra("displacementAmplitudeX", displacementAmplitudeX);
        intent.putExtra("displacementAmplitudeY", displacementAmplitudeY);
        intent.putExtra("displacementAmplitudeZ", displacementAmplitudeZ);


        intent.putExtra("sensor_data", buffer);
      //  Log.d("DEBUG", "Buffer values: " + Arrays.toString(buffer));

        intent.putExtra("timer",delay);
        intent.putExtra("measurement_date",measurement_date);
       // Log.d("VibChecker", "Max X: " + maxX + " Max Y: " + maxY + " Max Z: " + maxZ + "Timer" +delay + "measurement_date" +measurement_date);
        // send the dominant frequency

        intent.putExtra("Frequency_X", peakFrequencysX);
     //   Log.d("DebugDebug", "IFrequency_X: " + peakFrequencyX );

        intent.putExtra("Frequency_Y", peakFrequencysY);
       // Log.d("Debug", "IFrequency_Y: " + peakFrequencyY );

        intent.putExtra("Frequency_Z", peakFrequencysZ);
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
       // Log.d("VibChecker", "Max X: " + maxX + " Max Y: " + maxY + " Max Z: " + maxZ);

        // 03-12-2024  insert the raw data list x, list y, list z......
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
        xData.clear();
        yData.clear();
        zData.clear();
        xDisplacement.clear();
        yDisplacement.clear();
        zDisplacement.clear();
        xAxis.clear();
        yAxis.clear();
        zAxis.clear();
        timeStamps.clear();
        xMagnitudes.clear();
        yMagnitudes.clear();
        zMagnitudes.clear();

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
        if (elapsedTime < (1000 /120)) {
            return; // Skip if too soon since the last update
        }
        lastUpdateTime = currentTime;


       // ::::::::::::::::::::
        // Record timestamp and acceleration values
        xAxis.add((float) event.values[0]);
        yAxis.add((float) event.values[1]);
        zAxis.add((float) event.values[2]);

        float ax = event.values[0];
        float ay = event.values[1];
        float az = event.values[2];

        // Apply low-pass filter if enabled
        if (applyLowPassFilter) {
            gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * ax;
            gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * ay;
            gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * az;
            ax -= gravity[0];
            ay -= gravity[1];
            az -= gravity[2];
        }

        //::::::::::::::::::::::::::::::
        // Calculate time interval since the last insertion ssssss
        long timeInterval = currentTime - lastInsertTime;
        timeStamps.add((float) timeInterval);

       // Log.d("AccValues","acc X:: " +ax +"acc Y:: " +ay +"acc Z::" +az);

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
        processSensorData();
        dbHelper.insertSensorsData(getCurrentDateTime(), timeInterval,ax,ay,az,counterViewModel.getCounter());

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

    private void processSensorData() {
        // Convert List to array
        double[] timeArray = timeStamps.stream().mapToDouble(Float::floatValue).toArray();
        double[] xArray = xAxis.stream().mapToDouble(Float::doubleValue).toArray();
        double[] yArray = yAxis.stream().mapToDouble(Float::doubleValue).toArray();
        double[] zArray = zAxis.stream().mapToDouble(Float::doubleValue).toArray();

        // Sampling rate estimation (e.g., average time difference)
        double samplingRate = 1000.0 / ((timeArray[timeArray.length - 1] - timeArray[0]) / timeArray.length);

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
        double amplitudeX = findAmplitude(fftDataX, (int) (peakFrequencyX * interpolatedX.length / samplingRate));

        DoubleFFT_1D fftY = new DoubleFFT_1D(interpolatedY.length);
        double[] fftDataY = new double[interpolatedY.length * 2];
        System.arraycopy(interpolatedY, 0, fftDataY, 0, interpolatedY.length);
        fftY.realForwardFull(fftDataY);
        double amplitudeY = findAmplitude(fftDataY, (int) (peakFrequencyY * interpolatedY.length / samplingRate));

        DoubleFFT_1D fftZ = new DoubleFFT_1D(interpolatedZ.length);
        double[] fftDataZ = new double[interpolatedZ.length * 2];
        System.arraycopy(interpolatedZ, 0, fftDataZ, 0, interpolatedZ.length);
        fftZ.realForwardFull(fftDataZ);
        double amplitudeZ = findAmplitude(fftDataZ, (int) (peakFrequencyZ * interpolatedZ.length / samplingRate));



        // Display results
        System.out.println("Mean Acceleration (X): " + String.format(Locale.US, "%.1f", meanAccelerationX));
        System.out.println("Mean Acceleration (Y): " + String.format(Locale.US, "%.1f", meanAccelerationY));
        System.out.println("Mean Acceleration (Z): " + String.format(Locale.US, "%.1f", meanAccelerationZ));

        System.out.println("Peak Frequency (X): " + String.format(Locale.US, "%.1f", peakFrequencysX));
        System.out.println("Peak Frequency (Y): " + String.format(Locale.US, "%.1f", peakFrequencysY));
        System.out.println("Peak Frequency (Z): " + String.format(Locale.US, "%.1f", peakFrequencysZ));

        System.out.println("Amplitude (X): " + String.format(Locale.US, "%.1f", amplitudeX));
        System.out.println("Amplitude (Y): " + String.format(Locale.US, "%.1f", amplitudeY));
        System.out.println("Amplitude (Z): " + String.format(Locale.US, "%.1f", amplitudeZ));
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
    private double findAmplitude(double[] fftData, int frequencyIndex) {
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
      //  Log.d("Debug", "xMagnitudesArray: " + Arrays.toString(xMagnitudesArray));

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
      //  Log.d("Debug", "Frequencies: " + Arrays.toString(frequencies));


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
          //  Log.d("Debug", "Peak Frequency X: " + peakFrequencyX + " Hz, Displacement Amplitude X: " + displacementAmplitudeX + " mm");
        } else {
           // Log.e("Error", "Invalid Peak Frequency X: " + peakFrequencyX + ". Ensure data is correct.");
        }

        if (peakFrequencyY > 0) {
            float maxAmplitudeY = getMaxAmplitude(yMagnitudesArray);
             displacementAmplitudeY = calculateDisplacementAmplitude(maxAmplitudeY, peakFrequencyY);
          //  Log.d("VibChecker", "Peak Frequency Y: " + peakFrequencyY + " Hz, Displacement Amplitude Y: " + displacementAmplitudeY + " mm");
        }

        if (peakFrequencyZ > 0) {
            float maxAmplitudeZ = getMaxAmplitude(zMagnitudesArray);
             displacementAmplitudeZ = calculateDisplacementAmplitude(maxAmplitudeZ, peakFrequencyZ);
           // Log.d("VibChecker", "Peak Frequency Z: " + peakFrequencyZ + " Hz, Displacement Amplitude Z: " + displacementAmplitudeZ + " mm");
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
           // Log.d("Debug", "Peak Frequency " + axis + ": " + peakFrequency + " Hz");
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
                // txt_fivesSecond.setText("0s\ntime");
                currentTimerValue = 5; // Timer finished
                txt_fivesSecond.setText("Timer\n0 sec");
              //  accDataBuffer.clear();
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.cancel(); // Stop the timer
        }
        txt_fivesSecond.setText("Timer\n5 sec");
       // accDataBuffer.clear();
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