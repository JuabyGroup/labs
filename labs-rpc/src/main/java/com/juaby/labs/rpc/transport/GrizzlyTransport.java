package com.juaby.labs.rpc.transport;

import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.util.Endpoint;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.connectionpool.SingleEndpointPool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by juaby on 16-2-23.
 */
public class GrizzlyTransport implements RpcTransport {

    private Connection connection;

    private SingleEndpointPool pool;

    public GrizzlyTransport(SingleEndpointPool pool) {
        this.pool = pool;
    }

    public GrizzlyTransport(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void sendMessage(RpcMessage message) {
        try {
            Future<Connection> connectionFuture = pool.take();
            connection = connectionFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.write(message);
    }

    @Override
    public void release(Endpoint endpoint) {
        pool.release(connection);
    }

    @Override
    public boolean isWritable() {
        return connection.canWrite();
    }

}
