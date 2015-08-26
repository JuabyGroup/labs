package com.juaby.labs.rpc;

import org.glassfish.grizzly.samples.filterchain.GIOPServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 17:09.
 */
public class Test {

    public static void main(String[] args) {
        ServiceConfig config = new ServiceConfig(MessageService.class.getName());
        Endpoint endpoint = new Endpoint(GIOPServer.HOST, GIOPServer.PORT);
        EndpointHelper.add(config.getName(), endpoint);

        MessageService messageService = new MessageClientServiceImpl(config);

        TestBean bean = new TestBean();
        bean.setId("100");
        bean.setCode("200");
        bean.setMessage("我来了");
        List<String> params = new ArrayList<String>();
        params.add("COME HERE!");
        TestResult result = messageService.message(bean, params);
        System.out.println(result.getContent());
    }

}
