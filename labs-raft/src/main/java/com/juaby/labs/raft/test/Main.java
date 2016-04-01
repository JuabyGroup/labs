package com.juaby.labs.raft.test;

import org.objectweb.asm.util.ASMifier;

import static org.objectweb.asm.Type.BOOLEAN_TYPE;

/**
 * Created by juaby on 16-4-1.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ASMifier.main(new String[] {ClientServiceTemplate.class.getName()});
        System.out.println(BOOLEAN_TYPE.getClassName());
    }

}
