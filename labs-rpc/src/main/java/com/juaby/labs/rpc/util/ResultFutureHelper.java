package com.juaby.labs.rpc.util;

import com.juaby.labs.rpc.message.ResponseMessageBody;
import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.streams.Stream;

import java.io.IOException;
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

    public static final class CustomClientFilter extends BaseFilter {

        public CustomClientFilter() {
        }

        @Override
        public NextAction handleRead(FilterChainContext ctx) throws IOException {
            final RpcMessage message = ctx.getMessage();

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

            return ctx.getStopAction();
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
