package com.juaby.labs.rpc;

import java.util.UUID;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 15:41.
 */
public class MessageIdGenerator {

    public static String id() {
        String messageId = UUID.randomUUID().toString().replaceAll("-", "");
        return messageId;
    }

}
