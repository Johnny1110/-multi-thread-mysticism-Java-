package mysticism.highLevelAPI;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReenterLockCondition implements Runnable {

    public static ReentrantLock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    @Override
    public void run() {
        try {
            lock.lock();
            condition.await();  // #1
            System.out.println("Thread is going on.");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReenterLockCondition task = new ReenterLockCondition();
        Thread t1 = new Thread(task);
        t1.start();
        Thread.sleep(2000);
        lock.lock();  // #2
        condition.signal();  // #3
        lock.unlock();  // #4
    }
}
