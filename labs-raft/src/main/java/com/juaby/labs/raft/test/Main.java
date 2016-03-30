package com.juaby.labs.raft.test;

import com.juaby.labs.raft.client.ClientService;
import com.juaby.labs.raft.client.ClientServiceImpl;
import com.juaby.labs.raft.sm.KVReplicatedStateMachine;
import com.juaby.labs.rpc.proxy.ProxyHelper;
import com.juaby.labs.rpc.proxy.RpcServerProxyGenerator;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;
import jdk.internal.org.objectweb.asm.util.ASMifier;

/**
 * Created by juaby on 16-3-29.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ASMifier.main(new String[] {ClientServiceRpcServerProxyTemplate.class.getName()});
        KVReplicatedStateMachine<String, String> stateMachine = new KVReplicatedStateMachine<String, String>();
        ServiceClassInfo classInfo = ServiceClassInfoHelper.parser(ClientService.class);
        ProxyHelper.addServiceInstance(classInfo.getName(), new ClientServiceImpl(stateMachine));
        for (String methodKey : classInfo.getMethods().keySet()) {
            ServiceClassInfo.MethodInfo methodInfo = classInfo.getMethods().get(methodKey);
            System.out.println(methodInfo);
            new RpcServerProxyGenerator().newInstance(classInfo, methodInfo);
        }
    }

}
