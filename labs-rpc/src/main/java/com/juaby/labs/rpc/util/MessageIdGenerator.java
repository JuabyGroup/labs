package com.juaby.labs.rpc.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 15:41.
 */
public class MessageIdGenerator {

    private static AtomicInteger idCounter = new AtomicInteger();

    public static Integer id() {
        return idCounter.getAndIncrement();
    }

}
