package com.juaby.labs.rpc.test;

import com.juaby.labs.rpc.util.RpcCallback;

import java.io.File;
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
public class MessageServerServiceImpl<O, F> implements MessageService<O, F> {

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
    public TestResult message(TestBean testBean, List<String> param, RpcCallback callback) {
        TestResult result = new TestResult();
        result.setId("2000");
        result.setName("RESULT_NAME");
        result.setContent("COME ON BABY");
        result.setTime(new Date());
        return result;
    }

    @Override
    public <T> TestResult message2(TestBean testBean, List<String> param) {
        return null;
    }

    @Override
    public List<Map<String, File>> message3(TestBean testBean, Map<String, List<TestBean>> param) {
        return null;
    }

    @Override
    public List<Map<String, File>> message4(TestBean testBean, Map<String, List<TestBean>> param, List<TestBean> t) {
        return null;
    }

    @Override
    public <V> V vmethod() {
        return null;
    }

    @Override
    public <V> void vmethod(V v) {

    }

    @Override
    public void vmethod3() {

    }

    @Override
    public <V, R> R vmethod2(V v) {
        return (R)v;
    }

    @Override
    public <T> TestResult message2(TestBean testBean, List<String> param, T t) {
        return null;
    }

    @Override
    public <T> O message2(List<F> param, O t) {
        return null;
    }

    @Override
    public <T> O messageENUM(List<F> param, TypeObject t) {
        return null;
    }
}
