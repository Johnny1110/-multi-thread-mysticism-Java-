# 鎖分離

<br>

---

<br>

鎖分離是根據讀寫操作的不同，對獨佔鎖進行有效的分離。

舉經典例子就是 __LinkedBlockingQueue__。在 __LinkedBlockingQueue__ 中的 `take()` 與 `put()` 方法分別實現從對列頭部取出值，和從尾部放入值。從理論上來說，二者並不存在衝突，如果使用獨佔鎖來鎖住整體隊列，那麼 `take()` 與 `put()` 方法就不可能同時執行，它們彼此會等待對方釋放鎖，影響多執行緒效能。

JDK 的實現使用的是 2 把不同鎖來分離 `take()` 與 `put()`。

<br>

```java
private final ReentrantLock takeLock = new ReentrantLock();
private final Condition notEmpty = takeLock.newCondition(); // take() 方法使用的 takeLock

private final ReentrantLock putLock = new ReentrantLock();
private final Condition notFull = putLock.newCondition();   // put() 方法使用的 putLock
```

<br>

`take()` 與 `put()` 使用了不同的鎖，因此它們之間就互相獨立了，只需要 `take()` 與 `take()` 之間，`put()` 與 `put()` 之間進行競爭就可以了。

<br>
<br>

`take()` 方法實現： 

```java
public E take() throws InterruptedException {
        E x;
        int c = -1;
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            try {
                while (count.get() == 0) {
                    notEmpty.await();
                }
            } catch (InterruptedException e) {
                notEmpty.signal();
                throw e;
            }
            x = extract(); // 取出頭部第一筆資料，不放回。
            c = count.getAndDecrement(); // 數量 -1，原子操作，c 為 -1 前的值。

            if (c > 1) {
                notEmpty.signal(); // 數量還有，可以繼續取值
            }
        }finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull(); // c == capacity 代表 take 之前空間被塞滿了，put() 都在等，所以取出一個後要通知 put 操作可以繼續了。
        }
        return x;
    }
```

<br>
<br>

`put()` 方法實現： 

```java
public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        int c = -1;
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try{
            try{
                while (count.get() == capacity){
                    notFull.await();
                }
            }catch (InterruptedException ex) {
                notFull.signal();
                throw ex;
            }
            insert(e);
            c = count.getAndIncrement(); // 更新總數量，c 是 count+1 前的值。
            if (c + 1 < capacity) { // 如果新增後的數量 < 總容量。
                notFull.signal(); // 通知等待的 put() 還有空間。
            }
        }finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();  // c == 0 代表 put() 前隊列已空，take() 們都在等，所以要通知 take() 們可以繼續。
        }
    }
```

<br>

透過 `takeLock` 與 `putLock` 實現了 __LinkedBlockingQueue__ 的讀寫分離，真正意義上實現了併發。