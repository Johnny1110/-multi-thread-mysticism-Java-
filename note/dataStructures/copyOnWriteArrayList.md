# 高效讀取陣列：不變模式下的　__CopyOnWriteArrayList__

<br>

---

<br>

在某些情境下，讀操作次數可能會遠遠大於寫操作。比如系統級別信息，往往只需要修改很少次數，但是會被系統內所有模組頻繁訪問。我們希望讀操作可以盡可能快，而寫操作慢一些也沒關西。

讀操作不會修改原本資料，因此對於讀與讀操作間都進行上鎖是一種浪費行為，所以我們應該允許多 Thread 同時訪問 __List__。但是讀與寫操作同時發生時就必須上鎖，否則可能會讀到不一致的資料。

JDK 提供了 __CopyOnWriteArrayList__ 工具類，對他來說，讀與讀完全不用上鎖，更猛的是，__寫也不會阻塞讀 !__，只有寫入與寫入之間需要進行同步化處理（上鎖）。性能直接飛越式提升。

<br>

CopyOnWrite 就是在寫操作時，進行一次備份。當這個 __List__ 需要修改時，並不真的立即修改原本內容，而是對原本資料進行 Copy，將修改的內容寫入這份 Copy 中，寫完之後再將修改好的副本替換真正的正本，這樣就保證寫操作不會影響讀（替換動作就一步，符合原子性）。

<br>

__CopyOnWriteArrayList__ 的讀操作 `get()` 這邊就不展開 source code 了，只要知道 `get()` 全程都沒有任何鎖。我們主要展開看一下 `add()` 方法。

`add()` 方法 source code：

<br>

```java
private volatile transient Object[] array;

public boolean add(E e) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try{
        Object[] elements = getArray(); // 取出正本 array
        int len = elements.length;
        Object[] copyElements = Arrays.copyOf(elements, len + 1); // 副本長度為正本長度 + 1
        copyElements[len] = e;
        setArray(copyElements); // 一步到位
        return true;
    } finally {
        lock.unlock();
    }
}
```

<br>

這個鎖僅控制寫與寫操作，修改完成後，執行讀取任務的 Thread 可以馬上察覺這個修改，因為 `array` 變數是宣告成 `volatile` 的。 