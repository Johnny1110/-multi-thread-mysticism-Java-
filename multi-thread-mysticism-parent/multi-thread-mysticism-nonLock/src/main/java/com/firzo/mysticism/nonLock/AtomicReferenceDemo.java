package com.firzo.mysticism.nonLock;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceDemo {

    private static AtomicReference<Integer> money = new AtomicReference<>();

    static {
        money.set(19);
    }

    public static class AddMoneyTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                Integer gash = money.get();
                if (gash < 20) {
                    if (money.compareAndSet(gash, gash + 20)) {
                        System.out.println("餘額：" + gash + " 贈送 20 元，加值後餘額：" + money.get());
                    }
                }
            }

        }
    }

    public static class CostMoneyTask implements Runnable {

        @Override
        public void run() {
            for (int i = 1; i < 100; ++i){
                while (true){
                    Integer m = money.get();
                    if (m > 10) {
                        System.out.println("大於 10 元");
                        if (money.compareAndSet(m, m-10)){
                            System.out.println("成功消費 10 元，餘額：" + money.get());
                            break;
                        }
                    }else{
                        System.out.println("餘額不足");
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    public static void main(String[] args) {
        Thread[] threads = new Thread[3];
        Runnable addTask = new AddMoneyTask();
        Runnable costTask = new CostMoneyTask();
        for (int i = 0; i < threads.length; i++) {
            new Thread(addTask).start();
        }
        new Thread(costTask).start();
    }
}
