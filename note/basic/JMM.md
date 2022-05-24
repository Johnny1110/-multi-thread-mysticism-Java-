# Java内存模型（Java Memory Model，__JMM__）-> 原子性( Atomicity )、可見性( Visibility )、有序性( Ordering )

<br>

--------------

<br>

JMM 保證 Thread 間可以有效正確協作，JMM 關鍵點都是圍繞 原子性( Atomicity )、可見性( Visibility )、有序性( Ordering ) 建立的，下面一一介紹這些概念。

<br>
<br>

## 原子性( Atomicity )

<br>

原子性指一個操作是不可中斷的，多 Thread 一起執行時，一個操作一旦開始就不會被其他人干擾。

舉例來說有一個變數 `static int i = 0`，有兩個 Thread 對他進行修改，Thread-1 修改為 `i=1` Thread-2 修改為 `i=2`，那麼 `i` 必定不是 1 就是 2，這就是原子性。或許看到這邊你會想說 " 阿不就是 1 或 2 嗎 ? 還會是甚麼 ? 如果今天 `int` 換成 `long` 型態就會出問題。

對於 32 位元系統來說 `long` 型態資料讀寫就不是原子性 ( 因為 `long` 有 64 位元 )，如果多個 Thread 對它進行寫操作就會發生干擾。

如果有 4 個 Thread 對 `long t` 進行修改，分別修改為 `111`、`-999`、`333`、`444`，然後用一個 Thread 執行讀 `t` 的任務，在 32 位元的系統中實際讀到的 `t` 值可能會亂七八糟，像這樣：

<br>

code : 



```java
package com.frizo.lab.thread.mysticism.aboutJMM;

public class MultiThreadLong {

    public static long t = 0;

    // 寫任務
    public static class ChangeT implements Runnable {

        private long to;

        public ChangeT(long to){
            this.to = to;
        }

        @Override
        public void run() {
            while (true) {
                MultiThreadLong.t = to;
                Thread.yield();
            }
        }
    }

    // 讀任務
    public static class ReadT implements Runnable {

        @Override
        public void run() {
            while (true) {
                long temp = MultiThreadLong.t;
                if (temp != 111L && temp != -999L && temp != 333L && temp != -444L) {
                    System.out.println("abnormal value: " + temp);
                }
                Thread.yield();
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new ChangeT(111L)).start();
        new Thread(new ChangeT(-999L)).start();
        new Thread(new ChangeT(333L)).start();
        new Thread(new ChangeT(-444L)).start();

        new Thread(new ReadT()).start();
    }

}

```

<br>

output :

<br>

```
abnormal value: -4294966963
abnormal value: 4294966852
abnormal value: -4294966963
...
```

<br>

解釋一下，首先我們先把 `111`、`-999`、`333`、`444` 轉換成 64 位元大的 2 進制來表示（`long` 型態佔 64 位）：

```
111          = 0000000000000000000000000000000000000000000000000000000001101111
-999         = 1111111111111111111111111111111111111111111111111111110000011001
333          = 0000000000000000000000000000000000000000000000000000000101001101
-444         = 1111111111111111111111111111111111111111111111111111111001000100
4294966852L  = 0000000000000000000000000000000011111111111111111111111001000100
-4294966963L = 1111111111111111111111111111111100000000000000000000000101001101
```

<br>
<br>
<br>
<br>

## 可見性( Visibility )

<br>

<br>
<br>
<br>
<br>

## 有序性( Ordering )

<br>