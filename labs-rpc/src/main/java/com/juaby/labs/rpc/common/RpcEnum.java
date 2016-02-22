package com.juaby.labs.rpc.common;

/**
 * Created by juaby on 16-2-22.
 */
public enum RpcEnum {

    Grizzly(1, "Grizzly"),
    Netty(2, "Netty"),
    Server(1, "Server"),
    Client(2, "Client");

    private int value;

    private String desc;


    RpcEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int value() {
        return value;
    }

    public String desc() {
        return desc;
    }

}
