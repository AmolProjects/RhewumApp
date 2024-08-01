package com.rhewum.Activity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.rhewum.Activity.MeshConveterData.Utils;
import com.rhewum.Activity.VibcheckerGraph.FFTProcessor;
import com.rhewum.Activity.VibcheckerGraph.PlotView;
import com.rhewum.R;

import org.jtransforms.fft.FloatFFT_1D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VibCheckerAccelerometer2Activity extends AppCompatActivity {
    TextView txtBack,txt_fiveSecond,txt_zeroDelay,txt_onLpFilter,txtZ,txtx,txty,txt_content,txtZeroDelay,txt_fivesSecond;
    LinearLayout llPlots;
    ImageView imgBack;
    Button bt_vib_start,bt_vib_reset;
    boolean startFlag=true;
    private SensorEventListener sensorEventListener;
    private float[] buffer;
    private int indexInBuffer;
    private PlotView pvPlot;
    private Handler handler;
    private boolean isSensorRunning = false;
    private CountDownTimer countdownTimer;
    static private final int ZERO_TIME_DRAW_DURATION_MILLIS =1000; // Duration to draw the graph in one milliseconds
    static private final int SAMPLING_PERIOD_MICROS = 10000;
    static private final int PLOT_BUFFER_MILLIS = 6000;
    private static final float SAMPLING_RATE = 50.0f; // Example: 50 Hz
    // Variables to store max values
    private float maxX = Float.MIN_VALUE;
    private float maxY = Float.MIN_VALUE;
    private float maxZ = Float.MIN_VALUE;
    // store senor data for x,y,z axis for the frequency
    private final ArrayList<Float> xData = new ArrayList<>();
    private final ArrayList<Float> yData = new ArrayList<>();
    private final ArrayList<Float> zData = new ArrayList<>();

    // frequency magnitude
    private List<Float>xMagnitudes=new ArrayList<>();
    private List<Float> yMagnitudes=new ArrayList<>();
    private List<Float> zMagnitudes=new ArrayList<>();
    // dominant frequency
    float xDominantFrequency;
    float yDominantFrequency;
    float zDominantFrequency;
    Bundle bundle;

    private final List<Float> xVelocity = new ArrayList<>();
    private final List<Float> yVelocity = new ArrayList<>();
    private final List<Float> zVelocity = new ArrayList<>();

    private final List<Float> xDisplacement = new ArrayList<>();
    private final List<Float> yDisplacement = new ArrayList<>();
    private final List<Float> zDisplacement = new ArrayList<>();

    private static final float NS2S = 1.0f / 1000000000.0f; // Nanoseconds to seconds
    private long lastTimestamp = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setFontFamily("fonts/heebo.ttf");
        setContentView(R.layout.activity_vib_checker_accelerometer2);
        initObjects();
        initGUI();
        handler = new Handler();
        // click on the previous back screen
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                VibCheckerAccelerometer2Activity.this.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });
        // click on the previous back screen
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                VibCheckerAccelerometer2Activity.this.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });
        // click on zero delay second
        txt_zeroDelay.setOnClickListener(v -> {
            initGUI();
            startSensor();
            Intent intent=new Intent(VibCheckerAccelerometer2Activity.this,VibCheckerAccelerometer3Activity.class);
            startActivity(intent);

        });
        // click on start button
        bt_vib_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startFlag){
                    hideTheUi();
                    initGUI();
                    startSensor();
                    startCountdown();
                    resetMaxValues();
                   // Toast.makeText(getApplicationContext(),"First Called",Toast.LENGTH_SHORT).show();
                    startFlag = false;
                }else{
                    nextToScreen();
                }

            }
        });
        // click on the reset button
        bt_vib_reset.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                startFlag = true;
                stopSensor();
                stopTimer();
                resetData();
                bt_vib_start.setText(R.string.start);
                bt_vib_start.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this,R.color.primary_s));
              //  Toast.makeText(VibCheckerAccelerometer2Activity.this,"Reset data !!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    // initialise the object
    private void initObjects(){
        txtBack=findViewById(R.id.txtBack);
        txt_fiveSecond=findViewById(R.id.txt_fiveSecond);
        txtZ=findViewById(R.id.txtZ);
        txtx=findViewById(R.id.txtx);
        txty=findViewById(R.id.txty);
        txt_content=findViewById(R.id.txt_content);
        txt_zeroDelay=findViewById(R.id.txt_zeroDelay);
        txt_onLpFilter=findViewById(R.id.txt_onLpFilter);
        txt_fivesSecond=findViewById(R.id.txt_fivesSecond);
        imgBack=findViewById(R.id.imgBack);
        txtZeroDelay=findViewById(R.id.txtZeroDelay);
        bt_vib_start=findViewById(R.id.bt_vib_start);
        bt_vib_reset=findViewById(R.id.bt_vib_reset);
        txtx.setVisibility(View.INVISIBLE);
        txty.setVisibility(View.INVISIBLE);
        txtZ.setVisibility(View.INVISIBLE);
        bundle = new Bundle();


    }
    // next to the accelerometer screen
    private void nextToScreen(){
        bt_vib_start.setBackgroundColor(ContextCompat.getColor(VibCheckerAccelerometer2Activity.this,R.color.primary_s));
        bt_vib_start.setText(R.string.start);
        Intent intent=new Intent(VibCheckerAccelerometer2Activity.this,VibCheckerMainActivity.class);
        // send the data for acceleration
        intent.putExtra("accelerationMax_X", maxX);
        intent.putExtra("accelerationMax_Y", maxY);
        intent.putExtra("accelerationMax_Z", maxZ);
        intent.putExtra("sensor_data", buffer);

       // send the dominant frequency
        intent.putExtra("Frequency_X", xDominantFrequency);
        intent.putExtra("Frequency_Y", yDominantFrequency);
        intent.putExtra("Frequency_Z", zDominantFrequency);
       // send the frequency magnitude
        bundle.putSerializable("Frequency_xMagnitudes",(Serializable)xMagnitudes);
        bundle.putSerializable("Frequency_yMagnitudes",(Serializable)yMagnitudes);
        bundle.putSerializable("Frequency_zMagnitudes",(Serializable)zMagnitudes);

       // send the data for displacement
        bundle.putSerializable("displacement_dataX",(Serializable)xDisplacement);
        bundle.putSerializable("displacement_dataY",(Serializable)yDisplacement);
        bundle.putSerializable("displacement_dataZ",(Serializable)zDisplacement);
        intent.putExtra("BUNDLE",bundle);

        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        startFlag = true;
    }
    // hide the ui screen text content ...etc
    private void hideTheUi(){
        txt_content.setVisibility(View.INVISIBLE);
        txtx.setVisibility(View.VISIBLE);
        txty.setVisibility(View.VISIBLE);
        txtZ.setVisibility(View.VISIBLE);
        bt_vib_start.setBackgroundColor(Color.RED);
        bt_vib_start.setText(R.string.results);
    }
    // activity is destroy in the stack
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSensor();
        stopTimer();
        resetData();
        Toast.makeText(getApplicationContext(),"onDestroy Called",Toast.LENGTH_SHORT).show();

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
    // initialise the ui and buffer for layout design
    private void initGUI(){
        llPlots=findViewById(R.id.ll_plots);
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
    }
    // start the sensor
    private void startSensor(){
        if (!isSensorRunning) {
            initSensor();
            // Duration to draw the graph in milliseconds
            int USER_TIME_DRAW_DURATION_MILLIS = 6000;
            handler.postDelayed(this::stopSensor, USER_TIME_DRAW_DURATION_MILLIS);
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
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    if (lastTimestamp == 0) {
                        lastTimestamp = event.timestamp;
                        return;
                    }
                    // filter for calculate the FFT
                        if(xData.size()>16){
                            processFFT();
                        }
                        // calculate for acceleration
                    onSensorData(event.values[0], event.values[1], event.values[2]);
                    // calculate for frequency filter FFT
                    xData.add(event.values[0]);
                    yData.add(event.values[1]);
                    zData.add(event.values[2]);

                    // store for calculate time difference
                    float dt = (event.timestamp - lastTimestamp) * NS2S; // Time difference in seconds
                    lastTimestamp = event.timestamp;

                    float ax = event.values[0];
                    float ay = event.values[1];
                    float az = event.values[2];

                    // Integrate acceleration to get velocity
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
                }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(sensorEventListener, accelerometer, SAMPLING_PERIOD_MICROS);
    }

    // stop the sensor
    private void stopSensor() {
        if (isSensorRunning) {
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(sensorEventListener);
            isSensorRunning = false;
        }
    }
    // apply on  FFT on accelerometer data
    public void  processFFT(){

        float[] xArray = convertListToArray(xData);
        float[] yArray = convertListToArray(yData);
        float[] zArray = convertListToArray(zData);
        FFTProcessor fftProcessor = new FFTProcessor();
        // magnitude for the frequency
        xMagnitudes = fftProcessor.processFFT(xArray);
        yMagnitudes = fftProcessor.processFFT(yArray);
        zMagnitudes = fftProcessor.processFFT(zArray);


        xDominantFrequency = fftProcessor.getDominantFrequency(xMagnitudes, SAMPLING_RATE);
        yDominantFrequency = fftProcessor.getDominantFrequency(yMagnitudes, SAMPLING_RATE);
        zDominantFrequency = fftProcessor.getDominantFrequency(zMagnitudes, SAMPLING_RATE);

       // System.out.println("X Axis Dominant Frequency: " + xDominantFrequency + " Hz");
       // System.out.println("Y Axis Dominant Frequency: " + yDominantFrequency + " Hz");
       // System.out.println("Z Axis Dominant Frequency: " + zDominantFrequency + " Hz");

    }

   // reset the sensor and data
    private void resetData() {
        // Reset the buffer
        buffer = new float[3 * PLOT_BUFFER_MILLIS * 1000 / SAMPLING_PERIOD_MICROS];
        indexInBuffer = 0;
        // Clear the plot view
        pvPlot.setBuffer(buffer);
        pvPlot.postInvalidate();
        // Ensure sensor is stopped
        stopSensor();
    }

    private void onSensorData(float x, float y, float z) {
        // Update max values
        if (x > maxX) maxX = x;
        if (y > maxY) maxY = y;
        if (z > maxZ) maxZ = z;
        // plots accept +/- 1.0 range
        // so we scale to have the displayed range of +/- 10 m/s²:
        x /= 10;
        y /= 10;
        z /= 10;

        buffer[indexInBuffer++] = x;
        buffer[indexInBuffer++] = y;
        buffer[indexInBuffer++] = z;

        indexInBuffer %= buffer.length;
        pvPlot.postInvalidate();
    }
    private void startCountdown() {
        countdownTimer = new CountDownTimer(10000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txt_fivesSecond.setText(String.valueOf(millisUntilFinished / 2000));
            }

            @Override
            public void onFinish() {
                txt_fivesSecond.setText("0s\ntime");
            }
        }.start();
    }
    private void stopTimer(){
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
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
        }
    }



}