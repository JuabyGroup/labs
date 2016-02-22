package com.juaby.labs.rpc.util;

import com.juaby.labs.rpc.message.ResponseMessageBody;
import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
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

    private static final Map<Integer, RpcFutureImpl<ResponseMessageBody>> resultFutureMap = new ConcurrentHashMap<Integer, RpcFutureImpl<ResponseMessageBody>>();

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

            ResponseMessageBody responseMessageBody = new ResponseMessageBody();
            SerializeTool.deserialize(message.getBody(), responseMessageBody);

            //TODO
            String service = responseMessageBody.getService();
            String method = responseMessageBody.getMethod();
            String key = service + method;
            ServiceClassInfo.MethodInfo methodInfo = ServiceClassInfoHelper.get(service).getMethods().get(method);
            if(methodInfo.isCallback()) {
                //TODO
                RpcCallback callback = RpcCallbackHandler.getCallback(key);
                RpcCallbackHandler.handler(callback, responseMessageBody.getBody());
            }

            if (message != null) {
                Integer messageId = message.getId();
                RpcFutureImpl<ResponseMessageBody> resultFuture = resultFutureMap.get(messageId);
                if (resultFuture != null) {
                    resultFuture.result(responseMessageBody);
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

    public static ResponseMessageBody result(Integer messageId) throws InterruptedException, ExecutionException, TimeoutException {
        if (messageId == null) {
            return null;
        }
        RpcFutureImpl<ResponseMessageBody> future = map().get(messageId);
        return future.get(10, TimeUnit.SECONDS); //TODO
    }

    public static Map<Integer, RpcFutureImpl<ResponseMessageBody>> map() {
        return resultFutureMap;
    }

}
