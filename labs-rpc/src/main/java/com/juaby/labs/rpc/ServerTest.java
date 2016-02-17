package com.juaby.labs.rpc;

import com.juaby.labs.rpc.server.Rpc2Server;
import com.juaby.labs.rpc.server.RpcServer;
import com.juaby.labs.rpc.server.Server;

/**
 * Created by juaby on 16-2-17.
 */
public class ServerTest extends Thread {

    private final Server server;

    public static void main(String[] args) {
        Server server = new RpcServer();
        Server server2 = new Rpc2Server();
        new ServerTest(server).start();
        new ServerTest(server2).start();
    }

    public ServerTest(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        server.startup();
    }

}
