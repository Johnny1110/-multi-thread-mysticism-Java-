# ThreadLocal，為每個 Thread 分配專屬於自己的資源（不用共享）

<br>

---

<br>

一般提到 Thread Safe，我們都會想到上鎖。ThreadLocal 是另一種實現 Thread Safe 的辦法。

100 個 Thread 搶一個資源，我們要用鎖來保護這一個資源。如果 100 個資源每個都有屬於自己的資源，那就不用上鎖了，大家都用自己的，其樂融融。

<br><br>

## ThreadLocal 簡單使用

```java
public class ThreadLocalDemo {

    static ThreadLocal<SimpleDateFormat> local = new ThreadLocal<>();

    public static class ParseDate implements Runnable {

        int i = 0;

        public ParseDate(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            try {
                if (local.get() == null) // #1
                    local.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                Date date = local.get().parse("2022-06-30 11:50:" + i%60); // #2
                System.out.println(i + ":" + date);
            } catch (ParseException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++){
            es.execute(new ParseDate(i));
        }
        es.shutdown();
    }
}
```

<br>

#1 處調用了 `local.get()`ㄤ法，__這個方法會在當前 Thread 作用域中找看看有沒有能 get 出來的東西（也就是已經定義好的 SimpleDateFormat）__。如果找不到就為當前 Thread 建立一個並 `set()` 到當前 Thread 裡面。

<br>

#2 處取出當前 Thread 獨享的 __SimpleDateFormat__，進行作業。

<br>
<br>

注意！為每一個 Thread 分派一個物件並不是由 ThreadLocal 完成的，而是要在應用層完成。如果我們不是在 Thread 中 `new` 出物件並 `set()`，改成在外部統一建立，那就跟沒用 ThreadLocal 一樣，無法保證 Thread Safe：

<br>

```java
static ThreadLocal<SimpleDateFormat> local = new ThreadLocal<>();

    public static class ParseDate implements Runnable {

        int i = 0;

        public ParseDate(int i) {
            this.i = i;
            local.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        }
        ...
    }
```

<br>

果然噴錯了...

<br>

```
Exception in thread "pool-1-thread-2" Exception in thread "pool-1-thread-1" Exception in thread "pool-1-thread-3" Exception in thread "pool-1-thread-4" Exception in thread "pool-1-thread-8" Exception in thread "pool-1-thread-10" java.lang.NullPointerException
...
```

<br>
<br>
<br>
<br>
