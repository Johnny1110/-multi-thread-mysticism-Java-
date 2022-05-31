package com.frizo.lab.thread.mysticism.basicOperation;

public class SuspendResumeBadDemo {

    public static final Object LOCK = new Object();


    public static class SuspendThread extends Thread {
        public SuspendThread(String name) {
            super.setName(name);
        }

        @Override
        public void run() {
            synchronized (LOCK) {
                System.out.println("in " + getName());
                Thread.currentThread().suspend();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SuspendThread t1 = new SuspendThread("t1");
        SuspendThread t2 = new SuspendThread("t2");

        t1.start();
        Thread.sleep(200); // 保證讓 t1 先掛起
        t2.start();
        t1.resume();
        t2.resume();
        t1.join();
        t2.join();
    }

}
