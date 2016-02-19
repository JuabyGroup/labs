package com.juaby.labs.rpc.util;

/**
 * Created by juaby on 16-2-19.
 */
public interface RpcCallback<REQ, RES> {

    public RES callback(REQ req);

}
