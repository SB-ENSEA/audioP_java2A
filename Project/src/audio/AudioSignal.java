package audio;
import math.Complex;
import javax.sound.sampled.*;
import java.util.ArrayList;



/** A container for an audio signal backed by a double buffer so as to allow floating point calculation
for signal processing and avoid saturation effects. Samples are 16 bit wide in this implementation. */
public class AudioSignal {

    private double[] sampleBuffer; // floating point representation of audio samples
    private double dBlevel; // current signal level

    public void setdBlevel(double dBleveltarget) {
        double dBlevelpresent = this.dBlevel;
        double scaler = dBleveltarget / dBlevelpresent;
        scaler = Math.pow(10, scaler / 20);
        for (int i = 0; i < this.getFrameSize(); i++) {
            this.setSample(i, scaler * this.getSample(i));
        }
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
    public void setFrom(AudioSignal other) throws Exception {
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


    double getSample(int i) {
        return this.sampleBuffer[i];
    }

    void setSample(int i, double value) {
        this.sampleBuffer[i] = value;
    }

    int getFrameSize() {
        return this.sampleBuffer.length;
    }

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
   /* Complex[] computeFFT(){
        Complex t = new Complex(1,1);
        Complex e = new Complex(2,1);
        Complex s = new Complex(1,2);
        return
    }*/

}


