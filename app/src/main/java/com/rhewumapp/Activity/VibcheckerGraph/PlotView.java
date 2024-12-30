package com.rhewumapp.Activity.VibcheckerGraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;

public class PlotView extends View {
    private final List<Paint> paints;
    private final List<Path> paths;
    private float[] buffer;
    private int channelCount;
    private int channel; // 0, 1, ...
    private int color;

    // New variables for timing
    private boolean isRecording = false;
    private long startTime;
    private static final long RECORD_DURATION = 5000; // 5 seconds in milliseconds
    private boolean shouldUpdate = true; // Flag to control plotting updates

    private Handler handler;

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

    public PlotView(Context context) {
        super(context);
        setWillNotDraw(false);
        paints = new ArrayList<>();
        paths = new ArrayList<>();
        handler = new Handler();

        for (int i = 0; i < 3; i++) {
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setAntiAlias(true);
            paints.add(paint);
            paths.add(new Path());
        }
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        // Initialize the GestureDetector with a double-tap listener
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void startRecording() {
        isRecording = true;
        startTime = System.currentTimeMillis();
        handler.postDelayed(this::stopRecording, RECORD_DURATION);
        invalidate(); // Trigger a redraw
    }

    public void stopRecording() {
        isRecording = false;
        invalidate(); // Trigger a redraw
    }

    public void setBuffer(float[] buffer) {
        this.buffer = buffer;
    }

    public void setNumChannels(int channelCount) {
        this.channelCount = channelCount;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setColor(int index, int color) {
        paints.get(index).setColor(color);
    }

    public void setLineWidth(float width) {
        for (Paint paint : paints) paint.setStrokeWidth(width);
    }

    @Override
   /* public void onDraw(Canvas cnv) {
        int w = getWidth();
        int h = getHeight();

        // Apply zoom and pan transformations
        cnv.save();
        cnv.scale(scaleFactor, scaleFactor);
        cnv.translate(offsetX / scaleFactor, offsetY / scaleFactor);

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

            // Draw the existing paths regardless of shouldUpdate
            for (int j = 0; j < buffer.length; j += channelCount) {
//                x = (((float) (j)) * w) / buffer.length;
                //added 27
//                x = (((float) (j)) / (buffer.length - 1)) * w;
                if (buffer.length < w) {
                    x = ((float) j / (buffer.length - 1)) * w;
                } else {
                    x = (((float) (j)) * w) / buffer.length;
                }


                y = (buffer[j + i] + 1) / 2.0f; // 0 .. 1
                y = h - y * h;

                if (x < 0) {
                    x = 0;
                }
                if (x >= w) {
                    x = w - 1;
                }
                if (y < 0) {
                    y = 0;
                }
                if (y >= h) {
                    y = h - 1;
                }

                if (j == 0) {
                    path.moveTo(x, y);
                } else if (j == buffer.length - 1) {
                    path.setLastPoint(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }

            // Draw the path after updating
            cnv.drawPath(path, paint);
        }

        // If shouldUpdate is false, do not clear the paths, just stop updating them.
        if (!shouldUpdate) {
            return; // Do not allow further updates
        }
    }*/



    public void onDraw(Canvas cnv) {
        int w = getWidth();
        int h = getHeight();

        // Apply zoom and pan transformations
        cnv.save();
        cnv.scale(scaleFactor, scaleFactor);
        cnv.translate(offsetX / scaleFactor, offsetY / scaleFactor);

        for (int i = 0; i < paints.size(); i++) {
            Paint paint = paints.get(i);
            Path path = paths.get(i);
            paintFill(paint, true);
            paint.setTextSize(20);

            if (buffer == null || buffer.length < channelCount) {
                cnv.drawText("buffer == null or too small", w / 2.0f, h / 2.0f, paint);
                return;
            }

            float x;
            float y;

            path.reset();
            paintFill(paint, false);

            int points = buffer.length / channelCount;  // Total points based on channels
            float step = (float) w / (points - 1);  // Calculate step for even distribution

            // Draw path by interpolating points
//            for (int j = 0; j < points; j++) {
//                x = j * step;  // Evenly distribute points across the width
//
//                // Calculate y value (normalizing buffer values)
//                y = (buffer[j * channelCount + i] + 1) / 2.0f;  // Normalize to 0..1
//                y = h - y * h;  // Invert to match canvas coordinate system
//
//                // Constrain to canvas bounds
//                x = Math.max(0, Math.min(x, w - 1));
//                y = Math.max(0, Math.min(y, h - 1));
//
//                // Move to the start point or draw the path
//                if (j == 0) {
//                    path.moveTo(x, y);
//                } else {
//                    path.lineTo(x, y);
//                }
//            }

         points = buffer.length / channelCount;
            step = (float) w / (points - 1);  // Step to evenly distribute points

            for (int j = 0; j < points; j++) {
                x = j * step;  // Spread points evenly across width
                y = (buffer[j * channelCount + i] + 1) / 2.0f;  // Normalize y
                y = h - y * h;

                if (j == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }


            // Draw the path after constructing it
            cnv.drawPath(path, paint);
        }

        // Stop updates if flag is false
        if (!shouldUpdate) {
            return;
        }
    }

    public void paintFill(Paint paint, boolean fill) {
        if (fill) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
    }

    // Method to stop plotting without clearing the graph
    public void stopPlotting() {
        shouldUpdate = false; // Disable further updates to the graph
        invalidate(); // This will refresh the view, but keep the existing paths
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