package com.rhewumapp.Activity.stft;

public class RealDoubleFFT extends RealDoubleFFT_Mixed {
    private double[] ch;
    private int ndim;
    public double norm_factor;
    private double[] wavetable;

    public RealDoubleFFT(int i) {
        this.ndim = i;
        this.norm_factor = (double) i;
        double[] dArr = this.wavetable;
        if (dArr == null || dArr.length != (i * 2) + 15) {
            this.wavetable = new double[((i * 2) + 15)];
        }
        rffti(i, this.wavetable);
        this.ch = new double[i];
    }

    public void ft(double[] dArr) {
        int length = dArr.length;
        int i = this.ndim;
        if (length == i) {
            rfftf(i, dArr, this.wavetable, this.ch);
            return;
        }
        throw new IllegalArgumentException("The length of data can not match that of the wavetable");
    }
}
