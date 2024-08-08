package com.rhewum.Activity.data;

import android.graphics.Paint;

import androidx.core.view.ViewCompat;

import com.github.mikephil.charting.data.BarEntry;

public class BarEntryWithShelf extends BarEntry {
    public final Paint paint;
    public int shelfColor;
    public float shelfVal;

    public BarEntryWithShelf(float f, int i, float f2, int i2) {
        super(f, i);
        this.shelfColor = i2;
        this.shelfVal = f2;
        Paint paint2 = new Paint();
        this.paint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint2.setStrokeWidth(4.0f);
    }
}
