package apps.wazzzainvst.miai.utilities;

import apps.wazzzainvst.miai.Main;
import apps.wazzzainvst.miai.objects.Instrument;
import apps.wazzzainvst.miai.objects.SoundFXGenerator;
import apps.wazzzainvst.miai.windows.Compositions;
import apps.wazzzainvst.miai.windows.Window_RemoteImageAnalysis;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import apps.wazzzainvst.miai.utilities.Jsyn.GoogleWaveOscillator;
import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.unitgen.*;
import com.softsynth.shared.time.TimeStamp;

import java.io.File;
import java.util.*;

import static java.lang.Math.abs;
/**
 * Class for responsible for playing compositions usinf JSYN
 */
public class Player {
   public static Synthesizer synth,synthSamp;
    UnitGenerator ugen = null;
    UnitVoice voice;
    LineOut lineOut;
    Timer timer;
    UnitOscillator leadOsc;
    UnitOscillator bassOsc;
    UnitOscillator sawOsc;

    UnitOscillator chord1Osc;
    UnitOscillator chord2Osc;
    private boolean running = false;
    HashMap<String, ArrayList> soundFx = new HashMap<>();
    private String[] dynamics;
    Thread t;
    ArrayList<Synthesizer>threads;
    SegmentedEnvelope leadEnvelope,bassEnvelope,sawEnvelope;
    VariableRateDataReader leadEnvelopePlayer,bassEnvelopePlayer,sawEnvelopePlayer,chord1EnvelopePlayer,chord2EnvelopePlayer;
    private SegmentedEnvelope chord1Envelope,chord2nvelope;
    public ArrayList<String[]> ObjectsFoundFromDetections;
    ArrayList <String> possibleFolders;
    private boolean test;
    SoundFXGenerator soundFxGenerator;
    private VariableRateStereoReader samplePlayer;
    private Composer composer;


    public Player(){

        //create sound effect categoris from directoy tree
        File directory = new File(Main.Path+"Java\\SFX");

        if (Main.Path == null){
            directory = new File(Compositions.Path+"Java\\SFX");

            if (Compositions.Path == null){
                directory = new File(Window_RemoteImageAnalysis.Path+"Java\\SFX");
            }
        }


        //iterate through all the files in current dir and print them
        File[] files = new File(directory.getAbsolutePath()).listFiles();
        System.out.println("List dir: "+directory.getAbsolutePath());
        int count = 0;
        for (File folder:files
             ) {
         //   System.out.println("Folder: "+count+": "+folder.getName());
            File[] thisFolder = new File(folder.getAbsolutePath()).listFiles();
            ArrayList <String> samples = new ArrayList();
            int fileCount =0;
            for (File sample:thisFolder
            ) {
           //     System.out.println("Samples: "+fileCount+": "+sample.getName());
                fileCount++;
                samples.add(sample.getName());
            }
            soundFx.put(folder.getName(),samples);
            count++;
        }

    }

    public void playPiece(Instrument leadIns, Instrument bassIns, Instrument sawIns, Instrument chordIns, ArrayList<ArrayList> song, Timer timer, int timeInterval, int begin, int beats, HashMap<String, Double> noteToFreq, Boolean[] mood, String javaPath, String[] dynamics, boolean test, ImageAnalysis imageAnalysis, Composer composer) {
    this.composer = composer;
        this.test = test;
       //sound fx
        try {
            this.ObjectsFoundFromDetections = imageAnalysis.ObjectsFoundFromDetections;
            //catagorize samples by the objects detected
            possibleFolders = new ArrayList<>();
            if (ObjectsFoundFromDetections.size() != 0) {

                for (String[] object : ObjectsFoundFromDetections) {
                    String obj = object[0];
                    String objCategory = imageAnalysis.Objects.get(obj).getCategory();
                    if (!possibleFolders.contains(objCategory)) {
                        possibleFolders.add(objCategory);
                        System.out.println("possible fx folder: " + objCategory);
                    }
                    }
            }
        }catch (Exception e){
          //  throw e;
        }




        this.timer = timer;
        this.dynamics= dynamics;
        // Create a context for the synthesizer.
        synth = JSyn.createSynthesizer();

        // Start synthesizer using default stereo output at 44100 Hz.
        synth.start();

        //synth.add(leadOsc = new TriangleOscillator());
//evelopes
        // Create an envelope and fill it with recognizable data.
        // Add an envelope player.


        synth.add(leadOsc = new SineOscillator());
        synth.add(leadEnvelopePlayer = new VariableRateMonoReader());
        synth.add(sawEnvelopePlayer = new VariableRateMonoReader());
        synth.add(bassEnvelopePlayer = new VariableRateMonoReader());

        //chord player
        synth.add(chord1Osc = new SineOscillator());
        synth.add(chord2Osc = new SineOscillator());
        synth.add(chord1EnvelopePlayer = new VariableRateMonoReader());
        synth.add(chord2EnvelopePlayer = new VariableRateMonoReader());

        synth.add(bassOsc = new SineOscillator());
        synth.add(sawOsc = new GoogleWaveOscillator());
        // Add a stereo audio output unit.
        synth.add(lineOut = new LineOut());

//        leadOsc.amplitude.set(leadIns.getMasterVolume());
        leadOsc.amplitude.set(0.3);

        leadEnvelopePlayer.output.connect( leadOsc.amplitude );
        bassEnvelopePlayer.output.connect( bassOsc.amplitude );
        sawEnvelopePlayer.output.connect( sawOsc.amplitude );

        //chords
        chord1EnvelopePlayer.output.connect( chord1Osc.amplitude );
        chord2EnvelopePlayer.output.connect( chord2Osc.amplitude );

        // Connect the oscillator to both channels of the output.


        leadOsc.output.connect(0, lineOut.input, 0);
        leadOsc.output.connect(0, lineOut.input, 1);
        Delay delay = new Delay();
        delay.addPort( leadEnvelopePlayer.output);

        bassOsc.output.connect(0, lineOut.input, 0);
        bassOsc.output.connect(0, lineOut.input, 1);

        sawOsc.output.connect(0, lineOut.input, 0);
        sawOsc.output.connect(0, lineOut.input, 1);

        chord1Osc.output.connect(0, lineOut.input, 0);
        chord1Osc.output.connect(0, lineOut.input, 1);
        chord2Osc.output.connect(0, lineOut.input, 0);
        chord2Osc.output.connect(0, lineOut.input, 1);

        double timeNow = synth.getCurrentTime();

        // Advance to a near future time so we have a clean start.
      //  TimeStamp timeStamp = new TimeStamp(timeNow + 0.5);
        // Advance to a near future time so we have a clean start.
        final TimeStamp[] timeStamp = {new TimeStamp(timeNow+1)};
        lineOut.start();
        leadOsc.amplitude.set(0);
        leadEnvelopePlayer.amplitude.set(0);
        bassOsc.amplitude.set(0);
        bassEnvelopePlayer.amplitude.set(0);
        sawOsc.amplitude.set(0);
        sawEnvelopePlayer.amplitude.set(0);
        chord1Osc.amplitude.set(0);
        chord1EnvelopePlayer.amplitude.set(0);
        chord2Osc.amplitude.set(0);
        chord2EnvelopePlayer.amplitude.set(0);

        running = true;
        Random random = new Random();
        threads = new ArrayList<>();

        //initialise sample player

        //create sample player
        synthSamp = JSyn.createSynthesizer();
        synthSamp.add(lineOut = new LineOut());
        synthSamp.start();

        //initialize soundFx generator
        soundFxGenerator = new SoundFXGenerator(soundFx,threads,synthSamp,lineOut,dynamics,possibleFolders,ObjectsFoundFromDetections,javaPath,samplePlayer,composer);


        System.out.println("-----Player -------");
        System.out.println("Playing "+song.size()+" Instrument(s)");
        timer.schedule(timerTask(song, noteToFreq, leadIns, leadOsc, sawIns, sawOsc, bassIns, bassOsc,chord1Osc,chord2Osc,chordIns,timeStamp, timeInterval, beats, mood,

                javaPath),
                begin,
                timeInterval
               );
        System.out.println("-----End of Player -------");
    }

    public TimerTask timerTask(ArrayList<ArrayList> song, HashMap<String, Double> noteToFreq, Instrument leadIns,
                               UnitOscillator leadOsc, Instrument sawIns, UnitOscillator sawOsc, Instrument bassIns,
                               UnitOscillator bassOsc, UnitOscillator chord1Osc, UnitOscillator chord2Osc, Instrument chordIns, TimeStamp[] timeStamp, int timeInterval, int beats, Boolean[] mood, String javaPath){
        return new TimerTask() {
            int counter = 0;
            @Override
            public void run() {
                //call the method

                if (counter !=0 ) {
                    //play sound effects
                  //  System.out.println("playing FX: "+counter);
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!test) {
                          soundFxGenerator.playFx(counter, mood);
                            }
                        }
                    });
                    t.start();

                    //play each instrument
                    String []d = (String[]) song.get(0).get(0);

                // System.out.println("melody has :" +song.size());
                    for (int i = 0; i < song.size();i++) {
                        ArrayList <String []> melody = song.get(i);


                        try {
                                  //decide which ins is playing

                                double  freq = noteToFreq.get(melody.get(counter)[0]);
                                double  freq2 = noteToFreq.get(thirdNoteGenerator(melody.get(counter)[0])); //needs to be third apart
                                double vel = Double.parseDouble(melody.get(counter)[1]);
                                double len = Double.parseDouble(melody.get(counter)[2]);
                                //decide which ins is playing

                            if (song.size() == 2){


                                //homophonic texture
                                if (i == 1){
                                    break;
                                }
                                if (vel != 0) {
                                    /* Connect envelope to oscillator amplitude. */
                                    // try {
                                    //System.out.println("playing homophony");
                                    double[] data;
                                    if (mood[6]) { //play smoother notes if ambiance is on
                                        data = new double[]
                                                {
                                                        0.5, 1.0,  // duration,value pair for frame[0] //attack
                                                        //0.30, 0.1,  // duration,value pair for frame[1]
                                                        // 0.50, 0.7,  // duration,value pair for frame[2]
                                                        // 0.50, 0.9,  // duration,value pair for frame[3]
                                                        len, 0.0// duration,value pair for frame[4] //release
                                                };
                                    } else {
                                        data = new double[]
                                                {
                                                        0.1, 0.5,  // duration,value pair for frame[0] //attack
                                                        len, 0.8,
                                                        0.5, 0.0
                                                        // duration,value pair for frame[4] //release
                                                };
                                    }
                                    // Add a tone generator.
                                    chord1Envelope = new SegmentedEnvelope(data);
                                    chord2nvelope = new SegmentedEnvelope(data);


//                                               System.out.println("LEAD VEL: "+(leadOsc.amplitude.get()));

                                    //one beat is 500 mill, so length will be a fraction of mil
                                    Double sleep = 500 * len;
                                    String sleepTime = String.valueOf(sleep).replace(".0", "");
                                    //    System.out.println("Sleeping for:"+sleepTime);
                                    chord1Osc.frequency.set(freq);
                                    chord2Osc.frequency.set(freq2);
                                    //  leadOsc.amplitude.set(0.01);
                                    // ---------------------------------------------
                                    // Queue the entire envelope to play once.
                                    chord1EnvelopePlayer.dataQueue.queue(chord1Envelope);
                                    chord2EnvelopePlayer.dataQueue.queue(chord2nvelope);


                                    if (dynamics[0].equals("loud")){
                                        chord1EnvelopePlayer.amplitude.set(0.025);
                                        chord2EnvelopePlayer.amplitude.set(0.025);
                                    }else {
                                        //vel up or down
                                        if(chord1EnvelopePlayer.amplitude.get() > vel * chordIns.getMasterVolume()){
                                            velDown(chord2EnvelopePlayer,chord2EnvelopePlayer.amplitude.get(),vel * chordIns.getMasterVolume());
                                            velDown(chord1EnvelopePlayer,chord1EnvelopePlayer.amplitude.get(),vel * chordIns.getMasterVolume());
                                        }else {
                                            velUp(chord2EnvelopePlayer,chord2EnvelopePlayer.amplitude.get(),vel * chordIns.getMasterVolume());
                                            velUp(chord1EnvelopePlayer,chord1EnvelopePlayer.amplitude.get(),vel * chordIns.getMasterVolume());
                                        }
                                    }

                                    System.out.println(dynamics[0]+" chord VEL: "+(chord1EnvelopePlayer.amplitude.get()));
                                    // Thread.sleep(Integer.parseInt(sleepTime)-100); //cut off 100 mills for release
                                    //fade out
                                    //     leadOsc.noteOff();
                                } else {
                                    chord1EnvelopePlayer.amplitude.set(0);
                                    chord2EnvelopePlayer.amplitude.set(0);
                                }

                            }else {
                                switch (i) {
                                    case 0:

                                        if (vel != 0) {

                                            if (leadIns.getDynamicVariation()[0]) {

                                               /* if (leadIns.getDynamicVariation()[1]){
                                                    if (leadIns.getMasterVolume() != 0) {


                                                        vel = random.nextFloat() - 0.25;
                                                    }
                                                }else {
                                                    if (leadIns.getMasterVolume() != 0) {
                                                        vel = random.nextFloat() - 0.65;
                                                    }
                                                }*/
                                            }
                                            //freq and amplitude
                                            //leadOsc.noteOn(freq,0);

                                            /* Connect envelope to oscillator amplitude. */
                                            // try {

                                          //  System.out.println("note play to play: "+vel);
                                            double[] data;
                                            if (mood[6]) { //play smoother notes if ambiance is on
                                                data = new double[]
                                                        {
                                                                0.5, 1.0,  // duration,value pair for frame[0] //attack
                                                                //0.30, 0.1,  // duration,value pair for frame[1]
                                                                // 0.50, 0.7,  // duration,value pair for frame[2]
                                                                // 0.50, 0.9,  // duration,value pair for frame[3]
                                                                len, 0.0// duration,value pair for frame[4] //release
                                                        };
                                            } else {
                                                data = new double[]
                                                        {
                                                                0.1, 0.5,  // duration,value pair for frame[0] //attack
                                                                len, 0.8,
                                                                0.5, 0.0
                                                                // duration,value pair for frame[4] //release
                                                        };
                                            }
                                            // Add a tone generator.
                                            leadEnvelope = new SegmentedEnvelope(data);

//                                               System.out.println("LEAD VEL: "+(leadOsc.amplitude.get()));

                                            //one beat is 500 mill, so length will be a fraction of mil
                                            Double sleep = 500 * len;
                                            String sleepTime = String.valueOf(sleep).replace(".0", "");
                                            //    System.out.println("Sleeping for:"+sleepTime);
                                            leadOsc.frequency.set(freq);
                                            //  leadOsc.amplitude.set(0.01);
                                            // ---------------------------------------------
                                            // Queue the entire envelope to play once.
                                            leadEnvelopePlayer.dataQueue.queue(leadEnvelope);
                                            //vel up or down
                                            if(leadEnvelopePlayer.amplitude.get() > vel * leadIns.getMasterVolume()){
                                                velDown(leadEnvelopePlayer,leadEnvelopePlayer.amplitude.get(),vel * bassIns.getMasterVolume());

                                            }else {
                                                velUp(leadEnvelopePlayer,leadEnvelopePlayer.amplitude.get(),vel * leadIns.getMasterVolume());
                                            }
                                            System.out.println(dynamics[0]+" lead VEL: "+(leadEnvelopePlayer.amplitude.get()));

                                            // Thread.sleep(Integer.parseInt(sleepTime)-100); //cut off 100 mills for release
                                            //fade out
                                            //     leadOsc.noteOff();
                                        } else {
                                            velDown(leadEnvelopePlayer,leadEnvelopePlayer.amplitude.get(),0);
                                        }

                                        break;
                                    case 1:
                                        if (vel != 0) {
                                            /*
                                            if (bassIns.getDynamicVariation()[0]){

                                                if (bassIns.getDynamicVariation()[1]){
                                                    if (bassIns.getMasterVolume() != 0) {
                                                        vel = random.nextFloat() - 0.25;
                                                    }
                                                }else {
                                                    if (bassIns.getMasterVolume() != 0) {
                                                        vel = random.nextFloat() - 0.65;
                                                    }
                                                }
                                            }*/
                                            double[] data =
                                                    {
                                                            1, 0.8,  // duration,value pair for frame[0] //attack
                                                            //0.30, 0.1,  // duration,value pair for frame[1]
                                                            // 0.50, 0.7,  // duration,value pair for frame[2]
                                                            // 0.50, 0.9,  // duration,value pair for frame[3]
                                                            len, 1.0,
                                                            0.5, 0.0   // duration,value pair for frame[4] //release
                                                    };

                                            // Add a tone generator.

                                            bassEnvelope = new SegmentedEnvelope(data);

                                            bassOsc.frequency.set(freq, len);
                                          //  bassOsc.amplitude.set(vel * bassIns.getMasterVolume());
                                           // System.out.println("Bass VEL: " + (vel));

                                            // System.out.println("Bass VEL (master): "+(bassIns.getMasterVolume()));
                                            // Queue the entire envelope to play once.
                                            bassEnvelopePlayer.dataQueue.queue(bassEnvelope);

                                            //vel up or down
                                            if(bassEnvelopePlayer.amplitude.get() > vel * bassIns.getMasterVolume()){
                                                velDown(bassEnvelopePlayer,bassEnvelopePlayer.amplitude.get(),vel * bassIns.getMasterVolume());

                                            }else {
                                                velUp(bassEnvelopePlayer,bassEnvelopePlayer.amplitude.get(),vel * bassIns.getMasterVolume());
                                            }
                                        } else {
                                            velDown(bassEnvelopePlayer,bassEnvelopePlayer.amplitude.get(),0);
                                        }


                                    case 2: //chords
                                        //    System.out.println("chords");
                                        if (vel != 0) {

                                            /* Connect envelope to oscillator amplitude. */
                                            // try {
                                            double[] data;
                                            if (mood[6]) { //play smoother notes if ambiance is on
                                                data = new double[]
                                                        {
                                                                0.5, 1.0,  // duration,value pair for frame[0] //attack
                                                                //0.30, 0.1,  // duration,value pair for frame[1]
                                                                // 0.50, 0.7,  // duration,value pair for frame[2]
                                                                // 0.50, 0.9,  // duration,value pair for frame[3]
                                                                len, 0.0// duration,value pair for frame[4] //release
                                                        };
                                            } else {
                                                data = new double[]
                                                        {
                                                                0.1, 0.5,  // duration,value pair for frame[0] //attack
                                                                len, 0.8,
                                                                0.5, 0.0
                                                                // duration,value pair for frame[4] //release
                                                        };
                                            }
                                            // Add a tone generator.
                                            chord1Envelope = new SegmentedEnvelope(data);
                                            chord2nvelope = new SegmentedEnvelope(data);


//                                               System.out.println("LEAD VEL: "+(leadOsc.amplitude.get()));

                                            //one beat is 500 mill, so length will be a fraction of mil
                                            Double sleep = 500 * len;
                                            String sleepTime = String.valueOf(sleep).replace(".0", "");
                                            //    System.out.println("Sleeping for:"+sleepTime);
                                            chord1Osc.frequency.set(freq);
                                            chord2Osc.frequency.set(freq2);
                                            //  leadOsc.amplitude.set(0.01);
                                            // ---------------------------------------------
                                            // Queue the entire envelope to play once.
                                            chord1EnvelopePlayer.dataQueue.queue(chord1Envelope);
                                            chord2EnvelopePlayer.dataQueue.queue(chord2nvelope);

                                            //vel up or down
                                            if(chord1EnvelopePlayer.amplitude.get() > vel * chordIns.getMasterVolume()){
                                                velDown(chord2EnvelopePlayer,chord2EnvelopePlayer.amplitude.get(),vel * chordIns.getMasterVolume());
                                                velDown(chord1EnvelopePlayer,chord1EnvelopePlayer.amplitude.get(),vel * chordIns.getMasterVolume());
                                            }else {
                                                velUp(chord2EnvelopePlayer,chord2EnvelopePlayer.amplitude.get(),vel * chordIns.getMasterVolume());
                                                velUp(chord1EnvelopePlayer,chord1EnvelopePlayer.amplitude.get(),vel * chordIns.getMasterVolume());
                                            }

                                            // Thread.sleep(Integer.parseInt(sleepTime)-100); //cut off 100 mills for release
                                            //fade out
                                            //     leadOsc.noteOff();
                                        } else {
                                            velDown(chord1EnvelopePlayer,chord1EnvelopePlayer.amplitude.get(),0);
                                            velDown(chord2EnvelopePlayer,chord2EnvelopePlayer.amplitude.get(),0);
                                        }

                                    case 3:
                                        if (vel != 0) {
                                           /* if (sawIns.getDynamicVariation()[0]){

                                                if (sawIns.getDynamicVariation()[1]){
                                                    if (sawIns.getMasterVolume() != 0) {


                                                        vel = random.nextFloat() - 0.25;
                                                    }
                                                }else {
                                                    if (sawIns.getMasterVolume() != 0) {
                                                        vel = random.nextFloat() - 0.65;
                                                    }
                                                }
                                            }*/
                                            double[] data =
                                                    {
                                                            0.02, 1.0,  // duration,value pair for frame[0] //attack
                                                            //0.30, 0.1,  // duration,value pair for frame[1]
                                                            // 0.50, 0.7,  // duration,value pair for frame[2]
                                                            // 0.50, 0.9,  // duration,value pair for frame[3]
                                                            len, 0.0   // duration,value pair for frame[4] //release
                                                    };

                                            // Add a tone generator.

                                            sawEnvelope = new SegmentedEnvelope(data);
                                            sawOsc.frequency.set(freq, len);
                                            sawOsc.amplitude.set(vel * sawIns.getMasterVolume());

                                            // Queue the entire envelope to play once.
                                            sawEnvelopePlayer.dataQueue.queue(sawEnvelope);
                                            sawEnvelopePlayer.amplitude.set(vel * sawIns.getMasterVolume());
                                        } else {
                                            sawEnvelopePlayer.amplitude.set(0);
                                        }

                                        break;
                                }
                            }

                        }catch (Exception e){
                         //   e.printStackTrace();
                              //System.out.println(e);
                        }
                    }



                    timeStamp[0] = new TimeStamp( timeStamp[0].getTime() + timeInterval);
                }
                if (counter >= beats+1){

                    running = false;
                    // Stop everything.
                    try {
                        Thread.sleep(timeInterval);
                      //  timer.cancel();
                        stop();
                        //synth.stop();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                counter++;
            }
        };
    }

    private String thirdNoteGenerator(String note) {
      //  System.out.println("calculating third of "+note);

        //calcutate the third degree of the scale given the note to make the chord
        if (note.contains("c")) {
            return note.replace("c", "e");
        }
            if (note.contains("d")) {
                return note.replace("d", "f");
            }
            if (note.contains("e")) {
                return note.replace("e", "g");
            }
            if (note.contains("f")) {
                return note.replace("f", "a");
            }
            if (note.contains("g")) {
                return note.replace("g", "b");
            }
            if (note.contains("a")) {
                return note.replace("a", "c");
            }
           else  {
                return note.replace("b","d");
        }
    }



    public void stop() throws InterruptedException {
        Thread.sleep(200);


        newNoteOff(leadEnvelopePlayer,leadEnvelopePlayer.amplitude.get(),leadEnvelopePlayer.amplitude.get() * 0.75);
        newNoteOff(bassEnvelopePlayer,bassEnvelopePlayer.amplitude.get(),bassEnvelopePlayer.amplitude.get() * 0.75);
        newNoteOff(chord1EnvelopePlayer,chord1EnvelopePlayer.amplitude.get(),chord1EnvelopePlayer.amplitude.get() * 0.75);
        newNoteOff(chord2EnvelopePlayer,chord2EnvelopePlayer.amplitude.get(),chord2EnvelopePlayer.amplitude.get() * 0.75);
//
        for (VariableRateDataReader  sammplePlayer:
             soundFxGenerator.getSamplePlayers()) {
            try{
                newNoteOff(sammplePlayer,sammplePlayer.amplitude.get(),samplePlayer.amplitude.get() * 0.75);
            }catch (Exception e) {

//

            }
        }
        Thread.sleep(200);
    //    System.out.println("Stage 2");
        newNoteOff(leadEnvelopePlayer,leadEnvelopePlayer.amplitude.get(),leadEnvelopePlayer.amplitude.get() * 0.5);
        newNoteOff(bassEnvelopePlayer,bassEnvelopePlayer.amplitude.get(),bassEnvelopePlayer.amplitude.get() * 0.5);
        newNoteOff(chord1EnvelopePlayer,chord1EnvelopePlayer.amplitude.get(),chord1EnvelopePlayer.amplitude.get() * 0.5);
        newNoteOff(chord2EnvelopePlayer,chord2EnvelopePlayer.amplitude.get(),chord2EnvelopePlayer.amplitude.get() * 0.5);

        for (VariableRateDataReader  sammplePlayer:
                soundFxGenerator.getSamplePlayers()) {
            try{
                newNoteOff(sammplePlayer,sammplePlayer.amplitude.get(),samplePlayer.amplitude.get() * 0.5);
            }catch (Exception e) {

            }
        }
        System.out.println(leadEnvelopePlayer.amplitude.get());
        System.out.println(bassEnvelopePlayer.amplitude.get());
        System.out.println(chord1EnvelopePlayer.amplitude.get());
        System.out.println(chord2EnvelopePlayer.amplitude.get());


        Thread.sleep(200);
    //    System.out.println("Stage 3");
        newNoteOff(leadEnvelopePlayer,leadEnvelopePlayer.amplitude.get(),leadEnvelopePlayer.amplitude.get() * 0.25);
        newNoteOff(bassEnvelopePlayer,bassEnvelopePlayer.amplitude.get(),bassEnvelopePlayer.amplitude.get() * 0.25);
        newNoteOff(chord1EnvelopePlayer,chord1EnvelopePlayer.amplitude.get(),chord1EnvelopePlayer.amplitude.get() * 0.25);
        newNoteOff(chord2EnvelopePlayer,chord2EnvelopePlayer.amplitude.get(),chord2EnvelopePlayer.amplitude.get() * 0.25);

        for (VariableRateDataReader  sammplePlayer:
                soundFxGenerator.getSamplePlayers()) {
            try{

                newNoteOff(sammplePlayer,sammplePlayer.amplitude.get(),samplePlayer.amplitude.get() * 0.25);
            }catch (Exception e) {

            }
         //
        }

        Thread.sleep(200);
      //  System.out.println("Stage 4");
        newNoteOff(leadEnvelopePlayer,leadEnvelopePlayer.amplitude.get(),0);
        newNoteOff(bassEnvelopePlayer,bassEnvelopePlayer.amplitude.get(),0);
        newNoteOff(chord1EnvelopePlayer,chord1EnvelopePlayer.amplitude.get(),0);
        newNoteOff(chord2EnvelopePlayer,chord2EnvelopePlayer.amplitude.get(),0);
        for (VariableRateDataReader  sammplePlayer:
                soundFxGenerator.getSamplePlayers()) {

            try{
                newNoteOff(sammplePlayer,sammplePlayer.amplitude.get(),0);
            }catch (Exception e) {

            }
         //
        }




        Thread.sleep(500);
        try {
            synth.stop();
            synthSamp.stop();
        }catch (Exception ignored){

        }

        timer.cancel();
        running= false;
        //stop all playing samples
        for (Synthesizer t : threads) {
            t.stop();
        }
        soundFxGenerator.samplePlayer.stop();
        lineOut.stop();
    }

    public boolean isPlaying() {
        return running;
    }

    private void newNoteOff(VariableRateDataReader osc,double currentVel, double targetVel) throws InterruptedException
    {
        velDown(osc,currentVel,targetVel);
    }

    private void velUp(VariableRateDataReader osc,double currentVel, double targetVel) throws InterruptedException
    {
        if (currentVel != targetVel) {
          //  System.out.println("-------Volume Up-------");
          //  System.out.println("vel up target " + targetVel+ " current: "+currentVel);
            double amp = currentVel;
            for (int i = 0; i < 1000; i++) {

                amp = amp + 0.005;
                osc.amplitude.set(amp);
                //   System.out.println("COUNTING uP: "+amp);

                Thread.sleep(1);
                if (amp + 0.005 >= targetVel || amp + 0.005 >= 0.95) {
                    break;
                }
            }
          //  System.out.println("vel up to " + osc.amplitude.get());
        }
    }
    private void velDown(VariableRateDataReader osc,double currentVel, double targetVel) throws InterruptedException
    {
        if (currentVel != targetVel) {
          //  System.out.println("-------Volume Down-------");
            double amp = currentVel;
          //  System.out.println("vel down target " + targetVel+ " current: "+currentVel);
            for (int i = 0; i < 1000; i++) {


                amp = amp - 0.005;
                osc.amplitude.set(amp);
                //   System.out.println("COUNTING down: "+amp);

                Thread.sleep(1);
                if (amp - 0.005 < targetVel || amp - 0.005 <= 0) {
                    break;
                }
            }
         //   System.out.println("vel down to " + osc.amplitude.get());
        }
    }
}
//}
