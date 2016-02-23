package com.juaby.labs.rpc.client;

import com.juaby.labs.rpc.message.ResponseMessageBody;
import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.util.*;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;

/**
 * Created by chaos on 16-2-23.
 */
public class CustomClientFilter extends BaseFilter {

    public CustomClientFilter() {
    }

    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        final RpcMessage message = ctx.getMessage();

        ResultFutureHelper.handleRead(message);

        return ctx.getStopAction();
    }

}
