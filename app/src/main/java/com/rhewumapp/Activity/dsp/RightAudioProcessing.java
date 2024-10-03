package com.rhewumapp.Activity.dsp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioRecord;

import androidx.core.app.ActivityCompat;


import com.rhewumapp.Activity.stft.RealDoubleFFT;

import java.util.ArrayList;
import java.util.Iterator;

public class RightAudioProcessing extends Thread {
    public static final int SAMPLE_RATE = 44100;
    public static final int SAMPLE_SIZE = 16384;
    private static final String TAG = "RightAudioProcessing";
    private static AudioProcessingListener mListener;
    private AudioRecord mRecorder;
    private AudioMeasurement mSingleMeasurement = new AudioMeasurement();
    private boolean stopped;
    Context context;
    private final RealDoubleFFT transformer = new RealDoubleFFT(16384);


    public RightAudioProcessing(Context context) {
        this.context=context;
        start();
    }

    public void run() {
        runWithAudioRecord();
    }

    private void runWithAudioRecord() {
        resetCurrentMeasurement();
        ArrayList<Integer> arrayList = new ArrayList<>();
        short[] sArr = new short[16384];
        double[] dArr = new double[16384];

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        AudioRecord audioRecord = new AudioRecord(1, SAMPLE_RATE, 16, 2, 16384);
        this.mRecorder = audioRecord;
        audioRecord.startRecording();
        while (!this.stopped) {
            int read = this.mRecorder.read(sArr, 0, 16384);
            if (read > 0) {
                for (int i = 0; i < read; i++) {
                    arrayList.add(Integer.valueOf(sArr[i]));
                }
                if (arrayList.size() >= 16384) {
                    int i2 = -1;
                    arrayList = HammingWindow(arrayList);
                    Iterator<Integer> it = arrayList.iterator();
                    while (it.hasNext()) {
                        int intValue = it.next().intValue();
                        i2++;
                        if (i2 >= 16384) {
                            break;
                        }
                        double d = (double) intValue;
                        Double.isNaN(d);
                        dArr[i2] = d / 32768.0d;
                    }
                    arrayList.clear();
                    this.transformer.ft(dArr);
                    this.mSingleMeasurement.assimilateNewDataFromRawFFT(dArr);
                    notifyListenersOnFFTSamplesAvailableForDrawing(this.mSingleMeasurement);
                    try {
                        Thread.sleep(333);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.mRecorder.stop();
        this.mRecorder.release();
    }

    public ArrayList<Integer> HammingWindow(ArrayList<Integer> arrayList) {
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        Iterator<Integer> it = arrayList.iterator();
        int i = 0;
        while (it.hasNext()) {
            int intValue = it.next().intValue();
            double d = (double) i;
            Double.isNaN(d);
            double d2 = (double) (intValue * 1000);
            Double.isNaN(d2);
            arrayList2.add(Integer.valueOf((int) Math.round((d2 * (0.54d - (Math.cos(1.4247908812398436E-4d * d) * 0.46d))) / 1000.0d)));
            i++;
            if (i >= 44100) {
                break;
            }
        }
        return arrayList2;
    }

    public void resetCurrentMeasurement() {
        this.mSingleMeasurement = new AudioMeasurement();
    }

    public void close() {
        this.stopped = true;
    }

    public static void registerDrawableFFTSamplesAvailableListener(AudioProcessingListener audioProcessingListener) {
        mListener = audioProcessingListener;
    }

    public static void unregisterDrawableFFTSamplesAvailableListener() {
        mListener = null;
    }

    public void notifyListenersOnFFTSamplesAvailableForDrawing(AudioMeasurement audioMeasurement) {
        AudioProcessingListener audioProcessingListener = mListener;
        if (audioProcessingListener != null) {
            audioProcessingListener.onDrawableFFTSignalAvailable(audioMeasurement);
        }
    }
}
