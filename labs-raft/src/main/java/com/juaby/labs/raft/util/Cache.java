package com.juaby.labs.raft.util;

import com.juaby.labs.raft.protocols.ElectionService;
import com.juaby.labs.rpc.util.Endpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by juaby on 16-3-22.
 */
public class Cache {

    private final static Map<Endpoint, ElectionService> electionServiceCache = new ConcurrentHashMap<Endpoint, ElectionService>();
    private final static Map<Endpoint, ElectionService> raftServiceCache = new ConcurrentHashMap<Endpoint, ElectionService>();

    public static void addElectionService(Endpoint endpoint, ElectionService election) {
        electionServiceCache.put(endpoint, election);
    }

    public static ElectionService getElectionService(Endpoint endpoint) {
        return electionServiceCache.get(endpoint);
    }

}
