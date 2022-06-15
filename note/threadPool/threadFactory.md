# 自定義 Thread 創建：__ThreadFactory__

<br>

---

<br>

ThreadPool 的 Thread 是由 __ThreadFactory__ 建立的。

__ThreadFactory__ 是一個介面，他只有一個方法：

<br>

```java
public interface ThreadFactory {

    Thread newThread(Runnable r);
}
```

<br>

當 ThreadPool 需要新建 Thread 時，就用這個方法。

<br>

自定義 ThreadPool 可以幫我們做到很多事，比如追蹤 ThreadPool 於何時建立多少 Thread，也可以自訂 Thread 名稱，ThreadGroup 以及優先級等，甚至可以設定所有 Thread 為 Daemon Thread。總之就是自由度更高。

下面有一個範例使用 ThreadFactory，一方面記錄 Thread 的建立，另一方面將所有 Thread 設定為 Daemon Thread。這樣一來，當主 Thread 退出後，將強制銷毀 ThreadPool。

<br>

```java
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
```

<br>

印出結果

<br>

```
Thread created Thread[Thread-0,5,main]
Thread created Thread[Thread-1,5,main]
Thread created Thread[Thread-2,5,main]
1655264559: Thread ID: 12
Thread created Thread[Thread-3,5,main]
1655264559: Thread ID: 13
Thread created Thread[Thread-4,5,main]
1655264559: Thread ID: 14
1655264559: Thread ID: 15
1655264559: Thread ID: 16
```