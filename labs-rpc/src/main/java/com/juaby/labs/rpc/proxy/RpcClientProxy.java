package com.juaby.labs.rpc.proxy;

import com.juaby.labs.rpc.*;
import com.juaby.labs.rpc.base.RequestMessageBody;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.config.ServiceConfigHelper;
import org.glassfish.grizzly.samples.filterchain.RpcClient;

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
public class RpcClientProxy<O, F> implements MessageService<O, F> {

    private ServiceConfig config = ServiceConfigHelper.getConfig(getClass().getInterfaces()[0].getName());

    @Override
    public TestResult message(TestBean testBean, List<String> params) {
        RequestMessageBody requestMessageBody = new RequestMessageBody(config.getName());
        requestMessageBody.setMethod("message");
        requestMessageBody.setParams(new Object[] {testBean, params});
        return RpcClient.sendMessage(requestMessageBody);
    }

    @Override
    public <T> TestResult message2(TestBean testBean, List<String> params) {
        RequestMessageBody requestMessageBody = new RequestMessageBody(config.getName());
        requestMessageBody.setMethod("message2");
        requestMessageBody.setParams(new Object[] {testBean, params});
        return RpcClient.sendMessage(requestMessageBody);
    }

    @Override
    public List<Map<String, File>> message3(TestBean testBean, Map<String, List<TestBean>> param) throws NegativeArraySizeException, IOException {
        return null;
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
}
