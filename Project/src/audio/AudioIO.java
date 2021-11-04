package audio;
import javax.sound.sampled.*;
import java.lang.annotation.Target;
import java.util.Arrays;

/** A collection of static utilities related to the audio system. */
public class AudioIO {



    /** Displays every audio mixer available on the current system. */
    public static void printAudioMixers() {
     System.out.println("Mixers:");
     Arrays.stream(AudioSystem.getMixerInfo())
     .forEach(e -> System.out.println("- name=\"" + e.getName() + "\" description=\"" + e.getDescription() + " by " + e.getVendor() + "\""));
     }




    /** @return a Mixer.Info whose name matches the given string.
    Example of use: getMixerInfo("Macbook default output") */

    public static Mixer.Info getMixerInfo(String mixerName) {
         // see how the use of streams is much more compact than for() loops!
         return Arrays.stream(AudioSystem.getMixerInfo())
         .filter(e -> e.getName().equalsIgnoreCase(mixerName)).findFirst().get();
         }

    /** Return a line that's appropriate for recording sound from a microphone.
  * Example of use:
  * TargetDataLine line = obtainInputLine("USB Audio Device", 8000);
     **/
   public static TargetDataLine obtainInputLine(String mixerName,float sampleRate) throws Exception{
       AudioFormat format= new AudioFormat(sampleRate,8,1,true,true);
       Mixer.Info mixInfo = getMixerInfo(mixerName);
       Mixer mixer = AudioSystem.getMixer(mixInfo);
       DataLine.Info  targetInfo=new DataLine.Info(TargetDataLine.class,format);
       return (TargetDataLine) mixer.getLine(Arrays.stream(mixer.getTargetLineInfo()).filter(lineInfo -> lineInfo.matches(targetInfo)).findFirst().get());
       /*for(Line lines : mixer.getTargetLines()){
           if(lines.info.matches(target){
               AudioSystem.getTargetDataLine(format,mixInfo);
           }
       }
       return null
       */
   }



  /** @param mixerName a string that matches one of the available mixers.
    //@see AudioSystem.getMixerInfo() which provides a list of all mixers on your system.
     public static TargetDataLine obtainAudioInput(String mixerName, int sampleRate){ ... }
      Return a line that's appropriate for playing sound to a loudspeaker. */

    public static SourceDataLine obtainAudioOutput(String mixerName, int sampleRate) throws Exception {
        AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, true);
        Mixer.Info mixInfo = getMixerInfo(mixerName);
        Mixer mixer = AudioSystem.getMixer(mixInfo);
        DataLine.Info targetInfo = new DataLine.Info(SourceDataLine.class, format);
        return (SourceDataLine) mixer.getLine(Arrays.stream(mixer.getSourceLineInfo()).filter(lineInfo -> lineInfo.matches(targetInfo)).findFirst().get());
    }


     public static void main(String[] args){
        int sampleRate = 8000;
        AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, true);
        SourceDataLine Source=null;
        TargetDataLine Target=null;
        AudioSignal sig = new AudioSignal(80000);
        Boolean lineAvailable = false;
        for(Mixer.Info mixInf : AudioSystem.getMixerInfo()) {
            try {
                Source = obtainAudioOutput(mixInf.getName(), sampleRate);
                Target = obtainInputLine(mixInf.getName(), sampleRate);
                lineAvailable = true;
            } catch (Exception e) {
                lineAvailable = false;
            }
        }
        if (!lineAvailable){
            System.out.println("no available lines");
        }
        else{
            sig.recordFrom(Target);
            sig.playTo(Source);
        }
     }

}
