package apps.wazzzainvst.miai.utilities;

import apps.wazzzainvst.miai.Main;
import apps.wazzzainvst.miai.objects.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Class responsible for image data analysis and processing
 */
public class ImageAnalysis {
    private String url;

    private ArrayList<HashMap<String, String>> imageData;

    private String tempo;
    private Boolean[] mood;
    private String scene;
    public static HashMap<String, MyObject> Objects;
    HashMap<String, String[]> tempoFactors = new HashMap<String, String[]>();

    //tempo scores
    Integer slowTempoPoints = 0;
    Integer midTempoPoints = 0;
    Integer highTempoPoints = 0;

    //determines mood
    boolean natureMood = false;  //animals, trees
    boolean indooreMood = false; //inside a building
    boolean crowdeMood = false; //are we in a crowd of people
    boolean watereMood = false; //are we near water
    boolean darkeMood = false; //are we in a dark place
    boolean trafficeMood = false; //are we in traffic
    boolean ambianteMood = false; //are we a quiet ambiant space
    boolean smallSpaceeMood = false;//are we in a small space

    //colors
    MyColor red;
    MyColor green;
    MyColor blue;
    MyColor magenta;
    MyColor cyan;
    MyColor yellow;
    MyColor black;
    MyColor white;
    //object colors
    MyColor orangeColor;
    MyColor redObjs;
    MyColor greenObjs;
    MyColor blueObjs;
    MyColor magentaObjs;
    MyColor cyanObjs;
    MyColor yellowObjs;
    MyColor blackObjs;
    MyColor whiteObjs;
    MyColor orangeObjs;

    //tonality
    double majorTonalityPoints = 0;
    double minorTonalityPoints = 0;
    double chromaticTonalityPoints = 0;
    private boolean tonality;
    private boolean chromaticTonality = false;
    JLabel infoLabel, domColor, domObjColor, moodLabel;
    HashMap<String, Integer> counter;

    Instrument insLead;
    Instrument insBass;
    Instrument insSaw;
    Player player;

    String javaPath;
    private int instrumentCount = 0;
    private String level;
    private boolean dynamicVariation;
    public ArrayList<String[]> ObjectsFoundFromDetections;
    BufferedImage myImageInput;
    String dynamics;
    MyColor maxObjColor;
    MyColor maxColor;
    int quietPoints = 0;
    //loud - loud
    int loudPoints = 0;
    private double avgDistance;
    MyCategoryObject dominantFrequencyCategoryObject; //the object category occurring the most
    MyCategoryObject dominantLargestObjectAreaCategory; //the object category with the largest area
    ArrayList<String[]> objectsForColorDetection;
    private boolean sports;

    public ImageAnalysis(String url, BufferedImage myImage, JLabel infoLabel, Instrument lead, Instrument bass, Instrument saw, Instrument chord, Player player, JLabel domColor, JLabel domObjColor, String javaPath, JLabel moodLabel) {
        this.url = url;
        this.javaPath = javaPath;
        this.infoLabel = infoLabel;
        this.insBass = bass;
        this.insLead = lead;
        this.insSaw = saw;
        this.player = player;
        this.domColor = domColor;
        this.domObjColor = domObjColor;
        this.moodLabel = moodLabel;
        this.myImageInput = myImage;
        //create objects
        Objects = new MyObjects().getObjects();

        tempo = "slow";

        //check animate/inanimate
        tempoFactors.put("immobile", new String[]{"slow"});
        tempoFactors.put("mobile", new String[]{"medium", "fast"});


        //create color objects
        red = new MyColor("red", 0.0);
        blue = new MyColor("blue", 0.0);
        green = new MyColor("green", 0.0);
        black = new MyColor("black", 0.0);
        white = new MyColor("white", 0.0);
        yellow = new MyColor("yellow", 0.0);
        cyan = new MyColor("cyan", 0.0);
        magenta = new MyColor("magenta", 0.0);
        orangeColor = new MyColor("orange", 0.0);

        redObjs = new MyColor("red", 0.0);
        blueObjs = new MyColor("blue", 0.0);
        greenObjs = new MyColor("green", 0.0);
        blackObjs = new MyColor("black", 0.0);
        whiteObjs = new MyColor("white", 0.0);
        yellowObjs = new MyColor("yellow", 0.0);
        cyanObjs = new MyColor("cyan", 0.0);
        magentaObjs = new MyColor("magenta", 0.0);
        orangeObjs = new MyColor("orange", 0.0);
    }

    public void analyse(BufferedImage myImage) throws IOException, URISyntaxException {

        //call python detector class and scene
        ObjectsFoundFromDetections = new ArrayList<String[]>();
        HashMap<String, String[]> map = new HashMap<>();

        //read file and store into array
        try {
            File myObj = new File(url);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data[] = myReader.nextLine().split(", ':', ");
                String object = data[0].replace("'", "").trim();
                String prob = data[1].replace("'", "").trim();
                String[] coordinates = data[2].replace("'", "").replace("[", "").replace("]", "").split(",");
                //    System.out.println("OBJECT FOUND: " + object.trim() + " " + prob.trim() + " where " + coordinates[0] + "," + coordinates[1] + "," + coordinates[2] + "," + coordinates[3]);
                String[] data0 = new String[]{object, prob, coordinates[0], coordinates[1], coordinates[2], coordinates[3]};
                ObjectsFoundFromDetections.add(data0);

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File has not been analyze.");
            JFrame frame = new JFrame("File not analyzed!");
            JLabel label = new JLabel("This image has not yet been analyzed! Please run the image analysis!");
            restartApplication();
            frame.setSize(500, 200);
            //  loadingFrame.add(panel);
            frame.setContentPane(label);
            frame.setVisible(true);
            ObjectsFoundFromDetections = new ArrayList<String[]>();

            //  e.printStackTrace();
        } catch (Exception e) {
            ObjectsFoundFromDetections = new ArrayList<String[]>();
        }


        //decide tempo, determined by type of objects, number of objects, color

        String highestFrequencyCat;
        String largestObjectCat;

        if (ObjectsFoundFromDetections.size() != 0) {
            highestFrequencyCat = dominantCalculator(ObjectsFoundFromDetections)[0];
            largestObjectCat = dominantCalculator(ObjectsFoundFromDetections)[1];
            dominantFrequencyCategoryObject = new MyCategoryObject(highestFrequencyCat);
            dominantLargestObjectAreaCategory = new MyCategoryObject(largestObjectCat);
            System.out.println("Dominant Frequency Category: " + highestFrequencyCat);
            System.out.println("Dominant Area Category: " + largestObjectCat);
        } else {
            dominantFrequencyCategoryObject = null;
            dominantLargestObjectAreaCategory = null;
        }

        //do the color scan
        colorScan(myImage, 0, 0, 0, 0, true, 0, "");

        //color scan all the objects
        int objIndex = 0;
        for (String[] obj : ObjectsFoundFromDetections) {

            objectsForColorDetection = new ArrayList<>();
            //filter objects found to only those in dominant categories
            String object = obj[0];
            String objCategory = Objects.get(object).getCategory();
            if (objCategory.equals("sports")) {
                sports = true;
            }
            //if the object is in a dominant frequency or largest area category send it for color detection
            if (objCategory.equals(dominantFrequencyCategoryObject.getCategory()) || objCategory.equals(dominantLargestObjectAreaCategory.getCategory())) {
                objectsForColorDetection.add(obj);
                colorScan(myImage, Integer.valueOf(obj[2].trim()), Integer.valueOf(obj[3].trim()), Integer.valueOf(obj[4].trim()), Integer.valueOf(obj[5].trim()), false, objIndex, object);
            }
            objIndex++;
        }
       /* System.out.println("red "+this.red.getValue());
        System.out.println("yellow "+this.yellow.getValue());
        System.out.println("green "+this.green.getValue());
        System.out.println("cyan "+this.cyan.getValue());
        System.out.println("blue "+this.blue.getValue());
        System.out.println("magenta "+this.magenta.getValue());
        System.out.println("black "+this.black.getValue());
        System.out.println("white "+this.white.getValue());
        System.out.println("white "+this.orangeColor.getValue());*/

        //decide tempo factors - these categories will be one factor determining speed of piece

        //how fast should the piece be// DONE!!
        tempoGenerator(myImage);

        //decide mood  //decide tonality DONE!!
        tonalityGenerator(myImage);


        //how load should the instruments be
        dynamicsGenerator(myImage);


        //mood generator - to get appropriate sound fx
        moodGenerator();
    }

    private void moodGenerator() {
        int naturePoints = 0;  //animals, trees
        int indoorPoints = 0; //inside a building
        int outdoorPoints = 0; //outside a building
        int crowdPoints = 0; //are we in a crowd of people
        int waterPoints = 0; //are we near water
        int darkPoints = 0; //are we in a dark place
        int brightPoints = 0; //are we in a bright place
        int trafficPoints = 0; //are we in traffic
        int ambiantPoints = 0; //are we a quiet ambiant space
        int smallSpacePoints = 0; //are we in a small space
        int largeSpacePoints = 0; //are we in a large space

        //decides what sound fx to play in the background base on:
        //color, full and objects
        //types of objects
        //tempo
        //tonality

        //color mood factors from whole picture
        switch (largestOfColors(true, ObjectsFoundFromDetections).getName()) {
            case "white":
                //: Major 5, Minor 0
                brightPoints = brightPoints + 5;
                break;
            case "red":
                //: Major 4, Minor 0
                brightPoints = brightPoints + 4;
                break;
            case "orange":
                //: Major 3, Minor 0
                brightPoints = brightPoints + 3;

                break;
            case "yellow":
                //: Major 2, Minor 1
                brightPoints = brightPoints + 2;
                outdoorPoints = outdoorPoints + 1;
                break;
            case "green":
                //: Major 1, Minor 1
                brightPoints = brightPoints + 1;
                darkPoints = darkPoints + 1;
                naturePoints = naturePoints + 5;
                outdoorPoints = outdoorPoints + 1;
                break;
            case "cyan":
                //: Major 2, Minor 1
                outdoorPoints = outdoorPoints + 2;
                brightPoints = brightPoints + 3;
                break;
            case "blue":
                //: Major 0, Minor 3
                outdoorPoints = outdoorPoints + 2;
                brightPoints = brightPoints + 2;
                waterPoints = waterPoints + 1;
                break;
            case "magenta":
                //: Major 0, Minor 4
                darkPoints = darkPoints + 4;
                indoorPoints = indoorPoints + 1;
                break;
            case "black":
                //: Major 0, Minor 5
                darkPoints = darkPoints + 5;
                break;
        }
        //        //should calculate mood from colors of all objects found
        if (ObjectsFoundFromDetections.size() != 0) {
            switch (largestOfColors(false, ObjectsFoundFromDetections).getName()) {
                case "white":
                    //: Major 5, Minor 0
                    brightPoints = brightPoints + 5;
                    break;
                case "red":
                    //: Major 4, Minor 0
                    brightPoints = brightPoints + 4;
                    break;
                case "orange":
                    //: Major 3, Minor 0
                    brightPoints = brightPoints + 3;

                    break;
                case "yellow":
                    //: Major 2, Minor 1
                    brightPoints = brightPoints + 2;
                    outdoorPoints = outdoorPoints + 1;
                    break;
                case "green":
                    //: Major 1, Minor 1
                    brightPoints = brightPoints + 1;
                    darkPoints = darkPoints + 1;
                    naturePoints = naturePoints + 5;
                    outdoorPoints = outdoorPoints + 1;
                    break;
                case "cyan":
                    //: Major 2, Minor 1
                    outdoorPoints = outdoorPoints + 2;
                    brightPoints = brightPoints + 3;
                    break;
                case "blue":
                    //: Major 0, Minor 3
                    outdoorPoints = outdoorPoints + 2;
                    brightPoints = brightPoints + 2;
                    waterPoints = waterPoints + 1;
                    break;
                case "magenta":
                    //: Major 0, Minor 4
                    darkPoints = darkPoints + 4;
                    indoorPoints = indoorPoints + 1;
                    break;
                case "black":
                    //: Major 0, Minor 5
                    darkPoints = darkPoints + 5;
                    break;
            }
        }

        //type of objects
        int count = ObjectsFoundFromDetections.size();
        int maj = 0;
        int min = 0;
        if (count != 0) {
            for (String[] obj : objectsForColorDetection) {
                String object = obj[0];
                String category = Objects.get(object).getCategory();
                //   System.out.println(object+" - "+speed);
                //   System.out.println("Object: "+ object + " Category: "+category);


                    ambiantPoints =ambiantPoints + objectCategoryScorer(object, category, "mood")[8];
                naturePoints =naturePoints+ objectCategoryScorer(object, category, "mood")[0];
                 indoorPoints = indoorPoints + objectCategoryScorer(object, category, "mood")[1];; //inside a building
                outdoorPoints = outdoorPoints + objectCategoryScorer(object, category, "mood")[2];
                crowdPoints = crowdPoints + objectCategoryScorer(object, category, "mood")[3];
                waterPoints = waterPoints + objectCategoryScorer(object, category, "mood")[4];
                darkPoints =  darkPoints + objectCategoryScorer(object, category, "mood")[5];
                brightPoints = brightPoints + objectCategoryScorer(object, category, "mood")[6];
                trafficPoints = trafficPoints + objectCategoryScorer(object, category, "mood")[7];
                ambiantPoints =ambiantPoints + objectCategoryScorer(object, category, "mood")[8];
                smallSpacePoints = smallSpacePoints + objectCategoryScorer(object, category, "mood")[9];
                largeSpacePoints =largeSpacePoints+ objectCategoryScorer(object, category, "mood")[10];


                count++;
            }
        } else {
            count = 1;
        }



        //number of objects
        if (ObjectsFoundFromDetections.size() == 1) {

            if (!sports) {
                ambiantPoints = ambiantPoints + 5;
            }
        }
        ///if 2 than play two
        if (ObjectsFoundFromDetections.size() == 2) {
            if (!sports) {
                ambiantPoints = ambiantPoints + 2;
            }
        }
        //if more than five
        if (ObjectsFoundFromDetections.size() > 5) {
            largeSpacePoints = largeSpacePoints + 1;
        }

        System.out.println("-----Mood Factors-----");
        if (naturePoints != 0)
            System.out.println("Nature Points: " + naturePoints);
        if (indoorPoints != 0)
            System.out.println("Indoor Points: " + indoorPoints);
        if (outdoorPoints != 0)
            System.out.println("Outdoor Points: " + outdoorPoints);
        if (crowdPoints != 0)
            System.out.println("Crowd Points: " + crowdPoints);
        if (waterPoints != 0)
            System.out.println("Water Points: " + waterPoints);
        if (darkPoints != 0)
            System.out.println("Dark Points: " + darkPoints);
        if (brightPoints != 0)
            System.out.println("Bright Points: " + brightPoints);
        if (trafficPoints != 0)
            System.out.println("Traffic Points: " + trafficPoints);
        if (ambiantPoints != 0)
            System.out.println("Ambiant Points: " + ambiantPoints);
        if (smallSpacePoints != 0)
            System.out.println("Small Space Points: " + smallSpacePoints);
        if (largeSpacePoints != 0)
            System.out.println("Large Space Points: " + largeSpacePoints);
        System.out.println("-----End of Mood Factors-----");
        System.out.println("-----Mood Results-----");

        /*
        Calculate final points
         */
        Random random = new Random();
        if (naturePoints > 0) {
            natureMood = true;

        } else {
            System.out.println("NO NATURE");

        }
        if (indoorPoints > outdoorPoints) {
            //cant be nature
            System.out.println("Where: INDOOR");
            indooreMood = true;
            watereMood = false;
        } else if (indoorPoints == outdoorPoints && indoorPoints != 0) {
            //pick one randomly
            if (random.nextBoolean()) {
                System.out.println("Where: INDOOR- Random");
                indooreMood = true;
                watereMood = false;
            } else {
                System.out.println("Where: OUTDOOR- Random");
                indooreMood = false;
                if (natureMood)
                    System.out.println("NATURE");
            }
        } else {
            System.out.println("Where: OUTDOOR");
            indooreMood = false;
            if (natureMood)
                System.out.println("NATURE");
        }

        if (crowdPoints > 5) {
            crowdeMood = true;
            System.out.println("CROWDED PLACE");
        } else {

            try {
                if (dominantFrequencyCategoryObject.isMobile() && ObjectsFoundFromDetections.size() > 5) {
                    crowdeMood = true;
                    System.out.println("CROWDED PLACE");
                } else {
                    System.out.println("NOT CROWDED PLACE");
                }
            } catch (Exception e) {

            }
        }

        if (darkPoints > brightPoints) {
            //cant be nature
            System.out.println("DARK PLACE");
            darkeMood = true;
        } else if (darkPoints == brightPoints && brightPoints != 0) {
            //pick one randomly
            if (random.nextBoolean()) {
                System.out.println("DARK PLACE - Random");
                darkeMood = true;
            } else {
                System.out.println("BRIGHT PLACE- Random");
                darkeMood = false;
            }
        } else {
            System.out.println("BRIGHT PLACE");
            darkeMood = false;
        }

        if (trafficPoints > 0) {
            System.out.println("OUTDOOR PLACE");
            indooreMood = false;
            trafficeMood = true;
        }

        if (ambiantPoints > 0) {
            System.out.println("AMBIENT ENVIRONMENT");
            ambianteMood = true;
        }

        if (smallSpacePoints > largeSpacePoints) {
            System.out.println("SMALL SPACE");
            smallSpaceeMood = true;
        } else if (smallSpacePoints == largeSpacePoints && largeSpacePoints != 0) {
            //pick one randomly
            if (random.nextBoolean()) {
                smallSpaceeMood = true;
                System.out.println("SMALL SPACE- Random");
            } else {
                System.out.println("LARGE SPACE- Random");
                smallSpaceeMood = false;
            }
        } else {
            System.out.println("LARGE SPACE");
            smallSpaceeMood = false;
        }

        System.out.println("-----End of Mood Results-----");


        mood = new Boolean[]{natureMood, indooreMood, crowdeMood, watereMood, darkeMood, trafficeMood, ambianteMood, smallSpaceeMood};
    }

    private void dynamicsGenerator(BufferedImage myImage) {
        boolean dynamicVariation = false;
        //dynamic or static var

        boolean loud = false;

        //static - same volume
        int staticPoints = 0;
        //dynamic - changing volume
        int dynamicPoints = 0;
        //soft - quiet
        quietPoints = 0;
        //loud - loud
        loudPoints = 0;

        //static or dynamic based on scene
        //if no objects were found dont check

        System.out.println("-----Dynamic Factors ------");

        //check property of each object
        //go through all the objects
        //calculate speed of all the objects found and the color
        int count = ObjectsFoundFromDetections.size();
        //If there were objects found, use them to alter the dynamics of the piece
        if (count != 0) {

            for (String[] obj : ObjectsFoundFromDetections) {
                String object = obj[0];
                String category = Objects.get(object).getCategory();
                //decide whats factors control loudness
                dynamicPoints = dynamicPoints+ objectCategoryScorer(object, category, "dynamicVariation")[0];
                staticPoints = staticPoints +objectCategoryScorer(object, category,
                        "dynamicVariation")[1];

                loudPoints = loudPoints+ objectCategoryScorer(object, category, "dynamics")[0];
                quietPoints = quietPoints +objectCategoryScorer(object, category, "dynamics")[1];

                loud = Objects.get(object).isLoud();
                if (loud) {
                    //major
                    if (Objects.get(object).isMobile()) {
                        dynamicPoints = dynamicPoints + 5;
                        loudPoints = loudPoints + 5;
                    }
                    loudPoints++;
                } else { //mid
                    if (!Objects.get(object).isMobile()) {
                        staticPoints = staticPoints + 5;
                        quietPoints = quietPoints + 5;
                    }
                    quietPoints++;
                }

            }

        } else {
            System.out.println("No objects");
            count = 1;
        }
        //get avg values
        loudPoints = loudPoints + (loudPoints / count);
        quietPoints = quietPoints + (quietPoints / count);

        //check colors
        //find most dominant color in the image
        //should scan average colors of the whole picture
        switch (largestOfColors(true, ObjectsFoundFromDetections).getName()) {
            case "white":
                //: Loud -  5
                dynamicPoints = dynamicPoints + 1;
                staticPoints = staticPoints + 5;

                loudPoints = loudPoints + 3;
                System.out.println("full image: white");
                break;
            case "red":
                //Loud -  4
                dynamicPoints = dynamicPoints + 2;
                staticPoints = staticPoints + 4;

                loudPoints = loudPoints + 3;
                System.out.println("full image: red");
                break;
            case "orange":
                //Loud -  3
                dynamicPoints = dynamicPoints + 3;
                staticPoints = staticPoints + 3;

                loudPoints = loudPoints + 2;
                System.out.println("full image: orange");
                break;
            case "yellow":
                //Loud -  2
                dynamicPoints = dynamicPoints + 4;
                staticPoints = staticPoints + 2;

                loudPoints = loudPoints + 2;
                System.out.println("full image: yellow");
                break;
            case "green":
                //Quiet – 1, Loud 1
                dynamicPoints = dynamicPoints + 5;
                staticPoints = staticPoints + 1;

                quietPoints = quietPoints + 2;
                loudPoints = loudPoints + 1;
                System.out.println("full image: green");
                break;
            case "cyan":
                //Quiet - 2
                dynamicPoints = dynamicPoints + 4;
                staticPoints = staticPoints + 2;

                quietPoints = quietPoints + 3;
                System.out.println("full image: cyan");
                break;
            case "blue":
                //Quiet - 3
                dynamicPoints = dynamicPoints + 3;
                staticPoints = staticPoints + 3;

                quietPoints = quietPoints + 4;
                System.out.println("full image: blue");
                break;
            case "magenta":
                //Quiet - 4
                dynamicPoints = dynamicPoints + 2;
                staticPoints = staticPoints + 4;

                quietPoints = quietPoints + 5;
                System.out.println("full image: magenta");
                break;
            case "black":
                //Quiet - 5
                dynamicPoints = dynamicPoints + 1;
                staticPoints = staticPoints + 5;

                quietPoints = quietPoints + 6;
                System.out.println("full image: black");
                break;
        }

        //should calculate average colors of all objects found
        if (ObjectsFoundFromDetections.size() != 0) {
            maxObjColor = largestOfColors(false, objectsForColorDetection);
            switch (maxColor.getName()) {
                case "white":
                    //: Loud -  5
                    dynamicPoints = dynamicPoints + 1;
                    staticPoints = staticPoints + 5;

                    loudPoints = loudPoints + 3;
                    System.out.println("obj image: white");
                    break;
                case "red":
                    //Loud -  4
                    dynamicPoints = dynamicPoints + 2;
                    staticPoints = staticPoints + 4;

                    loudPoints = loudPoints + 2;
                    System.out.println("obj image: red");
                    break;
                case "orange":
                    //Loud -  3
                    dynamicPoints = dynamicPoints + 3;
                    staticPoints = staticPoints + 3;

                    loudPoints = loudPoints + 2;
                    System.out.println("obj image: orange");
                    break;
                case "yellow":
                    //Loud -  2
                    dynamicPoints = dynamicPoints + 4;
                    staticPoints = staticPoints + 2;

                    loudPoints = loudPoints + 2;
                    System.out.println("obj image: yellow");
                    break;
                case "green":
                    //Quiet – 1, Loud 1
                    dynamicPoints = dynamicPoints + 5;
                    staticPoints = staticPoints + 1;

                    quietPoints = quietPoints + 2;
                    loudPoints = loudPoints + 1;
                    System.out.println("obj image: green");
                    break;
                case "cyan":
                    //Quiet - 2
                    dynamicPoints = dynamicPoints + 4;
                    staticPoints = staticPoints + 2;

                    quietPoints = quietPoints + 3;
                    System.out.println("obj image: cyan");
                    break;
                case "blue":
                    //Quiet - 3
                    dynamicPoints = dynamicPoints + 3;
                    staticPoints = staticPoints + 3;

                    quietPoints = quietPoints + 4;
                    System.out.println("obj image: blue");
                    break;
                case "magenta":
                    //Quiet - 4
                    dynamicPoints = dynamicPoints + 2;
                    staticPoints = staticPoints + 4;

                    quietPoints = quietPoints + 5;
                    System.out.println("obj image: magenta");
                    break;
                case "black":
                    //Quiet - 5
                    dynamicPoints = dynamicPoints + 1;
                    staticPoints = staticPoints + 5;

                    quietPoints = quietPoints + 6;
                    System.out.println("obj image: black");
                    break;
            }


        }
        //average the points

        if (staticPoints < dynamicPoints) {
            dynamicVariation = true;
        }

        System.out.println("Scores: l:" + loudPoints + " - q:" + quietPoints);
        Random random = new Random();
        //find largest scoring dynamic
        if (loudPoints > quietPoints) {
            if (insLead.getMasterVolume() != 0) {
                insLead.setMasterVolume(0.6);
            }
            if (insBass.getMasterVolume() != 0) {
                insBass.setMasterVolume(0.5);
            }
            if (insSaw.getMasterVolume() != 0) {
                insSaw.setMasterVolume(0.4);
            }
            //set overall volume to loud
            String dynamics;
            if (dynamicVariation) {
                dynamics = "Variation";
            } else {
                dynamics = "No Variation";
            }

            loud = true;
            setDynamics("loud", dynamicVariation);
        } else {
            setDynamics("quiet", dynamicVariation);
        }

        if (ObjectsFoundFromDetections.size() != 0 && staticPoints < dynamicPoints) {
            System.out.println("adding dynamic variation");
            insSaw.setDynamicVariation(true, loud);
            insLead.setDynamicVariation(true, loud);
            insBass.setDynamicVariation(true, loud);
            dynamicVariation = true;
            //  dynamicVariation = true;
        } else {
            System.out.println("no dynamic variation");
            insSaw.setDynamicVariation(false, loud);
            insLead.setDynamicVariation(false, loud);
            insBass.setDynamicVariation(false, loud);
            dynamicVariation = false;
        }
    }

    private void tonalityGenerator(BufferedImage myImage) {
        //initialise
        majorTonalityPoints = 0;
        minorTonalityPoints = 0;
        chromaticTonalityPoints = 0;
        //should scan the colors of the objects
        //find most dominant color in the image
        //should calculate major/minor from colors of whole pic
        System.out.println("------Tonality Analysis-----");
        System.out.println("Checking Tonality- " + largestOfColors(true, ObjectsFoundFromDetections).getName());
        //should scan average colors of the whole picture
        switch (largestOfColors(true, null).getName()) {
            case "white":
                //: Major 5, Minor 0
                majorTonalityPoints = majorTonalityPoints + 3;
                minorTonalityPoints = minorTonalityPoints + 0;
                break;
            case "red":
                //: Major 4, Minor 0
                majorTonalityPoints = majorTonalityPoints + 2;
                minorTonalityPoints = minorTonalityPoints + 1;
                break;
            case "orange":
                //: Major 3, Minor 0
                majorTonalityPoints = majorTonalityPoints + 1;
                minorTonalityPoints = minorTonalityPoints + 1;
                break;
            case "yellow":
                //: Major 2, Minor 1
                majorTonalityPoints = majorTonalityPoints + 1;
                minorTonalityPoints = minorTonalityPoints + 1;
                break;
            case "green":
                //: Major 1, Minor 1
                majorTonalityPoints = majorTonalityPoints + 1;
                minorTonalityPoints = minorTonalityPoints + 1;
                break;
            case "cyan":
                //: Major 2, Minor 1
                majorTonalityPoints = majorTonalityPoints + 1;
                minorTonalityPoints = minorTonalityPoints + 2;
                break;
            case "blue":
                //: Major 0, Minor 3
                majorTonalityPoints = majorTonalityPoints + 0;
                minorTonalityPoints = minorTonalityPoints + 3;
                break;
            case "magenta":
                //: Major 0, Minor 4
                majorTonalityPoints = majorTonalityPoints + 0;
                minorTonalityPoints = minorTonalityPoints + 4;
                break;
            case "black":
                //: Major 0, Minor 5
                majorTonalityPoints = majorTonalityPoints + 0;
                minorTonalityPoints = minorTonalityPoints + 5;

                System.out.println("black: " + minorTonalityPoints);
                break;
        }
        //should calculate major/minor from colors of all objects found
        if (ObjectsFoundFromDetections.size() != 0) {
            switch (largestOfColors(false, objectsForColorDetection).getName()) {
                case "white":
                    //: Major 5, Minor 0
                    majorTonalityPoints = majorTonalityPoints + 3;
                    minorTonalityPoints = minorTonalityPoints + 0;
                    chromaticTonalityPoints = 4;

                    //dominant category should get upper edge so add one more points
                    if (dominantFrequencyCategoryObject.isMobile()) {
                        majorTonalityPoints = majorTonalityPoints + 5;
                    } else {
                        chromaticTonalityPoints = chromaticTonalityPoints + 5;
                    }
                    break;
                case "red":
                    //: Major 4, Minor 0
                    majorTonalityPoints = majorTonalityPoints + 2;
                    minorTonalityPoints = minorTonalityPoints + 1;

                    //dominant category should get upper edge so add one more points
                    if (dominantFrequencyCategoryObject.isMobile()) {
                        majorTonalityPoints = majorTonalityPoints + 5;
                    } else {
                        minorTonalityPoints = minorTonalityPoints + 5;
                    }
                    break;
                case "orange":
                    //: Major 3, Minor 0
                    majorTonalityPoints = majorTonalityPoints + 1;
                    minorTonalityPoints = minorTonalityPoints + 1;

                    //dominant category should get upper edge so add one more points
                    if (dominantFrequencyCategoryObject.isMobile()) {
                        majorTonalityPoints = majorTonalityPoints + 5;
                    } else {
                        minorTonalityPoints = minorTonalityPoints + 5;
                    }
                    break;
                case "yellow":
                    //: Major 2, Minor 1
                    majorTonalityPoints = majorTonalityPoints + 1;
                    minorTonalityPoints = minorTonalityPoints + 1;

                    //dominant category should get upper edge so add one more points
                    if (dominantFrequencyCategoryObject.isMobile()) {
                        majorTonalityPoints = majorTonalityPoints + 5;
                    } else {
                        minorTonalityPoints = minorTonalityPoints + 5;
                    }
                    break;
                case "green":
                    //: Major 1, Minor 1
                    majorTonalityPoints = majorTonalityPoints + 1;
                    minorTonalityPoints = minorTonalityPoints + 1;

                    //dominant category should get upper edge so add one more points
                    if (dominantFrequencyCategoryObject.isMobile()) {
                        majorTonalityPoints = majorTonalityPoints + 5;
                    } else {
                        minorTonalityPoints = minorTonalityPoints + 5;
                    }
                    break;
                case "cyan":
                    //: Major 2, Minor 1
                    majorTonalityPoints = majorTonalityPoints + 1;
                    minorTonalityPoints = minorTonalityPoints + 2;

                    //dominant category should get upper edge so add one more points
                    if (dominantFrequencyCategoryObject.isMobile()) {
                        majorTonalityPoints = majorTonalityPoints + 5;
                    } else {
                        minorTonalityPoints = minorTonalityPoints + 5;
                    }
                    break;
                case "blue":
                    //: Major 0, Minor 3
                    majorTonalityPoints = majorTonalityPoints + 0;
                    minorTonalityPoints = minorTonalityPoints + 3;

                    //dominant category should get upper edge so add one more points
                    if (dominantFrequencyCategoryObject.isMobile()) {
                        majorTonalityPoints = majorTonalityPoints + 5;
                    } else {
                        minorTonalityPoints = minorTonalityPoints + 5;
                    }
                    break;
                case "magenta":
                    //: Major 0, Minor 4
                    majorTonalityPoints = majorTonalityPoints + 0;
                    minorTonalityPoints = minorTonalityPoints + 4;

                    //dominant category should get upper edge so add one more points
                    if (dominantFrequencyCategoryObject.isMobile()) {
                        majorTonalityPoints = majorTonalityPoints + 5;
                    } else {
                        minorTonalityPoints = minorTonalityPoints + 5;
                    }
                    break;
                case "black":
                    //: Major 0, Minor 5
                    majorTonalityPoints = majorTonalityPoints + 0;
                    minorTonalityPoints = minorTonalityPoints + 5;
                    chromaticTonalityPoints = 4;

                    //dominant category should get upper edge so add one more points
                    if (dominantFrequencyCategoryObject.isMobile()) {
                        chromaticTonalityPoints = chromaticTonalityPoints + 5;
                    } else {
                        minorTonalityPoints = minorTonalityPoints + 5;
                    }
                    break;
            }
        }


        //look at some of the objects


        //go through all the objects
        //calculate speed of all the objects found
        int count = ObjectsFoundFromDetections.size();
        int maj = 0;
        int min = 0;
        int chromatic = 0;
        if (count != 0) {
            for (String[] obj : ObjectsFoundFromDetections) {
                String object = obj[0];
                String objCategory = Objects.get(object).getCategory();
                System.out.println("object: " + object + " - Object Cat: " + objCategory);
                maj = maj + objectCategoryScorer(object, objCategory, "tonality")[0];
                min = min + objectCategoryScorer(object, objCategory, "tonality")[1];
                chromatic = chromatic + objectCategoryScorer(object, objCategory, "tonality")[1];
                boolean happy = Objects.get(object).isHappy();
                //   System.out.println(object+" - "+speed);
                if (happy) {
                    //major
                    if (Objects.get(object).isMobile()) {
                        maj = +10;
                    } else {
                        maj++;
                    }
                } else { //mid
                    min++;
                }
                count++;
            }
        } else {
            count = 1;
        }
        minorTonalityPoints = minorTonalityPoints + (min / count);
        majorTonalityPoints = majorTonalityPoints + (maj / count);
        chromaticTonalityPoints = chromaticTonalityPoints + (chromatic / count);
        System.out.println("Scores: maj:" + majorTonalityPoints + " - min:" + minorTonalityPoints + " - chro:" + chromaticTonalityPoints);

        //find largest scoring tonality

        switch (largestOutOfThree(majorTonalityPoints, minorTonalityPoints, chromaticTonalityPoints)) {
            case "n1":
                System.out.println(majorTonalityPoints + " major tonality");

                setTonality(true);
                break;
            case "n2":
                System.out.println(minorTonalityPoints + " mid  tonality");
                setTonality(false);
                break;
            case "n3":
                System.out.println(chromaticTonalityPoints + " chromatic  tonality");

                setTonality(false);
                chromaticTonality = true;
                break;
        }


        System.out.println("------End of Tonality Analysis-----");
    }

    private void tempoGenerator(BufferedImage myImage) {
        //initialize tempo points
        slowTempoPoints = 0;
        midTempoPoints = 0;
        highTempoPoints = 0;
        int count = ObjectsFoundFromDetections.size();
        //calculate tempo factors base on the objects found in the picture
        //what kind of objects , are they mobile, and what speed value do they have-
        if (count != 0) {
            for (String[] obj : ObjectsFoundFromDetections) {
                String object = obj[0];
                //   System.out.println(object+" - "+speed);
                //what kind of object is this and how will that influence the speed of the music
                String objCategory = Objects.get(object).getCategory();
                System.out.println("object: " + object + " - Object Cat: " + objCategory);
                slowTempoPoints = slowTempoPoints + java.util.Objects.requireNonNull(objectCategoryScorer(object, objCategory, "tempo"))[0];
                midTempoPoints = midTempoPoints + java.util.Objects.requireNonNull(objectCategoryScorer(object, objCategory, "tempo"))[1];
                highTempoPoints = highTempoPoints + java.util.Objects.requireNonNull(objectCategoryScorer(object, objCategory, "tempo"))[2];
            }

            //average scores over the number of object found
            slowTempoPoints =   (slowTempoPoints / count);
            midTempoPoints =  (slowTempoPoints / count);
            highTempoPoints = (slowTempoPoints / count);


            //number of objects on the scene should determine speed too, emphasis given to dominant categories
            if (dominantFrequencyCategoryObject.isMobile()) {

                if (ObjectsFoundFromDetections.size() < 4) {
                    System.out.println("1 or more mobile objects");
                    slowTempoPoints = slowTempoPoints + 4;
                    highTempoPoints = highTempoPoints + 10;
                } else if (ObjectsFoundFromDetections.size() >= 4 && ObjectsFoundFromDetections.size() <= 10) {
                    System.out.println("few objects");
                    midTempoPoints = midTempoPoints + 5;
                    highTempoPoints = highTempoPoints + 10;
                } else if (ObjectsFoundFromDetections.size() > 10) {
                    //if the object found is mobile the speed should be faster
                    System.out.println("many mobile objects");
                    highTempoPoints = highTempoPoints + 10;
                }
            } else {
                /// if objects are immobile
                if (ObjectsFoundFromDetections.size() < 4) {
                    System.out.println("1 or more immobile objects");
                    slowTempoPoints = slowTempoPoints + 10;
                } else if (ObjectsFoundFromDetections.size() >= 4 && ObjectsFoundFromDetections.size() <= 10) {
                    System.out.println("few objects immobile objects");
                    midTempoPoints = midTempoPoints + 5;
                } else if (ObjectsFoundFromDetections.size() > 10) {
                    System.out.println("many immobile objects");
                    highTempoPoints = highTempoPoints + 10;
                }

            }


            //number largest objects category on the scene should determine speed too, emphasis given to dominant categories
            if (dominantLargestObjectAreaCategory.isMobile()) {

                if (ObjectsFoundFromDetections.size() < 4) {
                    System.out.println("1 or more mobile objects");
                    slowTempoPoints = slowTempoPoints + 4;
                    highTempoPoints = highTempoPoints + 10;
                } else if (ObjectsFoundFromDetections.size() >= 4 && ObjectsFoundFromDetections.size() <= 10) {
                    System.out.println("few objects");
                    midTempoPoints = midTempoPoints + 5;
                    highTempoPoints = highTempoPoints + 10;
                } else if (ObjectsFoundFromDetections.size() > 10) {
                    //if the object found is mobile the speed should be faster
                    System.out.println("many mobile objects");
                    highTempoPoints = highTempoPoints + 10;
                }
            } else {
                /// if objects are immobile
                if (ObjectsFoundFromDetections.size() < 4) {
                    System.out.println("1 or more immobile objects");
                    slowTempoPoints = slowTempoPoints + 10;
                } else if (ObjectsFoundFromDetections.size() >= 4 && ObjectsFoundFromDetections.size() <= 10) {
                    System.out.println("few objects immobile objects");
                    midTempoPoints = midTempoPoints + 5;
                } else if (ObjectsFoundFromDetections.size() > 10) {
                    System.out.println("many immobile objects");
                    highTempoPoints = highTempoPoints + 10;
                }

            }


        } else {
            count = 1;
            midTempoPoints = 10;
        }

        //should scan average colors of the whole picture
        switch (largestOfColors(true, ObjectsFoundFromDetections).getName()) {
            case "white":
                //: highTempoPoints 5, midTempoPoints 2,slowTempoPoints 0
                highTempoPoints = highTempoPoints + 3;
                midTempoPoints = midTempoPoints + 2;
                slowTempoPoints = slowTempoPoints + 1;
                break;
            case "red":
                //: highTempoPoints 4, midTempoPoints 3,slowTempoPoints 0
                highTempoPoints = highTempoPoints + 2;
                midTempoPoints = midTempoPoints + 3;
                slowTempoPoints = slowTempoPoints + 1;
                break;
            case "orange":
                //: highTempoPoints 3, midTempoPoints 4,slowTempoPoints 0
                highTempoPoints = highTempoPoints + 2;
                midTempoPoints = midTempoPoints + 3;
                slowTempoPoints = slowTempoPoints + 1;
                break;
            case "yellow":
                //: highTempoPoints 2, midTempoPoints 5,slowTempoPoints 0
                highTempoPoints = highTempoPoints + 2;
                midTempoPoints = midTempoPoints + 4;
                slowTempoPoints = slowTempoPoints + 1;
                break;
            case "green":
                //: highTempoPoints 0, midTempoPoints 5,slowTempoPoints 0
                midTempoPoints = midTempoPoints + 5;

                break;
            case "cyan":
                //: highTempoPoints 0, midTempoPoints 5,slowTempoPoints 2
                highTempoPoints = highTempoPoints + 0;
                midTempoPoints = midTempoPoints + 4;
                slowTempoPoints = slowTempoPoints + 2;
                break;
            case "blue":
                //: highTempoPoints 0, midTempoPoints 4,slowTempoPoints 3
                highTempoPoints = highTempoPoints + 0;
                midTempoPoints = midTempoPoints + 3;
                slowTempoPoints = slowTempoPoints + 3;
                break;
            case "magenta":
                //: highTempoPoints 1, midTempoPoints 3,slowTempoPoints 4
                highTempoPoints = highTempoPoints + 0;
                midTempoPoints = midTempoPoints + 2;
                slowTempoPoints = slowTempoPoints + 4;
                break;
            case "black":
                //: highTempoPoints 0, midTempoPoints 2,slowTempoPoints 5
                highTempoPoints = highTempoPoints + 0;
                midTempoPoints = midTempoPoints + 2;
                slowTempoPoints = slowTempoPoints + 5;
                break;
        }

        //should calculate speed from colors of all objects found within the dominant categories

        if (ObjectsFoundFromDetections.size() != 0) {


            switch (largestOfColors(false, objectsForColorDetection).getName()) {
                case "white":
                    //: highTempoPoints 5, midTempoPoints 2,slowTempoPoints 0
                    highTempoPoints = highTempoPoints + 3;
                    midTempoPoints = midTempoPoints + 2;
                    slowTempoPoints = slowTempoPoints + 1;
                    break;
                case "red":
                    //: highTempoPoints 4, midTempoPoints 3,slowTempoPoints 0
                    highTempoPoints = highTempoPoints + 2;
                    midTempoPoints = midTempoPoints + 3;
                    slowTempoPoints = slowTempoPoints + 1;
                    break;
                case "orange":
                    //: highTempoPoints 3, midTempoPoints 4,slowTempoPoints 0
                    highTempoPoints = highTempoPoints + 2;
                    midTempoPoints = midTempoPoints + 3;
                    slowTempoPoints = slowTempoPoints + 1;
                    break;
                case "yellow":
                    //: highTempoPoints 2, midTempoPoints 5,slowTempoPoints 0
                    highTempoPoints = highTempoPoints + 2;
                    midTempoPoints = midTempoPoints + 4;
                    slowTempoPoints = slowTempoPoints + 1;
                    break;
                case "green":
                    //: highTempoPoints 0, midTempoPoints 5,slowTempoPoints 0
                    midTempoPoints = midTempoPoints + 5;

                    break;
                case "cyan":
                    //: highTempoPoints 0, midTempoPoints 5,slowTempoPoints 2
                    highTempoPoints = highTempoPoints + 0;
                    midTempoPoints = midTempoPoints + 4;
                    slowTempoPoints = slowTempoPoints + 2;
                    break;
                case "blue":
                    //: highTempoPoints 0, midTempoPoints 4,slowTempoPoints 3
                    highTempoPoints = highTempoPoints + 0;
                    midTempoPoints = midTempoPoints + 3;
                    slowTempoPoints = slowTempoPoints + 3;
                    break;
                case "magenta":
                    //: highTempoPoints 1, midTempoPoints 3,slowTempoPoints 4
                    highTempoPoints = highTempoPoints + 0;
                    midTempoPoints = midTempoPoints + 2;
                    slowTempoPoints = slowTempoPoints + 4;
                    break;
                case "black":
                    //: highTempoPoints 0, midTempoPoints 2,slowTempoPoints 5
                    highTempoPoints = highTempoPoints + 0;
                    midTempoPoints = midTempoPoints + 2;
                    slowTempoPoints = slowTempoPoints + 5;
                    break;
            }
        }


        System.out.println("------Tempo Analysis-----");


        System.out.println("Scores: s:" + slowTempoPoints + " - m:" + midTempoPoints + " - f:" + highTempoPoints);
        //find largest scoring tempo


        double n1 = slowTempoPoints, n2 = midTempoPoints, n3 = highTempoPoints;
        switch (largestOutOfThree(n1, n2, n3)) {
            case "n1":
                System.out.println(n1 + " slow tempo");

                setTempo("slow");
                break;
            case "n2":
                System.out.println(n2 + " mid  tempo");
                setTempo("normal");
                break;
            case "n3":
                System.out.println(n3 + " fast  tempo");

                setTempo("fast");
                break;
        }
        System.out.println("------End of Tempo Analysis-----");
        //
    }

    private void setDynamics(String level, boolean dynamicVariation) {
        this.level = level;
        this.dynamicVariation = dynamicVariation;
    }

    public String[] getDynamics() {
        return new String[]{level, String.valueOf(dynamicVariation)};
    }

    public static void restartApplication() throws IOException, URISyntaxException {
        Main.restartApplication();
    }

    public MyColor largestOfColors(boolean fullImage, ArrayList<String[]> objectFound) {
        MyColor[] myColors;
        if (fullImage) {
            myColors = new MyColor[]{red, green, black, blue, magenta, cyan, yellow, white, orangeColor};
        } else {
            //    System.out.println("calculating largest avg color of objects");
            myColors = new MyColor[]{redObjs, greenObjs, blackObjs, blueObjs, magentaObjs, cyanObjs, yellowObjs, whiteObjs, orangeObjs};
        }
        double max = myColors[0].getValue();
        MyColor maxColor = new MyColor(myColors[0].getName(), 0.0);
        for (int i = 0; i < myColors.length; i++) {
            MyColor current = new MyColor(myColors[i].getName(), 0.0);
            double value = myColors[i].getValue();


            if (max < value) {
                //     System.out.println(current.getName()+" is larger than  "+maxColor.getName());
                max = value;
                maxColor = current;
            } else {
                // System.out.println(maxColor.getName()+" (currentMax) is larger than  "+current.getName());

            }
        }
        //  System.out.println("calculated largest avg color of objects: "+maxColor.getName());

        //set color
        if (fullImage) {
            this.maxColor = maxColor;
        } else {
            if (objectFound.size() == 0) {

                System.out.println("no objects bro");
            } else {

            }
        }
        return maxColor;
    }

    private String[] dominantCalculator(ArrayList<String[]> ObjectsFoundFromDetections) {
        counter = new HashMap<String, Integer>();

        int numberOfObjects = ObjectsFoundFromDetections.size();
        // System.out.println(numberOfObjects + " Objects");

        String highestFrequencyCat = "";
        int highestCount = 0;

        //count how many of each object there was
        for (int i = 0; i < ObjectsFoundFromDetections.size(); i++) {
            String[] data = ObjectsFoundFromDetections.get(i);
            String testCat = Objects.get(data[0]).getCategory().trim();

            if (counter.get(testCat) != null) {
                counter.put(testCat, (counter.get(testCat) + 1));
                //     System.out.println(testCat + " - " + (counter.get(testCat)));
                if (highestCount < counter.get(testCat)) {
                    highestFrequencyCat = testCat;
                    highestCount = counter.get(testCat);
                }
            } else {
                //   System.out.println(testCat + " found - 1");
                counter.put(testCat, 1);

                if (highestCount < 0 || i == 0) {
                    highestFrequencyCat = testCat;
                    highestCount = 1;

                }
            }
        }
        String categoryWithLargestObjectSize = "none";
        //check which objects take up the most space in the picture
        if (ObjectsFoundFromDetections.size() > 0) {
            categoryWithLargestObjectSize = categoryWithLargestObjectSize();
        }
        System.out.println("------Dom Obj Category Calculation---------");
        System.out.println("Largest Obj Count Category: " + highestFrequencyCat);
        System.out.println("Largest Obj Category: " + categoryWithLargestObjectSize);
        System.out.println("------Dom Obj Category End---------");
        return new String[]{highestFrequencyCat, categoryWithLargestObjectSize};
    }


    public boolean avgObjectDensity() {
        //distance between objects
        double totalDistances = 0;
        System.out.println("-----Calculate Relative Obj Density ------");


        if (ObjectsFoundFromDetections.size() > 1) { //if there are more than two objects

            int objCount = ObjectsFoundFromDetections.size();
            for (String[] obj : ObjectsFoundFromDetections) {

                int x1 = Integer.parseInt(obj[2].trim());
                int y1 = Integer.parseInt(obj[3].trim());
                int x2 = Integer.parseInt(obj[4].trim());
                int y2 = Integer.parseInt(obj[5].trim());
                //centre position of every object
                int x_centre = ((x2 - x1) / 2) + x1;
                int y_centre = ((y2 - y1) / 2) + y1;
                String location = "(" + x_centre + " , " + y_centre + ")";
                for (int j = 1; j < ObjectsFoundFromDetections.size(); j++) {

                    int cur_x1 = Integer.parseInt(ObjectsFoundFromDetections.get(j)[2].trim());
                    int cur_y1 = Integer.parseInt(ObjectsFoundFromDetections.get(j)[3].trim());
                    int cur_x2 = Integer.parseInt(ObjectsFoundFromDetections.get(j)[4].trim());
                    int cur_y2 = Integer.parseInt(ObjectsFoundFromDetections.get(j)[5].trim());


                    int cur_x_centre = ((cur_x2 - cur_x1) / 2) + cur_x1;
                    int cur_y_centre = ((cur_y2 - cur_y1) / 2) + cur_y1;

                    //pythagorus
                    double dist = Math.sqrt(Math.pow(cur_x_centre - x_centre, 2) + Math.pow(cur_y_centre - y_centre, 2));

                    String locationObj = "(" + cur_x_centre + " , " + cur_y_centre + ")";
                    //   System.out.println("distance between: "+obj [0] +" "+location+ " and "+imageAnalysis.ObjectsFoundFromDetections.get(j)[0] +" "+locationObj+ " : "+dist);
                    totalDistances = totalDistances + dist;

                }
            }
            avgDistance = 2.0 * totalDistances / (ObjectsFoundFromDetections.size() * (ObjectsFoundFromDetections.size() - 1));

            System.out.println("average distance between objects is: " + avgDistance + " pixels");

            System.out.println("average density relative to area  is : " + ((avgDistance / myImageInput.getWidth()) + (avgDistance / myImageInput.getHeight())) / 2);
            System.out.println("-----End Relative Obj Density ------");

            double densityRelativeToArea = ((avgDistance / myImageInput.getWidth()) + (avgDistance / myImageInput.getHeight())) / 2;

            //smaller it is the closer the are
            //they are dense
            return densityRelativeToArea < 0.75;


        }
        //if no objects automatically assume low density
        System.out.println("Only one or no objects");
        System.out.println("-----End Relative Obj Density ------");
        return false;
    }

    public String avgObjectSizeRelativeToImage() {

        //add all the areas of object then devide by area to get percentage


        int imageArea = myImageInput.getWidth() * myImageInput.getHeight();
        System.out.println("-----Calculate Relative Obj Size to Image ------");
        System.out.println("Image Area: " + imageArea);
        double ratios = 0.0;

        if (ObjectsFoundFromDetections.size() > 0) {
            for (String[] obj : ObjectsFoundFromDetections) {

                int x1 = Integer.parseInt(obj[2].trim());
                int y1 = Integer.parseInt(obj[3].trim());
                int x2 = Integer.parseInt(obj[4].trim());
                int y2 = Integer.parseInt(obj[5].trim());

                int width = x2 - x1;
                int height = y2 - y1;

                double ratio = ((double) width * (double) height) / (double) imageArea;
                String relative = "small";
                if (ratio > 0.1) {
                    relative = "medium";
                }
                if (ratio > 0.5) {
                    relative = "large";
                }
                ratios = ratios + ratio;
                //  System.out.println("object: "+obj[0]+" area "+(width * height) +" relative size: "+relative);

            }

            //add calculate avg
            double avg = ratios / ObjectsFoundFromDetections.size();
            String relative = "small";
            if (avg > 0.1) {
                relative = "medium";
            }
            if (avg > 0.5) {
                relative = "large";
            }
            System.out.println("Average Object To Picture Ratio: " + avg + " size:" + relative);
            System.out.println("-----Done Calculate Relative Obj Size to Image ------");

            return relative;
        } else {
            return "none";
        }
    }

    public String categoryWithLargestObjectSize() {

        HashMap<String, Double> largestObjectsCounter = new HashMap<String, Double>();

        int imageArea = myImageInput.getWidth() * myImageInput.getHeight();
        System.out.println("-----Calculate Obj Category with the largest objects Size  ------");
        System.out.println("Image Area: " + imageArea);
        double ratios = 0.0;
        int i = 0;

        for (String[] obj : ObjectsFoundFromDetections) {
            String[] data = ObjectsFoundFromDetections.get(i);
            String category = Objects.get(data[0]).getCategory().trim();

            int x1 = Integer.parseInt(obj[2].trim());
            int y1 = Integer.parseInt(obj[3].trim());
            int x2 = Integer.parseInt(obj[4].trim());
            int y2 = Integer.parseInt(obj[5].trim());

            int width = x2 - x1;
            int height = y2 - y1;

            double objectArea = ((double) width * (double) height);
            System.out.println("object: " + Objects.get(data[0]) + " Category: " + category + " " + largestObjectsCounter.get(category));

            largestObjectsCounter.merge(category, objectArea, Double::sum);
            i++;
        }

        //add calculate largest category
        double largestArea = 0;
        String largestCategory = "";
        for (String category : largestObjectsCounter.keySet()) {

            //exclude buildings unless they're the only thing in the picture
            if ((largestObjectsCounter.size() == 1 || !category.equals("buildings")) && largestArea < largestObjectsCounter.get(category)) {
                largestArea = largestObjectsCounter.get(category);
                largestCategory = category;
            }

        }
        System.out.println("Largest Area: " + largestArea + " Category:" + largestCategory);
        System.out.println("-----Done Calculate Largest Obj Area ------");

        return largestCategory;

    }





    private int[] objectCategoryScorer(String object, String objCategory, String scoringCategory) {

        switch (objCategory) {

            case "animal":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 1;
                    int midScore = 2;
                    int slowScore = 3;

                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 1;
                    int minorScore = 2;
                    int chromaticScore = 1;
                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 1;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 1;
                    int dynamicPoints = 2;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 1;  //animals, trees
                    int indoorPoints = 0; //inside a building
                    int outdoorPoints = 1; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 0; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 1; //are we a quiet ambiant space
                    int smallSpacePoints = 0; //are we in a small space
                    int largeSpacePoints = 0; //are we in a large space


                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }


                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "nature":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 0;
                    int midScore = 2;
                    int slowScore = 3;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 2;
                    int minorScore = 3;
                    int chromaticScore = 1;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 1;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 1;
                    int dynamicPoints = 2;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 1;  //animals, trees
                    int indoorPoints = 0; //inside a building
                    int outdoorPoints = 1; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 0; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 1; //are we a quiet ambiant space
                    int smallSpacePoints = 0; //are we in a small space
                    int largeSpacePoints = 1; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            outdoorPoints = outdoorPoints + 1;

                            naturePoints = naturePoints + 1;
                            break;
                        case "normal":
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "slow":
                            ambiantPoints = ambiantPoints + 2;
                            naturePoints = naturePoints + 1;
                            outdoorPoints = outdoorPoints + 1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }


                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "human":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 3;
                    int midScore = 2;
                    int slowScore = 1;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 3;
                    int minorScore = 1;
                    int chromaticScore = 1;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 2;
                    int quietPoints = 1;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 1;
                    int dynamicPoints = 2;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {
                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 0; //inside a building
                    int outdoorPoints = 0; //outside a building
                    int crowdPoints = 1; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 0; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 0; //are we a quiet ambiant space
                    int smallSpacePoints = 0; //are we in a small space
                    int largeSpacePoints = 0; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            crowdPoints = crowdPoints + 1;
                            break;
                        case "normal":

                            crowdPoints = crowdPoints +1;
                            break;
                        case "slow":

                            crowdPoints = crowdPoints +1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        darkPoints = darkPoints + 1;
                    }


                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "transport":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 3;
                    int midScore = 2;
                    int slowScore = 1;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 2;
                    int minorScore = 1;
                    int chromaticScore = 3;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 2;
                    int quietPoints = 1;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 1;
                    int dynamicPoints = 2;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 0; //inside a building
                    int outdoorPoints = 1; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 0; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 0; //are we a quiet ambiant space
                    int smallSpacePoints = 0; //are we in a small space
                    int largeSpacePoints = 1; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            outdoorPoints = outdoorPoints + 1;
                            crowdPoints = crowdPoints + 1;
                            break;
                        case "normal":
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "slow":
                            ambiantPoints = ambiantPoints + 2;

                            outdoorPoints = outdoorPoints + 1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "traffic":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 3;
                    int midScore = 2;
                    int slowScore = 1;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 2;
                    int minorScore = 1;
                    int chromaticScore = 3;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 2;
                    int quietPoints = 1;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 1;
                    int dynamicPoints = 2;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 0; //inside a building
                    int outdoorPoints = 1; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 1; //are we in a bright place
                    int trafficPoints = 1; //are we in traffic
                    int ambiantPoints = 0; //are we a quiet ambiant space
                    int smallSpacePoints = 0; //are we in a small space
                    int largeSpacePoints = 1; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            outdoorPoints = outdoorPoints + 1;
                            crowdPoints = crowdPoints + 1;
                            break;
                        case "normal":
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "slow":
                            ambiantPoints = ambiantPoints + 2;
                            outdoorPoints = outdoorPoints + 1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }


                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "outdoor":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 2;
                    int midScore = 3;
                    int slowScore = 1;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 3;
                    int minorScore = 2;
                    int chromaticScore = 2;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 2;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 1;
                    int dynamicPoints = 2;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 0; //inside a building
                    int outdoorPoints = 1; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 1; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 1; //are we a quiet ambiant space
                    int smallSpacePoints = 0; //are we in a small space
                    int largeSpacePoints = 1; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            outdoorPoints = outdoorPoints + 1;
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "normal":
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "slow":
                            ambiantPoints = ambiantPoints + 2;
                            outdoorPoints = outdoorPoints + 1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "accessory":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 1;
                    int midScore = 2;
                    int slowScore = 3;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 1;
                    int minorScore = 2;
                    int chromaticScore = 1;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 1;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 2;
                    int dynamicPoints = 1;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 1; //inside a building
                    int outdoorPoints = 0; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 1; //are we in a dark place
                    int brightPoints = 0; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 1; //are we a quiet ambiant space
                    int smallSpacePoints = 0; //are we in a small space
                    int largeSpacePoints = 0; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            break;
                        case "normal":
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "slow":
                            ambiantPoints = ambiantPoints + 2;
                            indoorPoints = indoorPoints + 1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "sports":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 3;
                    int midScore = 2;
                    int slowScore = 1;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 3;
                    int minorScore = 2;
                    int chromaticScore = 1;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 2;
                    int quietPoints = 1;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 1;
                    int dynamicPoints = 2;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 0; //inside a building
                    int outdoorPoints = 1; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 0; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 0; //are we a quiet ambiant space
                    int smallSpacePoints = 0; //are we in a small space
                    int largeSpacePoints = 1; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            outdoorPoints = outdoorPoints + 1;
                            break;
                        case "normal":
                            outdoorPoints = outdoorPoints + 1;
                            break;
                        case "slow":
                            outdoorPoints = outdoorPoints + 1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "kitchen":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 1;
                    int midScore = 3;
                    int slowScore = 2;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 3;
                    int minorScore = 2;
                    int chromaticScore = 1;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 1;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 2;
                    int dynamicPoints = 1;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 1; //inside a building
                    int outdoorPoints = 0; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 1; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 1; //are we a quiet ambiant space
                    int smallSpacePoints = 1; //are we in a small space
                    int largeSpacePoints = 0; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            break;
                        case "normal":
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "slow":
                            ambiantPoints = ambiantPoints + 2;
                            indoorPoints = indoorPoints + 1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "food":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 1;
                    int midScore = 3;
                    int slowScore = 2;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 3;
                    int minorScore = 2;
                    int chromaticScore = 1;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 1;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 2;
                    int dynamicPoints = 1;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 1; //inside a building
                    int outdoorPoints = 0; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 1; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 1; //are we a quiet ambiant space
                    int smallSpacePoints = 1; //are we in a small space
                    int largeSpacePoints = 0; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            brightPoints = brightPoints + 1;
                            break;
                        case "normal":
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "slow":
                            ambiantPoints = ambiantPoints + 2;
                            indoorPoints = indoorPoints + 1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "furniture":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 1;
                    int midScore = 2;
                    int slowScore = 3;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 5;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 2;
                    int minorScore = 2;
                    int chromaticScore = 1;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 1;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 2;
                    int dynamicPoints = 1;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {
                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 3; //inside a building
                    int outdoorPoints = 0; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 1; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 0; //are we a quiet ambiant space
                    int smallSpacePoints = 1; //are we in a small space
                    int largeSpacePoints = 0; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            break;
                        case "normal":
                            break;
                        case "slow":

                            indoorPoints = indoorPoints + 1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                       // ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "bathroom":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 1;
                    int midScore = 2;
                    int slowScore = 3;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 1;
                    int minorScore = 2;
                    int chromaticScore = 1;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 1;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 1;
                    int dynamicPoints = 2;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 3; //inside a building
                    int outdoorPoints = 0; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 1; //are we in a dark place
                    int brightPoints = 0; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 1; //are we a quiet ambiant space
                    int smallSpacePoints = 1; //are we in a small space
                    int largeSpacePoints = 0; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            break;
                        case "normal":
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "slow":
                            ambiantPoints = ambiantPoints + 2;
                            indoorPoints = indoorPoints + 1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "electronics":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 1;
                    int midScore = 2;
                    int slowScore = 3;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 3;
                    int minorScore = 2;
                    int chromaticScore = 4;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 1;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 2;
                    int dynamicPoints = 1;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {
                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 1; //inside a building
                    int outdoorPoints = 0; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 1; //are we in a dark place
                    int brightPoints = 0; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 1; //are we a quiet ambiant space
                    int smallSpacePoints = 1; //are we in a small space
                    int largeSpacePoints = 0; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            break;
                        case "normal":
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "slow":
                            ambiantPoints = ambiantPoints + 2;
                            indoorPoints = indoorPoints + 1;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "stationary":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 1;
                    int midScore = 2;
                    int slowScore = 3;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 3;
                    int minorScore = 1;
                    int chromaticScore = 2;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 1;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 2;
                    int dynamicPoints = 1;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {
                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 1; //inside a building
                    int outdoorPoints = 0; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 1; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 0; //are we a quiet ambiant space
                    int smallSpacePoints = 1; //are we in a small space
                    int largeSpacePoints = 0; //are we in a large space



                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "toy":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 1;
                    int midScore = 2;
                    int slowScore = 3;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 3;
                    int minorScore = 1;
                    int chromaticScore = 2;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 1;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 2;
                    int dynamicPoints = 1;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {
                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 1; //inside a building
                    int outdoorPoints = 0; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 1; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 1; //are we a quiet ambiant space
                    int smallSpacePoints = 1; //are we in a small space
                    int largeSpacePoints = 0; //are we in a large space



                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "buildings":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 0;
                    int midScore = 0;
                    int slowScore = 3;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 1;
                    int minorScore = 3;
                    int chromaticScore = 2;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 1;
                    int quietPoints = 2;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 2;
                    int dynamicPoints = 1;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 0; //inside a building
                    int outdoorPoints = 1; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 1; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 1; //are we a quiet ambiant space
                    int smallSpacePoints = 0; //are we in a small space
                    int largeSpacePoints = 1; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            outdoorPoints = outdoorPoints + 1;
                            break;
                        case "normal":
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "slow":
                            ambiantPoints = ambiantPoints + 2;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                }
                break;
            case "instruments":
                if (scoringCategory.equals("tempo")) {
                    int fastScore = 1;
                    int midScore = 2;
                    int slowScore = 3;
                    //mobility of object, if it moves and its a fast or slow moving object, adjust accordingly
                    int speed = Objects.get(object).getSpeed();
                    switch (speed) {
                        case 1: //slow
                            if (Objects.get(object).isMobile()) {
                                slowScore = slowScore + 10;
                            } else {
                                slowScore = slowScore + 1;
                            }
                            break;
                        case 2: //mid
                            midScore = midScore + 1;
                            break;
                        case 3://fast
                            if (Objects.get(object).isMobile()) {
                                fastScore = fastScore + 5;
                            } else {
                                //anything s
                                fastScore = fastScore + 1;
                            }
                            break;
                    }

                    return new int[]{slowScore, midScore, fastScore};
                }
                if (scoringCategory.equals("tonality")) {
                    int majorScore = 3;
                    int minorScore = 1;
                    int chromaticScore = 0;

                    return new int[]{majorScore, minorScore, chromaticScore};
                }
                if (scoringCategory.equals("dynamics")) {
                    int loudPoints = 2;
                    int quietPoints = 1;
                    return new int[]{loudPoints, quietPoints};
                }
                if (scoringCategory.equals("dynamicVariation")) {
                    int staticPoints = 1;
                    int dynamicPoints = 2;
                    return new int[]{dynamicPoints, staticPoints};
                }
                if (scoringCategory.equals("mood")) {

                    //score for each mood
                    int naturePoints = 0;  //animals, trees
                    int indoorPoints = 2; //inside a building
                    int outdoorPoints = 1; //outside a building
                    int crowdPoints = 0; //are we in a crowd of people
                    int waterPoints = 0; //are we near water
                    int darkPoints = 0; //are we in a dark place
                    int brightPoints = 1; //are we in a bright place
                    int trafficPoints = 0; //are we in traffic
                    int ambiantPoints = 1; //are we a quiet ambiant space
                    int smallSpacePoints = 1; //are we in a small space
                    int largeSpacePoints = 0; //are we in a large space

                    //tempo
                    switch (this.tempo) {
                        case "fast":
                            break;
                        case "normal":
                            ambiantPoints = ambiantPoints + 1;
                            break;
                        case "slow":
                            ambiantPoints = ambiantPoints + 2;
                            break;
                    }

                    //tonality
                    if (this.tonality) { //major
                        brightPoints = brightPoints + 1;
                    } else {
                        ambiantPoints = ambiantPoints + 1;
                        darkPoints = darkPoints + 1;
                    }

                    return new int[]{naturePoints, indoorPoints, outdoorPoints, crowdPoints, waterPoints, darkPoints, brightPoints, trafficPoints, ambiantPoints, smallSpacePoints, largeSpacePoints};
                    }
                break;
                }
        //if not category, not possible
        return null;
    }

    private void colorScan(BufferedImage myImage, int x1, int y1, int x2, int y2, boolean full, int count, String object) {

        int scanX1 = x1;
        int scanY1 = y1;
        int scanX2 = x2;
        int scanY2 = y2;

        if (full) {

            //       System.out.println("------Full Image Color Analysis-----");

            //     System.out.println("Full Image: "+myImage.getWidth()+" x "+myImage.getHeight());
            //   System.out.println("- starting at: "+scanX1+ ","+scanY1 + " to "+scanX2 + ","+scanY2);

            scanX1 = 0;
            scanY1 = 0;
            scanX2 = myImage.getWidth();
            scanY2 = myImage.getHeight();

            for (int y = scanY1; y < scanY2; y++) {

                for (int x = scanX1; x < scanX2; x++) {

                    getPixelColor(x, y, true, myImage);
                }
            }
        } else {
            //remove background
            BufferedImage edgeImage = null;
            try {
                edgeImage = ImageIO.read(new File(Main.projectPath + "output\\" + Main.imageName + "\\noback\\" + object + "_" + count + ".png"));

                //use background removal
                for (int y = 0; y < edgeImage.getHeight(); y++) {
                    for (int x = 0; x < edgeImage.getWidth(); x++) {
                        getPixelColor(x, y, false, edgeImage);
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println(e);
            }
        }
//        System.out.println("------End of Color Analysis-----");
    }

    public void getPixelColor(int x, int y, boolean full, BufferedImage myImage) {
        int clr = myImage.getRGB(x, y);
        //determine the what color was found
        float hsb[] = new float[3];
        int r = (clr >> 16) & 0xFF;
        int g = (clr >> 8) & 0xFF;
        int b = (clr) & 0xFF;
        Color.RGBtoHSB(r, g, b, hsb);

        if (hsb[1] < 0.1 && hsb[2] > 0.9) {
            white.inc();
        } else if (hsb[2] < 0.1) {
            this.black.inc();
        } else {
            float deg = hsb[0] * 360;
            if (full) {
                if (deg >= 0 && deg < 30) {
                    this.red.inc();
                } else if (deg >= 30 && deg < 45) {
                    this.orangeColor.inc();
                } else if (deg >= 30 && deg < 90) {
                    this.yellow.inc();
                } else if (deg >= 90 && deg < 150) {
                    this.green.inc();
                } else if (deg >= 150 && deg < 210) {
                    this.cyan.inc();
                } else if (deg >= 210 && deg < 270) {
                    this.blue.inc();
                } else if (deg >= 270 && deg < 330) {
                    this.magenta.inc();
                } else {
                    this.red.inc();
                }

            } else {
                //part images
                if (deg >= 0 && deg < 30) {
                    this.redObjs.inc();
                } else if (deg >= 30 && deg < 45) {
                    this.orangeObjs.inc();
                } else if (deg >= 30 && deg < 90) {
                    this.yellowObjs.inc();
                } else if (deg >= 90 && deg < 150) {
                    this.greenObjs.inc();
                    ;
                } else if (deg >= 150 && deg < 210) {
                    this.cyanObjs.inc();
                    ;
                } else if (deg >= 210 && deg < 270) {
                    this.blueObjs.inc();
                    ;
                } else if (deg >= 270 && deg < 330) {
                    this.magentaObjs.inc();
                    ;
                } else {
                    this.redObjs.inc();
                }
            }
        }
    }

    public String largestOutOfThree(double n1, double n2, double n3) {
        if (n1 >= n2 && n1 >= n3) {
            return "n1";
        } else if (n2 >= n1 && n2 >= n3) {
            return "n2";
        } else {
            return "n3";
        }
    }

    public String getTonality() {
        if (tonality) {
            return "major";
        } else {
            return "minor";
        }
    }

    public void setTonality(boolean tonality) {
        this.tonality = tonality;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public Boolean[] getMood() {
        return mood;
    }

    public String getScene() {
        return scene;
    }


}
