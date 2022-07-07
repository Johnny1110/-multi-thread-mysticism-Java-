package mysticism.advancedConception;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.*;

public class ThreadLocalPerformance {

    public static final int GEN_COUNT = 10000000; // 生成數量
    public static final int THREAD_COUNT = 4; // 執行緒數量
    static ExecutorService exe = Executors.newFixedThreadPool(THREAD_COUNT);

    public static Random rnd = new Random(123); // 第一種不使用 ThreadLocal 做法

    public static ThreadLocal<Random> local = new ThreadLocal<Random>() { // 第二種使用 ThreadLocal 做法
        protected Random initialValue() {
            return new Random(123);
        }
    };

    public static class RandTask implements Callable<Long> {
        // mode 為 0 代表多 Thread 共用一個 Random，為 1 代表各 Thread 都各分配一個 Random。
        private int mode = 0;

        public RandTask(int mode) {
            this.mode = mode;
        }

        public Random getRandom() {
            if (mode == 0) {
                return rnd;
            }
            if (mode == 1) {
                return local.get();
            }
            return null;
        }

        @Override
        public Long call() {
            long begin = System.currentTimeMillis();
            for (long i = 0; i < GEN_COUNT; ++i) {
                getRandom().nextInt();
            }
            long end = System.currentTimeMillis();
            long result = end - begin;
            System.out.println(Thread.currentThread().getName() + " spend " + result + " ms.");
            return result;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int mode = 1;
        Future<Long>[] futs = new Future[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i) {
            futs[i] = exe.submit(new RandTask(mode));
        }

        long totalTime = 0;
        for (int i = 0; i < futs.length; i++) {
            totalTime += futs[i].get();
        }
        System.out.println("使用 mode-" + mode+ " 共所耗時間：" + totalTime + "ms");
        exe.shutdown();
    }
}
