package com.juaby.labs.rpc;

import com.juaby.labs.rpc.proxy.RpcClientProxyTemplate;
import org.objectweb.asm.util.ASMifier;

/**
 * Created by juaby on 16-1-20.
 */
public class AsmTest {

    public static void main(String[] args) throws Exception {
        ASMifier.main(new String[] {RpcClientProxyTemplate.class.getName()});
    }

}
