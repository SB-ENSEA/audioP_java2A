package math;
import java.lang.Math;

public class Complex {
    public double re;
    public double im;
    private static final Complex i=new Complex(0,1); // the complex i, it is final so it can't be modified and static because it is shared by all complexes.
    public Complex() {
        this(0, 0);
    }

    public Complex(double r, double i) {
        re = r;
        im = i;
    }

    //all methods for
    public Complex add(Complex b) {
        return new Complex(this.re + b.re, this.im + b.im);
    }

    public Complex sub(Complex b) {
        return new Complex(this.re - b.re, this.im - b.im);
    }

    public Complex mult(Complex b) {
        return new Complex(this.re * b.re - this.im * b.im,
                this.re * b.im + this.im * b.re);
    }

    public double abs(){
        return Math.sqrt(Math.pow(this.re,2) + Math.pow(this.im,2));

    }

    //we use fft as a method of complex as it is used only once in the code. We also are able to use all the Complex class methods directly
    public static Complex[] fft(Complex[] x) {
        int n = x.length;
        // base case
        if (n == 1) return new Complex[] { x[0] };
        // radix 2 Cooley-Tukey FFT
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }
        Complex[] even = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] evenFFT = fft(even);
        // compute FFT of odd terms
        Complex[] odd  = even;  // reuse the array (to avoid n log n space)
        for (int k = 0; k < n/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] oddFFT = fft(odd);
        // combine
        Complex[] y = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            double kth = -2 * k * Math.PI / n;
            Complex wk = Cexp(kth);
            y[k]       = evenFFT[k].add(wk.mult(oddFFT[k]));
            y[k + n/2] = evenFFT[k].sub(wk.mult(oddFFT[k]));
        }
        return y;
    }

    //method to compute the complex exponential of an argument z. not used right now.
    public static Complex Cexp(double z){
        return new Complex(Math.cos(z),Math.sin(z));
    }


    @Override
    public String toString() {
        return String.format("(%f,%f)", re, im);
    }
}