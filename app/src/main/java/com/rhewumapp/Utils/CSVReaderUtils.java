package com.rhewumapp.Utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReaderUtils {

    public static List<double[]> readVibCheckerData() {
        List<double[]> data = new ArrayList<>();
        try {
            // Locate the file in Downloads
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File csvFile = new File(downloadsDir, "VibCheckerData.csv");

            if (!csvFile.exists()) {
                Log.e("CSV Reader", "File not found: " + csvFile.getAbsolutePath());
                return data; // Return empty list if file doesn't exist
            }

            // Read the file
            try (InputStream inputStream = new FileInputStream(csvFile)) {
                data = readCSV(inputStream);
            }

        } catch (IOException e) {
            Log.e("CSV Reader", "Error reading CSV file", e);
        }
        return data;
    }

    private static List<double[]> readCSV(InputStream is) throws IOException {
        List<double[]> data = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        boolean isHeader = true;

        while ((line = br.readLine()) != null) {
            if (isHeader) {
                isHeader = false; // Skip the header
                continue;
            }

            String[] values = line.split(",");
            Log.i("CSV Debug", "Parsed Row: " + String.join(", ", values));

            // Ensure correct parsing based on the CSV structure
            try {
                data.add(new double[]{
                        Double.parseDouble(values[2]), // x
                        Double.parseDouble(values[3]), // y
                        Double.parseDouble(values[4]), // z
                        Double.parseDouble(values[1])  // time
                });
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                Log.e("CSV Reader", "Error parsing line: " + line, e);
            }
        }
        return data;
    }
}
