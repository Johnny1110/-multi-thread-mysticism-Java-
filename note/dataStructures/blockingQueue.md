# 資料共享通道：__BlockingQueue__

<br>

---

<br>

如果需要再多 Thread 進行資料共享，比如 Thread-A 希望給 Thread-B 發一個消息，這種情況下，使用 __BlockingQueue__ 是最合理的。

在使用 __BlockingQueue__ 的情境下，Thread-A 能夠通知 Thread-B，但是 Thread-A 不必知道 Thread-B 的存在，Thread-A 只需要往 __BlockingQueue__ 裡放入消息就好了，至於誰消費消息對 Thread-A 來說不重要，而需要消息的 Thread-B 可以自己取出消息。

<br>

__BlockingQueue__ 是一個 interface，他的主要實現有以下幾個：

<br>

* interface：__BlockingQueue\<E>__

    * class：__ArrayBlockingQueue\<E>__

    * class：__DelayWorkQueue__

    * class：__DelayQueue\<E>__

    * class：__LinkedBlockingQueue\<E>__

    * class：__PriorityBlockingQueue\<E>__

    * class：__SynchronousQueue\<E>__

    * interface：__BlockingDeque\<E>__

<br>

其中比較重要得是 __ArrayBlockingQueue__（基於陣列） 與 __LinkedBlockingQueue__（基於鏈表）

__ArrayBlockingQueue__ 適合做有界隊列，因為陣列可容納最大元素需要在隊列建立時指定。__LinkedBlockingQueue__ 適合做無界隊列，或邊界值非常大的隊列。

<br>

__BlockingQueue__ 的 Block 是阻塞的意思，當 Thread 消費完隊列中所有消息，他如何知道下一條消息何時到來呢？

有一種做法是讓消費 Thread 按照一定時間循環監控隊列。這是一種可行作法，但是會造成不必要資源浪費。而 __BlockingQueue__ 很好的解決這個問題。它會讓消費 Thread 在消息隊列為空時進行等待，當有新的消息進入隊列後，自動將消費 Thread 喚醒。

接下來，就以 __ArrayBlockingQueue__ 為例，解析一下如何實現這樣的功能。

<br>
<br>

__ArrayBlockingQueue__ 的內部元素都放在一個物件陣列中：

<br>

```java
final Object[] items;
```

<br>

向隊列新增元素可以使用 `offer()` 方法與 `put()` 方法，它們都向隊列尾部新增元素。

對於 `offer()` 方法而言，如果當前隊列已滿，他會立刻返回 `false`。如果沒滿則正常讓元素入列。這並沒有涉及到上面提到的等待機制，所以不做討論。

`put()` 方法也是讓新元素入列，但如果隊列滿了，他就一直等待，直到隊列中出現空位。

<br>

從隊列彈出元素可以使用 `poll()` 方法與 `take()` 方法，它們都從隊列頭部取出元素。

如果隊列為空，`poll()` 方法會直接返回 null，而 `take()` 方法會一直等待直到出現新元素。

<br>

綜上所述，`put()` 方法與 `take()` 方法才是 __BlockingQueue__ 的關鍵。

<br>

__ArrayBlockingQueue__ 內部定義了鎖相關屬性：

<br>

```java
final ReentrantLock lock;
private final Condition notEmpty;
private final Condition notFull;
```

<br>

 當執行 `take()` 時，如果隊列為空，則讓當前 Thread 等待在 `notEmpty` 上。新元素入隊時，通知等待在 `notEmpty` 上的 Thread 繼續執行。

<br>

source code ：

```java
public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0)
                notEmpty.await();
            return dequeue();
        } finally {
            lock.unlock();
        }
    }
```

<br>

當 `count` 為 0 時，要求當前 Thread 在 `notEmpty` 上等待，當隊列中有新元素入列時（`enqueue()`），Thread 會得到一個通知：

<br>

```java
private void enqueue(E x) {
        final Object[] items = this.items;
        items[putIndex] = x;  
        if (++putIndex == items.length)  // 不重要
            putIndex = 0;  // 不重要
        count++;
        notEmpty.signal();
    }
```

<br>

可以看到，新元素加入後，會通知等待在 `notEmpty` 上的 Thread 繼續工作。

<br>
<br>

同理，對於 `put()` 方法也是一樣，當隊列滿了，就會讓當前執行新增任務的 Thread 等待。

<br>

```java
public void put(E e) throws InterruptedException {
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length)
                notFull.await();
            enqueue(e);
        } finally {
            lock.unlock();
        }
    }
```

<br>

`count` 滿出來時，使當前 Thread 在 `notFull` 上進行等待。當有元素被取走後（`dequeue()`），自然也會通知在 `notFull` 上等待的 Thread 繼續執行新增任務。

<br>

```java
private E dequeue() {
        final Object[] items = this.items;
        E x = (E) items[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == items.length)
            takeIndex = 0;
        count--;
        if (itrs != null)
            itrs.elementDequeued();
        notFull.signal();
        return x;
    }
```

<br>
<br>

以上就是本章對 __BlockingQueue__ 的介紹。
