package com.juaby.labs.raft.test;

import com.juaby.labs.raft.client.ClientService;
import com.juaby.labs.rpc.message.ResponseMessageBody;
import com.juaby.labs.rpc.server.RpcServiceHandler;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/26 13:14.
 */
public class ClientServiceTemplate implements RpcServiceHandler {

    private ClientService clientService;

    public ClientServiceTemplate(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public ResponseMessageBody handler(Object[] params) {
        double endpoint = (double)params[0];
        clientService.dm(endpoint);
        ResponseMessageBody messageBody = new ResponseMessageBody();
        messageBody.setBody(null);
        messageBody.setReturnClass("ReturnTypeDesc");
        return messageBody;
    }

}
