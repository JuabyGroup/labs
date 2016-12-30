package com.juaby.labs.rpc.test;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by juaby on 16-6-6.
 */
public class LongEventMain {

    public static void main(String[] args) throws Exception {
        // The factory for the event
        LongEventFactory factory = new LongEventFactory();

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(factory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new BusySpinWaitStrategy());

        // Connect the handler
        disruptor.handleEventsWith(new LongEventHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        //LongEventProducer producer = new LongEventProducer(ringBuffer);
        LongEventProducerWithTranslator producerWithTranslator = new LongEventProducerWithTranslator(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);
        long s = System.nanoTime();
        for (long l = 0; l < 10000000; l++) {
            bb.putLong(0, l);
            //producer.onData(bb);
            producerWithTranslator.onData(bb);
            //Thread.sleep(1000);
        }
        disruptor.halt();
        System.out.println((System.nanoTime() - s) / (1000 * 1000 * 1000));
    }

}
