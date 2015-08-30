package com.juaby.labs.rpc.util;

import com.juaby.labs.rpc.proxy.ServiceClassInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Juaby on 2015/8/26.
 */
public class ServiceClassInfoHelper {

    private static final Map<String, ServiceClassInfo> serviceClassInfoCache = new ConcurrentHashMap<String, ServiceClassInfo>();

    public static ServiceClassInfo get(Class service) {
        if (service == null) {
            return null;
        }
        return parser(service);
    }

    public static ServiceClassInfo parser(Class service) {
        if (serviceClassInfoCache.containsKey(service.getName())) {
            return serviceClassInfoCache.get(service.getName());
        }
        ServiceClassInfo serviceClassInfo = new ServiceClassInfo(service.getName());
        Field[] fields = service.getFields();
        serviceClassInfo.setFields(fields);
        Method[] methods = service.getMethods();
        if (methods == null || methods.length == 0) {
            return serviceClassInfo;
        }
        ServiceClassInfo.MethodInfo[] methodInfos = new ServiceClassInfo.MethodInfo[methods.length];
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            ServiceClassInfo.MethodInfo methodInfo = serviceClassInfo.new MethodInfo();
            methodInfo.setMethod(method); // 方法
            // 方法的参数
            Type[] paramTypes = method.getGenericParameterTypes();// 方法的参数列表
            ServiceClassInfo.ParamInfo[] paramInfos = new ServiceClassInfo.ParamInfo[paramTypes.length];

            for (int p = 0; p < paramTypes.length; p++) {
                Type paramType = paramTypes[p];
                ServiceClassInfo.ParamInfo paramInfo = serviceClassInfo.new ParamInfo();
                paramInfo.setParamType(paramType);
                // 如果是泛型类型
                if (paramType instanceof ParameterizedType) {
                    Type[] types = ((ParameterizedType) paramType).getActualTypeArguments();
                    paramInfo.setParameterizedTypes(types);
                }
                paramInfos[p] = paramInfo;
            }
            methodInfo.setParamTypes(paramInfos);
            // 方法的返回值
            Type returnType = method.getGenericReturnType();// 返回类型
            ServiceClassInfo.ReturnInfo returnInfo = serviceClassInfo.new ReturnInfo();
            returnInfo.setReturnType(returnType);
            //* 如果是泛型类型
            if (returnType instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) returnType).getActualTypeArguments();
                returnInfo.setParameterizedTypes(types);
            }
            methodInfo.setReturnInfo(returnInfo);
            methodInfos[i] = methodInfo;
        }
        serviceClassInfo.setMethods(methodInfos);
        serviceClassInfoCache.put(service.getName(), serviceClassInfo);
        return serviceClassInfo;
    }

}
