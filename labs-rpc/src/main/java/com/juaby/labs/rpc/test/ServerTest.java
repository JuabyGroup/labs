package com.juaby.labs.rpc.test;

import com.juaby.labs.rpc.config.ServerConfig;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.server.Rpc2Server;
import com.juaby.labs.rpc.server.Server;
import com.juaby.labs.rpc.server.ServerFactory;

/**
 * Created by juaby on 16-2-17.
 */
public class ServerTest extends Thread {

    private final Server server;

    public static void main(String[] args) {
        ServiceConfig<MessageService> serviceConfig = new ServiceConfig<MessageService>(1, MessageService.class);
        serviceConfig.setServiceInstance(new MessageServerServiceImpl());
        ServerConfig serverConfig = new ServerConfig(1, "localhost", 9098);
        serviceConfig.setServerConfig(serverConfig);
        Server server = ServerFactory.getServer(serverConfig);
        Server server2 = new Rpc2Server();
        new ServerTest(server).start();
        //new ServerTest(server2).start();
    }

    public ServerTest(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        server.startup();
    }

}
