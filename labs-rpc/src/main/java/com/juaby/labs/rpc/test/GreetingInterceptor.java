package com.juaby.labs.rpc.test;

/**
 * Created by juaby on 16-5-19.
 */
public class GreetingInterceptor {

    public Object greet(Object argument) {
        return "Hello from " + argument;
    }

}
