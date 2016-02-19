package com.juaby.labs.rpc.util;

import io.netty.channel.Channel;
import org.glassfish.grizzly.Connection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by juaby on 16-2-19.
 */
public class RpcCallbackHandler {

    private final static Map<String, RpcCallback> callbackCache = new ConcurrentHashMap<String, RpcCallback>();
    private final static Map<String, Channel> callbackChannelCache = new ConcurrentHashMap<String, Channel>();
    private final static Map<String, Connection> callbackConnectionCache = new ConcurrentHashMap<String, Connection>();

    public static void addCallback(String key, RpcCallback callback) {
        callbackCache.put(key, callback);
    }

    public static RpcCallback getCallback(String key) {
        return callbackCache.get(key);
    }

    public static void addCallbackChannel(String key, Channel channel) {
        callbackChannelCache.put(key, channel);
    }

    public static Channel getCallbackChannel(String key) {
        return callbackChannelCache.get(key);
    }

    public static void addCallbackChannel(String key, Connection connection) {
        callbackConnectionCache.put(key, connection);
    }

    public static Connection getCallbackConnection(String key) {
        return callbackConnectionCache.get(key);
    }

    public static <RES> void handler(RpcCallback callback, RES res) {
        callback.callback(res);
    }

}
