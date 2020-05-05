package apps.wazzzainvst.miai.windows;

import apps.wazzzainvst.miai.Main;
import apps.wazzzainvst.miai.objects.MyColor;
import apps.wazzzainvst.miai.objects.Theme;
import apps.wazzzainvst.miai.utilities.ImageAnalysis;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
/**
 * Moemedi Wazzza Rakhudu
 * Class responsible for displaying raw from image analysis results
 */
public class Window_ViewRawData {

    private MyColor red;
    private MyColor green;
    private MyColor blue;
    private MyColor magenta;
    private MyColor cyan;
    private MyColor yellow;
    private MyColor black;
    private MyColor white;
    private MyColor orangeColor;
    public JFrame loadingFrame;
    private Thread thread;

    public void dispose(){
        loadingFrame.dispose();
    }
    public Window_ViewRawData(JFrame myJFrame, ArrayList<String[]> objectFound, ImageAnalysis imageAnalysis, String imageFile, int x, int y) {

        red = new MyColor("red",0.0);
        blue = new MyColor("blue",0.0);
        green = new MyColor("green",0.0);
        black = new MyColor("black",0.0);
        white = new MyColor("white",0.0);
        yellow = new MyColor("yellow",0.0);
        cyan = new MyColor("cyan",0.0);
        magenta = new MyColor("magenta",0.0);
        orangeColor = new MyColor("orange",0.0);
        if (objectFound != null) {
         loadingFrame = new JFrame("View Detected Objects - "+imageFile+".jpg");
        loadingFrame.setLocation(x,y);
        JLabel content = new JLabel("<html>Objects Detected:<br>");
        JButton done = new JButton("Close");
        done.setSize(new Dimension(400, 100));
        done.setVisible(false);


            JPanel data = new JPanel(new GridLayout(objectFound.size(), 1));
            //get all objects detected images
            int count = 0;
            for (String[] obj : objectFound) {


                JLabel jImage = new JLabel();

                JPanel imageData = new JPanel();
                imageData.setLayout(new BoxLayout(imageData, BoxLayout.Y_AXIS));

                imageData.add(new JLabel("Object: " + obj[0] + " - " + count));
                imageData.add(new JLabel("  Probability: " + obj[1]));
                imageData.add(new JLabel("  Location: [" + obj[2] + "," + obj[3] + "," + obj[4] + "," + obj[5] + "]"));
                imageData.add(new JLabel("  Category: " + imageAnalysis.Objects.get(obj[0]).getCategory()));
                imageData.add(new JLabel("  Dominant Color: " + getDomColor(imageFile, Integer.valueOf(obj[2].trim()), Integer.valueOf(obj[3].trim()), Integer.valueOf(obj[4].trim()), Integer.valueOf(obj[5].trim()), imageAnalysis, count, obj[0])));
                imageData.add(new JLabel("  Mobility: " + imageAnalysis.Objects.get(obj[0]).isMobile()));

                imageData.add(new JLabel("<html>"));
                data.add(imageData);
                //images.add();
                count++;
                Theme theme = new Theme();

            }
            JButton openButton = new JButton("Add custom model");
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

            thread = new Thread(new Runnable() {
                @Override
                public void run() {



                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    //panel.add(images);

                    JScrollPane scrollFrame = new JScrollPane(data);
                    panel.setAutoscrolls(true);
                    //scrollFrame.setPreferredSize();
                    // this.add(content);
//Theming

                    content.setHorizontalAlignment(SwingConstants.CENTER);

                    panel.add(scrollFrame);

                    loadingFrame.setSize(600, 400);
                    //  loadingFrame.add(panel);
                    loadingFrame.setContentPane(panel);
                    loadingFrame.setVisible(true);
                }
            });


        }

    }

    private String getDomColor(String imageFile, Integer x1, Integer y1, Integer x2, Integer y2, ImageAnalysis imageAnalysis, int count,String obj) {

        try {
         //   System.out.println("filename: "+Main.projectPath + "output\\" +imageFile+"\\noback\\"+obj+"_"+count+".png");
            BufferedImage image = ImageIO.read(new File(Main.projectPath + "output\\" +imageFile+"\\noback\\"+obj+"_"+count+".png"));
             for (int y = 0; y < image.getHeight() ; y++) {

                for (int x = 0; x < image.getWidth(); x++) {
                   // System.out.println("check color");
                    getPixelColor(x,y,false,image);
                }
            }
             MyColor [] myColors = new MyColor[] {red,green,black,blue,magenta,cyan,yellow,white,orangeColor};
            double max = myColors[0].getValue();
            MyColor maxColor = new MyColor(myColors[0].getName(),0.0);
            for(MyColor color: myColors)
            {
                MyColor current = new MyColor(color.getName(),0.0);
                double value = color.getValue();

                if(max < value){
                     max = value;
                    maxColor = current;
                }else{
                  }

            }

            return maxColor.getName();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return "no color";
    }

    public void getPixelColor(int x, int y, boolean full, BufferedImage myImage) {


        int clr = myImage.getRGB(x, y);
        //determine the what color was found
        float hsb[] = new float[3];
        int r = (clr >> 16) & 0xFF;
        int g = (clr >>  8) & 0xFF;
        int b = (clr      ) & 0xFF;
        Color.RGBtoHSB(r, g, b, hsb);

        if      (hsb[1] < 0.1 && hsb[2] > 0.9) {white.inc();;}
        else if (hsb[2] < 0.1) {this.black.inc();}
        else {
            float deg = hsb[0]*360;

                if (deg >= 0 && deg < 30) {
                    this.red.inc(); //System.out.println("red");
                } else if (deg >= 30 && deg < 45) {
                    this.orangeColor.inc();//System.out.println("orangeColor");
                } else if (deg >= 30 && deg < 90) {
                    this.yellow.inc();//System.out.println("yellow");
                } else if (deg >= 90 && deg < 150) {
                    this.green.inc();//System.out.println("green");
                } else if (deg >= 150 && deg < 210) {
                    this.cyan.inc();//System.out.println("cyan");
                } else if (deg >= 210 && deg < 270) {
                    this.blue.inc();//.out.println("blue");
                } else if (deg >= 270 && deg < 330) {
                    this.magenta.inc();///System.out.println("magenta");
                } else {
                    this.red.inc();//System.out.println("red 2");
                }
        }
    }

    public void show() {
        thread.run();

    }
}
