package com.juaby.labs.rpc;

import org.objectweb.asm.Type;

import java.util.List;
import java.util.Map;

/**
 * Created by Juaby on 2015/8/26.
 */
public class Check implements MessageService {

    public static void main(String[] args) {
        System.out.println(Check.class.getInterfaces()[0].getName());
        Type r = Type.getReturnType((MessageService.class.getMethods()[2]));
        Type[] p = Type.getArgumentTypes((MessageService.class.getMethods()[2]));
        System.out.println(Type.getMethodDescriptor(r, p));
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
    public int message3(TestBean testBean, Map<String, List<TestBean>> param) {
        return 0;
    }
}
