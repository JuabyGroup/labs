package com.juaby.labs.rpc.server;

import com.juaby.labs.rpc.config.ServerConfig;

/**
 * Created by chaos on 16-2-21.
 */
public class ServerFactory {

    public static Server getServer(ServerConfig serverConfig) {
        return new RpcServer();
    }

    public static Server getNettyServer(ServerConfig serverConfig) {
        return new Rpc2Server();
    }

    public static Server getGrizzlyServer(ServerConfig serverConfig) {
        return new RpcServer();
    }

}
