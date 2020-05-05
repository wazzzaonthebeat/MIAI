package apps.wazzzainvst.miai.utilities;
/**
 * Class responsible for creating the musical composition based off of the musical image analysis
 */

import apps.wazzzainvst.miai.objects.Instrument;
import apps.wazzzainvst.miai.objects.NoteToFreq;
import apps.wazzzainvst.miai.windows.Compositions;

import java.util.*;

import static java.lang.Math.abs;

public class Composer {
    HashMap<String, Double> noteToFreq = new HashMap<>();
    HashMap<String, Double> noteToFreqChromatic = new HashMap<>();
    Instrument leadIns;
    Instrument bassIns;
    Instrument sawIns;
    Instrument chordIns;
    Timer timer;
    int begin;
    int timeInterval;
    int beats;
    ImageAnalysis imageAnalysis;
    String prev = "";
    boolean major;
    int octave;
    Player player;

    //musical elements
    boolean densityDense;
    String avgSizeBig;
    boolean chromatic;
    String noteSpaces;
    String noteLength;
    String texture;
    String tempo;
    boolean syncopation;
    String length;
    String structure;
    public boolean tonality;
    String noteVelocity;
    ArrayList<ArrayList> song;
    double avgDistance = 0;
    private boolean test;
    private String avgObjectSizeRelativeToImage;
    public String dynamics;
    public String dyamicsLevel;
    public String Tonality;
    public ArrayList<String[]> ObjectsFoundFromDetections;
    public Composer(Instrument leadIns, Instrument bass, Instrument sawIns, Instrument chord, Timer timer, int begin, int timeInterval, int beats, ImageAnalysis imageAnalysis, Player player) {

        //assign note to freq
        NoteToFreq NoteToFreq = new NoteToFreq();
        noteToFreq = NoteToFreq.getNotes();
        noteToFreqChromatic = NoteToFreq.getNotesChromatic();


        this.leadIns = leadIns;
        this.bassIns = bass;
        this.sawIns = sawIns;
        this.chordIns = chord;
        this.timer = timer;
        this.begin = begin;
        this.timeInterval = timeInterval;
        this.beats = beats;
        this.imageAnalysis = imageAnalysis;
        this.player = player;

        try {


            if (imageAnalysis.getTonality().equals("major")) {
                this.major = true;
            } else {
                this.major = false;
            }

            tonality = false;
            if (imageAnalysis.getTonality().equals("major")) {
                tonality = true;
            }
        } catch (Exception e) {

        }
    }

    public void start(){
        song = AlgorithmicPiece1(imageAnalysis.getMood(), tonality, imageAnalysis.getTempo(), imageAnalysis.getScene());

    }
    public void tester(Boolean[] mood, Boolean tonality, String tempo,
                       String scene, boolean densityDense, String avgSizeBig,
                       boolean chromatic, String noteSpaces, String noteLength,
                       String texture, boolean syncopation, String lengthOfPiece,
                       String structure, String noteVelocity, double avgDistance,
                       String avgObjectSizeRelativeToImage,String dynamics) {

        test = true;

        this.densityDense = densityDense; //true is dense, false sparse

        //avg object size relative to image, are these small objects of big
        this.avgSizeBig = avgSizeBig; //small, medium, large


        //musical elements computation
        //tempo will be a range not just static values
        this.tempo = tempo;
        setTempo(tempo);
        //figure out what different elements are going to do to the composition
        //factors - type of objects, location, colors, moods, space size, dynamic variation,
        //chromaticism? should it follow the maj/minor convention
        this.chromatic = chromatic;
        //few or many notes
        this.noteSpaces = noteSpaces;
        //length of notes, short abrupt notes, long sustained notes, mixed
        this.noteLength = noteLength;
        //polyphony vs homophony or monophony VS //melody and accompaniment //number of instruments
        this.texture = texture;
         //syncopation - on the off beat or not
        this.syncopation = syncopation;
        //length of piece
        this.length = lengthOfPiece;
        //structure
        this.structure = structure;
        //velocity of note
        this.noteVelocity = noteVelocity;

        this.avgDistance = avgDistance;

        this.avgObjectSizeRelativeToImage =avgObjectSizeRelativeToImage;

        this.dynamics =dynamics; //dynamic variation

        if (tonality){
            Tonality = "major";
        }else {
            Tonality = "minor";
        }
        dyamicsLevel = "loud";
        major = tonality;
        song = AlgorithmicPiece1(mood, tonality, tempo, scene);

    }

    public ArrayList<ArrayList> AlgorithmicPiece1(Boolean[] mood, boolean tonality, String tempo, String scene) {

        //note values //note,velocity,length 1 is semibreve ,0.5 minum, 0.25 crotecht, 0.125 quaver
        //note, vel, len


        if (!test) {
            //non music computation
            //avg object density - variation of proximity of objects between, avg distance between objects
            densityDense = imageAnalysis.avgObjectDensity(); //true is dense, false sparse

            //avg object size relative to image, are these small objects of big
            avgSizeBig = imageAnalysis.avgObjectSizeRelativeToImage(); //small, medium, large
            avgObjectSizeRelativeToImage =imageAnalysis.avgObjectSizeRelativeToImage();

            //musical elements computation
            //tempo will be a range not just static values
            this.tempo = tempo;
            setTempo(tempo);
            //figure out what different elements are going to do to the composition
            //factors - type of objects, location, colors, moods, space size, dynamic variation,
            //chromaticism? should it follow the maj/minor convention
            chromatic = chromaticism();
            //few or many notes
            noteSpaces = noteSpaces();
            //length of notes, short abrupt notes, long sustained notes, mixed
            noteLength = noteLength();
            //polyphony vs homophony or monophony VS //melody and accompaniment //number of instruments
            texture = getTexture();
            //syncopation - on the off beat or not
            syncopation = syncopation();
            //length of piece
            length = lengthOfPiece();
            //structure
            structure = structure();
            //velocity of note
            noteVelocity = noteVelocity();

             dynamics = imageAnalysis.getDynamics()[1];


            Tonality = imageAnalysis.getTonality() ;
            dyamicsLevel = imageAnalysis.getDynamics()[0];

            if (Tonality.equals("major")){
                major = true;
            }else {
                major = false;
            }
        }

        //compose
        //create and choose instruments based on texture
        ArrayList<ArrayList> songArrayList = new ArrayList<>();
        System.out.println("----------Composition-----------");

        switch (length) { //length of a given section
            case "short":
                beats = 32 + 1;
                break;
            case "long":
                beats = 64 + 1;
                break;
            case "random":
                Random random = new Random();
                beats = ((random.nextInt(3) + 2) * 16) + 1;
                break;
        }

        switch (texture) {
            case "mono":
                ComposerObject melody1 = new ComposerObject(this, leadIns, "lead", null, null);
                songArrayList.add(melody1.get());
                System.out.println("Texture: " + texture);
                beats = melody1.get().size() + (melody1.sections * 4) - 2;
                System.out.println("Piece Length: " + beats);
                leadIns.setMasterVolume(0.5);
                break;
            case "homo": //two melodies following same stepwise movement
                melody1 = new ComposerObject(this, leadIns, "lead", null, null);
                ComposerObject melody2 = new ComposerObject(this, chordIns, "lead", melody1, melody1);
                songArrayList.add(melody1.get());
                songArrayList.add(melody2.get());
                chordIns.setMasterVolume(0.25);
                System.out.println("Texture: " + texture);
                beats = melody1.get().size() + (melody1.sections * 4) - 2;
                System.out.println("Piece Length: " + beats);
                break;
            case "melacc": ///melody and accompaniment
                melody1 = new ComposerObject(this, leadIns, "lead", null, null); //melody
                melody2 = new ComposerObject(this, chordIns, "chords", null, melody1); //chords
                ComposerObject bass = new ComposerObject( this, bassIns, "bass", null, melody1); //bass
                songArrayList.add(melody1.get());
                songArrayList.add(bass.get());
                songArrayList.add(melody2.get());

                System.out.println("Lead Size: " + melody1.get().size());
                System.out.println("Bass Size: " + bass.get().size());
                System.out.println("Chords Size: " + melody2.get().size());


                System.out.println("Texture: " + texture);
                beats = melody1.get().size() + (melody1.sections * 4) - 2;
                System.out.println("Piece Length: " + beats);
                break;
            case "poly":
                melody1 = new ComposerObject(this, leadIns, "lead", null, null);
                melody2 = new ComposerObject(this, leadIns, "lead", null, melody1);
                bass = new ComposerObject(this, bassIns, "bass", null, melody1);
                ComposerObject melody3 = new ComposerObject(this, leadIns, "role", null, melody1);
                songArrayList.add(melody1.get());
                songArrayList.add(bass.get());
                songArrayList.add(melody2.get());
                songArrayList.add(melody3.get());

                System.out.println("Texture: " + texture);

                beats = melody1.get().size() + (melody1.sections * 4) - 2;
                System.out.println("Piece Length: " + beats);
                break;
        }


        //    String [][][]song  = {melody,bass,melody2};
        System.out.println("------Musical Elements------ ");
        System.out.println("Texture " + texture);
        System.out.println("Tempo " + tempo);
        System.out.println("Tonality " + tonality);
        System.out.println("Note Lengths " + noteLength);
        System.out.println("Object Size " + avgObjectSizeRelativeToImage);
        System.out.println("Object Density " + densityDense);
        System.out.println("Note Spaces " + noteSpaces);
        System.out.println("Note Velocity " + noteVelocity + " Dynamic Variation: "+dynamics);
        System.out.println("Chromatic " + chromatic);
        System.out.println("Syncopation " + syncopation);
        System.out.println("Piece Length " + length + " Beats: " + (beats));
        System.out.println("Piece Structure " + structure);
        System.out.println("Adding " + songArrayList.size() + " Instrument(s)");

        if (dynamics.equals("true")) {
            dynamics = "Variation";
        } else {
            dynamics = "No Variation";
        }

        try {

        //update UI
        if (test) {
            Compositions.getInfoLabel().setText("<html>Tempo: " + tempo + "" +
                    "<br>Tonality: " + Tonality + " Dynamics: " + dyamicsLevel + " (" + dynamics + ")" +
                    "<br>Instruments: " + songArrayList.size() + "</html>");

            Compositions.getDomColor().setText("Dominant Color: " + Compositions.getMaxObjColor().getName());

            Compositions.getMoodLabel().setText("<html>Nature: " + Compositions.isNatureMood() + " - Indoor: " + Compositions.isIndooreMood() + " - Crowded: " + Compositions.isCrowdeMood()
                    + "<br>Dark: " + Compositions.isDarkeMood() + " - Traffic: " + Compositions.isTrafficeMood()
                    + "<br>Ambiance: " + Compositions.isAmbianteMood() + " - Small Space: " + Compositions.isSmallSpaceeMood());

            if (Compositions.getObjectsFoundFromDetections().size() == 0) {
                Compositions.getDomColor().setText("Dominant Object Color: No Objects");
            } else {
                Compositions.getDomColor().setText("Dominant Object Color: " +  Compositions.getMaxObjColor().getName());
            }
        }else {


            imageAnalysis.infoLabel.setText("<html>Tempo: " + tempo + "" +
                    "<br>Tonality: " + Tonality + " Dynamics: " + dyamicsLevel + " (" + dynamics + ")" +
                    "<br>Instruments: " + songArrayList.size() + "</html>");

            imageAnalysis.domColor.setText("Dominant Color: " + imageAnalysis.maxColor.getName());

            imageAnalysis.moodLabel.setText("<html>Nature: " + imageAnalysis.natureMood + " - Indoor: " + imageAnalysis.indooreMood + " - Crowded: " + imageAnalysis.crowdeMood
                    + "<br>Dark: " + imageAnalysis.darkeMood + " - Traffic: " + imageAnalysis.trafficeMood
                    + "<br>Ambiance: " + imageAnalysis.ambianteMood + " - Small Space: " + imageAnalysis.smallSpaceeMood);

            if (imageAnalysis.ObjectsFoundFromDetections.size() == 0) {
                imageAnalysis.domObjColor.setText("Dominant Object Color: no objects");
            } else {
                imageAnalysis.domObjColor.setText("Dominant Object Color: " + imageAnalysis.maxObjColor.getName());
            }
        }

        }catch (Exception e){

        }
        System.out.println("-----End of Composer -------");
        return songArrayList;
    }

    private String noteVelocity() {

        //loud quiet random
        //how long is the piece, i.e number of bars
        int loudPoints = 0;
        int quietPoints = 0;
        int randomPoints = 0;
        //scene will determine note length

        //relative size
        if (avgSizeBig.equals("large")) {
            loudPoints = loudPoints + 3;

        } else if (avgSizeBig.equals("medium")) {
            randomPoints = loudPoints + 1;
        } else {
            //small
            quietPoints = loudPoints + 3;
        }

        //note length based off of objects detected, scene
        if (imageAnalysis.natureMood) { // nature theme
            quietPoints = quietPoints + 2;
        }
        // System.out.println("TEST: "+indoorMood);
        if (imageAnalysis.indooreMood) { // indoor theme
            quietPoints = quietPoints + 2;
            randomPoints = randomPoints + 1;

        } else { //outdoor
            randomPoints = randomPoints + 1;
            loudPoints = loudPoints + 1;
        }
        if (imageAnalysis.crowdeMood) { // crowded theme
            loudPoints = loudPoints + 2;
        }
        if (imageAnalysis.smallSpaceeMood) { // low reverb theme
            quietPoints = quietPoints + 1;
        } else { //play high reverb sounds - large space
            loudPoints = loudPoints + 1;
        }
        if (imageAnalysis.ambianteMood) { // ambient theme
            quietPoints = quietPoints + 1;
        }

        //if objects are close together more loud
        if (densityDense){
            loudPoints = loudPoints +2;
        }else{
            quietPoints = quietPoints+2;
        }



        //calculate largest texture points
        HashMap<String, Integer> categories = new HashMap<String, Integer>();

        System.out.println("---- Note Velocity----");
        categories.put("quiet", (quietPoints +imageAnalysis.quietPoints)/2);
        categories.put("loud", (loudPoints+imageAnalysis.loudPoints)/2);
        categories.put("random", randomPoints);

        System.out.println("Quiet Points: " + quietPoints);
        System.out.println("Loud Points: " + loudPoints);
        System.out.println("Random Points: " + randomPoints);

        return largestCategory(categories);
    }

    private boolean chromaticism() {

        //whether our piece will follow major minor convention or be chromatic
        //based on activity of picture
        int chromaticPoints = 0;
        int nonChromaticPoints = 0;


        if (imageAnalysis.natureMood) { // nature theme
            nonChromaticPoints = nonChromaticPoints + 1;
        }
        // System.out.println("TEST: "+indoorMood);
        if (imageAnalysis.indooreMood) { // indoor theme
            nonChromaticPoints = nonChromaticPoints + 1;

        } else { //outdoor
            nonChromaticPoints = nonChromaticPoints + 1;
        }
        if (imageAnalysis.crowdeMood) { // crowded theme
            chromaticPoints = chromaticPoints + 1;
        }
        if (imageAnalysis.smallSpaceeMood) { // low reverb theme
            nonChromaticPoints = nonChromaticPoints + 1;
        } else { //play high reverb sounds - large space
            nonChromaticPoints = nonChromaticPoints + 1;
        }
        if (imageAnalysis.ambianteMood) { // ambient theme
            nonChromaticPoints = nonChromaticPoints + 2;

        }

        //calculate largest texture points
        HashMap<String, Integer> categories = new HashMap<String, Integer>();

        System.out.println("---- Chromaticsm----");
        categories.put("non-chromatic", nonChromaticPoints);
        categories.put("chromatic", chromaticPoints);

        System.out.println("Chromatic Notes Points: " + chromaticPoints);
        System.out.println("Non-Chromatic Notes Points: " + nonChromaticPoints);

        return largestCategory(categories).equals("chromatic");
    }



    private String lengthOfPiece() {

        //how long is the piece, i.e number of bars
        int shortPoints = 0;
        int longPoints = 0;
        int randomPoints = 0;
        //scene will determine note length

        //note length based off of objects detected, scene
        if (imageAnalysis.natureMood) { // nature theme
            randomPoints = randomPoints + 1;
        }
        // System.out.println("TEST: "+indoorMood);
        if (imageAnalysis.indooreMood) { // indoor theme
            longPoints = longPoints + 1;

        } else { //outdoor
            randomPoints = randomPoints + 1;
        }
        if (imageAnalysis.crowdeMood) { // crowded theme
            randomPoints = randomPoints + 1;
        }
        if (imageAnalysis.smallSpaceeMood) { // low reverb theme
            shortPoints = shortPoints + 1;
        } else { //play high reverb sounds - large space
            longPoints = longPoints + 1;
        }
        if (imageAnalysis.ambianteMood) { // ambient theme
            longPoints = longPoints + 2;
        }

        //calculate largest texture points
        HashMap<String, Integer> categories = new HashMap<String, Integer>();

        System.out.println("---- Piece Length----");
        categories.put("long", longPoints);
        categories.put("short", shortPoints);
        categories.put("random", randomPoints);

        System.out.println("Short Points: " + shortPoints);
        System.out.println("Long Points: " + longPoints);
        System.out.println("Random Points: " + randomPoints);

        return largestCategory(categories);
    }

    private String structure() {

        //what form is the piece going to follow
        //binary - AB - two distinct sectiona
        //ternary - ABA - three sections, one unique, one return
        //rondo  - ABACADA , returning section
        //no structure

        int ternaryPoints = 0;
        int binaryPoints = 0;
        int rondoPoints = 0;
        int randomPoints = 0;
        //scene will determine note length

        //note length based off of objects detected, scene
        if (imageAnalysis.natureMood) { // nature theme
            ternaryPoints = ternaryPoints + 2;
        }
        // System.out.println("TEST: "+indoorMood);
        if (imageAnalysis.indooreMood) { // indoor theme
            rondoPoints = rondoPoints + 2;

        } else { //outdoor
            binaryPoints = binaryPoints + 2;
        }
        if (imageAnalysis.crowdeMood) { // crowded theme
            randomPoints = randomPoints + 1;
        }
        if (imageAnalysis.smallSpaceeMood) { // low reverb theme
            ternaryPoints = ternaryPoints + 2;
        } else { //play high reverb sounds - large space
            binaryPoints = binaryPoints + 2;
        }
        if (imageAnalysis.ambianteMood) { // ambient theme
            rondoPoints = rondoPoints + 2;
        }

        //calculate largest texture points
        HashMap<String, Integer> categories = new HashMap<String, Integer>();

        System.out.println("---- Note Lengths----");
        categories.put("binary", binaryPoints);
        categories.put("ternary", ternaryPoints);
        categories.put("rondo", rondoPoints);
        categories.put("random", randomPoints);

        System.out.println("Binary Points: " + binaryPoints);
        System.out.println("Ternary Points: " + ternaryPoints);
        System.out.println("Rondo Points: " + rondoPoints);
        System.out.println("Random Points: " + randomPoints);

        return largestCategory(categories);
    }

    private String noteSpaces() {
        //wide , thin, or random
        //amount of space between subsequent notes
        int widePoints = 0;
        int thinPoints = 0;
        int randomPoints = 0;

        if (imageAnalysis.natureMood) { // nature theme
            randomPoints = randomPoints + 1;
        }
        // System.out.println("TEST: "+indoorMood);
        if (imageAnalysis.indooreMood) { // indoor theme
            thinPoints = thinPoints + 1;

        } else { //outdoor
            randomPoints = randomPoints + 1;
        }
        if (imageAnalysis.crowdeMood) { // crowded theme
            randomPoints = randomPoints + 1;
        }
        if (imageAnalysis.smallSpaceeMood) { // low reverb theme
            thinPoints = thinPoints + 1;
        } else { //play high reverb sounds - large space
            widePoints = widePoints + 1;
        }
        if (imageAnalysis.ambianteMood) { // ambient theme
            widePoints = widePoints + 2;
        }

        //object density
        if (densityDense) {
            //dense
            thinPoints = thinPoints + 2;
        } else {
            //sparse
            widePoints = widePoints + 2;
        }

        //calculate largest texture points
        HashMap<String, Integer> categories = new HashMap<String, Integer>();

        System.out.println("---- Note Spaces----");
        categories.put("wide", widePoints);
        categories.put("thin", thinPoints);
        categories.put("random", randomPoints);

        System.out.println("Wide Space Points: " + widePoints);
        System.out.println("Thin Space Points: " + thinPoints);
        System.out.println("Random Space Points: " + randomPoints);

        return largestCategory(categories);
    }

    private String noteLength() {
        //short, long, random
        int shortPoints = 0;
        int longPoints = 0;
        int randomPoints = 0;
        //scene will determine note length

        //note length based off of objects detected, scene
        if (imageAnalysis.natureMood) { // nature theme
            randomPoints = randomPoints + 1;
        }
        // System.out.println("TEST: "+indoorMood);
        if (imageAnalysis.indooreMood) { // indoor theme
            longPoints = longPoints + 1;

        } else { //outdoor
            randomPoints = randomPoints + 1;
        }
        if (imageAnalysis.crowdeMood) { // crowded theme
            randomPoints = randomPoints + 1;
        }
        if (imageAnalysis.smallSpaceeMood) { // low reverb theme
            shortPoints = shortPoints + 1;
        } else { //play high reverb sounds - large space
            longPoints = longPoints + 1;
        }
        if (imageAnalysis.ambianteMood) { // ambient theme
            longPoints = longPoints + 2;

        }

        //calculate largest texture points
        HashMap<String, Integer> categories = new HashMap<String, Integer>();

        System.out.println("---- Note Lengths----");
        categories.put("long", longPoints);
        categories.put("short", shortPoints);
        categories.put("random", randomPoints);

        System.out.println("Short Points: " + shortPoints);
        System.out.println("Long Points: " + longPoints);
        System.out.println("Random Points: " + randomPoints);

        return largestCategory(categories);
    }

    private String getTexture() {
        //decide how thick the texture will be based on the analysis -
        //monophonic - single instrument
        //homophonic - multiple instruments in the same key, chords
        //melody and accompaniment - soloist and accomapiment
        //polyphonic - multiple dominant melodies

        int monoPoints = 0;
        int polyPoints = 0;
        int homoPoints = 0;
        int melaccPoints = 0;

        //texture based of number of objects
        if (imageAnalysis.ObjectsFoundFromDetections.size() == 1) {
            monoPoints = monoPoints + 2;
        }

        if (imageAnalysis.ObjectsFoundFromDetections.size() == 2  ){
            System.out.println("there is two objects");
            homoPoints = homoPoints + 5;        }

        if ( imageAnalysis.ObjectsFoundFromDetections.size() == 3) {
            melaccPoints = melaccPoints + 3;
        }
        if (imageAnalysis.ObjectsFoundFromDetections.size() > 3) {
            polyPoints = polyPoints + 2;
        }
        //texture based of objects detected, scene
        if (imageAnalysis.natureMood) { // nature theme
            polyPoints = polyPoints + 1;
        }
        // System.out.println("TEST: "+indoorMood);
        if (imageAnalysis.indooreMood) { // indoor theme
            homoPoints = homoPoints + 1;
            melaccPoints = melaccPoints + 1;
        } else { //outdoor
            polyPoints = polyPoints + 1;
        }
        if (imageAnalysis.crowdeMood) { // crowded theme
            polyPoints = polyPoints + 1;
        }
        if (imageAnalysis.smallSpaceeMood) { // low reverb theme
            melaccPoints = melaccPoints + 1;
            monoPoints = monoPoints + 1;
        } else { //play high reverb sounds - large space
            melaccPoints = melaccPoints + 1;
        }
        if (imageAnalysis.ambianteMood) { // ambient theme
            melaccPoints = melaccPoints + 1;
            polyPoints = polyPoints + 1;
        }
        System.out.println("---- Texture----");
        //calculate largest texture points
        HashMap<String, Integer> categories = new HashMap<String, Integer>();
        categories.put("mono", monoPoints);
        categories.put("homo", homoPoints);
        categories.put("poly", polyPoints);
        categories.put("melacc", melaccPoints);

        System.out.println("Mono Points: " + monoPoints);
        System.out.println("Homo Points: " + homoPoints);
        System.out.println("Poly Points: " + polyPoints);
        System.out.println("Melacc Points: " + melaccPoints);

        return largestCategory(categories);
    }

    private boolean syncopation() {

        //decide factor that will determine whether it is syncopated or not
        //whether our piece will follow major minor convention or be chromiatic
        //based on activity of picture
        int syncopationPoints = 0;
        int nonSyncopationPoints = 0;

        if (imageAnalysis.natureMood) { // nature theme
            nonSyncopationPoints = nonSyncopationPoints + 1;
        }
        // System.out.println("TEST: "+indoorMood);
        if (imageAnalysis.indooreMood) { // indoor theme
            nonSyncopationPoints = nonSyncopationPoints + 1;

        } else { //outdoor
            nonSyncopationPoints = nonSyncopationPoints + 1;
        }
        if (imageAnalysis.crowdeMood) { // crowded theme
            syncopationPoints = nonSyncopationPoints + 1;
        }
        if (imageAnalysis.smallSpaceeMood) { // low reverb theme
            nonSyncopationPoints = nonSyncopationPoints + 1;
        } else { //play high reverb sounds - large space
            nonSyncopationPoints = nonSyncopationPoints + 1;
        }
        if (imageAnalysis.ambianteMood) { // ambient theme
            nonSyncopationPoints = nonSyncopationPoints + 2;
        }

        //calculate largest texture points
        HashMap<String, Integer> categories = new HashMap<String, Integer>();

        System.out.println("---- Chromaticsm----");
        categories.put("non-Syncopation", nonSyncopationPoints);
        categories.put("syncopation", syncopationPoints);

        System.out.println("Syncopation  Points: " + syncopationPoints);
        System.out.println("Non-Syncopation  Points: " + nonSyncopationPoints);

        return largestCategory(categories).equals("syncopation");
    }

    private void setTempo(String tempo) {
        Random random = new Random();
        switch (tempo) {
            case "fast":
                //max 300 min 200
                int min = 200;
                timeInterval = random.nextInt(100) + min;
                System.out.println("Tempo: " + timeInterval + "  " + tempo);
                break;
            case "normal":
                min = 350; //120 bpm
                //max 550 min 250
                timeInterval = random.nextInt(200) + min;
                System.out.println("Tempo: " + timeInterval + "  " + tempo);
                break;
            case "slow":
                min = 700;
                //max 900 min 700
                timeInterval = random.nextInt(200) + min;
                System.out.println("Tempo: " + timeInterval + "  " + tempo);
                break;
        }

    }

    public String largestCategory(HashMap<String, Integer> categories) {
        double max = 0;
        String maxCat = "none";
        for (String cat : categories.keySet()) {
            String current = cat;
            double value = categories.get(cat);
            if (max < value) {
                max = value;
                maxCat = current;
            } else {

            }
        }


        return maxCat;
    }


    public void play() {


        if(!test) {
            player.playPiece(leadIns, bassIns, sawIns, chordIns, song, new Timer(), timeInterval, begin, beats, noteToFreq, imageAnalysis.getMood(), imageAnalysis.javaPath, imageAnalysis.getDynamics(),test,imageAnalysis,this);
        }else {

            player.playPiece(leadIns, bassIns, sawIns, chordIns, song, new Timer(), timeInterval, begin, beats, noteToFreq, Compositions.getMood(), Compositions.getJavaPath(),  Compositions.getDynamics(),test, null,this);

        }
    }

    public void stop() {
        try {
            player.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
