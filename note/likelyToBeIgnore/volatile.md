# `volatile` 可以保證可見性與有序性，但不能保證原子性

<br>

--------------------------------

<br>

`volatile` 中文翻譯是 "易揮發的"，這就代表被加上這個關鍵字的變數是一個易變的值，使用這個關鍵字來告訴 Java 虛擬機要尤其注意，不能隨意變動優化目標指令，因為這個值極有可能會被其他 Thread 修改。

為了確保 `volatile` 變數被修改後，其他 Thread 都可以 "看見" 這個改動，JVM 就必須採取一些特殊手段，保證其可見性。比如根據編譯器優化原則，不使用 `volatile` 的變數被修改後，其他 Thread 可能並不會被通知到，一但用了 `volatile`，JVM 就會特別小心處理他的變動。

<br>

__要注意的是，`volatile` 並不能代替鎖，他不能保證一需複合操作的原子性，他只能確保一個 Thread 修改資料後，其他 Thread 能看到這個改動。但當兩個 Thread 同時修改某個資料時，卻依然會產生衝突。__ 例如：

<br>

```java
public class VolatileBadDemo {

    private static volatile int i = 0;

    public static class PlusTask implements Runnable {

        @Override
        public void run() {
            for (int k = 0; k < 10000; k++){
                i++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++){
            threads[i] = new Thread(new PlusTask());
            threads[i].start();
        }
        for (int i = 0; i < 10; i++){
            threads[i].join();
        }
        System.out.println(i);
    }

}
```

<br>

理論上 10 個 Thread 對 `i` 做 10000 次遞加，應該得到 100000 才對，但事實上得到的結果永遠小於 100000。

<br>

__總結起來，何時可以安心相信 `volatile` 呢 ? 就是在單純多 Thread 為變數賦值時可以相信他，當多 Thread 要對變數做運算動作時，還是需要有鎖去配合，不能直接用 `volatile` 來取代鎖。__

<br>

看範例之前，要先了解一件事，就事 JVM 的 Server mode 與 Client mode。現在只要是 64 位元的主機，JVM 一律使用 Server mode，32 位元的主機可以任意切換兩種模式。

簡單說一下 Server mode 與 Client mode 差在哪裡，就是 Server mode Server mode 啟動速度比 Client mode 慢 10%，但執行速度卻是 Client mode 的 10 倍。

檢查自己 JVM 處於哪一種 mode，可以透過 `java -version` 指令得知：

<br>

```
PS D:\IDEA_WORKSPACE\Java-multi-thread-mysticism> java -version
openjdk version "1.8.0-262"
OpenJDK Runtime Environment (build 1.8.0-262-b10)
OpenJDK 64-Bit Server VM (build 25.71-b10, mixed mode)
```

<br>

最後一行的 `OpenJDK 64-Bit Server VM` 就可得知是 Server Mode。

為甚麼要提到這個呢 ? 因為 Client mode 下，[JIT](https://zh.wikipedia.org/wiki/%E5%8D%B3%E6%99%82%E7%B7%A8%E8%AD%AF) 沒有足夠優化，導致有些時候不用 `volatile` 也沒關係，因為不做優化就不用暫存技術，所以取值都直接去記憶體找（保證最新）。Server mode 下由於系統優化使用暫存技術，所以不用 `volatile` 的話，JVM 並不會特別注意這個變數。

<br>

了解以上知識後，來看一下範例：

<br>

```java
public class Novisibility {

    //private static boolean ready;
    private volatile static boolean ready;
    private static int number;

    private static class Reader extends Thread {
        @Override
        public void run() {
            while(!ready);
            System.out.println(number);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Reader().start();
        Thread.sleep(1000);
        number = 42;
        ready = true;
        Thread.sleep(10000);
    }

}
```

<br>

Reader 只有在資料準備好時（`ready` 為 true）時才會印出 `number`。他透過 `ready` 值判斷是否該印出結果，在主 Thread 中，開啟 Reader 後就為 `number` 和 `ready` 賦值。

可以把 `ready` 變數開頭宣告的 `volatile` 拿掉試試看，結果會導致印不出資料。