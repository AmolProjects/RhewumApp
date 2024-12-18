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
import com.jjoe64.graphview.series.PointsGraphSeries;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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

    private static List<Float> xArrayAmplitude = new ArrayList<>();
    private static List<Float> yArrayAmplitude = new ArrayList<>();
    private static List<Float> zArrayAmplitude = new ArrayList<>();
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
    public static void updateArrayAmplitudeX(List<Float> listAmplitudeX) {
        xArrayAmplitude=listAmplitudeX;
    }
    public static void updateArrayAmplitudeY(List<Float> listAmplitudeY) {
        yArrayAmplitude=listAmplitudeY;
    }
    public static void updateArrayAmplitudeZ(List<Float> listAmplitudeZ) {
        zArrayAmplitude=listAmplitudeZ;
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

    private void frequency_LoadChart() {
        mFreSeries1 = new LineGraphSeries<>();
        mFreSeries2 = new LineGraphSeries<>();
        mFreSeries3 = new LineGraphSeries<>();
        PointsGraphSeries<DataPoint> peakMarker = new PointsGraphSeries<>();

        Log.e("Data Sizes", "XFreq: " + xFrequencyMagnitude.size() +
                ", XAmp: " + xArrayAmplitude.size());

        // Sort the data for mFreSeries1
        List<DataPoint> sortedXDataPoints = getSortedDataPoints(xFrequencyMagnitude, xArrayAmplitude);
        for (DataPoint dataPoint : sortedXDataPoints) {
            mFreSeries1.appendData(dataPoint, true, xFrequencyMagnitude.size());
        }
        // Find and mark the peak value for mFreSeries1
        DataPoint peakPoint = getPeakPoint(sortedXDataPoints);
        if (peakPoint != null) {
            peakMarker.appendData(peakPoint, true, 1);
        }

        // Sort the data for mFreSeries2
        List<DataPoint> sortedYDataPoints = getSortedDataPoints(yFrequencyMagnitude, yArrayAmplitude);
        for (DataPoint dataPoint : sortedYDataPoints) {
            mFreSeries2.appendData(dataPoint, true, yFrequencyMagnitude.size());
        }

        // Find and mark the peak value for mFreSeries1
        DataPoint ypeakPoint = getPeakPoint(sortedYDataPoints);
        if (ypeakPoint != null) {
            peakMarker.appendData(ypeakPoint, true, 1);
        }

        // Sort the data for mFreSeries3
        List<DataPoint> sortedZDataPoints = getSortedDataPoints(zFrequencyMagnitude, zArrayAmplitude);
        for (DataPoint dataPoint : sortedZDataPoints) {
            mFreSeries3.appendData(dataPoint, true, zFrequencyMagnitude.size());
        }

        // Find and mark the peak value for mFreSeries1
        DataPoint zpeakPoint = getPeakPoint(sortedYDataPoints);
        if (zpeakPoint != null) {
            peakMarker.appendData(zpeakPoint, true, 1);
        }

        // Add the series to the graph
        frequency_chart.addSeries(mFreSeries1);
        frequency_chart.addSeries(peakMarker);

        frequency_chart.addSeries(mFreSeries2);
        frequency_chart.addSeries(peakMarker);

        frequency_chart.addSeries(mFreSeries3);
        frequency_chart.addSeries(peakMarker);

        // Customize series colors
        mFreSeries1.setColor(Color.BLACK);
        mFreSeries2.setColor(Color.RED);
        mFreSeries3.setColor(Color.BLUE);

        // Set custom X-axis labels if needed
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(frequency_chart);
        String[] xAxisLabels = new String[]{"0", "10", "20", "30", "40", "50"};
        staticLabelsFormatter.setHorizontalLabels(xAxisLabels);
        frequency_chart.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        // Set the exact X-axis range to match your data
        frequency_chart.getViewport().setMinX(0);
        frequency_chart.getViewport().setMaxX(90);
        frequency_chart.getViewport().setXAxisBoundsManual(true);
    }

    /**
     * Sorts the frequency and amplitude data into a list of DataPoints.
     */
    private List<DataPoint> getSortedDataPoints(List<Float> frequencies, List<Float> amplitudes) {
        List<DataPoint> dataPoints = new ArrayList<>();
        for (int i = 0; i < frequencies.size() && i < amplitudes.size(); i++) {
            dataPoints.add(new DataPoint(frequencies.get(i), amplitudes.get(i)));
        }
        // Sort the data points by X value (frequency)
        Collections.sort(dataPoints, Comparator.comparingDouble(DataPoint::getX));
        return dataPoints;
    }


    private void highlightPeakValue(LineGraphSeries<DataPoint> series, String label) {
        double maxAmplitude = Double.MIN_VALUE;
        double frequencyAtMax = 0;

        for (Iterator<DataPoint> it = series.getValues(0, series.getHighestValueX()); it.hasNext(); ) {
            DataPoint dp = it.next();
            if (dp.getY() > maxAmplitude) {
                maxAmplitude = dp.getY();
                frequencyAtMax = dp.getX();
            }
        }

        // Add a marker for the peak point
        PointsGraphSeries<DataPoint> peakPoint = new PointsGraphSeries<>(
                new DataPoint[]{new DataPoint(frequencyAtMax, maxAmplitude)});
        peakPoint.setShape(PointsGraphSeries.Shape.POINT);
        peakPoint.setColor(Color.GREEN);
        peakPoint.setSize(10);
        frequency_chart.addSeries(peakPoint);

        // Optionally add a label to the peak point
        Log.i(label, "Frequency: " + frequencyAtMax + ", Amplitude: " + maxAmplitude);
    }

    /**
     * Finds the peak (maximum amplitude) point from a list of DataPoints.
     */
    private DataPoint getPeakPoint(List<DataPoint> dataPoints) {
        if (dataPoints.isEmpty()) return null;

        DataPoint peakPoint = dataPoints.get(0);
        for (DataPoint point : dataPoints) {
            if (point.getY() > peakPoint.getY()) {
                peakPoint = point;
            }
        }
        return peakPoint;
    }



    /*private void frequency_LoadChart(){
        mFreSeries1 = new LineGraphSeries<>();
        mFreSeries2 = new LineGraphSeries<>();
        mFreSeries3 = new LineGraphSeries<>();

        // Log the size of the lists
        Log.e("XValue","XVale is length :::>>>>>>>>>"+xFrequencyMagnitude.size());

        // Initialize variables to store peak values
        double peakXValue = Double.MIN_VALUE;
        double peakYValue = Double.MIN_VALUE;
        double peakZValue = Double.MIN_VALUE;

        int peakXIndex = -1, peakYIndex = -1, peakZIndex = -1;

        // Add data to mSeries1 (xFrequencyMagnitude)
        for (int i = 0; i < xFrequencyMagnitude.size(); i++) {
            double value = xFrequencyMagnitude.get(i);
            mFreSeries1.appendData(new DataPoint(i, value), true, xFrequencyMagnitude.size());

            if (value > peakXValue) {  // Find peak value
                peakXValue = value;
                peakXIndex = i;
            }
        }

        // Add data to mSeries2 (yFrequencyMagnitude)
        for (int i = 0; i < yFrequencyMagnitude.size(); i++) {
            double value = yFrequencyMagnitude.get(i);
            mFreSeries2.appendData(new DataPoint(i, value), true, yFrequencyMagnitude.size());

            if (value > peakYValue) {  // Find peak value
                peakYValue = value;
                peakYIndex = i;
            }
        }

        // Add data to mSeries3 (zFrequencyMagnitude)
        for (int i = 0; i < zFrequencyMagnitude.size(); i++) {
            double value = zFrequencyMagnitude.get(i);
            mFreSeries3.appendData(new DataPoint(i, value), true, zFrequencyMagnitude.size());

            if (value > peakZValue) {  // Find peak value
                peakZValue = value;
                peakZIndex = i;
            }
        }

        // Add the series to the graph
        frequency_chart.addSeries(mFreSeries1);
        frequency_chart.addSeries(mFreSeries2);
        frequency_chart.addSeries(mFreSeries3);

        // Customize series colors
        mFreSeries1.setColor(Color.BLACK);
        mFreSeries2.setColor(Color.RED);
        mFreSeries3.setColor(Color.BLUE);

        // Highlight peak values with a PointGraphSeries
        PointsGraphSeries<DataPoint> peakSeries = new PointsGraphSeries<>();
        peakSeries.appendData(new DataPoint(peakXIndex, peakXValue), true, 1);
        peakSeries.appendData(new DataPoint(peakYIndex, peakYValue), true, 1);
        peakSeries.appendData(new DataPoint(peakZIndex, peakZValue), true, 1);

        peakSeries.setShape(PointsGraphSeries.Shape.POINT);
        peakSeries.setColor(Color.GREEN);
        peakSeries.setSize(8f);  // Adjust marker size

        frequency_chart.addSeries(peakSeries);

        // Log peak values
        Log.i("Peak Values", "X Peak: " + peakXValue + " at index " + peakXIndex);
        Log.i("Peak Values", "Y Peak: " + peakYValue + " at index " + peakYIndex);
        Log.i("Peak Values", "Z Peak: " + peakZValue + " at index " + peakZIndex);

        // Add labels (optional)
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(frequency_chart);
        String[] xAxisLabels = new String[] {"0","10","20", "30", "40", "50"};
        staticLabelsFormatter.setHorizontalLabels(xAxisLabels);
        frequency_chart.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        // X-axis range
        frequency_chart.getViewport().setMinX(0);
        frequency_chart.getViewport().setMaxX(90);
        frequency_chart.getViewport().setXAxisBoundsManual(true);

        // Enable zoom and scrolling
        frequency_chart.getViewport().setScalable(true);
        frequency_chart.getViewport().setScalableY(true);
        frequency_chart.getViewport().setScrollableY(true);
    }*/


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