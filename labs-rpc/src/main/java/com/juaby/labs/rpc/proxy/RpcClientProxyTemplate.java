package com.juaby.labs.rpc.proxy;

import com.juaby.labs.rpc.test.MessageService;
import com.juaby.labs.rpc.test.TestBean;
import com.juaby.labs.rpc.test.TestResult;
import com.juaby.labs.rpc.test.TypeObject;
import com.juaby.labs.rpc.util.RpcCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 17:08.
 */
public class RpcClientProxyTemplate<O, F> extends RpcClientProxy implements MessageService<O, F> {

    private RpcClientProxyTemplate(String serviceName) {
        super(serviceName);
        setConfig(serviceName);
    }

    @Override
    public TestResult message(TestBean testBean, List<String> params) {
        return sendMessage(getConfig().getName(), "message", new Object[]{testBean, params});
    }

    @Override
    public TestResult message(TestBean testBean, List<String> params, RpcCallback callback) {
        return sendMessage(getConfig().getName(), "message", new Object[]{testBean, params, callback});
    }

    @Override
    public <T> TestResult message2(TestBean testBean, List<String> params) {
        return sendMessage(getConfig().getName(), "message2", new Object[]{testBean, params});
    }

    @Override
    public List<Map<String, File>> message3(TestBean testBean, Map<String, List<TestBean>> params) throws NegativeArraySizeException, IOException {
        return sendMessage(getConfig().getName(), "message3", new Object[]{testBean, params});
    }

    @Override
    public List<Map<String, File>> message4(TestBean testBean, Map<String, List<TestBean>> params, List<TestBean> t) throws NegativeArraySizeException, IOException {
        return sendMessage(getConfig().getName(), "message4", new Object[]{testBean, params, t});
    }

    @Override
    public <V> V vmethod() {
        return sendMessage(getConfig().getName(), "<V> V vmethod()", new Object[]{});
    }

    @Override
    public <V> void vmethod(V v) {
        sendMessage(getConfig().getName(), "<V> void vmethod(V v)", new Object[]{v});
        return;
    }

    @Override
    public void vmethod3() {
        Object [] params = new Object[100000];
        for (int i = 0; i < 1000; i++) {
            params[i] = i;
        }
        sendMessage(getConfig().getName(), "vmethod3", params);
        return;
    }

    @Override
    public <V, R> R vmethod2(V v) {
        return sendMessage(getConfig().getName(), "<V, R> R vmethod2(V v)", new Object[]{v});
    }

    @Override
    public <T> TestResult message2(TestBean testBean, List<String> params, T t) {
        return sendMessage(getConfig().getName(), "message2", new Object[]{testBean, params, t});
    }

    @Override
    public <T> O message2(List<F> param, O t) {
        return sendMessage(getConfig().getName(), "message2", new Object[]{param, t});
    }

    @Override
    public <T> O messageENUM(List<F> param, TypeObject t) {
        return sendMessage(getConfig().getName(), "messageENUM", new Object[]{param, t});
    }

}
