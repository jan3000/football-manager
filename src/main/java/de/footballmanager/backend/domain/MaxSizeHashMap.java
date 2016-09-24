package de.footballmanager.backend.domain;

import java.util.HashMap;

public class MaxSizeHashMap<K, V> extends HashMap<K, V> {

    private int maxSize;

    public MaxSizeHashMap(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    @Override
    public V put(K key, V value) {
        if (this.size() >= maxSize) {
            throw new IllegalArgumentException("maxSize is already reached");
        }
        super.put(key, value);
        return value;
    }
}
