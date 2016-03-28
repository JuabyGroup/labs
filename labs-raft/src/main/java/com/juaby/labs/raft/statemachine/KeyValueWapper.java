package com.juaby.labs.raft.statemachine;

/**
 * Created by juaby on 16-3-25.
 */
public class KeyValueWapper<K, V> {

    private K key;

    private V value;

    public KeyValueWapper() {
    }

    public KeyValueWapper(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

}