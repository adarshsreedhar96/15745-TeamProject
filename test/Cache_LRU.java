import java.util.*;

class CacheEntry_LRU<V>{
    V value;
    long timeSinceLastHit;
    CacheEntry_LRU(V value){
        this.value = value;
        this.timeSinceLastHit = new Date().getTime();
    }
}
public class Cache_LRU<K, V> {
    private Map<K, CacheEntry_LRU<V>> map;
    private int size;
    private final int CAPACITY;
    private int hitCount = 0;
    private int missCount = 0;

    public Cache_LRU() {
        CAPACITY = 10;
        map = new HashMap<>(CAPACITY);
    }

    public void put(K key, V value) {
        // first check if we are full already
        if (map.size()==CAPACITY){
            evictEntry();
        }
        System.out.println("key: "+key+" added to cache");
        this.map.put(key, new CacheEntry_LRU(value));
        this.size = map.size();
        printCounts();
    }

    public V get(K key) {
        if (map.containsKey(key) == false) {
            ++missCount;
            return null;
        }
        System.out.println("hit for key: "+key);
        ++hitCount;
        CacheEntry_LRU ce = map.get(key);
        resetTime(ce);
        printCounts();
        return (V)ce.value;
    }
    public void resetTime(CacheEntry_LRU ce){
        ce.timeSinceLastHit = new Date().getTime();
    }
    
    public void evictEntry(){
        // iterate over all the entries
        K keyOfOldestEntry   = null;
        long timeOfOldestEntry = new Date().getTime();
        for (Map.Entry<K, CacheEntry_LRU<V>> entry : map.entrySet()){
            if(entry.getValue().timeSinceLastHit < timeOfOldestEntry){
                keyOfOldestEntry = entry.getKey();
                timeOfOldestEntry = entry.getValue().timeSinceLastHit;
            }
        }
        map.remove(keyOfOldestEntry);
    }
    public void printCounts(){
        System.out.println("cache hit count: "+this.hitCount);
        System.out.println("cache miss count: "+this.missCount);
    }
}