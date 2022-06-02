# Thread Safe 與 synchronized 概念

<br>

-------

<br>

多執行緒開發最大重點就是 Thread Safe，一般來說多執行緒是為了獲得更高的執行效率。但是高效能不能以犧牲正確性為代價，如果多執行緒化後連基本的正確性都無法保證那就毫無意義了。

前面有關 `volatile` 的章節提到，`volatile` 只能保證一個 Thread 修改資料後，其他 Thread 可以看到這個改動，當兩個 Thread 同時修改某個資料時，依然會產生衝突。

以下是展示一個 counter，兩個 Thread 同時對 `i` 做累加操作，各執行 1000000 次，當然我們期待最終結果 `i` 應該等於 2000000，但執行以下程式，`i` 值永遠小於 2000000。這就是因為兩個 Thread 同時對 `i` 做寫入 ，其中一個 Thread 會覆蓋領一個（盡管 `i` 被宣告為 `volatile`）。

<br>

```java
public class AccountingVol implements Runnable {

    private static AccountingVol instance= new AccountingVol();
    private static volatile int i = 0;

    public static void increase() {
        i++;
    }

    @Override
    public void run() {
        for (int j = 0; j < 1000000; j++){
            increase();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(instance);
        Thread t2 = new Thread(instance);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("i = " + i);
    }
}
```

<br>

執行以上程式你會發現 `i` 值永遠小於 2000000，這就是衝突狀況。t1 與 t2 同時讀取 `i` 值為 0，它們各自計算得到 `i = 1`，並寫回到 `i`。因此 `i++` 即使被執行 2 次，但實際上 i 值只增加 1。

<br>

圖示如下：

<br>

![6](../IMGS/6.JPG)

<br>

讀操作因為 `volatile` 原因都是讀到一樣的，但寫操作沒有鎖保護，所以容易出現這種寫入衝突狀況。

<br>

要解決這個問題，我們就必須保證多個 Thread 在對 `i` 進行操作時完全同步，當 Thread-1 在寫入時，Thread-2 不僅不能寫，同時不能讀。因為 Thread-1 寫完前，Thread-2 讀取到的一定是個過期資料。`synchronized` 就是用來解決這個問題的存在。

<br>

`synchronized` 可以用很多種用法，以下整理三種：

<br>

* 指定加鎖對象：對給定 Object 加鎖，進入同步區塊代碼前須要獲得給定 Object 的鎖。

* 直接作用於物件方法：相當於對當前 Object 上鎖，進入同步區塊代碼前要獲得當前 Object 鎖。

* 直接作用於靜態方法：相當於對當前 class 上鎖，進入同步區塊代碼前要獲得當前 class 鎖。

<br><br>

### 範例一 `synchronized` 作用於指定加鎖對象

<br>

```java
public class AccountingSync implements Runnable {

    private static AccountingSync instance = new AccountingSync();
    private static int i = 0;

    public static void increase() {
        i++;
    }

    @Override
    public void run() {
        for (int j = 0; j<1000000; j++) {
            synchronized (instance){
                increase();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(instance);
        Thread t2 = new Thread(instance);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("i = " + i);
    }
}
```

<br>

上面一段代碼改寫了有問題的 AccountingVol，對指定 Object 進行加鎖。

<br>
<br>

### 範例二 `synchronized` 作用於物件方法

<br>

```java
public class AccountingSync2 implements Runnable {

    private static AccountingSync2 instance = new AccountingSync2();
    static int i = 0;

    public synchronized void increase() {
        i++;
    }

    @Override
    public void run() {
        for (int j = 0; j < 1000000; j++){
            increase();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(instance);
        Thread t2 = new Thread(instance);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("i = " + i);
    }
}
```

<br>

範例二把 `synchronized` 加在了 `increase()` 方法上。意思是進入 `increase()` 方法前，Thread 必須獲得當前 Object 鎖，也就是 `instance`。__注意 main 方法中，建立 Thread 使用的是同一個 `Runnable` 介面的實作物件（`instance` 物件），這就保證了兩個 Thread 工作時，能夠關注到同一個物件鎖，從而保證 Thread Safe。__

<br>
<br>

### 範例三 `synchronized` 作用於類別靜態方法

<br>

```java
public static synchronized void increase() {
    i++;
}
```

<br>

把 `increase()` 變成類別靜態方法，再給他加上 `synchronized`，這樣一來，要進入 `increase()` 方法就需要取得 __類別鎖__。也就不需要讓兩個 Thread 指向相同的 instance 了，因為這次兩個 Thread 要爭搶的是類別了，而不是實例化的物件。