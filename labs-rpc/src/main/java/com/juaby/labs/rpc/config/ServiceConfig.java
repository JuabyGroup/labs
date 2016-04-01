package com.juaby.labs.rpc.config;

import com.juaby.labs.rpc.common.RpcEnum;
import com.juaby.labs.rpc.exception.RpcException;
import com.juaby.labs.rpc.proxy.ProxyHelper;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.util.Endpoint;
import com.juaby.labs.rpc.util.RpcListener;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 18:02.
 */
public class ServiceConfig<S> {

    private volatile String name;

    public static final int HEADER_SIZE = 12 + 4 + 4;

    public static final int MAX_OBJECT_SIZE = 1 * 1024 * 1024;

    private int serviceType;

    private int serverType;

    private ServerConfig serverConfig;

    private Endpoint endpoint;

    private RpcListener listener;

    private boolean async;

    private long timeout = 5000L;

    public ServiceConfig(int serviceType) {
        this.serviceType = serviceType;
    }

    public ServiceConfig(int serviceType, Class<S> serviceClass) {
        this.serviceType = serviceType;
        if (serviceClass == null) {
            //TODO
        }

        ServiceClassInfo classInfo = ServiceClassInfoHelper.parser(serviceClass);

        this.name = classInfo.getName();

        if (serviceType == RpcEnum.Server.value()) {

        }
        if (serviceType == RpcEnum.Client.value()) {

        }
        ServiceConfigHelper.addConfig(this);
    }

    public ServiceConfig(int serviceType, Class<S> serviceClass, ServerConfig serverConfig) {
        this(serviceType, serviceClass);
        this.serverConfig = serverConfig;
        serverConfig.addServiceConfig(this);
        ServiceConfigHelper.addConfig(this);
    }

    public <I> ServiceConfig(int serviceType, Class<S> serviceClass, I instance) {
        this(serviceType, serviceClass);
        ProxyHelper.addServiceInstance(getName(), instance);
        ServiceConfigHelper.addConfig(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        ServiceConfigHelper.addConfig(this);
    }

    public <I> void setServiceInstance(I instance) {
        if (serviceType == RpcEnum.Client.value()) {
            throw new RpcException("TODO"); //TODO
        }
        if (serviceType == RpcEnum.Server.value()) {
            ProxyHelper.addServiceInstance(getName(), instance);
        }
        ServiceConfigHelper.addConfig(this);
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
        ServiceConfigHelper.addConfig(this);
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        serverConfig.addServiceConfig(this);
        ServiceConfigHelper.addConfig(this);
    }

    public int getServerType() {
        return serverType;
    }

    public void setServerType(int serverType) {
        this.serverType = serverType;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public RpcListener getListener() {
        return listener;
    }

    public void setListener(RpcListener listener) {
        this.listener = listener;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}