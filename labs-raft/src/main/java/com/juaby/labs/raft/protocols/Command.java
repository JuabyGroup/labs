package com.juaby.labs.raft.protocols;

import java.io.Serializable;

/**
 * Created by juaby on 16-3-25.
 */
public class Command<K, V> implements Serializable {

    private CommandType type;

    private K key;

    private V value;

    public Command() {
    }

    public Command(CommandType type, K key, V value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
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