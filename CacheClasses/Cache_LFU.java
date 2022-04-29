import java.util.*;

class CacheEntry<V>{
    V value;
    int frequency;
    CacheEntry(V value){
        this.value = value;
        this.frequency = 0;
    }
}
public class Cache_LFU<K, V>{
    private Map<K, CacheEntry<V>> map;
    private int size;
    private final int CAPACITY;
    private int hitCount = 0;
    private int missCount = 0;

    public Cache_LFU() {
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
        resetFrequency(ce);
        return (V)ce.value;
    }
    public void resetFrequency(CacheEntry ce){
        ce.frequency = 0;
    }
    
    public void evictEntry(){
        // iterate over all the entries
        K keyOfLeastFrequentEntry  = null;
        long frequencyOfLeastFrequentEntry = 0;
        for (Map.Entry<K, CacheEntry<V>> entry : map.entrySet()){
            if(entry.getValue().frequency > frequencyOfLeastFrequentEntry){
                keyOfLeastFrequentEntry = entry.getKey();
                frequencyOfLeastFrequentEntry = entry.getValue().frequency;
            }
        }
        map.remove(keyOfLeastFrequentEntry);
    }
}