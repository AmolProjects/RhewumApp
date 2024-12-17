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
    private Handler handler = new Handler();
    private Runnable runnable;
    List<PsdSummaryDao> psdSummaryDaoArrayList;

    private static List<Float> xDisplacement = new ArrayList<>();
    private static List<Float> yDisplacement = new ArrayList<>();
    private static List<Float> zDisplacement = new ArrayList<>();
    // declare the frequency magnitude

    private static List<Float[]> xFrequencyMagnitude = new ArrayList<Float[]>();
    private static List<Float[]> yFrequencyMagnitude = new ArrayList<Float[]>();
    private static List<Float[]> zFrequencyMagnitude = new ArrayList<Float[]>();


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
    public static void xUpdateMagnitudeFrequency(List<Float[]>xMagnitudeFrequncy){
        xFrequencyMagnitude=xMagnitudeFrequncy;
    }
    public static void yUpdateMagnitudeFrequency(List<Float[]>yMagnitudeFrequncy){
        yFrequencyMagnitude=yMagnitudeFrequncy;
    }
    public static void zUpdateMagnitudeFrequency(List<Float[]>zMagnitudeFrequncy){
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
//        displacement_loadChart();

//        bt_archieve.setOnClickListener(view -> {
//            ExecutorService executorService = Executors.newSingleThreadExecutor();
//            executorService.execute(() -> {
//                try {
//                    dbHelper.saveDisplacementData(xDisplacement, yDisplacement, zDisplacement);
//                    dbHelper.saveFrequencyMagnitudeData(xFrequencyMagnitude, yFrequencyMagnitude, zFrequencyMagnitude);
//                    startActivity(new Intent(getActivity(), VibChekerArchiveActivity.class));
//                    runnable=new Runnable() {
//                        @Override
//                        public void run() {
//                            psdSummaryDaoArrayList = dbHelper.getLatestDisplacementsByListSize(100);
//                            Log.e("FRAGMENT","FRAGMENT LIST"+psdSummaryDaoArrayList.size());
//                            try {
//                                // Step 1: Save data to the CSV file
//                                saveDisplacementDataToCSV((Context) getActivity(), (ArrayList<PsdSummaryDao>) psdSummaryDaoArrayList);
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//                            Log.e("FRAGMENT","FRAGMENT LIST 2"+psdSummaryDaoArrayList.size());
//
//                        }
//                    };handler.postDelayed(runnable,1000);
//
//                } catch (SQLException e) {
//                    e.printStackTrace();  // Consider logging this exception properly
//                }
//            });
//            executorService.shutdown();  // Shutdown the executor when the task is done
////            Toast.makeText(getActivity(), "Data is save successfully !!", Toast.LENGTH_SHORT).show();
//
//        });

        // click on button share
//        bt_share.setOnClickListener(v->{
//            if(!psdSummaryDaoArrayList.isEmpty()) {
//                Log.e("FRAGMENT","FRAGMENT LIST"+psdSummaryDaoArrayList.size());
//                // Step 2: Get the CSV file reference
//                File csvFile = new File(requireActivity().getCacheDir() + "/" + getResources().getString(R.string.psd_summary) + ".csv");
//                // Step 3: Share the CSV file via email
//                shareCSVFileViaEmail(getActivity(),csvFile);
//            }else{
//                Log.e("FRAGMENT","FRAGMENT LIST"+psdSummaryDaoArrayList.size());
////                    Toast.makeText(getActivity(),"Save the data first ?",Toast.LENGTH_SHORT).show();
//            }
//        });
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

        // Add data to mSeries1 (xFrequencyMagnitude)
        for (int i = 0; i < xFrequencyMagnitude.size(); i++) {
            Float[] magnitudeArray = xFrequencyMagnitude.get(i);  // Extract the Float[] array
            if (magnitudeArray != null && magnitudeArray.length > 0) {
                for (int j = 0; j < magnitudeArray.length; j++) {  // Iterate through array elements
                    mFreSeries1.appendData(new DataPoint(j, magnitudeArray[j]), true, magnitudeArray.length);
                }
            }
        }
        // Add data to mSeries2 (yFrequencyMagnitude)

        for (int i = 0; i < yFrequencyMagnitude.size(); i++) {
            Float[] magnitudeArray = yFrequencyMagnitude.get(i);  // Extract the Float[] array
            if (magnitudeArray != null && magnitudeArray.length > 0) {
                for (int j = 0; j < magnitudeArray.length; j++) {  // Iterate through array elements
                    mFreSeries2.appendData(new DataPoint(j, magnitudeArray[j]), true, magnitudeArray.length);
                }
            }
        }

        // Add data to mSeries3 (zFrequencyMagnitude)
        for (int i = 0; i < zFrequencyMagnitude.size(); i++) {
            Float[] magnitudeArray = zFrequencyMagnitude.get(i);  // Extract the Float[] array
            if (magnitudeArray != null && magnitudeArray.length > 0) {
                for (int j = 0; j < magnitudeArray.length; j++) {  // Iterate through array elements
                    mFreSeries3.appendData(new DataPoint(j, magnitudeArray[j]), true, magnitudeArray.length);
                }
            }
        }

        // Add the series to the graph
        frequency_chart.addSeries(mFreSeries1);
        frequency_chart.addSeries(mFreSeries2);
        frequency_chart.addSeries(mFreSeries3);
        // Customize series colors if needed

        mFreSeries1.setColor(Color.BLACK);
        mFreSeries2.setColor(Color.GREEN);
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

//    private void displacement_loadChart(){
//        mDispSeries1 = new LineGraphSeries<>();
//        mDispSeries2 = new LineGraphSeries<>();
//        mDispSeries3 = new LineGraphSeries<>();
//
//        // Add data to mSeries1 (xDisplacement)
//        for (int i = 0; i < xDisplacement.size(); i++) {
//            mDispSeries1.appendData(new DataPoint(i, xDisplacement.get(i)), true, xDisplacement.size());
//        }
//        // Add data to mSeries2 (yDisplacement)
//        for (int i = 0; i < yDisplacement.size(); i++) {
//            mDispSeries2.appendData(new DataPoint(i, yDisplacement.get(i)), true, yDisplacement.size());
//        }
//        // Add data to mSeries3 (zDisplacement)
//        for (int i = 0; i < zDisplacement.size(); i++) {
//            mDispSeries3.appendData(new DataPoint(i, zDisplacement.get(i)), true, zDisplacement.size());
//        }
//        // Add the series to the graph
//        displacement_graph.addSeries(mDispSeries1);
//        displacement_graph.addSeries(mDispSeries2);
//        displacement_graph.addSeries(mDispSeries3);
//
//        // Customize series colors if needed
//        mDispSeries1.setColor(Color.BLACK);
//        mDispSeries2.setColor(Color.GREEN);
//        mDispSeries3.setColor(Color.RED);
//
//
//        // Set custom X-axis labels
//        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(displacement_graph);
//        //Ensure the number of labels matches the unique X-values in your series
//        String[] xAxisLabels = new String[] {"0","10","20", "30", "40", "50", "60", "70", "80", "90"};
//        staticLabelsFormatter.setHorizontalLabels(xAxisLabels);
//
//// Apply the static labels formatter to the graph
//        displacement_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
//
//// Set the exact X-axis range to match your data
//        displacement_graph.getViewport().setMinX(0);  // Set min X value
//        displacement_graph.getViewport().setMaxX(90);  // Set max X value
//        displacement_graph.getViewport().setXAxisBoundsManual(true);
//        // Optional: Set the number of horizontal labels (can be the same as your custom labels)
//        displacement_graph.getGridLabelRenderer().setNumHorizontalLabels(xAxisLabels.length);
//        // Optional: Disable vertical labels if you don't need them
//        staticLabelsFormatter.setVerticalLabels(null);
//        // vertical y axis hide
//        displacement_graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
//
//        // Customize graph settings if needed
//        displacement_graph.getViewport().setScalable(true);  // enables horizontal zooming and scrolling
//        displacement_graph.getViewport().setScrollable(true);  // enables horizontal scrolling
//        displacement_graph.getViewport().setScalableY(true);  // enables vertical zooming and scrolling
//        displacement_graph.getViewport().setScrollableY(true);  // enables vertical scrolling
//    }

    public void saveDisplacementDataToCSV(Context context, ArrayList<PsdSummaryDao> psdSummaryDaoArrayList) throws IOException {
        // Check if external storage is available
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.e("CSV Creation", "External storage not mounted or writable");
            return;
        }
        File file = new File(requireActivity().getCacheDir().getAbsolutePath() + "/" + requireActivity().getResources().getString(R.string.psd_summary) + ".csv");

        // Create a directory and CSV file in external storage
        if (!file.exists() && !file.createNewFile()) {
            Toast.makeText(requireActivity(), "SOMETHING WENT WRONG", Toast.LENGTH_LONG).show();
        }
        try (FileWriter writer = new FileWriter(file)) {
            // Write CSV header
            writer.append("xDisplacement,yDisplacement,zDisplacement,xFrequency,yFrequency,zFrequency\n");

            // Write data to CSV
            for (PsdSummaryDao dao : psdSummaryDaoArrayList) {
                writer.append(String.valueOf(dao.xDisplacement))
                        .append(",")
                        .append(String.valueOf(dao.yDisplacement))
                        .append(",")
                        .append(String.valueOf(dao.zDisplacement))
                        .append(",")
                        .append(String.valueOf(dao.xFrequencyMagnitude))
                        .append(",")
                        .append(String.valueOf(dao.yFrequencyMagnitude))
                        .append(",")
                        .append(String.valueOf(dao.zFrequencyMagnitude))
                        .append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;  // Re-throw exception if needed
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void shareCSVFileViaEmail(Context context, File csvFile) {
        if (csvFile.exists()) {
            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", csvFile);
            Log.e("PSD",context.getPackageName() + ".provider");

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/csv");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Displacement Data CSV");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find the attached CSV file containing the displacement data.");
            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(emailIntent, "Send email using:"));
            } else {
                Toast.makeText(context, "No email client found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "CSV file not found.", Toast.LENGTH_SHORT).show();
        }
    }



}