package apps.wazzzainvst.miai.objects;

import java.awt.*;
/**
 * Class object responsible for representing an Custom Color
 */
public class MyColor {
private Double value;
private String name;
private Color color;
public MyColor(String name, Double value){

    this.name = name;
    this.value = value;
    switch (name){
       case "red":color=Color.red;break;
        case "blue":color=Color.blue;break;
        case"green":color=Color.green;break;
        case"yellow":color=Color.yellow;break;
        case"orange":color=Color.orange;break;
        case"cyan":color=Color.cyan;break;
        case"magenta":color=Color.magenta;break;
        case"black":color=Color.black;break;
        case"white":color=Color.white;break;
       // case"red":color=Color.red;break;
    }

}

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public  void inc(){
    value++;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
