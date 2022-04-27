import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Gateway {

    static Map<String, String> data = new HashMap<String, String>();

    public static String getInfo(String id) {
        String res = getInfoCall(id);
        return res;
    }

    public static String getInfoCall(String id) {
        System.out.println("making actual call");
        return data.get(id);
    }

    public static void main(String[] args) throws Exception {
        data.put("a", "customer 1");
        data.put("b", "customer 2");
        data.put("c", "customer 3");
        data.put("d", "customer 4");
        data.put("e", "customer 5");
        String[] arr = { "a", "b", "c", "d", "e" };
        Object temp = null;
        String temp2 = (String) temp;
        System.out.println(temp2);
        while (true) {
            int rand = ThreadLocalRandom.current().nextInt(0, 5);
            System.out.println(getInfo(arr[rand]));
            Thread.sleep(2000);
        }
    }

}
