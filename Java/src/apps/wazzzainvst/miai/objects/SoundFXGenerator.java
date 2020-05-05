package apps.wazzzainvst.miai.objects;

import apps.wazzzainvst.miai.utilities.Composer;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SoundFXGenerator {
    private   Composer composer;
    HashMap<String, ArrayList> soundFx = new HashMap<>();
    ArrayList<Synthesizer>threads;
    public VariableRateDataReader samplePlayer;
    public  Synthesizer synthSamp;
    LineOut lineOut;
    private String[] dynamics;
    public ArrayList<String[]> ObjectsFoundFromDetections;
    ArrayList <String> possibleFolders;
    String javaPath;
    private ArrayList<VariableRateDataReader> samplePlayers;

    public  SoundFXGenerator(HashMap<String, ArrayList> soundFx, ArrayList<Synthesizer> threads, Synthesizer synthSamp, LineOut lineOut, String[] dynamics, ArrayList<String> possibleFolders, ArrayList<String[]> objectsFoundFromDetections, String javaPath, VariableRateStereoReader samplePlayer, Composer composer){
        this.soundFx = soundFx;
        this.threads = threads;
        this.synthSamp = synthSamp;
        this.lineOut = lineOut;
        this.dynamics = dynamics;
        this.possibleFolders = possibleFolders;
        this.ObjectsFoundFromDetections = objectsFoundFromDetections;
        this.javaPath = javaPath;
        this.samplePlayer = samplePlayer;
        this.composer = composer;
    }

    public void playFx(int counter, Boolean[] mood) {

        boolean natureMood = mood[0];
        boolean indooreMood =mood[1];
        boolean crowdeMood=mood[2];
        boolean watereMood=mood[3];
        boolean darkeMood=mood[4];
        boolean trafficeMood=mood[5];
        boolean ambianteMood=mood[6];
        boolean smallSpaceeMood=mood[7];

        samplePlayers = new ArrayList<>();
        Random random = new Random();
        //decide when to play an effect,
        if (natureMood){ //play nature sounds
            int playPick = random.nextInt(10);//vary probability of sound
            if (playPick == 1 ){
                //   System.out.println("play nature samples");
                playSample(javaPath,"nature");
            }
        }

        //  System.out.println("TEST: "+indooreMood);
        if (indooreMood){ //play indoor sounds
            int playPick = random.nextInt(20);//vary probability of sound
            if (playPick == 1 ){
                //   System.out.println("play indoor samples");
                playSample(javaPath,"indoor");
            }
        }else {
            int playPick = random.nextInt(20);//vary probability of sound
            if (playPick == 1 ){
                //    System.out.println("play outdoor samples");
                playSample(javaPath,"outdoor");
            }
        }

        if (crowdeMood){ //play crowded sounds
            int playPick = random.nextInt(30);//vary probability of sound
            if (playPick == 1 ){
                //    System.out.println("play crowded samples");
                playSample(javaPath,"crowd");
            }
        }

        if (darkeMood){ //play dark sounds
            int playPick = random.nextInt(20);//vary probability of sound
            if (playPick == 1 ){
                //      System.out.println("play water samples");
                playSample(javaPath,"dark");
            }
        }else{ //play bright sounds
            int playPick = random.nextInt(20);//vary probability of sound
            if (playPick == 1 ){
                //    System.out.println("play bright samples");
                playSample(javaPath,"bright");
            }
        }

        if (watereMood){ //play water sounds
            int playPick = random.nextInt(20);//vary probability of sound
            if (playPick == 1 ){
                //   System.out.println("play water samples");
                playSample(javaPath,"water");
            }
        }

        if (trafficeMood){ //play traffic sounds
            int playPick = random.nextInt(20);//vary probability of sound
            if (playPick == 1 ){
                //   System.out.println("play traffic samples");
                playSample(javaPath,"traffic");
            }
        }

        if (smallSpaceeMood){ //play low reverb sounds
            int playPick = random.nextInt(30);//vary probability of sound
            if (playPick == 1 ){
                //      System.out.println("play small space (low reverb) samples");
                playSample(javaPath,"smallSpace");
            }
        }else{ //play high reverb sounds
            int playPick = random.nextInt(30);//vary probability of sound
            if (playPick == 1 ){
                //   System.out.println("play large space (high reverb) samples");
                playSample(javaPath,"largeSpace");
            }
        }

        if (ambianteMood){ //play ambient sounds
            int playPick = random.nextInt(20); //vary probability of sound
            if (playPick == 1 ){
                //     System.out.println("play ambient samples");
                playSample(javaPath,"ambient");
            }
        }


    }

    private void playSample(String javaPath, String mood) {

        File sampleFile;
        //decide which folder its playing

        String sampleName =  soundFxSelector (mood);;
        sampleFile = new File(javaPath+"SFX\\"+sampleName);


        threads.add(synthSamp);
        System.out.println("Sound FX selected: "+sampleFile);
        FloatSample sample;
        try {
            // Add an output mixer.

            // Load the sample and display its properties.
            SampleLoader.setJavaSoundPreferred(false);
            sample = SampleLoader.loadFloatSample(sampleFile);

            // Start synthesizer using default stereo output at 44100 Hz.

                if (sample.getChannelsPerFrame() == 1) {


                   // samplePlayers.add( samplePlayer  = new VariableRateMonoReader());
                    synthSamp.add(samplePlayer = new VariableRateMonoReader());

                    samplePlayer.output.connect(0, lineOut.input, 0);
                } else if (sample.getChannelsPerFrame() == 2) {

                    synthSamp.add(samplePlayer  = new VariableRateStereoReader());

                    samplePlayer.output.connect(0, lineOut.input, 0);
                    samplePlayer.output.connect(1, lineOut.input, 1);

                } else {
                    throw new RuntimeException("Can only play mono or stereo samples.");
                }

            //panning!!




            samplePlayer.rate.set(sample.getFrameRate());

            //set volume of the sampler
            double variation =1;
            double amplitude = 1;

              Random random = new Random();
            switch (dynamics[0]){
                case "loud":
                    if (dynamics[1].equals("true")){
                        amplitude = 0.2; System.out.println("FX dynamics: loud");
                        variation =(((double) random.nextInt(30)+10) /100);
                    }else {
                    amplitude = 0.1; System.out.println("FX dynamics: loud");
                     }


                    break;
                case "normal": System.out.println("FX dynamics: normal");
                    if (dynamics[1].equals("true")){
                        variation =(((double) random.nextInt(10))+5 /100);
                        amplitude = 0.2;
                    }else {
                        amplitude = 0.05;
                    }
                    break;
                case "quiet": System.out.println("FX dynamics: quiet");
                    if (dynamics[1].equals("true")){
                        variation =((double) random.nextInt(5) /100);
                        amplitude = 0.1;
                    }else {
                        amplitude = 0.025;
                    }
            }

            samplePlayer.amplitude.set(amplitude*variation);
            System.out.println("FX Volume "+samplePlayer.amplitude.get());
            // We only need to start the LineOut. It will pull data from the
            // sample player.
            lineOut.start();


            samplePlayer.dataQueue.queue(sample);
            samplePlayers.add(samplePlayer);

            // We can simply queue the entire file.
            // Or if it has a loop we can play the loop for a while.
            /*if (sample.getSustainBegin() < 0) {
                System.out.println("queue the sample");
                samplePlayer.dataQueue.queue(sample);
            } else {
                System.out.println("queueOn the sample");
                samplePlayer.dataQueue.queueOn(sample);
                synthSamp.sleepFor(8.0);
                System.out.println("queueOff the sample");
                samplePlayer.dataQueue.queueOff(sample);
            }*/

            // Wait until the sample has finished playing.

            synthSamp.sleepFor(0.5);

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Stop everything.
        //     synthSamp.stop();

    }

    private String soundFxSelector(String mood) {
        ArrayList<String> folders = soundFx.get(mood);

        ArrayList<String> possibleSoundFxToPlay = new ArrayList<>();
        for (String audioFolder:possibleFolders) {

      //      System.out.println("FX: Playing "+audioFolder+" sounds in "+mood+ " category");
            //add all the transport sounds with the name of the object

            //get all fx in that folder
            File directory = new File(javaPath+"SFX\\"+mood+"\\");
            File[] files = new File(directory.getAbsolutePath()).listFiles();

            File objDirectory = new File(javaPath+"SFX\\objects\\"+audioFolder);
            File[] objFiles = new File(objDirectory.getAbsolutePath()).listFiles();

            File majorInstrumentFolder = new File(javaPath+"SFX\\objects\\instruments\\major");
            File minorInstrumentFolder = new File(javaPath+"SFX\\objects\\instruments\\minor");
            File chromInstrumentFolder = new File(javaPath+"SFX\\objects\\instruments\\chromatic");


                for (File sampleFile :objFiles){

                //if that sample file name contains any of the object found add it to the final possible fx

                for (String[] obj:ObjectsFoundFromDetections) {

                    String objectName =obj[0];

                    //check if instrument
                    if (sampleFile.getAbsolutePath().contains("instruments") ) {


                        switch (composer.Tonality){

                                case "major": //go through all the major objects and add them

                                    for (File majorIns : majorInstrumentFolder.listFiles()){
                                       // System.out.println("MAjor Instruments: "+majorIns.getAbsolutePath() +" - "+composer.Tonality);

                                        if (majorIns.getName().contains(objectName) && !possibleSoundFxToPlay.contains("objects\\instruments\\major\\" + majorIns.getName())) {
                                            //find any sound which

                                            if (majorIns.getName().contains(".wav")) {
                                        //     System.out.println("MAJOR INS: adding: "+majorIns+ " to possible sounds to play");
                                                possibleSoundFxToPlay.add("objects\\instruments\\major\\" + majorIns.getName());
                                            }
                                        }
                                    }
                                    break;
                                case "minor":
                                    for (File minorIns : minorInstrumentFolder.listFiles()){
                                   //     System.out.println("Minor Instruments: "+minorIns.getAbsolutePath() +" - "+composer.Tonality);

                                        if (minorIns.getName().contains(objectName) && !possibleSoundFxToPlay.contains("objects\\instruments\\minor\\" + minorIns.getName())) {
                                            //find any sound which

                                            if (minorIns.getName().contains(".wav")) {
                                         //     System.out.println("MINOR INS: adding: "+minorIns+ " to possible sounds to play");
                                                possibleSoundFxToPlay.add("objects\\instruments\\minor\\" + minorIns.getName());
                                            }
                                        }
                                    }
                                    break;
                                case "chromatic":
                                    for (File chromaitcIns : chromInstrumentFolder.listFiles()){
                                       // System.out.println("Chromatic Instruments: "+chromaitcIns.getAbsolutePath() +" - "+composer.Tonality);

                                        if (chromaitcIns.getName().contains(objectName) && !possibleSoundFxToPlay.contains("objects\\instruments\\chromatic\\" + chromaitcIns.getName())) {
                                            //find any sound which

                                            if (chromaitcIns.getName().contains(".wav")) {
                                             // System.out.println("CHROMATIC INS: adding: "+chromaitcIns+ " to possible sounds to play");
                                                possibleSoundFxToPlay.add("objects\\instruments\\chromatic\\" + chromaitcIns.getName());
                                            }
                                        }
                                    }
                                    break;
                        }



                    }else {

                        if (sampleFile.getName().contains(objectName) && !possibleSoundFxToPlay.contains("objects\\" + audioFolder + "\\" + sampleFile.getName())) {
                            //find any sound which
                            //       System.out.println("adding: "+sampleFile+ " to possible sounds to play");
                            if (sampleFile.getName().contains(".wav")) {
                                possibleSoundFxToPlay.add("objects\\" + audioFolder + "\\" + sampleFile.getName());
                            }
                        }
                    }
                }
            }

            for (File sampleFile :files){
                //if that sample file name contains any of the object found add it to the final possible fx
                        //find any sound which
             //           System.out.println("adding: mood "+sampleFile+ " to possible sounds to play");
                if (sampleFile.getName().contains(".wav")) {
                    possibleSoundFxToPlay.add(mood + "\\" + sampleFile.getName());
                }
            }
        }

        Random random = new Random();
      int possibleSoundFxToPlayIndex = random.nextInt(possibleSoundFxToPlay.size());
        String fxToPlay = possibleSoundFxToPlay.get(possibleSoundFxToPlayIndex);

        //returns the file name of the fx
        return fxToPlay;
    }

    public ArrayList<VariableRateDataReader> getSamplePlayers() {
        return samplePlayers;
    }
}
