# 無鎖的 Array：__AtomicIntegerArray__

<br>

-------

<br>

JDK 提供了原子化處理的陣列結構，有：__AtomicIntegerArray__、__AtomicLongArray__、__AtomicIntegerReferenceArray__。

<br>

這裡以 __AtomicIntegerArray__ 為例來示範原子陣列使用方式。

<br>

__AtomicIntegerArray__ 本質上是對 `int[]` 封裝，使用 __Unsafe__ 類通過 CAS 控制 `int[]`，保證 Thread Safe。以下列出常用 API：

<br>

```java
// 獲得 index 為 i 的元素
public final int get(int i)
// 獲得長度
public final int length()
// 設定 index 為 i 元素的新值，返回舊值
public final int getAndSet(int i, int newVaule)
// CAS 操作，index 為 i 的元素等於 expect 則設置為 update，成功就返回 true
public final boolean compareAndSet(int i, int expect, int update)
// 將 index 為 i 的元素 + 1，並返回舊值
public final int getAndIncrement(int i)
// 將 index 為 i 的元素 - 1，並返回舊值
public final int getAndDecrement(int i)
// 將 index 為 i 的元素 + delta(delta 可以為負值)，並返回舊值
public final int getAndAdd(int i, int delta)
```

<br>

使用範例：

<br>

```java
public class AtomicIntegerArrayDemo {

    static AtomicIntegerArray arr = new AtomicIntegerArray(10);

    public static class AddTask implements Runnable{

        @Override
        public void run() {
            IntStream.range(0, 10000).forEach(i -> {
                arr.getAndIncrement(i%arr.length());
            });
        }
    }

    public static void main(String[] args) {
        Thread[] ts = new Thread[10];
        IntStream.range(0, 10).forEach(i -> {
            ts[i] = new Thread(new AddTask());
        });
        IntStream.range(0, 10).forEach(i -> {
            ts[i].start();
        });
        IntStream.range(0, 10).forEach(i -> {
            try {
                ts[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(arr);
    }
}
```

<br>



__AddTask__ 對宣告的陣列個元素各加 1000 次，宣告 10 個 Thread，所以是一共加 10000 次。事實上，執行結果也符合我們預期：

<br>

```
[10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000]
```

<br>

__AtomicIntegerArray__ 保證了陣列的 Thread Safe。