package com.juaby.labs.rpc.test;

/**
 * Created by Juaby on 2015/8/28.
 */
public interface InterfaceObject3 {

    public <V> V vmethod();
    public <V> void vmethod(V v);
    public <V, R> R vmethod2(V v);

}
