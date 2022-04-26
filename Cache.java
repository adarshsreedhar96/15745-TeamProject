import java.util.*;

public class Cache<K, V> {
    private Map<K, V> map;
    private int size;
    private final int CAPACITY;
    private int hitCount = 0;
    private int missCount = 0;

    // public Cache(int capacity) {
    // CAPACITY = capacity; //
    // map = new HashMap<>(CAPACITY);
    // }

    public Cache() {
        CAPACITY = 100; //
        map = new HashMap<>(CAPACITY);
    }

    public void put(K key, V value) {
        this.map.put(key, value);
        this.size = map.size();
    }

    public V get(K key) {
        if (map.containsKey(key) == false) {
            return null;
        }
        V value = map.get(key);
        return value;
    }

    public int size() {
        return size;
    }

    public int getHitCount() {
        return hitCount;
    }

    public int getMissCount() {
        return missCount;
    }
}