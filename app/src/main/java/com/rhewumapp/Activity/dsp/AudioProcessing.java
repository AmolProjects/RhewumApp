package com.rhewumapp.Activity.dsp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.util.Log;
import androidx.core.app.ActivityCompat;

import com.rhewumapp.Activity.fft.FFTHelper;

public class AudioProcessing extends Thread {
    private static final String TAG = "AudioProcessing";
    private static AudioProcessingListener mListener;
    private FFTHelper mFFT;
    private int mMinBufferSize;
    private int mNumberOfFFTPoints;
    private AudioRecord mRecorder;
    private double mSampleRateInHz;
    private boolean stopped;
    private Context context;

    public void notifyListenersOnFFTSamplesAvailableForDrawing(double[] dArr) {
    }

    public AudioProcessing(Context context,double d, int i) {
        this.context=context;
        this.mSampleRateInHz = d;
        this.mNumberOfFFTPoints = i;
        this.mFFT = new FFTHelper(d, i);
        start();
    }

    public void run() {
        runWithAudioRecord();
    }

    private void runWithSignalHelper() {
        int i = this.mNumberOfFFTPoints * 2;
        while (!this.stopped) {
            byte[] bArr = new byte[i];
            int read = SignalHelper.SignalGenerator.read(bArr, 100, this.mNumberOfFFTPoints, this.mSampleRateInHz, true, false);
            if (read > 0) {
                FFTHelper fFTHelper = this.mFFT;
                if (fFTHelper != null) {
                    notifyListenersOnFFTSamplesAvailableForDrawing(fFTHelper.calculateFFT(bArr));
                }
            } else {
                String str = TAG;
                Log.e(str, "There was an error reading the audio device - ERROR: " + read);
            }
        }
    }

    private void runWithAudioRecord() {
        int i = this.mNumberOfFFTPoints * 2;
        this.mMinBufferSize = AudioRecord.getMinBufferSize((int) this.mSampleRateInHz, 16, 2);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        AudioRecord audioRecord = new AudioRecord(1, (int) this.mSampleRateInHz, 16, 2, this.mMinBufferSize * 10);
        this.mRecorder = audioRecord;
        audioRecord.startRecording();
        while (!this.stopped) {
            byte[] bArr = new byte[i];
            int read = this.mRecorder.read(bArr, 0, i);
            if (read > 0) {
                FFTHelper fFTHelper = this.mFFT;
                if (fFTHelper != null) {
                    notifyListenersOnFFTSamplesAvailableForDrawing(fFTHelper.calculateFFT(bArr));
                }
            } else {
                String str = TAG;
                Log.e(str, "There was an error reading the audio device - ERROR: " + read);
            }
        }
        this.mRecorder.stop();
        this.mRecorder.release();
    }

    public double getPeakFrequency() {
        return this.mFFT.getPeakFrequency();
    }

    public double getPeakFrequency(int[] iArr) {
        return this.mFFT.getPeakFrequency(iArr);
    }

    public double getMaxFFTSample() {
        return this.mFFT.getMaxFFTSample();
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
}
