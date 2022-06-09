package mysticism.highLevelAPI;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {

    public static class Soldier implements Runnable{
        private String soldierName;
        private final CyclicBarrier barrier;

        public Soldier(CyclicBarrier barrier, String soldierName){
            this.barrier = barrier;
            this.soldierName = soldierName;
        }

        @Override
        public void run() {
            try {
                // wait for assemble.
                barrier.await();
                doWork();
                // wait for soldier finish work.
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        private void doWork() {
            try {
                Thread.sleep(Math.abs(new Random().nextInt() % 10000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(this.soldierName + " : mission complete.");
        }
    }

    public static class BarrierRun implements Runnable {
        boolean flag;
        int N;

        public BarrierRun(boolean flag, int N){
            this.flag = flag;
            this.N = N;
        }

        @Override
        public void run() {
            if (flag) {
                System.out.println("Commander: [Soldier " + N + ", mission complete.]");
            } else {
                System.out.println("Commander: [Soldier " + N + ", assemble complete.]");
                flag = true;
            }
        }
    }

    public static void main(String[] args) {
        final int N = 10;
        Thread[] allSoldier = new Thread[N];
        boolean flag = false;
        CyclicBarrier barrier = new CyclicBarrier(N, new BarrierRun(flag, N));
        System.out.println("Commander: [assemble army!]");
        for (int i = 0; i < N; ++i){
            System.out.println("Soldier " + i + ": yeah sir.");
            allSoldier[i] = new Thread(new Soldier(barrier, "Soldier " + i));
            allSoldier[i].start();
        }
    }
}
