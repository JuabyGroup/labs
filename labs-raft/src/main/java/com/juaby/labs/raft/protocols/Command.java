package com.juaby.labs.raft.protocols;

import java.io.Serializable;

/**
 * Command fro Log
 *
 * Created by juaby on 16-3-25.
 */
public class Command<CT, CD> implements Serializable {

    private CT type;

    private CD data;

    public Command() {
    }

    public Command(CT type, CD data) {
        this.type = type;
        this.data = data;
    }

    public CT getType() {
        return type;
    }

    public void setType(CT type) {
        this.type = type;
    }

    public CD getData() {
        return data;
    }

    public void setData(CD data) {
        this.data = data;
    }

}