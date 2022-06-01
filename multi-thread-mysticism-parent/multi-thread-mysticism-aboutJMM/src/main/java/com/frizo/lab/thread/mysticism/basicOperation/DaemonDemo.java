package com.frizo.lab.thread.mysticism.basicOperation;

public class DaemonDemo {

    public static class DaemonT extends Thread {

        public DaemonT(boolean isDaemon){
            super.setDaemon(isDaemon);
        }

        @Override
        public void run() {
            while (true){
                System.out.println("I'm alive.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new DaemonT(false);
        t.start();
        Thread.sleep(2000);
    }

}
