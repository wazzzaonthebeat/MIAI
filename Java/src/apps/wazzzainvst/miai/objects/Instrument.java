package apps.wazzzainvst.miai.objects;

import apps.wazzzainvst.miai.utilities.Player;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.instruments.WaveShapingVoice;
import com.jsyn.scope.AudioScope;
import com.jsyn.swing.JAppletFrame;
import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.LineOut;
import com.jsyn.util.PseudoRandom;
import com.jsyn.util.VoiceAllocator;
import com.softsynth.math.AudioMath;
import com.softsynth.shared.time.TimeStamp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
/**
 * Class object responsible for representing Instruments
 */
public class Instrument {

    private Player player;
    private String upperLimit;
    private String lowerLimit;
    private boolean loud;

    public String getName() {
        return name;
    }

    private String name;
    private double masterVolume;
    private int pan;
    public ArrayList<String> notes = new ArrayList<>();
    private ArrayList<String> notesAvailable = new ArrayList<>();
    private boolean dynamicVariation = false;

    public Instrument(String name, String upperLimit, String lowerLimit, double masterVolume, int pan) {
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
        this.name = name;
        this.player = player;
        this.masterVolume = masterVolume;
        this.pan = pan;
        //initialize notes
        notes = new ArrayList<>();
        notes.add("c1");
        notes.add("c#1");
        notes.add("d1");
        notes.add("d#1");
        notes.add("e1");
        notes.add("f1");
        notes.add("f#1");
        notes.add("g1");
        notes.add("g#1");
        notes.add("a1");
        notes.add("a#1");
        notes.add("b1");
        notes.add("c2");
        notes.add("c#2");
        notes.add("d2");
        notes.add("d#2");
        notes.add("e2");
        notes.add("f2");
        notes.add("f#2");
        notes.add("g2");
        notes.add("g#2");
        notes.add("a2");
        notes.add("a#2");
        notes.add("b2");
        notes.add("c3");
        notes.add("c#3");
        notes.add("d3");
        notes.add("d#3");
        notes.add("e3");
        notes.add("f3");
        notes.add("f#3");
        notes.add("g3");
        notes.add("g#3");
        notes.add("a3");
        notes.add("a#3");
        notes.add("b3");
        notes.add("c4");
        notes.add("c#4");
        notes.add("d4");
        notes.add("d#4");
        notes.add("e4");
        notes.add("f4");
        notes.add("f#4");
        notes.add("g4");
        notes.add("g#4");
        notes.add("a4");
        notes.add("a#4");
        notes.add("b4");
        notes.add("c5");
        notes.add("c#5");
        notes.add("d5");
        notes.add("d#5");
        notes.add("e5");
        notes.add("f5");
        notes.add("f#5");
        notes.add("g5");
        notes.add("g#5");
        notes.add("a5");
        notes.add("a#5");
        notes.add("b5");
        notes.add("c6");
        notes.add("c#6");
        notes.add("d6");
        notes.add("d#6");
        notes.add("e6");
        notes.add("f6");
        notes.add("f#6");
        notes.add("g6");
        notes.add("g#6");
        notes.add("a6");
        notes.add("a#6");
        notes.add("b6");
        notes.add("c7");
        notes.add("c#7");
        notes.add("d7");
        notes.add("d#7");
        notes.add("e7");
        notes.add("f7");
        notes.add("e#7");
        notes.add("g7");
        notes.add("g#7");
        notes.add("a7");
        notes.add("a#7");
        notes.add("b7");
        notes.add("c8");
        notes.add("c#8");
        notes.add("d8");
        notes.add("d#8");
        notes.add("e8");
        notes.add("f8");
        notes.add("f#8");
        notes.add("g8");
        notes.add("g#8");
        notes.add("a8");
        notes.add("a#8");
        notes.add("b8");
        notes.add("c9");
    }

    public ArrayList<String> customRange(boolean chromatic) {


        for (int i = notes.indexOf("c" + lowerLimit); i < notes.indexOf("c" + upperLimit) + 1; i++) {
            if (!chromatic) {
                if (!notes.get(i).contains("#")){
                    notesAvailable.add(notes.get(i));
                   // System.out.println(name+": adding non chromatic: "+notes.get(i));
                }
                    //         System.out.println(name+": adding: "+notes.get(i));
            } else {
                //chromatic
           //  System.out.println(name+": adding chromatic: "+notes.get(i));
                    notesAvailable.add(notes.get(i));
            }

        }

        return notesAvailable;
    }
    public ArrayList<String> customRangeChr(boolean chromatic) {


        for (int i = notes.indexOf("c" + lowerLimit); i < notes.indexOf("c" + upperLimit) + 1; i++) {

                //chromatic
                 notesAvailable.add(notes.get(i));


        }

        return notesAvailable;
    }
    /*
    public  void notePlayer (String ins,Double freq, double vel, double len){
        player.playNote(name,freq,vel,len);
    }

     */

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public double getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(double masterVolume) {
        this.masterVolume = masterVolume;
    }

    public int getPan() {
        return pan;
    }

    public void setPan(int pan) {
        this.pan = pan;
    }

    public void setDynamicVariation(boolean b, boolean loud) {
        dynamicVariation = b;
        this.loud = loud;
    }

    public boolean[] getDynamicVariation() {
        return new boolean[]{dynamicVariation, loud};
    }

}
