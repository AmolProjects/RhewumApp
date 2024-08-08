package com.rhewum.Activity.dsp;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.itextpdf.text.pdf.codec.TIFFConstants;
import com.itextpdf.text.pdf.codec.wmf.MetaDo;
import com.rhewum.Activity.MeshConveterData.Constants;

public class SignalHelper {
    private static double mScaleFator = 100.0d;
    private static int[] signal;
    private static int[] signalWithXAxis = {0, 0, 10, 0, 20, 0, 30, 0, 40, 0, 50, 0, 60, 0, 70, 0, 80, 0, 90, 5, 100, 20, 110, 35, 120, 100, 130, 45, 140, 35, 150, 15, 160, 5, 170, 0, 180, 0, 190, 0, 200, 0, 210, 0, 220, 0, 230, 0, 240, 0, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 0, MetaDo.META_SETROP2, 0, TIFFConstants.TIFFTAG_IMAGEDESCRIPTION, 0, TIFFConstants.TIFFTAG_MINSAMPLEVALUE, 0, TIFFConstants.TIFFTAG_GRAYRESPONSEUNIT, 0, 300, 0, 310, 0, TIFFConstants.TIFFTAG_COLORMAP, 0, TIFFConstants.TIFFTAG_SUBIFD, 0, TIFFConstants.TIFFTAG_SMINSAMPLEVALUE, 0, 350, 0};

    public static int[] getTestSignal(int i) {
        int[] iArr = new int[(i / 2)];
        signal = iArr;
        iArr[9] = 5;
        iArr[10] = 20;
        iArr[11] = 35;
        iArr[12] = 100;
        iArr[13] = 45;
        iArr[14] = 35;
        iArr[15] = 15;
        iArr[16] = 5;
        return iArr;
    }

    public static int[] getTestSignalWithSpecificFrequency(double d, int i, double d2) {
        int[] iArr = new int[(i / 2)];
        signal = iArr;
        double d3 = (double) i;
        Double.isNaN(d3);
        iArr[(int) (d * (d3 / d2))] = 100;
        return iArr;
    }

    public static int[] getTestSignalWithXAxis() {
        return signalWithXAxis;
    }

    public static int[] getDrawableFFTSignal(int[] iArr) {
        int[] iArr2 = new int[(iArr.length * 2)];
        int i = 0;
        for (int i2 : iArr) {
            int i3 = i * 2;
            iArr2[i3] = i;
            double d = mScaleFator;
            double d2 = (double) i2;
            Double.isNaN(d2);
            iArr2[i3 + 1] = (int) (d * d2);
            i++;
        }
        return iArr2;
    }

    public static int[] getDrawableFFTSignal(double[] dArr) {
        int[] iArr = new int[(dArr.length * 2)];
        int i = 0;
        for (double d : dArr) {
            int i2 = i * 2;
            iArr[i2] = i;
            iArr[i2 + 1] = (int) (mScaleFator * d);
            i++;
        }
        return iArr;
    }

    public static void setScaleFator(double d) {
        mScaleFator = d;
    }

    public static class SignalGenerator {
        public static int read(byte[] bArr, int i, double d, boolean z, boolean z2) {
            double d2 = 1.0d / d;
            int i2 = (int) d;
            for (int i3 = 0; i3 < i2; i3++) {
                double d3 = (double) i;
                Double.isNaN(d3);
                double d4 = Constants.PI * 2.0d * d3;
                double d5 = (double) i3;
                Double.isNaN(d5);
                double d6 = d4 * d5 * d2;
                double sin = Math.sin(d6) * 16383.5d;
                if (z) {
                    sin += Math.sin(d6 * 2.0d) * 16383.5d;
                }
                if (z2) {
                    sin += Math.random() * 8191.75d;
                }
                short s = (short) ((int) sin);
                int i4 = i3 * 2;
                bArr[i4] = (byte) (s & 255);
                bArr[i4 + 1] = (byte) ((s >> 8) & 255);
            }
            return i2;
        }

        public static int read(byte[] bArr, int i, int i2, double d, boolean z, boolean z2) {
            int i3 = i2;
            double d2 = 1.0d / d;
            for (int i4 = 0; i4 < i3; i4++) {
                double d3 = (double) i;
                Double.isNaN(d3);
                double d4 = Constants.PI * 2.0d * d3;
                double d5 = (double) i4;
                Double.isNaN(d5);
                double d6 = d4 * d5 * d2;
                double sin = Math.sin(d6) * 16383.5d;
                if (z) {
                    sin += Math.sin(d6 * 2.0d) * 16383.5d;
                }
                if (z2) {
                    sin += Math.random() * 8191.75d;
                }
                short s = (short) ((int) sin);
                int i5 = i4 * 2;
                bArr[i5] = (byte) (s & 255);
                bArr[i5 + 1] = (byte) ((s >> 8) & 255);
            }
            return i3;
        }
    }
}
