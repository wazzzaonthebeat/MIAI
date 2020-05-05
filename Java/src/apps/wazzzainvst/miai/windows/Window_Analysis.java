package apps.wazzzainvst.miai.windows;

import apps.wazzzainvst.miai.Main;
import apps.wazzzainvst.miai.objects.Theme;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
/**
 * Class responsible for image analysis with Python Image AI and UI
 */
public class Window_Analysis {
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
    private  JScrollPane scrollFrame;

    public static void main(String[] args) {
        Window_Analysis window_analysis = new Window_Analysis(new JFrame());
    }

    public Window_Analysis(JFrame myJFrame) {

        loadingFrame = new JFrame("Analyze Images");
        content = new JLabel();
        JButton done = new JButton("Close");
        done.setSize(new Dimension(400, 100));
        done.setVisible(false);
        JLabel label1 = new JLabel("<html><br>Image Source Directory: " + Main.projectPath + "\\input<br>");
        startAnalysis = new JButton("Start Analysis");
        JButton inputButton = new JButton("Open Input Folder");
        JButton outputButton = new JButton("Open Output Folder");
        JButton modelButton = new JButton("Open Model Folder");
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
                fileChooser.setCurrentDirectory(new File(Main.projectPath + "/model"));
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

                if (!running) {
                    analysisThread[0] = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                running = true;
                                status.setText("Status: Running - Please Wait...");
                                startAnalysis.setText("Stop Analysis");
                                if (custom[0]) {
                                    //check if file entered and prob
                                    if (customModel.equals("")) {
                                        //please select model
                                        content.setText("Invalid Custom Model, Please click on \"Browse\" and select a valid custom model!");
                                        running = false;
                                        status.setText("Status: Not Running");
                                        System.out.println("Stopping AI");
                                        startAnalysis.setText("Start Analysis");

                                    } else {
                                        //check pro
                                        System.out.println("Checking Probability");
                                        if (customProb.getText().length() > 0 && (Integer.parseInt(customProb.getText()) > 0 && Integer.parseInt(customProb.getText()) <= 100)) {

                                            content.setText("<html>Initializing Tensor Flow AI - With Custom Objects<br><br>Please Wait, This may take a few minutes...");


                                            runPython(done, label1, true);
                                        } else {
                                            //error invalid prob

                                            content.setText("Invalid Probability, Please enter a valid detection confidence level minimum between 1 and 100 Percent!");
                                            status.setText("Status: Not Running");
                                            System.out.println("Stopping AI");
                                            startAnalysis.setText("Start Analysis");
                                            running = false;

                                        }
                                    }

                                } else {
                                    content.setText("<html>Initializing Tensor Flow AI<br><br>Please Wait, This may take a few minutes...");
                                    runPython(done, label1, false);
                                }
                            } catch (IOException ex) {
                                status.setText("Status: Not Running");
                                System.out.println("Stopping AI");
                                ex.printStackTrace();
                                running = false;
                                startAnalysis.setText("Start Analysis");
                            }

                        }
                    });
                    analysisThread[0].start();
                } else {
                    process.destroy();
                    analysisThread[0].interrupt();

                    running = false;
                    content.setText("");
                    status.setText("Status: Not Running");
                    startAnalysis.setText("Start Analysis");
                }
            }
        });
        inputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFolder("input");
            }
        });
        outputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFolder("output");
            }
        });
        modelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFolder("model");
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
        buttonPanel.add(customLabel);
        buttonPanel.add(customCheckBox);
        buttonPanel.add(inputButton);
        buttonPanel.add(outputButton);
        buttonPanel.add(modelButton);
        panel.add(buttonPanel);
        JPanel statusPanel= new JPanel(new GridLayout());
        statusPanel.add(status);
        panel.add(statusPanel);
        panel.add(filePicker);

        scrollFrame = new JScrollPane(content);
        scrollFrame.setPreferredSize(new Dimension(800, 600));

        content.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(scrollFrame);
        JPanel donePanel = new JPanel(new GridLayout());
        donePanel.add(startAnalysis);
        donePanel.add(done);
        panel.add(donePanel);
        done.setVisible(true);
        loadingFrame.setSize(800, 600);
        //  loadingFrame.add(panel);

        //Theming
        Theme theme = new Theme();

        panel.setBackground(theme.getMainBack());
        customCheckBox.setBackground(theme.getMainBack());

        topLabel.setBackground(theme.getMainBack());
        scrollFrame.setBackground(theme.getSecondaryBack());
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

        inputButton.setBackground(theme.getButtonColor());
        inputButton.setForeground(theme.getTextColor());
        inputButton.setBorder(Theme.getButton());

        outputButton.setBackground(theme.getButtonColor());
        outputButton.setForeground(theme.getTextColor());
        outputButton.setBorder(Theme.getButton());

        modelButton.setBackground(theme.getButtonColor());
        modelButton.setForeground(theme.getTextColor());
        modelButton.setBorder(Theme.getButton());

        browseModel.setBackground(theme.getButtonColor());
        browseModel.setForeground(theme.getTextColor());
        browseModel.setBorder(Theme.getButton());



        loadingFrame.setContentPane(panel);
        loadingFrame.setVisible(true);

    }

    private void openFolder(String folder) {

        try {
            System.out.println("launching: " + Main.projectPath + "" + folder);
            Runtime.getRuntime().exec("explorer.exe /open," + Main.projectPath + "" + folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runPython(JButton done, JLabel label, boolean custom) throws IOException {

        System.out.println(" Initializing AI");
        String model = "";
        String customObj = "";
        String prob = "";
        if (custom) {
            model = customModel; //name of model to use
            customObj = customObjFolder; //name of custom object
            prob = customProb.getText();
        }
        String program = "\"" + Main.pythonPath + "\" \"" + Main.projectPath + "detectorToImage.py\" " + customObj + " " + model + " " + prob + "\"";

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
                    JScrollBar sb = scrollFrame.getVerticalScrollBar();
                    sb.setValue(sb.getMaximum());

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
        }catch (Exception e){
          JFrame error =  new JFrame("File not found!");
          error.setContentPane((Container) new JPanel().add(new JLabel("<html>File Not Found!" +
                  "<br><br> Please check your Python Path or Project Directory!" +
                  "<br><br>Error: "+e.getMessage())));
          error.setSize(500,200);
          error.setLocation(300,300);
          error.setVisible(true);
            status.setText("Status: Not Running");
            running = false;
            startAnalysis.setText("Start Analysis");
            content.setText("");
        }

        running = false;
        startAnalysis.setText("Start Analysis");



    }

}
