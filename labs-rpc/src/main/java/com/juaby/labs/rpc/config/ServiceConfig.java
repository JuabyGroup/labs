package com.juaby.labs.rpc.config;

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
