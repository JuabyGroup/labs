package com.juaby.labs.rpc.util;

import com.juaby.labs.rpc.proxy.Rpcifier;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Juaby on 2015/8/26.
 */
public class ServiceClassInfoHelper {

    private static final Map<String, ServiceClassInfo> serviceClassInfoCache = new ConcurrentHashMap<String, ServiceClassInfo>();
    private static final Map<String, AtomicInteger> methodCounterCache = new ConcurrentHashMap<String, AtomicInteger>();

    public static ServiceClassInfo get(String service) {
        if (service == null) {
            return null;
        }
        return serviceClassInfoCache.get(service);
    }

    public static ServiceClassInfo parser(Class service) {
        if (serviceClassInfoCache.containsKey(service.getName())) {
            return serviceClassInfoCache.get(service.getName());
        }
        String name = service.getCanonicalName();
        ServiceClassInfo mailClassInfo = new ServiceClassInfo();
        mailClassInfo.setId(service.getName());
        ServiceClassInfo othersClassInfo;
        try {
            mailClassInfo = new Rpcifier().parser(name, mailClassInfo);

            String[] interfaces = mailClassInfo.getInterfaces();
            while (interfaces != null && interfaces.length > 0) {
                List<String> othersInterfaces = new ArrayList<String>();
                for (String interfaceName : interfaces) {
                    othersClassInfo = new ServiceClassInfo();
                    othersClassInfo = new Rpcifier().parser(interfaceName, othersClassInfo);
                    if (!othersClassInfo.getMethods().isEmpty()) {
                        mailClassInfo.getMethods().putAll(othersClassInfo.getMethods());
                    }
                    if (othersClassInfo.getInterfaces() != null && othersClassInfo.getInterfaces().length > 0) {
                        for (String newInterfaceName : othersClassInfo.getInterfaces()) {
                            othersInterfaces.add(newInterfaceName);
                        }
                    }
                }

                interfaces = new String[othersInterfaces.size()];
                if (!othersInterfaces.isEmpty()) {
                    othersInterfaces.toArray(interfaces);
                }
            }
            serviceClassInfoCache.put(mailClassInfo.getName(), mailClassInfo);
        } catch (Exception e) {
            //TODO
        }

        return mailClassInfo;
    }

    public static AtomicInteger getMethodCounter(String key) {
        AtomicInteger counter = methodCounterCache.get(key);
        if (counter != null) {
            return counter;
        }
        counter = new AtomicInteger();
        methodCounterCache.put(key, counter);
        return counter;
    }

}
