# 千萬不要對 Integer 上鎖

<br>

-------

<br>

如標題，千萬不要對 Integer 上鎖，在解釋為甚麼之前，先來看一下對 Integer 上鎖會發生甚麼事：

<br>

```java
public class BadLockOnInteger implements Runnable {

    private static Integer i = 0;

    @Override
    public void run() {
        for (int j = 0; j < 100000; ++j){
            synchronized (i){
                i++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new BadLockOnInteger());
        Thread t2 = new Thread(new BadLockOnInteger());
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(i);
    }
}
```

<br>

乍一看感覺沒有任何問題，對 `i` 上鎖確保執行緒安全問題。但事實上卻不是這樣一回事，看一下執行結果：

<br>

```
146206
```

<br>

不管執行幾次，最終結果基本上都會小於預期值，明明我們對 `i` 上鎖了，為甚麼還會這樣呢？這就是 Java 多執行緒中一個比較典型隱晦的錯誤了。

<br>
<br>

要想理解這個錯誤如何發生，我們要先從 Integer 說起。在 Java 中的 Integer 屬於不變的物件，也就是說 __一旦 Integer 被建立就不可被修改__。如果你有一個 Integer `i` 代表 1，那你永遠不可能使 `i` 變成 2。那你要是說 `i = 2` 不就是把 `i` 改成 2 了嗎 ? 事實上底層是新建了一個 Integer 讓新的 Integer 代表 2，然後讓 `i` 標籤指向這個新建立的 Integer。

<br>

把 `i++` 反編譯，其實就可以發現事實上 `i++` 變成了：

<br>

```java
i = Integer.valueOf(i.intValue() + 1);
```

<br>

我們再進一步看看 `valueOf(int i)` 本質：

<br>

```java
public static Integer valueOf(int i) {
    assert IntegerCache.high >= 127;
    if (i >= IntegerCache.low && i <= IntegerCache.high) {
        return Integer.cache[i + (-IntegerCache.low)];
    }
    return new Integer(i);
}
```

<br>

`Integer.valueOf()` 實際上是一個工廠方法，它會傾向於返回一個代表指定數值的 Integer 物件。因此 `i++` 本質是建立一個新 Integer 物件，並將 `i` 標籤指向這個新 Integer。

上面的 `valueOf()` 方法主要是先看看 `IntegerCache` 快取中有沒有現成的 Integer，JVM 預設會建立一個 `Integer[]`，把範圍在 -127 ~ 127 之間的數字預先建立好放進去，只要有需要就不用重複建立新 Integer 物件，直接從這個暫存中取用就好。

<br>

如此一來，我們就可以明白問題所在了，多 Thread 間鎖住的並不是同一個 Integer 物件，因為 `i` 標籤所指向的 Integer 一直在變換，兩個 Thread 每次加鎖都加到不同的 Integer 物件上了，從而導致出現 Thread Safe 問題。