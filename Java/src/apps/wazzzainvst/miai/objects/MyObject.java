package apps.wazzzainvst.miai.objects;
/**
 * Class object responsible for representing an object that can be detected and common attributes
 */
public class MyObject {
    private boolean mobile;
    private boolean happy;
    private int[] color;

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public boolean isHappy() {
        return happy;
    }

    public void setDark(boolean happy) {
        this.happy = happy;
    }

    public int[] getColor() {
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }
    public int getSize() {
        return size;
    }
    public int getSpeed() {
        return speed;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String category;
    private int speed;
    private int size;
    private boolean loud = false;

    public MyObject(boolean mobile, boolean happy, int[] color, String category, int speed, int size,boolean loud){

        //is this object mobile
        this.mobile = mobile;
        //is this a bright or dark object
        this.happy = happy;
        //what color is this object - predominantly
        this.color = color;
        //what kind of object is this
        this.category = category;
        //speed of object the specific , fast,slow,
        this.speed = speed;

        //size of the specific object
        this.size = size;

        //is this object loud?
        this.loud = loud;


    }

    public boolean isLoud() {
        return loud;
    }

    public void setLoud(boolean loud) {
        this.loud = loud;
    }


}
