package com.rhewumapp.Activity.Fragments;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.core.cartesian.series.Line;
import com.anychart.enums.TooltipPositionMode;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.rhewumapp.Activity.database.PsdSummaryDao;
import com.rhewumapp.Activity.database.RhewumDbHelper;
import com.rhewumapp.R;

public class PsdFragment extends Fragment {
    View view;
    RhewumDbHelper dbHelper;
    static float xMaxAcceleration, yMaxAcceleration, zMaxAcceleration;
    static float amplitudeX, amplitudeY, amplitudeZ;
    static float xMaxFrequency, yMaxFrequency, zMaxFrequency;
    List<PsdSummaryDao> psdSummaryDaoArrayList;


    private static ArrayList<Double> xArrayAmplitude = new ArrayList<>();
    private static ArrayList<Double> yArrayAmplitude = new ArrayList<>();
    private static ArrayList<Double> zArrayAmplitude = new ArrayList<>();
    // declare the frequency magnitude

    private static ArrayList<Double> xFrequencyMagnitude = new ArrayList<Double>();
    private static ArrayList<Double> yFrequencyMagnitude = new ArrayList<Double>();
    private static ArrayList<Double> zFrequencyMagnitude = new ArrayList<Double>();

    public PsdFragment() {
        // Required empty public constructor
    }

    public static void xMaxAcceleration(float x) {
        xMaxAcceleration = x;
    }

    public static void yMaxAcceleration(float y) {
        yMaxAcceleration = y;
    }

    public static void zMaxAcceleration(float z) {
        zMaxAcceleration = z;
    }

    public static void xMaxFrequency(float x) {
        xMaxFrequency = x;
    }

    public static void yMaxFrequency(float y) {
        yMaxFrequency = y;
    }

    public static void zMaxFrequency(float z) {
        zMaxFrequency = z;
    }

    public static void xMaxAmplitude(float x) {
        amplitudeX = x;
    }

    public static void yMaxAmplitude(float y) {
        amplitudeY = y;
    }

    public static void zMaxAmplitude(float z) {
        amplitudeZ = z;
    }


    // receive the displacement x axis
    public static void updateArrayAmplitudeX(ArrayList<Double> listAmplitudeX) {
        xArrayAmplitude = listAmplitudeX;
    }

    public static void updateArrayAmplitudeY(ArrayList<Double> listAmplitudeY) {
        yArrayAmplitude = listAmplitudeY;
    }

    public static void updateArrayAmplitudeZ(ArrayList<Double> listAmplitudeZ) {
        zArrayAmplitude = listAmplitudeZ;
    }

    // receive the Frequency x axis
    public static void xUpdateMagnitudeFrequency(ArrayList<Double> xMagnitudeFrequncy) {
        xFrequencyMagnitude = xMagnitudeFrequncy;

    }

    public static void yUpdateMagnitudeFrequency(ArrayList<Double> yMagnitudeFrequncy) {
        yFrequencyMagnitude = yMagnitudeFrequncy;
    }

    public static void zUpdateMagnitudeFrequency(ArrayList<Double> zMagnitudeFrequncy) {
        zFrequencyMagnitude = zMagnitudeFrequncy;
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
        // Initialize GraphView
        GraphView graph = view.findViewById(R.id.frequency_chart);
        Log.e("PSD", "xFrequencyListMagnitude::" + xFrequencyMagnitude);
        Log.e("PSD", "yFrequencyListMagnitude::" + yFrequencyMagnitude);
        Log.e("PSD", "zFrequencyListMagnitude::" + zFrequencyMagnitude);

        Log.e("PSD", "xAmplitude::" + xArrayAmplitude);
        Log.e("PSD", "yAmplitude::" + yArrayAmplitude);
        Log.e("PSD", "zAmplitude::" + zArrayAmplitude);
        plotData(graph);
        return view;
    }

    private void initObjects() {
        // Initialize GraphView
        psdSummaryDaoArrayList = new ArrayList<>();
        dbHelper = new RhewumDbHelper(getActivity());

    }

    private void plotData(GraphView graph) {
        // Plot data for X-axis
        LineGraphSeries<DataPoint> xSeries = new LineGraphSeries<>();
        for (int i = 0; i < xFrequencyMagnitude.size(); i++) {
            xSeries.appendData(new DataPoint(xFrequencyMagnitude.get(i), xArrayAmplitude.get(i)*1000), true, xFrequencyMagnitude.size());
        }
        xSeries.setTitle("X Axis");
        xSeries.setColor(Color.BLACK);

        // Plot data for Y-axis
        LineGraphSeries<DataPoint> ySeries = new LineGraphSeries<>();
        for (int i = 0; i < yFrequencyMagnitude.size(); i++) {
            ySeries.appendData(new DataPoint(yFrequencyMagnitude.get(i), yArrayAmplitude.get(i)*1000), true, yFrequencyMagnitude.size());
        }
        ySeries.setTitle("Y Axis");
        ySeries.setColor(Color.RED);

        // Plot data for Z-axis
        LineGraphSeries<DataPoint> zSeries = new LineGraphSeries<>();
        for (int i = 0; i < zFrequencyMagnitude.size(); i++) {
            zSeries.appendData(new DataPoint(zFrequencyMagnitude.get(i), zArrayAmplitude.get(i)*1000), true, zFrequencyMagnitude.size());
        }
        zSeries.setTitle("Z Axis");
        zSeries.setColor(Color.BLUE);

        // Add all series to the graph
        graph.addSeries(xSeries);
        graph.addSeries(ySeries);
        graph.addSeries(zSeries);

        // Customize graph appearance
        graph.getViewport().setScalable(true); // Enable zooming
        graph.getViewport().setScalableY(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(90);
        graph.getViewport().setYAxisBoundsManual(true);
    }
}



   /* // Helper method to calculate max Y value
    private double getMaxYValue() {
        double max = 0;

        // Compare amplitudes
        max = Math.max(max, amplitudeX);
        max = Math.max(max, amplitudeY);
        max = Math.max(max, amplitudeZ);
        // Add 10% padding
        return max * 1.1;
    }
*/
