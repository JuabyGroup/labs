package com.juaby.labs.rpc.util;

/**
 * Created by juaby on 16-4-1.
 */
public interface RpcListener {

    public void handle(Object result);

    public void catchException(Throwable throwable);

}
