# Thread 的優先級，權貴與庶民

<br>

-------

<br>

Java 裡也可以對 Thread 進行優先級劃分，優先級高的 Thread 在資源競爭上傭有更大的優勢，更高機率搶占資源（還是概率問題）。運氣不好的話高優先級的 Thread 一樣會產生飢餓問題。

優先級調度跟系統底層密切相關，在不同 OS 表現不同，這種優先級產生的後果無法預測，無法精確控制，因此在嚴謹的專案上，還是要在應用層解決 Thread 調度問題。

<br>

在 Java 中，使用 1-10 表示 Thread 優先級，一班可以使用預設的 3 個靜態標量表示：

<br>

```java
public final static int MIN_PRIORITY = 1;
public final static int NORM_PRIORITY = 5;
public final static int MAX_PRIORITY = 10;
```

<br>

數字範圍越大優先級越大，有效值在 1-10 之間，下面展示一個範例，高優先級 Thread 傾向更快完成。

<br>

```java
public class PriorityDemo {

    public static class CountTask implements Runnable {

        private static int count = 0;

        @Override
        public void run() {
            while (true){
                synchronized (PriorityDemo.class){
                    count++;
                    if(count > 1000000){
                        System.out.println("Name: " + Thread.currentThread().getName() +
                                " PriorityLevel: " + Thread.currentThread().getPriority() + " done job.");
                        break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread hightPriorityThread = new Thread(new CountTask(), "HIghtPriority");
        Thread lowPriorityThread = new Thread(new CountTask(),"lowPriority");
        hightPriorityThread.setPriority(Thread.MAX_PRIORITY);
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);
        lowPriorityThread.start();
        hightPriorityThread.start();
    }
}
```

<br>

執行結果：

<br>

```
Name: HIghtPriority PriorityLevel: 10 done job.
Name: lowPriority PriorityLevel: 1 done job.
```

<br>

多試己幾次基本上也是高優先級的 Thread 先完成任務，但這並不能代表總是這樣，可能某些情況下就會反過來了，只是機率很小而已。