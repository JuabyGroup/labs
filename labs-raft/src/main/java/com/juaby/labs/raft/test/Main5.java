package com.juaby.labs.raft.test;

import com.juaby.labs.raft.config.RaftConfig;
import com.juaby.labs.raft.protocols.*;
import com.juaby.labs.raft.util.Cache;
import com.juaby.labs.rpc.config.ServerConfig;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.server.Server;
import com.juaby.labs.rpc.server.ServerFactory;
import com.juaby.labs.rpc.util.Endpoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juaby on 16-3-28.
 */
public class Main5 {

    public static void main(String[] args) throws Exception {
        RaftConfig config = new RaftConfig();
        RaftConfig.Member member1 = config.new Member("1", "10.12.165.43", 7081);
        RaftConfig.Member member2 = config.new Member("2", "10.12.165.43", 7082);
        RaftConfig.Member member3 = config.new Member("3", "10.12.165.43", 7083);
        RaftConfig.Member member4 = config.new Member("4", "10.12.165.43", 7084);
        RaftConfig.Member member5 = config.new Member("5", "10.12.165.43", 7085);
        config.getMembers().add(member1);
        config.getMembers().add(member2);
        config.getMembers().add(member3);
        config.getMembers().add(member4);
        config.getMembers().add(member5);
        List<String> members = new ArrayList<String>();
        for (RaftConfig.Member member : config.getMembers()) {
            members.add(member.getHost() + ":" + member.getPort());
        }
        String bindHost = "10.12.165.43";
        int bindPort = 7085;
        MainCommon.start(bindHost, bindPort, members);
    }

}
