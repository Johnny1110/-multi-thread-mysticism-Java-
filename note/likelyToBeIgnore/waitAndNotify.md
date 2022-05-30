# `wait()` 與 `notify()` 怎麼用 ?

<br>

-------

<br>

JDK 為多 Thread 協作提供了兩個重要方法 `wait()` 與 `notify()`。__這兩個方法並不在 Thread 類別中，而是在 Object 類別。__ 這代表任何物件都可調用這兩個方法。

<br>

```java
public final void wait() throws InterruptedException

public final native void notify();
```

<br>

__當一個物件調用 wait() 後，當前 Thread 就會在這個物件上等待。__ 舉例來說，Thread-1 調用了 `objectA.wait()`，那 Thread-1 就停止執行，變成 WAITING 狀態，直到有其他 Thread 調用 `objectA.notify()`，Thread-1 才會恢復 RUNNING 狀態。

<br>

`wait()` 與 `notify()` 的運作機制有必要了解一下。如果一個 Thread 調用了 `object.wait()` 之後，這個 Thread 就會進入這個 object 的 __等待隊列__，這個等待隊列中可以排多個 Thread。當 `object.notify()` 被調用時，會從這個 object 的等待隊列中隨機抓一個 Thread 喚醒，這個選擇是不公平的，並不是先等待的先喚醒。

除了 `notify()` 外，還有 `notifyAll()` 他跟 `notify()` 的差別在於他直接喚醒所有在等待的 Thread 們。

<br>

`Object.wait()` 方法並不是可以隨意用，他必須被包在 `synchronzied()` 語法使用。Thread 無論要調用 `wait()` 或 `notify()` 都必須用 `synchronzied()` 獲得目標物件取用權。下面用 2 個 Thread 來示範一下這個過程：

<br>

步驟|Thread-1| Thread-2| 
:-----: | :-----: | :-----: |
1|取得 object 取用權    | - |
2|調用 `object.wait()`    | - |
3|釋放 object 取用權    | - |  
4|-| 取得 object 取用權 |  
5|-| 調用 `object.notify()` |  
6|等待 object 取用權 | 釋放 object 取用權 |  
7|取得 object 取用權    | - |
8|繼續執行   | - |

<br>

Thread-2 在調用 `notify()` 前需要持有 object 取用權，Thread-1 釋放了 object 所以 Thread-2 可以成功持有它。Thread-2 緊接著隨機喚醒一個 Thread，此時 object 等待隊列中只有 Thread-1，所以 Thread-1 被喚醒，__喚醒後的 Thread-1 並不是繼續執行，而是嘗試重新獲得 object 使用權。__ 如果不能取得，那 Thread-1 還是要繼續等待直到取得為止才能繼續執行任務。

<br>
<br>

下面展示一段 `wait()` 與 `notify()` 使用範例：

<br>

```java
public class WaitNotifyDemo {

    final static Object object = new Object();

    public static class Thread1 extends Thread {
        @Override
        public void run(){
            synchronized (object){
                System.out.println(System.currentTimeMillis() + ": Thread-1 start.");
                try {
                    System.out.println(System.currentTimeMillis() + ": Thread-1 wait for object.");
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis()+ ": Thread-1 end.");
            }
        }
    }

    public static class Thread2 extends Thread {
        @Override
        public void run(){
            synchronized (object){
                System.out.println(System.currentTimeMillis() + ": Thread-2 start.");
                System.out.println(System.currentTimeMillis() + ": Thread-2 notify a random Thread.");
                object.notify();
                System.out.println(System.currentTimeMillis()+ ": Thread-2 waiting 2 sec to release object.");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis() + ": Thread-2 end.");
            }
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread1();
        Thread t2 = new Thread2();
        t1.start();
        t2.start();
    }

}
```

<br>

執行結過：

<br>

```
1653884712668: Thread-1 start.
1653884712668: Thread-1 wait for object.
1653884712668: Thread-2 start.
1653884712668: Thread-2 notify a random Thread.
1653884712668: Thread-2 waiting 2 sec to release object.
1653884714678: Thread-2 end.
1653884714678: Thread-1 end.
```

<br>

透過時間戳記可以看到，T1 被喚醒後並沒有繼續執行，而是等到重新持有 object 鎖時才繼續。

<br>

__注意 ! `Object.wait(`) 與 `Thread.sleep()` 方法都可以讓 Thread 等待，區別除了 `wait()` 可以被喚醒外，還有就是 `wait()` 會主動釋放目標物件鎖，而 `sleep()` 不會釋放任何資源。__