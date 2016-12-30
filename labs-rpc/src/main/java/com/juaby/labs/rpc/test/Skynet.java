package com.juaby.labs.rpc.test;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.channels.Channel;

import java.util.Arrays;
import java.util.Random;

import static co.paralleluniverse.strands.channels.Channels.newChannel;

/**
 * Created by juaby on 16-6-7.
 */
public class Skynet {

    private static Random random = new Random();
    private static final int NUMBER_COUNT = 1000;

    private static final int RUNS = 4;
    private static final int BUFFER = 1000; // = 0 unbufferd, > 0 buffered ; < 0 unlimited

    private static void numberSort() {
        int[] nums = new int[NUMBER_COUNT];
        for (int i = 0; i < NUMBER_COUNT; i++)
            nums[i] = random.nextInt(NUMBER_COUNT);
        Arrays.sort(nums);
    }

    static void skynet(Channel<Long> c, long num, int size, int div) throws SuspendExecution, InterruptedException {
        if (size == 1) {
            c.send(num);
            return;
        }

        //加入排序逻辑
        numberSort();

        Channel<Long> rc = newChannel(BUFFER);
        long sum = 0L;
        for (int i = 0; i < div; i++) {
            long subNum = num + i * (size / div);
            new Fiber(() -> skynet(rc, subNum, size / div, div)).start();
        }
        for (int i = 0; i < div; i++)
            sum += rc.receive();
        c.send(sum);
    }

    public static void main(String[] args) throws Exception {
        //这里跑4次，是为了让JVM预热好做优化，所以我们以最后一个结果为准。
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();

            Channel<Long> c = newChannel(BUFFER);
            new Fiber(() -> skynet(c, 0, 1_000_000, 10)).start();
            long result = c.receive();

            long elapsed = (System.nanoTime() - start) / 1_000_000;
            System.out.println((i + 1) + ": " + result + " (" + elapsed + " ms)");
        }
    }

}
