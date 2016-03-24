package com.juaby.labs.raft.util;

import com.juaby.labs.raft.protocols.ElectionProtocol;
import com.juaby.labs.raft.protocols.ElectionService;
import com.juaby.labs.raft.protocols.RaftProtocol;
import com.juaby.labs.raft.protocols.RaftService;
import com.juaby.labs.rpc.util.Endpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by juaby on 16-3-22.
 */
public class Cache {

    private final static Map<Endpoint, ElectionService> electionServiceCache = new ConcurrentHashMap<Endpoint, ElectionService>();
    private final static Map<Endpoint, RaftService> raftServiceCache = new ConcurrentHashMap<Endpoint, RaftService>();

    private final static ElectionProtocol electionProtocol = new ElectionProtocol();
    private final static RaftProtocol raftProtocol = new RaftProtocol();

    public static void addElectionService(Endpoint endpoint, ElectionService election) {
        electionServiceCache.put(endpoint, election);
    }

    public static ElectionService getElectionService(Endpoint endpoint) {
        return electionServiceCache.get(endpoint);
    }

    public static void addRaftService(Endpoint endpoint, RaftService raft) {
        raftServiceCache.put(endpoint, raft);
    }

    public static RaftService getRaftService(Endpoint endpoint) {
        return raftServiceCache.get(endpoint);
    }

    public static ElectionProtocol getElectionProtocol() {
        return electionProtocol;
    }

    public static RaftProtocol getRaftProtocol() {
        return raftProtocol;
    }

}
