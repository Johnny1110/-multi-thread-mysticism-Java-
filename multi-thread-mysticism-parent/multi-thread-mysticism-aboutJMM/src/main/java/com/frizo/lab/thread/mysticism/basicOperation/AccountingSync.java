package com.frizo.lab.thread.mysticism.basicOperation;

public class AccountingSync implements Runnable {

    private static AccountingSync instance = new AccountingSync();
    private static int i = 0;

    public static void increase() {
        i++;
    }

    @Override
    public void run() {
        for (int j = 0; j<1000000; j++) {
            synchronized (instance){
                increase();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(instance);
        Thread t2 = new Thread(instance);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("i = " + i);
    }
}
