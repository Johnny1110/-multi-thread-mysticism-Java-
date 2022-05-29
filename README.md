# Multi-thread mysticism (by Java)

<br>

---

<br>
<br>

多執行緒奧義，深入研究 Java 多執行緒相關問題的紀錄歸總。

<br>

## 目錄

<br>

### 一、關於開發多執行緒必須知道的幾件事 (基礎概念)

 <br>

* [甚麼時候用並行 (Parallel) ?](note/basic/whenToUseParallel.md)

* [同步 (Synchronous)、非同步 (Asynchronous)](note/basic/synchronousAndAsynchronous.md)

* [並行 (Concurrency)、平行 (Parallelism)](note/basic/concurrencyAndParallelism.md)

* [阻塞 (Blocking)、非阻塞 (Non-Blocking)](note/basic/blockingAndNonBlocking.md)

* [死鎖 (Deadlock)、飢餓 (Starvation)、活鎖 (Livelock)](note/basic/deadlockStarvationLivelock.md)

* [mulit-thread 級別 ( 阻塞、無飢餓、無障礙、無鎖、無等待 )](note/basic/multiThreadGrading.md)

* [Java内存模型（Java Memory Model，__JMM__）
  -> 原子性( Atomicity )、可見性( Visibility )、有序性( Ordering )](note/basic/JMM.md)

* [Java Thread 的 6 個生命週期狀態 （NEW、RUNNABLE、BLOCKED、WAITING、TIME_WAITING、TERMINATED）](note/basic/threadsLifeCycle.md)

  <br>
  
### 二、Java 多執行緒基礎操作中可能被你忽略的點

  <br>

  * [多實作 Runnable 少繼承 Thread](note/likelyToBeIgnore/runnableVsThread.md)

  * [如何合理的終止 Thread 運行（不要用 `stop()`方法）](note/likelyToBeIgnore/howToWStopThread.md)

  * [Thread 中斷]()