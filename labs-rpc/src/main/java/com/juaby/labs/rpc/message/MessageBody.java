package com.juaby.labs.rpc.message;

/**
 * Created by chaos on 16-2-20.
 */
public class MessageBody {

    private String service;

    private String method;

    public MessageBody() {
    }

    public MessageBody(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
