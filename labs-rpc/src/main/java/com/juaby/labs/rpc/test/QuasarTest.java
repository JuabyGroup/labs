package com.juaby.labs.rpc.test;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;
import co.paralleluniverse.strands.concurrent.CountDownLatch;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by juaby on 16-6-7.
 */
public class QuasarTest {

    private static void printer(Channel<Integer> in) throws SuspendExecution, InterruptedException {
        Integer v;
        while ((v = in.receive()) != null) {
            System.out.println(v);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, SuspendExecution {
        //定义两个Channel
        Channel<Integer> naturals = Channels.newChannel(-1);
        Channel<Integer> squares = Channels.newChannel(-1);

        //运行两个Fiber实现.
        new Fiber(() -> {
            for (int i = 0; i < 10; i++)
                naturals.send(i);
            naturals.close();
        }).start();

        new Fiber(() -> {
            Integer v;
            while ((v = naturals.receive()) != null)
                squares.send(v * v);
            squares.close();
        }).start();

        printer(squares);

        int FiberNumber = 5_000_000;
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < FiberNumber; i++) {
            new Fiber(() -> {
                counter.incrementAndGet();
                if (counter.get() == FiberNumber) {
                    System.out.println("done");
                }
                Strand.sleep(1000000);
            }).start();
        }
        latch.await();
    }

}
