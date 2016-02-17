package com.juaby.labs.rpc;

import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.config.ServiceConfigHelper;
import com.juaby.labs.rpc.proxy.DynamicServiceClientGenerator;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.util.Endpoint;
import com.juaby.labs.rpc.util.EndpointHelper;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juaby on 16-1-20.
 */
public class RpcTest {

    public static void main(String[] args) throws Exception {
        ServiceClassInfo classInfo = ServiceClassInfoHelper.get(MessageService.class);
        ServiceConfig config = new ServiceConfig(classInfo.getName(), 1);
        ServiceConfigHelper.addConfig(config);
        Class<MessageService> s = MessageService.class;
        MessageService messageService = new DynamicServiceClientGenerator().newInstance(classInfo, s);
        Endpoint endpoint = new Endpoint("localhost", 9098);
        EndpointHelper.add(classInfo.getName(), endpoint);
        TestBean testBean = new TestBean();
        testBean.setId("007");
        List<String> params = new ArrayList<String>();
        params.add("hello");
        TestResult result = messageService.message(testBean, params);
        System.out.println(result.getId());
        messageService.vmethod("hello");
        //classInfo = ServiceClassInfoHelper.get(ProviderServiceProxy.class);
        boolean flag = true;
    }

}
