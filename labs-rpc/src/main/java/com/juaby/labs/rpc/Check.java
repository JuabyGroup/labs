package com.juaby.labs.rpc;

import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Juaby on 2015/8/26.
 */
public class Check<O, F> implements MessageService<O, F> {

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
    public <T> TestResult message2(TestBean testBean, List<String> param) {
        return null;
    }

    @Override
    public List<Map<String, File>> message3(TestBean testBean, Map<String, List<TestBean>> param) throws NegativeArraySizeException, IOException {
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
    public <T> O messageENUM(List<F> param, com.juaby.labs.rpc.proxy.TypeObject t) {
        return null;
    }
}
