package apps.wazzzainvst.miai.objects;

import java.util.HashMap;
/**
 * Class object responsible for representing an object category
 */
public class MyCategoryObject {
    private boolean mobile;
    private boolean dark;
    private int[] color;
    private HashMap <String,Boolean> categories;

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
        System.out.println(category+" is mobile?: "+mobile);
    }


    public String getCategory() {
        return category;
    }

    private String category;

    public MyCategoryObject(String category){

        this.category = category;
        categories = new HashMap<>();
        categories.put("human",true);
        categories.put("transport",true);
        categories.put("traffic",true);
        categories.put("outdoor",false);
        categories.put("animal",true);
        categories.put("accessory",false);
        categories.put("sports",true);
        categories.put("kitchen",false);
        categories.put("food",false);
        categories.put("furniture",false);
        categories.put("bathroom",false);
        categories.put("electronics",false);
        categories.put("stationary",false);
        categories.put("toy",false);
        categories.put("buildings",false);
        categories.put("instruments",false);
        categories.put("nature",false);

        //what kind of object is this
        this.category = category;
        System.out.println("getting: "+category);
        if (categories.get(category)) {
            setMobile(true);
        }else {
            setMobile(false);
        }
    }
}
