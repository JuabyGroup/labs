package com.juaby.labs.rpc;

/**
 * Created by Juaby on 2015/8/28.
 */
public interface InterfaceObject extends InterfaceObject2 {

    public <V> V vmethod();
    public <V> void vmethod(V v);
    public void vmethod3();
    public <V, R> R vmethod2(V v);

}
