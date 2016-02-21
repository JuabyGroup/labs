package com.juaby.labs.rpc.config;

import com.juaby.labs.rpc.proxy.ProxyHelper;
import com.juaby.labs.rpc.proxy.RpcServerProxyGenerator;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.server.RpcServiceHandler;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;

import java.lang.reflect.InvocationTargetException;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 18:02.
 */
public class ServiceConfig {

    private volatile String name;

    public static final int HEADER_SIZE = 12 + 4 + 4;

    public static final int MAX_OBJECT_SIZE = 1 * 1024 * 1024;

    private int serviceType;

    private ServerConfig serverConfig;

    public ServiceConfig(int serviceType) {
        this.serviceType = serviceType;
    }

    public ServiceConfig(int serviceType, Class<?> ... classes) {
        this.serviceType = serviceType;
        if (classes == null || classes.length == 0) {
            //TODO
        }
        for (Class serviceClass : classes) {
            ServiceClassInfo classInfo = ServiceClassInfoHelper.parser(serviceClass);
            Class<RpcServiceHandler> rpcServiceHandlerClass = RpcServiceHandler.class;
            for (String methodSignature : classInfo.getMethods().keySet()) {
                RpcServiceHandler rpcServiceHandler = null;
                try {
                    rpcServiceHandler = new RpcServerProxyGenerator().newInstance(classInfo, classInfo.getMethods().get(methodSignature), rpcServiceHandlerClass);
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
                ProxyHelper.addProxyInstance(classInfo.getName() + methodSignature, rpcServiceHandler);
            }
        }
    }

    public ServiceConfig(int serviceType, ServerConfig serverConfig, Class<?> ... classes) {
        this(serviceType, classes);
        this.serverConfig = serverConfig;
    }

    public <I> ServiceConfig(int serviceType, Class<?> serviceClass, I instance) {
        this(serviceType);
        if (serviceClass == null) {
            //TODO
        }
        ServiceClassInfo classInfo = ServiceClassInfoHelper.parser(serviceClass);
        ProxyHelper.addServiceInstance(classInfo.getName(), instance);
    }

    public ServiceConfig(int serviceType, String name) {
        this.serviceType = serviceType;
        this.name = name;
    }

    public ServiceConfig(int serviceType, ServerConfig serverConfig) {
        this.serviceType = serviceType;
        this.serverConfig = serverConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addServices(Class<?> ... classes) {
        if (classes == null || classes.length == 0) {
            //TODO
        }
        for (Class serviceClass : classes) {
            ServiceClassInfo classInfo = ServiceClassInfoHelper.parser(serviceClass);
            Class<RpcServiceHandler> rpcServiceHandlerClass = RpcServiceHandler.class;
            for (String methodSignature : classInfo.getMethods().keySet()) {
                RpcServiceHandler rpcServiceHandler = null;
                try {
                    rpcServiceHandler = new RpcServerProxyGenerator().newInstance(classInfo, classInfo.getMethods().get(methodSignature), rpcServiceHandlerClass);
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
                ProxyHelper.addProxyInstance(classInfo.getName() + methodSignature, rpcServiceHandler);
            }
        }
    }

    public void addService(Class<?> serviceClass) {
        if (serviceClass == null) {
            //TODO
        }
        ServiceClassInfo classInfo = ServiceClassInfoHelper.parser(serviceClass);
        Class<RpcServiceHandler> rpcServiceHandlerClass = RpcServiceHandler.class;
        for (String methodSignature : classInfo.getMethods().keySet()) {
            RpcServiceHandler rpcServiceHandler = null;
            try {
                rpcServiceHandler = new RpcServerProxyGenerator().newInstance(classInfo, classInfo.getMethods().get(methodSignature), rpcServiceHandlerClass);
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
            ProxyHelper.addProxyInstance(classInfo.getName() + methodSignature, rpcServiceHandler);
        }
    }

    public <I> void addServiceInstance(Class<?> serviceClass, I instance) {
        if (serviceClass == null) {
            //TODO
        }
        ServiceClassInfo classInfo = ServiceClassInfoHelper.parser(serviceClass);
        ProxyHelper.addServiceInstance(classInfo.getName(), instance);
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

}
