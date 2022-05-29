# Java Thread 的 6 個生命週期狀態 （NEW、RUNNABLE、BLOCKED、WAITING、TIME_WAITING、TERMINATED）

<br>

---------

<br>

描述一個 Thread 的完整生命週期如下：

<br>

* NEW （建立） 一個 Thread 出來，`start()` 啟動。

* 進入 RUNNABLE （運行）狀態。

* 遇到 `synchronized()` 關鍵字進入 BLOCKED （阻塞）狀態，直到取得 `synchronized()` 要同步化的資源再繼續回到 RUNNABLE 狀態。

* 遇到 `wait()` 方法進入 WAITING （無限等待） 狀態，直到收到 `notify()` 方法通知再繼續回到 RUNNABLE 狀態。

* 同樣遇到 `wait()` 方法，但是給定了一個等待時間則進入 TIMED_WAITING （限時等待），直到時間結束或者收到 `notify()` 方法通知再繼續回到 RUNNABLE 狀態。

* 任務結束後進入 TERMINATED （終結）狀態。

<br>

Thread 的 State enum 有列出這 6 個狀態：


```java
public enum State {
    NEW,
    RUNNABLE,
    BLOCKED,
    WAITING,
    TIME_WAITING,
    TERMINATED
}
```

<br>

__注意 : Thread 從 NEW 狀態出發後不能再回到 NEW 狀態，處於 TERMINATED 狀態的 Thread 也不能回到 RUNNABLE 狀態。__