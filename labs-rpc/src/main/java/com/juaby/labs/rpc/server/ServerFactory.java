package com.juaby.labs.rpc.server;

import com.juaby.labs.rpc.common.RpcEnum;
import com.juaby.labs.rpc.config.ServerConfig;

/**
 * Created by chaos on 16-2-21.
 */
public class ServerFactory {

    public static Server getServer(ServerConfig serverConfig) {
        Server server = null;
        if (serverConfig.getServerType() == RpcEnum.Grizzly.value()) {
            server = getGrizzlyServer();
        }
        if (serverConfig.getServerType() == RpcEnum.Netty.value()) {
            server = getNettyServer();
        }
        return server;
    }

    public static Server getNettyServer() {
        return new Rpc2Server();
    }

    public static Server getGrizzlyServer() {
        return new RpcServer();
    }

}
