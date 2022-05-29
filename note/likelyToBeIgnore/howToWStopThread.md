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