package com.juaby.labs.rpc;

import org.glassfish.grizzly.samples.filterchain.GIOPServer;

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
        String service = "service-1";
        Endpoint endpoint = new Endpoint(GIOPServer.HOST, GIOPServer.PORT);
        EndpointHelper.add(service, endpoint);
        ServiceConfig config = new ServiceConfig(service);
        MessageService messageService = new MessageClientServiceImpl(config);
        TestBean bean = new TestBean();
        bean.setId("100");
        bean.setCode("200");
        bean.setMessage("我来了");
        TestResult result = messageService.message(bean);
        System.out.println(result.getContent());
    }

}
