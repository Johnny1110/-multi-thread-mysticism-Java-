package com.frizo.lab.thread.mysticism.basicOperation;

public class PriorityDemo {

    public static class CountTask implements Runnable {

        private static int count = 0;

        @Override
        public void run() {
            while (true){
                synchronized (PriorityDemo.class){
                    count++;
                    if(count > 1000000){
                        System.out.println("Name: " + Thread.currentThread().getName() +
                                " PriorityLevel: " + Thread.currentThread().getPriority() + " done job.");
                        break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread hightPriorityThread = new Thread(new CountTask(), "HIghtPriority");
        Thread lowPriorityThread = new Thread(new CountTask(),"lowPriority");
        hightPriorityThread.setPriority(Thread.MAX_PRIORITY);
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);
        lowPriorityThread.start();
        hightPriorityThread.start();
    }
}
