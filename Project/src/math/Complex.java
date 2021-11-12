package math;
import java.lang.Math;

public class Complex {
    public double re;
    public double im;
    private static final Complex i=new Complex(0,1);
    public Complex() {
        this(0, 0);
    }

    public Complex(double r, double i) {
        re = r;
        im = i;
    }

    public Complex Cexp(double z){
        Complex res = new Complex();
        res.re=Math.cos(z);
        res.im=Math.sin(z);
        return res;

    }

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


    public static Complex[] fft(Complex[] x) {
        int n = x.length;
        // base case
        if (n == 1) return new Complex[] { x[0] };
        // radix 2 Cooley-Tukey FFT
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }
        // compute FFT of even terms
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
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = evenFFT[k].add(wk.mult(oddFFT[k]));
            y[k + n/2] = evenFFT[k].sub(wk.mult(oddFFT[k]));
        }
        return y;
    }

    @Override
    public String toString() {
        return String.format("(%f,%f)", re, im);
    }
}