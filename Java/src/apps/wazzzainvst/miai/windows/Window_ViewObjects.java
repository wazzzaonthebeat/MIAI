package apps.wazzzainvst.miai.windows;

import apps.wazzzainvst.miai.Main;
import apps.wazzzainvst.miai.objects.Theme;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 * Class responsible for displaying individual detected object images from image analysis results
 */
public class Window_ViewObjects {
    public JFrame loadingFrame;
    public void dispose(){
        loadingFrame.dispose();
    }
    private Thread thread;
    public static void main (String [] args){
        File directory = new File (Main.projectPath + "output\\");
        File[] files = {};
        Window_ViewObjects window_ViewObjects;
        try{
           files = new File(directory.getAbsolutePath()).listFiles();
            window_ViewObjects = new Window_ViewObjects(new JFrame(),new File(files[0].getAbsolutePath()+"\\noback"),0,0);
            window_ViewObjects.show();
        }catch (Exception e){
          }

    }
    public Window_ViewObjects(JFrame myJFrame, File imgFolder, int x, int y){

         loadingFrame = new JFrame("View Detected Objects");
         loadingFrame.setLocation(x,y);
        JLabel content = new JLabel("<html>Objects Detected:<br>");
        JButton done = new JButton("Close");
        done.setSize(new Dimension(400, 100));
        done.setVisible(false);

        JPanel images = new JPanel(new GridLayout((imgFolder.listFiles().length),1));
        //get all objects detected images
        //Theming
        Theme theme = new Theme();

        for (File file: imgFolder.listFiles()){
            if ( file.getName().contains("png")){
                try {

                    BufferedImage image = ImageIO.read(new File(file.getAbsolutePath()));

                    JLabel jImage = new JLabel();
                    Image dimg = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);

                    jImage.setIcon(new ImageIcon(dimg));

                    JPanel imageAndCaption = new JPanel();
                    imageAndCaption.setLayout(new BoxLayout(imageAndCaption, BoxLayout.Y_AXIS));
                    JLabel filename = new JLabel(file.getName());
                    filename.setForeground(theme.getTextColor());
                    imageAndCaption.add(filename);

                    imageAndCaption.add(jImage);
                    imageAndCaption.add(new JLabel("<html><br>"));
                    imageAndCaption.setBorder(theme.getPaddingBorder());
                    imageAndCaption.setForeground(theme.getTextColor());
                    imageAndCaption.setBackground(theme.getSecondaryBack());
                    jImage.setBackground(theme.getSecondaryBack());

                    images.add(imageAndCaption);
                    //images.add();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

         thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                JScrollPane scrollFrame = new JScrollPane(images);
                panel.setAutoscrolls(true);

                content.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(scrollFrame);
                panel.add(done);



                panel.setBackground(theme.getMainBack());
                loadingFrame.setBackground(theme.getMainBack());
                loadingFrame.repaint();

                loadingFrame.setSize(600, 400);
                loadingFrame.setContentPane(panel);
                loadingFrame.setVisible(true);
            }
        });
    }

    public void show() {
        thread.run();
    }
}
