package com.rhewumapp.Activity.data;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;

public class CustomBarChartRendererLiveData extends BarChartRenderer {
    private final int lineColor;

    public CustomBarChartRendererLiveData(BarChart chart, int lineColor) {
        super(chart, chart.getAnimator(), chart.getViewPortHandler());
        this.lineColor = lineColor;
    }

    @Override
    public void drawValues(Canvas c) {
        super.drawValues(c);

        Paint paint = new Paint();
        paint.setColor(lineColor);
        paint.setStrokeWidth(3f);

        for (IBarDataSet dataSet : mChart.getBarData().getDataSets()) {
            Transformer transformer = mChart.getTransformer(dataSet.getAxisDependency());

            for (int i = 0; i < dataSet.getEntryCount(); i++) {
                BarEntryWithShelf entry = (BarEntryWithShelf) dataSet.getEntryForIndex(i);
                float mainValueY = entry.shelfVal; // mainValue for line indicator

                // Calculate pixel coordinates for the mainValueY position
                float[] pts = new float[]{entry.getXIndex(), mainValueY};
                transformer.pointValuesToPixel(pts);

                // Draw line at the mainValue position within the bar
                float barWidth = 60 / 2f; // Adjust for bar width
                c.drawLine(pts[0] - barWidth, pts[1], pts[0] + barWidth, pts[1], paint);
            }
        }
    }

}






