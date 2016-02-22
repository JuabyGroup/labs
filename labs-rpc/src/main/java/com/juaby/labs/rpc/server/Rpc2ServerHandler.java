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
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.test.Callback2Test;
import com.juaby.labs.rpc.test.CallbackTest;
import com.juaby.labs.rpc.util.RpcCallbackHandler;
import com.juaby.labs.rpc.util.SerializeTool;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

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

        ServiceClassInfo.MethodInfo methodInfo = ServiceClassInfoHelper.get(requestMessageBody.getService()).getMethods().get(requestMessageBody.getMethod());
        if(methodInfo.isCallback()) {
            //TODO
            RpcCallbackHandler.addCallbackChannel(requestMessageBody.getService() + requestMessageBody.getMethod(), ctx.channel());
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

        Callback2Test.main(new String[] {}); //TODO

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
