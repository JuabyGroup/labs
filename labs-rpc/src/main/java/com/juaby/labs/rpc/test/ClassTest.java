package com.juaby.labs.rpc.test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 17:09.
 */
public class ClassTest {

    public static void main(String[] args) {
        Method[] methods = MessageService.class.getMethods();
        for (Method method : methods) {
            System.out.println("method:" + method.getName());// 方法名
            // //////////////方法的参数
            System.out.println(" paramTypeType: ");
            Type[] paramTypeList = method.getGenericParameterTypes();// 方法的参数列表
            for (Type paramType : paramTypeList) {
                System.out.println("  " + paramType.getTypeName());// 参数类型
                if (paramType instanceof ParameterizedType)/**//* 如果是泛型类型 */{
                    Type[] types = ((ParameterizedType) paramType)
                            .getActualTypeArguments();// 泛型类型列表
                    System.out.println("  TypeArgument: ");
                    for (Type type : types) {
                        System.out.println("   " + type.getTypeName());
                    }
                }
            }
            // //////////////方法的返回值
            System.out.println(" returnType: ");
            Type returnType = method.getGenericReturnType();// 返回类型
            System.out.println("  " + returnType);
            if (returnType instanceof ParameterizedType)/**//* 如果是泛型类型 */{
                Type[] types = ((ParameterizedType) returnType)
                        .getActualTypeArguments();// 泛型类型列表
                System.out.println("  TypeArgument: ");
                for (Type type : types) {
                    System.out.println("   " + type);
                }
            }
        }
    }

}
