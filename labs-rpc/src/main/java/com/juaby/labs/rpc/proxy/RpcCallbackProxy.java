package com.juaby.labs.rpc.proxy;

import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.message.ResponseMessageBody;
import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.transport.RpcTransport;
import com.juaby.labs.rpc.util.RpcCallbackHandler;
import com.juaby.labs.rpc.util.SerializeTool;

/**
 * Created by juaby on 16-3-23.
 */
public class RpcCallbackProxy<REQ, RES> {

    private String transportKey;

    public RpcCallbackProxy() {
    }

    public RpcCallbackProxy(String transportKey) {
        this.transportKey = transportKey;
    }

    public RES transport(REQ o) {
        RpcTransport transport = RpcCallbackHandler.getCallbackRpcTransport(getTransportKey());
        if (transport.isWritable()) {
            ResponseMessageBody<REQ> responseMessageBody = new ResponseMessageBody<REQ>();
            //TODO
            String[] transportKeyArray = getTransportKey().split(":");
            responseMessageBody.setService(transportKeyArray[2]);
            responseMessageBody.setMethod(transportKeyArray[3]);
            responseMessageBody.setReturnClass("LVoid;");
            responseMessageBody.setBody(o);
            byte [] body = SerializeTool.serialize(responseMessageBody);
            RpcMessage sentMessage = new RpcMessage((byte) 1, (byte) 2,
                    (byte) 0x0F, (byte) 0, body);

            sentMessage.setId(-1);
            sentMessage.setTotalLength(ServiceConfig.HEADER_SIZE + body.length);

            transport.sendMessage(sentMessage);
        }
        return null;
    }

    public String getTransportKey() {
        return transportKey;
    }

    public void setTransportKey(String transportKey) {
        this.transportKey = transportKey;
    }

}