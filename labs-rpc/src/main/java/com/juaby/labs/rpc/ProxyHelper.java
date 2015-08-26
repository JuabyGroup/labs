package com.juaby.labs.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Juaby on 2015/8/25.
 */
public class ProxyHelper {

    private static final Map<String, ProviderService> providerServiceProxyCache = new ConcurrentHashMap<String, ProviderService>();

    public static Map<String, ProviderService> addProxyInstance(String key, ProviderService proxy) {
        providerServiceProxyCache.put(key, proxy);
        return providerServiceProxyCache;
    }

    public static ProviderService getProxyInstance(String key) {
        return providerServiceProxyCache.get(key);
    }

}
