package com.juaby.labs.rpc;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 17:08.
 */
public class MessageServerServiceImpl implements MessageService {

    @Override
    public TestResult message(TestBean testBean, List<String> param) {
        TestResult result = new TestResult();
        result.setId("2000");
        result.setName("RESULT_NAME");
        result.setContent("COME ON BABY");
        result.setTime(new Date());
        return result;
    }

    @Override
    public TestResult message2(TestBean testBean, List<String> param) {
        return null;
    }

    @Override
    public int message3(TestBean testBean, Map<String, List<TestBean>> param) {
        return 0;
    }

}
