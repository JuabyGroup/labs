package com.juaby.labs.rpc;

import com.juaby.labs.rpc.proxy.RpcClientProxyExample;
import com.juaby.labs.rpc.proxy.Rpcifier;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.proxy.ServiceClassInfo.MethodInfo;
import jdk.internal.org.objectweb.asm.util.ASMifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juaby on 16-1-20.
 */
public class AsmTest {

    public static void main(String[] args) throws Exception {
        String name = null;
        name = RpcClientProxyExample.class.getCanonicalName();
        //name = MessageService.class.getCanonicalName();
        ServiceClassInfo mailClassInfo = new ServiceClassInfo();
        ServiceClassInfo othersClassInfo;
        mailClassInfo = new Rpcifier().parser(name, mailClassInfo);

        String[] interfaces = mailClassInfo.getInterfaces();
        while (interfaces != null && interfaces.length > 0) {
            List<String> othersInterfaces = new ArrayList<String>();
            for (String interfaceName : interfaces) {
                othersClassInfo = new ServiceClassInfo();
                othersClassInfo = new Rpcifier().parser(interfaceName, othersClassInfo);
                if (!othersClassInfo.getMethods().isEmpty()) {
                    mailClassInfo.getMethods().putAll(othersClassInfo.getMethods());
                }
                if (othersClassInfo.getInterfaces() != null && othersClassInfo.getInterfaces().length > 0) {
                    for (String newInterfaceName : othersClassInfo.getInterfaces()) {
                        othersInterfaces.add(newInterfaceName);
                    }
                }
            }

            interfaces = new String[othersInterfaces.size()];
            if (!othersInterfaces.isEmpty()) {
                othersInterfaces.toArray(interfaces);
            }
        }
        boolean flag = true;
    }

}
