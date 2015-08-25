package com.juaby.labs.rpc;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 17:12.
 */
public class EndpointHelper {

    private static final Map<String, Set<Endpoint>> endpointCache = new ConcurrentHashMap<String, Set<Endpoint>>();

    public static Set<Endpoint> add(String service, Endpoint endpoint) {
        if (service == null) {
            return null; //TODO
        }
        if (!endpointCache.containsKey(service)) {
            endpointCache.put(service, new ConcurrentSkipListSet<Endpoint>());
        }
        endpointCache.get(service).add(endpoint);
        return endpointCache.get(service);
    }

    public static Set<Endpoint> remove(String service, Endpoint endpoint) {
        if (service == null) {
            return null; //TODO
        }
        if (!endpointCache.containsKey(service)) {
            endpointCache.put(service, new ConcurrentSkipListSet<Endpoint>());
        }
        endpointCache.get(service).remove(endpoint);
        return endpointCache.get(service);
    }

    public static Set<Endpoint> cache(String service) {
        return endpointCache.get(service);
    }

}
