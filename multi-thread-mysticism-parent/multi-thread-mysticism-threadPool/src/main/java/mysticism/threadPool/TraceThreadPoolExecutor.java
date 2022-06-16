package mysticism.threadPool;

import java.util.concurrent.*;

public class TraceThreadPoolExecutor extends ThreadPoolExecutor {

    public TraceThreadPoolExecutor(int corePoolSize,
                                   int maximumPoolSize,
                                   long keepAliveTime,
                                   TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue
                                   ) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public void execute(Runnable task) {
        super.execute(wrap(task, clientTrace(), Thread.currentThread().getName()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(wrap(task, clientTrace(), Thread.currentThread().getName()));
    }

    private Exception clientTrace() {
        return new Exception("client stack trace.");
    }

    private Runnable wrap(final Runnable task, final Exception clientStack, String threadName){
        return () -> {
          try{
              task.run();
          } catch (Exception e){
              clientStack.printStackTrace();
              throw  e;
          }
        };
    }


    public static void main(String[] args) {
        ExecutorService es = new TraceThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                0L,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>()
        );
        for (int i= 0; i < 5; ++i){
            es.execute(new DivTask(100, i));
        }
    }
}
