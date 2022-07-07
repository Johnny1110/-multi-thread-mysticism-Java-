# ThreadLocal，為每個 Thread 分配專屬於自己的資源（不用共享）

<br>

---

<br>

一般提到 Thread Safe，我們都會想到上鎖。ThreadLocal 是另一種實現 Thread Safe 的辦法。

100 個 Thread 搶一個資源，我們要用鎖來保護這一個資源。如果 100 個資源每個都有屬於自己的資源，那就不用上鎖了，大家都用自己的，其樂融融。

<br><br>

<span id="1">

## ThreadLocal 簡單使用

<br>

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

## ThreadLocal 實現原理

<br>

__ThreadLocal__ 保證了這些物件只能被當前 Thread 訪問。

<br>

實現原理要從 `set()` 與 `get()` 說起。

<br>

### `set()` 方法：

<br>

```java
public void set(T val) {
    Thread thread = Thread.currentThread();
    ThreadLocalMap localMap = getMap(thread);
    if (localMap!=null) {
        map.set(this, val); // this 是 ThreadLocal 物件
    }else{
        createMap(thread, value)
    }
}
```

<br>

使用 `set()` 時，先取得當前 Thread，然後透過 `getMap()` 取得當前 __ThreadLocalMap__（當前 Thread 專屬的 Map），並將放入其中。

__ThreadLocalMap__ 不是真正意義上的 __Map__，但是我們可以簡單理解他是一個 __HashMap__，他是定義在 Thread 內部的成員。

<br>

```java
ThreadLocal.ThreadLocalMap threadLocals = null;
```

<br>

放入 __ThreadLocal__ 中的資料，正是寫入了 __ThreadLocalMap__ 中。其中 key 為 __ThreadLocal__ 當前物件，value 就是要存的值。__這樣一來要 `get()` 時只要以當初執行 `set()` 方法的 ThreadLocal 作為 key 就可以取出對應值了__。

<br>

### `get()` 方法：

<br>

```java
public T get() {
        Thread thread = Thread.currentThread();
        ThreadLocalMap localMap = getMap(thread);
        if (localMap != null) {
            ThreadLocalMap.Entry e = localMap.getEntry(this);
            if (e != null) {
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }
```

<br>

__ThreadLocal__ 將自己作為 key，從當前 Thread 中取出實際 value。

<br>
<br>
<br>
<br>

以上，我們了解了 __ThreadLocal__ 內部實現，這其中有一個問題，就是這些值維護在 Thread 內部，所以只要 Thread 不退出，該物件的引用會一直存在。

當 Thread 退出時要進行一些清理工作，其中就包括要清理 __ThreadLocalMap__。

<br>

### `exit()` 方法

<br>

```java
private void exit() {
        if (group != null) {
            group.threadTerminated(this);
            group = null;
        }
        target = null;
        /* Speed the release of some of these resources */
        threadLocals = null;
        inheritableThreadLocals = null;
        inheritedAccessControlContext = null;
        blocker = null;
        uncaughtExceptionHandler = null;
    }
```

<br>

__注意！如果使用 ThreadPool，那意味著當前 Thread 不一定會退出，所以如果把很吃資源的物件放到 ThreadLocal 中，用幾次之後就不用了，也不回收，一直放在那邊就可能會造成 leakOfMemory 的問題。如果要及時回收，可以使用 `ThreadLocal.remove()` 方法移除保存的值，讓 GC 回收掉它。__

<br>
<br>

## 關於 ThreadLocal 回收

<br>

接下來內容稍微有點複雜了，不想看可以跳過這邊，不影響使用，這部分算是加強觀念。

<br>

我們可以寫出類似 `obj=null` 的方式加速 __ThreadLocal__ 的回收。對於 __ThreadLocal__ 而言，如果我們執行 `local = null`，那麼它所對應的所有 Thread 局部變數都 __可能__ 被回收。看一下範例：

<br>

```java
public class ThreadLocalDemo_GC {

    static volatile ThreadLocal<SimpleDateFormat> local = new ThreadLocal<SimpleDateFormat>() {
        protected void finalize() { // #1
            System.out.println(this.toString() + "has been GC(ThreadLocal).");
        }
    };

    static volatile CountDownLatch cd = new CountDownLatch(10000);

    public static class ParseDate implements Runnable {

        int i = 0;

        public ParseDate(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            try {
                if (local.get() == null) {
                    local.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {
                        protected void finalize() { // #2
                            System.out.println("SimpleDateFormat has been GC.");
                        }
                    });
                    System.out.println(Thread.currentThread().getId() + " created SimpleDateFormat.");
                }
                Date t = local.get().parse("2022-06-31 11:34:" + i % 60);
            } catch (ParseException e) {
                e.printStackTrace();
            }finally {
                cd.countDown();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10000; i++){
            es.execute(new ParseDate(i));
        }
        cd.await();
        System.out.println("mission-1 done!");
        local = null; // #3
        System.gc();
        System.out.println("first GC done.");

        local = new ThreadLocal<SimpleDateFormat>();
        cd = new CountDownLatch(10000);

        for (int i = 0; i < 10000; i++){
            es.execute(new ParseDate(i));
        }
        cd.await();
        System.out.println("mission-2 done!");
        es.shutdown();
        Thread.sleep(1000);
        System.gc();
        System.out.println("sec GC done.");
    }
}
```

<br>

範例追蹤 __ThreadLocal__ 以及內部的 __SimpleDateFormat__ 物件的 GC 情況。#1 與 #2 處重載了 `finalize()` 方法，這樣一來，在 GC 時就可以看到印出的資訊。

在 `main()` 方法中，我們先後進行 2 次任務提交，每次執行 10000 個任務，第一次任務完成後，我們將 `local` 標籤設為 null，嘗試讓，接著進行一次 GC，在第二次提交任務完成後，再進行一次 GC。來看一下執行結果：

<br>

輸出:

<br>

```
16 created SimpleDateFormat.
21 created SimpleDateFormat.
19 created SimpleDateFormat.
13 created SimpleDateFormat.
12 created SimpleDateFormat.
18 created SimpleDateFormat.
20 created SimpleDateFormat.
15 created SimpleDateFormat.
17 created SimpleDateFormat.
14 created SimpleDateFormat.
mission-1 done!
first GC done.
mysticism.advancedConception.ThreadLocalDemo_GC$1@195724dbhas been GC(ThreadLocal).
19 created SimpleDateFormat.
13 created SimpleDateFormat.
15 created SimpleDateFormat.
20 created SimpleDateFormat.
17 created SimpleDateFormat.
16 created SimpleDateFormat.
21 created SimpleDateFormat.
18 created SimpleDateFormat.
14 created SimpleDateFormat.
12 created SimpleDateFormat.
mission-2 done!
sec GC done.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
SimpleDateFormat has been GC.
```

<br>

首先 ThreadPool 中 10 個 Thread 各自建立一個 __SimpleDateFormat__ 物件，緊接著任務完成，進行第一次 GC，可以看到由於我們把 `local` 標籤設為 null，所以 __ThreadLocal__ 被回收了。接著提交第二次任務，一樣建立 10 個 __SimpleDateFormat__ 物件，完成任務後進行第二次 GC，在第二次 GC 後，我們先前建立的共 20 個 __SimpleDateFormat__ 一起被回收了。雖然沒有 `remove()` 物件，但 GC 還是會回收它們。

<br>

進一步了解 __ThreadLocalMap__ 實現，他比較像是 __WeakHashMap__，__ThreadLocalMap__ 實現使用了弱引用，JVM 在發現弱引用時會立刻回收。__ThreadLocalMap__ 內部由一堆 __Entry__ 構成，每一個 __Entry__ 都是 __WeakReference<ThreadLocal>__。

<br>


```java
static class Entry extends WeakReference<ThreadLocal> {
    Object value;
    
    Entry(ThreadLocal key, Object val) {
        super(key);
        value = val;
    }
}
```

<br>

弱引用就跟 Linux 的鏈結檔案（ln 指令）很像。

使用 __ThreadLocal__ 作為 __Map__ 的 `key`，實際上它不真正持有 __ThreadLocal__ 引用（只持有弱引用，當除本身弱引用指向物件外，沒有任何強引用指向物件時，弱引用直接當不存在處理）。當 __ThreadLocal__ 外部的強引用被回收時，__ThreadLocalMap__ 中的 key 也就變成 null。當系統進行 ThreadLocalMap 清理時，就會自動將這些沒有被引用的物件回收。

<br>
<br>
<br>
<br>


## ThreadLocal 對性能的提升

<br>

我們做一個測試，測試內容是使用 __Random__ 物件產生隨機數，分別做成使用 __ThreadLocal__ 版本與多執行緒共享一個 __Random__ 版本。

__Random__ 本身是 Thread Safe 的物件，所以這邊就不需要給他上鎖了。

具體細節都在 code 中寫了註解，直接看就可以。

<br>

```java
public class ThreadLocalPerformance {

    public static final int GEN_COUNT = 10000000; // 生成數量
    public static final int THREAD_COUNT = 4; // 執行緒數量
    static ExecutorService exe = Executors.newFixedThreadPool(THREAD_COUNT);

    public static Random rnd = new Random(123); // 第一種不使用 ThreadLocal 做法

    public static ThreadLocal<Random> local = new ThreadLocal<Random>() { // 第二種使用 ThreadLocal 做法
        protected Random initialValue() {
            return new Random(123);
        }
    };

    public static class RandTask implements Callable<Long> {
        // mode 為 0 代表多 Thread 共用一個 Random，為 1 代表各 Thread 都各分配一個 Random (ThreadLocal)
        private int mode = 0;

        public RandTask(int mode) {
            this.mode = mode;
        }

        public Random getRandom() {
            if (mode == 0) {
                return rnd;
            }
            if (mode == 1) {
                return local.get();
            }
            return null;
        }

        @Override
        public Long call() {
            long begin = System.currentTimeMillis();
            for (long i = 0; i < GEN_COUNT; ++i) {
                getRandom().nextInt();
            }
            long end = System.currentTimeMillis();
            long result = end - begin;
            System.out.println(Thread.currentThread().getName() + " spend " + result + " ms.");
            return result;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int mode = 0;
        Future<Long>[] futs = new Future[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i) {
            futs[i] = exe.submit(new RandTask(mode));
        }

        long totalTime = 0;
        for (int i = 0; i < futs.length; i++) {
            totalTime += futs[i].get();
        }
        System.out.println("使用 mode-" + mode+ " 共所耗時間：" + totalTime + "ms");
        exe.shutdown();
    }
}
```

<br>

這裡先測試 `mode = 0` 的情況（共享單一 __Random__）：

<br>

```
pool-1-thread-3 spend 3136 ms.
pool-1-thread-4 spend 3281 ms.
pool-1-thread-2 spend 3287 ms.
pool-1-thread-1 spend 3287 ms.
使用 mode-0 共所耗時間：12991ms
```

大概執行了 3.2 秒左右完成。

<br>

換成 `mode = 1` 來看一下（使用 __ThreadLocal__）：

<br>

```java
pool-1-thread-3 spend 136 ms.
pool-1-thread-1 spend 142 ms.
pool-1-thread-2 spend 144 ms.
pool-1-thread-4 spend 146 ms.
使用 mode-1 共所耗時間：568ms
```

<br>

平均一個花了 0.14 秒，效率直接體現出來了。