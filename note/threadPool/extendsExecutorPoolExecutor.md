# 擴展 ThreadPool，繼承 __ThreadPoolExecutor__

<br>

---

<br>

如果我們不滿足於使用 __ThreadPoolExecutor__，還想要多加入一些功能，比如監控每個任務執行的開始與結束，或者自訂一些增強功能，我們可以繼承 __ThreadPoolExecutor__ 進行擴展。

__ThreadPoolExecutor__ 提供 3 個可繼承方法來實現對 ThreadPool 控制：

<br>

* `beforeExecute()` 執行前

* `afterExecute()` 執行後

* `terminated()` 執行結束

<br>

__ThreadPoolExecutor.Worker.runTask()__ 方法內部實現如下：

<br>

```java
boolean ran = false;
beforeExecute(thread, task);
try {
    task.run();
    ran = true;
    afterExecute(task, null);
    ++completedTasks;
} catch (RuntimeException ex) {
    if (!ran) {
        afterExecute(task, ex);
    }
    throw ex;
}
```

<br>

__ThreadPoolExecutor.Worker__ 是 __ThreadPoolExecutor__ 的內部內別，他是一個實現 __Runnable__ 介面的類別。__ThreadPoolExecutor__ 中的工作 Thread 也正是 __Worker__。

`Worker.runTask()` 方法會被多個 Thread 訪問。因此 `beforeExecute()`，`afterExecute()` 也會被多 Thread 訪問。

在默認的 ThreadPoolExecutor 實現中，提供了空的 `beforeExecute()` 與 `afterExecute()`，在實際使用中，可以繼承它來實現對 ThreadPool 運行狀態追蹤。下面演示一個範例：

<br>

```java
public class ExtThreadPool {

    public static class MyTask implements Runnable {
        public String name;

        public MyTask(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println("正在執行 Thread ID: " + Thread.currentThread().getId()
                    + " Task Name: " + name);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        ExecutorService es = new ThreadPoolExecutor(
                5,
                5,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<Runnable>()
        ) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println("準備執行：" +((MyTask)r).name);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println("執行完成：" +((MyTask)r).name);
            }

            @Override
            protected void terminated() {
                System.out.println("ThreadPool 退出");
            }
        };

        IntStream.range(1, 6).forEach(i -> {
            MyTask task = new MyTask("Task-No." + i);
            es.execute(task);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        es.shutdown();
    }
}
```

<br>

我們擴展了 __ThreadPoolExecutor__，定義了 3 個方法 `beforeExecute()`、`afterExecute()`、`terminated()`。

這裡我們使用了 `execute()` 替代原本一直在使用的 `submit()`。因為 `submit()` 會把 __Runnable__ 轉成子類： __FutureTask__ 物件，在進入 `beforeExecute()` 等方法時，無法將 __FutureTask__ 強轉成自訂的 __MyTask__ 物件，所以這邊暫時先用　`execute()`　頂一下。

<br>

提交完成後，使用 `shutdown()` 方法關閉 ThreadPool。這是一個比較安全的方法，如果當前有 Thread 在執行，`shutdown()`並不會立即暴立停止所有任務，它會等所有任務都執行完畢再關閉 ThreadPool，__它就像發送一個關閉信號，在 `shutdown()` 方法執行後，這個 ThreadPool 就不能在接受新任務了。__

<br>

執行結果：

<br>

```
準備執行：Task-No.1
正在執行 Thread ID: 12 Task Name: Task-No.1
準備執行：Task-No.2
正在執行 Thread ID: 13 Task Name: Task-No.2
準備執行：Task-No.3
正在執行 Thread ID: 14 Task Name: Task-No.3
準備執行：Task-No.4
正在執行 Thread ID: 15 Task Name: Task-No.4
準備執行：Task-No.5
正在執行 Thread ID: 16 Task Name: Task-No.5
執行完成：Task-No.1
執行完成：Task-No.2
執行完成：Task-No.3
執行完成：Task-No.4
執行完成：Task-No.5
ThreadPool 退出
```