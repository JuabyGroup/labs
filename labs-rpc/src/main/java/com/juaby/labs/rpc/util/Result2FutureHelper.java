package com.juaby.labs.rpc.util;

import com.juaby.labs.rpc.base.RpcMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 13:38.
 */
public class Result2FutureHelper {

    private static final Map<Integer, RpcFutureImpl<RpcMessage>> resultFutureMap = new ConcurrentHashMap<Integer, RpcFutureImpl<RpcMessage>>();

    /**
     * Handler implementation for the object echo client.  It initiates the
     * ping-pong traffic between the object echo client and server by sending the
     * first message to the server.
     */
    public static final class Rpc2ClientHandler extends ChannelInboundHandlerAdapter {

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
            final RpcMessage message = (RpcMessage) msg;
            if (message != null) {
                Integer messageId = message.getId();
                RpcFutureImpl<RpcMessage> resultFuture = resultFutureMap.get(messageId);
                if (resultFuture != null) {
                    resultFuture.result(message);
                }
            }
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

    public static RpcMessage result(Integer messageId) throws InterruptedException, ExecutionException, TimeoutException {
        if (messageId == null) {
            return null;
        }
        RpcFutureImpl<RpcMessage> future = map().get(messageId);
        return future.get(10, TimeUnit.SECONDS); //TODO
    }

    public static Map<Integer, RpcFutureImpl<RpcMessage>> map() {
        return resultFutureMap;
    }

}
