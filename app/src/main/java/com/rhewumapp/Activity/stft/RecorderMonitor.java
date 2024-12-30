package com.rhewumapp.Activity.stft;

import android.os.SystemClock;
import android.util.Log;

public class RecorderMonitor {
    String TAG;
    final String TAG0 = "RecorderMonitor::";
    int bufferSampleSize;
    boolean lastCheckOverrun = false;
    long lastOverrunTime;
    long nSamplesRead;
    int sampleRate;
    double sampleRateReal;
    long timeStarted;
    long timeUpdateInterval;
    long timeUpdateOld;

    public RecorderMonitor(int i, int i2, String str) {
        this.sampleRate = i;
        this.bufferSampleSize = i2;
        this.timeUpdateInterval = 2000;
        this.TAG = str + "RecorderMonitor::";
    }

    public void start() {
        this.nSamplesRead = 0;
        this.lastOverrunTime = 0;
        long uptimeMillis = SystemClock.uptimeMillis();
        this.timeStarted = uptimeMillis;
        this.timeUpdateOld = uptimeMillis;
        this.sampleRateReal = (double) this.sampleRate;
    }

    public boolean updateState(int i) {
        int i2 = i;
        long uptimeMillis = SystemClock.uptimeMillis();
        long j = this.nSamplesRead;
        if (j == 0) {
            this.timeStarted = uptimeMillis - ((long) ((i2 * 1000) / this.sampleRate));
        }
        long j2 = j + ((long) i2);
        this.nSamplesRead = j2;
        long j3 = this.timeUpdateOld;
        long j4 = this.timeUpdateInterval;
        if (j3 + j4 > uptimeMillis) {
            return false;
        }
        long j5 = j3 + j4;
        this.timeUpdateOld = j5;
        if (j5 + j4 <= uptimeMillis) {
            this.timeUpdateOld = uptimeMillis;
        }
        double d = (double) (uptimeMillis - this.timeStarted);
        double d2 = this.sampleRateReal;
        Double.isNaN(d);
        long j6 = (long) ((d * d2) / 1000.0d);
        double d3 = (double) j2;
        Double.isNaN(d3);
        double d4 = d3 / d2;
        double d5 = (double) j6;
        Double.isNaN(d5);
        double d6 = d5 / d2;
        if (j6 > ((long) this.bufferSampleSize) + j2) {
            String str = this.TAG;
            StringBuilder sb = new StringBuilder();
            long j7 = uptimeMillis;
            sb.append("Looper::run(): Buffer Overrun occured !\n should read ");
            sb.append(j6);
            sb.append(" (");
            double round = (double) Math.round(d6 * 1000.0d);
            Double.isNaN(round);
            sb.append(round / 1000.0d);
            sb.append("s), actual read ");
            sb.append(this.nSamplesRead);
            sb.append(" (");
            double round2 = (double) Math.round(d4 * 1000.0d);
            Double.isNaN(round2);
            sb.append(round2 / 1000.0d);
            sb.append("s)\n diff ");
            sb.append(j6 - this.nSamplesRead);
            sb.append(" (");
            double round3 = (double) Math.round((d6 - d4) * 1000.0d);
            Double.isNaN(round3);
            sb.append(round3 / 1000.0d);
            sb.append("s) sampleRate = ");
            double round4 = (double) Math.round(this.sampleRateReal * 100.0d);
            Double.isNaN(round4);
            sb.append(round4 / 100.0d);
            sb.append("\n Overrun counter reseted.");
            Log.w(str, sb.toString());
            uptimeMillis = j7;
            this.lastOverrunTime = uptimeMillis;
            this.nSamplesRead = 0;
        }
        long j8 = this.nSamplesRead;
        String str2 = "\n Overrun counter reseted.";
        int i3 = this.sampleRate;
        String str3 = "s) sampleRate = ";
        double d7 = d4;
        if (j8 > ((long) (i3 * 10))) {
            double d8 = d6;
            double d9 = (double) j8;
            Double.isNaN(d9);
            double d10 = (double) (uptimeMillis - this.timeStarted);
            Double.isNaN(d10);
            double d11 = (this.sampleRateReal * 0.9d) + (((d9 * 1000.0d) / d10) * 0.1d);
            this.sampleRateReal = d11;
            double d12 = (double) i3;
            Double.isNaN(d12);
            double abs = Math.abs(d11 - d12);
            double d13 = (double) this.sampleRate;
            Double.isNaN(d13);
            if (abs > d13 * 0.0145d) {
                String str4 = this.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Looper::run(): Sample rate inaccurate, possible hardware problem !\n should read ");
                sb2.append(j6);
                sb2.append(" (");
                double round5 = (double) Math.round(d8 * 1000.0d);
                Double.isNaN(round5);
                sb2.append(round5 / 1000.0d);
                sb2.append("s), actual read ");
                sb2.append(this.nSamplesRead);
                sb2.append(" (");
                double round6 = (double) Math.round(d7 * 1000.0d);
                Double.isNaN(round6);
                sb2.append(round6 / 1000.0d);
                sb2.append("s)\n diff ");
                sb2.append(j6 - this.nSamplesRead);
                sb2.append(" (");
                double round7 = (double) Math.round((d8 - d7) * 1000.0d);
                Double.isNaN(round7);
                sb2.append(round7 / 1000.0d);
                sb2.append(str3);
                double round8 = (double) Math.round(this.sampleRateReal * 100.0d);
                Double.isNaN(round8);
                sb2.append(round8 / 100.0d);
                sb2.append(str2);
                Log.w(str4, sb2.toString());
                this.nSamplesRead = 0;
            }
        }
        this.lastCheckOverrun = this.lastOverrunTime == uptimeMillis;
        return true;
    }

    public boolean getLastCheckOverrun() {
        return this.lastCheckOverrun;
    }

    public long getLastOverrunTime() {
        return this.lastOverrunTime;
    }

    public double getSampleRate() {
        return this.sampleRateReal;
    }
}
