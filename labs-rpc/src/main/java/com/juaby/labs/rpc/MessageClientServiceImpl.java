package com.juaby.labs.rpc;

import org.glassfish.grizzly.samples.filterchain.GIOPClient;

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
public class MessageClientServiceImpl implements MessageService {

    private ServiceConfig config;

    public MessageClientServiceImpl(ServiceConfig config) {
        this.config = config;
    }

    @Override
    public TestResult message(TestBean testBean, String param) {
        TestResult result = new TestResult();
        try {
            result = GIOPClient.sendMessage(config, testBean, result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return result;
    }

}
