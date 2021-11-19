package audio;
import math.Complex;
import java.lang.*;
import javax.sound.sampled.*;
import java.util.ArrayList;



/** A container for an audio signal backed by a double buffer so as to allow floating point calculation
for signal processing and avoid saturation effects. Samples are 16 bit wide in this implementation. */
public class AudioSignal {

    private double[] sampleBuffer; // floating point representation of audio samples
    private double dBlevel; // current signal level




    //a few simple methods to compute the other methods in this class
    double getSample(int i) {
        return this.sampleBuffer[i];
    }

    void setSample(int i, double value) {
        this.sampleBuffer[i] = value;
    }

    public int getFrameSize() {
        return this.sampleBuffer.length;
    }



    /**
     * Construct an AudioSignal that may contain up to "frameSize" samples.
     *
     * @param frameSize the number of samples in one audio frame
     */
    public AudioSignal(int frameSize) {
        this.sampleBuffer = new double[frameSize];
    }


    /**
     * Sets the content of this signal from another signal.
     *
     * @param other other.length must not be lower than the length of this signal.
     */
    public void setFrom(AudioSignal other) throws Exception { //This throws an Exception just for principle. In reality everytime we use setFrom the 'other' audio signal is right by design
        if (other.getFrameSize() < this.getFrameSize()) {
            Exception e = new Exception();
            throw e;
        }
        for (int i = 0; i <= other.sampleBuffer.length; i++) {
            this.setSample(i, other.getSample(i));
        }
    }

    /**
     * Fills the buffer content from the given input. Byte's are converted on the fly to double's.
     *
     * @return false if at end of stream
     */
    public boolean recordFrom(TargetDataLine audioInput) {
        byte[] byteBuffer = new byte[sampleBuffer.length * 2]; // 16 bit samples
        if (audioInput.read(byteBuffer, 0, byteBuffer.length) == -1) return false;
        for (int i = 0; i < sampleBuffer.length; i++) {
            sampleBuffer[i] = ((byteBuffer[2 * i] << 8) + byteBuffer[2 * i + 1]) / 32768.0; // big endian
            //this.setSample(i, ((byteBuffer[2 * i] << 8) + byteBuffer[2 * i + 1]) / 32768.0);
        }
        this.getdBlevel();
        return true;
    }

    /**
     * Plays the buffer content to the given output.
     *
     * @return false if at end of stream
     */
    public boolean playTo(SourceDataLine audioOutput) {
        AudioFormat format = new AudioFormat(8000, 16, 1, true, true);
        byte[] buffer = new byte[this.sampleBuffer.length * 2];
        if (audioOutput.write(buffer, 0, buffer.length) == -1) {
            return false;
        }
        for (int i = 0; i < buffer.length; i++) {
            this.sampleBuffer[i] = ((buffer[2 * i] << 8) + buffer[2 * i + 1]) / 32768.0;
            //this.setSample(i, ((buffer[2 * i] << 8) + buffer[2 * i + 1]) / 32768.0);
        }
        this.getdBlevel();
        return true;
    }

    //Few choices have been made for this function : We define the Db level as the level of the average of the samples (see AudioSignal.getDblevel)
    //this function computes the multiplicative coefficient to apply to each sample to reach the chosen Db value i.e. this method does nothing if the target db level is the same as the current db level
    public void setdBlevel(double dBleveltarget) {
        double dBlevelpresent = this.dBlevel;
        double scaler = dBleveltarget / dBlevelpresent;
        scaler = Math.pow(10, scaler / 20);
        for (int i = 0; i < this.getFrameSize(); i++) {
            this.setSample(i, scaler * this.getSample(i));
        }
    }

    //As said previously, the DB level is defined as the db level of the mean of samples.
    //The value of the Db level is then closer to a 'volume' parameter that exist in a lot of other audio processing systems
    double getdBlevel() {
        double sum = 0;
        double med = 0;
        for (int i = 0; i < this.getFrameSize(); i++) {
            sum += this.getSample(i);
        }
        med = sum / this.getFrameSize();
        this.dBlevel = 20 * Math.log(med);
        return this.dBlevel;
    }


    double[] computeFFT(){
        //type conversion from double to complex
        Integer N = this.sampleBuffer.length;
        if(!AudioIO.IsPowOf2(N)){N= Integer.highestOneBit(N);} //Check that the signal has length of a power of 2. If not,remove the last samples
        Complex[] x = new Complex[N];
        int i = 0;
        for(i =0;i<N;i++){
            x[i].re=this.getSample(i);
        }
        //I found no better implementation than to use 3 lists, not knowing if x = Complex.fft(x) would work considering the fft code
        Complex[] y = Complex.fft(x);
        double[] z = new double[N];
        for(i=0;i<N;i++){z[i]=y[i].abs();}
        return z;
    }




}


