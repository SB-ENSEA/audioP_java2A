package audio;
import javax.sound.sampled.*;
import java.util.Arrays;

/** A collection of static utilities related to the audio system. */
public class AudioIO {



    /** Displays every audio mixer available on the current system. */
    public static void printAudioMixers() {
     System.out.println("Mixers:");
     Arrays.stream(AudioSystem.getMixerInfo())
     .forEach(e -> System.out.println("- name=\"" + e.getName() + "\" description=\"" + e.getDescription() + " by " + e.getVendor() + "\""));
     }

     //gets the name of the available mixers on the computer, for the comboBox of the interface
    public static String[] getMixerNames(){
        int N =(AudioSystem.getMixerInfo()).length;
        String[] L = new String[N];
        Mixer.Info[] Info = AudioSystem.getMixerInfo();
        for(int i=0; i<N; i++){
            L[i]=(Info[i]).getName();
        }
        return L;
    }


    /** @return a Mixer.Info whose name matches the given string.
    Example of use: getMixerInfo("Macbook default output") */

    public static Mixer.Info getMixerInfo(String mixerName) {
         return Arrays.stream(AudioSystem.getMixerInfo())
         .filter(e -> e.getName().equalsIgnoreCase(mixerName)).findFirst().get();
         }

    /** Return a line that's appropriate for recording sound from a microphone.
  * Example of use:
  * TargetDataLine line = obtainInputLine("USB Audio Device", 8000);
     **/
   public static TargetDataLine obtainAudioInput(String mixerName,float sampleRate) throws LineUnavailableException{
       AudioFormat format= new AudioFormat(sampleRate,24,1,true,true);
       Mixer.Info mixInfo = getMixerInfo(mixerName);
       return AudioSystem.getTargetDataLine(format,mixInfo);


       //tried to do it without AudioSystem.getTargetDataLine
       // Listing all lines in the chosen mixer and checking if their format match.
       /*
       Mixer mixer = AudioSystem.getMixer(mixInfo);
       DataLine.Info  targetInfo=new DataLine.Info(TargetDataLine.class,format);
       return (TargetDataLine) mixer.getLine(Arrays.stream(mixer.getTargetLineInfo()).filter(lineInfo -> lineInfo.matches(targetInfo)).findFirst().get());
       */

       /*for(Line lines : mixer.getTargetLines()){
           if(lines.info.matches(target){
               AudioSystem.getTargetDataLine(format,mixInfo);
           }
       }
       return null
       */
   }

    //used to compute the fft
    public static boolean IsPowOf2(int N){
        int n=N;
        while(n%2==0) {
            if(n==64){ //any power of two works, we chose 64 because we know our signal could never be so small
                return true;
            }
            n=N/2;
        }
        return false;
    }


  /** @param mixerName a string that matches one of the available mixers.
    //@see AudioSystem.getMixerInfo() which provides a list of all mixers on your system.
     public static TargetDataLine obtainAudioInput(String mixerName, int sampleRate){ ... }
      Return a line that's appropriate for playing sound to a loudspeaker. */

    public static SourceDataLine obtainAudioOutput(String mixerName, int sampleRate) throws LineUnavailableException {
        AudioFormat format = new AudioFormat(sampleRate, 24, 2, true, true);
        Mixer.Info mixInfo = getMixerInfo(mixerName);
        return AudioSystem.getSourceDataLine(format,mixInfo);


        //tried to do it without AudioSystem.getSourceDataLine
        // Listing all lines in the chosen mixer and checking if their format match.
        /*
        Mixer mixer = AudioSystem.getMixer(mixInfo);
        DataLine.Info SourceInfo = new DataLine.Info(SourceDataLine.class, format);
        return (SourceDataLine) mixer.getLine(Arrays.stream(mixer.getSourceLineInfo()).filter(lineInfo -> lineInfo.matches(SourceInfo)).findFirst().get());
        */

    }

    public static void StopAudioProcessing(AudioProcessor process){
        process.terminateAudioThread();
    } //kinda duplicate with audioProcessor.terminateAudioThread, especially when startAudioProcessing return an AudioProcessor.


    public static AudioProcessor startAudioProcessing(String inputMixer,String outputMixer, int sampleRate, int frameSize){
        AudioFormat format = new AudioFormat(sampleRate, 24, 1, true, true);
        SourceDataLine Source=null;
        TargetDataLine Target=null;
        AudioSignal SourceSig = null;
        AudioSignal TargetSig = null;
        AudioSignal sig = new AudioSignal(frameSize);
        try{
            Source = obtainAudioOutput(inputMixer, sampleRate);
            Source.open();
            Source.start();
            Target = obtainAudioInput(outputMixer, sampleRate);
            Target.start();
            Target.open();
            return new AudioProcessor(Target,Source,frameSize);
        }catch(Exception e){System.out.println("no compatible lines in given mixers");} //here we centralize any LineUnavailableException that we threw from our other methods
       return null;
    }
    //we chose to return an Audioprocessor so we can have a single AudioProcessor instance for the Main class


    //test main() that uses my computer's peripherals
   public static void main(String[] args){startAudioProcessing("Microphone (Realtek Audio)","Haut-parleurs / ??couteurs (Realtek Audio)",8000,8000*5);}

     // some main() functions that have been used during debugging
    /*public static void main(String[] args){
         int sampleRate = 44100;
         SourceDataLine Source=null;
         TargetDataLine Target=null;
         printAudioMixers();
         AudioSignal sig = new AudioSignal(48000);
         try{
         Source=obtainAudioOutput(getMixerNames()[4],sampleRate);
         }catch(Exception e){e.printStackTrace();}
     }*/
/*
    public static void main(String[] args){
        printAudioMixers();
        AudioFormat format = new AudioFormat(48000, 24, 1, true, true);
        Mixer.Info Minfo = getMixerInfo("Port Mixage st??r??o (Realtek Audio)" );
        System.out.println(Minfo);
        Mixer m = AudioSystem.getMixer(Minfo);
        Line.Info[] lineList = m.getTargetLineInfo();
        System.out.println(lineList);
        System.out.println(Arrays.stream(lineList).count());
        System.out.println(lineList[0].getLineClass());


    }*/



}
