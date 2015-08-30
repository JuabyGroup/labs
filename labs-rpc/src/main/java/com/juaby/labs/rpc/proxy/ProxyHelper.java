package com.juaby.labs.rpc.proxy;

import com.juaby.labs.rpc.server.ProviderService;

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
