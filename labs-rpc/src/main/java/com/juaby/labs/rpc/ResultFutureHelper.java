package com.juaby.labs.rpc;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.impl.FutureImpl;
import org.glassfish.grizzly.samples.filterchain.GIOPMessage;
import org.glassfish.grizzly.utils.Charsets;

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

    private static final Map<String, FutureImpl<GIOPMessage>> resultFutureMap = new ConcurrentHashMap<String, FutureImpl<GIOPMessage>>();

    public static final class CustomClientFilter extends BaseFilter {

        public CustomClientFilter() {
        }

        @Override
        public NextAction handleRead(FilterChainContext ctx) throws IOException {
            final GIOPMessage message = ctx.getMessage();
            if (message != null) {
                String messageId = new String(message.getId(), Charsets.UTF8_CHARSET);
                FutureImpl<GIOPMessage> resultFuture = resultFutureMap.get(messageId);
                if (resultFuture != null) {
                    resultFuture.result(message);
                }
            }

            return ctx.getStopAction();
        }
    }

    public static GIOPMessage result(String messageId) throws InterruptedException, ExecutionException, TimeoutException {
        if (messageId == null || messageId.length() == 0) {
            return null;
        }
        return map().get(messageId).get(10, TimeUnit.SECONDS);
    }

    public static Map<String, FutureImpl<GIOPMessage>> map() {
        return resultFutureMap;
    }

}
