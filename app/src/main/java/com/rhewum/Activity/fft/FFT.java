package com.rhewum.Activity.fft;
import com.rhewum.Activity.MeshConveterData.Constants;

public class FFT {
    public static Complex[] fft(Complex[] complexArr) {
        int length = complexArr.length;
        if (length == 1) {
            return new Complex[]{complexArr[0]};
        } else if (length % 2 == 0) {
            int i = length / 2;
            Complex[] complexArr2 = new Complex[i];
            for (int i2 = 0; i2 < i; i2++) {
                complexArr2[i2] = complexArr[i2 * 2];
            }
            Complex[] fft = fft(complexArr2);
            for (int i3 = 0; i3 < i; i3++) {
                complexArr2[i3] = complexArr[(i3 * 2) + 1];
            }
            Complex[] fft2 = fft(complexArr2);
            Complex[] complexArr3 = new Complex[length];
            for (int i4 = 0; i4 < i; i4++) {
                double d = (double) (i4 * -2);
                Double.isNaN(d);
                double d2 = (double) length;
                Double.isNaN(d2);
                double d3 = (d * 3.141592653589793d) / d2;
                Complex complex = new Complex(Math.cos(d3), Math.sin(d3));
                complexArr3[i4] = fft[i4].plus(complex.times(fft2[i4]));
                complexArr3[i4 + i] = fft[i4].minus(complex.times(fft2[i4]));
            }
            return complexArr3;
        } else {
            throw new RuntimeException("N is not a power of 2");
        }
    }

    public static Complex[] ifft(Complex[] complexArr) {
        int length = complexArr.length;
        Complex[] complexArr2 = new Complex[length];
        for (int i = 0; i < length; i++) {
            complexArr2[i] = complexArr[i].conjugate();
        }
        Complex[] fft = fft(complexArr2);
        for (int i2 = 0; i2 < length; i2++) {
            fft[i2] = fft[i2].conjugate();
        }
        for (int i3 = 0; i3 < length; i3++) {
            Complex complex = fft[i3];
            double d = (double) length;
            Double.isNaN(d);
            fft[i3] = complex.times(1.0d / d);
        }
        return fft;
    }

    public static Complex[] cconvolve(Complex[] complexArr, Complex[] complexArr2) {
        if (complexArr.length == complexArr2.length) {
            int length = complexArr.length;
            Complex[] fft = fft(complexArr);
            Complex[] fft2 = fft(complexArr2);
            Complex[] complexArr3 = new Complex[length];
            for (int i = 0; i < length; i++) {
                complexArr3[i] = fft[i].times(fft2[i]);
            }
            return ifft(complexArr3);
        }
        throw new RuntimeException("Dimensions don't agree");
    }

    public static Complex[] convolve(Complex[] complexArr, Complex[] complexArr2) {
        Complex complex = new Complex(Constants.PI, Constants.PI);
        Complex[] complexArr3 = new Complex[(complexArr.length * 2)];
        for (int i = 0; i < complexArr.length; i++) {
            complexArr3[i] = complexArr[i];
        }
        for (int length = complexArr.length; length < complexArr.length * 2; length++) {
            complexArr3[length] = complex;
        }
        Complex[] complexArr4 = new Complex[(complexArr2.length * 2)];
        for (int i2 = 0; i2 < complexArr2.length; i2++) {
            complexArr4[i2] = complexArr2[i2];
        }
        for (int length2 = complexArr2.length; length2 < complexArr2.length * 2; length2++) {
            complexArr4[length2] = complex;
        }
        return cconvolve(complexArr3, complexArr4);
    }

    public static void show(Complex[] complexArr, String str) {
        System.out.println(str);
        System.out.println("-------------------");
        for (Complex println : complexArr) {
            System.out.println(println);
        }
        System.out.println();
    }

    public static void main(String[] strArr) {
        int parseInt = Integer.parseInt(strArr[0]);
        Complex[] complexArr = new Complex[parseInt];
        for (int i = 0; i < parseInt; i++) {
            complexArr[i] = new Complex((double) i, Constants.PI);
            complexArr[i] = new Complex((Math.random() * -2.0d) + 1.0d, Constants.PI);
        }
        show(complexArr, "x");
        Complex[] fft = fft(complexArr);
        show(fft, "y = fft(x)");
        show(ifft(fft), "z = ifft(y)");
        show(cconvolve(complexArr, complexArr), "c = cconvolve(x, x)");
        show(convolve(complexArr, complexArr), "d = convolve(x, x)");
    }
}
