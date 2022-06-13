package mysticism.threadPool;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class ThreadPoolDemo {

    public static class MyTask implements Runnable {

        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + " Thread ID: " + Thread.currentThread().getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        MyTask task = new MyTask();
        ExecutorService es = Executors.newFixedThreadPool(5);
        IntStream.range(0, 10).forEach(i -> {
            es.submit(task);
        });
        es.shutdown();
    }
}
