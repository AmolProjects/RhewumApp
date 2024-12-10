package com.rhewumapp.Activity.dsp;

import com.rhewumapp.Activity.MeshConveterData.Constants;

public class FFTUtil {
    public static double[] fft2(double[] dArr, double[] dArr2, boolean z) {
        double[] dArr3 = dArr;
        int length = dArr3.length;
        double d = (double) length;
        double log = Math.log(d) / Math.log(2.0d);
        int i = (int) log;
        double d2 = (double) i;
        Double.isNaN(d2);
        if (d2 - log != Constants.PI) {
            System.out.println("The number of elements is not a power of 2.");
            return null;
        }
        int i2 = length / 2;
        int i3 = i - 1;
        double[] dArr4 = new double[length];
        double[] dArr5 = new double[length];
        double d3 = z ? -6.283185307179586d : 6.283185307179586d;
        for (int i4 = 0; i4 < length; i4++) {
            dArr4[i4] = dArr3[i4];
            dArr5[i4] = dArr2[i4];
        }
        for (int i5 = 1; i5 <= i; i5++) {
            int i6 = 0;
            while (i6 < length) {
                int i7 = 1;
                while (i7 <= i2) {
                    double bitreverseReference = (double) bitreverseReference(i6 >> i3, i);
                    Double.isNaN(bitreverseReference);
                    Double.isNaN(d);
                    double d4 = (bitreverseReference * d3) / d;
                    double cos = Math.cos(d4);
                    double sin = Math.sin(d4);
                    int i8 = i6 + i2;
                    double d5 = (dArr4[i8] * cos) + (dArr5[i8] * sin);
                    double d6 = (dArr5[i8] * cos) - (dArr4[i8] * sin);
                    dArr4[i8] = dArr4[i6] - d5;
                    dArr5[i8] = dArr5[i6] - d6;
                    dArr4[i6] = dArr4[i6] + d5;
                    dArr5[i6] = dArr5[i6] + d6;
                    i6++;
                    i7++;
                    length = length;
                }
                int i9 = length;
                i6 += i2;
            }
            int i10 = length;
            i3--;
            i2 /= 2;
        }
        int i11 = length;
        for (int i12 = 0; i12 < i11; i12++) {
            int bitreverseReference2 = bitreverseReference(i12, i);
            if (bitreverseReference2 > i12) {
                double d7 = dArr4[i12];
                double d8 = dArr5[i12];
                dArr4[i12] = dArr4[bitreverseReference2];
                dArr5[i12] = dArr5[bitreverseReference2];
                dArr4[bitreverseReference2] = d7;
                dArr5[bitreverseReference2] = d8;
            }
        }
        int i13 = i11 * 2;
        double[] dArr6 = new double[i13];
        double sqrt = 1.0d / Math.sqrt(d);
        for (int i14 = 0; i14 < i13; i14 += 2) {
            int i15 = i14 / 2;
            dArr6[i14] = dArr4[i15] * sqrt;
            dArr6[i14 + 1] = dArr5[i15] * sqrt;
        }
        return dArr6;
    }

    private static int bitreverseReference(int i, int i2) {
        int i3 = 1;
        int i4 = 0;
        while (i3 <= i2) {
            int i5 = i / 2;
            i4 = ((i4 * 2) + i) - (i5 * 2);
            i3++;
            i = i5;
        }
        return i4;
    }

    public static void fft(double[][] dArr) {
        double[][] dArr2 = dArr;
        int length = dArr2.length;
        if (Integer.highestOneBit(length) == length) {
            int numberOfLeadingZeros = Integer.numberOfLeadingZeros(length) + 1;
            for (int i = 0; i < length; i++) {
                int reverse = Integer.reverse(i) >>> numberOfLeadingZeros;
                if (reverse > i) {
                    double[] dArr3 = dArr2[reverse];
                    dArr2[reverse] = dArr2[i];
                    dArr2[i] = dArr3;
                }
            }
            for (int i2 = 2; i2 <= length; i2 += i2) {
                int i3 = 0;
                while (true) {
                    int i4 = i2 / 2;
                    if (i3 >= i4) {
                        break;
                    }
                    double d = (double) (i3 * -2);
                    Double.isNaN(d);
                    double d2 = (double) i2;
                    Double.isNaN(d2);
                    double d3 = (d * 3.141592653589793d) / d2;
                    double cos = Math.cos(d3);
                    double sin = Math.sin(d3);
                    for (int i5 = 0; i5 < length / i2; i5++) {
                        int i6 = (i5 * i2) + i3;
                        double[] dArr4 = dArr2[i6 + i4];
                        double d4 = dArr4[0];
                        double d5 = dArr4[1];
                        double[] dArr5 = dArr2[i6];
                        double d6 = dArr5[0];
                        double d7 = dArr5[1];
                        double d8 = (cos * d4) - (sin * d5);
                        double d9 = (d5 * cos) + (d4 * sin);
                        dArr4[0] = d6 - d8;
                        dArr4[1] = d7 - d9;
                        dArr5[0] = d6 + d8;
                        dArr5[1] = d7 + d9;
                    }
                    i3++;
                }
            }
            return;
        }
        throw new RuntimeException("N is not a power of 2");
    }
}
