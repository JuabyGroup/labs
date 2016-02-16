package com.juaby.labs.rpc.proxy;

import com.juaby.labs.rpc.MessageService;
import com.juaby.labs.rpc.TestBean;
import com.juaby.labs.rpc.TestResult;
import com.juaby.labs.rpc.base.ResponseMessageBody;
import com.juaby.labs.rpc.server.ProviderService;

import java.util.List;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/26 13:14.
 */
public class ProviderServiceProxyWithResultTemplate implements ProviderService {

    private MessageService messageService;

    public ProviderServiceProxyWithResultTemplate(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public ResponseMessageBody<TestResult> handler(Object[] params) {
        TestBean testBean = (TestBean)params[0];
        List<String> param = (List<String>)params[1];
        TestResult result = messageService.message(testBean, param);
        ResponseMessageBody<TestResult> messageBody = new ResponseMessageBody<TestResult>();
        messageBody.setBody(result);
        messageBody.setReturnClass("ReturnTypeDesc");
        return messageBody;
    }

}
