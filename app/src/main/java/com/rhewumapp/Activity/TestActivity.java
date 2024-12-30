package com.rhewumapp.Activity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.opencsv.CSVReader;
import com.rhewumapp.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("app_raw_data_25hz.csv");
            ArrayList<float[]> accData = readCsvData(inputStream);

            float[] means = calculateMean(accData);
            Log.e("Means Value","Means value is :::"+ Arrays.toString(means));
            ArrayList<float[]> normalizedData = normalizeData(accData, means);
            Log.e("Normalised Value","Normalised value is :::"+normalizedData);

            displayData(normalizedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public float[] calculateMean(ArrayList<float[]> data) {
        float sumX = 0, sumY = 0, sumZ = 0;
        for (float[] row : data) {
            sumX += row[0];
            sumY += row[1];
            sumZ += row[2];
        }
        int size = data.size();
        return new float[]{sumX / size, sumY / size, sumZ / size};
    }

    public ArrayList<float[]> normalizeData(ArrayList<float[]> data, float[] means) {
        ArrayList<float[]> normalizedData = new ArrayList<>();
        for (float[] row : data) {
            normalizedData.add(new float[]{
                    row[0] - means[0],
                    row[1] - means[1],
                    row[2] - means[2]
            });
        }
        return normalizedData;
    }

    public ArrayList<float[]> readCsvData(InputStream inputStream) {
        ArrayList<float[]> accData = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] nextLine;
            boolean isHeader = true;
            while ((nextLine = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip header
                    continue;
                }
                float x = Float.parseFloat(nextLine[0]); // Assuming xRawValue is in column 0
                float y = Float.parseFloat(nextLine[1]); // yRawValue
                float z = Float.parseFloat(nextLine[2]); // zRawValue
                accData.add(new float[]{x, y, z});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accData;
    }
    public void displayData(ArrayList<float[]> data) {
        LineChart chart = findViewById(R.id.lineChart);
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            entries.add(new Entry((float) i, (int) data.get(i)[0])); // xRawValue as example
        }
        LineDataSet dataSet = new LineDataSet(entries, "X Axis Data");
        LineData lineData = new LineData((List<String>) dataSet);
        chart.setData(lineData);
        chart.invalidate(); // Refresh chart
    }


}