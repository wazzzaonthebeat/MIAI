package apps.wazzzainvst.miai.objects;
/**
 * Class object responsible for converting note frequencies to actual music notes in music theory
 */
import java.util.HashMap;

public class NoteToFreq {
     HashMap<String,Double> noteToFreq = new HashMap<>();

    public HashMap<String, Double> getNotesChromatic() {
        return noteToFreqChromatic;
    }

    HashMap<String,Double> noteToFreqChromatic = new HashMap<>();

    public NoteToFreq(){



        //chromatic
        noteToFreqChromatic.put("rest",0.0);
        noteToFreqChromatic.put("c1",32.70);
        noteToFreqChromatic.put("c#1",34.65);
        noteToFreqChromatic.put("d1",36.71);
        noteToFreqChromatic.put("d#1",38.89);
        noteToFreqChromatic.put("e1",41.20);
        noteToFreqChromatic.put("f1",43.65);
        noteToFreqChromatic.put("f#1",46.25);
        noteToFreqChromatic.put("g1",49.00);
        noteToFreqChromatic.put("g#1",51.91);
        noteToFreqChromatic.put("a1",55.00);
        noteToFreqChromatic.put("a#1",58.27);
        noteToFreqChromatic.put("b1",61.74);

        noteToFreqChromatic.put("c2",65.41);
        noteToFreqChromatic.put("c#2",69.30);
        noteToFreqChromatic.put("d2",73.42);
        noteToFreqChromatic.put("d#2",77.78);
        noteToFreqChromatic.put("e2",82.41);
        noteToFreqChromatic.put("f2",87.31);
        noteToFreqChromatic.put("f#2",92.50);
        noteToFreqChromatic.put("g2",98.00);
        noteToFreqChromatic.put("g#2",103.83);
        noteToFreqChromatic.put("a2",110.00);
        noteToFreqChromatic.put("a#2",116.54);
        noteToFreqChromatic.put("b2",123.47);

        noteToFreqChromatic.put("c3",130.82);
        noteToFreqChromatic.put("c#3",138.59);
        noteToFreqChromatic.put("d3",146.83);
        noteToFreqChromatic.put("d#3",155.56);
        noteToFreqChromatic.put("e3",164.81);
        noteToFreqChromatic.put("f3",174.61);
        noteToFreqChromatic.put("f#3",185.00);
        noteToFreqChromatic.put("g3",196.0);
        noteToFreqChromatic.put("g#3",207.65);
        noteToFreqChromatic.put("a3",220.00);
        noteToFreqChromatic.put("a#3",233.08);
        noteToFreqChromatic.put("b3",246.94);

        noteToFreqChromatic.put("c4",261.63); //middle c
        noteToFreqChromatic.put("c#4",277.18);
        noteToFreqChromatic.put("d4",293.66);
        noteToFreqChromatic.put("d#4",311.13);
        noteToFreqChromatic.put("e4",329.63);
        noteToFreqChromatic.put("f4",349.23);
        noteToFreqChromatic.put("f#4",369.99);
        noteToFreqChromatic.put("g4",392.0);
        noteToFreqChromatic.put("g#4",415.30);
        noteToFreqChromatic.put("a4",440.00);
        noteToFreqChromatic.put("a#4",466.16);
        noteToFreqChromatic.put("b4",493.88);

        noteToFreqChromatic.put("c5",523.25);
        noteToFreqChromatic.put("c#5",554.37);
        noteToFreqChromatic.put("d5",587.33);
        noteToFreqChromatic.put("d#5",622.25);
        noteToFreqChromatic.put("e5",659.26);
        noteToFreqChromatic.put("f5",698.46);
        noteToFreqChromatic.put("f#5",739.99);
        noteToFreqChromatic.put("g5",783.99);
        noteToFreqChromatic.put("g#5",830.61);
        noteToFreqChromatic.put("a5",880.00);
        noteToFreqChromatic.put("a#5",932.33);
        noteToFreqChromatic.put("b5",987.77);

        noteToFreqChromatic.put("c6",1047.00);
        noteToFreqChromatic.put("c#6",1108.73);
    }

    public HashMap getNotes(){
        return noteToFreqChromatic;
    }
}
