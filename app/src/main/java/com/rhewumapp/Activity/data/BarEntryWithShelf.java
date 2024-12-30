package com.rhewumapp.Activity.data;



import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.github.mikephil.charting.data.BarEntry;


public class BarEntryWithShelf extends BarEntry {
    public final Paint paint;
    public int shelfColor;
    public float shelfVal;

    @SuppressLint("ResourceAsColor")
    public BarEntryWithShelf(float f, int i, float f2, int i2) {
        super(f, i);
        this.shelfColor = i2;
        this.shelfVal = f2;
        Paint paint2 = new Paint();
        this.paint = paint2;
        paint2.setColor(View.MEASURED_STATE_MASK);
        //paint2.setColor(shelfColor);
        paint2.setStrokeWidth(4.0f);
        paint2.setStyle(Paint.Style.STROKE);

    }
}
