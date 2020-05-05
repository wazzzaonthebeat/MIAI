package apps.wazzzainvst.miai.windows;

import apps.wazzzainvst.miai.objects.Instrument;
import apps.wazzzainvst.miai.objects.MyColor;
import apps.wazzzainvst.miai.objects.Theme;
import apps.wazzzainvst.miai.utilities.Composer;
import apps.wazzzainvst.miai.utilities.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Timer;

public class Compositions {
    private static Composer composer;
    private static int timeInterval = 500;
    private static int beats = 32 + 1;
    private static Player player;

    //musical elements
    static boolean densityDense;
    static String avgSizeBig;
    static boolean chromatic;
    static String noteSpaces;
    static String noteLength;
    static  String texture;
    static String tempo;
    static boolean syncopation;
    static String lengthOfPiece;
    static String structure;



    public static boolean tonality;
    private static String dynamics;
    public static ArrayList<String[]> getObjectsFoundFromDetections() {
        return ObjectsFoundFromDetections;
    }

    public static void setObjectsFoundFromDetections(ArrayList<String[]> objectFound) {
        ObjectsFoundFromDetections = objectFound;
    }

    public static ArrayList<String[]> ObjectsFoundFromDetections;
    public static String getAvgObjectSizeRelativeToImage() {
        return avgObjectSizeRelativeToImage;
    }

    public static void setAvgObjectSizeRelativeToImage(String avgObjectSizeRelativeToImage) {
        Compositions.avgObjectSizeRelativeToImage = avgObjectSizeRelativeToImage;
    }

    private static String avgObjectSizeRelativeToImage;

    public static String getNoteVelocity() {
        return noteVelocity;
    }

    public static void setNoteVelocity(String noteVelocity) {
        Compositions.noteVelocity = noteVelocity;
    }

    public static MyColor getMaxObjColor() {
        return maxObjColor;
    }

    public static void setMaxObjColor(MyColor maxObjColor) {
        Compositions.maxObjColor = maxObjColor;
    }

    static MyColor maxObjColor;
    static String noteVelocity;

    public static Boolean[] getMood() {
        return mood;
    }

    public static void setMood(Boolean[] mood) {
        Compositions.mood = mood;
    }

    static Boolean[] mood;
    static String scene;
    private static JFrame myJFrame;
    static int index = 0;
    public static String projectPath;
    public static String Path;

    public static JLabel getInfoLabel() {
        return infoLabel;
    }

    public static void setInfoLabel(JLabel infoLabel) {
        Compositions.infoLabel = infoLabel;
    }

    private static JLabel infoLabel;

    public static JLabel getMoodLabel() {
        return moodLabel;
    }

    public static void setMoodLabel(JLabel moodLabel) {
        Compositions.moodLabel = moodLabel;
    }

    private static JLabel moodLabel;

    public static JLabel getDomColor() {
        return domColor;
    }

    public static void setDomColor(JLabel domColor) {
        Compositions.domColor = domColor;
    }

    public static int getTimeInterval() {
        return timeInterval;
    }

    public static void setTimeInterval(int timeInterval) {
        Compositions.timeInterval = timeInterval;
    }

    private static JLabel domColor;

    public static JLabel getDomObjColor() {
        return domObjColor;
    }

    public static void setDomObjColor(JLabel domObjColor) {
        Compositions.domObjColor = domObjColor;
    }

    private static JLabel domObjColor;

    public static double getAvgDistance() {
        return avgDistance;
    }

    public static void setAvgDistance(double avgDistance) {
        Compositions.avgDistance = avgDistance;
    }

    private static double avgDistance;

    public static boolean isNatureMood() {
        return natureMood;
    }

    public static void setNatureMood(boolean natureMood) {
        Compositions.natureMood = natureMood;
    }

    static boolean natureMood;  //animals, trees

    public static boolean isIndooreMood() {
        return indooreMood;
    }

    public static void setIndooreMood(boolean indooreMood) {
        Compositions.indooreMood = indooreMood;
    }

    static boolean indooreMood; //inside a building

    public static boolean isCrowdeMood() {
        return crowdeMood;
    }

    public static void setCrowdeMood(boolean crowdeMood) {
        Compositions.crowdeMood = crowdeMood;
    }

    static boolean crowdeMood ; //are we in a crowd of people

    public static boolean isWatereMood() {
        return watereMood;
    }

    public static void setWatereMood(boolean watereMood) {
        Compositions.watereMood = watereMood;
    }

    static  boolean watereMood ; //are we near water

    public static boolean isDarkeMood() {
        return darkeMood;
    }

    public static void setDarkeMood(boolean darkeMood) {
        Compositions.darkeMood = darkeMood;
    }

    static boolean darkeMood ; //are we in a dark place

    public static boolean isTrafficeMood() {
        return trafficeMood;
    }

    public static void setTrafficeMood(boolean trafficeMood) {
        Compositions.trafficeMood = trafficeMood;
    }

    static  boolean trafficeMood ; //are we in traffic

    public static boolean isAmbianteMood() {
        return ambianteMood;
    }

    public static void setAmbianteMood(boolean ambianteMood) {
        Compositions.ambianteMood = ambianteMood;
    }

    public static int getBeats() {
        return beats;
    }

    public static void setBeats(int beats) {
        Compositions.beats = beats;
    }

    public static Player getPlayer() {
        return player;
    }

    public static void setPlayer(Player player) {
        Compositions.player = player;
    }

    static boolean ambianteMood ; //are we a quiet ambiant space

    public static boolean isSmallSpaceeMood() {
        return smallSpaceeMood;
    }

    public static void setSmallSpaceeMood(boolean smallSpaceeMood) {
        Compositions.smallSpaceeMood = smallSpaceeMood;
    }

    static  boolean smallSpaceeMood ;//are we in a small space

    public static Composer getComposer() {
        return composer;
    }

    public static void setComposer(Composer composer) {
        Compositions.composer = composer;
    }

    private static String javaPath;
    static String  [] pieceDynamics;
    public static String[] getDynamics(){
        return pieceDynamics;
    }
    private static Instrument lead;
    private static Instrument bass;
    private static Instrument saw ;
    private static Instrument chord;
    private static int begin = 0;
    private static  Timer timer;
    public static void main(String[] args){
        File syeDir = new File(System.getProperty("user.dir"));

        Path  = new File(syeDir.getParent()).getAbsolutePath()+"\\";
        projectPath = Path + "Python\\ImageAI\\";
        javaPath = Path + "Java\\";
        createUI();
         timer = new Timer();

        //initialize player
        player = new Player();
         begin = 0;
         lead = new Instrument("lead", "6", "4", 0.7, 0);
         bass = new Instrument("bass", "3", "3", 0.4, 0);
         saw = new Instrument("saw", "4", "3", 0, 0);
         chord = new Instrument("chord", "5", "4", 0.1, 0);

        tonality = true;
        //set defaults
        densityDense = false; //true is dense, false sparse

        //avg object size relative to image, are these small objects of big
        avgSizeBig = "medium"; //small, medium, large


        //musical elements computation
        //tempo will be a range not just static values
        tempo = "normal";

        //figure out what different elements are going to do to the composition
        //factors - type of objects, location, colors, moods, space size, dynamic variation,
        //chromaticism? should it follow the maj/minor convention
        chromatic = false;
        //few or many notes
        noteSpaces ="wide";
        //length of notes, short abrupt notes, long sustained notes, mixed
        noteLength = "long";
        //polyphony vs homophony or monophony VS //melody and accompaniment //number of instruments
        texture = "mono";
        //syncopation - on the off beat or not
        syncopation = false;
        //length of piece
        lengthOfPiece = "long";
        //structure---------------------------
        structure = "binary";
        //velocity of note
        noteVelocity = "loud";

         natureMood = false;  //animals, trees
         indooreMood = false; //inside a building
         crowdeMood = false; //are we in a crowd of people
         watereMood = false; //are we near water
         darkeMood = false; //are we in a dark place
         trafficeMood = false; //are we in traffic
         ambianteMood = false; //are we a quiet ambiant space
         smallSpaceeMood = false;//are we in a small space

        setMaxObjColor(new MyColor("red",0.0));
        ObjectsFoundFromDetections = new ArrayList<String[]>();
        //define objects

        setObjectsFoundFromDetections(ObjectsFoundFromDetections);

    mood = new Boolean[] {natureMood,indooreMood,crowdeMood,watereMood,darkeMood,trafficeMood,ambianteMood,smallSpaceeMood};


        scene = scene;
        avgDistance = 0;

        avgObjectSizeRelativeToImage = "small";

        dynamics = "true";
        pieceDynamics = new String[]{"loud",dynamics};
        composer = new Composer(lead, bass, saw,chord, timer, begin, timeInterval, beats, null, player);
        composer.tester(mood, tonality,  tempo,
                 scene, densityDense, avgSizeBig,
         chromatic, noteSpaces, noteLength,
                 texture, syncopation,  lengthOfPiece,
                 structure,  noteVelocity,avgDistance,avgObjectSizeRelativeToImage,dynamics);


    }

    private static void createUI() {

        myJFrame = new JFrame("Musical Image AI - Composition Center");
        myJFrame.setSize(800, 850);

        JPanel jPanel = new JPanel();
        //draw toolbar
        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");
        JButton replayButton = new JButton("<html>Replay<br>Piece");
        JButton viewObjectsButton = new JButton("<html>View<br>Objects");
        JButton viewDataButton = new JButton("<html>View<br>Data");
        JLabel label = new JLabel("Structure: ", SwingConstants.LEFT);
        JPanel imageSelectorPanel = new JPanel();

        JComboBox selectorStructure = new JComboBox(new String [] {"binary","rondo","ternary","random"});

        JLabel labelTempo = new JLabel("Tempo: ", SwingConstants.LEFT);
        JComboBox selectorTempo = new JComboBox(new String [] {"normal","fast","slow"});
        JPanel tempoSelectorPanel = new JPanel();
        tempoSelectorPanel.setLayout(new BoxLayout(tempoSelectorPanel, BoxLayout.X_AXIS));
        tempoSelectorPanel.add(labelTempo);
        tempoSelectorPanel.add(selectorTempo);
        selectorTempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    tempo = (String) selectorTempo.getSelectedItem();

            }
        });

        JLabel labelTonality = new JLabel("Tonality: ", SwingConstants.LEFT);
        JComboBox selectorTonality = new JComboBox(new String [] {"major","minor","chromatic"});
        JPanel tonalitySelectorPanel = new JPanel();
        tonalitySelectorPanel.setLayout(new BoxLayout(tonalitySelectorPanel, BoxLayout.X_AXIS));
        tonalitySelectorPanel.add(labelTonality);
        tonalitySelectorPanel.add(selectorTonality);
        selectorTonality.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (selectorTonality.getSelectedItem().equals("chromatic")){
                    chromatic = true;

                }else {
                    chromatic = false;
                    if (selectorTonality.getSelectedItem().equals("major")){
                        tonality = true;

                    }else{
                        tonality = false;
                    }
        }
                }
        });

        JLabel labelObjDensity = new JLabel("Object Density: ", SwingConstants.LEFT);
        JComboBox selectorObjDensity  = new JComboBox(new String [] {"true","false"});
        JPanel ObjDensitySelectorPanel = new JPanel();
        ObjDensitySelectorPanel.setLayout(new BoxLayout(ObjDensitySelectorPanel, BoxLayout.X_AXIS));
        ObjDensitySelectorPanel.add(labelObjDensity);
        ObjDensitySelectorPanel.add(selectorObjDensity);
        selectorObjDensity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                densityDense = (Boolean) selectorObjDensity.getSelectedItem();
            }
        });

        JLabel labelObjSize = new JLabel("Object Size: ", SwingConstants.LEFT);
        JComboBox selectorObjSize= new JComboBox(new String [] {"small","big","medium"});
        JPanel ObjSizeSelectorPanel = new JPanel();
        ObjSizeSelectorPanel.setLayout(new BoxLayout(ObjSizeSelectorPanel, BoxLayout.X_AXIS));
        ObjSizeSelectorPanel.add(labelObjSize);
        ObjSizeSelectorPanel.add(selectorObjSize);
        selectorObjSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                avgObjectSizeRelativeToImage = (String) selectorObjSize.getSelectedItem();
            }
        });

        JLabel labelNoteLength = new JLabel("Note Length: ", SwingConstants.LEFT);
        JComboBox selectorNoteLength = new JComboBox(new String [] {"long","short","random"});
        JPanel NoteLengthSelectorPanel = new JPanel();
        NoteLengthSelectorPanel.setLayout(new BoxLayout(NoteLengthSelectorPanel, BoxLayout.X_AXIS));
        NoteLengthSelectorPanel.add(labelNoteLength);
        NoteLengthSelectorPanel.add(selectorNoteLength);
        selectorNoteLength.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                noteLength = (String) selectorNoteLength.getSelectedItem();
            }
        });

        JLabel labelNoteSpaces = new JLabel("Note Spaces: ", SwingConstants.LEFT);
        JComboBox selectorNoteSpaces = new JComboBox(new String [] {"wide","thin","random"});
        JPanel NoteSpacesSelectorPanel = new JPanel();
        NoteSpacesSelectorPanel.setLayout(new BoxLayout(NoteSpacesSelectorPanel, BoxLayout.X_AXIS));
        NoteSpacesSelectorPanel.add(labelNoteSpaces);
        NoteSpacesSelectorPanel.add(selectorNoteSpaces);
        selectorNoteSpaces.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                noteSpaces = (String) selectorNoteSpaces.getSelectedItem();
            }
        });

        JLabel labelNoteVelcity = new JLabel("Note Velocity: ", SwingConstants.LEFT);
        JComboBox selectorNoteVelcity= new JComboBox(new String [] {"loud","quiet"});
        JPanel NoteVelcitySelectorPanel = new JPanel();
        NoteVelcitySelectorPanel.setLayout(new BoxLayout(NoteVelcitySelectorPanel, BoxLayout.X_AXIS));
        NoteVelcitySelectorPanel.add(labelNoteVelcity);
        NoteVelcitySelectorPanel.add(selectorNoteVelcity);
        selectorNoteVelcity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                noteVelocity = (String) selectorNoteVelcity.getSelectedItem();
            }
        });

        JLabel labelNoteVelcityDynamic = new JLabel("Note Velocity Dynamic Variation: ", SwingConstants.LEFT);
        JComboBox selectorNoteVelocityDynamic= new JComboBox(new String [] {"true","false"});
        JPanel NoteVelocitySelectorPanelDynamic = new JPanel();
        NoteVelocitySelectorPanelDynamic.setLayout(new BoxLayout(NoteVelocitySelectorPanelDynamic, BoxLayout.X_AXIS));
        NoteVelocitySelectorPanelDynamic.add(labelNoteVelcityDynamic);
        NoteVelocitySelectorPanelDynamic.add(selectorNoteVelocityDynamic);
        selectorNoteVelocityDynamic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dynamics = (String) selectorNoteVelocityDynamic.getSelectedItem();
            }
        });

        JLabel labelSyncopation = new JLabel("Syncopation: ", SwingConstants.LEFT);
        JComboBox selectorSyncopation= new JComboBox(new Boolean [] {false,true});
        JPanel SyncopationSelectorPanel = new JPanel();
        SyncopationSelectorPanel.setLayout(new BoxLayout(SyncopationSelectorPanel, BoxLayout.X_AXIS));
        SyncopationSelectorPanel.add(labelSyncopation);
        SyncopationSelectorPanel.add(selectorSyncopation);
        selectorSyncopation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                syncopation = (Boolean) selectorSyncopation.getSelectedItem();
            }
        });


        JLabel labelSectionLength = new JLabel("Section length: ", SwingConstants.LEFT);
        JComboBox selectorectionLength= new JComboBox(new String [] {"short","long","random"});
        JPanel sectionLengthSelectorPanel = new JPanel();
        sectionLengthSelectorPanel.setLayout(new BoxLayout(sectionLengthSelectorPanel, BoxLayout.X_AXIS));
        sectionLengthSelectorPanel.add(labelSectionLength);
        sectionLengthSelectorPanel.add(selectorectionLength);
        selectorectionLength.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lengthOfPiece = (String) selectorectionLength.getSelectedItem();
            }
        });

        JLabel labelTexture = new JLabel("Texture: ", SwingConstants.LEFT);
        JComboBox selectorTexture= new JComboBox(new String [] {"mono","homo","melacc","poly"});
        JPanel TextureSelectorPanel = new JPanel();
        TextureSelectorPanel.setLayout(new BoxLayout(TextureSelectorPanel, BoxLayout.X_AXIS));
        TextureSelectorPanel.add(labelTexture);
        TextureSelectorPanel.add(selectorTexture);
        selectorTexture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                texture = (String) selectorTexture.getSelectedItem();
            }
        });

        JPanel compositionPanel = new JPanel();
        compositionPanel.setLayout(new BoxLayout(compositionPanel, BoxLayout.Y_AXIS));

        compositionPanel.add(tempoSelectorPanel);
        compositionPanel.add(new JLabel("<html><br><br>"));

        compositionPanel.add(tonalitySelectorPanel);
        compositionPanel.add(new JLabel("<html><br><br>"));

        compositionPanel.add(ObjDensitySelectorPanel);
        compositionPanel.add(new JLabel("<html><br><br>"));

        compositionPanel.add(ObjSizeSelectorPanel);
        compositionPanel.add(new JLabel("<html><br><br>"));

        compositionPanel.add(NoteLengthSelectorPanel);
        compositionPanel.add(new JLabel("<html><br><br>"));

        compositionPanel.add(NoteVelcitySelectorPanel);
        compositionPanel.add(new JLabel("<html><br><br>"));

        compositionPanel.add(NoteVelocitySelectorPanelDynamic);
        compositionPanel.add(new JLabel("<html><br><br>"));

        compositionPanel.add(NoteSpacesSelectorPanel);
        compositionPanel.add(new JLabel("<html><br><br>"));


        compositionPanel.add(SyncopationSelectorPanel);
        compositionPanel.add(new JLabel("<html><br><br>"));

        compositionPanel.add(sectionLengthSelectorPanel);
        compositionPanel.add(new JLabel("<html><br><br>"));

        compositionPanel.add(TextureSelectorPanel);
        compositionPanel.add(new JLabel("<html><br><br>"));



        JPanel actionPanel = new JPanel();

        JPanel colorsPanel = new JPanel();
        actionPanel.setSize(myJFrame.getWidth(), 10);
        actionPanel.setSize(myJFrame.getWidth(), 10);


        JPanel statusPanel = new JPanel();
        statusPanel.setSize(myJFrame.getWidth(), 10);

        infoLabel = new JLabel("loading...", SwingConstants.LEFT);
        domColor = new JLabel("loading...", SwingConstants.LEFT);
        domObjColor = new JLabel("loading...", SwingConstants.LEFT);
        moodLabel = new JLabel("loading...", SwingConstants.LEFT);

            imageSelectorPanel.setLayout(new BoxLayout(imageSelectorPanel, BoxLayout.X_AXIS));
            imageSelectorPanel.add(label);




           // selector.setSelectedIndex(index);
            // get the selected item:
        selectorStructure.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    structure = (String) selectorStructure.getSelectedItem();
                }
            });



            playButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    play();
                }
            });
            stopButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try { //check if its playing
                        composer.stop();
                    } catch (Exception ignored) {
                    }
                }
            });

            replayButton.addActionListener(e -> {
                replay();
            });

            viewObjectsButton.addActionListener(e -> {


            });

            viewDataButton.addActionListener(e -> {


            });
            imageSelectorPanel.add(selectorStructure);
            actionPanel.add(imageSelectorPanel);
            actionPanel.add(playButton);
            actionPanel.add(stopButton);
            actionPanel.add(replayButton);
            actionPanel.add(viewObjectsButton);
            actionPanel.add(viewDataButton);
            statusPanel.add(infoLabel);

            colorsPanel.setLayout(new BoxLayout(colorsPanel,BoxLayout.Y_AXIS));
            colorsPanel.add(domColor);
            colorsPanel.add(domObjColor);

            statusPanel.add(colorsPanel);
            statusPanel.add(moodLabel);
            //jPanel.add(imageSelectorPanel);
            jPanel.add(actionPanel);
            jPanel.add(statusPanel);
            jPanel.add(compositionPanel);



        //draw image
        Border padding = BorderFactory.createEmptyBorder(5, 5, 5, 25);
        jPanel.setBorder(padding);
        JPanel main = new JPanel();
        main.setSize(myJFrame.getWidth(), myJFrame.getHeight());


        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        try {
            BufferedImage logo = ImageIO.read(new File(projectPath + "logo.png"));
            Image dimgLogo = logo.getScaledInstance(150, 50, Image.SCALE_SMOOTH);
            main.add(new JLabel (new ImageIcon(dimgLogo)));
        }catch (Exception e){

        }


        main.add(jPanel);

        //theme formatting
        Theme theme = new Theme();

        main.setBackground(theme.getMainBack());
        actionPanel.setBackground(theme.getMainBack());
        compositionPanel.setBackground(theme.getMainBack());
        statusPanel.setBackground(theme.getButtonBorderColor());
        statusPanel.setBorder(theme.getRounded());
        colorsPanel.setBackground(theme.getButtonBorderColor());
        colorsPanel.setForeground(theme.getTextColor2());

        jPanel.setBackground(theme.getMainBack());
        playButton.setBackground(theme.getButtonColor());
        playButton.setBorder(Theme.getSingleLineButton());
        playButton.setForeground(theme.getTextColor());

        stopButton.setBackground(theme.getButtonColor());
        stopButton.setBorder(Theme.getSingleLineButton());
        stopButton.setForeground(theme.getTextColor());

        viewDataButton.setBackground(theme.getButtonColor());
        viewDataButton.setBorder(Theme.getButton());
        viewDataButton.setForeground(theme.getTextColor());

        viewObjectsButton.setBackground(theme.getButtonColor());
        viewObjectsButton.setBorder(Theme.getButton());
        viewObjectsButton.setForeground(theme.getTextColor());

        replayButton.setBackground(theme.getButtonColor());
        replayButton.setBorder(Theme.getButton());
        replayButton.setForeground(theme.getTextColor());

        label.setBackground(theme.getButtonColor());
        label.setForeground(theme.getTextColor());

        selectorStructure.setBackground(theme.getButtonColor());
        selectorStructure.setForeground(theme.getTextColor());

        selectorNoteLength.setBackground(theme.getButtonColor());
        selectorNoteLength.setForeground(theme.getTextColor());

        selectorNoteVelcity.setBackground(theme.getButtonColor());
        selectorNoteVelcity.setForeground(theme.getTextColor());

        selectorObjDensity.setBackground(theme.getButtonColor());
        selectorObjDensity.setForeground(theme.getTextColor());

        selectorObjSize.setBackground(theme.getButtonColor());
        selectorObjSize.setForeground(theme.getTextColor());

        selectorSyncopation.setBackground(theme.getButtonColor());
        selectorSyncopation.setForeground(theme.getTextColor());

        selectorTempo.setBackground(theme.getButtonColor());
        selectorTempo.setForeground(theme.getTextColor());

        selectorTonality.setBackground(theme.getButtonColor());
        selectorTonality.setForeground(theme.getTextColor());


        imageSelectorPanel.setBackground(theme.getButtonColor());
        imageSelectorPanel.setBorder(Theme.getButton());
        imageSelectorPanel.setForeground(theme.getTextColor());

        infoLabel.setForeground(theme.getTextColor2());
        domColor.setForeground(theme.getTextColor2());
        domObjColor.setForeground(theme.getTextColor2());

        moodLabel.setForeground(theme.getTextColor2());

        myJFrame.setContentPane(main);
        myJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myJFrame.setVisible(true);
    }

    private  static void replay(){
        try{
            stop();
        }catch (Exception e){

        }
        composer.play();
    }
    private static void play() {
        try{
            stop();
        }catch (Exception e){

        }
        timer = new Timer();
        composer = new Composer(lead, bass, saw,chord, timer, begin, timeInterval, beats, null, player);
        composer.tester(mood, tonality,  tempo,
                scene, densityDense, avgSizeBig,
                chromatic, noteSpaces, noteLength,
                texture, syncopation,  lengthOfPiece,
                structure,  noteVelocity,avgDistance,avgObjectSizeRelativeToImage,dynamics);

        composer.play();
    }

    private static void stop() {
        composer.stop();
    }

    public static String getJavaPath() {
        return javaPath;
    }

    public static void setJavaPath(String javaPath) {
        Compositions.javaPath = javaPath;
    }
}
