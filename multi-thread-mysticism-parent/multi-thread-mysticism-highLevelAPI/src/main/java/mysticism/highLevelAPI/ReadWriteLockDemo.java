package mysticism.highLevelAPI;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockDemo {

    private static Lock lock = new ReentrantLock();
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private static Lock readLock = readWriteLock.readLock();
    private static Lock writeLock = readWriteLock.writeLock();

    private int value;

    public Object handleRead(Lock lock){
        try{
            lock.lock();
            Thread.sleep(1000);
            return value;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }finally {
            lock.unlock();
        }
    }

    public void handleWrite(Lock lock, int index) {
        try {
            lock.lock();
            Thread.sleep(1000);
            this.value = index;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        final ReadWriteLockDemo demo = new ReadWriteLockDemo();
        Runnable readTask = () -> {
            demo.handleRead(readLock);
            //demo.handleRead(lock);
        };

        Runnable writeTask = () -> {
            demo.handleWrite(writeLock, new Random().nextInt());
            //demo.handleWrite(lock, new Random().nextInt());
        };

        for (int i = 0; i < 18; ++i){
            new Thread(readTask).start();
        }

        for (int i = 18; i < 20; ++i){
            new Thread(writeTask).start();
        }
    }
}
