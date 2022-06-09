# `ReadWriteLock` 讀寫分離鎖

<br>

---

<br>

一般的重入鎖 （`ReentranLock`） 內部鎖（`synchronized`）理論上所有讀操作、寫操作都是串行的。白話一點就是一般多 Thread 要讀寫一份共享資料時，同一時間只能有一個 Thread 在做這兩種操作之一。當 Thread-1 在做讀取時，Thread-2 Thread-3 則需要等待鎖。

仔細一想，如果今天有 10 個 Thread，僅僅是對共享資料做讀取動作而已，並不會破壞資料完整性。那為何還要一個等一個的執行呢 ? 這顯然不合理。

`ReadWriteLock` 的誕生就是為了解決這個問題。`ReadWriteLock` 允許多 Thread 同時讀，使多個執行讀操作的 Thread 真正做到並行。但考量到資料完整性，寫與寫，寫與讀操作依然需要互相等待持有鎖。

<br>

| -   | 讀 | 寫   |
|:---:|:---:|:---:|
| 讀 |   非阻塞 |  阻塞  |
| 寫 |   阻塞 |  阻塞  |

<br>

如果在應用中，讀操作次數遠遠大於寫操作，那就非常適合 `ReadWriteLock` 進行發揮。

來看一個範例：

<br>

```java
public class ReadWriteLockDemo {

    private static Lock lock = new ReentrantLock();
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private static Lock readLock = readWriteLock.readLock();
    private static Lock writeLock = readWriteLock.writeLock();

    private int value;

    public Object handleRead(Lock lock){
        try{
            lock.lock();
            Thread.sleep(1000);
            return value;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }finally {
            lock.unlock();
        }
    }

    public void handleWrite(Lock lock, int index) {
        try {
            lock.lock();
            Thread.sleep(1000);
            this.value = index;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        final ReadWriteLockDemo demo = new ReadWriteLockDemo();
        Runnable readTask = () -> {
            demo.handleRead(readLock);
            //demo.handleRead(lock);
        };

        Runnable writeTask = () -> {
            demo.handleWrite(writeLock, new Random().nextInt());
            //demo.handleWrite(lock, new Random().nextInt());
        };

        for (int i = 0; i < 18; ++i){
            new Thread(readTask).start();
        }

        for (int i = 18; i < 20; ++i){
            new Thread(writeTask).start();
        }
    }
}
```

<br>

以上是一個使用 `ReadWriteLock` 的例子，18 個 Thread 做讀操作，2 個 Thread 做寫操作。一般情況下 2-4 秒整個任務就執行完成了。如果把註解部分打開，換成一般的 Lock 來做則需要等待至少 20 秒。這就是 `ReadWriteLock` 帶來的效率提升。