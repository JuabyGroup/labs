package com.juaby.labs.rpc;

import org.glassfish.grizzly.samples.filterchain.MessageBody;

import java.util.List;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/26 13:14.
 */
public class ProviderServiceProxy implements ProviderService {

    private MessageService messageService;

    public ProviderServiceProxy(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public MessageBody<TestResult> handler(Object[] params) {
        TestBean testBean = (TestBean)params[0];
        List<String> param = (List<String>)params[1];
        TestResult result = messageService.message(testBean, param);
        MessageBody<TestResult> messageBody = new MessageBody<TestResult>();
        messageBody.setBody(result);
        messageBody.setReturnClass(result.getClass().getName());
        return messageBody;
    }

}
