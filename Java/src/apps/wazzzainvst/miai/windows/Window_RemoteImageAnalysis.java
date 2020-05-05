package apps.wazzzainvst.miai.windows;

import apps.wazzzainvst.miai.Main;
import apps.wazzzainvst.miai.objects.Instrument;
import apps.wazzzainvst.miai.objects.Theme;
import apps.wazzzainvst.miai.utilities.Composer;
import apps.wazzzainvst.miai.utilities.ImageAnalysis;
import apps.wazzzainvst.miai.utilities.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;

public class Window_RemoteImageAnalysis {
    private JLabel status;
    private JLabel content;
    private JPanel filePicker;
    private boolean running = false;
    private JButton startAnalysis;
    private Process process;
    private String customModel = "";
    private String customObjFolder = ""; //name of custom object
    private JTextField customProb;
    private JFrame loadingFrame;
    private BufferedImage myImage;
    private JPanel imageFrame;
    private String selectedImagefilePath;
    private JLabel pic;
    public static String Path;
    String projectPath;
    String pythonPath;
    JFrame myJFrame;
    private static int timeInterval = 500;
    private static int beats = 32 + 1;
    private static Window_ViewRawData data;
    private static Window_ViewObjects objects;
    private static int obj_x = 760;
    private static int obj_y = 0;
    private static int data_x = 760;
    private static int data_y = 400;
    private static boolean startData = true;
    private static boolean startObj = true;
    private boolean objectsFound;
    private Player player;
    private ImageAnalysis imageAnalysis;
    private String[] file;
    private String javaPath;
    private Composer composer;

    public static void main(String[] args) {

        Window_RemoteImageAnalysis window_analysis = new Window_RemoteImageAnalysis(new JFrame());
    }

    public Window_RemoteImageAnalysis(JFrame myJFrame) {


        File syeDir = new File(System.getProperty("user.dir"));
        Path = new File(syeDir.getParent()).getAbsolutePath() + "\\";
        projectPath = Path + "Python\\ImageAI\\";
        pythonPath = Path + "Python\\Python\\Python36\\python.exe";
        javaPath = Path + "Java\\";
        player = new Player();
        this.myJFrame = myJFrame;
        ;
        try {
            myImage = ImageIO.read(new File(projectPath + "holder.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadingFrame = new JFrame("Analyze Remote Images");
        content = new JLabel();
        JButton done = new JButton("Close");
        done.setSize(new Dimension(400, 100));
        done.setVisible(false);
        JLabel label1 = new JLabel("<html><br>Image Source Directory: " + projectPath + "\\input<br>");
        startAnalysis = new JButton("Start Analysis");
        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");
        JButton replayButton = new JButton("Replay");
        JButton selectImageButton = new JButton("Download Remote Image");

        JButton viewObjectsButton = new JButton("<html>View<br>Objects");
        JButton viewDataButton = new JButton("<html>View<br>Data");



        status = new JLabel("Status: Not Running");
        filePicker = new JPanel();
        final JLabel fileSelected = new JLabel("Custom Model: None Selected");
        final JLabel probLabel = new JLabel("Detection Confidence: ");
        JButton browseModel = new JButton("Browse");
        customProb = new JTextField();
        customProb.setText("90");
        customProb.setColumns(3);
        customProb.createToolTip();
        customProb.setToolTipText("Enter a number between 0 and 100");

        filePicker.add(fileSelected);
        filePicker.add(browseModel);
        filePicker.add(probLabel);
        filePicker.add(customProb);

        JFrame filePickWindow = new JFrame("Choose Model");
        JCheckBox customCheckBox = new JCheckBox();
        browseModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.addChoosableFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {

                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return null;
                    }
                });
                fileChooser.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println(e.toString());
                        System.out.println(e.getID());
                        if (e.getID() == 1001) {
                            //cancel
                            filePickWindow.dispose();
                        }
                    }
                });
                fileChooser.setCurrentDirectory(new File(projectPath + "/model"));
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.getName().endsWith(".h5") || f.isDirectory()) {
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return "Custom Models (.h5)";
                    }
                });
                int result = fileChooser.showOpenDialog(filePickWindow);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected Model: " + selectedFile.getName());
                    fileSelected.setText("Custom Model: " + selectedFile.getName());
                    customModel = selectedFile.getName().replace(".h5", "");
                    File parentDir = new File(selectedFile.getParent());
                    customObjFolder = parentDir.getName();
                    System.out.println("Selected Folder: " + customObjFolder);
                    customCheckBox.isSelected();
                }

            }
        });
        final boolean[] custom = {false};
        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadingFrame.dispose();
                myJFrame.dispose();
                try {
                    Main.analyse(0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });


        // fc = new JFileChooser();

        JLabel customLabel = new JLabel("Use Custom Model");
        customCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                custom[0] = customCheckBox.isSelected();

            }
        });
        final Thread[] analysisThread = {new Thread()};
        startAnalysis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Thread runAnalysis = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        status.setText("Status: Running...Please Wait");
                        try {
                            if (custom[0]) {
                                //check if file entered and prob
                                if (customModel.equals("")) {
                                    //please select model
                                    status.setText("Invalid Custom Model, Please click on \"Browse\" and select a valid custom model!");
                                    running = false;
                                    // status.setText("Status: Not Running");
                                    System.out.println("Stopping AI");
                                    startAnalysis.setText("Start Analysis");

                                } else {
                                    //check pro
                                    System.out.println("Checking Probability");
                                    if (customProb.getText().length() > 0 && (Integer.parseInt(customProb.getText()) > 0 && Integer.parseInt(customProb.getText()) <= 100)) {

                                        // content.setText("<html>Initializing Tensor Flow AI - With Custom Objects<br><br>Please Wait, This may take a few minutes...");

                                        objectsFound = false;
                                        runPython(done, label1, true);
                                    } else {
                                        //error invalid prob

                                        status.setText("Invalid Confidence Percentage, Please enter a valid detection confidence percentage between 1 and 100 percent!");
                                        // status.setText("Status: Not Running");
                                        System.out.println("Stopping AI");
                                        startAnalysis.setText("Start Analysis");
                                        running = false;

                                    }
                                }

                            } else {
                                // content.setText("<html>Initializing Tensor Flow AI<br><br>Please Wait, This may take a few minutes...");
                                runPython(done, label1, false);
                                objectsFound = false;
                            }
                        } catch (IOException | URISyntaxException ex) {
                            ex.printStackTrace();
                        }

                    }
                });
                runAnalysis.start();
            }
        });
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //   openFolder("input");
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
        replayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //    openFolder("model");
            }
        });
        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //    openFolder("model");
                try{
                    status.setText("Status: Downloading...Please Wait");
                    selectedImagefilePath = "https://tablemate.online/miai/images/image.jpg";

                    Thread downloadImage = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                myImage = ImageIO.read(new URL(selectedImagefilePath));

                                Image dimg = myImage.getScaledInstance(myJFrame.getWidth() - 50, 430, Image.SCALE_SMOOTH);
                                pic.setIcon(new ImageIcon(dimg));

                                status.setText("Status: Download Complete...Ready To Start Analysis");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                    downloadImage.start();

                }catch (Exception ee){
                    status.setText("Status: Not Running");
                }

            }
        });

        viewObjectsButton.addActionListener(e -> {
            if (objectsFound) {
                objects.show();
                startObj = false;
                obj_x = objects.loadingFrame.getX();
                obj_y = objects.loadingFrame.getY();
            }
        });

        viewDataButton.addActionListener(e -> {
            if (objectsFound) {
                data.show();
                startData = false;
                data_x = data.loadingFrame.getX();
                data_x = data.loadingFrame.getY();
            }
        });


        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel topLabel = new JPanel(new GridLayout());
        topLabel.add(label1);
        panel.add(topLabel);

        JPanel buttonPanel = new JPanel();


        buttonPanel.add(startAnalysis);

        buttonPanel.add(playButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(replayButton);
        buttonPanel.add(selectImageButton);
        buttonPanel.add(customLabel);
        buttonPanel.add(customCheckBox);
        buttonPanel.add(viewObjectsButton);
        buttonPanel.add(viewDataButton);
        panel.add(buttonPanel);
        JPanel statusPanel = new JPanel(new GridLayout());
        statusPanel.add(status);
        panel.add(statusPanel);
        panel.add(filePicker);

        pic = new JLabel();
        Image dimg = myImage.getScaledInstance(myJFrame.getWidth() - 50, 430, Image.SCALE_SMOOTH);
        pic.setIcon(new ImageIcon(dimg));
        imageFrame = new JPanel();
        imageFrame.add(pic);
        //  scrollFrame.setPreferredSize(new Dimension(800, 600));

        content.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(imageFrame);
        JPanel donePanel = new JPanel(new GridLayout());
        donePanel.add(startAnalysis);
        donePanel.add(done);
        panel.add(donePanel);
        done.setVisible(true);
        loadingFrame.setSize(800, 750);
        //  loadingFrame.add(panel);

        //Theming
        Theme theme = new Theme();

        panel.setBackground(theme.getMainBack());
        customCheckBox.setBackground(theme.getMainBack());

        topLabel.setBackground(theme.getMainBack());
        imageFrame.setBackground(theme.getSecondaryBack());
        statusPanel.setBackground(theme.getMainBack());
        filePicker.setBackground(theme.getMainBack());
        buttonPanel.setBackground(theme.getMainBack());

        status.setForeground(theme.getTextColor());
        customLabel.setForeground(theme.getTextColor());
        fileSelected.setForeground(theme.getTextColor());
        probLabel.setForeground(theme.getTextColor());

        label1.setForeground(theme.getTextColor());

        done.setBackground(theme.getButtonColor());
        done.setForeground(theme.getTextColor());
        done.setBorder(Theme.getButton());

        startAnalysis.setBackground(theme.getButtonColor());
        startAnalysis.setForeground(theme.getTextColor());
        startAnalysis.setBorder(Theme.getButton());

        playButton.setBackground(theme.getButtonColor());
        playButton.setForeground(theme.getTextColor());
        playButton.setBorder(Theme.getButton());

        stopButton.setBackground(theme.getButtonColor());
        stopButton.setForeground(theme.getTextColor());
        stopButton.setBorder(Theme.getButton());

        replayButton.setBackground(theme.getButtonColor());
        replayButton.setForeground(theme.getTextColor());
        replayButton.setBorder(Theme.getButton());

        selectImageButton.setBackground(theme.getButtonColor());
        selectImageButton.setForeground(theme.getTextColor());
        selectImageButton.setBorder(Theme.getButton());

        browseModel.setBackground(theme.getButtonColor());
        browseModel.setForeground(theme.getTextColor());
        browseModel.setBorder(Theme.getButton());

        viewObjectsButton.setBackground(theme.getButtonColor());
        viewObjectsButton.setBorder(theme.getButton());
        viewObjectsButton.setForeground(theme.getTextColor());

        viewDataButton.setBackground(theme.getButtonColor());
        viewDataButton.setBorder(theme.getButton());
        viewDataButton.setForeground(theme.getTextColor());

        loadingFrame.setContentPane(panel);
        loadingFrame.setVisible(true);

    }

    private void runPython(JButton done, JLabel label, boolean custom) throws IOException, URISyntaxException {

        System.out.println(" Initializing AI");
        String model = "";
        String customObj = "";
        String prob = "";
        if (custom) {
            model = customModel; //name of model to use
            customObj = customObjFolder; //name of custom object
            prob = customProb.getText();
        }
        String program = "\"" + pythonPath + "\" \"" + projectPath + "detectorToImageRemote.py\" " + customObj + " " + model + " " + prob + " " + selectedImagefilePath + "\"";

        ProcessBuilder pb = new ProcessBuilder(program.trim());
        //  pb.directory(file);
        try {
            process = pb.start();
            System.out.println("Running Command: " + program.trim());
            String line;
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((line = input.readLine()) != null) {
                    System.out.println("detectorImage.py: " + line);
                    String finalLine = line;
                    content.setText("<html>" + content.getText() + "<br><hr><br>" + finalLine);
                    // loadingFrame.repaint();
                    // JScrollBar sb = scrollFrame.getVerticalScrollBar();
                    // sb.setValue(sb.getMaximum());

                    content.repaint();
                }
                //
                input.close();

                //close loading bar
                // loadingFrame.dispose();
                done.setVisible(true);


                status.setText("Status: Complete");
                JPanel finished = new JPanel();
                finished.add(new JLabel("Analysis Complete!"));
                JFrame complete = new JFrame();
                complete.add(finished);
                running = false;
                startAnalysis.setText("Start Analysis");
            } catch (Exception e) {
                new JFrame("File not found!").add(new JLabel("File Not Found!"));
            }

            //close loading bar
            // loadingFrame.dispose();
            done.setVisible(true);


            status.setText("Status: Complete");
            JPanel finished = new JPanel();
            finished.add(new JLabel("Analysis Complete!"));
            JFrame complete = new JFrame();
            complete.add(finished);
            running = false;
            startAnalysis.setText("Start Analysis");
        } catch (Exception e) {
            JFrame error = new JFrame("File not found!");
            error.setContentPane((Container) new JPanel().add(new JLabel("<html>File Not Found!" +
                    "<br><br> Please check your Python Path or Project Directory!" +
                    "<br><br>Error: " + e.getMessage())));
            error.setSize(500, 200);
            error.setLocation(300, 300);
            error.setVisible(true);
            status.setText("Status: Not Running");
            running = false;
            startAnalysis.setText("Start Analysis");
            content.setText("");
        }


        //update image to analysed version

        myImage = ImageIO.read(new File(projectPath + "outputRemote\\"+selectedImagefilePath.replace("https://tablemate.online/miai/images/","")));
        Image dimg = myImage.getScaledInstance(myJFrame.getWidth() - 50, 430, Image.SCALE_SMOOTH);
        pic.setIcon(new ImageIcon(dimg));

        running = false;
        startAnalysis.setText("Start Analysis");
        objectsFound = true;

        analyseImage();
    }
    private  ArrayList <String>listDir() {
        File directory = new File(projectPath + "inputRemote");
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
    private void analyseImage() throws IOException, URISyntaxException {
        ArrayList<String> list = listDir();
        file = new String[list.size()];
        for (int j = 0; j < list.size(); j++) {
            // Assign each value to String array
            file[j] = list.get(j);
        }

        try {
            File imgFolder = new File(projectPath + "outputRemote\\image\\noback");
            objects = new Window_ViewObjects(myJFrame, imgFolder, obj_x, obj_y);
            data = new Window_ViewRawData(myJFrame, imageAnalysis.ObjectsFoundFromDetections, imageAnalysis, file[0], data_x, data_y);
        } catch (Exception ignored) {

        }

        BufferedImage myImageInput = ImageIO.read(new File(projectPath + "inputRemote\\image.jpg"));

        Instrument lead = new Instrument("lead", "6", "4", 0.7, 0);
        Instrument bass = new Instrument("bass", "3", "2", 0.5, 0);
        Instrument saw = new Instrument("saw", "4", "3", 0, 0);
        Instrument chord = new Instrument("chord", "5", "4", 0.1, 0);

        //start image analysis
         imageAnalysis = new ImageAnalysis(projectPath + "\\dataRemote\\image" + ".data", myImageInput, null, lead, bass, saw,chord, player, null, null, javaPath,null);
        imageAnalysis.analyse(myImage);

        java.util.Timer timer = new Timer();

        int begin = 0;
        composer = new Composer(lead, bass, saw,chord, timer, begin, timeInterval, beats, imageAnalysis, player);
        composer.start();
    }


    public  void play() {

        composer.play();
    }

}
