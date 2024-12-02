package com.rhewumapp.Activity.data;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

public class CustomTypefaceSpan extends MetricAffectingSpan {

    private final Typeface typeface;  // Custom font

    public CustomTypefaceSpan(Typeface typeface) {
        this.typeface = typeface;
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint textPaint) {
// Apply the custom Typeface when measuring text
        applyCustomTypeFace(textPaint);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
// Apply the custom Typeface when measuring text
        applyCustomTypeFace(tp);
    }

    // Helper method to apply the custom Typeface
    private void applyCustomTypeFace(TextPaint paint) {
        // Set the custom Typeface
        paint.setTypeface(typeface);
    }
}
