# 把普通變數進化成原子化變數：__AtomicFieldUpdater__

<br>

--------------------------------

<br>

當大型系統開發在一開始設計時，針對一些變數沒有考量到 Thread Safe 問題，在後面改造時需要耗費大量經歷重新把這些變數改成 __AtomicInteger__ 等類別。但是這樣一來不僅需要耗工，還破壞了程式開發的開閉原則。所謂開閉原則就是系統對功能新增是開放態度，對功能修改是保守態度。不輕易改 code，但是可以新增功能。

<br>

__AtomicFieldUpdater__ 可以解決這種尷尬的問題，他可以在幾乎不改動原有 code 基礎上，讓普通變數也享受 CAS 操作帶來的好處。

<br>

__AtomicFieldUpdater__ 有三種：

* __AtomicIntegerFieldUpdater__

* __AtomicLongFieldUpdater__

* __AtomicReferenceFieldUpdater__

<br>

根據類別名稱，就可以知道類別分別對應 int long 和一般物件。

<br>

舉一個例子，原本有一個 __Candidate__ 物件以下稱為候選人，候選人有一個 `score` 屬性，有 10000 個選民（Thread）會隨機投票給這個候選人，每當投一票，score 就會 + 1。
在起初設計程式時，沒有考慮到 Thread Safe 問題，所以需要在不改動原本 `score` 的 int 型態同時，我們需要滿足 Thread Safe 需求，於是使用到了 __AtomicIntegerFieldUpdater__：

<br>


```java
public class AtomicIntegerUpdateDemo {

    public static class Candidate{
        int id;
        volatile int score; // 被 AtomicIntegerFieldUpdater 修飾的變數必須是 volatile
    }

    public final static AtomicIntegerFieldUpdater<Candidate> scoreUpdater =
            AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");

    public static AtomicInteger allScore = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final Candidate stu = new Candidate();
        Thread[] threads = new Thread[10000];
        for(int i = 0; i < 10000; ++i){
            threads[i] = new Thread(() -> {
                if(Math.random() > 0.4) {
                    scoreUpdater.incrementAndGet(stu);
                    allScore.incrementAndGet(); // 同時與 Candidate 累加，後面驗證正確性用。
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < 10000; i++) {
            threads[i].join();
        }
        System.out.println("score: " + stu.score);
        System.out.println("Allscore: " + allScore);
    }
}
```

<br>

印出結果：

<br>

```
score: 6004
Allscore: 6004
```

<br>
<br>
<br>
<br>

__！！！__ __AtomicFieldUpdater__ 有 3 個使用上需要注意點：

<br>

1. __Updater__ 們只能修改可見範圍的變數，他使用的是反射原理，如果變數不可見（如 `private`），就沒辦法 work 了。

2. 為了保證變數被正確讀出，變數必須被宣告為 `volatile`。如果原本的類別沒有宣告為 `volatile`，加上去就好了，並不會有甚麼問題。

3. 由於 __CAS__ 操作會通過物件內屬性的相對偏移量進行賦值，說了是 __物件__ 因此他不支持類別屬的 `static` 變數（`Unsafe.objectFieldOffset()` 不支持靜態變數）。