package com.rhewumapp.Activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.j256.ormlite.dao.Dao;
import com.rhewumapp.Activity.MeshConveterData.Constants;
import com.rhewumapp.Activity.MeshConveterData.ResponsiveAndroidBars;
import com.rhewumapp.Activity.MeshConveterData.Utils;
import com.rhewumapp.Activity.data.BarEntryWithShelf;
import com.rhewumapp.Activity.data.CustomBarChartRenderer;
import com.rhewumapp.Activity.data.CustomBarChartRendererLiveData;
import com.rhewumapp.Activity.database.MeasurementDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.Activity.dsp.AudioMeasurement;
import com.rhewumapp.Activity.dsp.AudioProcessingListener;
import com.rhewumapp.Activity.dsp.RightAudioProcessing;
import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Base64;

import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.rhewumapp.databinding.ActivityVibSonicBinding;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.time.DateUtils;


public class VibSonicActivity extends DrawerBaseActivity implements View.OnClickListener, OnChartValueSelectedListener, AudioProcessingListener {
   // private RelativeLayout archiveLayout;
   // private RelativeLayout backLayout;
    private Chronometer chronometer;
    private double dbaCurrent;
    private TextView dbaValue;
    ImageView activity_vib_sonic_back_ivs;
    private double dbaValueFinal = Constants.PI;
    private RhewumDbHelper dbHelper;
    private RelativeLayout graphLayout;
    /* access modifiers changed from: private */
    public Handler handlerNew = new Handler();
    LinearLayout info;
    /* access modifiers changed from: private */
    public int i = 0;
    private boolean isAudioThreadStarted = false;
    private boolean isFirstTime = false;
    private boolean isGraphStarted = false;
    private RightAudioProcessing mAudioCapture;
    private BarChart mChart;
    private String[] mXaxisValues = {"32", "63", "125", "250", "500", "1K", "2K", "4K", "8K", "16K"};
    private HashMap<Integer, Double> mainHashMap = new HashMap<>();
    private TextView meanLevelTotal,text_vibs;
    Runnable r;
    private HashMap<Integer, Double> meanValueHashMap = new HashMap<>();
    private Button play_stop, bt_archive;
    private TextView timer;
    ActivityVibSonicBinding activityVibSonicBinding;
    int color = Color.parseColor("#555555");
    // Assuming measurementListDouble is initialized somewhere in your activity
    ArrayList<Double> measurementListDouble = new ArrayList<>();
    private Handler stopHandler = new Handler();
    private boolean isGraphStopped = false;
    private AudioMeasurement audioMeasurement;
    float maxFrequency=0.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityVibSonicBinding = ActivityVibSonicBinding.inflate(getLayoutInflater());
        setContentView(activityVibSonicBinding.getRoot());
        ResponsiveAndroidBars.setNotificationBarColor(this, getResources().getColor(R.color.header_backgrounds), false);
        ResponsiveAndroidBars.setNavigationBarColor(this, getResources().getColor(R.color.header_backgrounds), false, false);
        setUpViews();
        getHelper();
        this.play_stop.setText(getResources().getString(R.string.start));
       // this.backLayout.setOnClickListener(this);
        this.info.setOnClickListener(this);
        this.play_stop.setOnClickListener(this);
        this.bt_archive.setOnClickListener(this);
        this.activity_vib_sonic_back_ivs.setOnClickListener(this);
        this.text_vibs.setOnClickListener(this);

    }

    private void setUpViews() {
        this.timer = (TextView) findViewById(R.id.activity_vib_sonic_time_elapsed_tv);
        this.dbaValue = (TextView) findViewById(R.id.activity_vib_sonic_dbaValue);
        this.meanLevelTotal = (TextView) findViewById(R.id.activity_vib_sonic_mean_level_tv);
        this.text_vibs=(TextView)findViewById(R.id.activity_vib_sonic_back_tv);
        this.play_stop = (Button) findViewById(R.id.bt_vib_reset);
        this.info = (LinearLayout) findViewById(R.id.info_layouts);
        this.activity_vib_sonic_back_ivs=(ImageView)findViewById(R.id.activity_vib_sonic_back_iv);
       // this.archiveLayout = (RelativeLayout) findViewById(R.id.activity_vib_sonic_archive_layout);
        this.bt_archive = (Button) findViewById(R.id.activity_vib_sonic_archive_tvs);
        this.graphLayout = (RelativeLayout) findViewById(R.id.graphLayout);
        this.mChart = (BarChart) findViewById(R.id.activity_vib_sonic_soundGraph);
        this.chronometer = (Chronometer) findViewById(R.id.chronometer1);
        this.bt_archive.setVisibility(View.VISIBLE);
        this.mChart.setOnChartValueSelectedListener(this);
        this.mChart.setDrawBarShadow(false);
        this.mChart.setDrawValueAboveBar(true);
        this.mChart.zoom(0.0f, 0.0f, 0.f, 0.0f);
        this.mChart.setDescription("");
        this.mChart.setPinchZoom(false);
        this.mChart.setClickable(false);

        //  this.mChart.setHighlightEnabled(false);
        this.mChart.setDrawGridBackground(true);
        XAxis xAxis = this.mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(1);

        // xAxis.setSpaceMin(1);
        xAxis.setTextColor(getResources().getColor(R.color.black));
        YAxis axisLeft = this.mChart.getAxisLeft();
        // axisLeft.setLabelCount(12);
        axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        axisLeft.enableGridDashedLine(40.0f, 10.0f, 40.0f);
        axisLeft.setSpaceTop(0.0f);
        axisLeft.setAxisMinValue(0.0f);
        axisLeft.setAxisMaxValue(120.0f);
        axisLeft.setStartAtZero(true);

        // Use YAxisValueFormatter
        YAxisValueFormatter customFormatter = new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                // Ensure we are rounding and returning values only in the specified range
                int roundedValue = Math.round(value / 10) * 10;
                // Only return values that are within the 0-120 range
                if (roundedValue % 10 == 0 && roundedValue <= 120 && roundedValue >= 0) {
                    return String.valueOf(roundedValue); // Return the formatted value
                } else {
                    return ""; // Return empty for values outside 0-120 range
                }
            }
        };

        // Set the custom formatter to the left y-axis
        axisLeft.setValueFormatter(customFormatter);
        // Optionally: Set the number of labels you want on the y-axis
        axisLeft.setLabelCount(13, true); // Adjust the label count for better display

        this.mChart.getLegend().setEnabled(false);
        this.mChart.getAxisRight().setEnabled(false);
        this.mChart.setTouchEnabled(false);

        /*// Set the custom renderer
        CustomBarChartRenderer customRenderer = new CustomBarChartRenderer(mChart, mChart.getAnimator(), mChart.getViewPortHandler());
        mChart.setRenderer(customRenderer);*/

        //            // Apply the custom renderer to draw the indicator line at mainValue
        CustomBarChartRendererLiveData customRenderer = new CustomBarChartRendererLiveData(mChart, getResources().getColor(R.color.black));
        mChart.setRenderer(customRenderer);

    }

    @SuppressLint("ResourceAsColor")
    public void onClick(View view) {
        if (view.equals(this.play_stop)) {
            this.mAudioCapture.resetCurrentMeasurement();
            String charSequence = this.play_stop.getText().toString();
            if (charSequence.equals(getResources().getString(R.string.start))) {
                this.play_stop.setText(getResources().getString(R.string.stop_save));
                this.play_stop.setBackgroundColor(Color.RED);
                this.isGraphStarted = true;
                startGraph();
                startTimer();
                this.bt_archive.setVisibility(View.GONE);
                Log.e("VibSonicActivity", "VibSonicActivity" + "Start");
            } else if (charSequence.equals(getResources().getString(R.string.play))) {
                this.chronometer.setBase(SystemClock.elapsedRealtime());
                startTimer();
                this.isGraphStarted = true;
                startGraph();
                this.play_stop.setText(getResources().getString(R.string.stop_save));
                this.play_stop.setBackgroundColor(Color.RED);
                // play_stop.setBackgroundColor(ContextCompat.getColor(VibSonicActivity.this, R.color.header_backgrounds));

                Log.e("VibSonicActivity", "VibSonicActivity" + "Play");
            }
            else if (charSequence.equals(getResources().getString(R.string.stop_save))) {
                this.isGraphStarted = false;
                // Prevent further updates to mainHashMap
                isGraphStopped = true;
                stopGraph();
                String takeScreenShot = takeScreenShot();
                String charSequence2 = this.chronometer.getText().toString();
                String replace = this.meanLevelTotal.getText().toString().replace("Mean Level Total : ", "").replace("dB(A)", "");
                dbHelper.addNewRecord(VibSonicActivity.this, takeScreenShot, charSequence2, replace + "dB(A)", this.meanValueHashMap);
                this.chronometer.stop();
                this.play_stop.setText(getResources().getString(R.string.play));
                this.bt_archive.setVisibility(View.VISIBLE);
                play_stop.setBackgroundColor(ContextCompat.getColor(VibSonicActivity.this, R.color.header_backgrounds));
                Log.d("VibSonicActivity", "MainHashMap data at stop: " + mainHashMap.toString());
                Intent intent = new Intent(this, VibSonicArchiveActivity.class);
                intent.putExtra("JumpFrom", "MainPage");
                intent.putExtra("MaxFrequency", maxFrequency);
                startActivity(intent);
            }


        } else if (view.equals(this.activity_vib_sonic_back_ivs)) {
            this.chronometer.stop();
            stopGraph();
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

        } else if (view.equals(this.text_vibs)) {
            this.chronometer.stop();
            stopGraph();
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }

        else if (view.equals(this.info)) {
            startActivity(new Intent(this, VibSonicInfoActivity.class));
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }
         else if (view.equals(this.bt_archive)) {
            startActivity(new Intent(this, VibSonicArchiveListActivity.class));
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }
    }

    private String takeScreenShot() {
        this.graphLayout.setDrawingCacheEnabled(true);
        this.graphLayout.buildDrawingCache(true);
        Bitmap createBitmap = Bitmap.createBitmap(this.graphLayout.getDrawingCache());
        this.graphLayout.setDrawingCacheEnabled(false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        createBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, 0);
    }


    private void startGraph() {
        handlerNew = new Handler();
        r = new Runnable() {
            public void run() {
                VibSonicActivity vibSonicActivity = VibSonicActivity.this;
                vibSonicActivity.setData(10, 10.0f, vibSonicActivity.i);
                VibSonicActivity vibSonicActivity2 = VibSonicActivity.this;
                int unused = vibSonicActivity2.i = vibSonicActivity2.i + 1;
                VibSonicActivity.this.handlerNew.postDelayed(this, 1000);
            }
        };
        this.handlerNew.post(r);


    }

    private void stopGraph() {
        if (this.isGraphStarted) {
            this.handlerNew.removeCallbacks(this.r);
            this.mAudioCapture.close();
            RightAudioProcessing.unregisterDrawableFFTSamplesAvailableListener();
        }
    }


    public void setData(int i2, float f, int i3) {
        HashMap<Integer, Double> hashMap = this.mainHashMap;
        if (hashMap != null && hashMap.size() > 0) {
            boolean equals = this.play_stop.getText().toString().equals(getResources().getString(R.string.start));
            boolean equals2 = this.play_stop.getText().toString().equals(getResources().getString(R.string.stop_save));
            ArrayList<String> arrayList = new ArrayList<>();
            ArrayList arrayList2 = new ArrayList<>();

            for (int i4 = 0; i4 < i2; i4++) {
                arrayList.add(this.mXaxisValues[i4 % 10]);
                if (equals2) {
                    try {
                        arrayList2.add(new BarEntryWithShelf(this.meanValueHashMap.get(Integer.valueOf(i4)).floatValue(), i4, this.mainHashMap.get(Integer.valueOf(i4)).floatValue(), ViewCompat.MEASURED_STATE_MASK));
                    } catch (Exception unused) {
                        return;
                    }
                } else {
                    arrayList2.add(new BarEntryWithShelf(this.mainHashMap.get(Integer.valueOf(i4)).floatValue(), i4, this.mainHashMap.get(Integer.valueOf(i4)).floatValue(), ViewCompat.MEASURED_STATE_MASK));
                }
            }

            // Create the dataset and set it to the chart
            BarDataSet barDataSet = new BarDataSet(arrayList2, "DataSet");


            barDataSet.setBarSpacePercent(35.0f);
            if (equals2) {
                barDataSet.setColor(getResources().getColor(R.color.header_backgrounds));
            } else {
                barDataSet.setColor(0);
            }

            ArrayList<IBarDataSet> arrayList3 = new ArrayList<>();
            arrayList3.add(barDataSet);
            BarData barData = new BarData((List<String>) arrayList, arrayList3);
            // Set a ValueFormatter to display only whole numbers without decimal points
            barData.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int) value); // Convert float to int to remove decimal
                }
            });
            barData.setValueTextSize(10.0f);
            barData.setValueTextColor(getResources().getColor(R.color.header_backgrounds));
            this.mChart.setData(barData);
            this.mChart.invalidate();

            // Update the displayed dB(A) values
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);
            decimalFormatSymbols.setDecimalSeparator(ClassUtils.PACKAGE_SEPARATOR_CHAR);
            decimalFormatSymbols.setGroupingSeparator(',');
            DecimalFormat decimalFormat = new DecimalFormat("00.00", decimalFormatSymbols);
            TextView textView = this.dbaValue;
            textView.setText("Mean Levels: " + decimalFormat.format(this.dbaCurrent) + " dB(A)");
            if (!equals) {
                TextView textView2 = this.meanLevelTotal;
                textView2.setText("" + decimalFormat.format(this.dbaValueFinal) + "dB(A) ");


                Log.d("Mean","meanLevel "+dbaCurrent);
                Log.d("Mean","meanLevelTotal "+dbaValueFinal);



            }


        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.RECORD_AUDIO"}, 1);
            return;
        }
        this.mAudioCapture = new RightAudioProcessing(VibSonicActivity.this);
        RightAudioProcessing.registerDrawableFFTSamplesAvailableListener(this);
           this.isFirstTime = true;
        for (int i2 = 0; i2 < 10; i2++) {
//            this.mainHashMap.put(Integer.valueOf(i2), Double.valueOf(Constants.PI));
//            this.meanValueHashMap.put(Integer.valueOf(i2), Double.valueOf(Constants.PI));
        }
        if (this.r == null) {
            this.r = new Runnable() {
                public void run() {
                    VibSonicActivity vibSonicActivity = VibSonicActivity.this;
                    vibSonicActivity.setData(10, 10.0f, vibSonicActivity.i);
                    VibSonicActivity.this.handlerNew.postDelayed(this, 100);
                }
            };
        }
        this.handlerNew.post(this.r);
        this.isAudioThreadStarted = true;
    }



    public void onBackPressed() {
        super.onBackPressed();
        this.chronometer.stop();
        stopGraph();
        stopHandler.removeCallbacksAndMessages(null); // Cancel any scheduled stops
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private void startTimer() {
        this.chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @SuppressLint("SetTextI18n")
            public void onChronometerTick(Chronometer chronometer) {
                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                String str;
                long elapsedRealtime = SystemClock.elapsedRealtime() - chronometer.getBase();
                int i = (int) (elapsedRealtime / DateUtils.MILLIS_PER_HOUR);
                long j = elapsedRealtime - ((long) (3600000 * i));
                int i2 = ((int) j) / 60000;
                int i3 = ((int) (j - ((long) (60000 * i2)))) / 1000;
                if (i < 10) {
                    sb.append("0");
                    sb.append(i);
                } else {
                    sb = new StringBuilder();
                    sb.append(i);
                    sb.append("");
                }
                String sb3 = sb.toString();
                if (i2 < 10) {
                    sb2.append("0");
                    sb2.append(i2);
                } else {
                    sb2 = new StringBuilder();
                    sb2.append(i2);
                    sb2.append("");
                }
                String sb4 = sb2.toString();
                if (i3 < 10) {
                    str = "0" + i3;
                } else {
                    str = i3 + "";
                }
                chronometer.setText(sb3 + ":" + sb4 + ":" + str);
            }
        });
        this.chronometer.setBase(SystemClock.elapsedRealtime());
        this.chronometer.start();

        // Use a Handler to stop the Chronometer after 30 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            this.chronometer.stop();
            stopGraph();
        }, 31000);// 30000 milliseconds = 30 seconds

    }

    private RhewumDbHelper getHelper() {
        if (this.dbHelper == null) {
            this.dbHelper = (RhewumDbHelper) OpenHelperManager.getHelper(this, RhewumDbHelper.class);
        }
        return this.dbHelper;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e != null) {
            this.mChart.zoom(0.0f, 0.0f, 0.0f, 0.0f);
            // this.mChart.setHighlightEnabled(false);
            this.mChart.setPinchZoom(false);
        }
    }

    public void onNothingSelected() {
        this.mChart.setPinchZoom(false);
        this.mChart.zoom(0.0f, 0.0f, 0.0f, 0.0f);
    }


    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.isAudioThreadStarted) {
            this.mAudioCapture.close();
            RightAudioProcessing.unregisterDrawableFFTSamplesAvailableListener();
        }
    }


    public void onDrawableFFTSignalAvailable(AudioMeasurement audioMeasurement) {
        boolean equals = this.play_stop.getText().toString().equals(getResources().getString(R.string.stop_save));
        for (int i2 = 0; i2 < 10; i2++) {
            this.mainHashMap.put(Integer.valueOf(i2), Double.valueOf(audioMeasurement.currentSpectrumDBValues[i2]));
            this.meanValueHashMap.put(Integer.valueOf(i2), Double.valueOf(audioMeasurement.finalSpectrumDBValues[i2]));
        }
        this.dbaCurrent = audioMeasurement.tmpCurrentAmplitudeDBValue;
        if (equals) {
            this.dbaValueFinal = audioMeasurement.finalAmplitudeDBValue;
        }

    }

    public void onRequestPermissionsResult(int i2, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i2, strArr, iArr);
        if (i2 == 1) {
            if (iArr.length > 0) {
                if (iArr[0] == 0) {
                    this.mAudioCapture = new RightAudioProcessing(VibSonicActivity.this);
                    RightAudioProcessing.registerDrawableFFTSamplesAvailableListener(this);
                    this.isFirstTime = true;
                    for (int i3 = 0; i3 < 10; i3++) {
                        this.mainHashMap.put(Integer.valueOf(i3), Double.valueOf(Constants.PI));
                        this.meanValueHashMap.put(Integer.valueOf(i3), Double.valueOf(Constants.PI));
                    }
                    if (this.r == null) {
                        this.r = new Runnable() {
                            public void run() {
                                VibSonicActivity vibSonicActivity = VibSonicActivity.this;
                                vibSonicActivity.setData(10, 10.0f, vibSonicActivity.i);
                                VibSonicActivity.this.handlerNew.postDelayed(this, 1000);
                            }
                        };
                    }
                    this.handlerNew.post(this.r);
                    this.isAudioThreadStarted = true;
                    setUpViews();
                    getHelper();
                    this.play_stop.setText(getResources().getString(R.string.start));
                   // this.backLayout.setOnClickListener(this);
                   // this.archiveLayout.setOnClickListener(this);
                    this.play_stop.setOnClickListener(this);
                    return;
                }
            }
            onBackPressed();
            Toast.makeText(this, "To use this function you need to grant access to your microphone", Toast.LENGTH_SHORT).show();
        }
    }





}