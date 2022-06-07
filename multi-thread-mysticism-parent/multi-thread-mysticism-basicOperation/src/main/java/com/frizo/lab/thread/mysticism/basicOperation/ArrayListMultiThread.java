package com.frizo.lab.thread.mysticism.basicOperation;

import java.util.ArrayList;

public class ArrayListMultiThread {

    private static ArrayList<Integer> list = new ArrayList<>(10);

    private static class AddTask implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i <1000000; ++i){
                list.add(i);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new AddTask());
        Thread t2 = new Thread(new AddTask());
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(list.size());
    }
}
