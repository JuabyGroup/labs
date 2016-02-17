package com.juaby.labs.rpc.util;

import com.juaby.labs.rpc.client.Rpc2ClientDecoder;
import com.juaby.labs.rpc.client.Rpc2ClientEncoder;
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
    static final int maxObjectSize = 1 * 1024 * 1024;

    private static final Map<String, Channel> channelCache = new ConcurrentHashMap<String, Channel>();

    public static Channel get(Endpoint endpoint) {
        Channel channel = null;
        if (!channelCache.containsKey(endpoint.key())) {
            initPool(endpoint);
        }
        channel = channelCache.get(endpoint.key());
        return channel;
    }

    public static Channel get(Endpoint endpoint, long timeout) {
        return null; //TODO
    }

    public void release(Endpoint endpoint) {
        channelCache.get(endpoint.key()).close();
    }

    public static void initPool(Endpoint endpoint) {
        Channel channel;
        NamedThreadFactory threadName = new NamedThreadFactory("RPC-CLI-WORKER", true);
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
                                    new Rpc2ClientDecoder(maxObjectSize),
                                    new Result2FutureHelper.Rpc2ClientHandler());
                        }
                    });

            // Start the connection attempt.
            ChannelFuture channelFuture = b.connect(endpoint.getHost(), endpoint.getPort());
            channelFuture.awaitUninterruptibly(5000, TimeUnit.MILLISECONDS); //TODO
            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
                channelCache.put(endpoint.key(), channel);
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
