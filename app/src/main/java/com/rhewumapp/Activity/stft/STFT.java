package com.rhewumapp.Activity.stft;

import android.util.Log;


import com.rhewumapp.Activity.MeshConveterData.Constants;

import java.util.Arrays;

public class STFT {
    private boolean boolAWeighting = true;
    private int cntRMS = 0;
    private double cumRMS = Constants.PI;
    private double[] dBAFactor;
    private int fftLen;
    public double maxAmpDB = Double.NaN;
    public double maxAmpFreq = Double.NaN;
    private int nAnalysed = 0;
    private double outRMS = Constants.PI;
    private int sampleRate;
    private RealDoubleFFT spectrumAmpFFT;
    private double[] spectrumAmpIn;
    private double[] spectrumAmpInTmp;
    private double[] spectrumAmpOut;
    private double[][] spectrumAmpOutArray;
    private int spectrumAmpOutArrayPt = 0;
    private double[] spectrumAmpOutCum;
    private double[] spectrumAmpOutDB;
    private double[] spectrumAmpOutTmp;
    private int spectrumAmpPt;
    private UpdateDbAListner updateDbAListner;
    private double[] wnd;
    private double wndEnergyFactor = 1.0d;

    private double sqr(double d) {
        return d * d;
    }

    private void initDBAFactor(int i, double d) {
        int i2 = i;
        int i3 = (i2 / 2) + 1;
        this.dBAFactor = new double[i3];
        for (int i4 = 0; i4 < i3; i4++) {
            double d2 = (double) i4;
            double d3 = (double) i2;
            Double.isNaN(d2);
            Double.isNaN(d3);
            double d4 = (d2 / d3) * d;
            double d5 = d4 * d4;
            double sqr = (sqr(12200.0d) * sqr(sqr(d4))) / (((sqr(20.6d) + d5) * Math.sqrt((sqr(107.7d) + d5) * (sqr(737.9d) + d5))) * (d5 + sqr(12200.0d)));
            this.dBAFactor[i4] = sqr * sqr * 1.58489319246111d;
        }
    }

    private void initWindowFunction(int i, String str) {
        double[] dArr;
        String str2 = str;
        this.wnd = new double[i];
        double d = 2.0d;
        if (!str2.equals("Bartlett")) {
            double d2 = 6.283185307179586d;
            if (!str2.equals("Hanning")) {
                double d3 = 12.566370614359172d;
                if (!str2.equals("Blackman")) {
                    if (!str2.equals("Blackman Harris")) {
                        if (!str2.equals("Kaiser, a=2.0")) {
                            if (!str2.equals("Kaiser, a=3.0")) {
                                if (!str2.equals("Kaiser, a=4.0")) {
                                    int i2 = 0;
                                    while (true) {
                                        double[] dArr2 = this.wnd;
                                        if (i2 >= dArr2.length) {
                                            break;
                                        }
                                        dArr2[i2] = 1.0d;
                                        i2++;
                                    }
                                } else {
                                    double i0 = besselCal.i0(12.566370614359172d);
                                    int i3 = 0;
                                    while (true) {
                                        double[] dArr3 = this.wnd;
                                        if (i3 >= dArr3.length) {
                                            break;
                                        }
                                        double d4 = (double) i3;
                                        Double.isNaN(d4);
                                        double d5 = d4 * 2.0d;
                                        double length = (double) (dArr3.length - 1);
                                        Double.isNaN(length);
                                        double length2 = (double) (dArr3.length - 1);
                                        Double.isNaN(length2);
                                        dArr3[i3] = besselCal.i0(Math.sqrt(1.0d - (((d5 / length) - 1.0d) * ((d5 / length2) - 1.0d))) * 12.566370614359172d) / i0;
                                        i3++;
                                    }
                                }
                            } else {
                                double i02 = besselCal.i0(9.42477796076938d);
                                int i4 = 0;
                                while (true) {
                                    double[] dArr4 = this.wnd;
                                    if (i4 >= dArr4.length) {
                                        break;
                                    }
                                    double d6 = (double) i4;
                                    Double.isNaN(d6);
                                    double d7 = d6 * d;
                                    double length3 = (double) (dArr4.length - 1);
                                    Double.isNaN(length3);
                                    double length4 = (double) (dArr4.length - 1);
                                    Double.isNaN(length4);
                                    dArr4[i4] = besselCal.i0(Math.sqrt(1.0d - (((d7 / length3) - 1.0d) * ((d7 / length4) - 1.0d))) * 9.42477796076938d) / i02;
                                    i4++;
                                    d = 2.0d;
                                }
                            }
                        } else {
                            double i03 = besselCal.i0(6.283185307179586d);
                            int i5 = 0;
                            while (true) {
                                double[] dArr5 = this.wnd;
                                if (i5 >= dArr5.length) {
                                    break;
                                }
                                double d8 = (double) i5;
                                Double.isNaN(d8);
                                double d9 = d8 * 2.0d;
                                double length5 = (double) (dArr5.length - 1);
                                Double.isNaN(length5);
                                double length6 = (double) (dArr5.length - 1);
                                Double.isNaN(length6);
                                dArr5[i5] = besselCal.i0(6.283185307179586d * Math.sqrt(1.0d - (((d9 / length5) - 1.0d) * ((d9 / length6) - 1.0d)))) / i03;
                                i5++;
                            }
                        }
                    } else {
                        int i6 = 0;
                        while (true) {
                            double[] dArr6 = this.wnd;
                            if (i6 >= dArr6.length) {
                                break;
                            }
                            double d10 = (double) i6;
                            Double.isNaN(d10);
                            double length7 = (double) (dArr6.length - 1);
                            Double.isNaN(length7);
                            double cos = 0.35875d - (Math.cos((d10 * 6.283185307179586d) / length7) * 0.48829d);
                            Double.isNaN(d10);
                            double d11 = d10 * d3;
                            double length8 = (double) (this.wnd.length - 1);
                            Double.isNaN(length8);
                            double cos2 = cos + (Math.cos(d11 / length8) * 0.14128d);
                            Double.isNaN(d10);
                            double length9 = (double) (this.wnd.length - 1);
                            Double.isNaN(length9);
                            dArr6[i6] = (cos2 - (Math.cos((d10 * 18.84955592153876d) / length9) * 0.01168d)) * 2.0d;
                            i6++;
                            d3 = 12.566370614359172d;
                        }
                    }
                } else {
                    int i7 = 0;
                    while (true) {
                        double[] dArr7 = this.wnd;
                        if (i7 >= dArr7.length) {
                            break;
                        }
                        double d12 = (double) i7;
                        Double.isNaN(d12);
                        double d13 = d12 * d2;
                        double length10 = (double) (dArr7.length - 1);
                        Double.isNaN(length10);
                        Double.isNaN(d12);
                        double length11 = (double) (this.wnd.length - 1);
                        Double.isNaN(length11);
                        dArr7[i7] = (0.42d - (Math.cos(d13 / length10) * 0.5d)) + (Math.cos((d12 * 12.566370614359172d) / length11) * 0.08d);
                        i7++;
                        d2 = 6.283185307179586d;
                    }
                }
            } else {
                int i8 = 0;
                while (true) {
                    double[] dArr8 = this.wnd;
                    if (i8 >= dArr8.length) {
                        break;
                    }
                    double d14 = (double) i8;
                    Double.isNaN(d14);
                    double length12 = (double) dArr8.length;
                    Double.isNaN(length12);
                    dArr8[i8] = (1.0d - Math.cos((d14 * 6.283185307179586d) / (length12 - 1.0d))) * 0.5d * 2.0d;
                    i8++;
                }
            }
        } else {
            int i9 = 0;
            while (true) {
                double[] dArr9 = this.wnd;
                if (i9 >= dArr9.length) {
                    break;
                }
                double d15 = (double) i9;
                Double.isNaN(d15);
                double length13 = (double) dArr9.length;
                Double.isNaN(length13);
                dArr9[i9] = (Math.asin(Math.sin((d15 * 3.141592653589793d) / length13)) / 3.141592653589793d) * 2.0d;
                i9++;
            }
        }
        double d16 = 0.0d;
        int i10 = 0;
        while (true) {
            dArr = this.wnd;
            if (i10 >= dArr.length) {
                break;
            }
            d16 += dArr[i10];
            i10++;
        }
        double length14 = (double) dArr.length;
        Double.isNaN(length14);
        double d17 = length14 / d16;
        this.wndEnergyFactor = Constants.PI;
        int i11 = 0;
        while (true) {
            double[] dArr10 = this.wnd;
            if (i11 < dArr10.length) {
                dArr10[i11] = dArr10[i11] * d17;
                this.wndEnergyFactor += dArr10[i11] * dArr10[i11];
                i11++;
            } else {
                double length15 = (double) dArr10.length;
                double d18 = this.wndEnergyFactor;
                Double.isNaN(length15);
                this.wndEnergyFactor = length15 / d18;
                return;
            }
        }
    }

    public void setAWeighting(boolean z) {
        this.boolAWeighting = z;
    }

    public boolean getAWeighting() {
        return this.boolAWeighting;
    }

    private void init(int i, int i2, int i3, String str, UpdateDbAListner updateDbAListner2) {
        this.updateDbAListner = updateDbAListner2;
        if (i3 <= 0) {
            throw new IllegalArgumentException("STFT::init(): should minFeedSize >= 1.");
        } else if (((-i) & i) == i) {
            this.sampleRate = i2;
            this.fftLen = i;
            int i4 = i / 2;
            int i5 = i4 + 1;
            this.spectrumAmpOutCum = new double[i5];
            this.spectrumAmpOutTmp = new double[i5];
            this.spectrumAmpOut = new double[i5];
            this.spectrumAmpOutDB = new double[i5];
            this.spectrumAmpIn = new double[i];
            this.spectrumAmpInTmp = new double[i];
            this.spectrumAmpFFT = new RealDoubleFFT(this.spectrumAmpIn.length);
            double d = (double) i3;
            double d2 = (double) i4;
            Double.isNaN(d);
            Double.isNaN(d2);
            this.spectrumAmpOutArray = new double[((int) Math.ceil(d / d2))][];
            int i6 = 0;
            while (true) {
                double[][] dArr = this.spectrumAmpOutArray;
                if (i6 < dArr.length) {
                    dArr[i6] = new double[i5];
                    i6++;
                } else {
                    initWindowFunction(i, str);
                    initDBAFactor(i, (double) i2);
                    this.boolAWeighting = true;
                    return;
                }
            }
        } else {
            throw new IllegalArgumentException("STFT::init(): Currently, only power of 2 are supported in fftlen");
        }
    }

    public STFT(int i, int i2, int i3, String str, UpdateDbAListner updateDbAListner2) {
        init(i, i2, i3, str, updateDbAListner2);
    }

    public STFT(int i, int i2, String str, UpdateDbAListner updateDbAListner2) {
        init(i, i2, 1, str, updateDbAListner2);
    }

    public void feedData(short[] sArr) {
        feedData(sArr, sArr.length);
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0013 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void feedData(short[] r12, int r13) {
        /*
            r11 = this;
            int r0 = r12.length
            if (r13 <= r0) goto L_0x000b
            java.lang.String r13 = "STFT"
            java.lang.String r0 = "dsLen > ds.length !"
            android.util.Log.e(r13, r0)
            int r13 = r12.length
        L_0x000b:
            double[] r0 = r11.spectrumAmpIn
            int r0 = r0.length
            double[] r1 = r11.spectrumAmpOut
            int r1 = r1.length
            r2 = 0
            r3 = 0
        L_0x0013:
            if (r3 >= r13) goto L_0x009a
        L_0x0015:
            int r4 = r11.spectrumAmpPt
            if (r4 >= r0) goto L_0x003d
            if (r3 >= r13) goto L_0x003d
            int r5 = r3 + 1
            short r3 = r12[r3]
            double r6 = (double) r3
            r8 = 4674736413210574848(0x40e0000000000000, double:32768.0)
            java.lang.Double.isNaN(r6)
            double r6 = r6 / r8
            double[] r3 = r11.spectrumAmpIn
            int r8 = r4 + 1
            r11.spectrumAmpPt = r8
            r3[r4] = r6
            double r3 = r11.cumRMS
            double r6 = r6 * r6
            double r3 = r3 + r6
            r11.cumRMS = r3
            int r3 = r11.cntRMS
            int r3 = r3 + 1
            r11.cntRMS = r3
            r3 = r5
            goto L_0x0015
        L_0x003d:
            if (r4 != r0) goto L_0x0013
            r4 = 0
        L_0x0040:
            if (r4 >= r0) goto L_0x0053
            double[] r5 = r11.spectrumAmpInTmp
            double[] r6 = r11.spectrumAmpIn
            r7 = r6[r4]
            double[] r6 = r11.wnd
            r9 = r6[r4]
            double r7 = r7 * r9
            r5[r4] = r7
            int r4 = r4 + 1
            goto L_0x0040
        L_0x0053:
            com.rhewumapp.vibsonic.stft.RealDoubleFFT r4 = r11.spectrumAmpFFT
            double[] r5 = r11.spectrumAmpInTmp
            r4.ft(r5)
            double[] r4 = r11.spectrumAmpOutTmp
            double[] r5 = r11.spectrumAmpInTmp
            r11.fftToAmp(r4, r5)
            double[] r4 = r11.spectrumAmpOutTmp
            double[][] r5 = r11.spectrumAmpOutArray
            int r6 = r11.spectrumAmpOutArrayPt
            r5 = r5[r6]
            int r6 = r4.length
            java.lang.System.arraycopy(r4, r2, r5, r2, r6)
            int r4 = r11.spectrumAmpOutArrayPt
            int r4 = r4 + 1
            double[][] r5 = r11.spectrumAmpOutArray
            int r5 = r5.length
            int r4 = r4 % r5
            r11.spectrumAmpOutArrayPt = r4
            r4 = 0
        L_0x0078:
            if (r4 >= r1) goto L_0x0088
            double[] r5 = r11.spectrumAmpOutCum
            r6 = r5[r4]
            double[] r8 = r11.spectrumAmpOutTmp
            r9 = r8[r4]
            double r6 = r6 + r9
            r5[r4] = r6
            int r4 = r4 + 1
            goto L_0x0078
        L_0x0088:
            int r4 = r11.nAnalysed
            int r4 = r4 + 1
            r11.nAnalysed = r4
            double[] r4 = r11.spectrumAmpIn
            int r5 = r4.length
            int r5 = r5 / 2
            java.lang.System.arraycopy(r4, r5, r4, r2, r5)
            r11.spectrumAmpPt = r5
            goto L_0x0013
        L_0x009a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.rhewumapp.vibsonic.stft.STFT.feedData(short[], int):void");
    }

    private void fftToAmp(double[] dArr, double[] dArr2) {
        double[] dArr3 = dArr2;
        double length = (double) (dArr3.length * dArr3.length);
        Double.isNaN(length);
        double d = 4.0d / length;
        dArr[0] = ((dArr3[0] * dArr3[0]) * d) / 4.0d;
        int i = 1;
        int i2 = 1;
        while (i < dArr3.length - 1) {
            int i3 = i + 1;
            dArr[i2] = ((dArr3[i] * dArr3[i]) + (dArr3[i3] * dArr3[i3])) * d;
            i += 2;
            i2++;
        }
        dArr[i2] = ((dArr3[dArr3.length - 1] * dArr3[dArr3.length - 1]) * d) / 4.0d;
    }

    public final double[] getSpectrumAmp() {
        if (this.nAnalysed != 0) {
            int length = this.spectrumAmpOut.length;
            double[] dArr = this.spectrumAmpOutCum;
            for (int i = 0; i < length; i++) {
                double d = dArr[i];
                double d2 = (double) this.nAnalysed;
                Double.isNaN(d2);
                dArr[i] = d / d2;
            }
            for (int i2 = 0; i2 < length; i2++) {
                dArr[i2] = dArr[i2] * this.dBAFactor[i2];
            }
            System.arraycopy(dArr, 0, this.spectrumAmpOut, 0, length);
            Arrays.fill(dArr, Constants.PI);
            this.nAnalysed = 0;
            for (int i3 = 0; i3 < length; i3++) {
                this.spectrumAmpOutDB[i3] = Math.log10(this.spectrumAmpOut[i3]) * 10.0d;
            }
        }
        return this.spectrumAmpOut;
    }

    public final double[] getSpectrumAmpDB() {
        getSpectrumAmp();
        return this.spectrumAmpOutDB;
    }

    public double getRMS() {
        int i = this.cntRMS;
        if (i > 266) {
            double d = this.cumRMS;
            double d2 = (double) i;
            Double.isNaN(d2);
            this.outRMS = Math.sqrt((d / d2) * 2.0d);
            this.cumRMS = Constants.PI;
            this.cntRMS = 0;
        }
        return this.outRMS;
    }

    public double getRMSFromFT() {
        getSpectrumAmpDB();
        int i = 1;
        double d = Constants.PI;
        while (true) {
            double[] dArr = this.spectrumAmpOut;
            if (i >= dArr.length) {
                return Math.sqrt(d * this.wndEnergyFactor);
            }
            d += dArr[i];
            i++;
        }
    }

    public int nElemSpectrumAmp() {
        return this.nAnalysed;
    }

    public void calculatePeak() {
        double[] dArr;
        getSpectrumAmpDB();
        this.maxAmpDB = Math.log10(3.814697265625E-6d) * 20.0d;
        this.maxAmpFreq = Constants.PI;
        int i = 1;
        while (true) {
            dArr = this.spectrumAmpOutDB;
            if (i >= dArr.length) {
                break;
            }
            if (dArr[i] > this.maxAmpDB) {
                this.maxAmpDB = dArr[i];
                this.maxAmpFreq = (double) i;
            }
            i++;
        }
        double d = this.maxAmpFreq;
        double d2 = (double) this.sampleRate;
        Double.isNaN(d2);
        double d3 = d * d2;
        double d4 = (double) this.fftLen;
        Double.isNaN(d4);
        double d5 = d3 / d4;
        this.maxAmpFreq = d5;
        this.updateDbAListner.onUpdateDbAListner(dArr, d5);
        int i2 = this.sampleRate;
        int i3 = this.fftLen;
        double d6 = this.maxAmpFreq;
        if (((double) (i2 / i3)) < d6 && d6 < ((double) ((i2 / 2) - (i2 / i3)))) {
            double d7 = (double) i2;
            Double.isNaN(d7);
            double d8 = (double) i3;
            Double.isNaN(d8);
            int round = (int) Math.round((d6 / d7) * d8);
            double[] dArr2 = this.spectrumAmpOutDB;
            double d9 = dArr2[round - 1];
            double d10 = dArr2[round];
            double d11 = dArr2[round + 1];
            double d12 = ((d11 + d9) / 2.0d) - d10;
            double d13 = (d11 - d9) / 2.0d;
            if (d12 < Constants.PI) {
                double d14 = (-d13) / (2.0d * d12);
                if (Math.abs(d14) < 1.0d) {
                    double d15 = this.maxAmpFreq;
                    double d16 = (double) this.sampleRate;
                    Double.isNaN(d16);
                    double d17 = d14 * d16;
                    double d18 = (double) this.fftLen;
                    Double.isNaN(d18);
                    this.maxAmpFreq = d15 + (d17 / d18);
                    double d19 = d12 * 4.0d;
                    this.maxAmpDB = ((d10 * d19) - (d13 * d13)) / d19;
                    Log.v("", "Peak = " + this.maxAmpFreq + " : " + this.maxAmpDB);
                }
            }
        }
    }

    public void clear() {
        int i = 0;
        this.spectrumAmpPt = 0;
        Arrays.fill(this.spectrumAmpOut, Constants.PI);
        Arrays.fill(this.spectrumAmpOutDB, Math.log10(Constants.PI));
        Arrays.fill(this.spectrumAmpOutCum, Constants.PI);
        while (true) {
            double[][] dArr = this.spectrumAmpOutArray;
            if (i < dArr.length) {
                Arrays.fill(dArr[i], Constants.PI);
                i++;
            } else {
                return;
            }
        }
    }
}
