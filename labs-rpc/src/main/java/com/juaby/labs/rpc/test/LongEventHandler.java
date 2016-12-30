package com.juaby.labs.rpc.test;

import com.lmax.disruptor.EventHandler;

/**
 * Created by juaby on 16-6-6.
 */
public class LongEventHandler implements EventHandler<LongEvent> {

    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
        //System.out.println("Event: " + event);
    }

}