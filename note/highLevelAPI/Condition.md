# `wait()`、`notify()` 的替代品：`Condition`

<br>

--------------------------------

<br>

Condition 與 `wait()`、`notify()` 作用大致上相同，但 `wait()`、`notify()` 是與 `synchronized()` 關鍵字組合使用的，Condition 則是與 ReentranLock 組合使用。

通過 ReentranLock 的 `newCondition()` 方法可以產生一個與它綁定的 Condition：

```java
ReentranLock lock = new ReentranLock();
Condition condition = lock.newCondition();
```

<br>

Condition 介面提供以下基本方法：

<br>

* `void await()`

    使當前 Thread 等待，同時釋放鎖，當其他 Thread 使用 `signal()` 或 `signalAll()` 時，Thread 會重新獲得鎖並繼續執行。或當 Thread 被中斷時，也能跳出等待。這與 `Object.wait()` 方法相似。

    <br>

* `void awaitUninterruptibly()`

    它與 `await()` 方法基本相同，但它並不會在等待過程中響應中斷。

    <br>

* `void singal()`

    `signal()` 用於喚醒一個在等待中的 Thread，`signalAll()` 會喚醒所有 Thread。這與 `Object.notify()` 方法相似。

    <br>
    <br>

Condition 演示

<br>

```java
public class ReenterLockCondition implements Runnable {

    public static ReentrantLock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    @Override
    public void run() {
        try {
            lock.lock();
            condition.await();  // #1
            System.out.println("Thread is going on.");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReenterLockCondition task = new ReenterLockCondition();
        Thread t1 = new Thread(task);
        t1.start();
        Thread.sleep(2000);
        lock.lock();  // #2
        condition.signal();  // #3
        lock.unlock();  // #4
    }
}
```

<br>

#1 處 Thread 使用 `await()` 使當前 Thread 等待
並釋放當前鎖。

#2 處當前 Thread 先取得了鎖，為之後觸發 `signal()` 做準備（`signal()` 前需要取得鎖）

#3 處使用 `signal()` 通知等待隊列中的 Thread 繼續執行。

#4 處釋放鎖，使剛剛等待的 t1 可以取得鎖繼續執行。

<br>


