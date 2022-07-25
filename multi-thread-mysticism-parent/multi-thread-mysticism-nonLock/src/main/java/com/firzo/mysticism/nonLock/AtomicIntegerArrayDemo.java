package com.firzo.mysticism.nonLock;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

public class AtomicIntegerArrayDemo {

    static AtomicIntegerArray arr = new AtomicIntegerArray(10);

    public static class AddTask implements Runnable{

        @Override
        public void run() {
            IntStream.range(0, 10000).forEach(i -> {
                arr.getAndIncrement(i%arr.length());
            });
        }
    }

    public static void main(String[] args) {
        Thread[] ts = new Thread[10];
        IntStream.range(0, 10).forEach(i -> {
            ts[i] = new Thread(new AddTask());
        });
        IntStream.range(0, 10).forEach(i -> {
            ts[i].start();
        });
        IntStream.range(0, 10).forEach(i -> {
            try {
                ts[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(arr);
    }
}
