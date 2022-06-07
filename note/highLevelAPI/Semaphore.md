# `Semaphore` 信號量，允許多個 Thread 同時訪問

<br>

---

<br>

無論是內部鎖（`synchronized`）或重入鎖（ReentranLock），一次只允許一個 Thread 訪問一個資源，而信號量（Semaphore）可以指定多 Thread 同時訪問一個資源，Semaphore 主要提供以下建構函式：

<br>

```java
public Semaphore(int permits) // permits 限定一次可以多少同時進入

public Semaphore(int permits, boolean fair) // fair 指定是否公平
```

<br>

Semaphore 主要邏輯方法如下：

<br>

```java
public void acquire()

public void acquireUninterruptibly()

public boolean tryAcquire()

public boolean tryAcquire(long timeout, TimeUnit unit)

public void release()
```

<br>

* `acquire()` 方法嘗試獲取一個進入許可，若無法獲得則會等待，直到有 Thread 釋放一個許可或當前 Thread 被中斷。

* `acquireUninterruptibly()` 方法與 `acquire()` 一樣，但部會被中斷。

* `tryAcquire()` 嘗試或許一個許可，成功返回 true，失敗返回 `false`。它不會等待，而是立即返回。

* `tryAcquire(long timeout, TimeUnit unit)` 跟 `tryAcquire()` 一樣，只是可以等待一段時間。

* `release()` 方法用於當 Thread 訪問資源結束後，釋放一個許可，使其他等待許可的 Thread 可以進行訪問。

<br>
<br>

使用範例：

<br>

```java
public class SemaphoreDemo implements Runnable{

    private final Semaphore semap = new Semaphore(5);

    @Override
    public void run() {
        try{
            semap.acquire();
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName() + " done!");
            semap.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExecutorService exec = Executors.newFixedThreadPool(20);
        SemaphoreDemo demo = new SemaphoreDemo();
        for (int i = 0; i < 20; ++i){
            exec.submit(demo);
        }
        exec.shutdown();
    }
}
```

<br>

執行過程中，你會發現基本上都是以 5 行為一組輸出，因為我們在建立 `Semaphore` 物件時使用 5 作為參數。所以一次只放行最多 5 個 Thread 進到 `acquire()` ~ `release()` 區間。記得有 `acquire()` 就一定要 `release()`，不然會一直被占用。

<br>

輸出：

```
pool-1-thread-4 done!
pool-1-thread-3 done!
pool-1-thread-2 done!
pool-1-thread-1 done!
pool-1-thread-5 done!
pool-1-thread-8 done!
pool-1-thread-7 done!
pool-1-thread-6 done!
pool-1-thread-10 done!
pool-1-thread-9 done!
pool-1-thread-12 done!
pool-1-thread-11 done!
pool-1-thread-15 done!
pool-1-thread-14 done!
pool-1-thread-13 done!
pool-1-thread-19 done!
pool-1-thread-20 done!
pool-1-thread-18 done!
pool-1-thread-17 done!
pool-1-thread-16 done!
```

<br>
