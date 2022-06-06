package com.frizo.lab.thread.mysticism.highLevelAPI;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TimeLock implements Runnable {

    public static ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        try {
            if (lock.tryLock(3, TimeUnit.SECONDS)){
                Thread.sleep(4000);
                System.out.println(Thread.currentThread().getName() + " done task.");
            } else {
                System.out.println(Thread.currentThread().getName() + " get lock failed.");
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        TimeLock task = new TimeLock();
        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");
        t1.start();
        t2.start();
    }
}
