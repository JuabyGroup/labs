package com.juaby.labs.raft.client;

import com.juaby.labs.raft.sm.KVReplicatedStateMachine;
import com.juaby.labs.rpc.util.Endpoint;

/**
 * Created by juaby on 16-3-25.
 */
public class ClientServiceImpl<K, V> implements ClientService<K, V> {

    private KVReplicatedStateMachine<K, V> stateMachine;

    public ClientServiceImpl(KVReplicatedStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public V set(K key, V value) {
        try {
            return stateMachine.put(key, value);
        } catch (Exception e) {
            e.printStackTrace(); //TODO
        }
        return null;
    }

    @Override
    public V get(K key) {
        return stateMachine.get(key);
    }

    @Override
    public V remove(K key) {
        try {
            return stateMachine.remove(key);
        } catch (Exception e) {
            e.printStackTrace(); //TODO
        }
        return null;
    }

    @Override
    public boolean addServer(Endpoint endpoint) {
        return false;
    }

    @Override
    public boolean removeServer(Endpoint endpoint) {
        return false;
    }

    @Override
    public void leader() {
        return;
    }

}