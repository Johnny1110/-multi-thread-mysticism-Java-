package mysticism.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ExtThreadPool {

    public static class MyTask implements Runnable {
        public String name;

        public MyTask(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println("正在執行 Thread ID: " + Thread.currentThread().getId()
                    + " Task Name: " + name);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        ExecutorService es = new ThreadPoolExecutor(
                5,
                5,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<Runnable>()
        ) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println("準備執行：" +((MyTask)r).name);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println("執行完成：" +((MyTask)r).name);
            }

            @Override
            protected void terminated() {
                System.out.println("ThreadPool 退出");
            }
        };

        IntStream.range(1, 6).forEach(i -> {
            MyTask task = new MyTask("Task-No." + i);
            es.execute(task);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        es.shutdown();
    }
}
