# 使用 ThreadGroup 管理 Thread 們

<br>

--------------------

<br>

在開發多 Thread 專案時，如果 Thread 數量很多，且功能分配明確，就可以把功能相同的 Thread 放在同一個 ThreadGroup 裡。

<br>

```java
public class ThreadGroupName implements Runnable {
    @Override
    public void run() {
        String groupAndName = Thread.currentThread().getThreadGroup().getName() +
                "-" + Thread.currentThread().getName();
        while (true) {
            System.out.println("I am " + groupAndName);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ThreadGroup threadGroup = new ThreadGroup("PrinterGroup");
        Thread t1 = new Thread(threadGroup, new ThreadGroupName(), "T-1");
        Thread t2 = new Thread(threadGroup, new ThreadGroupName(), "T-2");
        t1.start();
        t2.start();
        System.out.println("Active thread count : " + threadGroup.activeCount());
        threadGroup.list();
    }
}
```

`activeCount()` 可以獲得活動 Thread 的總數，但是因為 Thread 是動態的，因此這個值只是一個估算，無法精確。`list()` 方法可以印出這兩個 Thread 的所有信息。

ThreadGroup 有 `stop()` 方法，可以讓組內 Thread 集體停止，但他的問題跟單體 Thread `stop()` 一樣，所以不能亂用。