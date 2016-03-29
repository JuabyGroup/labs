package com.juaby.labs.raft.test;

import com.juaby.labs.raft.protocols.ClientService;
import com.juaby.labs.rpc.common.RpcEnum;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.proxy.ServiceFactory;
import com.juaby.labs.rpc.util.Endpoint;
import com.juaby.labs.rpc.util.EndpointHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by juaby on 16-3-29.
 */
public class MainClient {

    public static void main(String[] args) throws InterruptedException {
        ServiceConfig<ClientService> serviceConfig = new ServiceConfig<ClientService>(2, ClientService.class);
        serviceConfig.setServerType(RpcEnum.Grizzly.value());
        Endpoint endpoint = new Endpoint("10.12.165.43", 7081);
        EndpointHelper.add(serviceConfig.getName(), endpoint);
        serviceConfig.setEndpoint(endpoint);
        ClientService<String, String> clientService = ServiceFactory.getService(serviceConfig);
        AtomicInteger counter = new AtomicInteger();
        while (true) {
            int flag = counter.getAndIncrement();
            //clientService.set("key1" + flag, "value" + flag);
            String val = clientService.get("key1" + flag);
            System.out.println(val);
            Thread.sleep(1000);
        }
    }

}
