package com.juaby.labs.rpc.server;

import com.juaby.labs.rpc.common.RpcEnum;
import com.juaby.labs.rpc.config.ServerConfig;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.proxy.ProxyHelper;
import com.juaby.labs.rpc.proxy.RpcServerProxyGenerator;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.util.Endpoint;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by chaos on 16-2-21.
 */
public class ServerFactory {

    public static Server getServer(ServerConfig serverConfig) {
        for (ServiceConfig serviceConfig : serverConfig.getServiceConfigs()) {
            ServiceClassInfo classInfo = ServiceClassInfoHelper.get(serviceConfig.getName());
            for (String key : classInfo.getMethods().keySet()) {
                RpcServiceHandler rpcServiceHandler = null;
                try {
                    rpcServiceHandler = new RpcServerProxyGenerator().newInstance(classInfo, classInfo.getMethods().get(key));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                ProxyHelper.addProxyInstance(classInfo.getName() + key, rpcServiceHandler);
            }
        }

        Server server = null;
        Endpoint endpoint = serverConfig.getEndpoint();
        if (serverConfig.getServerType() == RpcEnum.Grizzly.value()) {
            server = getGrizzlyServer(endpoint);
        }
        if (serverConfig.getServerType() == RpcEnum.Netty.value()) {
            server = getNettyServer(endpoint);
        }
        return server;
    }

    public static Server getNettyServer(Endpoint endpoint) {
        return new Rpc2Server(endpoint.getHost(), endpoint.getPort());
    }

    public static Server getGrizzlyServer(Endpoint endpoint) {
        return new RpcServer(endpoint.getHost(), endpoint.getPort());
    }

}
