package com.juaby.labs.rpc.proxy;

import com.juaby.labs.rpc.util.RpcCallback;

/**
 * Created by juaby on 16-3-23.
 */
public class RpcCallbackProxyTemplate<REQ, RES> extends RpcCallbackProxy implements RpcCallback<REQ, RES> {

    public RpcCallbackProxyTemplate(String transportKey) {
        setTransportKey(transportKey);
    }

    @Override
    public RES callback(REQ o) {
        transport(o);
        return null;
    }

}