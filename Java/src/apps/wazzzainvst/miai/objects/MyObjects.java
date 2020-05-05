package apps.wazzzainvst.miai.objects;
/**
 * Class object responsible for creating/initialising MyObject objects and setting attributes
 */
import java.util.HashMap;

public class MyObjects {
    HashMap<String, MyObject> Objects = new HashMap<String, MyObject>();
    public MyObjects(){

        int[] color = {0, 0, 0};

        MyObject person = new MyObject(true, true, color, "human", 2, 1,true);
        MyObject bicycle = new MyObject(true, true, color, "transport", 3, 2,false);
        MyObject car = new MyObject(true, true, color, "transport", 3, 2,true);
        MyObject motorcycle = new MyObject(true, true, color, "transport", 3, 2,true);
        MyObject airplane = new MyObject(true, true, color, "transport", 3, 3,true);
        MyObject bus = new MyObject(true, false, color, "transport", 2, 3,true);
        MyObject train = new MyObject(true, false, color, "transport", 3, 3,true);
        MyObject truck = new MyObject(true, false, color, "transport", 3, 3,true);
        MyObject boat = new MyObject(true, false, color, "transport", 1, 3,false);


        MyObject trafficLight = new MyObject(false, false, color, "traffic", 1, 2,false);
        MyObject fireHydrant = new MyObject(false, true, color, "traffic", 1, 1,false);
        MyObject stopSign = new MyObject(false, false, color, "traffic", 1, 1,false);
        MyObject parkingMeter = new MyObject(false, false, color, "traffic", 1, 1,false);
        MyObject bench = new MyObject(false, true, color, "furniture", 1, 2,false);


        MyObject bird = new MyObject(true, true, color, "animal", 3, 1,true);
        MyObject cat = new MyObject(true, true, color, "animal", 2, 1,false);
        MyObject dog = new MyObject(true, true, color, "animal", 2, 1,true);
        MyObject horse = new MyObject(true, true, color, "animal", 3, 3,true);
        MyObject sheep = new MyObject(true, true, color, "animal", 3, 2,true);
        MyObject cow = new MyObject(true, true, color, "animal", 2, 3,true);
        MyObject elephant = new MyObject(true, true, color, "animal", 3, 3,true);
        MyObject bear = new MyObject(true, true, color, "animal", 3, 3,true);
        MyObject zebra = new MyObject(true, true, color, "animal", 3, 3,false);
        MyObject giraffe = new MyObject(true, true, color, "animal", 3, 3,false);


        MyObject backpack = new MyObject(false, false, color, "accessory", 1, 1,false);
        MyObject umbrella = new MyObject(false, false, color, "accessory", 1, 1,false);
        MyObject handbag = new MyObject(false, false, color, "accessory", 1, 1,false);
        MyObject tie = new MyObject(false, false, color, "accessory", 1, 1,false);
        MyObject suitcase = new MyObject(false, false, color, "accessory", 1, 1,false);


        MyObject sportsBall = new MyObject(true, true, color, "sports", 3, 1,true);
        MyObject kite = new MyObject(false, true, color, "sports", 3, 1,true);
        MyObject baseballBat = new MyObject(false, true, color, "sports", 2, 1,true);
        MyObject baseballGlove = new MyObject(false, true, color, "sports", 1, 1,true);
        MyObject skateboard = new MyObject(false, true, color, "sports", 3, 1,true);
        MyObject surfboard = new MyObject(false, true, color, "sports", 3, 3,true);
        MyObject tennisRacket = new MyObject(false, true, color, "sports", 2, 1,false);
        MyObject skis = new MyObject(false, true, color, "sports", 3, 2,true);


        MyObject bottle = new MyObject(false, false, color, "kitchen", 1, 1,false);
        MyObject wineGlass = new MyObject(false, true, color, "kitchen", 1, 1,false);
        MyObject cup = new MyObject(false, true, color, "kitchen", 1, 1,false);
        MyObject fork = new MyObject(false, false, color, "kitchen", 1, 1,false);
        MyObject knife = new MyObject(false, false, color, "kitchen", 1, 1,false);
        MyObject bowl = new MyObject(false, true, color, "kitchen", 1, 1,false);


        MyObject microwave = new MyObject(false, true, color, "kitchen", 1, 1,false);
        MyObject oven = new MyObject(false, false, color, "kitchen", 1, 2,false);
        MyObject toaster = new MyObject(false, false, color, "kitchen", 1, 1,false);
        MyObject sink = new MyObject(false, false, color, "kitchen", 1, 1,false);
        MyObject refrigerator = new MyObject(false, false, color, "kitchen", 1, 2,false);


        MyObject banana = new MyObject(false, true, color, "food", 1, 1,false);
        MyObject apple = new MyObject(false, true, color, "food", 1, 1,false);
        MyObject sandwich = new MyObject(false, true, color, "food", 1, 1,false);
        MyObject orange = new MyObject(false, true, color, "food", 1, 1,false);
        MyObject broccoli = new MyObject(false, true, color, "food", 1, 1,false);
        MyObject carrot = new MyObject(false, true, color, "food", 1, 1,false);
        MyObject hotDog = new MyObject(false, true, color, "food", 1, 1,false);
        MyObject pizza = new MyObject(false, true, color, "food", 1, 1,false);
        MyObject donot = new MyObject(false, true, color, "food", 1, 1,false);
        MyObject cake = new MyObject(false, true, color, "food", 1, 1,false);


        MyObject chair = new MyObject(false, false, color, "furniture", 1, 1,false);
        MyObject couch = new MyObject(false, false, color, "furniture", 1, 3,false);
        MyObject pottedPlant = new MyObject(false, true, color, "furniture", 1, 1,false);
        MyObject bed = new MyObject(false, false, color, "furniture", 1, 3,false);
        MyObject diningTable = new MyObject(false, false, color, "furniture", 1, 3,false);
        MyObject vase = new MyObject(false, true, color, "furniture", 1, 1,false);

        MyObject toilet = new MyObject(false, false, color, "bathroom", 1, 2,true);

        MyObject tv = new MyObject(false, true, color, "electronics", 1, 2,true);
        MyObject laptop = new MyObject(false, true, color, "electronics", 1, 2,false);
        MyObject mouse = new MyObject(false, true, color, "electronics", 1, 1,false);
        MyObject remote = new MyObject(false, false, color, "electronics", 1, 1,false);
        MyObject keyboard = new MyObject(false, false, color, "electronics", 1, 1,false);
        MyObject cellPhone = new MyObject(false, true, color, "electronics", 1, 1,true);


        MyObject book = new MyObject(false, true, color, "stationary", 1, 1,false);
        MyObject clock = new MyObject(false, true, color, "furniture", 1, 1,false);
        MyObject scissors = new MyObject(false, false, color, "stationary", 1, 1,false);
        MyObject teddyBear = new MyObject(false, true, color, "toy", 1, 1,false);
        MyObject hairDryer = new MyObject(false, true, color, "electronics", 2, 1,true);
        MyObject toothbrush = new MyObject(false, true, color, "bathroom", 1, 1,false);

        Objects.put("person", person);
        Objects.put("bicycle", bicycle);
        Objects.put("car", car);
        Objects.put("motorcycle", motorcycle);
        Objects.put("airplane", airplane);
        Objects.put("bus", bus);
        Objects.put("train", train);
        Objects.put("truck", truck);
        Objects.put("boat", boat);

        Objects.put("traffic light", trafficLight);
        Objects.put("fire hydrant", fireHydrant);
        Objects.put("stop sign", stopSign);
        Objects.put("parking meter", parkingMeter);
        Objects.put("bench", bench);

        Objects.put("bird", bird);
        Objects.put("cat", cat);
        Objects.put("dog", dog);
        Objects.put("horse", horse);
        Objects.put("sheep", sheep);
        Objects.put("cow", cow);
        Objects.put("elephant", elephant);
        Objects.put("bear", bear);
        Objects.put("zebra", zebra);
        Objects.put("giraffe", giraffe);

        Objects.put("backpack", backpack);
        Objects.put("umbrella", umbrella);
        Objects.put("handbag", handbag);
        Objects.put("tie", tie);
        Objects.put("suitcase", suitcase);


        Objects.put("sports ball", sportsBall);
        Objects.put("skis", skis);
        Objects.put("kite", kite);
        Objects.put("baseball bat", baseballBat);
        Objects.put("baseball glove", baseballGlove);
        Objects.put("skateboard", skateboard);
        Objects.put("surfboard", surfboard);
        Objects.put("tennis racket", tennisRacket);
        //        //if this object can moves on its own

        Objects.put("bottle", bottle);
        Objects.put("wine glass", wineGlass);
        Objects.put("cup", cup);
        Objects.put("fork", fork);
        Objects.put("knife", knife);
        Objects.put("bowl", bowl);

        Objects.put("microwave", microwave);
        Objects.put("oven", oven);
        Objects.put("toaster", toaster);
        Objects.put("sink", sink);
        Objects.put("refrigerator", refrigerator);

        Objects.put("banana", banana);
        Objects.put("apple", apple);
        Objects.put("sandwich", sandwich);
        Objects.put("broccoli", broccoli);
        Objects.put("orange", orange);
        Objects.put("carrot", carrot);
        Objects.put("hot dog", hotDog);
        Objects.put("pizza", pizza);
        Objects.put("donut", donot);
        Objects.put("cake", cake);

        Objects.put("chair", chair);
        Objects.put("couch", couch);
        Objects.put("potted plant", pottedPlant);
        Objects.put("bed", bed);
        Objects.put("dining table", diningTable);
        Objects.put("vase", vase);
        Objects.put("toilet", toilet);
        Objects.put("tv", tv);
        Objects.put("laptop", laptop);
        Objects.put("mouse", mouse);
        Objects.put("remote", remote);
        Objects.put("keyboard", keyboard);
        Objects.put("cell phone", cellPhone);

        Objects.put("book", book);
        Objects.put("clock", clock);
        Objects.put("teddy bear", teddyBear);
        Objects.put("hair dryer", hairDryer);
        Objects.put("toothbrush", toothbrush);
        Objects.put("scissors", scissors);

        //custom objects
        MyObject lamp = new MyObject(false, false, color, "furniture", 1, 1,false);

        MyObject piano = new MyObject(false, true, color, "instruments", 1, 1,true);
        MyObject guitar = new MyObject(false, true, color, "instruments", 1, 1,true);
        MyObject speaker = new MyObject(false, true, color, "instruments", 3, 1,true);
        MyObject drum = new MyObject(false, true, color, "instruments", 1, 1,true);
        MyObject hand_drum = new MyObject(false, true, color, "instruments", 1, 1,true);

        MyObject building = new MyObject(false, false, color, "buildings", 1, 1,false);

        MyObject people_crowd = new MyObject(true, true, color, "human", 2, 1,true);

        Objects.put("piano", piano);
        Objects.put("guitar", guitar);
        Objects.put("lamp", lamp);
        Objects.put("speaker", speaker);
        Objects.put("drum", drum);
        Objects.put("hand_drum", hand_drum);
        Objects.put("building", building);
        Objects.put("people_crowd", people_crowd);

    }

    public HashMap<String, MyObject> getObjects() {
        return Objects;
    }
}
