package audio;
import javax.sound.sampled.*;

/** The main audio processing class, implemented as a Runnable so
 * as to be run in a separated execution Thread. */
public class AudioProcessor implements Runnable {
    private AudioSignal inputSignal, outputSignal,FFT;
    private TargetDataLine audioInput;
    private SourceDataLine audioOutput;
    private boolean isThreadRunning; // makes it possible to "terminate" thread

    //Boolean switches to know which audio effect to compute
    public boolean Computefft;
    public boolean ComputeDistortion;
    public boolean Playback;
    public boolean ComputeEcho;

        /** Creates an AudioProcessor that takes input from the given TargetDataLine, and plays back
        * to the given SourceDataLine.
        * @param frameSize the size of the audio buffer. The shorter, the lower the latency. */
    public AudioProcessor(TargetDataLine audioInput, SourceDataLine audioOutput, int frameSize) throws LineUnavailableException { //all exceptions are thrown to AudioIO methods instead of here
        this.inputSignal = new AudioSignal(frameSize);
        this.outputSignal = new AudioSignal(frameSize);
        this.audioInput = audioInput;
        this.audioOutput = audioOutput;
    }

    //Getter for the output signal
    public AudioSignal getOutputSignal() {return outputSignal;}

    //Getter for the FFT signal:
    public AudioSignal getFFT(){return this.FFT;}

    /** Audio processing thread code. Basically an infinite loop that continuously fills the sample
         * buffer with audio data fed by a TargetDataLine and then applies some audio effect, if any,
        * and finally copies data back to a SourceDataLine.*/

        @Override
        public void run() {
            isThreadRunning = true;
            while (isThreadRunning) {
                inputSignal.recordFrom(audioInput);
                //playback with double volume:
                if (this.Playback) {
                    inputSignal.setdBlevel(2 * inputSignal.getdBlevel());
                    try {
                        outputSignal.setFrom(inputSignal);
                        outputSignal.playTo(audioOutput);
                    }catch(Exception e){e.printStackTrace();}
                }
                //distortion effect:
                if (this.ComputeDistortion) {
                    double m = inputSignal.getSample(0);
                    for (int index = 0; index < inputSignal.getFrameSize();index++){
                        if(inputSignal.getSample(index) < m){
                            outputSignal.setSample(index,inputSignal.getSample(index) * 2);
                        }
                        else{
                            outputSignal.setSample(index,inputSignal.getSample(index) / 2);
                        }
                    }
                }
                // we used a simple distortion that increases/decreases volume depending on the first sample, better distortion could have been made
                //by computing the FFT, then removing some frequences or shifting the fft, and lastly computing the inverse FFT.


                //FFT computation:
                if (this.Computefft) {
                    for(int index =0; index < inputSignal.getFrameSize();index++){
                        double[] fft = inputSignal.computeFFT();
                        this.FFT.setSample(index,fft[index]);
                    }
                }


                //Echo computation:
                if (this.ComputeEcho) {

                    for (int index = 0; index < inputSignal.getFrameSize();index++){
                        outputSignal.setSample(index,inputSignal.getSample(index));
                    }
                    for (int index = inputSignal.getFrameSize() / 4; index < inputSignal.getFrameSize();index++){
                        outputSignal.setSample(index, outputSignal.getSample(index) + inputSignal.getSample(index - inputSignal.getFrameSize()) / 2.0); //we simple add to each sample the value of a previous sample (the shift is constant)
                    }
                }

            }
        }

         /** Tells the thread loop to break as soon as possible. This is an asynchronous process. */
        public void terminateAudioThread(){this.isThreadRunning=false;}




        /* an example of a possible test code */
        public static void main(String[] args) {
                TargetDataLine inLine = null;
                SourceDataLine outLine = null;
                AudioProcessor as = null;
                try {
                    inLine = AudioIO.obtainAudioInput("Default Audio Device", 48000);
                    outLine = AudioIO.obtainAudioOutput("Default Audio Device", 48000);
                    as = new AudioProcessor(inLine, outLine, 1024);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try{
                    inLine.open();
                    inLine.start();
                    outLine.open();
                    outLine.start();
                }catch(Exception e){e.printStackTrace();}
        new Thread(as).start();
        }
}
