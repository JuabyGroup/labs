package com.juaby.labs.raft.util;

import com.juaby.labs.raft.protocols.ElectionProtocol;
import com.juaby.labs.raft.protocols.ElectionService;
import com.juaby.labs.raft.protocols.RaftProtocol;
import com.juaby.labs.raft.protocols.RaftService;
import com.juaby.labs.rpc.common.RpcEnum;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.proxy.ServiceFactory;
import com.juaby.labs.rpc.util.Endpoint;
import com.juaby.labs.rpc.util.EndpointHelper;

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
        ElectionService electionService = electionServiceCache.get(endpoint);
        if (electionService != null) {
            return electionService;
        }
        ServiceConfig<ElectionService> serviceConfig = new ServiceConfig<ElectionService>(2, ElectionService.class);
        serviceConfig.setServerType(RpcEnum.Grizzly.value());
        endpoint.setPort(endpoint.getPort() + 1);
        EndpointHelper.add(serviceConfig.getName(), endpoint);
        serviceConfig.setEndpoint(endpoint);
        electionService = ServiceFactory.getService(serviceConfig);
        electionServiceCache.put(endpoint, electionService);
        return electionService;
    }

    public static void addRaftService(Endpoint endpoint, RaftService raft) {
        raftServiceCache.put(endpoint, raft);
    }

    public static RaftService getRaftService(Endpoint endpoint) {
        RaftService raftService = raftServiceCache.get(endpoint);
        if (raftService != null) {
            return raftService;
        }
        ServiceConfig<RaftService> serviceConfig = new ServiceConfig<RaftService>(2, RaftService.class);
        serviceConfig.setServerType(RpcEnum.Grizzly.value());
        EndpointHelper.add(serviceConfig.getName(), endpoint);
        serviceConfig.setEndpoint(endpoint);
        raftService = ServiceFactory.getService(serviceConfig);
        raftServiceCache.put(endpoint, raftService);
        return raftService;
    }

    public static ElectionProtocol getElectionProtocol() {
        return electionProtocol;
    }

    public static RaftProtocol getRaftProtocol() {
        return raftProtocol;
    }

}
