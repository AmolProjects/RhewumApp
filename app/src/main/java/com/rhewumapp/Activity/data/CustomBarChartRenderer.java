package com.rhewumapp.Activity.data;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomBarChartRenderer extends BarChartRenderer {
    private Paint linePaint;
    private Paint textPaint;

    public CustomBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        init(); // Ensure initialization
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK); // Set the desired color
        linePaint.setStrokeWidth(4f);  // Set the thickness of the line
        linePaint.setStyle(Paint.Style.STROKE); // Set style to stroke


        // Initialize textPaint
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK); // Set the text color
        textPaint.setTextSize(30f); // Set the desired text size
        textPaint.setTextAlign(Paint.Align.CENTER); // Center align text
    }
    //working bar example with Number//
    @Override
    public void drawValues(Canvas c) {
        if (linePaint == null) {
            init(); // Ensure linePaint is initialized
        }

        BarData barData = mChart.getBarData();

        for (int i = 0; i < barData.getDataSetCount(); i++) {
            IBarDataSet dataSet = barData.getDataSetByIndex(i);
            Transformer transformer = mChart.getTransformer(dataSet.getAxisDependency());
            float phaseY = 1f; // Assuming you're applying a phase (e.g., animation)

            float[] transformedValues = transformer.generateTransformedValuesBarChart(dataSet, i, barData, phaseY);

            for (int j = 0; j < transformedValues.length; j += 2) {
                float x = transformedValues[j]; // X coordinate
                float y = transformedValues[j + 1]; // Y coordinate

                // Draw a line at the transformed position if within chart bounds
                if (y >= mViewPortHandler.contentTop() && y <= mViewPortHandler.contentBottom()) {
                    float lineLength = 50f; // Adjust line length as needed
                    c.drawLine(x - lineLength / 2, y, x + lineLength / 2, y, linePaint);

                    // Draw the value above the line (rounded to an integer)
                    int value = (int) dataSet.getEntryForIndex(j / 2).getVal(); // Get the Y value as an integer

                    // Calculate the vertical position for the text
                    float textY = y - 20; // Default position above the line

                    // Ensure the text doesn't go above the content area of the chart
                    if (textY < mViewPortHandler.contentTop()) {
                        textY = y + 20; // Place it below the line if it goes out of bounds
                    }

                    // Ensure the text doesn't go below the content area of the chart
                    if (textY > mViewPortHandler.contentBottom()) {
                        textY = y - 20; // Place it above the line if it goes out of bounds
                    }

                    // Draw the integer value above the line
                    c.drawText(String.valueOf(value), x, textY, textPaint); // Use adjusted textY
                }
            }
        }
    }








}