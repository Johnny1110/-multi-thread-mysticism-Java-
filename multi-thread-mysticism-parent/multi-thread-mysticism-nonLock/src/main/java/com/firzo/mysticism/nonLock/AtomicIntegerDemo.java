package com.firzo.mysticism.nonLock;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class AtomicIntegerDemo {

    static AtomicInteger integer = new AtomicInteger();

    public static class AddTask implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 1000; ++i) {
                integer.incrementAndGet();
            }
        }

        public static void main(String[] args) {
            ExecutorService es = Executors.newFixedThreadPool(10);
            IntStream.range(0, 10).forEach(i -> {
                es.submit(new AddTask());
            });
            es.shutdown();
            while (integer.get() != 10000);
            System.out.println(integer);
        }
    }
}
