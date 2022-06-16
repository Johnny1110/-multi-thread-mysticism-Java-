package mysticism.threadPool;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class CountTask extends RecursiveTask<Long> {

    private static final int THRESHOLD = 10000;
    private long start;
    private long end;

    public CountTask(long start, long end){
        this.start = start;
        this.end = end;
    }

    public Long compute() {
        long sum = 0;
        boolean canCompute = (end - start) < THRESHOLD;
        if (canCompute) {
            for (long i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            long step = (start + end) / 100; // 大任務拆成 100 份，一個 step 為一個子任務需要處裡的量。
            ArrayList<CountTask> subTasks = new ArrayList<CountTask>();
            long position = start;
            for (int i = 0; i < 100; i++) {
                long lastOne = Math.min(position + step, end);
                CountTask subTask = new CountTask(position, lastOne);
                position += step + 1;
                subTasks.add(subTask);
                subTask.fork();
            }
            sum += subTasks.stream().mapToLong(ForkJoinTask::join).sum();
        }
        return sum;
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask task = new CountTask(0, 600000L);
        ForkJoinTask<Long> result = forkJoinPool.submit(task);
        try{
            long res = result.get();
            System.out.println("sum = " + res);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
