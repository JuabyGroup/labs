package com.juaby.labs.rpc.test;

import com.lmax.disruptor.EventFactory;

/**
 * Created by juaby on 16-6-6.
 */
public class LongEventFactory implements EventFactory<LongEvent> {

    public LongEvent newInstance() {
        return new LongEvent();
    }

}