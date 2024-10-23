package com.rhewumapp.Activity.VibcheckerGraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;

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

    // Fields for zooming and panning
    private float scaleFactor = 1.0f;
    private float offsetX = 0;
    private float offsetY = 0;
    private ScaleGestureDetector scaleGestureDetector;
    private float lastTouchX;
    private float lastTouchY;
    private boolean isPanning = false;
    // Add GestureDetector for double-tap
    private GestureDetector gestureDetector;



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
        scaleGestureDetector = new ScaleGestureDetector(context, new PlotViewMaxValues.ScaleListener());
        // Initialize the GestureDetector with a double-tap listener
        gestureDetector = new GestureDetector(context, new PlotViewMaxValues.GestureListener());
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

        // Apply the zoom and pan transformations to the canvas
        cnv.save(); // Save the current state of the canvas
        cnv.scale(scaleFactor, scaleFactor, getWidth() / 2.0f, getHeight() / 2.0f); // Scale relative to the center of the view
        cnv.translate(offsetX / scaleFactor, offsetY / scaleFactor); // Apply translation for panning
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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);

        // Handle pan gestures
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                isPanning = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isPanning) {
                    float dx = event.getX() - lastTouchX;
                    float dy = event.getY() - lastTouchY;
                    offsetX += dx;
                    offsetY += dy;
                    lastTouchX = event.getX();
                    lastTouchY = event.getY();
                    invalidate(); // Request redraw
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isPanning = false;
                break;
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f)); // Limit zoom
            invalidate(); // Request redraw
            return true;
        }
    }

    // Custom GestureListener for double-tap detection
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Zoom in on double-tap
            scaleFactor *= 1.5f; // Adjust this factor to control zoom level on double-tap
            scaleFactor = Math.min(scaleFactor, 5.0f); // Max zoom limit
            invalidate(); // Request redraw to apply the zoom
            return true;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
            resetZoomAndPan();
        }
    }

    private void resetZoomAndPan() {
        scaleFactor = 1.0f; // Reset zoom level to default
        offsetX = 0;        // Reset horizontal pan
        offsetY = 0;        // Reset vertical pan
        invalidate();
    }
}