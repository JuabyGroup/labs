package com.juaby.labs.rpc.util;

import com.juaby.labs.rpc.client.CustomClientFilter;
import com.juaby.labs.rpc.client.RpcClientFilter;
import com.juaby.labs.rpc.transport.GrizzlyTransport;
import com.juaby.labs.rpc.transport.RpcTransportFactory;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.connectionpool.SingleEndpointPool;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 13:55.
 */
public class ConnectionFactory {

    private static final Map<String, SingleEndpointPool> connCache = new ConcurrentHashMap<String, SingleEndpointPool>();
    private static final Map<String, TCPNIOTransport> transportCache = new ConcurrentHashMap<String, TCPNIOTransport>();

    public static Connection get(Endpoint endpoint) {
        Connection connection = null;
        if (!connCache.containsKey(endpoint.key())) {
            initPool(endpoint);
        }
        try {
            Future<Connection> connectionFuture = connCache.get(endpoint.key()).take();
            connection = connectionFuture.get();
        } catch (InterruptedException e) {
            //TODO
            e.printStackTrace();
        } catch (ExecutionException e) {
            //TODO
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection get(Endpoint endpoint, long timeout) {
        return null; //TODO
    }

    public static boolean release(Endpoint endpoint, Connection connection) {
        return connCache.get(endpoint.key()).release(connection);
    }

    public static void initPool(Endpoint endpoint) {
        // Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        // Add TransportFilter, which is responsible
        // for reading and writing data to the connection
        filterChainBuilder.add(new TransportFilter());
        filterChainBuilder.add(new RpcClientFilter());
        filterChainBuilder.add(new CustomClientFilter());

        // Create TCP NIO transport
        final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
        transport.setProcessor(filterChainBuilder.build());
        transportCache.put(endpoint.key(), transport);
        try {
            // start transport
            transport.start();

            SingleEndpointPool pool = SingleEndpointPool
                    .builder(SocketAddress.class)
                    .connectorHandler(transport)
                    .endpointAddress(new InetSocketAddress(endpoint.getHost(), endpoint.getPort()))
                    .maxPoolSize(8) //TODO
                    .build();
            connCache.put(endpoint.key(), pool);
            RpcTransportFactory.cache(endpoint, new GrizzlyTransport(pool));
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
    }

}
