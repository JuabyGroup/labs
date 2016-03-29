package com.juaby.labs.raft.test;

import com.juaby.labs.raft.protocols.ClientService;
import jdk.internal.org.objectweb.asm.util.ASMifier;

/**
 * Created by juaby on 16-3-29.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ASMifier.main(new String[] {ClientService.class.getName()});
    }

}
