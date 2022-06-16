# 在 ThreadPool 中尋找被藏起來的 __Exception__

<br>

-------

<br>

使用 ThreadPool 或許是一件好事，但是他還是有坑的，ThreadPool 有可能會吃掉本來該被拋出的 __Exception__，導致我們無法 debug。

來看一下被吃掉 __Exception__ 的範例：

<br>

```java
public class DivTask implements Runnable {

    private int a, b;

    public DivTask(int a, int b){
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
        double re = a/b;
        System.out.println(re);
    }

    public static void main(String[] args) {
        ExecutorService es = new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                0L,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>()
                );
        for (int i= 0; i < 5; ++i){
            es.submit(new DivTask(100, i));
        }
    }
}
```

<br>

這個程式計算 100 除以 0 ~ 4 的結果，我們預計會得到 5 個結果，但事實上只有 4 個：

<br>

```
100.0
25.0
33.0
50.0
```

<br>

發生了甚麼問題呢？是因為 100 / 0 導致的，如果在一般的程式中算一下 100 / 0 會報出以下錯誤：

<br>

```
java.lang.ArithmeticException / by zero
```

<br>

如果有這個鎖誤訊息我們 debug 會快很多，但是在這個範例中，錯誤訊息被 "消失" 了。這就是 ThreadPool 的坑。要想解決這一問題，我們要自己動手繼承 __ThreadPoolExecutor__ 來擴展功能。

<br>

```java
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
            es.execute(new DivTask(100, i)); // #1
        }
    }
}
```

<br>

注意 #1 處改用 `execute()` 方法，因為這樣才能印出具體的錯誤訊息，使用 `submit()` 只能得到部份錯誤訊息。

<br>

印出結果：

<br>

```
java.lang.Exception: client stack trace.
	at mysticism.threadPool.TraceThreadPoolExecutor.clientTrace(TraceThreadPoolExecutor.java:27)
	at mysticism.threadPool.TraceThreadPoolExecutor.execute(TraceThreadPoolExecutor.java:18)
	at mysticism.threadPool.TraceThreadPoolExecutor.main(TraceThreadPoolExecutor.java:51)
Exception in thread "pool-1-thread-1" java.lang.ArithmeticException: / by zero
	at mysticism.threadPool.DivTask.run(DivTask.java:19)
	at mysticism.threadPool.TraceThreadPoolExecutor.lambda$wrap$0(TraceThreadPoolExecutor.java:33)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
100.0
25.0
33.0
50.0
```