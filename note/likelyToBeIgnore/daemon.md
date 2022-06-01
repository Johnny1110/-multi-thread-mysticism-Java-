# Daemon 守護執行緒（後臺運行）

<br>

-------

<br>

Daemon Thread 會在後台默默完成一些服務，比如 GC、JIT 就是守護執行緒。與守護執行緒對應的是用戶執行緒，換句話說，假設我今天要測 3000 公尺慢跑，這就是我（用戶執行緒）要做的工作，相對應的就會有救護車在旁邊待命，救護車就是我的 Daemon Thread，他會默默的守護我，一旦我安全完成 3000 公尺任務，那麼他的守護任務也結束了。

守護執行緒要守護的對象已經不存在的話，那整個應用就應該自然結束，因此當一個 Java 應用中，只有 Daemon Thread 時，JVM 就應該自動退出。


<br>

```java
public class DaemonDemo {

    public static class DaemonT extends Thread {

        public DaemonT(boolean isDaemon){
            super.setDaemon(isDaemon);
        }

        @Override
        public void run() {
            while (true){
                System.out.println("I'm alive.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new DaemonT(true); // #1
        t.start();
        Thread.sleep(2000);
    }
}
```

__#1__ 處可以改改看變成 false，你會發線程序停下來。設置成 Daemon 的話就會跟主 Thread 一起結束退出。