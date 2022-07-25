# 無鎖的物件引用：__AtomicReference__

<br>

-------

<br>

## __AtomicReference__

<br>

__AtomicReference__ 可以保證你在修改物件引用時的 Thread Safe。

前面章節 [CAS 原子性操作的不足](CASProblem.md) 提到了一些問題，這裡用 __AtomicReference__ 物件來示範一下這個問題。

<br>

有一家店辦活動，如果貴賓卡里餘額小於 20 元就送 20 元，每一個課戶只會被贈送一次。

<br>

```java
public class AtomicReferenceDemo {

    private static AtomicReference<Integer> money = new AtomicReference<>();

    static {
        money.set(19);
    }

    public static class AddMoneyTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                Integer gash = money.get();
                if (gash < 20) {
                    if (money.compareAndSet(gash, gash + 20)) {
                        System.out.println("餘額：" + gash + " 贈送 20 元，加值後餘額：" + money.get());
                    }
                }
            }

        }
    }

    public static class CostMoneyTask implements Runnable {

        @Override
        public void run() {
            for (int i = 1; i < 100; ++i){
                while (true){
                    Integer m = money.get();
                    if (m > 10) {
                        System.out.println("大於 10 元");
                        if (money.compareAndSet(m, m-10)){
                            System.out.println("成功消費 10 元，餘額：" + money.get());
                            break;
                        }
                    }else{
                        System.out.println("餘額不足");
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    public static void main(String[] args) {
        Thread[] threads = new Thread[3];
        Runnable addTask = new AddMoneyTask();
        Runnable costTask = new CostMoneyTask();
        for (int i = 0; i < threads.length; i++) {
            new Thread(addTask).start();
        }
        new Thread(costTask).start();
    }
}
```

<br>

__AddMoneyTask__ 判斷餘額並贈送金額，如果已經被其他 Thread 處理，那當前 Thread 就會失敗，並繼續不斷檢查餘額。

如果不幸 __CostMoneyTask__ 正好在消費。在贈與金額到帳同時，進行一次消費，使總金額又小於 20，且正好共消費 20 元，使消費與贈與後金額相等於消費贈與前金額。負責加值的 Thread 就誤以為這個帳戶沒有贈與，所以會有多次贈與發生。

<br>

印出結果：

<br>

```
餘額：19 贈送 20 元，加值後餘額：39
大於 10 元
成功消費 10 元，餘額：29
大於 10 元
成功消費 10 元，餘額：39
大於 10 元
餘額：19 贈送 20 元，加值後餘額：39
成功消費 10 元，餘額：29
大於 10 元
成功消費 10 元，餘額：39
大於 10 元
餘額：19 贈送 20 元，加值後餘額：39
成功消費 10 元，餘額：29
大於 10 元
成功消費 10 元，餘額：39
...
```

<br>

這個範例比較極端一點，因為這個情況出現概率不大，但是還是會有可能出現。因此還是需要正視這個問題，JDK 提供了 __AtomicStampedReference__ 解決這個問題。除了比對期望值與實際值外，還要再額外比對時間戳記。

<br>
<br>
<br>
<br>

## __AtomicStampedReference__

<br>

__AtomicStampedReference__ 內部不僅維護了物件值，還維護了一個時間戳。當 __AtomicStampedReference__ 對應的數值被修改時，除了更新資料本身，還要更新時間戳。當 __AtomicStampedReference__ 設置物件值時，物件以及時間戳都必須滿足期望值才可以寫入。

因此，即使物件值被反覆讀寫，寫回原值，只要時間戳發生變化，就可以防止 CAS 誤判。

<br>

__AtomicStampedReference__ 的關於時間戳的 API：

<br>

```java
public boolean compareAndSet(V expectedReference, V newReference, int expectedStamp, int newStamp)

public V getReference()

public int getStamp()

public void set(V newReference, V expectedReference, int newStamp)
```

