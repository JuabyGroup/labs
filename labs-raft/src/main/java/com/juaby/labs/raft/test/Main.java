package com.juaby.labs.raft.test;

import com.juaby.labs.raft.client.ClientService;
import com.juaby.labs.rpc.proxy.RpcClientProxyTemplate;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;
import org.objectweb.asm.util.ASMifier;

/**
 * Created by juaby on 16-4-1.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ASMifier.main(new String[] {ClientServiceTemplate.class.getName()});
        String x = "Ljava/lang/Boolean;";
        System.out.println(x.substring(1, x.length() - 1));
        ServiceClassInfo classInfo = ServiceClassInfoHelper.parser(ClientService.class);
        System.out.println(classInfo.getName());
        int a= 0;
        Integer b = 1;
        a = b;
    }

}
