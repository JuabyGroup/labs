package com.juaby.labs.raft.client;

import com.juaby.labs.rpc.util.Endpoint;

/**
 * Created by juaby on 16-3-25.
 */
public interface ClientService<K, V> {

    public V set(K key, V value);

    public V get(K key);

    public V remove(K key);

    public boolean addServer(Endpoint endpoint);

    public boolean removeServer(Endpoint endpoint);

    public Endpoint leader();

    public String role();

}
