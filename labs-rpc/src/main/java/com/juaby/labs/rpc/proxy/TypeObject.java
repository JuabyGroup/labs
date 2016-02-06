package com.juaby.labs.rpc.proxy;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/9/2 10:10.
 */
public enum TypeObject {
    A(1),B(2),C(3);

    TypeObject(Integer i) {
        value = i;
    }

    private Integer value;

    public Integer value() {
        return value;
    }
}
