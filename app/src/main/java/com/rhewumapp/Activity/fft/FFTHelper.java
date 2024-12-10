package com.rhewumapp.Activity.fft;


import com.rhewumapp.Activity.MeshConveterData.Constants;

import kotlin.UByte;

public class FFTHelper {
    private static double dbaValue;
    private double mMaxFFTSample;
    private int mNumberOfFFTPoints;
    private double mPeakFreq;
    private int mPeakPos;
    private double mSampleRateInHz;

    public FFTHelper() {
    }

    public FFTHelper(double d, int i) {
        this.mSampleRateInHz = d;
        this.mNumberOfFFTPoints = i;
    }

    public double[] calculateFFT(byte[] bArr) {
        int i = this.mNumberOfFFTPoints;
        Complex[] complexArr = new Complex[i];
        double[] dArr = new double[(i / 2)];
        for (int i2 = 0; i2 < this.mNumberOfFFTPoints; i2++) {
            int i3 = i2 * 2;
            double d = (double) ((bArr[i3 + 1] << 8) | (bArr[i3] & UByte.MAX_VALUE));
            Double.isNaN(d);
            complexArr[i2] = new Complex(d / 32768.0d, Constants.PI);
        }
        Complex[] fft = FFT.fft(complexArr);
        this.mMaxFFTSample = Constants.PI;
        this.mPeakPos = 0;
        for (int i4 = 0; i4 < this.mNumberOfFFTPoints / 2; i4++) {
            dArr[i4] = Math.sqrt(Math.pow(fft[i4].re(), 2.0d) + Math.pow(fft[i4].im(), 2.0d));
            if (dArr[i4] > this.mMaxFFTSample) {
                this.mMaxFFTSample = dArr[i4];
                this.mPeakPos = i4;
                double log10 = Math.log10(getPeakFrequency()) * 20.0d;
                dbaValue = log10;
                setDBAValue(log10);
            }
        }
        return dArr;
    }

    public static double getDBAValue() {
        return dbaValue;
    }

    public void setDBAValue(double d) {
        dbaValue = d;
    }

    public double getPeakFrequency() {
        double d = (double) this.mPeakPos;
        double d2 = this.mSampleRateInHz;
        double d3 = (double) this.mNumberOfFFTPoints;
        Double.isNaN(d3);
        Double.isNaN(d);
        double d4 = d * (d2 / d3);
        this.mPeakFreq = d4;
        return d4;
    }

    public double getPeakFrequency(int[] iArr) {
        int i = 0;
        int i2 = iArr[0];
        int i3 = 1;
        while (true) {
            int i4 = this.mNumberOfFFTPoints;
            if (i3 < i4 / 2) {
                if (iArr[i3] > i2) {
                    i2 = iArr[i3];
                    i = i3;
                }
                i3++;
            } else {
                double d = (double) i;
                double d2 = this.mSampleRateInHz;
                double d3 = (double) i4;
                Double.isNaN(d3);
                Double.isNaN(d);
                return d * (d2 / d3);
            }
        }
    }

    public double getMaxFFTSample() {
        return this.mMaxFFTSample;
    }
}
