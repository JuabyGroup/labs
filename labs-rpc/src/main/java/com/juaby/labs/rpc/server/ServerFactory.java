package com.juaby.labs.rpc.server;

import com.juaby.labs.rpc.common.RpcEnum;
import com.juaby.labs.rpc.config.ServerConfig;
import com.juaby.labs.rpc.proxy.ProxyHelper;
import com.juaby.labs.rpc.proxy.RpcServerProxyGenerator;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by chaos on 16-2-21.
 */
public class ServerFactory {

    public static Server getServer(ServerConfig serverConfig) {
        ServiceClassInfo classInfo = ServiceClassInfoHelper.get(serverConfig.getServiceConfig().getName());
        for (String methodSignature : classInfo.getMethods().keySet()) {
            RpcServiceHandler rpcServiceHandler = null;
            try {
                rpcServiceHandler = new RpcServerProxyGenerator().newInstance(classInfo, classInfo.getMethods().get(methodSignature));
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
            ProxyHelper.addProxyInstance(classInfo.getName() + methodSignature, rpcServiceHandler);
        }
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
