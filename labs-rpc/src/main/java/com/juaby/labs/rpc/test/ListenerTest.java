package com.juaby.labs.rpc.test;

import com.juaby.labs.rpc.util.RpcListener;

/**
 * Created by juaby on 16-4-1.
 */
public class ListenerTest implements RpcListener {

    @Override
    public void handle(Object result) {
        TestResult r = (TestResult) result;
        System.out.println("Listener : " + r.getTime());
    }

    @Override
    public void catchException(Throwable throwable) {

    }

}