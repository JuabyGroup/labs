package com.juaby.labs.rpc.transport;

import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.util.Endpoint;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.connectionpool.SingleEndpointPool;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by juaby on 16-2-23.
 */
public class GrizzlyTransport implements RpcTransport {

    private Connection connection;

    private SingleEndpointPool pool;

    private String host;

    private int port;

    public GrizzlyTransport(SingleEndpointPool pool) {
        this.pool = pool;
    }

    public GrizzlyTransport(Connection connection) {
        this.connection = connection;
        InetSocketAddress inetSocketAddress = (InetSocketAddress) connection.getLocalAddress();
        this.host = inetSocketAddress.getHostString();
        this.port = inetSocketAddress.getPort();
    }

    @Override
    public void sendMessage(RpcMessage message) {
        /**
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
         */
        //TODO
        connection.write(message);
    }

    @Override
    public void release(Endpoint endpoint) {
        //TODO
        //pool.release(connection);
    }

    @Override
    public boolean isWritable() {
        return connection.canWrite();
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

}
