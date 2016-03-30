package com.juaby.labs.rpc.util;

import com.juaby.labs.rpc.message.ResponseMessageBody;
import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;

import java.net.InetSocketAddress;
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
public class ResultFutureHelper {

    private static final Map<Integer, RpcFutureImpl<ResponseMessageBody>> resultFutureMap = new ConcurrentHashMap<Integer, RpcFutureImpl<ResponseMessageBody>>();

    public static void handleRead(RpcMessage message, InetSocketAddress inetSocketAddress) {
        ResponseMessageBody responseMessageBody = new ResponseMessageBody();
        SerializeTool.deserialize(message.getBody(), responseMessageBody);

        //TODO
        String service = responseMessageBody.getService();
        String method = responseMessageBody.getMethod();
        String transportKey = inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort() + ":" + service + ":" + method;
        ServiceClassInfo.MethodInfo methodInfo = ServiceClassInfoHelper.get(service).getMethods().get(method);

        Integer messageId = message.getId();

        if(message != null && messageId != null && messageId.intValue() == -1 && methodInfo.isCallback()) {
            //TODO
            RpcCallback callback = RpcCallbackHandler.getClientCallbackProxy(transportKey);
            RpcCallbackHandler.handler(callback, responseMessageBody.getBody());
        }

        if (message != null && messageId != null && messageId.intValue() != -1) {
            RpcFutureImpl<ResponseMessageBody> resultFuture = map().get(messageId);
            if (resultFuture != null) {
                resultFuture.result(responseMessageBody);
            }
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
