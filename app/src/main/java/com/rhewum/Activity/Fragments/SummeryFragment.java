package com.rhewum.Activity.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.rhewum.Activity.VibcheckerGraph.PlotViewMaxValues;
import com.rhewum.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SummeryFragment extends Fragment {
    View view;
    TextView txtZZ,txtX,txty,txtfrZ,txtfrx,txtfry,txtDateTime;
    LinearLayout ll_plots;
    private PlotViewMaxValues pvPlot;
    private float[] buffer;
    static float xDominantFrequency;
    static float yDominantFrequency;
    static float zDominantFrequency;
    // declare the frequency magnitude list
    private static List<Float> xFrequencyMagnitude = new ArrayList<>();
    private static List<Float> yFrequencyMagnitude = new ArrayList<>();
    private static List<Float> zFrequencyMagnitude = new ArrayList<>();
   // declare the displacement list
    private static List<Float> xDisplacement = new ArrayList<>();
    private static List<Float> yDisplacement = new ArrayList<>();
    private static List<Float> zDisplacement = new ArrayList<>();
    private Calendar calendar;


    public SummeryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_summery, container, false);
        initObjects();
        getData();
        initGUI();
        displayPeakFrequencies();
        return view;
    }
    @SuppressLint("SetTextI18n")
    private void initObjects(){
        txtZZ=view.findViewById(R.id.txtZZ);
        txty=view.findViewById(R.id.txty);
        txtX=view.findViewById(R.id.txtx);
        txtfrZ=view.findViewById(R.id.txtfrZ);
        txtfrx=view.findViewById(R.id.txtfrx);
        txtfry=view.findViewById(R.id.txtfry);
        ll_plots=view.findViewById(R.id.ll_plots);
        txtDateTime=view.findViewById(R.id.txtDateTime);
        calendar = Calendar.getInstance();
        txtDateTime.setText("New Record "+new SimpleDateFormat("MM-dd-yyyy,hh:mm:ss a", Locale.US).format(calendar.getTime()));
    }
    @SuppressLint("SetTextI18n")
    @SuppressWarnings("unchecked")
    private void getData(){
        Intent intent = requireActivity().getIntent();
        if(intent!=null) {
            float accelerationX = intent.getFloatExtra("accelerationMax_X", 0);
            float accelerationY = intent.getFloatExtra("accelerationMax_Y", 0);
            float accelerationZ = intent.getFloatExtra("accelerationMax_Z", 0);
            buffer = intent.getFloatArrayExtra("sensor_data");

            xDominantFrequency = intent.getFloatExtra("Frequency_X",0);
            yDominantFrequency = intent.getFloatExtra("Frequency_Y",0);
            zDominantFrequency = intent.getFloatExtra("Frequency_Z",0);

            Bundle args = intent.getBundleExtra("BUNDLE");
            assert args != null;
           // receive the frequency magnitude list
            xFrequencyMagnitude= (List<Float>) args.getSerializable("Frequency_xMagnitudes");
            yFrequencyMagnitude= (List<Float>) args.getSerializable("Frequency_yMagnitudes");
            zFrequencyMagnitude= (List<Float>) args.getSerializable("Frequency_zMagnitudes");
            // receive the displacement list
            xDisplacement= (List<Float>) args.getSerializable("displacement_dataX");
            yDisplacement= (List<Float>) args.getSerializable("displacement_dataY");
            zDisplacement= (List<Float>) args.getSerializable("displacement_dataZ");

            String formattedValueX = String.format(Locale.US, "%.1f", accelerationX);
            String formattedValueY = String.format(Locale.US, "%.1f", accelerationY);
            String formattedValueZ = String.format(Locale.US, "%.1f", accelerationZ);
            txtX.setText(formattedValueX+"m/s2"+"\n"+"x");
            txty.setText(formattedValueY+"m/s2"+"\n"+"y");
            txtZZ.setText(formattedValueZ+"m/s2"+"\n"+"z");
        }
    }
    private void initGUI() {
        pvPlot = new PlotViewMaxValues(getActivity());
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

    public static List<Float> getDataListX() {
        return xDisplacement;
    }
    public static List<Float> getDataListY() {
        return yDisplacement;
    }
    public static List<Float> getDataListZ() {
        return zDisplacement;
    }
    public static List<Float> xFrequencyMagnitude(){
        return xFrequencyMagnitude;
    }
    public static List<Float> yFrequencyMagnitude(){
        return yFrequencyMagnitude;
    }
    public static List<Float> zFrequencyMagnitude(){
        return zFrequencyMagnitude;
    }

}