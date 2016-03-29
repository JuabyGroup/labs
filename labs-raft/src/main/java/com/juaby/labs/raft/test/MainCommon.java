package com.juaby.labs.raft.test;

import com.juaby.labs.raft.protocols.*;
import com.juaby.labs.raft.statemachine.KVReplicatedStateMachine;
import com.juaby.labs.raft.util.Cache;
import com.juaby.labs.rpc.config.ServerConfig;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.server.Server;
import com.juaby.labs.rpc.server.ServerFactory;
import com.juaby.labs.rpc.util.Endpoint;

import java.util.List;

/**
 * Created by juaby on 16-3-29.
 */
public class MainCommon {

    public static void start(String bindHost, int bindPort, List<String> members) throws Exception {
        KVReplicatedStateMachine<String, String> stateMachine = new KVReplicatedStateMachine<String, String>();

        RaftProtocol raftProtocol = Cache.getRaftProtocol();
        raftProtocol.raftId(bindHost + ":" + bindPort);
        raftProtocol.members(members);
        raftProtocol.setLocalAddr(new Endpoint(bindHost, bindPort));
        raftProtocol.stateMachine(stateMachine);

        ServerConfig serverConfig = new ServerConfig(1, bindHost, bindPort);

        ServiceConfig<RaftService> serviceConfig = new ServiceConfig<RaftService>(1, RaftService.class);
        serviceConfig.setServiceInstance(new RaftServiceImpl(raftProtocol));
        serviceConfig.setServerConfig(serverConfig);

        raftProtocol.init();
        raftProtocol.start();

        ElectionProtocol electionProtocol = Cache.getElectionProtocol();
        electionProtocol.setLocalAddr(new Endpoint(bindHost, bindPort));
        electionProtocol.init();

        ServiceConfig<ElectionService> serviceConfig2 = new ServiceConfig<ElectionService>(1, ElectionService.class);
        serviceConfig2.setServiceInstance(new ElectionServiceImpl(electionProtocol));
        serviceConfig2.setServerConfig(serverConfig);

        ServiceConfig<ClientService> serviceConfig3 = new ServiceConfig<ClientService>(1, ClientService.class);
        serviceConfig3.setServiceInstance(new ClientServiceImpl<String, String>(stateMachine));
        serviceConfig3.setServerConfig(serverConfig);

        Server server = ServerFactory.getServer(serverConfig);
        server.startup();

        Thread.currentThread().join();
    }

}
