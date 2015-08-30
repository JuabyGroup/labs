package com.juaby.labs.rpc;

/**
 * Created by Juaby on 2015/8/28.
 */
public interface InterfaceObject {

    public <V> V vmethod();
    public <V> void vmethod(V v);
    public <V, R> R vmethod2(V v);

}
