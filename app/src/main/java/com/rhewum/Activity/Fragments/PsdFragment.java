package com.rhewum.Activity.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.EventLogTags;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.rhewum.R;

import java.util.ArrayList;
import java.util.List;

public class PsdFragment extends Fragment {
    View view;
   // LineChart frequency_chart;
    Button bt_save,bt_share;
    GraphView displacement_graph,frequency_chart;
    private static List<Float> xDisplacement = new ArrayList<>();
    private static List<Float> yDisplacement = new ArrayList<>();
    private static List<Float> zDisplacement = new ArrayList<>();
    // declare the frequency magnitude
    private static List<Float> xFrequencyMagnitude = new ArrayList<>();
    private static List<Float> yFrequencyMagnitude = new ArrayList<>();
    private static List<Float> zFrequencyMagnitude = new ArrayList<>();

    public LineGraphSeries<DataPoint>mDispSeries1;
    public LineGraphSeries<DataPoint>mDispSeries2;
    public LineGraphSeries<DataPoint>mDispSeries3;

    public LineGraphSeries<DataPoint>mFreSeries1;
    public LineGraphSeries<DataPoint>mFreSeries2;
    public LineGraphSeries<DataPoint>mFreSeries3;



    public PsdFragment() {
        // Required empty public constructor
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
        view= inflater.inflate(R.layout.fragment_psd, container, false);
        initObjects();
        frequency_LoadChart();
        displacement_loadChart();
        return view;
    }
    private void initObjects(){
        frequency_chart=view.findViewById(R.id.frequency_chart);
        displacement_graph=view.findViewById(R.id.displacement_graph);
        bt_save=view.findViewById(R.id.bt_save);
        bt_share=view.findViewById(R.id.bt_share);

    }
    private void frequency_LoadChart(){
        mFreSeries1 = new LineGraphSeries<>();
        mFreSeries2 = new LineGraphSeries<>();
        mFreSeries3 = new LineGraphSeries<>();

        // Add data to mSeries1 (xFrequencyMagnitude)
        for (int i = 0; i < xFrequencyMagnitude.size(); i++) {
            mFreSeries1.appendData(new DataPoint(i, xFrequencyMagnitude.get(i)), true, xFrequencyMagnitude.size());
        }
        // Add data to mSeries2 (yFrequencyMagnitude)
        for (int i = 0; i < yFrequencyMagnitude.size(); i++) {
            mFreSeries2.appendData(new DataPoint(i, yFrequencyMagnitude.get(i)), true, yFrequencyMagnitude.size());
        }
        // Add data to mSeries3 (zFrequencyMagnitude)
        for (int i = 0; i < zFrequencyMagnitude.size(); i++) {
            mFreSeries3.appendData(new DataPoint(i, zFrequencyMagnitude.get(i)), true, zFrequencyMagnitude.size());
        }

        // Add the series to the graph
        frequency_chart.addSeries(mFreSeries1);
        frequency_chart.addSeries(mFreSeries2);
        frequency_chart.addSeries(mFreSeries3);
        // Customize series colors if needed

        mFreSeries1.setColor(Color.BLACK);
        mFreSeries2.setColor(Color.GREEN);
        mFreSeries3.setColor(Color.RED);

        frequency_chart.getViewport().setXAxisBoundsManual(true);
        frequency_chart.getViewport().setMinX(0);

        // Customize graph settings if needed
        frequency_chart.getViewport().setScalable(true);  // enables horizontal zooming and scrolling
        frequency_chart.getViewport().setScalableY(true);  // enables vertical zooming and scrolling
        frequency_chart.getViewport().setScrollableY(true);  // enables vertical scrolling


    }
    private void displacement_loadChart(){
        mDispSeries1 = new LineGraphSeries<>();
        mDispSeries2 = new LineGraphSeries<>();
        mDispSeries3 = new LineGraphSeries<>();

        // Add data to mSeries1 (xDisplacement)
        for (int i = 0; i < xDisplacement.size(); i++) {
            mDispSeries1.appendData(new DataPoint(i, xDisplacement.get(i)), true, xDisplacement.size());
        }
        // Add data to mSeries2 (yDisplacement)
        for (int i = 0; i < yDisplacement.size(); i++) {
            mDispSeries2.appendData(new DataPoint(i, yDisplacement.get(i)), true, yDisplacement.size());
        }
        // Add data to mSeries3 (zDisplacement)
        for (int i = 0; i < zDisplacement.size(); i++) {
            mDispSeries3.appendData(new DataPoint(i, zDisplacement.get(i)), true, zDisplacement.size());
        }
        // Add the series to the graph
        displacement_graph.addSeries(mDispSeries1);
        displacement_graph.addSeries(mDispSeries2);
        displacement_graph.addSeries(mDispSeries3);

        // Customize series colors if needed
        mDispSeries1.setColor(Color.BLACK);
        mDispSeries2.setColor(Color.GREEN);
        mDispSeries3.setColor(Color.RED);

        // Customize graph settings if needed
        displacement_graph.getViewport().setScalable(true);  // enables horizontal zooming and scrolling
        displacement_graph.getViewport().setScrollable(true);  // enables horizontal scrolling
        displacement_graph.getViewport().setScalableY(true);  // enables vertical zooming and scrolling
        displacement_graph.getViewport().setScrollableY(true);  // enables vertical scrolling

    }

}