package com.juaby.labs.rpc;

import org.glassfish.grizzly.samples.filterchain.GIOPServer;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 17:09.
 */
public class Test {

    public static void main(String[] args) {
        /*ServiceConfig config = new ServiceConfig(MessageService.class.getName());
        Endpoint endpoint = new Endpoint(RpcServer.HOST, RpcServer.PORT);
        EndpointHelper.add(config.getName(), endpoint);

        MessageService messageService = new RpcClientProxy(config);

        TestBean bean = new TestBean();
        bean.setId("100");
        bean.setCode("200");
        bean.setMessage("我来了");
        List<String> params = new ArrayList<String>();
        params.add("COME HERE!");
        TestResult result = messageService.message(bean, params);
        System.out.println(result.getContent());*/
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
