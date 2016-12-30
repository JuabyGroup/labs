package com.juaby.labs.rpc.test;

import com.juaby.labs.rpc.proxy.RpcClientProxy;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * Created by juaby on 16-5-20.
 */
public class Agent {

    @RuntimeType
    public static Object intercept(@Origin Method method, @AllArguments Object[] args) {
        System.out.println("Invoked method with: " + args);
        return args;
    }

}