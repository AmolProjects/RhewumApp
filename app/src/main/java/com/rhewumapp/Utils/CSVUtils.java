package com.rhewumapp.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVUtils {

    public static void saveVibCheckerDataToCSV(Context context, String date, long timeInterval, float ax, float ay, float az) {
        // Check if external storage is available
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.e("CSV Creation", "External storage not mounted or writable");
            //  Toast.makeText(context, "Storage not available", Toast.LENGTH_LONG).show();
            return;
        }

        // Create file in Downloads directory (publicly accessible)
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
            Log.e("CSV Creation", "Failed to create Downloads directory");
            //  Toast.makeText(context, "Failed to access Downloads folder", Toast.LENGTH_LONG).show();
            return;
        }

        // Define the file name and location
        File file = new File(downloadsDir, "VibCheckerData.csv");


        try (FileWriter writer = new FileWriter(file, true)) { // Append mode
            if (file.length() == 0) {
                // Write CSV header if file is new
                writer.append("DateTime,Time [Ms],xRawValue,yRawValue,zRawValue\n");
            }

            // Write data to CSV
            writer.append(date)
                    .append(",")
                    .append(String.valueOf(timeInterval))
                    .append(",")
                    .append(String.valueOf(ax))
                    .append(",")
                    .append(String.valueOf(ay))
                    .append(",")
                    .append(String.valueOf(az))
                    .append("\n");

            writer.flush();

            // Notify user
            //   Toast.makeText(context, "CSV file saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            Log.d("CSV Creation", "File saved at: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("CSV Creation", "Error writing file", e);
            //  Toast.makeText(context, "Error saving CSV file", Toast.LENGTH_LONG).show();
        }
    }
}

