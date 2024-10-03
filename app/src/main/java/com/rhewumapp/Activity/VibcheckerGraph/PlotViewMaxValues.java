package com.rhewumapp.Activity.VibcheckerGraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import java.util.ArrayList;
import java.util.List;


public class PlotViewMaxValues extends View {
    private final List<Paint> paints;
    private final List<Path> paths;
    private float[] buffer;
    private int channelCount;
    private int channel; // 0, 1, ...
    private int color;
    private float maxX, maxY, maxZ; // Add these variables

    public PlotViewMaxValues(Context context) {
        super(context);
        setWillNotDraw(false);
        paints = new ArrayList<>();
        paths = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setAntiAlias(true);
            paints.add(paint);
            paths.add(new Path());
        }
    }

    public void setBuffer(float[] buffer) { this.buffer = buffer; }
    public void setNumChannels(int channelCount) { this.channelCount = channelCount; }
    public void setChannel(int channel) { this.channel = channel; }
    public void setColor(int index, int color) { paints.get(index).setColor(color); }
    public void setLineWidth(float width) { for (Paint paint : paints) paint.setStrokeWidth(width); }
    public void setMaxValues(float maxX, float maxY, float maxZ) { // Add this method
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        invalidate(); // Redraw the view
    }

    @Override
    public void onDraw(Canvas cnv) {
        int w = getWidth();
        int h = getHeight();

        for (int i = 0; i < paints.size(); i++) {
            Paint paint = paints.get(i);
            Path path = paths.get(i);
            paintFill(paint, true);
            paint.setTextSize(20);

            if (buffer == null) {
                cnv.drawText("buffer == null", w / 2.0f, h / 2.0f, paint);
                return;
            }

            float x;
            float y;

            path.reset();
            paintFill(paint, false);

            for (int j = 0; j < buffer.length; j += channelCount) {
                x = (((float) (j)) * w) / buffer.length;
                y = (buffer[j + i] + 1) / 2.0f; // 0 .. 1
                y = h - y * h;
                if (x < 0) { x = 0; }
                if (x >= w) { x = w - 1; }
                if (y < 0) { y = 0; }
                if (y >= h) { y = h - 1; }

                if (j == 0) {
                    path.moveTo(x, y);
                } else if (j == buffer.length - 1) {
                    path.setLastPoint(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }

            cnv.drawPath(path, paint);
        }

    }

    public void paintFill(Paint paint, boolean fill) {
        if (fill) paint.setStyle(Paint.Style.FILL);
        else paint.setStyle(Paint.Style.STROKE);
    }
}
