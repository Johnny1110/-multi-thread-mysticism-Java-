package mysticism.highLevelAPI;

import java.util.concurrent.locks.ReentrantLock;

public class DeadLockDemo implements Runnable {

    public static ReentrantLock lock1 = new ReentrantLock();
    public static ReentrantLock lock2 = new ReentrantLock();
    int lock;

    public DeadLockDemo(int lock){
        this.lock = lock;
    }

    @Override
    public void run() {
        try {

            if (lock == 1){
                lock1.lockInterruptibly();
                try {
                    Thread.sleep(500); // 等待 0.5 秒再去嘗試取得 lock2 鎖。
                } catch (InterruptedException e){}
                lock2.lockInterruptibly();
            } else {
                lock2.lockInterruptibly();
                try {
                    Thread.sleep(500);
                }catch (InterruptedException e){}
                lock1.lockInterruptibly();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock1.isHeldByCurrentThread()){
                lock1.unlock();
            }
            if (lock2.isHeldByCurrentThread()){
                lock2.unlock();
            }
            System.out.println(Thread.currentThread().getName() + " 退出。");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DeadLockDemo task1 = new DeadLockDemo(1);
        DeadLockDemo task2 = new DeadLockDemo(2);
        Thread t1 = new Thread(task1,"Thread-1");
        Thread t2 = new Thread(task2,"Thread-2");
        t1.start();
        t2.start();
        Thread.sleep(2000);
        t2.interrupt();
    }
}
