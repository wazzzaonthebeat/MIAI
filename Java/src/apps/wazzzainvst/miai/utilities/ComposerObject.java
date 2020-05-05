package apps.wazzzainvst.miai.utilities;

import apps.wazzzainvst.miai.objects.Instrument;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.abs;

/**
 * Class for Object responsible for individual instrument compositions
 */
public class ComposerObject {
    public ArrayList<String[]> get() {
        return melody;
    }


    ArrayList<String[]> melody;
    Composer composer;
    int sections;
    public ComposerObject(Composer composer, Instrument ins, String role, ComposerObject harmonicPartner, ComposerObject melody1) {
        this.composer = composer;

        //structure
        String structure = composer.structure;
         sections = 0;
        switch (structure) {
            case "binary":
                sections = 2;
                break; //AB
            case "rondo":
                sections = 5;
                break; //ABACA
            case "ternary": //ABA
                sections = 3
                ;
                break; //AABACA
            case "random":
                Random random = new Random();
                sections = random.nextInt(4) + 1;
                break; //do whatever
        }
        melody = new ArrayList<>();

        //make fast tempo x4 as long because they are two short
        if (composer.tempo .equals("fast")){
            if (composer.texture.equals("melacc")){
                composer.beats = (composer.beats* 4)+2;
            }else{
                composer.beats = composer.beats* 4;
            }

        }

        //This section controls the structure of the
        switch (structure) {
            case "binary"://AB

                for (int ii = 0; ii < sections; ii++) {
                    //add a 4 beat rest at start of each new section
                    if (ii != 0){ //but not the first

                        this.melody.add(sectionBreak(0,ins));
                        this.melody.add(sectionBreak(0,ins));
                        this.melody.add(sectionBreak(0,ins));
                        this.melody.add(sectionBreak(0,ins));
                    }
                    for (int i = 0; i < composer.beats - 1; i++) {

                        if (role.equals("lead")) { //lead `1`
                            //make fewer notes if fast
                            String[] note = playLeadNote(i,ins);
                            if (ii == 0) {
                            //    System.out.println("Lead Section A: Beat " + (i + 1) + " note:" + note[0]);
                            } else {
                              //  System.out.println("Lead Section B: Beat " + (i + 1) + " note:" + note[0]);
                            }
                            this.melody.add(note); //note length
                        }


                        if (role.equals("chords")) { //chords

                            //if playing harmony, needs to play what melody is playing
                            String[] note = playChord(i,ins,melody1);

                            if (ii == 0) {
                              //  System.out.println("Chords Section A: Beat " + (i + 1) + " note:" + note[0]);
                            } else {
                              //  System.out.println("Chords Section B: Beat " + (i + 1) + " note:" + note[0]);
                            }

                            this.melody.add(note); //note length
                        }

                        if (role.equals("bass")) { //bass

                            String[] note =playBassNote(i,ins,melody1);

                            if (ii == 0) {
                             //   System.out.println("Bass Section A: Beat " + (i + 1) + " note:" + note[0]);
                            } else {
                             //   System.out.println("Bass Section B: Beat " + (i + 1) + " note:" + note[0]);
                            }

                            this.melody.add(note); //note length
                        }

                        if (harmonicPartner != null) { //create harmonization

                            String[] note = playHarmony(i,ins);

                            if (ii == 0) {
                             //   System.out.println("Harmony Section A: Beat " + (i + 1) + " note:" + note[0]);
                            } else {
                             //   System.out.println("Harmony Section B: Beat " + (i + 1) + " note:" + note[0]);
                            }

                            this.melody.add(note); //note length
                        }
                    }
                }
                break;
            case "rondo": //ABACA

                //A is the repeating section so we need to memorise it
                ArrayList<String[]> repeatingSection = new ArrayList();
                for (int ii = 0; ii < sections; ii++) {

                    //add a 4 beat rest at start of each new section
                    if (ii != 0){ //but not the first
                        this.melody.add(sectionBreak(0,ins));
                        this.melody.add(sectionBreak(0,ins));
                        this.melody.add(sectionBreak(0,ins));
                        this.melody.add(sectionBreak(0,ins));
                    }
                    for (int i = 0; i < composer.beats - 1; i++) {
                        if (role.equals("lead")) { //lead

                            //create new note
                            String[] note =  playLeadNote(i,ins);

                            if (ii == 0) {
                             //   System.out.println("Lead Section A: Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(note); //note length
                                repeatingSection.add(note);
                            } else if (ii == 1) {
                             //   System.out.println("Lead Section B: Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(note); //note length
                            } else if (ii == 2) {
                                //retrieve note from first section a
                             //   System.out.println("Lead Section A'': Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(repeatingSection.get(i)); //note length
                            } else if (ii == 3) {
                             //   System.out.println("Lead Section C: Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(note); //note length
                            } else {
                                //retrieve note from first section a
                              //  System.out.println("Lead Section A''': Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(repeatingSection.get(i)); //note length
                            }

                        }

                        if (role.equals("chords")) { //chords
                            melody = new ArrayList<>();
                            //create new note
                            String[] note = playChord(i,ins, melody1);

                            if (ii == 0) {
                            //    System.out.println("Chords: Section A: Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(note); //note length
                                repeatingSection.add(note);
                            } else if (ii == 1) {
                             //   System.out.println("Chords: Section B: Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(note); //note length
                            } else if (ii == 2) {
                                //retrieve note from first section a
                            //    System.out.println("Chords: Section A'': Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(repeatingSection.get(i)); //note length
                            } else if (ii == 3) {
                             //   System.out.println("Chords: Section C: Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(note); //note length
                            } else {
                                //retrieve note from first section a
                            //    System.out.println("Chords: Section A''': Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(repeatingSection.get(i)); //note length
                            }
                        }

                        if (role.equals("bass")) { //bass

                            //create new note
                            String[] note = playBassNote(i,ins, melody1);

                            if (ii == 0) {
                             //   System.out.println("Bass: Section A: Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(note); //note length
                                repeatingSection.add(note);
                            } else if (ii == 1) {
                             //   System.out.println("Bass: Section B: Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(note); //note length
                            } else if (ii == 2) {
                                //retrieve note from first section a
                              //  System.out.println("Bass: Section A'': Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(repeatingSection.get(i)); //note length
                            } else if (ii == 3) {
                           //     System.out.println("Bass: Section C: Beat " + (i + 1) + " note:" + note[0]);
                               this.melody.add(note); //note length
                            } else {
                                //retrieve note from first section a
                             //   System.out.println("Bass: Section A''': Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(repeatingSection.get(i)); //note length
                            }

                        }

                        if (harmonicPartner != null) { //create harmonization


                            //create new note
                            String[] note = playHarmony(i,ins);

                            if (ii == 0) {
                           //     System.out.println("Harmony: Section A: Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(note); //note length
                                repeatingSection.add(note);
                            } else if (ii == 1) {
                           //     System.out.println("Harmony: Section B: Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(note); //note length
                            } else if (ii == 2) {
                                //retrieve note from first section a
                              //  System.out.println("Harmony: Section A'': Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(repeatingSection.get(i)); //note length
                            } else if (ii == 3) {
                              //  System.out.println("Harmony: Section C: Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(note); //note length
                            } else {
                                //retrieve note from first section a
                             //   System.out.println("Harmony: Section A''': Beat " + (i + 1) + " note:" + note[0]);
                                this.melody.add(repeatingSection.get(i)); //note length
                            }
                        }

                    }
                }


                break;
            case "ternary"://ABA
                for (int ii = 0; ii < sections; ii++) {
                    //add a 4 beat rest at start of each new section
                    if (ii != 0){ //but not the first

                        this.melody.add(sectionBreak(0,ins));
                        this.melody.add(sectionBreak(0,ins));
                        this.melody.add(sectionBreak(0,ins));
                        this.melody.add(sectionBreak(0,ins));
                    }
                    for (int i = 0; i < composer.beats - 1; i++) {

                        if (role.equals("lead")) { //lead

                            String[] note = playLeadNote(i,ins);
                            if (ii == 0) {
                        //        System.out.println("Lead Section A: Beat " + (i + 1));
                            } else if (ii == 1) {
                          //      System.out.println("Lead Section B: Beat " + (i + 1));
                            } else {
                          //      System.out.println("Lead Section A': Beat " + (i + 1));
                            }

                            this.melody.add(note);

                        }


                        if (role.equals("chords")) { //chords
                            melody = new ArrayList<>();
                            //create new note
                            String[] note = playChord(i,ins, melody1); //note length
                            if (ii == 0) {
                        //        System.out.println("Chords Section A: Beat " + (i + 1));
                            } else if (ii == 1) {
                          //      System.out.println("Chords Section B: Beat " + (i + 1));
                            } else {
                          //      System.out.println("Chords Section A': Beat " + (i + 1));
                            }

                            this.melody.add(note);
                        }

                        if (role.equals("bass")) { //bass

                            //create new note
                            String[] note = playBassNote(i,ins, melody1); //note length
                            if (ii == 0) {
                          //      System.out.println("Bass Section A: Beat " + (i + 1));
                            } else if (ii == 1) {
                          //      System.out.println("Bass Section B: Beat " + (i + 1));
                            } else {
                          //      System.out.println("Bass Section A': Beat " + (i + 1));
                            }
                            this.melody.add(note);
                        }

                        if (harmonicPartner != null) { //create harmonization
                            //create new note
                            String[] note = playHarmony(i,ins); //note length
                            if (ii == 0) {
                       //         System.out.println("Harmony Section A: Beat " + (i + 1));
                            } else if (ii == 1) {
                        //        System.out.println("Harmony Section B: Beat " + (i + 1));
                            } else {
                         //       System.out.println("Harmony Section A': Beat " + (i + 1));
                            }

                            this.melody.add(note);
                        }
                    }
                }

                break;
            case "random":
                for (int i = 0; i < composer.beats * 3; i++) {

                    if (role.equals("lead")) { //lead

                //        System.out.println("Lead Beat " + (i + 1));
                        this.melody.add(playLeadNote(i,ins)); //note length
                    }

                    if (role.equals("chords")) { //chords
                        melody = new ArrayList<>();
                        //create new note
                 //       System.out.println("Chords Beat " + (i + 1));
                        this.melody.add(playChord(i,ins, melody1)); //note length

                    }

                    if (role.equals("bass")) { //bass

                        //create new note
                 //       System.out.println("Bass Beat " + (i + 1));
                        this.melody.add(playBassNote(i,ins, melody1)); //note length
                    }

                    if (harmonicPartner != null) { //create harmonization


                        //create new note
                //        System.out.println("Harmony Beat " + (i + 1));
                        this.melody.add(playHarmony(i,ins)); //note length
                    }
                }
                break; //do whatever
        }
    }

    private String[] playLeadNote(int i, Instrument ins) {
    //    System.out.println("chromatic "+ composer.chromatic );
        if((composer.texture.equals("melacc") || composer.texture.equals("poly")) && composer.syncopation) {
            i = i+1;
        }
            if (composer.tempo.equals("fast")){


            if (i+1 == 1 || i % 2 == 0 || i ==composer.beats - 2){
                return new String[]{
                        randomNotePicker((i == 0), (i == composer.beats - 2), (i == composer.beats - 3), ins, composer.chromatic, false, i) //note
                        , RandomNoteVelocity(ins) //velocity
                        , NoteLength(composer.noteLength)};
            }else {
                try {
                    return melody.get(i - 1); //repeat the last note
                }catch (Exception e){
                    return new String[]{
                            randomNotePicker((i == 0), (i == composer.beats - 2), (i == composer.beats - 3), ins, composer.chromatic, false, i) //note
                            , RandomNoteVelocity(ins) //velocity
                            , NoteLength(composer.noteLength)};

                }
            }
        }else {
            return new String[]{
                    randomNotePicker((i == 0), (i == composer.beats - 2), (i == composer.beats - 3), ins, composer.chromatic, false, i) //note
                    , RandomNoteVelocity(ins) //velocity
                    , NoteLength(composer.noteLength)};
        }
    }
    private String[] playBassNote(int i, Instrument ins, ComposerObject melody1) {

        System.out.println("debug: "+melody1.melody.size() + " index: "+i);
        if ( i >= melody1.melody.size()-1){
            //do not do anything

            return  null;
        }
         if (composer.texture.equals("poly")) {
                if (composer.tempo.equals("fast")) {

                    if (i + 1 == 1 || i % 2 == 0 || i == composer.beats - 2) {
                        return new String[]{
                                randomNotePicker((i == 0), (i == composer.beats - 2), (i == composer.beats - 3), ins, composer.chromatic, false, i) //note
                                , RandomNoteVelocity(ins) //velocity
                                , NoteLength(composer.noteLength)};
                    } else {
                        return melody.get(i - 1); //repeat the last note
                    }
                } else {
                    return new String[]{
                            randomNotePicker((i == 0), (i == composer.beats - 2), (i == composer.beats - 3), ins, composer.chromatic, false, i) //note
                            , RandomNoteVelocity(ins) //velocity
                            , NoteLength(composer.noteLength)};
                }
        } else {
            //must harmonize with melody

             if ((i% 4 == 0 || i ==composer.beats - 2 )) { ///play only on down beat
                 if (melody1.melody.get(i)[0].equals("")) {
                     //play a chord if lead played a rest
                     //      System.out.println("lead played: rest " + melody1.melody.get(i)[0]);
                     return new String[]{
                             randomNotePicker((i == 0), (i == composer.beats - 2), (i == composer.beats - 3), ins, composer.chromatic, false, i)//note from melody
                             , RandomNoteVelocity(ins) //velocity
                             , NoteLength(composer.noteLength)};
                 }

                 //  System.out.println("inherited straight form melody");
                 //check if its in the bass range
                 if (!melody1.melody.get(i)[0].equals("rest")){
                     return new String[]{
                             correctRange(ins.getLowerLimit(), ins.getUpperLimit(), melody1.melody.get(i)[0]) //note from melody
                             , RandomNoteVelocity(ins) //velocity
                             , NoteLength(composer.noteLength)};
                }else {
                return null;
             }
             }else {

                     try {
                         return melody.get(i - 1); //repeat the last note
                     } catch (Exception e) {
                         return new String[]{
                                 randomNotePicker((i == 0), (i == composer.beats - 2), (i == composer.beats - 3), ins, composer.chromatic, false, i) //note
                                 , RandomNoteVelocity(ins) //velocity
                                 , NoteLength(composer.noteLength)};

                     }
                 }

       }
    }
    private String[] playChord(int i, Instrument ins, ComposerObject melody1) {

        //syncopatino
        if((composer.texture.equals("melacc") || composer.texture.equals("poly")) && composer.syncopation) {
            i = i+1;
            System.out.println("syncopation");
        }
        //at start of bar

        if ((i% 4 == 0 || i ==composer.beats - 2) && i < melody1.melody.size()-1){


                if (melody1.melody.get(i)[0].equals("")) {
                    //play a chord if lead played a rest
                    // System.out.println("lead played: rest "+ melody1.melody.get(i)[0]);
                    return new String[]{
                            randomNotePicker((i == 0), (i == composer.beats - 2), (i == composer.beats - 3), ins, composer.chromatic, false, i)//note from melody
                            , RandomNoteVelocity(ins) //velocity
                            , NoteLength(composer.noteLength)};
                }

        //    System.out.println("inherited straight form melody");
            //check if its in the bass range


            return new String[]{
                    melody1.melody.get(i)[0] //note from melody
                    , RandomNoteVelocity(ins) //velocity
                    , NoteLength(composer.noteLength)};


        }else {

            if (i < melody1.melody.size() - 1) { //if we are beyond the end of the bar
                return null;
            } else {
                try {
                    return melody.get(i - 1); //repeat the last note
                } catch (Exception e) {
                    return new String[]{
                            randomNotePicker((i == 0), (i == composer.beats - 2), (i == composer.beats - 3), ins, composer.chromatic, false, i) //note
                            , RandomNoteVelocity(ins) //velocity
                            , NoteLength(composer.noteLength)};

                }
            }
        }

    }

    private String correctRange(String lowerLimit, String upperLimit, String note) {
      //  System.out.println("upper limit: "+upperLimit+ " lower limit:"+lowerLimit + " note: "+note);
        int lower =Integer.parseInt(lowerLimit);
        int upper =Integer.parseInt(upperLimit);
        int octave= Integer.parseInt(note.substring(1));
        if (octave >= lower && octave < upper){
         //   System.out.println("in range");
            return note;
        }else {
           // System.out.println("out of range");
            int range  = upper -lower;
            Random random = new Random();
            String newOctave = String.valueOf(lower + random.nextInt(range + 1));
           // System.out.println(note.substring(0,1) + " new "+newOctave);
            return note.substring(0,1)+newOctave;
        }
    }

    private String[] playHarmony(int i, Instrument ins) {
            return new String[]{
                    randomNotePicker((i == 0), (i == composer.beats - 2), (i == composer.beats - 3), ins, composer.chromatic, false, i) //note
                    , RandomNoteVelocity(ins) //velocity
                    , NoteLength(composer.noteLength)};
    }

    private String[] sectionBreak(int i, Instrument ins){
        return new String[]{
                randomNotePicker((i == 0), (i == composer.beats - 1), (i == composer.beats - 2), ins, composer.chromatic, true, i) //note
                , "0" //velocity
                , "0"};
    }

    public String NoteLength(String length) {


        Random random = new Random();
        double lengthInt = 0;
        double noteLength = random.nextFloat();
        if (length.equals("short")) {
            noteLength = 0.1; //quarter of a crotchet
     //          System.out.println("NOTE LENGTH: "+length + " "+noteLength);
            return String.valueOf(noteLength);
        } else if (length.equals("long")) {
            noteLength = 1; //crotecht, quaver: 0.5 , semi quaver : 0.25
      //      System.out.println("NOTE LENGTH: "+length + " "+noteLength);
            return String.valueOf(noteLength);
        } else { //random length
            switch (random.nextInt(3)) {
                case 0: //crotchet
                    noteLength = 1;
                    break;
                case 1: //quaver
                    noteLength = 0.5;
                    break;
                case 2: //semi quaver
                    noteLength = 0.1;
                    break;
            }

      //        System.out.println("NOTE LENGTH: "+length + " "+noteLength);
            return String.valueOf(noteLength);
        }
    }

    public String RandomNoteVelocity(Instrument ins) {

            if (ins == null) {
         //       System.out.println("ins is null");
                return "0";
            } else {
                if (composer.dynamics.equals("true")) { //dynamic variation

                    if ("loud".equals(composer.noteVelocity)) {
                        Random random = new Random();
                        double velocity = Double.parseDouble(String.valueOf(random.nextInt(70))) / 100;

              //          System.out.println("Loud Dynamic Velocity: " + velocity);
                        return String.valueOf(velocity);
                    }
                    Random random;
                    double velocity;
                    random = new Random();
                    velocity = Double.parseDouble(String.valueOf(random.nextInt(20))) / 100;


             //       System.out.println("Quiet Dynamic Velocity: " + String.valueOf(velocity));
                    return String.valueOf(velocity);


                }else{
                    if ("loud".equals(composer.noteVelocity)) {
                ///        System.out.println("Loud Velocity: " + String.valueOf(1));
                        return String.valueOf(0.6);
                    }
                   // System.out.println("Quiet Velocity: " + String.valueOf(0.55));
                    return String.valueOf(0.2);
                }
            }

    }

    public double rangeCalculator(Double colorValue, double upper, double lower, String type) {


        // algorithm sourced from https://stackoverflow.com/questions/5294955/how-to-scale-down-a-range-of-numbers-with-a-known-min-and-max-value
        double b = lower;
        double a = upper;

        double max = 255;
        double min = 0;

        double scaled = ((b - a) * (colorValue - min)) / (max - min) + a;

        //    System.out.println(type+": scaled: " + colorValue + " to " + scaled);
        return scaled;
    }

    public String randomNotePicker(boolean first, boolean last, boolean secondLast, Instrument instrument, boolean chromatic, boolean restNote, int i) {
        ArrayList<String> notes;
        Random random = new Random();


        if (!restNote) {
            //is this chromatic or not
            int number = random.nextInt(37);
            int interval = 0;
            String note = "";
            if (!chromatic) {


                String maj = "c";
                String majDom = "c";


                if (composer.major) {
                    maj = "c";
                    majDom = "g";
                } else {
                    maj = "a";
                    majDom = "e";
                }


                //needs to be of significant intevalic distance
                if (first) {
                    notes = instrument.customRange(false);
                    int range = Integer.valueOf(instrument.getUpperLimit()) - Integer.valueOf(instrument.getLowerLimit());
                    //octave picker
                    composer.octave = Integer.valueOf(instrument.getLowerLimit()) + random.nextInt(range + 1);
                    //System.out.println("TEST "+(Integer.valueOf(instrument.getLowerLimit())+range));
                    //    System.out.println("ins: "+instrument.getName()+" range: "+range+" octave(s) starting octave: "+octave);

                    //octave  = 4;
                    //first note must be tonic
             //          System.out.println("First note: ");
                    note = maj + composer.octave; //c major
                    //    prev = note;
                }

                //second last note must be dominant
                if (secondLast) {
                    //second last note must be dominant
                    // System.out.println("Second Last note: ");
                    note = majDom + composer.octave;

                }


                //last note must be tonic
                if (last) {
                    //last note must be tonic
                     //    System.out.println("Last note: ");
                    note = maj + composer.octave; //c major

                }

                boolean restBool = false;
                //pick a note that is either a major
                if (!last && !first && !secondLast) {

                    //probability of a rest must be higher if  we have composer.notSpaces == wide
                    int rest = random.nextInt(4);
                    if (composer.noteSpaces.equals("wide")){
                     //   System.out.println("wide note spaces");
                        if (rest == 0 || rest == 2 ||rest == 3) {
                            //rest
                            restBool = true;
                            //      System.out.println("rest");
                        }
                    }


                    ;
                    if (instrument.getName().equals("bass")) {
                        rest = random.nextInt(1);

                    }

                     if (((rest == 0 || rest == 2) && (composer.noteSpaces.equals("random") || composer.noteSpaces.equals("wide")) && !instrument.getName().equals("bass")) ) {
                        //rest
                      //  System.out.println("thin note spaces");
                        restBool = true;
                        //      System.out.println("rest");
                    } else if (random.nextBoolean()) {
                        //rest
                      //   System.out.println("random note spaces");
                        restBool = true;
                        //      System.out.println("rest");
                    } else {

                        try {
                            //pick a number between 1 and 8
                            interval = (random.nextInt(4) + 2) % instrument.customRange(false).size();

                            //upward or downward movement
                            int upOrDown = random.nextInt(2);
                            //System.out.println("previous: "+instrument.customRange().get(instrument.customRange().indexOf(prev))+" index: "+instrument.customRange().indexOf(prev)+ " interval: "+interval);

                            switch (upOrDown) {
                                case 0:
                                    note = instrument.customRange(false).get((abs(instrument.customRange(false).indexOf(composer.prev) + interval)) % instrument.customRange(false).size());
//                            System.out.println("note to play: "+note);

                                    break;//System.out.println("up");break;
                                case 1:
                                    note = instrument.customRange(false).get(abs((instrument.customRange(false).indexOf(composer.prev) - interval)) % instrument.customRange(false).size());//System.out.println("down");break;
                                    //                          System.out.println("note to play: "+note);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                       //     System.out.println("note to play: " + note);

                            throw e;
                            //note = maj + octave;
                        }
                    }
                }
                //save value of this note
                if (!restBool) {
                    composer.prev = note;
                    // System.out.println("Note: " +note);
                }

                return note;
            } else {

                System.out.println("chromatic note");
                boolean restBool = false;
                //chromaitc note - upward or downward semitonal movement
                //probability of a rest must be higher if  we have composer.notSpaces == wide
                int rest = random.nextInt(4);
                if (composer.noteSpaces.equals("wide")){
                   // System.out.println("wide note spaces");
                    if (rest == 0 || rest == 2 ||rest == 3) {
                        //rest
                        restBool = true;
                        //      System.out.println("rest");
                    }
                }


                if ((rest == 0 || rest == 2) && (composer.noteSpaces.equals("random") || composer.noteSpaces.equals("wide")) ) {
                    //rest
                   // System.out.println("thin note spaces");
                    restBool = true;
                    //      System.out.println("rest");
                } else if (random.nextBoolean()) {
                    //rest
                    //System.out.println("random note spaces");
                    restBool = true;
                    //      System.out.println("rest");
                } else {

                    try {
                        //pick a number between 1 and 8
                        interval = (random.nextInt(4) + 2) % instrument.customRangeChr(true).size();

                        try {
              //              System.out.println("Interval Selected "+interval + " previous note: "+composer.prev +
              //                      " options: upper: index: "+instrument.customRangeChr(true).indexOf(composer.prev) +" "+instrument.customRangeChr(true).get(instrument.customRangeChr(true).indexOf(composer.prev)+interval));
                        }catch (Exception e) {
                           }

                        try {
               //             System.out.println("Interval Selected " + interval + " previous note: " + composer.prev +
               //                     " options: "+
                //                    " lower: index: "+instrument.customRangeChr(true).indexOf(composer.prev) +" "+ instrument.customRangeChr(true).get(instrument.customRangeChr(true).indexOf(composer.prev) - interval));

                        }catch (Exception e) {
                        }


                //        System.out.println("Checking notes: "+instrument.customRangeChr(true).get(interval) );



                       //upward or downward movement
                        int upOrDown = random.nextInt(2);
                        try {
                  //          System.out.println("previous: "+instrument.customRange(true).get(instrument.customRange(true).indexOf(composer.prev))+" index: "+instrument.customRange(true).indexOf(composer.prev)+ " interval: "+interval);

                        }catch (Exception e){

                        }

                        switch (upOrDown) {
                            case 0:
                                note = instrument.customRangeChr(true).get((abs(instrument.customRangeChr(true).indexOf(composer.prev) + interval)) % instrument.customRangeChr(true).size());
                        //        System.out.println("note to play: "+note);

                                break;//System.out.println("up");break;
                            case 1:
                                note = instrument.customRangeChr(true).get(abs((instrument.customRangeChr(true).indexOf(composer.prev) - interval)) % instrument.customRangeChr(true).size());//System.out.println("down");break;
                   //                                       System.out.println("note to play: "+note);

                        }

                    } catch (Exception e) {
                      System.out.println(e);

                        throw e;
                        //note = maj + octave;
                    }
                }
             //  System.out.println("note to play: "+note);
                //save value of this note
                if (!restBool) {
                    composer.prev = note;
                    // System.out.println("Note: " +note);
                }
                return note;
            }
        }

        return "rest";
    }

}
