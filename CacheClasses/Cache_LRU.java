import java.util.*;

class CacheEntry<V>{
    V value;
    long timeSinceLastHit;
    CacheEntry(V value){
        this.value = value;
        this.timeSinceLastHit = new Date().getTime();
    }
}
public class Cache_LRU<K, V>{
    private Map<K, CacheEntry<V>> map;
    private int size;
    private final int CAPACITY;
    private int hitCount = 0;
    private int missCount = 0;

    public Cache_LRU() {
        CAPACITY = 100;
        map = new HashMap<>(CAPACITY);
    }
    public void put(K key, V value) {
        // first check if we are full already
        if (map.size()==CAPACITY){
            evictEntry();
        }
        this.map.put(key, new CacheEntry(value));
        this.size = map.size();
    }
    public V get(K key) {
        if (map.containsKey(key) == false) {
            ++missCount;
            return null;
        }
        ++hitCount;
        CacheEntry ce = map.get(key);
        resetTime(ce);
        return (V)ce.value;
    }
    public void resetTime(CacheEntry ce){
        ce.timeSinceLastHit = new Date().getTime();
    }
    
    public void evictEntry(){
        // iterate over all the entries
        K keyOfOldestEntry   = null;
        long timeOfOldestEntry = new Date().getTime();
        for (Map.Entry<K, CacheEntry<V>> entry : map.entrySet()){
            if(entry.getValue().timeSinceLastHit < timeOfOldestEntry){
                keyOfOldestEntry = entry.getKey();
                timeOfOldestEntry = entry.getValue().timeSinceLastHit;
            }
        }
        map.remove(keyOfOldestEntry);
    }
}