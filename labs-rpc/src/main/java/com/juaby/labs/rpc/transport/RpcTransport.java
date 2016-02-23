package com.juaby.labs.rpc.transport;

import com.juaby.labs.rpc.util.Endpoint;

/**
 * Created by juaby on 16-2-23.
 */
public interface RpcTransport {

    public void sendMessage(Object message);

    public void release(Endpoint endpoint);

}
