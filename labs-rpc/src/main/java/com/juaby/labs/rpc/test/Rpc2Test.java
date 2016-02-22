package com.juaby.labs.rpc.test;

import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.config.ServiceConfigHelper;
import com.juaby.labs.rpc.proxy.RpcClientProxyGenerator;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.proxy.ServiceFactory;
import com.juaby.labs.rpc.util.Endpoint;
import com.juaby.labs.rpc.util.EndpointHelper;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juaby on 16-1-20.
 */
public class Rpc2Test {

    public static void main(String[] args) throws Exception {
        ServiceConfig<MessageService> serviceConfig = new ServiceConfig<MessageService>(2, MessageService.class);
        MessageService messageService = ServiceFactory.getService(serviceConfig);
        Endpoint endpoint = new Endpoint("localhost", 8007);
        EndpointHelper.add(serviceConfig.getName(), endpoint);
        TestBean testBean = new TestBean();
        testBean.setId("007");
        List<String> params = new ArrayList<String>();
        params.add("hello");
        TestResult result = messageService.message(testBean, params);
        System.out.println(result.getId());
        messageService.vmethod("hello");
        //classInfo = ServiceClassInfoHelper.get(RpcServerProxy.class);
        boolean flag = true;
    }

}
