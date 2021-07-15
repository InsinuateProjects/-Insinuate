package io.insinuate.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pair<K, V> {

    public final K key;
    public final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Deprecated
    public K getFirst() {
        return key;
    }

    @Deprecated
    public V getSecond() {
        return value;
    }
    public static <K, V> Map<K,V> pairsToMap(List<Pair<K, V>> pairs) {
        Map<K, V> map = new HashMap<>();
        pairs.forEach( pair -> map.put(pair.key, pair.value));
        return map;
    }

    public static <K, V> Map<K,V> pairToMap(Pair<K, V> pair) {
        Map<K, V> map = new HashMap<>();
        map.put(pair.key, pair.value);
        return map;
    }
}
