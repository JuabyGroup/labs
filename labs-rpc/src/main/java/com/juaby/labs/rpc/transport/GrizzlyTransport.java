package com.juaby.labs.rpc.transport;

import org.glassfish.grizzly.Connection;

/**
 * Created by juaby on 16-2-23.
 */
public class GrizzlyTransport implements RpcTransport {

    private Connection connection;

    public GrizzlyTransport(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void sendMessage(Object message) {
        connection.write(message);
    }

}
