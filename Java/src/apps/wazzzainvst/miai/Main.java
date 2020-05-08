package apps.wazzzainvst.miai;

import apps.wazzzainvst.miai.objects.Instrument;
import apps.wazzzainvst.miai.objects.Theme;
import apps.wazzzainvst.miai.utilities.Composer;
import apps.wazzzainvst.miai.utilities.ImageAnalysis;
import apps.wazzzainvst.miai.utilities.Player;
import apps.wazzzainvst.miai.windows.Window_Analysis;
import apps.wazzzainvst.miai.windows.Window_RemoteImageAnalysis;
import apps.wazzzainvst.miai.windows.Window_ViewObjects;
import apps.wazzzainvst.miai.windows.Window_ViewRawData;
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
import java.util.*;
import java.util.Timer;
/**
 * Class for initialisation of application and UI
 */
public class Main {

    public static String Path;
    public static String projectPath;
    private static String javaPath;
    public static String pythonPath;
    //tempo

    private static String[] file;
    private static BufferedImage myImage;
    private static Player player;
    private static int timeInterval = 500;
    private static int beats = 32 + 1;
    private static Composer composer;
    private static int index = 0;
    private static ImageAnalysis imageAnalysis;
    private static JLabel infoLabel;
    private static JLabel moodLabel;
    private static JLabel domColor;
    private static JLabel domObjColor;

    private static JFrame myJFrame;
    public static String imageName;
    private static Window_ViewRawData data;
    private static Window_ViewObjects objects;

    private static int obj_x = 760;
    private static int obj_y = 0;
    private static int data_x = 760;
    private static int data_y = 400;
    private static boolean startData = true;
    private static boolean startObj = true;
    private static JButton analyzeImagesButton;
    private static Theme theme;

    public static void main(String[] args) throws IOException, URISyntaxException {
        //theming for UI
        theme = new Theme();
        //set project directories
        File syeDir = new File(System.getProperty("user.dir"));
        Path  = new File(syeDir.getParent()).getAbsolutePath()+"\\";
        projectPath = Path + "Python\\ImageAI\\";
        javaPath = Path + "Java\\";
        pythonPath =  Path+"Python\\Python\\Python36\\python.exe";
        player = new Player();
        myImage = null;
        infoLabel = new JLabel("loading...", SwingConstants.LEFT);
        domColor = new JLabel("loading...", SwingConstants.LEFT);
        domObjColor = new JLabel("loading...", SwingConstants.LEFT);
        moodLabel = new JLabel("loading...", SwingConstants.LEFT);
        analyse(0);
    }
    private static ArrayList <String>listDir() {
        File directory = new File(projectPath + "input");
        //iterate through all the files in current dir and print them
        File[] files = new File(directory.getAbsolutePath()).listFiles();
        System.out.println("List dir: " + directory.getAbsolutePath());
        ArrayList<String> list = new ArrayList<>();
        if (files.length == 0) {
            System.out.println("Empty Directory");
        } else {
            for (File file : files) {
                if (file.getName().contains("jpg") || file.getName().contains("png"))
                    list.add(file.getName().replace(".jpg", ""));
            }
        }
        return list;
    }

    public static void analyse(int index) throws IOException, URISyntaxException {
        try {
            //images to scan
            ArrayList<String> list = listDir();
            file = new String[list.size()];
            for (int j = 0; j < list.size(); j++) {
                // Assign each value to String array
                file[j] = list.get(j);
            }
            infoLabel = new JLabel("loading...", SwingConstants.LEFT);
            domColor = new JLabel("loading...", SwingConstants.LEFT);
            domObjColor = new JLabel("No Objects Found!", SwingConstants.LEFT);
            myImage = ImageIO.read(new File(projectPath + "output\\" + file[index] + ".jpg"));
            imageName = file[index];

            //make sure the analysis is of the original image not the output
            BufferedImage myImageInput = ImageIO.read(new File(projectPath + "input\\" + file[index] + ".jpg"));

            Instrument lead = new Instrument("lead", "6", "4", 0.7, 0);
            Instrument bass = new Instrument("bass", "3", "2", 0.5, 0);
            Instrument saw = new Instrument("saw", "4", "3", 0, 0);
            Instrument chord = new Instrument("chord", "5", "4", 0.1, 0);

            //start image analysis
            imageAnalysis = new ImageAnalysis(projectPath + "\\data\\" + file[index] + ".data", myImageInput, infoLabel, lead, bass, saw,chord, player, domColor, domObjColor, javaPath,moodLabel);
            imageAnalysis.analyse(myImage);

            createUI( myImage);

            Timer timer = new Timer();

            int begin = 0;
            composer = new Composer(lead, bass, saw,chord, timer, begin, timeInterval, beats, imageAnalysis, player);
            composer.start();
        } catch (IOException e) {
            // System.out.println(file[index] + "\n\n" + e);
            //restartApplication();
            JFrame frame = new JFrame("File not analyzed!");
            JPanel panel = new JPanel();

            JLabel label = new JLabel("This image has not yet been analyzed! Please run the image analysis!");
            panel.add(label);
            analyzeImagesButton = new JButton("<html>Analyze<br>Images");
            analyzeImagesButton.setBackground(theme.getButtonColor());
            analyzeImagesButton.setBorder(theme.getButton());
            analyzeImagesButton.setForeground(theme.getTextColor());
            analyzeImagesButton.addActionListener(ee -> {
                Window_Analysis analysis = new Window_Analysis(myJFrame);
            });


            panel.add(analyzeImagesButton );

            panel.setBackground(theme.getMainBack());
            label.setForeground(theme.getTextColor());
            frame.setSize(500, 200);
            //  loadingFrame.add(panel);
            frame.setContentPane(panel);
            frame.setVisible(true);


        }

    }

    public static void play() {

        composer.play();
    }

    public static void restartApplication() throws IOException, URISyntaxException {
        analyse(0);
    }


    private static void createUI(BufferedImage myImage) {

        myJFrame = new JFrame("Musical Image AI");
        myJFrame.setSize(1000, 790);

        JPanel jPanel = new JPanel();
        //draw toolbar
        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");
         analyzeImagesButton = new JButton("<html>Analyze<br>Images");
        JButton viewObjectsButton = new JButton("<html>View<br>Objects");
        JButton viewDataButton = new JButton("<html>View<br>Data");
        JButton remoteAnalysisButton = new JButton("<html>Remote<br>Image");
        remoteAnalysisButton.addActionListener(ee -> {
            Window_RemoteImageAnalysis analysis = new Window_RemoteImageAnalysis(myJFrame);
        });

        JLabel label = new JLabel("Image: ", SwingConstants.LEFT);
        JPanel imageSelectorPanel = new JPanel();
        JPanel actionPanel = new JPanel();
        JComboBox selector = new JComboBox(file);
        JPanel colorsPanel = new JPanel();
        actionPanel.setSize(myJFrame.getWidth(), 10);

        JPanel statusPanel = new JPanel();
        statusPanel.setSize(myJFrame.getWidth(), 10);
        try {
            File imgFolder = new File(projectPath + "output\\" + file[index] + "\\noback");
            try {
                objects = new Window_ViewObjects(myJFrame, imgFolder, obj_x, obj_y);
                data = new Window_ViewRawData(myJFrame, imageAnalysis.ObjectsFoundFromDetections, imageAnalysis, file[index], data_x, data_y);
            } catch (Exception ignored) {

            }


            imageSelectorPanel.setLayout(new BoxLayout(imageSelectorPanel, BoxLayout.X_AXIS));
            imageSelectorPanel.add(label);




            selector.setSelectedIndex(index);
            // get the selected item:
            selector.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedBook = (String) selector.getSelectedItem();
                    index = selector.getSelectedIndex();
                    try {
                        try { //check if its playing
                            composer.stop();
                        } catch (Exception ignored) {
                        }
                        myJFrame.dispose();
                        analyse(index);
                        try {
                            if (!startObj && imageAnalysis.ObjectsFoundFromDetections.size() != 0) {
                                objects.dispose();
                                objects.show();
                                obj_x = objects.loadingFrame.getX();
                                obj_y = objects.loadingFrame.getY();
                            }
                        } catch (Exception ee) {
                            throw ee;
                        }

                        try {
                            // data.dispose();
                            if (!startData && imageAnalysis.ObjectsFoundFromDetections.size() != 0) {
                                data.show();
                                data_x = data.loadingFrame.getX();
                                data_x = data.loadingFrame.getY();
                            }
                        } catch (Exception eee) {
                            throw eee;
                        }
                    } catch (Exception ee) {
                        try {
                            throw ee;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (URISyntaxException ex) {
                            ex.printStackTrace();
                        }
                    }
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

            analyzeImagesButton.addActionListener(e -> {
                Window_Analysis analysis = new Window_Analysis(myJFrame);
            });

            viewObjectsButton.addActionListener(e -> {
                if (imageAnalysis.ObjectsFoundFromDetections.size() != 0) {
                    objects.show();
                    startObj = false;
                    obj_x = objects.loadingFrame.getX();
                    obj_y = objects.loadingFrame.getY();
                }
            });

            viewDataButton.addActionListener(e -> {
                if (imageAnalysis.ObjectsFoundFromDetections.size() != 0) {
                    data.show();
                    startData = false;
                    data_x = data.loadingFrame.getX();
                    data_x = data.loadingFrame.getY();
                }
            });
            imageSelectorPanel.add(selector);

            actionPanel.add(playButton);
            actionPanel.add(stopButton);
            actionPanel.add(analyzeImagesButton);
            actionPanel.add(viewObjectsButton);
            actionPanel.add(viewDataButton);
            actionPanel.add(remoteAnalysisButton );
            statusPanel.add(infoLabel);

            colorsPanel.setLayout(new BoxLayout(colorsPanel,BoxLayout.Y_AXIS));
            colorsPanel.add(domColor);
            colorsPanel.add(domObjColor);

            statusPanel.add(colorsPanel);
            statusPanel.add(moodLabel);
            //jPanel.add(imageSelectorPanel);
            jPanel.add(actionPanel);
            jPanel.add(statusPanel);

        } catch (Exception ignored) {
        }

        //draw image
        Border padding = BorderFactory.createEmptyBorder(5, 5, 5, 25);
        jPanel.setBorder(padding);
        JPanel main = new JPanel();
        main.setSize(myJFrame.getWidth(), myJFrame.getHeight());

        JPanel image = new JPanel();
        image.setAutoscrolls(true);

        JLabel pic = new JLabel();
        Image dimg = myImage.getScaledInstance(myJFrame.getWidth()-50, 430, Image.SCALE_SMOOTH);

        pic.setIcon(new ImageIcon(dimg));
        image.add(pic);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        JPanel imagePanel = new JPanel();
        try {
            BufferedImage logo = ImageIO.read(new File(projectPath + "logo.png"));
            Image dimgLogo = logo.getScaledInstance(150, 50, Image.SCALE_SMOOTH);
            JLabel imageHolder = new JLabel (new ImageIcon(dimgLogo));
            imageHolder.setSize(myJFrame.getWidth(),50);
            imagePanel.setLayout(new GridLayout(2,1));
            imagePanel.setPreferredSize(imageHolder.getPreferredSize());
            //imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
            imagePanel.add(imageHolder);

            imagePanel.add(imageSelectorPanel);
            main.add( imagePanel);
        }catch (Exception e){

        }


        main.add(jPanel);
        main.add(image);

        //theme formatting


        main.setBackground(theme.getMainBack());
        image.setBackground(theme.getSecondaryBack());
        imagePanel.setBackground(theme.getMainBack());
        actionPanel.setBackground(theme.getMainBack());
        statusPanel.setBackground(theme.getButtonBorderColor());
        statusPanel.setBorder(theme.getRounded());
        colorsPanel.setBackground(theme.getButtonBorderColor());
        colorsPanel.setForeground(theme.getTextColor2());

        jPanel.setBackground(theme.getMainBack());
        playButton.setBackground(theme.getButtonColor());
        playButton.setBorder(Theme.getSingleLineButton());
        playButton.setForeground(theme.getTextColor());

        stopButton.setBackground(theme.getButtonColor());
        stopButton.setBorder(theme.getSingleLineButton());
        stopButton.setForeground(theme.getTextColor());

        viewDataButton.setBackground(theme.getButtonColor());
        viewDataButton.setBorder(theme.getButton());
        viewDataButton.setForeground(theme.getTextColor());

        viewObjectsButton.setBackground(theme.getButtonColor());
        viewObjectsButton.setBorder(theme.getButton());
        viewObjectsButton.setForeground(theme.getTextColor());

        analyzeImagesButton.setBackground(theme.getButtonColor());
        analyzeImagesButton.setBorder(theme.getButton());
        analyzeImagesButton.setForeground(theme.getTextColor());

        remoteAnalysisButton.setBackground(theme.getButtonColor());
        remoteAnalysisButton.setBorder(theme.getButton());
        remoteAnalysisButton.setForeground(theme.getTextColor());


        label.setBackground(theme.getButtonColor());
        label.setForeground(theme.getTextColor());

        selector.setBackground(theme.getButtonColor());
        selector.setForeground(theme.getTextColor());


        imageSelectorPanel.setBackground(theme.getButtonColor());
        imageSelectorPanel.setBorder(theme.getButton());
        imageSelectorPanel.setForeground(theme.getTextColor());

        infoLabel.setForeground(theme.getTextColor2());
        domColor.setForeground(theme.getTextColor2());
        domObjColor.setForeground(theme.getTextColor2());

        moodLabel.setForeground(theme.getTextColor2());

        myJFrame.setContentPane(main);
        myJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myJFrame.setVisible(true);
    }
}
