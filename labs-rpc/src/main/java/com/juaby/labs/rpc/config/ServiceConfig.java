package com.juaby.labs.rpc.config;

import java.lang.management.ManagementFactory;

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

    private volatile int serverType;

    public final static String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    public static final int HEADER_SIZE = 12 + 4 + 4;

    public static final int MAX_OBJECT_SIZE = 1 * 1024 * 1024;

    public ServiceConfig() {
    }

    public ServiceConfig(String name) {
        this.name = name;
    }

    public ServiceConfig(String name, int serverType) {
        this.name = name;
        this.serverType = serverType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServerType() {
        return serverType;
    }

    public void setServerType(int serverType) {
        this.serverType = serverType;
    }

}
