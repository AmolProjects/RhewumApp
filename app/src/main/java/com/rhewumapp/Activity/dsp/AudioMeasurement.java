package com.rhewumapp.Activity.dsp;

import com.rhewumapp.Activity.MeshConveterData.Constants;

import java.util.Date;

public class AudioMeasurement {
    public static final double[] frequencyAttenuations = {-11.8d, -4.199999999999999d, 5.199999999999999d, 10.9d, 15.9d, 16.9d, 18.4d, 20.8d, 21.8d, 22.8d};
    public static final double[] frequencyValues = {31.0d, 63.0d, 125.0d, 250.0d, 500.0d, 1000.0d, 2000.0d, 4000.0d, 8000.0d, 16000.0d};
    public double[] currentSpectrumDBValues;
    public String durationOfMeasurement = "00 : 00";
    public double finalAmplitudeDBValue;
    public double[] finalSpectrumDBValues;
    public double maxAmplitudeDBValue;
    public double[] maxSpectrumDBValues;
    public Date measurementDate;
    public boolean measurementHasBeenNamed = false;
    public String measurementName = "";
    public int measurementNumberIfNotNamed = 1;
    public int numberOfSamples;
    public double tmpCurrentAmplitudeDBValue;
    public double tmpCurrentSquaredAmplitudeValue;
    public double[] tmpCurrentSquaredSpectrumValues;
    public double tmpTotalSquaredAmplitudeValue;
    public double[] tmpTotalSquaredSpectrumValues;

    public static String TAG() {
        return "SingleMeasurement";
    }

    public AudioMeasurement() {
        double[] dArr = frequencyValues;
        this.finalSpectrumDBValues = new double[dArr.length];
        this.tmpTotalSquaredSpectrumValues = new double[dArr.length];
        this.tmpCurrentSquaredSpectrumValues = new double[dArr.length];
        this.currentSpectrumDBValues = new double[dArr.length];
        this.maxSpectrumDBValues = new double[dArr.length];
        this.finalAmplitudeDBValue = Constants.PI;
        this.tmpCurrentSquaredAmplitudeValue = Constants.PI;
        this.numberOfSamples = 0;
        this.tmpCurrentAmplitudeDBValue = Constants.PI;
        resetAllData();
    }

    public static double safeGetSquaredAmplitudeAtIndex(double[] dArr, int i) {
        double d = (i <= 0 || i >= dArr.length) ? Constants.PI : dArr[i] * dArr[i];
        int i2 = i + 1;
        return i2 < dArr.length ? d + (dArr[i2] * dArr[i2]) : d;
    }

    public static double toDB(double d) {
        return (Math.log(d) * 5.0d) + 7.0d;
    }

    public void assimilateNewDataFromRawFFT(double[] dArr) {
        computeAmplitudesFromRawFFT(dArr, 44100.0d);
        this.numberOfSamples++;
        double d = 0.0d;
        for (int i = 0; i < frequencyValues.length; i++) {
            double[] dArr2 = this.tmpTotalSquaredSpectrumValues;
            double d2 = dArr2[i];
            double[] dArr3 = this.tmpCurrentSquaredSpectrumValues;
            dArr2[i] = d2 + dArr3[i];
            double[] dArr4 = this.currentSpectrumDBValues;
            double db = toDB(dArr3[i]);
            double[] dArr5 = frequencyAttenuations;
            dArr4[i] = db + dArr5[i];
            double d3 = this.tmpTotalSquaredSpectrumValues[i];
            double d4 = (double) this.numberOfSamples;
            Double.isNaN(d4);
            double db2 = toDB(d3 / d4) + dArr5[i];
            double[] dArr6 = this.finalSpectrumDBValues;
            if (db2 <= Constants.PI) {
                db2 = 0.0d;
            }
            dArr6[i] = db2;
            double[] dArr7 = this.maxSpectrumDBValues;
            dArr7[i] = Math.max(dArr7[i], dArr6[i]);
            d += Math.pow(10.0d, this.finalSpectrumDBValues[i] / 10.0d);
        }
        double d5 = this.tmpTotalSquaredAmplitudeValue;
        double d6 = this.tmpCurrentSquaredAmplitudeValue;
        this.tmpTotalSquaredAmplitudeValue = d5 + d6;
        this.tmpCurrentAmplitudeDBValue = toDB(d6) + 12.0d;
        double log10 = Math.log10(d) * 10.0d;
        this.finalAmplitudeDBValue = log10;
        this.maxAmplitudeDBValue = Math.max(this.maxAmplitudeDBValue, log10);
    }

    public void computeAmplitudesFromRawFFT(double[] dArr, double d) {
        this.tmpCurrentSquaredAmplitudeValue = Constants.PI;
        int i = 0;
        while (true) {
            double[] dArr2 = frequencyValues;
            if (i < dArr2.length) {
                double d2 = dArr2[i] / (d * 0.5d);
                double length = (double) dArr.length;
                Double.isNaN(length);
                int i2 = ((int) (d2 * length * 0.5d)) * 2;
                if (i2 + 3 >= dArr.length) {
                    i2 -= 4;
                }
                double safeGetSquaredAmplitudeAtIndex = safeGetSquaredAmplitudeAtIndex(dArr, i2) + safeGetSquaredAmplitudeAtIndex(dArr, i2 - 2) + safeGetSquaredAmplitudeAtIndex(dArr, i2 + 2);
                this.tmpCurrentSquaredAmplitudeValue += safeGetSquaredAmplitudeAtIndex;
                this.tmpCurrentSquaredSpectrumValues[i] = safeGetSquaredAmplitudeAtIndex;
                i++;
            } else {
                return;
            }
        }
    }

    public void resetAllData() {
        this.numberOfSamples = 0;
        this.tmpTotalSquaredAmplitudeValue = 1.0E-5d;
        this.maxAmplitudeDBValue = -100.0d;
        for (int i = 0; i < frequencyValues.length; i++) {
            this.tmpTotalSquaredSpectrumValues[i] = 1.0E-5d;
            this.tmpCurrentSquaredSpectrumValues[i] = 1.0E-5d;
            this.currentSpectrumDBValues[i] = -100.0d;
            this.finalSpectrumDBValues[i] = -100.0d;
            this.maxSpectrumDBValues[i] = -100.0d;
        }
    }

    public String toString() {
        String str = "values= { ";
        for (int i = 0; i < frequencyValues.length; i++) {
            str = str + String.format("%.1f ", new Object[]{Double.valueOf(this.finalSpectrumDBValues[i])});
        }
        return str + "}";
    }
}
