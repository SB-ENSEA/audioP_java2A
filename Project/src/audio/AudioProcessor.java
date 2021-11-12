package audio;
import javax.sound.sampled.*;

/** The main audio processing class, implemented as a Runnable so
 * as to be run in a separated execution Thread. */
public class AudioProcessor implements Runnable {
    private AudioSignal inputSignal, outputSignal;
    private TargetDataLine audioInput;
    private SourceDataLine audioOutput;
    private boolean isThreadRunning; // makes it possible to "terminate" thread

        /** Creates an AudioProcessor that takes input from the given TargetDataLine, and plays back
        * to the given SourceDataLine.
        * @param frameSize the size of the audio buffer. The shorter, the lower the latency. */
        public AudioProcessor(TargetDataLine audioInput, SourceDataLine audioOutput, int frameSize) throws LineUnavailableException {
            AudioSignal audio = new AudioSignal(frameSize);
            audio.recordFrom(audioInput);
            audio.playTo(audioOutput);
        }

        /** Audio processing thread code. Basically an infinite loop that continuously fills the sample
         * buffer with audio data fed by a TargetDataLine and then applies some audio effect, if any,
        * and finally copies data back to a SourceDataLine.*/

        @Override
        public void run() {
        isThreadRunning = true;
        while (isThreadRunning) {
            inputSignal.recordFrom(audioInput);
            //playback with double volume:
            inputSignal.setdBlevel(2 * inputSignal.getdBlevel());
            outputSignal.setFrom(inputSignal);
            //distortion effect:
       /*     double m=inputSignal.getSample(0);
            for(int index=0; i<inputSignal.getFrameSize(),i++){
                if inputSignal.getSample(i)<m{outputSignal.setSample(i)=inputSignal.getSample(i)*2;}
                else {outputSignal.setSample(i)=inputSignal.getSample(i)/2;}
            }
            */
            }
        }
         /** Tells the thread loop to break as soon as possible. This is an asynchronous process. */
        public void terminateAudioThread(){this.isThreadRunning=false;}

        // todo here: all getters and setters

        /* an example of a possible test code */
        public static void main(String[] args) {
                TargetDataLine inLine = null;
                SourceDataLine outLine = null;
                AudioProcessor as = null;
                try {
                    inLine = AudioIO.obtainAudioInput("Default Audio Device", 16000);
                    outLine = AudioIO.obtainAudioOutput("Default Audio Device", 16000);
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
        System.out.println("A new thread has been created!");
        }
}
