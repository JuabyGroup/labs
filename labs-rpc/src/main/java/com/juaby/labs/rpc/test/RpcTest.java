package com.juaby.labs.rpc.test;

import com.juaby.labs.rpc.common.RpcEnum;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.proxy.ServiceFactory;
import com.juaby.labs.rpc.util.Endpoint;
import com.juaby.labs.rpc.util.EndpointHelper;
import com.juaby.labs.rpc.util.RpcCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juaby on 16-1-20.
 */
public class RpcTest {

    public static void main(String[] args) throws Exception {
        ServiceConfig<MessageService> serviceConfig = new ServiceConfig<MessageService>(2, MessageService.class);
        serviceConfig.setServerType(RpcEnum.Grizzly.value());
        Endpoint endpoint = new Endpoint("localhost", 8007);
        EndpointHelper.add(serviceConfig.getName(), endpoint);
        serviceConfig.setEndpoint(endpoint);
        MessageService messageService = ServiceFactory.getService(serviceConfig);

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
