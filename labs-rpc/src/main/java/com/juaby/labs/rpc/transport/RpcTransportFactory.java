package com.juaby.labs.rpc.transport;

import com.juaby.labs.rpc.common.RpcEnum;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.util.ChannelFactory;
import com.juaby.labs.rpc.util.ConnectionFactory;
import com.juaby.labs.rpc.util.Endpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chaos on 16-2-23.
 */
public class RpcTransportFactory {

    private static final Map<String, RpcTransport> transportCache = new ConcurrentHashMap<String, RpcTransport>();

    public static void cache(Endpoint endpoint, RpcTransport transport) {
        transportCache.put(endpoint.key(), transport);
    }

    public static void cache(String key, RpcTransport transport) {
        transportCache.put(key, transport);
    }

    public static RpcTransport getTransport(Endpoint endpoint) {
        RpcTransport transport = transportCache.get(endpoint.key());
        if (transport == null) {
            //TODO
        }
        return transportCache.get(endpoint.key());
    }

    public static RpcTransport getTransport(Endpoint endpoint, ServiceConfig serviceConfig) {
        RpcTransport transport = transportCache.get(endpoint.key());
        if (transport == null && serviceConfig.getServerType() == RpcEnum.Grizzly.value()) {
            ConnectionFactory.initPool(endpoint);
            transport = transportCache.get(endpoint.key());
        }
        if (transport == null && serviceConfig.getServerType() == RpcEnum.Netty.value()) {
            ChannelFactory.initPool(endpoint);
            transport = transportCache.get(endpoint.key());
        }
        return transport;
    }

    public static RpcTransport getTransport(String key) {
        return transportCache.get(key);
    }

}
