package com.rhewumapp.Activity.fft;

import com.itextpdf.text.html.HtmlTags;
import com.rhewumapp.Activity.MeshConveterData.Constants;

import java.io.PrintStream;

public class Complex {
    private final double im;
    private final double re;

    public Complex(double d, double d2) {
        this.re = d;
        this.im = d2;
    }

    public String toString() {
        double d = this.im;
        if (d == Constants.PI) {
            return this.re + "";
        } else if (this.re == Constants.PI) {
            return this.im + HtmlTags.I;
        } else if (d < Constants.PI) {
            return this.re + " - " + (-this.im) + HtmlTags.I;
        } else {
            return this.re + " + " + this.im + HtmlTags.I;
        }
    }

    public double abs() {
        return Math.hypot(this.re, this.im);
    }

    public double phase() {
        return Math.atan2(this.im, this.re);
    }

    public Complex plus(Complex complex) {
        return new Complex(this.re + complex.re, this.im + complex.im);
    }

    public Complex minus(Complex complex) {
        return new Complex(this.re - complex.re, this.im - complex.im);
    }

    public Complex times(Complex complex) {
        double d = this.re;
        double d2 = complex.re;
        double d3 = this.im;
        double d4 = complex.im;
        return new Complex((d * d2) - (d3 * d4), (d * d4) + (d3 * d2));
    }

    public Complex times(double d) {
        return new Complex(this.re * d, d * this.im);
    }

    public Complex conjugate() {
        return new Complex(this.re, -this.im);
    }

    public Complex reciprocal() {
        double d = this.re;
        double d2 = this.im;
        double d3 = (d * d) + (d2 * d2);
        return new Complex(d / d3, (-d2) / d3);
    }

    public double re() {
        return this.re;
    }

    public double im() {
        return this.im;
    }

    public Complex divides(Complex complex) {
        return times(complex.reciprocal());
    }

    public Complex exp() {
        return new Complex(Math.exp(this.re) * Math.cos(this.im), Math.exp(this.re) * Math.sin(this.im));
    }

    public Complex sin() {
        return new Complex(Math.sin(this.re) * Math.cosh(this.im), Math.cos(this.re) * Math.sinh(this.im));
    }

    public Complex cos() {
        return new Complex(Math.cos(this.re) * Math.cosh(this.im), (-Math.sin(this.re)) * Math.sinh(this.im));
    }

    public Complex tan() {
        return sin().divides(cos());
    }

    public static Complex plus(Complex complex, Complex complex2) {
        return new Complex(complex.re + complex2.re, complex.im + complex2.im);
    }

    public static void main(String[] strArr) {
        Complex complex = new Complex(5.0d, 6.0d);
        Complex complex2 = new Complex(-3.0d, 4.0d);
        PrintStream printStream = System.out;
        printStream.println("a            = " + complex);
        PrintStream printStream2 = System.out;
        printStream2.println("b            = " + complex2);
        PrintStream printStream3 = System.out;
        printStream3.println("Re(a)        = " + complex.re());
        PrintStream printStream4 = System.out;
        printStream4.println("Im(a)        = " + complex.im());
        PrintStream printStream5 = System.out;
        printStream5.println("b + a        = " + complex2.plus(complex));
        PrintStream printStream6 = System.out;
        printStream6.println("a - b        = " + complex.minus(complex2));
        PrintStream printStream7 = System.out;
        printStream7.println("a * b        = " + complex.times(complex2));
        PrintStream printStream8 = System.out;
        printStream8.println("b * a        = " + complex2.times(complex));
        PrintStream printStream9 = System.out;
        printStream9.println("a / b        = " + complex.divides(complex2));
        PrintStream printStream10 = System.out;
        printStream10.println("(a / b) * b  = " + complex.divides(complex2).times(complex2));
        PrintStream printStream11 = System.out;
        printStream11.println("conj(a)      = " + complex.conjugate());
        PrintStream printStream12 = System.out;
        printStream12.println("|a|          = " + complex.abs());
        PrintStream printStream13 = System.out;
        printStream13.println("tan(a)       = " + complex.tan());
    }
}
