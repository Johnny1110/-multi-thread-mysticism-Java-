package com.frizo.lab.thread.mysticism.basicOperation;

public class YieldDemo {

    private final static Object LOCK = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true){
                synchronized (LOCK){
                    System.out.println("t1 is running");
                }
                Thread.yield();
            }
        });

        Thread t2 = new Thread(() -> {
            while (true){
                synchronized (LOCK){
                    System.out.println("t2 is running");
                }
                Thread.yield();
            }
        });

        t1.start();
        t2.start();
    }

}
