package com.juaby.labs.rpc.util;

import com.juaby.labs.rpc.client.Rpc2ClientDecoder;
import com.juaby.labs.rpc.client.Rpc2ClientEncoder;
import com.juaby.labs.rpc.client.Rpc2ClientHandler;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.transport.NettyTransport;
import com.juaby.labs.rpc.transport.RpcTransportFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 13:55.
 */
public class ChannelFactory {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    private static final Map<String, Channel> channelCache = new ConcurrentHashMap<String, Channel>();
    private static final Map<String, Bootstrap> bootstrapCache = new ConcurrentHashMap<String, Bootstrap>();

    public static Channel get(Endpoint endpoint) {
        Channel channel = channelCache.get(endpoint.key());
        if (channel == null) {
            initPool(endpoint);
        }
        return channel;
    }

    public static Channel get(Endpoint endpoint, long timeout) {
        return null; //TODO
    }

    public void release(Endpoint endpoint) {
        channelCache.get(endpoint.key()).close();
        channelCache.remove(endpoint.key());
    }

    public static void initPool(Endpoint endpoint) {
        Channel channel;
        if (bootstrapCache.containsKey(endpoint.key())) {
            // Start the connection attempt.
            ChannelFuture channelFuture = bootstrapCache.get(endpoint.key()).connect(endpoint.getHost(), endpoint.getPort());
            channelFuture.awaitUninterruptibly(5000, TimeUnit.MILLISECONDS); //TODO
            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
                channelCache.put(endpoint.key(), channel);
                RpcTransportFactory.cache(endpoint, new NettyTransport(channel));
            } else {
                Throwable cause = channelFuture.cause();
            }
        }

        RpcThreadFactory threadName = new RpcThreadFactory("RPC-CLI-WORKER", true);
        int threads = Runtime.getRuntime().availableProcessors() * 2 + 1;
        EventLoopGroup group = new NioEventLoopGroup(threads, threadName);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            // Configure SSL.
                            SslContext sslCtx = null;
                            if (SSL) {
                                try {
                                    sslCtx = SslContextBuilder.forClient()
                                            .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                                } catch (SSLException e) {
                                    e.printStackTrace(); //TODO
                                }
                            } else {
                                sslCtx = null;
                            }

                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                            }
                            p.addLast(
                                    //write
                                    new Rpc2ClientEncoder(),
                                    //read
                                    new Rpc2ClientDecoder(ServiceConfig.MAX_OBJECT_SIZE),
                                    new Rpc2ClientHandler());
                        }
                    });

            bootstrapCache.put(endpoint.key(), b);
            // Start the connection attempt.
            ChannelFuture channelFuture = b.connect(endpoint.getHost(), endpoint.getPort());
            channelFuture.awaitUninterruptibly(5000, TimeUnit.MILLISECONDS); //TODO
            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
                channelCache.put(endpoint.key(), channel);
                RpcTransportFactory.cache(endpoint, new NettyTransport(channel));
            } else {
                Throwable cause = channelFuture.cause();
            }
        } catch (Exception e) {
            //TODO
        } finally {
            //TODO
        }
    }

}
