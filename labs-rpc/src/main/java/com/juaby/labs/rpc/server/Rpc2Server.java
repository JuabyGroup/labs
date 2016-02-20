/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.juaby.labs.rpc.server;

import com.juaby.labs.rpc.test.MessageServerServiceImpl;
import com.juaby.labs.rpc.test.MessageService;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.proxy.RpcServerProxyGenerator;
import com.juaby.labs.rpc.proxy.ProxyHelper;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.util.NamedThreadFactory;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public final class Rpc2Server implements Server {

    static final boolean SSL = System.getProperty("ssl") != null;

    private String hsot = "localhost";
    private int port = 8007;

    private ServiceConfig config;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    public Rpc2Server() {
    }

    public Rpc2Server(ServiceConfig config) {
        this.config = config;
    }

    @Override
    public void init() {
        ServiceClassInfo classInfo = ServiceClassInfoHelper.parser(MessageService.class);
        MessageService messageServerService = new MessageServerServiceImpl();
        ProxyHelper.addServiceInstance(classInfo.getName(), messageServerService);
        Class<RpcServiceHandler> serviceClass = RpcServiceHandler.class;
        for (String methodSignature : classInfo.getMethods().keySet()) {
            RpcServiceHandler rpcServiceHandler = null;
            try {
                rpcServiceHandler = new RpcServerProxyGenerator().newInstance(classInfo, classInfo.getMethods().get(methodSignature), serviceClass);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            ProxyHelper.addProxyInstance(classInfo.getName() + methodSignature, rpcServiceHandler);
        }
    }

    public void start() {
        NamedThreadFactory threadName = new NamedThreadFactory("RPC-SVR-WORKER", false);
        int threads = Runtime.getRuntime().availableProcessors() * 2 + 1;
        bossGroup = new NioEventLoopGroup(threads, threadName);

        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();

                    // Configure SSL.
                    final SslContext sslCtx;
                    if (SSL) {
                        SelfSignedCertificate ssc = new SelfSignedCertificate();
                        sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
                    } else {
                        sslCtx = null;
                    }

                    if (sslCtx != null) {
                        p.addLast(sslCtx.newHandler(ch.alloc()));
                    }
                    p.addLast(
                            //output
                            new Rpc2ServerEncoder(),
                            //input
                            new Rpc2ServerDecoder(ServiceConfig.MAX_OBJECT_SIZE),
                            new Rpc2ServerHandler());
                }
             });

            // Bind and start to accept incoming connections.
            //b.bind(HOST, PORT).sync().channel().closeFuture().sync();
            b.bind(hsot, port);
        } finally {
        }
    }

    @Override
    public void startup() {
        init();
        start();
    }

    @Override
    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
