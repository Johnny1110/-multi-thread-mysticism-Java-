package com.frizo.lab.thread.mysticism.basicOperation;

public class InterruptDemo {

    public static void main(String[] args) throws InterruptedException {

        Thread t = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()){
                    System.out.println("Current thread is interrupted");
                    break;
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted when sleep.");
                    Thread.currentThread().interrupt();
                }
            }
        });

        t.start();
        Thread.sleep(3000);
        t.interrupt(); // 中斷通知
    }

}
