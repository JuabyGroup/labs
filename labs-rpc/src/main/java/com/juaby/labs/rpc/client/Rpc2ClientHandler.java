package com.juaby.labs.rpc.client;

import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.util.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

/**
 * Created by chaos on 16-2-23.
 */
public class Rpc2ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * Creates a client-side handler.
     */
    public Rpc2ClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send the first message if this handler is a client-side handler.
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        final Object localAddress = ctx.channel().localAddress();
        final InetSocketAddress inetSocketAddress = (InetSocketAddress) localAddress;
        final RpcMessage message = (RpcMessage) msg;

        ResultFutureHelper.handleRead(message, inetSocketAddress);
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
