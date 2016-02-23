package com.juaby.labs.rpc.proxy;

import com.juaby.labs.rpc.message.RequestMessageBody;
import com.juaby.labs.rpc.client.RpcClient;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.config.ServiceConfigHelper;
import com.juaby.labs.rpc.util.RpcCallback;
import com.juaby.labs.rpc.util.RpcCallbackHandler;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 17:08.
 */
public class RpcClientProxy {

    protected ServiceConfig config;

    protected <R> R sendMessage(String service, String method, Object[] params) {
        RequestMessageBody requestMessageBody = new RequestMessageBody(service);
        requestMessageBody.setMethod(method);
        requestMessageBody.setParams(params);

        ServiceClassInfo.MethodInfo methodInfo = ServiceClassInfoHelper.get(service).getMethods().get(method);
        if(methodInfo.isCallback()) {
            //TODO
            RpcCallbackHandler.addCallback(service + method, (RpcCallback) params[methodInfo.getCallbackIndex()]);
            params[methodInfo.getCallbackIndex()] = null;
        }

        //TODO
        return RpcClient.sendMessage(requestMessageBody);
    }

    protected void setConfig(String service) {
        this.config = ServiceConfigHelper.getConfig(service);
    }

    protected ServiceConfig getConfig() {
        return this.config;
    }

}
