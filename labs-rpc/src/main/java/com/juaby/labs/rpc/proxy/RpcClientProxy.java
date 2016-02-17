package com.juaby.labs.rpc.proxy;

import com.juaby.labs.rpc.base.RequestMessageBody;
import com.juaby.labs.rpc.client.Rpc2Client;
import com.juaby.labs.rpc.client.RpcClient;
import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.config.ServiceConfigHelper;

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
        if (config.getServerType() == 1) {
            return RpcClient.sendMessage(requestMessageBody);
        } else if (config.getServerType() == 2) {
            return Rpc2Client.sendMessage(requestMessageBody);
        } else {
            //TODO
            return null;
        }
    }

    protected void setConfig(String service) {
        this.config = ServiceConfigHelper.getConfig(service);
    }

    protected ServiceConfig getConfig() {
        return this.config;
    }

}
