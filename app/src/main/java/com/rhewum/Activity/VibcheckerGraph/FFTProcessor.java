package com.rhewum.Activity.VibcheckerGraph;
import org.jtransforms.fft.FloatFFT_1D;
import java.util.ArrayList;
import java.util.List;

public class FFTProcessor {
    public List<Float> processFFT(float[] vectors) {
        int length = vectors.length;
        float[] input = new float[length * 2]; // Allocate space for complex numbers

        // Fill the real part and set the imaginary part to 0
        for (int i = 0; i < length; i++) {
            input[2 * i] = vectors[i]; // Real part
            input[2 * i + 1] = 0;      // Imaginary part
        }

        // Perform FFT
        FloatFFT_1D fftlib = new FloatFFT_1D(length);
        fftlib.complexForward(input);

        // Calculate magnitudes
        float[] outputData = new float[length / 2 + 1]; // Magnitudes array
        for (int i = 0; i < length / 2 + 1; i++) {
            outputData[i] = (float) Math.sqrt(input[2 * i] * input[2 * i] + input[2 * i + 1] * input[2 * i + 1]);
        }

        // Convert to List<Float> for easier use in Android
        List<Float> output = new ArrayList<>();
        for (float f : outputData) {
            output.add(f);
        }

        return output;
    }
}
