package com.rhewum.Activity.dsp;

public interface AudioProcessingListener {
    void onDrawableFFTSignalAvailable(AudioMeasurement audioMeasurement);
}
