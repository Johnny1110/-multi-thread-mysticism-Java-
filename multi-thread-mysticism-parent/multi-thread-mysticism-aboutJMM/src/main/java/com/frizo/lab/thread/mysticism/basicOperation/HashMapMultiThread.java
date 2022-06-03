package com.frizo.lab.thread.mysticism.basicOperation;

import java.util.HashMap;

public class HashMapMultiThread {

    private static HashMap<String, String> map = new HashMap<String, String>();

    public static class AddThread implements Runnable{

        private int start = 0;

        public AddThread(int start){
            this.start = start;
        }

        @Override
        public void run() {
            for (int i = start; i < 100000; i+=2) {
                map.put(String.valueOf(i), Integer.toBinaryString(i));
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new AddThread(0));  // 新增雙數
        Thread t2 = new Thread(new AddThread(1));  // 新增單數
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(map.size());
    }
}
