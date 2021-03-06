import java.util.*;

class CacheEntry_Expiry<V>{
    V value;
    long timeSinceLastHit;
    CacheEntry_Expiry(V value){
        this.value = value;
        timeSinceLastHit = new Date().getTime();
    }
}

public class Cache_Expiry<K, V> {
    private Map<K, CacheEntry_Expiry<V>> map;
    private int size;
    private final int CAPACITY;
    private final int SECONDSBEFOREEXPIRY;
    private int hitCount = 0;
    private int missCount = 0;

    public Cache_Expiry() {
        CAPACITY = 100;
        SECONDSBEFOREEXPIRY=3600;
        map = new HashMap<>(CAPACITY);
    }

    public void put(K key, V value) {
        // first check if we are full already
        if (map.size()==CAPACITY){
            evictEntry();
        }
        this.map.put(key, new CacheEntry_Expiry(value));
        printCounts();
        this.size = map.size();
    }

    public V get(K key) {
        if (map.containsKey(key) == false) {
            ++missCount;
            return null;
        }
        ++hitCount;
        CacheEntry_Expiry ce = map.get(key);
        resetTime(ce);
        preEmptiveEviction();
        printCounts();
        return (V)ce.value;
    }
    public void resetTime(CacheEntry_Expiry ce){
        ce.timeSinceLastHit = new Date().getTime();
    }
    public void preEmptiveEviction(){
        long currentTime = new Date().getTime() / 1000;
        // iterate over all the entries
        for (Iterator<Map.Entry<K, CacheEntry_Expiry<V>>> it = map.entrySet().iterator() ; it.hasNext();){
            Map.Entry<K, CacheEntry_Expiry<V>> entry = it.next();
            if ((currentTime - (entry.getValue().timeSinceLastHit/1000)) > SECONDSBEFOREEXPIRY ) {
                it.remove();
            }
        }
    }
    public void evictEntry(){
        // iterate over all the entries
        K keyOfOldestEntry   = null;
        long timeOfOldestEntry = new Date().getTime();
        for (Map.Entry<K, CacheEntry_Expiry<V>> entry : map.entrySet()){
            if(entry.getValue().timeSinceLastHit < timeOfOldestEntry){
                keyOfOldestEntry = entry.getKey();
                timeOfOldestEntry = entry.getValue().timeSinceLastHit;
            }
        }
        map.remove(keyOfOldestEntry);
    }
    public void printCounts(){
        System.out.println("hitcount: "+this.hitCount);
        System.out.println("misscount: "+this.missCount);
    }
}