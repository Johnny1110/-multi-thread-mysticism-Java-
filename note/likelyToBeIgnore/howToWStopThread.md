# 如何合理的終止 Thread 運行（不要用 `stop()`方法）

<br>

當我們製作伺服器端的執行緒時，有些時候會在 `run()` 方法中用到無窮迴圈以提供某些服務，這種執行緒不會正常因任務執行完畢而進入 TERMINATED 狀態。如何合理的終結這些執行緒就是一個問題。

以前在公司上班的時候，同事遇到過這個問題跟我討論，他提出使用 `stop()` 方法來解決。確實，Thread 提供了一個方便的 `stop()` 方法解決強制終結問題。但是從不知道哪一代 JDK 開始，這個方法已經被官方標註棄用了：

```java
@Deprecated
public final void stop() {...}
```

tips : 棄用不是不能用，而是不建議用，在 IDE 上使用棄用方法會有一個橫線出現在方法名稱上顯示。

<br>

`stop()` 被棄用的原因在於他太過於暴力，強關執行緒會造成一些資料不一致問題。簡單來解釋一下：

例如有一個共享資料 A， Thread-1 取得資料 A 的存取權後對資料 A 寫值的過程中，寫到一半突然被 `stop()`，然後這筆資料就只有寫了一半，然後被其他執行緒拿走，假設接下來這個執行緒剛好執行的是把資料 A 寫入 DB 的任務，那就出大問題了。

__不要使用 `stop()` 方法，除非真的是非常特殊情況或你真的知道你在做甚麼。

<br>
<br>

## 自訂控制閥

<br>

Thread 的停止需邀我們自己設計邏輯，如何做 ? 其實很簡單，以下舉一個例子：

<br>

我們定義一個控制閥 `stopme()`，一但決定要關閉 Thread 時，用這個控制閥 `break` 離開無限迴圈。

<br>

```java
public class ChangeObjectThread extends Thread {

    private User user = new User();
    private volatile boolean stopme = false;

    // 控制閥
    public void stopMe() {
        this.stopme = true;
    }

    @Override
    public void run() {
        while (true){

            if (stopme){
                System.out.println("exit by stopme().");
                break;
            }

            synchronized (user){
                int value = new Random().nextInt();
                user.setId(value);
                user.setName(String.valueOf(value));
            }
            Thread.yield();
        }
    }

    public static class User {
        private int id;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

```

<br>

`volatile` 代表易變的意思，提醒 JVM 這個變數是會經常變動的，叫 Thread 們不要去抓他的副本到暫存中來讀，而是去記憶體中時刻關注變數最新值。

這種方法由邏輯去判斷終結 Thread 是最合理的，不會有資料做到一半被強行終止。

<br>
<br>

## interrupt() 中斷

<br>

JDK 提供更強大的終止 Thread 的方法，就是 `interrupt()`。

關於 Thread 中斷有 3 個方法如下：

<br>

```java
public void Thread.interrupt()  // 中斷 Thread
public boolean Thread.isInterrupted() // 判斷是否被中斷
public static boolean Thread.interrupted() // 判斷是否被中斷，並清除當前中斷狀態
```

<br>

這邊要說明一件事，`interrupt()` 並不是直接中斷 Thread，那那樣的話就跟 `stop()` 一樣了，當對 Thread 使用 `interrupt()` 時，相當於下達一個 __停止通知__，確切地來說是設置 __中斷標誌__。`Thread.isInterrupted()` 方法可以判斷當前 Thread 是否被中斷（通過檢查中斷標誌），`Thread.interrupted()` 則是同樣檢查是否中斷，並清空中斷標誌。

以上可知，`interrupt()` 有點像上面的 `stopme()` 控制閥，中斷通知下達後，具體如何中斷還需要我們親自在 Thread 中做邏輯去處理。

<br>

```java
Thread t = new Thread(() -> {
    while (true) {
        if (Thread.currentThread().isInterrupted()){
            System.out.println("Current thread is interrupted");
            break;
        }
        // TOD
    }
});

t.start();
Thread.sleep(3000);
t.interrupt(); // 中斷通知
```

<br>

中斷如果只是這樣那不就跟 `stopme()` 一樣嗎 ? 其實 `interrupt()` 強大的地方在於如果在循環中出現類似 `wait()` 與 `sleep()` 操作時，就必須靠中斷解決。

`Thread.sleep()` 完整方法宣告如下：

```java
public static native void sleep(long millis) throws InterruptedException
```

<br>

`sleep()` 拋出的 `InterruptedException` 不是 `RuntimeException`，這代表我們必須 catch 他並處理。__當 Thread 在 `sleep()` 休眠時，如果被中斷，這個 Exception 就會被拋出。__

<br>

```java
Thread t = new Thread(() -> {
    while (true) {
        if (Thread.currentThread().isInterrupted()){
            System.out.println("Current thread is interrupted");
            break;
        }
                
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Interrupted when sleeping.");
            // 重點 1
            Thread.currentThread().interrupt();
        }
    }
});
```

<br>

### 重要知識點

上方 __重點 1__ 處為甚麼又中斷一次呢 ? `Thread.sleep()` 由於中斷而拋出異常，此時他會清除中斷標記，如果不針對處理的話，下一個 while 循環的 `if (isInterrupted())` 就無法捕捉到這個中斷，所以 __在異常處裡中一定要再次設定中斷。__



