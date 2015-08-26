package com.juaby.labs.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Juaby on 2015/8/25.
 */
public class ProxyHelper {

    private static final Map<String, ProviderService> providerServiceProxyCache = new ConcurrentHashMap<String, ProviderService>();

    private static final Map<String, String> javaTypes = new HashMap<String, String>();

    static {
        javaTypes.put("boolean", "Z");
        javaTypes.put("char", "C");
        javaTypes.put("byte", "B");
        javaTypes.put("short", "S");
        javaTypes.put("int", "I");
        javaTypes.put("float", "F");
        javaTypes.put("long", "J");
        javaTypes.put("double", "D");
        javaTypes.put("boolean[]", "[Z");
        javaTypes.put("char[]", "[C");
        javaTypes.put("byte[]", "[B");
        javaTypes.put("short[]", "[S");
        javaTypes.put("int[]", "[I");
        javaTypes.put("float[]", "[F");
        javaTypes.put("long[]", "[J");
        javaTypes.put("double[]", "[D");
        javaTypes.put("boolean[][]", "[[Z");
        javaTypes.put("char[][]", "[[C");
        javaTypes.put("byte[][]", "[[B");
        javaTypes.put("short[][]", "[[S");
        javaTypes.put("int[][]", "[[I");
        javaTypes.put("float[][]", "[[F");
        javaTypes.put("long[][]", "[[J");
        javaTypes.put("double[][]", "[[D");
    }

    public static Map<String, ProviderService> addProxyInstance(String key, ProviderService proxy) {
        providerServiceProxyCache.put(key, proxy);
        return providerServiceProxyCache;
    }

    public static ProviderService getProxyInstance(String key) {
        return providerServiceProxyCache.get(key);
    }

    public static Map<String, String> javaTypes() {
        return javaTypes;
    }

    public static String javaType(String typeName) {
        if (javaTypes.containsKey(typeName)) {
            return javaTypes.get(typeName);
        } else {
            if (typeName.contains("[][]")) {
                return "[[L";
            } else {
                if (typeName.contains("[]")) {
                    return "[L";
                } else {
                    return "L";
                }
            }
        }
    }

}
