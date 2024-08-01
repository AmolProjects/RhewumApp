package com.rhewum.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rhewum.Activity.VibcheckerGraph.PlotView;
import com.rhewum.Activity.VibcheckerGraph.PlotViewMaxValues;
import com.rhewum.Activity.database.RhewumDbHelper;
import com.rhewum.R;

import org.jtransforms.fft.DoubleFFT_1D;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class VibCheckerAccelerometer3Activity extends AppCompatActivity {
    ImageView imgBack;
    TextView txtBack,txtDateTime,txtZZ,txtX,txty,txtfrZ,txtfrx,txtfry,txtSummary,txtPSD;
    private Calendar calendar;
    LinearLayout ll_plots;
    private PlotViewMaxValues pvPlot;
    private float[] buffer;
    float xDominantFrequency;
    float yDominantFrequency;
    float zDominantFrequency;
    float[] xFrequencyMagnitude,yFrequencyMagnitude,zFrequencyMagnitude;
    float[] xDisplacement,yDisplacement,zDisplacement;
    Button bt_save,bt_share;
    RhewumDbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vib_checker_accelerometer3);
        initObjects();
        getData();
        initGUI();
        displayPeakFrequencies();

        // click on back arrow
        imgBack.setOnClickListener(v -> {
            back();
        });
        // click on back textView
        txtBack.setOnClickListener(v -> {
            back();
        });
        // click on Psd
        txtPSD.setOnClickListener(v ->{

        });
        // click on button save
        bt_save.setOnClickListener(v->{
            Toast.makeText(getApplicationContext(),"Show save",Toast.LENGTH_SHORT).show();
            // Adding a new measurement record
            if (dbHelper == null) {
                dbHelper = new RhewumDbHelper(VibCheckerAccelerometer3Activity.this);
            }
            // Create a list to hold the accelerometer data
            List<float[]> accelerometerData = new ArrayList<>();
            // Ensure all arrays are the same length
            int length = Math.min(xFrequencyMagnitude.length, Math.min(yFrequencyMagnitude.length, zFrequencyMagnitude.length));

            // Populate the list with data
            for (int i = 0; i < length; i++) {
                float[] dataPoint = new float[3];
                dataPoint[0] = xFrequencyMagnitude[i];
                dataPoint[1] = yFrequencyMagnitude[i];
                dataPoint[2] = zFrequencyMagnitude[i];
                accelerometerData.add(dataPoint);
            }
            // Add accelerometer data to the database
            dbHelper.addAccelerometerData(accelerometerData);
            Log.e("VibChecker","Checker"+"save data");

        });

        // click on button share
        bt_share.setOnClickListener(v->{
            // Adding a new measurement record
            if (dbHelper == null) {
                dbHelper = new RhewumDbHelper(VibCheckerAccelerometer3Activity.this);
            }
            Log.e("DbHelper fetch List","DbHelper list data is:::"+dbHelper.getVibCheckerAcc());

        });

    }
    private void initObjects(){
        imgBack=findViewById(R.id.imgBack);
        txtBack=findViewById(R.id.txtBack);
        txtDateTime=findViewById(R.id.txtDateTime);
        txtZZ=findViewById(R.id.txtZZ);
        txty=findViewById(R.id.txty);
        txtX=findViewById(R.id.txtx);
        txtfrZ=findViewById(R.id.txtfrZ);
        txtfrx=findViewById(R.id.txtfrx);
        txtfry=findViewById(R.id.txtfry);
        txtSummary=findViewById(R.id.txtSummary);
        txtPSD=findViewById(R.id.txtPSD);
        bt_save=findViewById(R.id.bt_save);
        bt_share=findViewById(R.id.bt_share);
        ll_plots=findViewById(R.id.ll_plots);
        calendar = Calendar.getInstance();
        txtDateTime.setText(new SimpleDateFormat("MM-dd-yyyy,hh:mm:ss a", Locale.US).format(calendar.getTime()));

    }
    public void back(){
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
    @SuppressLint("SetTextI18n")
    private void getData(){
        Intent intent = getIntent();
        if(intent!=null) {
            // receive the acceleration max
            float accelerationX = intent.getFloatExtra("accelerationMax_X", 0);
            float accelerationY = intent.getFloatExtra("accelerationMax_Y", 0);
            float accelerationZ = intent.getFloatExtra("accelerationMax_Z", 0);
            buffer = intent.getFloatArrayExtra("sensor_data");
            // receive the DominantFrequency
            xDominantFrequency = intent.getFloatExtra("Frequency_X",0);
            yDominantFrequency = intent.getFloatExtra("Frequency_Y",0);
            zDominantFrequency = intent.getFloatExtra("Frequency_Z",0);

            // receive the Frequency Magnitudes
            xFrequencyMagnitude =intent.getFloatArrayExtra("Frequency_xMagnitudes");
            yFrequencyMagnitude =intent.getFloatArrayExtra("Frequency_yMagnitudes");
            zFrequencyMagnitude =intent.getFloatArrayExtra("Frequency_zMagnitudes");

            // receive the displacement max
            xDisplacement =intent.getFloatArrayExtra("displacement_dataX");
            yDisplacement =intent.getFloatArrayExtra("displacement_dataY");
            zDisplacement =intent.getFloatArrayExtra("displacement_dataZ");

            String formattedValueX = String.format(Locale.US, "%.1f", accelerationX);
            String formattedValueY = String.format(Locale.US, "%.1f", accelerationY);
            String formattedValueZ = String.format(Locale.US, "%.1f", accelerationZ);
            txtX.setText(formattedValueX+"m/s2"+"\n"+"x");
            txty.setText(formattedValueY+"m/s2"+"\n"+"y");
            txtZZ.setText(formattedValueZ+"m/s2"+"\n"+"z");
        }
    }
    private void initGUI() {
       pvPlot = new PlotViewMaxValues(this);
        pvPlot.setBuffer(buffer);
        pvPlot.setNumChannels(3);
        pvPlot.setChannel(0); // Not used but kept for compatibility
        pvPlot.setBackgroundColor(0xFFFFFF);
        pvPlot.setColor(0, R.color.black); // black for X
        pvPlot.setColor(1, 0xFFFF0000); // Red for Y
        pvPlot.setColor(2, 0xFF0000FF); // Blue for Z
        pvPlot.setLineWidth(3);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll_plots.addView(pvPlot, params);
    }

    @SuppressLint("SetTextI18n")
    private void displayPeakFrequencies() {

        String formattedValueX = String.format(Locale.US, "%.1f", xDominantFrequency);
        String formattedValueY = String.format(Locale.US, "%.1f", yDominantFrequency);
        String formattedValueZ = String.format(Locale.US, "%.1f", zDominantFrequency);

        txtfrx.setText(formattedValueX+"m/s2"+"\n"+"x");
        txtfry.setText(formattedValueY+"m/s2"+"\n"+"y");
        txtfrZ.setText(formattedValueZ+"m/s2"+"\n"+"z");
    }

}