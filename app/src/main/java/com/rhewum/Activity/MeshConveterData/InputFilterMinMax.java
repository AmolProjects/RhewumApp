package com.rhewum.Activity.MeshConveterData;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private int max;
    private int min;


    public InputFilterMinMax(int i, int i2) {
        this.min = i;
        this.max = i2;
    }


    /* JADX WARNING: Removed duplicated region for block: B:6:0x000e A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isInRange(int min, int max, int value) {
        /*
            r2 = this;
            r0 = 1
            r1 = 0
            if (r4 <= r3) goto L_0x0009
            if (r5 < r3) goto L_0x000e
            if (r5 > r4) goto L_0x000e
            goto L_0x000f
        L_0x0009:
            if (r5 < r4) goto L_0x000e
            if (r5 > r3) goto L_0x000e
            goto L_0x000f
        L_0x000e:
            r0 = 0
        L_0x000f:
            return r0
        */
        return min <= value && value <= max;
//        throw new UnsupportedOperationException("Method not decompiled: com.rhewumapp.utility.InputFilterMinMax.isInRange(int, int, int):boolean");
    }



    public InputFilterMinMax(String str, String str2) {
        this.min = Integer.parseInt(str);
        this.max = Integer.parseInt(str2);
    }

//    public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
//        try {
//            String str = spanned.toString().substring(0, i3) + spanned.toString().substring(i4, spanned.toString().length());
//            if (isInRange(this.min, this.max, Integer.parseInt(str.substring(0, i3) + charSequence.toString() + str.substring(i3, str.length())))) {
//                return null;
//            }
//            return "";
//        } catch (NumberFormatException unused) {
//            return "";
//        }
//    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            // The new value after modification
            String newVal = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend);
            int input = Integer.parseInt(newVal);

            // Check if the new value is within range
            if (isInRange(min, max, input)) {
                return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // If not within range, return an empty string (disallow input)
        return "";
    }
}

