package com.juaby.labs.raft.test;

import com.juaby.labs.rpc.proxy.RpcClientProxyTemplate;
import org.objectweb.asm.util.ASMifier;

import static org.objectweb.asm.Type.BOOLEAN_TYPE;

/**
 * Created by juaby on 16-4-1.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        //ASMifier.main(new String[] {RpcClientProxyTemplate.class.getName()});
        String x = "Ljava/lang/Boolean;";
        System.out.println(x.substring(1, x.length() - 1));
    }

}
