package com.rhewum.Activity.stft;


import com.rhewum.Activity.MeshConveterData.Constants;

class RealDoubleFFT_Mixed {
    static final int[] ntryh = {4, 2, 3, 5};

    RealDoubleFFT_Mixed() {
    }

    /* access modifiers changed from: package-private */
    public void radf2(int i, int i2, double[] dArr, double[] dArr2, double[] dArr3, int i3) {
        int i4 = i;
        int i5 = i2;
        for (int i6 = 0; i6 < i5; i6++) {
            int i7 = i6 * 2;
            int i8 = i6 * i4;
            int i9 = (i6 + i5) * i4;
            dArr2[i7 * i4] = dArr[i8] + dArr[i9];
            dArr2[(((i7 + 1) * i4) + i4) - 1] = dArr[i8] - dArr[i9];
        }
        if (i4 >= 2) {
            if (i4 != 2) {
                for (int i10 = 0; i10 < i5; i10++) {
                    for (int i11 = 2; i11 < i4; i11 += 2) {
                        int i12 = i4 - i11;
                        int i13 = (i11 - 2) + i3;
                        int i14 = i11 - 1;
                        int i15 = (i10 + i5) * i4;
                        int i16 = i14 + i15;
                        int i17 = i14 + i3;
                        int i18 = i15 + i11;
                        double d = (dArr3[i13] * dArr[i16]) + (dArr3[i17] * dArr[i18]);
                        double d2 = (dArr3[i13] * dArr[i18]) - (dArr3[i17] * dArr[i16]);
                        int i19 = i10 * 2;
                        int i20 = i19 * i4;
                        int i21 = i10 * i4;
                        int i22 = i11 + i21;
                        dArr2[i11 + i20] = dArr[i22] + d2;
                        int i23 = (i19 + 1) * i4;
                        dArr2[i12 + i23] = d2 - dArr[i22];
                        int i24 = i20 + i14;
                        int i25 = i14 + i21;
                        dArr2[i24] = dArr[i25] + d;
                        dArr2[(i12 - 1) + i23] = dArr[i25] - d;
                    }
                }
                if (i4 % 2 == 1) {
                    return;
                }
            }
            for (int i26 = 0; i26 < i5; i26++) {
                int i27 = i26 * 2;
                int i28 = i4 - 1;
                dArr2[(i27 + 1) * i4] = -dArr[((i26 + i5) * i4) + i28];
                dArr2[(i27 * i4) + i28] = dArr[i28 + (i26 * i4)];
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void radf3(int i, int i2, double[] dArr, double[] dArr2, double[] dArr3, int i3) {
        int i4 = i;
        int i5 = i2;
        int i6 = i3 + i4;
        for (int i7 = 0; i7 < i5; i7++) {
            int i8 = (i7 + i5) * i4;
            int i9 = ((i5 * 2) + i7) * i4;
            double d = dArr[i8] + dArr[i9];
            int i10 = i7 * 3;
            int i11 = i7 * i4;
            dArr2[i10 * i4] = dArr[i11] + d;
            dArr2[(i10 + 2) * i4] = (dArr[i9] - dArr[i8]) * 0.866025403784439d;
            dArr2[(i4 - 1) + ((i10 + 1) * i4)] = dArr[i11] + (d * -0.5d);
        }
        if (i4 != 1) {
            for (int i12 = 0; i12 < i5; i12++) {
                for (int i13 = 2; i13 < i4; i13 += 2) {
                    int i14 = i4 - i13;
                    int i15 = i13 - 2;
                    int i16 = i15 + i3;
                    int i17 = i13 - 1;
                    int i18 = (i12 + i5) * i4;
                    int i19 = i17 + i18;
                    int i20 = i17 + i3;
                    int i21 = i13 + i18;
                    double d2 = (dArr3[i16] * dArr[i19]) + (dArr3[i20] * dArr[i21]);
                    double d3 = (dArr3[i16] * dArr[i21]) - (dArr3[i20] * dArr[i19]);
                    int i22 = i15 + i6;
                    int i23 = ((i5 * 2) + i12) * i4;
                    int i24 = i17 + i23;
                    int i25 = i17 + i6;
                    int i26 = i23 + i13;
                    double d4 = (dArr3[i22] * dArr[i24]) + (dArr3[i25] * dArr[i26]);
                    double d5 = (dArr3[i22] * dArr[i26]) - (dArr3[i25] * dArr[i24]);
                    double d6 = d2 + d4;
                    double d7 = d3 + d5;
                    int i27 = i12 * 3;
                    int i28 = i27 * i4;
                    int i29 = i12 * i4;
                    int i30 = i17 + i29;
                    dArr2[i17 + i28] = dArr[i30] + d6;
                    int i31 = i13 + i29;
                    dArr2[i13 + i28] = dArr[i31] + d7;
                    double d8 = dArr[i30] + (d6 * -0.5d);
                    double d9 = dArr[i31] + (d7 * -0.5d);
                    double d10 = (d3 - d5) * 0.866025403784439d;
                    double d11 = (d4 - d2) * 0.866025403784439d;
                    int i32 = (i27 + 2) * i4;
                    dArr2[i17 + i32] = d8 + d10;
                    int i33 = (i27 + 1) * i4;
                    dArr2[(i14 - 1) + i33] = d8 - d10;
                    dArr2[i32 + i13] = d9 + d11;
                    dArr2[i14 + i33] = d11 - d9;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void radf4(int i, int i2, double[] dArr, double[] dArr2, double[] dArr3, int i3) {
        int i4 = i;
        int i5 = i2;
        int i6 = i3 + i4;
        int i7 = i6 + i4;
        for (int i8 = 0; i8 < i5; i8++) {
            int i9 = (i8 + i5) * i4;
            int i10 = ((i5 * 3) + i8) * i4;
            double d = dArr[i9] + dArr[i10];
            int i11 = i8 * i4;
            int i12 = ((i5 * 2) + i8) * i4;
            double d2 = dArr[i11] + dArr[i12];
            int i13 = i8 * 4;
            dArr2[i13 * i4] = d + d2;
            int i14 = i4 - 1;
            dArr2[i14 + ((i13 + 3) * i4)] = d2 - d;
            dArr2[i14 + ((i13 + 1) * i4)] = dArr[i11] - dArr[i12];
            dArr2[(i13 + 2) * i4] = dArr[i10] - dArr[i9];
        }
        if (i4 >= 2) {
            if (i4 != 2) {
                for (int i15 = 0; i15 < i5; i15++) {
                    for (int i16 = 2; i16 < i4; i16 += 2) {
                        int i17 = i4 - i16;
                        int i18 = i16 - 2;
                        int i19 = i18 + i3;
                        int i20 = i16 - 1;
                        int i21 = (i15 + i5) * i4;
                        int i22 = i20 + i21;
                        int i23 = i20 + i3;
                        int i24 = i21 + i16;
                        double d3 = (dArr3[i19] * dArr[i22]) + (dArr3[i23] * dArr[i24]);
                        double d4 = (dArr3[i19] * dArr[i24]) - (dArr3[i23] * dArr[i22]);
                        int i25 = i18 + i6;
                        int i26 = (i15 + (i5 * 2)) * i4;
                        int i27 = i20 + i26;
                        int i28 = i20 + i6;
                        int i29 = i16 + i26;
                        double d5 = (dArr3[i25] * dArr[i27]) + (dArr3[i28] * dArr[i29]);
                        double d6 = (dArr3[i25] * dArr[i29]) - (dArr3[i28] * dArr[i27]);
                        int i30 = i18 + i7;
                        int i31 = ((i5 * 3) + i15) * i4;
                        int i32 = i20 + i31;
                        int i33 = i20 + i7;
                        int i34 = i31 + i16;
                        double d7 = (dArr3[i30] * dArr[i32]) + (dArr3[i33] * dArr[i34]);
                        double d8 = (dArr3[i30] * dArr[i34]) - (dArr3[i33] * dArr[i32]);
                        double d9 = d3 + d7;
                        double d10 = d7 - d3;
                        double d11 = d4 + d8;
                        double d12 = d4 - d8;
                        int i35 = i15 * i4;
                        int i36 = i16 + i35;
                        double d13 = dArr[i36] + d6;
                        double d14 = dArr[i36] - d6;
                        int i37 = i20 + i35;
                        double d15 = dArr[i37] + d5;
                        double d16 = dArr[i37] - d5;
                        int i38 = i15 * 4;
                        int i39 = i38 * i4;
                        dArr2[i20 + i39] = d9 + d15;
                        int i40 = i17 - 1;
                        int i41 = (i38 + 3) * i4;
                        dArr2[i40 + i41] = d15 - d9;
                        dArr2[i16 + i39] = d11 + d13;
                        dArr2[i17 + i41] = d11 - d13;
                        int i42 = (i38 + 2) * i4;
                        dArr2[i20 + i42] = d12 + d16;
                        int i43 = (i38 + 1) * i4;
                        dArr2[i40 + i43] = d16 - d12;
                        dArr2[i42 + i16] = d10 + d14;
                        dArr2[i17 + i43] = d10 - d14;
                    }
                }
                if (i4 % 2 == 1) {
                    return;
                }
            }
            for (int i44 = 0; i44 < i5; i44++) {
                int i45 = i4 - 1;
                int i46 = ((i44 + i5) * i4) + i45;
                int i47 = (((i5 * 3) + i44) * i4) + i45;
                double d17 = (dArr[i46] + dArr[i47]) * -0.7071067811865475d;
                double d18 = (dArr[i46] - dArr[i47]) * 0.7071067811865475d;
                int i48 = i44 * 4;
                int i49 = (i44 * i4) + i45;
                dArr2[(i48 * i4) + i45] = dArr[i49] + d18;
                dArr2[((i48 + 2) * i4) + i45] = dArr[i49] - d18;
                int i50 = i45 + (((i5 * 2) + i44) * i4);
                dArr2[(i48 + 1) * i4] = d17 - dArr[i50];
                dArr2[(i48 + 3) * i4] = d17 + dArr[i50];
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void radf5(int i, int i2, double[] dArr, double[] dArr2, double[] dArr3, int i3) {
        int i4 = i;
        int i5 = i2;
        int i6 = i3 + i4;
        int i7 = i6 + i4;
        int i8 = i7 + i4;
        for (int i9 = 0; i9 < i5; i9++) {
            int i10 = ((i5 * 4) + i9) * i4;
            int i11 = (i9 + i5) * i4;
            double d = dArr[i10] + dArr[i11];
            double d2 = dArr[i10] - dArr[i11];
            int i12 = ((i5 * 3) + i9) * i4;
            int i13 = (i9 + (i5 * 2)) * i4;
            double d3 = dArr[i12] + dArr[i13];
            double d4 = dArr[i12] - dArr[i13];
            int i14 = i9 * 5;
            int i15 = i9 * i4;
            dArr2[i14 * i4] = dArr[i15] + d + d3;
            int i16 = i4 - 1;
            dArr2[i16 + ((i14 + 1) * i4)] = dArr[i15] + (d * 0.309016994374947d) + (d3 * -0.809016994374947d);
            dArr2[(i14 + 2) * i4] = (d2 * 0.951056516295154d) + (d4 * 0.587785252292473d);
            dArr2[i16 + ((i14 + 3) * i4)] = dArr[i15] + (d * -0.809016994374947d) + (d3 * 0.309016994374947d);
            dArr2[(i14 + 4) * i4] = (d2 * 0.587785252292473d) - (d4 * 0.951056516295154d);
        }
        if (i4 != 1) {
            for (int i17 = 0; i17 < i5; i17++) {
                for (int i18 = 2; i18 < i4; i18 += 2) {
                    int i19 = i4 - i18;
                    int i20 = i18 - 2;
                    int i21 = i20 + i3;
                    int i22 = i18 - 1;
                    int i23 = (i17 + i5) * i4;
                    int i24 = i22 + i23;
                    int i25 = i22 + i3;
                    int i26 = i18 + i23;
                    double d5 = (dArr3[i21] * dArr[i24]) + (dArr3[i25] * dArr[i26]);
                    double d6 = (dArr3[i21] * dArr[i26]) - (dArr3[i25] * dArr[i24]);
                    int i27 = i20 + i6;
                    int i28 = (i17 + (i5 * 2)) * i4;
                    int i29 = i22 + i28;
                    int i30 = i22 + i6;
                    int i31 = i18 + i28;
                    double d7 = (dArr3[i27] * dArr[i29]) + (dArr3[i30] * dArr[i31]);
                    double d8 = (dArr3[i27] * dArr[i31]) - (dArr3[i30] * dArr[i29]);
                    int i32 = i20 + i7;
                    int i33 = (i17 + (i5 * 3)) * i4;
                    int i34 = i22 + i33;
                    int i35 = i22 + i7;
                    int i36 = i18 + i33;
                    double d9 = (dArr3[i32] * dArr[i34]) + (dArr3[i35] * dArr[i36]);
                    double d10 = (dArr3[i32] * dArr[i36]) - (dArr3[i35] * dArr[i34]);
                    int i37 = i20 + i8;
                    int i38 = (i17 + (i5 * 4)) * i4;
                    int i39 = i22 + i38;
                    int i40 = i22 + i8;
                    int i41 = i18 + i38;
                    double d11 = (dArr3[i37] * dArr[i39]) + (dArr3[i40] * dArr[i41]);
                    double d12 = (dArr3[i37] * dArr[i41]) - (dArr3[i40] * dArr[i39]);
                    double d13 = d5 + d11;
                    double d14 = d11 - d5;
                    double d15 = d6 - d12;
                    double d16 = d6 + d12;
                    double d17 = d7 + d9;
                    double d18 = d9 - d7;
                    double d19 = d8 - d10;
                    double d20 = d8 + d10;
                    int i42 = i17 * 5;
                    int i43 = i42 * i4;
                    int i44 = i17 * i4;
                    int i45 = i22 + i44;
                    dArr2[i22 + i43] = dArr[i45] + d13 + d17;
                    int i46 = i18 + i44;
                    dArr2[i18 + i43] = dArr[i46] + d16 + d20;
                    double d21 = dArr[i45] + (d13 * 0.309016994374947d) + (d17 * -0.809016994374947d);
                    double d22 = dArr[i46] + (d16 * 0.309016994374947d) + (d20 * -0.809016994374947d);
                    double d23 = dArr[i45] + (d13 * -0.809016994374947d) + (d17 * 0.309016994374947d);
                    double d24 = dArr[i46] + (d16 * -0.809016994374947d) + (d20 * 0.309016994374947d);
                    double d25 = (d15 * 0.951056516295154d) + (d19 * 0.587785252292473d);
                    double d26 = (d14 * 0.951056516295154d) + (d18 * 0.587785252292473d);
                    double d27 = (d15 * 0.587785252292473d) - (d19 * 0.951056516295154d);
                    double d28 = (d14 * 0.587785252292473d) - (d18 * 0.951056516295154d);
                    int i47 = (i42 + 2) * i4;
                    dArr2[i22 + i47] = d21 + d25;
                    int i48 = i19 - 1;
                    int i49 = (i42 + 1) * i4;
                    dArr2[i48 + i49] = d21 - d25;
                    dArr2[i18 + i47] = d22 + d26;
                    dArr2[i19 + i49] = d26 - d22;
                    int i50 = (i42 + 4) * i4;
                    dArr2[i22 + i50] = d23 + d27;
                    int i51 = (i42 + 3) * i4;
                    dArr2[i48 + i51] = d23 - d27;
                    dArr2[i18 + i50] = d24 + d28;
                    dArr2[i19 + i51] = d28 - d24;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void radfg(int i, int i2, int i3, int i4, double[] dArr, double[] dArr2, double[] dArr3, double[] dArr4, double[] dArr5, double[] dArr6, int i5) {
        int i6 = i;
        int i7 = i2;
        int i8 = i3;
        int i9 = i4;
        double d = (double) i7;
        Double.isNaN(d);
        double d2 = 6.283185307179586d / d;
        double cos = Math.cos(d2);
        double sin = Math.sin(d2);
        int i10 = (i7 + 1) / 2;
        int i11 = i6 - 1;
        int i12 = i11 / 2;
        if (i6 != 1) {
            for (int i13 = 0; i13 < i9; i13++) {
                dArr5[i13] = dArr3[i13];
            }
            for (int i14 = 1; i14 < i7; i14++) {
                for (int i15 = 0; i15 < i8; i15++) {
                    int i16 = (i15 + (i14 * i8)) * i6;
                    dArr4[i16] = dArr2[i16];
                }
            }
            if (i12 <= i8) {
                int i17 = -i6;
                for (int i18 = 1; i18 < i7; i18++) {
                    i17 += i6;
                    int i19 = i17 - 1;
                    for (int i20 = 2; i20 < i6; i20 += 2) {
                        i19 += 2;
                        for (int i21 = 0; i21 < i8; i21++) {
                            int i22 = (i21 + (i18 * i8)) * i6;
                            int i23 = (i20 - 1) + i22;
                            int i24 = (i19 - 1) + i5;
                            int i25 = i19 + i5;
                            int i26 = i20 + i22;
                            dArr4[i23] = (dArr6[i24] * dArr2[i23]) + (dArr6[i25] * dArr2[i26]);
                            dArr4[i26] = (dArr6[i24] * dArr2[i26]) - (dArr6[i25] * dArr2[i23]);
                        }
                    }
                }
            } else {
                int i27 = -i6;
                for (int i28 = 1; i28 < i7; i28++) {
                    i27 += i6;
                    int i29 = 0;
                    while (i29 < i8) {
                        int i30 = i27 - 1;
                        int i31 = i27;
                        for (int i32 = 2; i32 < i6; i32 += 2) {
                            i30 += 2;
                            int i33 = (i29 + (i28 * i8)) * i6;
                            int i34 = (i32 - 1) + i33;
                            int i35 = (i30 - 1) + i5;
                            int i36 = i30 + i5;
                            int i37 = i32 + i33;
                            dArr4[i34] = (dArr6[i35] * dArr2[i34]) + (dArr6[i36] * dArr2[i37]);
                            dArr4[i37] = (dArr6[i35] * dArr2[i37]) - (dArr6[i36] * dArr2[i34]);
                        }
                        i29++;
                        i27 = i31;
                    }
                    int i38 = i27;
                }
            }
            if (i12 >= i8) {
                for (int i39 = 1; i39 < i10; i39++) {
                    int i40 = i7 - i39;
                    for (int i41 = 0; i41 < i8; i41++) {
                        for (int i42 = 2; i42 < i6; i42 += 2) {
                            int i43 = i42 - 1;
                            int i44 = (i41 + (i39 * i8)) * i6;
                            int i45 = i43 + i44;
                            int i46 = (i41 + (i40 * i8)) * i6;
                            int i47 = i43 + i46;
                            dArr2[i45] = dArr4[i45] + dArr4[i47];
                            int i48 = i42 + i44;
                            int i49 = i42 + i46;
                            dArr2[i47] = dArr4[i48] - dArr4[i49];
                            dArr2[i48] = dArr4[i48] + dArr4[i49];
                            dArr2[i49] = dArr4[i47] - dArr4[i45];
                        }
                    }
                }
            } else {
                for (int i50 = 1; i50 < i10; i50++) {
                    int i51 = i7 - i50;
                    for (int i52 = 2; i52 < i6; i52 += 2) {
                        for (int i53 = 0; i53 < i8; i53++) {
                            int i54 = i52 - 1;
                            int i55 = (i53 + (i50 * i8)) * i6;
                            int i56 = i54 + i55;
                            int i57 = (i53 + (i51 * i8)) * i6;
                            int i58 = i54 + i57;
                            dArr2[i56] = dArr4[i56] + dArr4[i58];
                            int i59 = i52 + i55;
                            int i60 = i52 + i57;
                            dArr2[i58] = dArr4[i59] - dArr4[i60];
                            dArr2[i59] = dArr4[i59] + dArr4[i60];
                            dArr2[i60] = dArr4[i58] - dArr4[i56];
                        }
                    }
                }
            }
        } else {
            for (int i61 = 0; i61 < i9; i61++) {
                dArr3[i61] = dArr5[i61];
            }
        }
        for (int i62 = 1; i62 < i10; i62++) {
            int i63 = i7 - i62;
            for (int i64 = 0; i64 < i8; i64++) {
                int i65 = ((i62 * i8) + i64) * i6;
                int i66 = (i64 + (i63 * i8)) * i6;
                dArr2[i65] = dArr4[i65] + dArr4[i66];
                dArr2[i66] = dArr4[i66] - dArr4[i65];
            }
        }
        double d3 = 1.0d;
        double d4 = Constants.PI;
        int i67 = 1;
        while (i67 < i10) {
            int i68 = i7 - i67;
            double d5 = (cos * d3) - (sin * d4);
            d4 = (d4 * cos) + (d3 * sin);
            for (int i69 = 0; i69 < i9; i69++) {
                dArr5[(i67 * i9) + i69] = dArr3[i69] + (dArr3[i69 + i9] * d5);
                dArr5[(i68 * i9) + i69] = dArr3[i69 + ((i7 - 1) * i9)] * d4;
            }
            double d6 = d4;
            double d7 = d5;
            int i70 = 2;
            while (i70 < i10) {
                int i71 = i7 - i70;
                double d8 = (d5 * d7) - (d4 * d6);
                d6 = (d6 * d5) + (d7 * d4);
                double d9 = cos;
                for (int i72 = 0; i72 < i9; i72++) {
                    int i73 = (i67 * i9) + i72;
                    dArr5[i73] = dArr5[i73] + (dArr3[i72 + (i70 * i9)] * d8);
                    int i74 = (i68 * i9) + i72;
                    dArr5[i74] = dArr5[i74] + (dArr3[i72 + (i71 * i9)] * d6);
                }
                i70++;
                cos = d9;
                d7 = d8;
            }
            double d10 = cos;
            i67++;
            d3 = d5;
        }
        for (int i75 = 1; i75 < i10; i75++) {
            for (int i76 = 0; i76 < i9; i76++) {
                dArr5[i76] = dArr5[i76] + dArr3[(i75 * i9) + i76];
            }
        }
        if (i6 >= i8) {
            for (int i77 = 0; i77 < i8; i77++) {
                for (int i78 = 0; i78 < i6; i78++) {
                    dArr[(i77 * i7 * i6) + i78] = dArr4[(i77 * i6) + i78];
                }
            }
        } else {
            for (int i79 = 0; i79 < i6; i79++) {
                for (int i80 = 0; i80 < i8; i80++) {
                    dArr[(i80 * i7 * i6) + i79] = dArr4[(i80 * i6) + i79];
                }
            }
        }
        for (int i81 = 1; i81 < i10; i81++) {
            int i82 = i7 - i81;
            int i83 = i81 * 2;
            for (int i84 = 0; i84 < i8; i84++) {
                int i85 = i84 * i7;
                dArr[(((i83 - 1) + i85) * i6) + i11] = dArr4[((i81 * i8) + i84) * i6];
                dArr[(i85 + i83) * i6] = dArr4[((i82 * i8) + i84) * i6];
            }
        }
        if (i6 != 1) {
            if (i12 >= i8) {
                for (int i86 = 1; i86 < i10; i86++) {
                    int i87 = i7 - i86;
                    int i88 = i86 * 2;
                    for (int i89 = 0; i89 < i8; i89++) {
                        for (int i90 = 2; i90 < i6; i90 += 2) {
                            int i91 = i6 - i90;
                            int i92 = i90 - 1;
                            int i93 = i89 * i7;
                            int i94 = (i88 + i93) * i6;
                            int i95 = i92 + i94;
                            int i96 = ((i86 * i8) + i89) * i6;
                            int i97 = i92 + i96;
                            int i98 = (i89 + (i87 * i8)) * i6;
                            int i99 = i92 + i98;
                            dArr[i95] = dArr4[i97] + dArr4[i99];
                            int i100 = ((i88 - 1) + i93) * i6;
                            dArr[(i91 - 1) + i100] = dArr4[i97] - dArr4[i99];
                            int i101 = i96 + i90;
                            int i102 = i90 + i98;
                            dArr[i94 + i90] = dArr4[i101] + dArr4[i102];
                            dArr[i91 + i100] = dArr4[i102] - dArr4[i101];
                        }
                    }
                }
                return;
            }
            for (int i103 = 1; i103 < i10; i103++) {
                int i104 = i7 - i103;
                int i105 = i103 * 2;
                for (int i106 = 2; i106 < i6; i106 += 2) {
                    int i107 = i6 - i106;
                    for (int i108 = 0; i108 < i8; i108++) {
                        int i109 = i106 - 1;
                        int i110 = i108 * i7;
                        int i111 = (i105 + i110) * i6;
                        int i112 = i109 + i111;
                        int i113 = ((i103 * i8) + i108) * i6;
                        int i114 = i109 + i113;
                        int i115 = (i108 + (i104 * i8)) * i6;
                        int i116 = i109 + i115;
                        dArr[i112] = dArr4[i114] + dArr4[i116];
                        int i117 = ((i105 - 1) + i110) * i6;
                        dArr[(i107 - 1) + i117] = dArr4[i114] - dArr4[i116];
                        int i118 = i113 + i106;
                        int i119 = i106 + i115;
                        dArr[i111 + i106] = dArr4[i118] + dArr4[i119];
                        dArr[i107 + i117] = dArr4[i119] - dArr4[i118];
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void rfftf1(int i, double[] dArr, double[] dArr2, int i2, double[] dArr3) {
        int i3;
        int i4;
        int i5 = i;
        double[] dArr4 = dArr2;
        int i6 = i2;
        double[] dArr5 = dArr3;
        System.arraycopy(dArr4, i6, dArr5, 0, i5);
        int i7 = i5 * 2;
        int i8 = (int) dArr4[i7 + 1 + i6];
        int i9 = (i5 - 1) + i5 + i6;
        int i10 = 1;
        int i11 = i5;
        int i12 = 1;
        int i13 = 1;
        while (i13 <= i8) {
            int i14 = (int) dArr4[(i8 - i13) + 2 + i7 + i6];
            int i15 = i11 / i14;
            int i16 = i5 / i11;
            int i17 = i16 * i15;
            int i18 = i9 - ((i14 - 1) * i16);
            int i19 = 1 - i12;
            if (i14 == 4) {
                if (i19 == 0) {
                    radf4(i16, i15, dArr, dArr3, dArr2, i18);
                } else {
                    radf4(i16, i15, dArr3, dArr, dArr2, i18);
                }
            } else if (i14 == 2) {
                if (i19 == 0) {
                    radf2(i16, i15, dArr, dArr3, dArr2, i18);
                } else {
                    radf2(i16, i15, dArr3, dArr, dArr2, i18);
                }
            } else if (i14 == 3) {
                if (i19 == 0) {
                    radf3(i16, i15, dArr, dArr3, dArr2, i18);
                } else {
                    radf3(i16, i15, dArr3, dArr, dArr2, i18);
                }
            } else if (i14 != 5) {
                if (i16 == i10) {
                    i19 = 1 - i19;
                }
                if (i19 == 0) {
                    i4 = i13;
                    i3 = i8;
                    radfg(i16, i14, i15, i17, dArr, dArr, dArr, dArr3, dArr3, dArr2, i18);
                    i12 = 1;
                } else {
                    i4 = i13;
                    i3 = i8;
                    radfg(i16, i14, i15, i17, dArr3, dArr3, dArr3, dArr, dArr, dArr2, i18);
                    i12 = 0;
                }
                i13 = i4 + 1;
                dArr4 = dArr2;
                i11 = i15;
                i9 = i18;
                i8 = i3;
                i10 = 1;
            } else if (i19 == 0) {
                radf5(i16, i15, dArr, dArr3, dArr2, i18);
            } else {
                radf5(i16, i15, dArr3, dArr, dArr2, i18);
            }
            i12 = i19;
            i4 = i13;
            i3 = i8;
            i13 = i4 + 1;
            dArr4 = dArr2;
            i11 = i15;
            i9 = i18;
            i8 = i3;
            i10 = 1;
        }
        if (i12 != 1) {
            for (int i20 = 0; i20 < i5; i20++) {
                dArr[i20] = dArr5[i20];
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void rfftf(int i, double[] dArr, double[] dArr2, double[] dArr3) {
        if (i != 1) {
            rfftf1(i, dArr, dArr2, 0, dArr3);
        }
    }

    /* access modifiers changed from: package-private */
    public void rffti1(int i, double[] dArr, int i2) {
        int i3;
        int i4;
        int i5 = i;
        int i6 = i5;
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        loop0:
        while (true) {
            i3 = 1;
            i7++;
            i8 = i7 <= 4 ? ntryh[i7 - 1] : i8 + 2;
            while (true) {
                int i10 = i6 / i8;
                if (i6 - (i8 * i10) == 0) {
                    i9++;
                    i4 = i5 * 2;
                    dArr[i9 + 1 + i4 + i2] = (double) i8;
                    if (i8 == 2 && i9 != 1) {
                        for (int i11 = 2; i11 <= i9; i11++) {
                            int i12 = (i9 - i11) + 2;
                            dArr[i12 + 1 + i4 + i2] = dArr[i12 + i4 + i2];
                        }
                        dArr[i4 + 2 + i2] = 2.0d;
                    }
                    if (i10 == 1) {
                        break loop0;
                    }
                    i6 = i10;
                }
            }
        }
        double d = (double) i5;
        dArr[i4 + 0 + i2] = d;
        dArr[i4 + 1 + i2] = (double) i9;
        Double.isNaN(d);
        double d2 = 6.283185307179586d / d;
        int i13 = i9 - 1;
        if (i13 != 0) {
            int i14 = 1;
            int i15 = 1;
            int i16 = 0;
            while (i14 <= i13) {
                i14++;
                int i17 = (int) dArr[i14 + i4 + i2];
                int i18 = i15 * i17;
                int i19 = i5 / i18;
                int i20 = i17 - i3;
                int i21 = 1;
                int i22 = 0;
                while (i21 <= i20) {
                    i22 += i15;
                    int i23 = i15;
                    double d3 = (double) i22;
                    Double.isNaN(d3);
                    double d4 = d3 * d2;
                    double d5 = Constants.PI;
                    int i24 = i16;
                    for (int i25 = 3; i25 <= i19; i25 += 2) {
                        i24 += 2;
                        d5 += 1.0d;
                        double d6 = d5 * d4;
                        dArr[(i24 - 2) + i5 + i2] = Math.cos(d6);
                        dArr[(i24 - 1) + i5 + i2] = Math.sin(d6);
                    }
                    i16 += i19;
                    i21++;
                    i15 = i23;
                    i3 = 1;
                }
                i15 = i18;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void rffti(int i, double[] dArr) {
        if (i != 1) {
            rffti1(i, dArr, 0);
        }
    }
}
