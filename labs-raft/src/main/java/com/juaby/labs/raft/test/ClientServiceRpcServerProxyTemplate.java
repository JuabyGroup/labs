package com.juaby.labs.raft.test;

import com.juaby.labs.raft.client.ClientService;
import com.juaby.labs.rpc.message.ResponseMessageBody;
import com.juaby.labs.rpc.server.RpcServiceHandler;
import com.juaby.labs.rpc.test.MessageService;
import com.juaby.labs.rpc.util.Endpoint;


/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/26 13:14.
 */
public class ClientServiceRpcServerProxyTemplate implements RpcServiceHandler {

    private ClientService clientService;

    public ClientServiceRpcServerProxyTemplate(MessageService messageService) {
        this.clientService = clientService;
    }

    @Override
    public ResponseMessageBody<Boolean> handler(Object[] params) {
        Endpoint endpoint = (Endpoint)params[0];
        Boolean result = clientService.removeServer(endpoint);
        ResponseMessageBody<Boolean> messageBody = new ResponseMessageBody<Boolean>();
        messageBody.setBody(result);
        messageBody.setReturnClass("ReturnTypeDesc");
        return messageBody;
    }

}
