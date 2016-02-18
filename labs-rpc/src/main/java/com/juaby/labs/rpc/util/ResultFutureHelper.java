package com.juaby.labs.rpc.util;

import com.juaby.labs.rpc.message.RpcMessage;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

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

    private static final Map<Integer, RpcFutureImpl<RpcMessage>> resultFutureMap = new ConcurrentHashMap<Integer, RpcFutureImpl<RpcMessage>>();

    public static final class CustomClientFilter extends BaseFilter {

        public CustomClientFilter() {
        }

        @Override
        public NextAction handleRead(FilterChainContext ctx) throws IOException {
            final RpcMessage message = ctx.getMessage();
            if (message != null) {
                Integer messageId = message.getId();
                RpcFutureImpl<RpcMessage> resultFuture = resultFutureMap.get(messageId);
                if (resultFuture != null) {
                    resultFuture.result(message);
                }
            }

            return ctx.getStopAction();
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
