package com.frizo.lab.thread.mysticism.basicOperation;

public class Novisibility {

    //private  static boolean ready;
    private volatile static boolean ready;
    private static int number;

    private static class Reader extends Thread {
        @Override
        public void run() {
            while(!ready);
            System.out.println(number);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Reader().start();
        Thread.sleep(1000);
        number = 42;
        ready = true;
        Thread.sleep(10000);
    }

}
