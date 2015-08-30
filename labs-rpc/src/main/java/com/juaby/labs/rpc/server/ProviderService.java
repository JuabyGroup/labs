package com.juaby.labs.rpc.server;

import com.juaby.labs.rpc.base.ResponseMessageBody;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/26 13:36.
 */
public interface ProviderService {

    public <R> ResponseMessageBody<R> handler(Object[] params);

}