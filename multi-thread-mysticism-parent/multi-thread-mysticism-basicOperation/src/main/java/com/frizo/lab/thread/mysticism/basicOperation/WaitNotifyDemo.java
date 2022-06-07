package com.frizo.lab.thread.mysticism.basicOperation;

public class WaitNotifyDemo {

    final static Object object = new Object();

    public static class Thread1 extends Thread {
        @Override
        public void run(){
            synchronized (object){
                System.out.println(System.currentTimeMillis() + ": Thread-1 start.");
                try {
                    System.out.println(System.currentTimeMillis() + ": Thread-1 wait for object.");
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis()+ ": Thread-1 end.");
            }
        }
    }

    public static class Thread2 extends Thread {
        @Override
        public void run(){
            synchronized (object){
                System.out.println(System.currentTimeMillis() + ": Thread-2 start.");
                System.out.println(System.currentTimeMillis() + ": Thread-2 notify a random Thread.");
                object.notify();
                System.out.println(System.currentTimeMillis()+ ": Thread-2 waiting 2 sec to release object.");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis() + ": Thread-2 end.");
            }
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread1();
        Thread t2 = new Thread2();
        t1.start();
        t2.start();
    }

}
