package com.juaby.labs.rpc.config;

import com.juaby.labs.rpc.util.Endpoint;

import java.lang.management.ManagementFactory;

/**
 * Created by chaos on 16-2-21.
 */
public class ServerConfig {

    private int serverType = 1;

    public final static String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    private Endpoint endpoint;

    private ServiceConfig serviceConfig;

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
        this.serviceConfig = serviceConfig;
    }

    public ServerConfig(int serverType, ServiceConfig serviceConfig) {
        this.serverType = serverType;
        this.serviceConfig = serviceConfig;
    }

    public int getServerType() {
        return serverType;
    }

    public void setServerType(int serverType) {
        this.serverType = serverType;
    }

    public ServiceConfig getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

}
