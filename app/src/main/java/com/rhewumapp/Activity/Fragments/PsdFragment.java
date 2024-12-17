package com.rhewumapp.Activity.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.rhewumapp.Activity.VibChekerArchiveActivity;
import com.rhewumapp.Activity.database.PsdSummaryDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PsdFragment extends Fragment {
    View view;
    // LineChart frequency_chart;
    Button bt_archieve,bt_share;
    GraphView displacement_graph,frequency_chart;
    RhewumDbHelper dbHelper;
    static float xMaxAcceleration,yMaxAcceleration,zMaxAcceleration;
    static float amplitudeX,amplitudeY,amplitudeZ;
    static float xMaxFrequency,yMaxFrequency,zMaxFrequency;
    List<PsdSummaryDao> psdSummaryDaoArrayList;

    private static List<Float> xDisplacement = new ArrayList<>();
    private static List<Float> yDisplacement = new ArrayList<>();
    private static List<Float> zDisplacement = new ArrayList<>();
    // declare the frequency magnitude

    private static List<Float> xFrequencyMagnitude = new ArrayList<Float>();
    private static List<Float> yFrequencyMagnitude = new ArrayList<Float>();
    private static List<Float> zFrequencyMagnitude = new ArrayList<Float>();


    public LineGraphSeries<DataPoint>mFreSeries1;
    public LineGraphSeries<DataPoint>mFreSeries2;
    public LineGraphSeries<DataPoint>mFreSeries3;

    public PsdFragment() {
        // Required empty public constructor
    }

    public static void xMaxAcceleration(float x){
        xMaxAcceleration=x;
    }
    public static void yMaxAcceleration(float y){
        yMaxAcceleration=y;
    }
    public static void zMaxAcceleration(float z){
        zMaxAcceleration=z;
    }
    public static void xMaxFrequency(float x){
        xMaxFrequency=x;
    }
    public static void yMaxFrequency(float y){
        yMaxFrequency=y;
    }
    public static void zMaxFrequency(float z){
        zMaxFrequency=z;
    }

    public static void xMaxAmplitude(float x){
        amplitudeX=x;
    }
    public static void yMaxAmplitude(float y){
        amplitudeY=y;
    }
    public static void zMaxAmplitude(float z){
        amplitudeZ=z;
    }


    // receive the displacement x axis
    public static void updateDataX(List<Float> listDisplacementX) {
        xDisplacement=listDisplacementX;
    }
    public static void updateDataY(List<Float> listDisplacementY) {
        yDisplacement=listDisplacementY;
    }
    public static void updateDataZ(List<Float> listDisplacementZ) {
        zDisplacement=listDisplacementZ;
    }

    // receive the Frequency x axis
    public static void xUpdateMagnitudeFrequency(List<Float>xMagnitudeFrequncy){
        xFrequencyMagnitude=xMagnitudeFrequncy;

    }
    public static void yUpdateMagnitudeFrequency(List<Float>yMagnitudeFrequncy){
        yFrequencyMagnitude=yMagnitudeFrequncy;
    }
    public static void zUpdateMagnitudeFrequency(List<Float>zMagnitudeFrequncy){
        zFrequencyMagnitude=zMagnitudeFrequncy;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_psd, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        initObjects();
        frequency_LoadChart();
        return view;
    }
    private void initObjects(){
        frequency_chart=view.findViewById(R.id.frequency_chart);
//        displacement_graph=view.findViewById(R.id.displacement_graph);
        psdSummaryDaoArrayList=new ArrayList<>();
//        bt_archieve=view.findViewById(R.id.bt_save);
//        bt_share=view.findViewById(R.id.bt_share);
        dbHelper=new RhewumDbHelper(getActivity());

    }

    private void frequency_LoadChart(){
        mFreSeries1 = new LineGraphSeries<>();
        mFreSeries2 = new LineGraphSeries<>();
        mFreSeries3 = new LineGraphSeries<>();
        Log.e("XValue","XVale is length :::>>>>>>>>>"+xFrequencyMagnitude.size());

        // Add data to mSeries1 (xFrequencyMagnitude)

        for (int i = 0; i < xFrequencyMagnitude.size(); i++) {
            mFreSeries1.appendData(new DataPoint(i, xFrequencyMagnitude.get(i)), true, xFrequencyMagnitude.size());
        }

// Add data to mSeries2 (yFrequencyMagnitude)
        for (int i = 0; i < yFrequencyMagnitude.size(); i++) {
            mFreSeries2.appendData(new DataPoint(i, yFrequencyMagnitude.get(i)), true, yFrequencyMagnitude.size());
        }

        for (int i = 0; i < zFrequencyMagnitude.size(); i++) {
            mFreSeries3.appendData(new DataPoint(i, zFrequencyMagnitude.get(i)), true, zFrequencyMagnitude.size());
        }

        // Add the series to the graph
        frequency_chart.addSeries(mFreSeries1);
        frequency_chart.addSeries(mFreSeries2);
        frequency_chart.addSeries(mFreSeries3);
        // Customize series colors if needed

        mFreSeries1.setColor(Color.BLACK);
        mFreSeries2.setColor(Color.BLUE);
        mFreSeries3.setColor(Color.RED);

        // Set custom X-axis labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(frequency_chart);
        //Ensure the number of labels matches the unique X-values in your series
        String[] xAxisLabels = new String[] {"0","10","20", "30", "40", "50", "60", "70", "80", "90"};
        staticLabelsFormatter.setHorizontalLabels(xAxisLabels);

// Apply the static labels formatter to the graph
        frequency_chart.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

// Set the exact X-axis range to match your data
        frequency_chart.getViewport().setMinX(0);  // Set min X value
        frequency_chart.getViewport().setMaxX(90);  // Set max X value
        frequency_chart.getViewport().setXAxisBoundsManual(true);
        // Optional: Set the number of horizontal labels (can be the same as your custom labels)
        frequency_chart.getGridLabelRenderer().setNumHorizontalLabels(xAxisLabels.length);
        // Enable Y-axis labels (remove if unnecessary)
        frequency_chart.getGridLabelRenderer().setVerticalLabelsVisible(true); // Enable Y-axis labels

       /* // Customize Y-axis (optional: adjust range manually if needed)
        frequency_chart.getViewport().setYAxisBoundsManual(true);
        frequency_chart.getViewport().setMinY(0); // Set min Y value
        frequency_chart.getViewport().setMaxY(getMaxYValue()); // Set max Y value dynamically*/

        // Enable zooming and scrolling
        frequency_chart.getViewport().setScalable(true);   // Enables horizontal zooming and scrolling
        frequency_chart.getViewport().setScalableY(true);  // Enables vertical zooming and scrolling
        frequency_chart.getViewport().setScrollableY(true);

    }

    // Helper method to calculate max Y value
    private double getMaxYValue() {
        double max = 0;

        // Compare amplitudes
        max = Math.max(max, amplitudeX);
        max = Math.max(max, amplitudeY);
        max = Math.max(max, amplitudeZ);
        // Add 10% padding
        return max * 1.1;
    }

}