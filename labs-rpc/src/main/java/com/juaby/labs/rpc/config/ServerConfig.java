package com.juaby.labs.rpc.config;

import com.juaby.labs.rpc.common.RpcEnum;
import com.juaby.labs.rpc.util.Endpoint;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaos on 16-2-21.
 */
public class ServerConfig {

    private int serverType = RpcEnum.Grizzly.value();

    public final static String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    private Endpoint endpoint;

    private List<ServiceConfig> serviceConfigs = new ArrayList<ServiceConfig>();

    public ServerConfig(int serverType) {
        this.serverType = serverType;
    }

    public ServerConfig(int serverType, String host, int port) {
        this.serverType = serverType;
        this.endpoint = new Endpoint(host, port);
    }

    public ServerConfig(int serverType, Endpoint endpoint, ServiceConfig serviceConfig) {
        this.serverType = serverType;
        this.endpoint = endpoint;
        this.serviceConfigs.add(serviceConfig);
    }

    public ServerConfig(int serverType, ServiceConfig serviceConfig) {
        this.serverType = serverType;
        this.serviceConfigs.add(serviceConfig);
    }

    public int getServerType() {
        return serverType;
    }

    public void setServerType(int serverType) {
        this.serverType = serverType;
    }

    public List<ServiceConfig> getServiceConfigs() {
        return serviceConfigs;
    }

    public void addServiceConfig(ServiceConfig serviceConfig) {
        this.serviceConfigs.add(serviceConfig);
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

}
