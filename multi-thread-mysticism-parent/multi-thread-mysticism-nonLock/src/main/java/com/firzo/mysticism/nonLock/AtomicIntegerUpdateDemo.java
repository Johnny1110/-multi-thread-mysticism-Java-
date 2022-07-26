package com.firzo.mysticism.nonLock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AtomicIntegerUpdateDemo {

    public static class Candidate{
        int id;
        volatile int score;
    }

    public final static AtomicIntegerFieldUpdater<Candidate> scoreUpdater =
            AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");

    public static AtomicInteger allScore = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final Candidate stu = new Candidate();
        Thread[] threads = new Thread[10000];
        for(int i = 0; i < 10000; ++i){
            threads[i] = new Thread(() -> {
                if(Math.random() > 0.4) {
                    scoreUpdater.incrementAndGet(stu);
                    allScore.incrementAndGet(); // 同時與 Candidate 累加，後面驗證正確性用。
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < 10000; i++) {
            threads[i].join();
        }
        System.out.println("score: " + stu.score);
        System.out.println("Allscore: " + allScore);
    }
}
