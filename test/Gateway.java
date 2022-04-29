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
        int dataStoreSize = 15; // change the datastore here as required
        for(int i=1;i<=dataStoreSize;i++){
            String value = "customer "+Integer.toString(i);
            data.put(Integer.toString(i), value);
        }
        String[] arr = new String[dataStoreSize];

        for(int i=1;i<=dataStoreSize;i++){
            arr[i-1] = Integer.toString(i);
        }

        Object temp = null;
        String temp2 = (String) temp;
        System.out.println(temp2);
        while (true) {
            int rand = ThreadLocalRandom.current().nextInt(0, dataStoreSize);
            System.out.println(getInfo(arr[rand]));
            Thread.sleep(2000);
        }
    }

}
