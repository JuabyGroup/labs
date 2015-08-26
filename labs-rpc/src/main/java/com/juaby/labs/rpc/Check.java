package com.juaby.labs.rpc;

import java.util.List;

/**
 * Created by Juaby on 2015/8/26.
 */
public class Check implements MessageService {

    public static void main(String[] args) {
        System.out.println(Check.class.getInterfaces()[0].getName());
    }

    @Override
    public TestResult message(TestBean testBean, List<String> param) {
        return null;
    }

    @Override
    public TestResult message2(TestBean testBean, List<String> param) {
        return null;
    }

    @Override
    public int message3(TestBean testBean, List<String> param) {
        return 0;
    }
}
