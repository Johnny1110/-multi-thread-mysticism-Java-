# `join()` 與 `yield()`

<br>

-------

<br>

在某些時候，一個 Thread 的輸入需要仰賴另一個 Thread 的輸出，換句話說就是這個 Thread 需要等待依賴的 Thread 執行完畢，才能繼續執行。JDK 提供了 `join()` 來實現這個功能。

<br>

```java
public final void join() throws InterruptedException

public final synchronized void join(long millis) throws InterruptedException
```

<br>

第一個 `join()` 表示無限等待，阻塞當前 Thread，直到目標 Thread 執行完畢。

第二個 `join(long millis)` 給出一個等待時間，超出給定時間目標 Thread 還未完成任務的話，當前 Thread 就不等了，繼續執行下去。

<br>

以下給一個使用範例：

<br>

```java
public class JoinDemo {

    public volatile static int i = 0;

    public static class AddThread extends Thread {
        @Override
        public void run(){
            for (i=0; i<1000000; i++){
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AddThread addThread = new AddThread();
        addThread.start();
        addThread.join(); // #1
        System.out.println(i);
    }
}
```

<br>

__#1__ 處如果不加 `join()` 的話，當前 Thread 根本不會等 addThread 執行完加總就印出 `i` 了。

<br>

`join()` 的本質是讓調用他的 Thread `wait()` 在當前 Thread 物件上。以下是 JDK 中的 `join()` 實現。

<br>

```java
while(isAlive()){
    wait(0);
}
```

<br>

當 join 的 Thread 執行完成後，他會在退出前調用 `notifyAll()` 通知所有 Thread 繼續執行。

<br>
<br>

`Thread.yield()` 定義如下：

<br>

```java
public static native void yield();
```

<br>

`yield()` 只要執行，就會使當前 Thread 讓出 CPU，讓出 CPU 之後並不代表當前 Thread 不執行了，而是重新進行 CPU 資源爭奪。至於能否被再次分配到舊不一定了。

`yield()` 就好像是說 : 我先休息一下，資源讓給其他 Thread 讓其他人有一點工作機會。

<br>

使用範例：

<br>

```java
public class YieldDemo {

    private final static Object LOCK = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true){
                synchronized (LOCK){
                    System.out.println("t1 is running");
                }
                Thread.yield();
            }
        });

        Thread t2 = new Thread(() -> {
            while (true){
                synchronized (LOCK){
                    System.out.println("t2 is running");
                }
                Thread.yield();
            }
        });

        t1.start();
        t2.start();
    }

}
```

<br>
<br>

__注意!__ 已經持有鎖資源的 Thread 對於重新取得鎖有絕對的優勢，所以經常能看到 2 個 Thread 在交替進行作業時 （印出一些　String），大概率會出現某個 Thread 一次印出很多筆，才換另一個 Thread 印出多筆。`yield()` 就可以讓鎖分配稍微更公平些。