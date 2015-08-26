package com.juaby.labs.rpc;

import java.util.Arrays;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/26 13:01.
 */
public class RequestMessageBody {

    private String service;

    private String method;

    private Object[] params;

    public RequestMessageBody() {
    }

    public RequestMessageBody(String service) {
        this.service = service;
    }

    public RequestMessageBody(String service, String method, Object[] params) {
        this.service = service;
        this.method = method;
        this.params = params;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "RequestMessageBody{" +
                "service='" + service + '\'' +
                ", method='" + method + '\'' +
                ", params=" + Arrays.toString(params) +
                '}';
    }

}
