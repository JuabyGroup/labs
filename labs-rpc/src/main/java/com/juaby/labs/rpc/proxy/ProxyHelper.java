package com.juaby.labs.rpc.proxy;

import com.juaby.labs.rpc.server.RpcServiceHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Juaby on 2015/8/25.
 */
public class ProxyHelper {

    private static final Map<String, RpcServiceHandler> providerServiceProxyCache = new ConcurrentHashMap<String, RpcServiceHandler>();

    private static final Map<String, Object> providerServiceCache = new ConcurrentHashMap<String, Object>();

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
        javaTypes.put("boolean[][][]", "[[[Z");
        javaTypes.put("char[][][]", "[[[C");
        javaTypes.put("byte[][][]", "[[[B");
        javaTypes.put("short[][][]", "[[[S");
        javaTypes.put("int[][][]", "[[[I");
        javaTypes.put("float[][][]", "[[[F");
        javaTypes.put("long[][][]", "[[[J");
        javaTypes.put("double[][][]", "[[[D");
        javaTypes.put("void", "V");
    }

    public static Map<String, RpcServiceHandler> addProxyInstance(String key, RpcServiceHandler proxy) {
        providerServiceProxyCache.put(key, proxy);
        return providerServiceProxyCache;
    }

    public static Map<String, Object> addServiceInstance(String key, Object service) {
        providerServiceCache.put(key, service);
        return providerServiceCache;
    }

    public static RpcServiceHandler getProxyInstance(String key) {
        return providerServiceProxyCache.get(key);
    }

    public static Object getServiceInstance(String key) {
        return providerServiceCache.get(key);
    }

    public static Map<String, String> javaTypes() {
        return javaTypes;
    }

    public static String javaType(String typeName) {
        if (javaTypes.containsKey(typeName)) {
            return javaTypes.get(typeName);
        } else {
            if (typeName.contains("[][][]")) {
                return "[[[L";
            }
            if (typeName.contains("[][]")) {
                return "[[L";
            }
            if (typeName.contains("[]")) {
                return "[L";
            } else {
                return "L";
            }
        }
    }

    public static boolean isJavaType(String typeName) {
        if (javaTypes.containsKey(typeName)) {
            return true;
        } else {
            return false;
        }
    }

}
