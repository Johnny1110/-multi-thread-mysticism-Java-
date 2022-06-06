# `synchronized()`、`wait()`、`notify()` 的替代品： `ReentrantLock`

<br>

---

<br>

## ReentrantLock 基本使用

<br>

JDK 1.5 開始 __ReentrantLock__ （重入鎖）就可以完全替代　`synchronized()` 了，且性能遠遠好於後者。JDK 1.6 開始， `synchronized()` 做了大量優化，使二者行能差距不大。

簡單的一個使用範例：

<br>

```java
public class ReenterLock implements Runnable {

    public static ReentrantLock lock = new ReentrantLock();
    public static int i = 0;

    @Override
    public void run() {
        for (int j = 0; j < 100000; j++){
            lock.lock();  // #1
            try {
                i++;
            }finally {
                lock.unlock();  // #2
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReenterLock lockTask = new ReenterLock();
        Thread t1 = new Thread(lockTask);
        Thread t2 = new Thread(lockTask);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(i);
    }
}
```

<br>

可以看到 #1 #2 處的操作，重入鎖有著明顯的操作上鎖解鎖的過程。我們必須手動控制上鎖解鎖，所以有好有壞，好處是我們可以靈活控制鎖邏輯，壞處是要時刻記得必須釋放鎖，不然其他 Thread 就沒機會訪問資源了。

至於為何 ReentrantLock 是 Reentrant（重入）呢？因為 ReentrantLock 可以重複上鎖：

<br>

```java
lock.lock();
lock.lock();
try{
    i++;
}finally{
    lock.unlock();
    lock.unlock();
}
```

<br>

同一個 Thread 可以連續兩次獲得同一把鎖，要注意的是，如果同一個 Thread 多次上鎖，納在釋放鎖時也必須釋放相同次數，否則無法解鎖。

<br>
<br>

## ReentrantLock 高級功能

<br>

ReentrantLock 還提供一些高級功能，像是：

<br>

* 中斷等待

* 鎖申請等待時間

* 公平鎖

<br>

下面一一做介紹。

<br>
<br>

### 中斷等待

<br>

對於 `synchronized()` 來說，如果一個 Thread 在等待鎖，那結果只有 2 種可能，要馬它得到鎖繼續執行，要馬它繼續等待。使用 `ReentrantLock` 的話則會出現另一種可能，就是 __被中斷__。也就是說在等待狀態下，我們可以根據需求取消對鎖的請求。使用情形就是當一個 Thread 正在等待鎖，那麼它依然可以收到一個通知，被告知無須繼續等待，可以停止作業了，這種情況對於處理 dead lock 有一定的幫助。

<br>

下面示範一個 dead lock，然後用等待中斷來解決這個問題：

<br>

```java
public class DeadLockDemo implements Runnable {

    public static ReentrantLock lock1 = new ReentrantLock();
    public static ReentrantLock lock2 = new ReentrantLock();
    int lock;

    public DeadLockDemo(int lock){
        this.lock = lock;
    }

    @Override
    public void run() {
        try {

            if (lock == 1){
                lock1.lockInterruptibly();
                try {
                    // 等待 0.5 秒再去嘗試取得 lock2 鎖。
                    Thread.sleep(500); 
                } catch (InterruptedException e){}
                lock2.lockInterruptibly();
            } else {
                lock2.lockInterruptibly();
                try {
                    // 等待 0.5 秒再去嘗試取得 lock1 鎖。
                    Thread.sleep(500);
                }catch (InterruptedException e){}
                lock1.lockInterruptibly();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock1.isHeldByCurrentThread()){
                lock1.unlock();
            }
            if (lock2.isHeldByCurrentThread()){
                lock2.unlock();
            }
            System.out.println(Thread.currentThread().getName() + " 退出。");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DeadLockDemo task1 = new DeadLockDemo(1);
        DeadLockDemo task2 = new DeadLockDemo(2);
        Thread t1 = new Thread(task1,"Thread-1");
        Thread t2 = new Thread(task2,"Thread-2");
        t1.start();
        t2.start();
        Thread.sleep(2000);
        t2.interrupt();
    }
}
```

<br>

印出結果：

<br>

```
java.lang.InterruptedException
	at ...
Thread-2 退出。
Thread-1 退出。
```

<br>

`lockInterruptibly()` 方法代表這是一個可以被中斷的上鎖，只要觸發 `interrupt()` 就直接退出資源競爭，執行 `finally{ }` 中的解鎖邏輯。

<br>
<br>

### 鎖申請等待時間

<br>

除了等待中斷通知外，要避免 dead lock 還有一種方法，那就是限時等待時間。給定一個時間，讓 Thread 自動放棄。`tryLock()` 提供這樣方便的功能。

<br>

```java
public class TimeLock implements Runnable {

    public static ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        try {
            if (lock.tryLock(3, TimeUnit.SECONDS)){ // #1
                Thread.sleep(4000);
                System.out.println(Thread.currentThread().getName() + " done task.");
            } else {
                System.out.println(Thread.currentThread().getName() + " get lock failed.");
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        TimeLock task = new TimeLock();
        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");
        t1.start();
        t2.start();
    }
}
```

<br>

印出結果：

<br>

```
Thread-2 get lock failed.
Thread-1 done task.
```

<br>

#1 處 `tryLock()` 接收 2 個參數，一個是等待時間，一個是計時單位。表示在這個鎖請求中，最多等待 3 秒，超過 3 秒沒取得鎖就返回 false，成功取得鎖就返回 true。

`tryLock()` 方法也可以不帶參數執行。在這種情況下當前 Thread 會嘗試取得鎖，如果鎖沒有被占用則申請成功，並返回 true。如果鎖被其他 Thread 占用則不進行等待，馬上返回 false。

由於篇幅原因，這裡有一個使用 `tryLock()` 解決 deal lock 的範例放在下面連結，就不展示在正文了。

<br>

[TryLock.java](./TryLock.java)

<br>
<br>

### 公平鎖

<br>

大多情況下，鎖的申請都是非公平的，也就是說 Thread-1 先申請 Lock-A，接著 Thread-2 也申請 Lock-A。那麼是 Thread-1 還是 Thread-2 獲得鎖是不確定的，系統只會從 Lock-A 的等待隊列中隨機挑選一個，因此沒有公平性可言。我們使用的 `synchronized()` 進行鎖控制就是這種非公平鎖。

ReentrantLock 允許我們對鎖公平進行設定。公平鎖會按照時間先後順序，保證先到先得。公平鎖一大特點是，它不會產生飢餓現象，只要你排隊，最終一定可以取得資源。

<br>

ReentrantLock 建構式中有這樣一個建構函式：

<br>

```java
public ReentrantLock(boolean fair)
```

<br>

當 `fair` 為 `true` 時，表示使用公平鎖。但是公平鎖實現成本比較高，因為內部要維護一個有序隊列。因此默認形況下都是使用非公平鎖。如果不是特別需求，這邊也建議不要用公平鎖。

下面示範一下公平鎖如何使用：

<br>

```java
public class FairLock implements Runnable {

    public static ReentrantLock fairLock = new ReentrantLock(true);

    @Override
    public void run() {
        while(true) {
            try {
                fairLock.lock();
                System.out.println(Thread.currentThread().getName() + " 獲得鎖。");
            } finally {
                fairLock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        FairLock r1 = new FairLock();
        Thread t1 = new Thread(r1, "Thread-1");
        Thread t2 = new Thread(r1, "Thread-2");
        t1.start();
        t2.start();
    }
}
```

<br>

印出部分結果：

<br>

```
...
Thread-1 獲得鎖。
Thread-2 獲得鎖。
Thread-1 獲得鎖。
Thread-2 獲得鎖。
Thread-1 獲得鎖。
Thread-2 獲得鎖。
Thread-1 獲得鎖。
Thread-2 獲得鎖。
...
```

<br>

可以看到兩個 Thread 會交替取得鎖，幾乎不會發生同一個 Thread 多次獲得鎖的狀況。

<br>

__非公平鎖情況下，根據系統調度，一個 Thread 會傾向再次獲取已持有的鎖。這種分配方法是高效的，但無公平性可言。__

<br>
<br>
<br>
<br>

總結一下 ReentrantLock 幾個重要方法：

* `lock()` 取得鎖，若鎖被占用則等待。

* `lockInterruptibly()` 取得鎖，等待時可被中斷。

* `tryLock()` 嘗試獲得鎖，若成功返回 true，失敗返回 false。該方法不等待，立刻返回。

* `tryLock(long time, TimeUnit unit)` 在給定時間內嘗試獲取鎖。

* `unlock()` 釋放鎖。