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

import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.exception.RpcException;
import com.juaby.labs.rpc.message.RequestMessageBody;
import com.juaby.labs.rpc.message.ResponseMessageBody;
import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.proxy.ProxyHelper;
import com.juaby.labs.rpc.proxy.RpcCallbackProxyGenerator;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.test.CallbackTest;
import com.juaby.labs.rpc.transport.NettyTransport;
import com.juaby.labs.rpc.util.RpcCallback;
import com.juaby.labs.rpc.util.RpcCallbackHandler;
import com.juaby.labs.rpc.util.SerializeTool;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

/**
 * Handles both client-side and server-side handler depending on which
 * constructor was called.
 */
public class Rpc2ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        final RpcMessage message = (RpcMessage) msg;
        RequestMessageBody requestMessageBody = new RequestMessageBody();
        SerializeTool.deserialize(message.getBody(), requestMessageBody);
        RpcServiceHandler rpcServiceHandler = ProxyHelper.getProxyInstance(requestMessageBody.getService() + requestMessageBody.getMethod());
        ServiceClassInfo serviceClassInfo = ServiceClassInfoHelper.get(requestMessageBody.getService());
        ServiceClassInfo.MethodInfo methodInfo = serviceClassInfo.getMethods().get(requestMessageBody.getMethod());
        String transportKey = null;
        if(methodInfo.isCallback()) {
            //TODO
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            transportKey = inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort() + ":" + requestMessageBody.getService() + ":" + requestMessageBody.getMethod();
            RpcCallbackHandler.addCallbackRpcTransport(transportKey, new NettyTransport(ctx.channel()));
            RpcCallback callback = RpcCallbackHandler.getServerCallbackProxy(transportKey);
            if (callback == null) {
                try {
                    callback = new RpcCallbackProxyGenerator().newInstance(serviceClassInfo, transportKey);
                } catch (IllegalAccessException e) {
                    //TODO
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    //TODO
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    //TODO
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    //TODO
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    //TODO
                    e.printStackTrace();
                }
                RpcCallbackHandler.addServerCallbackProxy(transportKey, callback);
                requestMessageBody.getParams()[methodInfo.getCallbackIndex()] = callback;
            }
        }

        ResponseMessageBody messageBody;
        try {
            messageBody = rpcServiceHandler.handler(requestMessageBody.getParams());
            if (messageBody == null) {
                messageBody = new ResponseMessageBody();
            }
        } catch (Exception e) {
            messageBody = new ResponseMessageBody(new RpcException(e));
        }

        messageBody.setService(requestMessageBody.getService());
        messageBody.setMethod(requestMessageBody.getMethod());

        byte[] body = SerializeTool.serialize(messageBody);
        message.setTotalLength(ServiceConfig.HEADER_SIZE + body.length);
        message.setBodyLength(body.length);
        message.setBody(body);

        ctx.writeAndFlush(message, ctx.voidPromise());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
