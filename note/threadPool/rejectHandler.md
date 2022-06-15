# 任務太多隊列塞爆了：拒絕策略

<br>

--------------------------------

<br>

ThreadPoolExecutor 最後一個參數指定了拒絕策略，就是當任務數量超過系統實際乘載能力時，該如何應對。

拒絕策略執行時機是，__當 ThreadPool 中的 Thread 用完了，無法繼續為新任務服務，同時等待隊列的任務也已經排滿了，放不下新任務，而且當前 Thread 數量等於 `maximumPoolSize` 無法擴張。__ 這時就會發動拒絕策略。

<br>

JDK 內部提供 4 種拒絕策略類別，他們統一實作 __RejectExceptionHandler__ 介面：

<br>

* interface：__RejectExceptionHandler__

    * class：__AbortPolicy__

    * class：__CallerRunsPolicy__

    * class：__DiscardOledestPolicy__

    * class：__DiscardPolicy__

<br>
<br>

### __AbortPolicy__ 策略

<br>

該策略直接拋出異常，阻止系統工作。

<br>

### __CallerRunsPolicy__ 策略

<br>

只要 ThreadPool 未關閉，該策略直接在調用者 Thread 中（主 Thread），執行當前被丟棄的任務。這種策略不會真正丟掉任務，但是任務提交給的 Thread 性能極有可能會急遽下降。

<br>

### __DiscardOledestPolicy__ 策略

<br>

該策略丟棄最老的一個任務，也就是即將被要被執行的還未出列的那個任務，並嘗試再次提交當前任務。

<br>

### __DiscardPolicy__ 策略

<br>

該策略丟棄所有無法處裡的任務，不予任何處理。

<br>
<br>
<br>
<br>

如果預設 4 種策略無法滿足開發需求，也可以自己 implements __RejectExceptionHanlder__ 來擴充。

<br>

__RejectExceptionHanlder__ 介面


```java
public interface RejectedExecutionHandler {

    void rejectedExecution(Runnable r, ThreadPoolExecutor executor);

}
```

<br>

下面演示一個使用範例：

<br>

```java
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
                    5, // corePoolSize
                    5, // maximumPoolSize
                    0L, // aliveTime
                    TimeUnit.MILLISECONDS, // 時間單位
                    new LinkedBlockingDeque<Runnable>(10), // 無界隊列，指定 10 容量就變成有界隊列了
                    Executors.defaultThreadFactory(), // Thread 工廠
                    (runnable, executor) -> { // 拒絕策略
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
```

<br>

透過前一個章節，我們知道 __LinkedBlockingDeque__ 是無界任務隊列，但是我們給他指定 10 容量，這樣一來就變成有界隊列了。該 ThreadPool 只有 5 個常駐 Thread，最大量也是 5，所以基本上就是一個 fixedThreadPool。

我們自定義了拒絕策略，當有要拒絕的任務時，把他印出來記錄信息，這只比內建的 __DiscardPolicy__ 好一些，至少他丟棄任務前會告知我們。

<br>

印出部分結果：

<br>

```
1655260514: Thread ID: 15
1655260514: Thread ID: 16
1655260514: Thread ID: 12
1655260514: Thread ID: 13
java.util.concurrent.FutureTask@6d03e736 is discard.
1655260514: Thread ID: 14
1655260514: Thread ID: 15
1655260514: Thread ID: 16
java.util.concurrent.FutureTask@378bf509 is discard.
1655260514: Thread ID: 12
1655260514: Thread ID: 13
java.util.concurrent.FutureTask@5fd0d5ae is discard.
1655260514: Thread ID: 14
1655260514: Thread ID: 15
```