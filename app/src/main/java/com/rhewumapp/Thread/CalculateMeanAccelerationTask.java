package com.rhewumapp.Thread;

import static com.rhewumapp.Activity.VibCheckerAccelerometer2Activity.calculateMeanAcceleration;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.rhewumapp.Utils.CSVReaderUtils;

import java.util.List;

public class CalculateMeanAccelerationTask extends AsyncTask<Void, Void, Void> {
    private ProgressBar progressBar;

    // Constructor to pass ProgressBar from the activity/fragment
    public CalculateMeanAccelerationTask(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Show the progress bar before starting the task
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Call the method that processes the data
        List<double[]> vibCheckerData = CSVReaderUtils.readVibCheckerData();
        calculateMeanAcceleration(vibCheckerData);  // Calling your method

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // Hide the progress bar after task completion
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        // You can update the UI after the task is complete
        //onDataProcessingComplete();
    }
}
