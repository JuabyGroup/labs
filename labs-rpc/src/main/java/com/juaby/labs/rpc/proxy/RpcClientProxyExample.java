package com.juaby.labs.rpc.proxy;

import com.juaby.labs.rpc.MessageService;
import com.juaby.labs.rpc.TestBean;
import com.juaby.labs.rpc.TestResult;
import com.juaby.labs.rpc.base.RequestMessageBody;
import com.juaby.labs.rpc.client.RpcClient;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.config.ServiceConfigHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 17:08.
 */
@RpcAnnotation
@Deprecated
public class RpcClientProxyExample<O, F> extends RpcClientProxy implements MessageService<O, F> {

    private Map<String, List<TypeObject>> map = new ConcurrentHashMap<String, List<TypeObject>>();
    public Map<String, List<TypeObject>> map2 = new ConcurrentHashMap<String, List<TypeObject>>();
    protected Map<String, List<TypeObject>> map3 = new ConcurrentHashMap<String, List<TypeObject>>();
    Map<String, List<TypeObject>> map4 = new ConcurrentHashMap<String, List<TypeObject>>();
    private final Map<String, List<TypeObject>> map5 = new ConcurrentHashMap<String, List<TypeObject>>();
    private static final Map<String, List<TypeObject>> map6 = new ConcurrentHashMap<String, List<TypeObject>>();

    private RpcClientProxyExample() {
        setConfig(getClass().getInterfaces()[0].getName());
    }

    @Override
    @RpcAnnotation
    @Deprecated
    public TestResult message(@Deprecated @RpcAnnotation TestBean testBean, @Deprecated @RpcAnnotation List<String> params) {
        return sendMessage(getConfig().getName(), "message", new Object[]{testBean, params});
    }

    @Override
    public <T> TestResult message2(TestBean testBean, List<String> params) {
        return sendMessage(getConfig().getName(), "message2", new Object[]{testBean, params});
    }

    @Override
    public List<Map<String, File>> message3(TestBean testBean, Map<String, List<TestBean>> param) throws NegativeArraySizeException, IOException {
        return sendMessage(getConfig().getName(), "message2", new Object());
    }

    @Override
    public <V> V vmethod() {
        return null;
    }

    @Override
    public <V> void vmethod(V v) {
        return;
    }

    @Override
    public <V, R> R vmethod2(V v) {
        return (R)v;
    }

    @Override
    public <T> TestResult message2(TestBean testBean, List<String> param, T t) {
        return null;
    }

    @Override
    public <T> O message2(List<F> param, O t) {
        return null;
    }

    @Override
    public <T> O messageENUM(List<F> param, TypeObject t) {
        return null;
    }
}
