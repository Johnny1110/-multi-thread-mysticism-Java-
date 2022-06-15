package mysticism.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ThreadFactoryDemo {

    public static class MyTask implements Runnable {

        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() / 1000 + ": Thread ID: " + Thread.currentThread().getId());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyTask task = new MyTask();
        ExecutorService es = new ThreadPoolExecutor(
                5,
                5,
                0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                (runnable) ->{
                    Thread t = new Thread(runnable);
                    t.setDaemon(true);
                    System.out.println("Thread created " + t);
                    return t;
                },
                new ThreadPoolExecutor.DiscardPolicy()
        );

        IntStream.range(0, 5).forEach(i -> {
            es.submit(task);
        });
        Thread.sleep(2000);
    }
}
