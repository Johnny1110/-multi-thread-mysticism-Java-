# 固定大小 ThreadPool 與計畫任務

<br>

-------

<br>

這裡示範如何使用 FixedThreadPool 與 ScheduledThreadPool。

<br>
<br>

## 固定大小 ThreadPool：FixedThreadPool

<br>

```java
public class ThreadPoolDemo {

    public static class MyTask implements Runnable {

        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + " Thread ID: " + Thread.currentThread().getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        MyTask task = new MyTask();
        ExecutorService es = Executors.newFixedThreadPool(5); // #1
        IntStream.range(0, 10).forEach(i -> { // #2
            es.submit(task);
        });
        es.shutdown();
    }
}
```

<br>

#1 建立了固定大小的 ThreadPool，內有 5 個 Thread。

#2 依次向 ThreadPool 提交了 10 個任務。

<br>

```
1655091968666 Thread ID: 12
1655091968667 Thread ID: 15
1655091968666 Thread ID: 14
1655091968666 Thread ID: 13
1655091968667 Thread ID: 16
1655091969680 Thread ID: 12
1655091969680 Thread ID: 13
1655091969680 Thread ID: 16
1655091969680 Thread ID: 15
1655091969680 Thread ID: 14
```

<br>

前五個任務和後五個任務執行時間差 1 秒，且前五個與後五個執行任務的 Thread ID 也一樣（都是 12 13 14 15 16）。

<br>
<br>
<br>
<br>

## 計畫任務：ScheduledThreadPool

<br>

使用 `newScheduledThreadPool()` 可以返回一個 ScheduledExecutorService 物件。可以根據時間需要對 Thread 進行調度。

主要方法如下：

<br>

```java
public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)

public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)

public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);
```

<br>

ScheduledExecutorService 並不會立即安排執行任務。他是起到計畫任務的作用。他在指定時間，對任務進行調度（任務排程）。

<br>

* `schedule()` 會再給定時間，對任務進行 __一次__ 調度。

<br>

* `scheduleAtFixedRate()` 會對任務進行週期性調度。他會以上一個任務開始執行後計時，到時間就進行下一項任務，如果任務執行時間超過等待時間，那就會等到上一個任務執行完才會執行下一個。

<br>

* `scheduleWithFixedDelay()` 也會對任務進行週期性調度。但是他會在上一個任務結束後，在經過 delay 時間後進行下一次任務調度。

<br>
<br>

看看一個使用 `scheduledAtFixedRate()` 方法的範例，這個任務會執行 1 秒時間，調度週期是 2 秒，也就是說每 2 秒就會被執行一次。

<br>

```java
public class ScheduledExecutorServiceDemo {

    public static void main(String[] args) {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
        ses.scheduleAtFixedRate(() ->{
            try {
                Thread.sleep(1000);
                System.out.println(System.currentTimeMillis() / 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
}
```

<br>

部屬任務，等待 1 秒，初始延遲 0 秒，週期為 2 秒。

基本上每 2 秒印出 1 行，但是如果任務執行時間超過等待時間，那實際上的等待時間就變成任務執行時間。

打比方說，一個閘道每 2 秒放行一輛車，但是要放下一輛車的前提是上一輛車必須行駛完成。所以如果一輛車行駛花費 8 秒，那下一輛車就不用在前一輛車行駛完後再等 2 秒，而是直接開始。

我們可以把上面範例的 `sleep()` 1 秒改 8 秒試試看。這樣一來任務執行週期會變成 8 秒，如果採用 `scheduleWithFixedDelay()` delay 設定 2 秒，則任務執行間隔會變成 10 秒。

<br>
<br>


注意 !：__如果任務遇到異常，那後續的所有子任務都會停止調度。因此必須保證異常捕捉要及時處理，為周期性任務的穩定性提供條件。__
