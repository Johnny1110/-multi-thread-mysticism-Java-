# Thread 完成數量達標才放行：CountDownLatch

<br>

-------

<br>

CountDownLatch 是用來控制 Thread 等待的工具，就如同它 Latch 字面上的意思 : 閂鎖。它會擋住執行完任務的 Thread，直到數量達到放行標準才會讓主執行緒繼續工往下作業。

CountDownLatch 建構函式接收一個整數作為參數：

<br>

```java
public CountDownLatch (int count)
```

<br>

下面示範一下如何使用

<br>

```java
public class CountDownLatchDemo implements Runnable {

    static final CountDownLatch end = new CountDownLatch(10);
    static final CountDownLatchDemo demo = new CountDownLatchDemo();

    @Override
    public void run() {
        try{
            Thread.sleep(new Random().nextInt(10)*1000);
            System.out.println("recharge complete.");
            end.countDown(); // 充能完畢後等待
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 11; i++){
            exec.submit(demo);
        }
        end.await(); // 等待計數器通知技術完成
        System.out.println("Fire!"); // 10 充能完畢，開火 !
        exec.shutdown();
    }
}
```

<br>

印出結果：

<br>

```
recharge complete.
recharge complete.
recharge complete.
recharge complete.
recharge complete.
recharge complete.
recharge complete.
recharge complete.
recharge complete.
recharge complete.
Fire!
recharge complete.
```

<br>

可以看到，直到第 10 個 Thread 完成充能工作後，主執行緒才繼續執行到 Fire 動作。