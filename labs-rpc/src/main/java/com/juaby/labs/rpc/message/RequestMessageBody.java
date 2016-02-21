package com.juaby.labs.rpc.message;

import java.util.Arrays;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/26 13:01.
 */
public class RequestMessageBody extends MessageBody {

    private Object[] params;

    public RequestMessageBody() {
    }

    public RequestMessageBody(String service) {
        super(service);
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
                ", params=" + Arrays.toString(params) +
                '}';
    }

}
