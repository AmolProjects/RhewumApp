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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.rhewumapp.Activity.VibcheckerGraph.PlotView;
import com.rhewumapp.Activity.database.RawSensorDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;
import com.rhewumapp.Utils.CSVReaderUtils;
import com.rhewumapp.Utils.CSVUtils;
import com.rhewumapp.databinding.ActivityVibCheckerAccelerometer2Binding;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.jtransforms.fft.DoubleFFT_1D;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VibCheckerAccelerometer2Activity extends DrawerBaseActivity {
    private SensorEventListener sensorEventListener;
    static private final int SAMPLING_PERIOD_MICROS = 10000;
    static private final int PLOT_BUFFER_MILLIS = 5000;

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
    private static final List<Double> timeStamps = new ArrayList<>();
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
    private static final List<Double> xFrequencyList = new ArrayList<Double>();
    private static final List<Double> yFrequencyList = new ArrayList<Double>();
    private static final List<Double> zFrequencyList = new ArrayList<Double>();
    private static final List<Double> xAmplitudeList = new ArrayList<>();
    private static final List<Double> yAmplitudeList = new ArrayList<>();
    private static final List<Double> zAmplitudeList = new ArrayList<>();
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
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
    static float meanAccelerationX;
    static float meanAccelerationY;
    static float meanAccelerationZ;
    static float peakFrequencysX;
    static float peakFrequencysY;
    static float peakFrequencysZ;
    static float maxXFrequency;
    static float maxYFrequency;
    static float maxZFrequency;
    static float displacementAmplitudesX;
    static float displacementAmplitudesY;
    static float displacementAmplitudesZ;
    ProgressBar progressBar;

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
            Context context;
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
                    // nextToScreen(); // Proceed to the next screen
                    new CalculateMeanAccelerationTask(VibCheckerAccelerometer2Activity.this,progressBar).execute();
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
                deleteCsvFileFromExternalStorage(VibCheckerAccelerometer2Activity.this);
                //CSVUtils.deleteVibCheckerDataFile(VibCheckerAccelerometer2Activity.this);
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


    // delete the csv file
    public void deleteCsvFileFromExternalStorage(Context context) {
        // Get the directory for external storage (e.g., Downloads folder)
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(directory, "VibCheckerData.csv");

        // Check if the file exists and delete it
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Log.d("FileDeleter", "File deleted successfully.");
            } else {
                Log.d("FileDeleter", "Failed to delete the file.");
            }
        } else {
            Log.d("FileDeleter", "File does not exist.");
        }
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
        progressBar = findViewById(R.id.progressBar);
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
                dbHelper.insertVibCheckerData(meanAccelerationX, meanAccelerationY, meanAccelerationZ, peakFrequencysX, peakFrequencysY, peakFrequencysZ,
                        displacementAmplitudesX, displacementAmplitudesY, displacementAmplitudesZ,
                        currentTimerValue, delay, measurement_date, serializedBuffer, new RawSensorDao());
                isSummarySaved = true;
                // Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
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

        intent.putExtra("Frequency_X", peakFrequencysX);
        //   Log.d("DebugDebug", "IFrequency_X: " + peakFrequencyX );

        intent.putExtra("Frequency_Y", peakFrequencysY);
        // Log.d("Debug", "IFrequency_Y: " + peakFrequencyY );

        intent.putExtra("Frequency_Z", peakFrequencysZ);
        // Log.d("Debug", "IFrequency_Z: " + peakFrequencyZ );
        intent.putExtra("Counter_Value",counter);

        // send the frequency magnitude
        bundle.putSerializable("Frequency_xMagnitudes", (Serializable) xFrequencyList);
        bundle.putSerializable("Frequency_yMagnitudes", (Serializable) yFrequencyList);
        bundle.putSerializable("Frequency_zMagnitudes", (Serializable) zFrequencyList);

        // send the data for displacement
        bundle.putSerializable("displacement_dataX", (Serializable) xAmplitudeList);
        bundle.putSerializable("displacement_dataY", (Serializable) yAmplitudeList);
        bundle.putSerializable("displacement_dataZ", (Serializable) zAmplitudeList);
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
        if (!ioExecutor.isShutdown()) {
            ioExecutor.shutdownNow();
        }
        deleteCsvFileFromExternalStorage(VibCheckerAccelerometer2Activity.this);
       // CSVUtils.deleteVibCheckerDataFile(VibCheckerAccelerometer2Activity.this);


    }

    // activity is resume state
    @Override
    protected void onResume() {
        super.onResume();
       // timeStamps.clear();
        sensorData.clear();
        xFrequencyList.clear();
        yFrequencyList.clear();
        zFrequencyList.clear();
        xAmplitudeList.clear();
        yAmplitudeList.clear();
        zAmplitudeList.clear();

    }
    // activity is on pause state

    @Override
    protected void onPause() {
        super.onPause();
        stopSensor();
        stopTimer();
       // resetData();
        //deleteCsvFileFromExternalStorage();
        //CSVUtils.deleteVibCheckerDataFile(VibCheckerAccelerometer2Activity.this);
        // Save counter value to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(COUNTER_KEY, counter);
        editor.apply();
        // Check if the user left the app (e.g., pressing the home button).
    }

    private void initGUI() {
        uiHandler.post(() -> {
            llPlots = findViewById(R.id.vibrationLineChart);
            llPlots.removeAllViews();
            // buffer will contain data of all 3 channels (x, y, z)
            buffer = new float[3 * (PLOT_BUFFER_MILLIS + 1000) * 550 / SAMPLING_PERIOD_MICROS];
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
        if (elapsedTime < 10) {
            return; // Skip if too soon since the last update
        }
        lastUpdateTime = currentTime;


        // ::::::::::::::::::::
        // Record timestamp and acceleration values
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
//        if (sensorData.size() > 10) {
//            //  calculateMeanAcceleration(sensorData); // Call your method to process the data
//            //sensorData.clear(); // Clear the list for new batch
//        }

        // Calculate time interval since the last insertion
        timeInterval = currentTime - lastInsertTime;
        timeStamps.add((double) timeInterval);

        // Update UI and database
        onSensorData(ax, ay, az);
        uiHandler.post(() -> pvPlot.invalidate()); // Redraw the plot
        //dbHelper.insertSensorsData(getCurrentDateTime(), timeInterval, ax, ay, az, counter);
        // Call the utility method
        // CSVUtils.saveVibCheckerDataToCSV(this, getCurrentDateTime(), timeInterval, ax, ay, az);

        final float finalAx = ax;
        final float finalAy = ay;
        final float finalAz = az;
        final long finalTimeInterval = timeInterval;

        ioExecutor.execute(() -> {
            // insert data into database
            dbHelper.insertSensorsData(getCurrentDateTime(), finalTimeInterval, finalAx, finalAy, finalAz, counter);
            // save the data into phone memory location
            CSVUtils.saveVibCheckerDataToCSV(this, getCurrentDateTime(), finalTimeInterval, finalAx, finalAy, finalAz);
        });
    }

    // calculate the mean
    public static void calculateMeanAcceleration(List<double[]> rawData) {
        // Step 1: Extract raw values
        double[] xRaw = rawData.stream().mapToDouble(row -> row[0]).toArray();
        double[] yRaw = rawData.stream().mapToDouble(row -> row[1]).toArray();
        double[] zRaw = rawData.stream().mapToDouble(row -> row[2]).toArray();
        double[] time = rawData.stream().mapToDouble(row -> row[3]).toArray();

       // double[] time = timeStamps.stream().mapToDouble(Double::floatValue).toArray();
        Log.i("Debug Data", "TimeInterval: " + time.length);

        //added 17
        double[] normalizedTime = Arrays.stream(time).map(t -> t - time[0]).toArray();
        //  Log.i("Debug Data", "normalizedTime Length " + normalizedTime.length);

        Log.i("Debug Data", "normalizedTime::::: " + Arrays.toString(normalizedTime));
//        double[] newTime = linspace(0, normalizedTime[normalizedTime.length - 1], 500);
        double stepSize = 10.0; // Match Python's step size
//        int numPoints = (int) ((normalizedTime[normalizedTime.length - 1] - 0) / stepSize) + 1;
        //added 26Dec
        int numPoints = (int) Math.max(((normalizedTime[normalizedTime.length - 1] - 0) / stepSize) + 1, 1);

        Log.i("Debug Data", "numPoints: " + (numPoints));
        double[] newTime = linspace(0, normalizedTime[normalizedTime.length - 1], numPoints);

        Log.i("Debug Data", "newTime Length" + newTime.length);


        Log.i("Debug Data", "First 10 X Values: " + Arrays.toString(Arrays.copyOfRange(xRaw, 0, 10)));
        Log.i("Debug Data", "First 10 Y Values: " + Arrays.toString(Arrays.copyOfRange(yRaw, 0, 10)));
        Log.i("Debug Data", "First 10 Z Values: " + Arrays.toString(Arrays.copyOfRange(zRaw, 0, 10)));
        Log.i("Debug Data", "First 10 time: " + Arrays.toString(Arrays.copyOfRange(time, 0, 10)));
        // Step 2: Calculate mean
        double meanX = Arrays.stream(xRaw).average().orElse(0.0);
        double meanY = Arrays.stream(yRaw).average().orElse(0.0);
        double meanZ = Arrays.stream(zRaw).average().orElse(0.0);

        Log.i("Debug Data", "X Mean: " + meanX + ", Y Mean: " + meanY + ", Z Mean: " + meanZ);

        // Step 3: Normalize data
        double[] normalizedX = Arrays.stream(xRaw).map(x -> x - meanX).toArray();
        double[] normalizedY = Arrays.stream(yRaw).map(y -> y - meanY).toArray();
        double[] normalizedZ = Arrays.stream(zRaw).map(z -> z - meanZ).toArray();

        Log.i("Debug Data", "Normalized X Values length: " + normalizedX.length);
        Log.i("Debug Data", "Normalized Y Values length: " + normalizedY.length);
        Log.i("Debug Data", "Normalized Z Values length" + normalizedZ.length);

        // Step 4: Calculate RMS for mean acceleration
        meanAccelerationX = (float) Math.sqrt(Arrays.stream(normalizedX).map(x -> x * x).average().orElse(0.0));
        meanAccelerationY = (float) Math.sqrt(Arrays.stream(normalizedY).map(y -> y * y).average().orElse(0.0));
        meanAccelerationZ = (float) Math.sqrt(Arrays.stream(normalizedZ).map(z -> z * z).average().orElse(0.0));

        Log.i("Debug Data", "Mean Acceleration [m/s²] X" + meanAccelerationX);
        Log.i("Debug Data", "Mean Acceleration [m/s²] Y:" + meanAccelerationY);
        Log.i("Debug Data", "Mean Acceleration [m/s²] Z:" + meanAccelerationZ);


        // >>>>>>>>>>>>>>>>>> calculate the frequency ::::::::>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //  double[] timeArray = timeStamps.stream().mapToDouble(Double::floatValue).toArray();
        // double[] time = rawData.stream().mapToDouble(row -> row[3]).toArray();

        // calculate the interpolation
        double[] xInterp = interpolateData_spilne(normalizedTime, normalizedX, newTime);
        double[] yInterp = interpolateData_spilne(normalizedTime, normalizedY, newTime);
        double[] zInterp = interpolateData_spilne(normalizedTime, normalizedZ, newTime);


        // Perform FFT and PSD calculations
        double timestep = newTime[1] - newTime[0];

        Log.i("Debug Data", "Timestep: " + timestep);

        double[] xResults = calculateFFTAndFindMax_New(xInterp, 0.01);
        double[] yResults = calculateFFTAndFindMax_New(yInterp, 0.01);
        double[] zResults = calculateFFTAndFindMax_New(zInterp, 0.01);

        // Log.i("Element", "xResults: " + Arrays.toString(xResults));
        // calculate the peak frequency
        peakFrequencysX= (float) xResults[1];
        peakFrequencysY= (float) yResults[1];
        peakFrequencysZ= (float) zResults[1];
        // calculate the amplitude

        displacementAmplitudesX=(float)xResults[0];
        displacementAmplitudesY=(float)yResults[0];
        displacementAmplitudesZ=(float)zResults[0];


        Log.i("Debug Data", "Max Frequency [Hz]: X=" + peakFrequencysX + ", Y=" + peakFrequencysY + ", Z=" + peakFrequencysZ);
        Log.i("Debug Data", "Amplitude [mm]: X=" + displacementAmplitudesX + ", Y=" + displacementAmplitudesY + ", Z=" + displacementAmplitudesZ);


        double[][] xFrequencies = calculateFFTAndReturnAllFrequencies(xInterp, 0.01, "X");
        double[][] yFrequencies = calculateFFTAndReturnAllFrequencies(yInterp, 0.01, "Y");
        double[][] zFrequencies = calculateFFTAndReturnAllFrequencies(zInterp, 0.01, "Z");


        // Convert xFrequencies to ArrayLists
        for (double freq : xFrequencies[0]) xFrequencyList.add(freq);
        for (double amp : xFrequencies[1]) xAmplitudeList.add(amp);

// Convert yFrequencies to ArrayLists
        for (double freq : yFrequencies[0]) yFrequencyList.add(freq);
        for (double amp : yFrequencies[1]) yAmplitudeList.add(amp);

// Convert zFrequencies to ArrayLists
        for (double freq : zFrequencies[0]) zFrequencyList.add(freq);
        for (double amp : zFrequencies[1]) zAmplitudeList.add(amp);


    }



    private static double[] interpolateData_spilne(double[] time, double[] values, double[] newTime) {
        // Sort the time array and ensure values are sorted accordingly
        int n = time.length;
        double[] sortedTime = new double[n];
        double[] sortedValues = new double[n];

        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }

        // Sort indices based on the time array
        Arrays.sort(indices, Comparator.comparingDouble(i -> time[i]));

        for (int i = 0; i < n; i++) {
            sortedTime[i] = time[indices[i]];
            sortedValues[i] = values[indices[i]];
        }

        // Remove duplicates or non-increasing values
        List<Double> cleanTime = new ArrayList<>();
        List<Double> cleanValues = new ArrayList<>();
        cleanTime.add(sortedTime[0]);
        cleanValues.add(sortedValues[0]);

        for (int i = 1; i < sortedTime.length; i++) {
            if (sortedTime[i] > cleanTime.get(cleanTime.size() - 1)) {
                cleanTime.add(sortedTime[i]);
                cleanValues.add(sortedValues[i]);
            }
        }

        // Convert cleaned lists to arrays
        double[] finalTime = cleanTime.stream().mapToDouble(Double::doubleValue).toArray();
        double[] finalValues = cleanValues.stream().mapToDouble(Double::doubleValue).toArray();

        // Perform interpolation
        SplineInterpolator interpolator = new SplineInterpolator();
        PolynomialSplineFunction spline = interpolator.interpolate(finalTime, finalValues);

        double[] interpolated = new double[newTime.length];
        for (int i = 0; i < newTime.length; i++) {
            if (newTime[i] < finalTime[0]) {
                interpolated[i] = finalValues[0];
            } else if (newTime[i] > finalTime[finalTime.length - 1]) {
                interpolated[i] = finalValues[finalValues.length - 1];
            } else {
                interpolated[i] = spline.value(newTime[i]);
            }
        }
        return interpolated;
    }


    // using fft calculate the peak frequency

    private static double[]  calculateFFTAndFindMax_New(double[] data, double timestep) {
        int n = data.length;
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        double[] fftData = new double[2 * n]; // Zero-padding for complex FFT
        double[] windowedData = applyHanningWindow(data); // Apply Hanning Window

        System.arraycopy(windowedData, 0, fftData, 0, n); // Copy windowed data
        fft.realForward(fftData);

        double[] psd = new double[n / 2];
        double[] freqs = linspace(0, 1 / (2 * timestep), n / 2);

        double maxPsd = 0;
        double maxFreq = 0;

        for (int i = 1; i < n / 2; i++) { // Start from index 1 to ignore DC component
            double real = fftData[2 * i];
            double imag = fftData[2 * i + 1];
            double freq = freqs[i];

            // Amplitude with angular frequency scaling
            psd[i] = 4 * Math.sqrt(real * real + imag * imag) / n / Math.pow((2 * Math.PI * freq), 2);

            // Filter out frequencies below 10 Hz
            if (freq < 10) {
                psd[i] = 0;
            }
            Log.i("Debug Data","Frequency Values List Values :::"+freqs[i]);
            Log.i("Debug Data","Amplitude Values List values :::"+psd[i]);

            // Track max amplitude
            if (psd[i] > maxPsd) {
                maxPsd = psd[i];
                maxFreq = freq;
            }
        }

        return new double[]{maxPsd * 1000, maxFreq}; // Convert amplitude to millimeters
    }

    private static double[][] calculateFFTAndReturnAllFrequencies(double[] data, double timestep, String axis) {
        int n = data.length;
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        double[] fftData = new double[2 * n]; // Zero-padding for complex FFT
        double[] windowedData = applyHanningWindow(data); // Apply Hanning Window

        System.arraycopy(windowedData, 0, fftData, 0, n); // Copy windowed data
        fft.realForward(fftData);

        double[] psd = new double[n / 2];
        double[] freqs = linspace(0, 1 / (2 * timestep), n / 2);

        for (int i = 1; i < n / 2; i++) { // Start from index 1 to ignore DC component
            double real = fftData[2 * i];
            double imag = fftData[2 * i + 1];
            double freq = freqs[i];

            // Amplitude with angular frequency scaling
            psd[i] = 2 * Math.sqrt(real * real + imag * imag) / n / Math.pow((2 * Math.PI * freq), 2);

            // Filter out frequencies below 10 Hz
            if (freq < 10) {
                psd[i] = 0;
            }
        }

        // Log all frequencies and amplitudes for debugging
        for (int i = 0; i < psd.length; i++) {
            Log.d("FFT Frequencies (" + axis + ")", "Frequency: " + freqs[i] + " Hz, Amplitude: " + psd[i]);
        }

        // Return frequencies and amplitudes as a 2D array
        return new double[][]{freqs, psd};
    }

    // Apply Hanning Window
    private static double[] applyHanningWindow(double[] data) {
        int n = data.length;
        double[] windowedData = new double[n];
        for (int i = 0; i < n; i++) {
            windowedData[i] = data[i] * (0.5 - 0.5 * Math.cos(2 * Math.PI * i / (n - 1)));
        }
        return windowedData;
    }

    // Linspace
    private static double[] linspace(double start, double end, int num) {
        double[] result = new double[num];
        double step = (end - start) / (num - 1);
        for (int i = 0; i < num; i++) {
            result[i] = start + i * step;
        }
        return result;
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


        if (indexInBuffer < buffer.length) {
            buffer[indexInBuffer++] = x;
            buffer[indexInBuffer++] = y;
            buffer[indexInBuffer++] = z;
        } else {
            // Use circular buffer logic to prevent empty spaces
            indexInBuffer = indexInBuffer % buffer.length;
            buffer[indexInBuffer++] = x;
            buffer[indexInBuffer++] = y;
            buffer[indexInBuffer++] = z;
        }

        // Ensure index wraps around
//        indexInBuffer %= buffer.length;
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


    @SuppressLint("StaticFieldLeak")
    class CalculateMeanAccelerationTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private ProgressBar progressBar;


        // Constructor to pass context only
        public CalculateMeanAccelerationTask(Context context) {
            this.context = context.getApplicationContext();  // Avoid memory leaks
        }

        // Constructor to pass context and ProgressBar
        public CalculateMeanAccelerationTask(Context context, ProgressBar progressBar) {
            this.context = context.getApplicationContext();  // Set context here too
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show the progress bar before starting the task
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Call the method that processes the data
            if (context == null) {
                Log.e("AsyncTask", "Context is null in doInBackground");
                return null;
            }

            // Read CSV data and process it
            List<double[]> vibCheckerData = CSVReaderUtils.readVibCheckerData(context);
            calculateMeanAcceleration(vibCheckerData);

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Hide the progress bar after task completion
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
                nextToScreen();
            }

            // You can update the UI after the task is complete
            //onDataProcessingComplete();
        }
    }
}
