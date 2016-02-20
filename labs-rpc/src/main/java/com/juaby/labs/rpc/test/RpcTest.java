package com.juaby.labs.rpc.test;

import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.config.ServiceConfigHelper;
import com.juaby.labs.rpc.proxy.RpcClientProxyGenerator;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.util.Endpoint;
import com.juaby.labs.rpc.util.EndpointHelper;
import com.juaby.labs.rpc.util.RpcCallback;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juaby on 16-1-20.
 */
public class RpcTest {

    public static void main(String[] args) throws Exception {
        Class<MessageService> serviceClass = MessageService.class;
        ServiceClassInfo classInfo = ServiceClassInfoHelper.parser(serviceClass);
        ServiceConfig config = new ServiceConfig(classInfo.getName(), 1);
        ServiceConfigHelper.addConfig(config);
        MessageService messageService = new RpcClientProxyGenerator().newInstance(classInfo, serviceClass);
        Endpoint endpoint = new Endpoint("localhost", 9098);
        EndpointHelper.add(classInfo.getName(), endpoint);
        TestBean testBean = new TestBean();
        testBean.setId("007");
        List<String> params = new ArrayList<String>();
        params.add("hello");
        TestResult result = messageService.message(testBean, params, new RpcCallback<TestResult, TestResult>() {
            @Override
            public TestResult callback(TestResult o) {
                System.out.println("callback : " + o.getTime());
                return null;
            }
        });
        System.out.println(result.getId());
        //messageService.vmethod("hello");
        //classInfo = ServiceClassInfoHelper.get(RpcServerProxy.class);
        boolean flag = true;
        Thread.currentThread().join();
    }

}
