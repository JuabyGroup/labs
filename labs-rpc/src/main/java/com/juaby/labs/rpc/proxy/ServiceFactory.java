package com.juaby.labs.rpc.proxy;

import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by juaby on 16-2-22.
 */
public class ServiceFactory {

    public static <S> S getService(ServiceConfig<S> serviceConfig) {
        ServiceClassInfo classInfo = ServiceClassInfoHelper.get(serviceConfig.getName());
        Class<S> serviceClass = null;
        try {
            serviceClass = (Class<S>) Class.forName(classInfo.getId());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            return new RpcClientProxyGenerator().newInstance(classInfo, serviceClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
