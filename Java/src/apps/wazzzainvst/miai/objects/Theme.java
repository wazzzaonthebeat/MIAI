package apps.wazzzainvst.miai.objects;
/**
 * Class object responsible for theme of the UI
 */
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Theme {
    public   Color mainBack = new Color(63,63,63);
    public   Color secondaryBack = new Color(88, 88, 88);
    public   Color buttonColor = new Color(38,38,38);
    public  static Color buttonBorderColor = new Color(94, 96, 96);
    public   Color textColor = new Color(187, 187, 187);

    public  Color getTextColor2() {
        return textColor2;
    }

    public   Color textColor2 = new Color(255, 255, 255);

    public  static Border paddingBorder = BorderFactory.createEmptyBorder(18, 20, 18, 20);
    public static  Border paddingBorderDouble = BorderFactory.createEmptyBorder(10, 20, 10, 20);
    public  static LineBorder rounded = new LineBorder(buttonBorderColor, 1, true);
    public  static Border singleLineButton = BorderFactory.createCompoundBorder(rounded,paddingBorder);
    public  static Border button = BorderFactory.createCompoundBorder(rounded,paddingBorderDouble);

    public  Color getMainBack() {
        return mainBack;
    }

    public  Color getSecondaryBack() {
        return secondaryBack;
    }

    public  Color getButtonColor() {
        return buttonColor;
    }

    public  Color getButtonBorderColor() {
        return buttonBorderColor;
    }

    public  Color getTextColor() {
        return textColor;
    }

    public  Border getPaddingBorder() {
        return paddingBorder;
    }

    public  Border getPaddingBorderDouble() {
        return paddingBorderDouble;
    }

    public  LineBorder getRounded() {
        return rounded;
    }

    public static Border getSingleLineButton() {
        return singleLineButton;
    }

    public static Border getButton() {
        return button;
    }

    public Theme (){

    }
}
