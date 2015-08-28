package com.juaby.labs.rpc;

import org.glassfish.grizzly.samples.filterchain.GIOPClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 17:08.
 */
public class MessageServiceClientProxy implements MessageService {

    private ServiceConfig config;

    public MessageServiceClientProxy(ServiceConfig config) {
        this.config = config;
    }

    @Override
    public TestResult message(TestBean testBean, List<String> params) {
        TestResult result = null;
        RequestMessageBody requestMessageBody = new RequestMessageBody(getClass().getInterfaces()[0].getName());
        requestMessageBody.setMethod("message");
        requestMessageBody.setParams(new Object[] {testBean, params});
        try {
            result = GIOPClient.sendMessage(requestMessageBody);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public TestResult message2(TestBean testBean, List<String> params) {
        TestResult result = null;
        RequestMessageBody requestMessageBody = new RequestMessageBody(getClass().getInterfaces()[0].getName());
        requestMessageBody.setMethod("message");
        requestMessageBody.setParams(new Object[] {testBean, params});
        try {
            result = GIOPClient.sendMessage(requestMessageBody);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int message3(TestBean testBean, Map<String, List<TestBean>> param) {
        return 0;
    }
}
