package com.juaby.labs.rpc;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 17:03.
 */
public interface MessageService<O, F> extends InterfaceObject {

    public static final String A = "V";
    public static final int B = TypeObject.B.value();

    public TestResult message(TestBean testBean, List<String> param);
    public <T> TestResult message2(TestBean testBean, List<String> param);
    public <T> TestResult message2(TestBean testBean, List<String> param, T t);
    public <T> O message2(List<F> param, O t);
    public List<Map<String, File>> message3(TestBean testBean, Map<String, List<TestBean>> param) throws NegativeArraySizeException, IOException;
    public List<Map<String, File>> message4(TestBean testBean, Map<String, List<TestBean>> param, List<TestBean> t) throws NegativeArraySizeException, IOException;
    public <T> O messageENUM(List<F> param, TypeObject t);

}
