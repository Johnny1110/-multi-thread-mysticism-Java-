# Executor 框架

<br>

-------

<br>

Thread Pool 是甚麼就不多解釋了，直接入正題介紹 JDK 對 Thread Pool 的支援，也就是 Executor 框架。 

Executor 幫助開發人員有效地進行 Thread 控制，本質就是一個 Thread Pool。

下面介紹幾個 Executor 系列框架比較重要的類別或介面。

<br>

1. interface : Executor

    Executor 介面是最核心的一個介面，他只有定義一個方法 `void execute(Runnable command)`。

<br>

2. interface : ExecutorService

    ExecutorService 繼承自 Executor，在 Executor 基礎上多加了幾個方法，比較重要的這邊列出來看看：

    `void shutdown()`

    `boolean isShutdown()`

    `boolean isTerminated()`

    `Future<?> submit(Runnable task)`

<br>

3. class : ThreadPoolExecutor

    ThreadPoolExecutor 實現了 ExecutorService，他就是真正意義上可以使用的 Thread Pool。他可以透過建構式自己建立，也可以用工廠類建立。

<br>

4. class : Executors

    Executors 類別是一個工廠類，通過他可以取得一個擁有特定功能的 Thread Pool。當然，這些 Thread Pool 都是 ThreadPoolExecutor，只是差在建構時參數不一樣，所以提供的功能也不太一樣。

<br>
<br>


## Executors 工廠

<br>

Executors 工廠類有必要拿出來單獨講講，他有 5 個比較重要的方法，可以提供 5 種 Thread Pool。

<br>

```java
public static ExecutorService newFixedThreadPool(int nThreads)

public static ExecutorService newSingleThreadExecutor()

public static ExecutorService newCachedThreadPool()

public static ScheduledExecutorService newSingleThreadScheduledExecutor()

public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize)
```

<br>

* `newFixedThreadPool(int nThreads)` 方法：

    該方法反回一個固定 Thread 數量的 ThreadPool，該 Pool 中的 Thread 數量始終保持不變，當有一個新任務提交時，ThreadPool 中若有空閒的 Thread 則立即執行，若沒有則新任務會被暫時存放在一個任務列中，待 Thread 空閒時就處理任務列的任務。

<br>

* `newSingleThreadExecutor()` 方法：

    該方法返回一個只有一個 Thread 的 ThreadPool，如果多於一個任務被提交給該 Pool，任務就會存到任務列中，待 Thread 空閒時，以先入先出順序執行。

<br>

* `newCachedThreadPool()` 方法：

    該方法返回一個可以根據實際情況調整 Thread 數量的 ThreadPool，Pool 裡的 Thread 數量不確定，如果有空閒 Thread 能用就優先，如果所有 Thread 都在工作時，有新任務提交，則建立新的 Thread 處理任務，任務完成後就編列到 Pool 中待命。

<br>

* `newSingleThreadScheduledExecutor()` 方法：

    該方法返回一個 ScheduledExecutorService 物件，ThreadPool 大小為 1。ScheduledExecutorService 繼承自 ExecutorService，他擴展了在給定時間執行某任務的功能，像是在某個固定延時候執行，或週期性執行某任務。

<br>

* `newScheduledThreadPool(int corePoolSize)` 方法：

    該方法也返回一個 ScheduledExecutorService 物件，但是他可以指定 Thread 數量。

<br>
<br>






