package com.frizo.lab.thread.mysticism.basicOperation;

import java.util.EnumSet;

public class BadLockOnInteger implements Runnable {

    private static Integer i = 0;

    @Override
    public void run() {
        for (int j = 0; j < 100000; ++j){
            synchronized (i){
                i++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new BadLockOnInteger());
        Thread t2 = new Thread(new BadLockOnInteger());
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(i);
    }
}
