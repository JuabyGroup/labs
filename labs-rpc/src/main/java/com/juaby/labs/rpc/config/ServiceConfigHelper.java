package com.juaby.labs.rpc.config;

import com.juaby.labs.rpc.config.ServiceConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Juaby on 2015/8/28.
 */
public class ServiceConfigHelper {

    private static final Map<String, ServiceConfig> serviceConfigCache = new ConcurrentHashMap<String, ServiceConfig>();

    public static void addConfig(ServiceConfig config) {
        serviceConfigCache.put(config.getName(), config);
    }

    public static ServiceConfig getConfig(String service) {
        return serviceConfigCache.get(service);
    }

}
