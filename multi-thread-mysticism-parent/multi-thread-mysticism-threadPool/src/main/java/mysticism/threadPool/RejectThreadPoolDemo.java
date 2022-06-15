package mysticism.threadPool;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class RejectThreadPoolDemo {

    public static class MyTask implements Runnable {

        @Override
        public void run() {
            System.out.println(System.currentTimeMillis()/1000 + ": Thread ID: " + Thread.currentThread().getId());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public static void main(String[] args) {
            MyTask task = new MyTask();
            ExecutorService es = new ThreadPoolExecutor(
                    5,
                    5,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<Runnable>(10), // 無界隊列，指定 10 容量就變成有界隊列了
                    Executors.defaultThreadFactory(),
                    (runnable, executor) -> {
                        System.out.println(runnable.toString() + " is discard.");
                    }
            );
            IntStream.range(0, Integer.MAX_VALUE).forEach(i -> {
                es.submit(task);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
