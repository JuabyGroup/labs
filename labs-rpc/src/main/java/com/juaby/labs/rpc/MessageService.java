package com.juaby.labs.rpc;

import java.util.List;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 17:03.
 */
public interface MessageService {

    public static final String A = "";

    public TestResult message(TestBean testBean, List<String> param);

}
