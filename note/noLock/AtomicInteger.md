# 無鎖的 Thread Safe 整數：__AtomicInteger__

<br>

---

<br>

為了讓 Java 使用者可以享用 CAS 等 CPU 指令，JDK 提供一個 __atomic__ package，裡面實現了一些直接使用 CAS 操作的類別。

__AtomicInteger__ 應該就算是最常用的一個類別，他是整數，但是與 Integer 不同，它是可變的（可以同步化，但沒必要），且是 Thread Safe。

列舉 __AtomicInteger__ 的主要方法：

<br>

```java
public final int get()                                  // 取得當前值
public final void set(int newVaule)                     // 設定當前值
public final int getAndSet(int newVaule)                // 設定新值，返回舊值
public final boolean compareAndSet(int expect, int n)   // 當前值為 expect 則設置為 n
public final int getAndIncrement()                      // 當前值 +1，返回舊值
public final int getAndDecrement()                      // 當前值 -1，返回舊值
public final int getAndAdd(int delta)                   // 當前值 + delta，返回舊值
public final int incrementAndGet()                      // 當前值 +1，返回新值
public final int decrementAndGet()                      // 當前值 -1，返回新值
public final int addAndGet(int delta)                   // 當前值 + delta，返回新值
```

<br>

__AtomicInteger__ 內部有兩個重要 Field：

<br>

```java
private volatile int value; // 當前值

private static final long valueOffset; // value 在 AtomicInteger 物件中的位置偏移量
```

<br>



__AtomicInteger__ 的使用非常簡單：

<br>

```java
public class AtomicIntegerDemo {

    static AtomicInteger integer = new AtomicInteger();

    public static class AddTask implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 1000; ++i) {
                integer.incrementAndGet();  // #1
            }
        }

        public static void main(String[] args) {
            ExecutorService es = Executors.newFixedThreadPool(10);
            IntStream.range(0, 10).forEach(i -> {
                es.submit(new AddTask());
            });
            es.shutdown();
            while (integer.get() != 10000);
            System.out.println(integer);
        }
    }
}
```

<br>

#1 處的 `incrementAndGet()` 方法會使用 CAS 操作將自己 + 1，並返回當前值。

使用 __AtomicInteger__ 比使用鎖來得好，這是一定的，看一下 `incrementAndGet()` 是如何實現的，這很有趣。

<br>

以下是 JDK 1.8 的實現：

```java
public final int incrementAndGet() {
        return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
    }

public final int getAndAddInt(Object o, long offset, int delta) {
        int expect;
        do {
            expect = getIntVolatile(o, offset); // 期望值
        } while (!compareAndSwapInt(o, offset, expect, expect + delta)); // CAS，回傳 false 就重試，直到成功
        return expect;
    }
```

<br>

`compareAndSwapInt()` 一旦檢測到發生衝突（實際值與期望值不符）就回傳 false，這裡會用 `while` 迴圈重試
直到成功修改。

<br>
<br>
<br>
<br>

與 __AtomicInteger__ 類似的還有：

* __AtomicLong（long）__ 
 
* __AtomicBoolean（boolean）__
 
* __AtomicReference（物件引用）__
