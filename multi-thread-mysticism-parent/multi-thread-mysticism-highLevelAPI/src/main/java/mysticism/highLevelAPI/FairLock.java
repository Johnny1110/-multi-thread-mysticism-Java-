package mysticism.highLevelAPI;

import java.util.concurrent.locks.ReentrantLock;

public class FairLock implements Runnable {

    public static ReentrantLock fairLock = new ReentrantLock(true);

    @Override
    public void run() {
        while(true) {
            try {
                fairLock.lock();
                System.out.println(Thread.currentThread().getName() + " 獲得鎖。");
            } finally {
                fairLock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        FairLock r1 = new FairLock();
        Thread t1 = new Thread(r1, "Thread-1");
        Thread t2 = new Thread(r1, "Thread-2");
        t1.start();
        t2.start();
    }
}
