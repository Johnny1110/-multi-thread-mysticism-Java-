package com.frizo.lab.thread.mysticism.basicOperation;

public class SuspendResumeGudDemo {

    public static final Object LOCK = new Object();

    public static class SuspendThread extends Thread {
        private volatile boolean suspendme = false;

        public void suspendMe() {
            this.suspendme = true;
        }

        public void resumeMe() {
            this.suspendme = false;
            synchronized (this) {
                notify();
            }
        }

        @Override
        public void run() {
            while (true) {
                synchronized (this) {
                    while (suspendme) {
                        try {
                            wait();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                synchronized (LOCK) {
                    System.out.println("in SuspendThread.");
                }
                Thread.yield();
            }
        }
    }

    public static class AnotherThread extends Thread {
        @Override
        public void run() {
            while (true) {
                synchronized (LOCK) {
                    System.out.println("in AnotherThread.");
                }
                Thread.yield();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SuspendThread t1 = new SuspendThread();
        AnotherThread t2 = new AnotherThread();
        t1.start();
        t2.start();
        Thread.sleep(1000);
        t1.suspendMe();
        System.out.println("suspend t1 2 sec.");
        Thread.sleep(2000);
        System.out.println("resume t1.");
        t1.resumeMe();
    }

}
