package com.frizo.lab.thread.mysticism.basicOperation;

public class ThreadGroupName implements Runnable {
    @Override
    public void run() {
        String groupAndName = Thread.currentThread().getThreadGroup().getName() +
                "-" + Thread.currentThread().getName();
        while (true) {
            System.out.println("I am " + groupAndName);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ThreadGroup threadGroup = new ThreadGroup("PrinterGroup");
        Thread t1 = new Thread(threadGroup, new ThreadGroupName(), "T-1");
        Thread t2 = new Thread(threadGroup, new ThreadGroupName(), "T-2");
        t1.start();
        t2.start();
        System.out.println("Active thread count : " + threadGroup.activeCount());
        threadGroup.list();
    }
}
