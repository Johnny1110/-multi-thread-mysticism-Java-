# 減小鎖粒度

<br>

---

<br>

減小鎖粒度的定義是：縮小上鎖目標範圍，從而減少鎖衝突可能性，進而提高系統多執行緒能力。

<br>

減小鎖粒度技術，套用於 __ConcurrentHashMap__ 這個類別中。

對於 __ConcurrentHashMap__ 而言，最重要的兩個方法就是 `get()` 與 `put()`。多執行緒情境下，我們首先想到的就是要對整個 `HashMap` 上鎖，當然，如此一來可以得到一個 Thread Safe 的 `HashMap`。但是這樣 __鎖粒度太大了__。對於 __ConcurrentHashMap__ 而言，他內部實現維護了 16 個 __HashMap__，稱之為 __段（SEGMENT）__。

如果我們要在 __ConcurrentHashMap__ 中新增一個新的值，不用將整個 __ConcurrentHashMap__ 上鎖，先根據 `hashcode` 得知該值應該被存到哪一個 SEGMENT 中，然後對該 SEGMENT 上鎖，並執行 `put()` 方法。在多 Thread 環境下，只要被加入的新值不存放在同一個 SEGMENT 中，那最多可以同時允許 16 個 Thread 同時執行（因為最多有 16 個 SEGMENT）。

<br>

看一下 __ConcurrentHashMap__ 的 `put()` 方法（JDK1.7）：

<br>

```java
public V put(K key, V value) {
        Segment<K, V> s;
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key); // 根據 key 取得 hash
        int segmentCode = (hash >>> segmentShift) & segmentMask;  // 根據 hash 取得段序號
        if ((s = (Segment<K, V>)UNSAFE.getObject(segment, (segmentCode << SSHIFT) + SBASE)) == null) {
            s = ensureSegment(segmentCode); //取得段
        }
        return s.put(key, hash, value, false); // 資料寫入段
    }
```

<br>

像這樣減少鎖粒度會引出新問題，就是當我們需要全局上鎖時，必須持有 16 個 SEGMENT 鎖。

比如我們 __ConcurrentHashMap__ 的 `size()` 方法，他需要統計 16 個 SEGMENT 的總數量，因此他就需要獲取這個 __ConcurrentHashMap__ 所有 SEGMENT 的鎖資源。

`size()` 方法的部分內容如下：

<br>

```java
sum = 0;
for (int i = 0; i < segments.length; ++i) {
    segments[i].lock(); // 上鎖
}
for (int i = 0; i < segments.length; ++i) {
    sum += segments[i].count; // 統計
}
for (int i = 0; i < segments.length; ++i) {
    segments[i].unlock(); // 解鎖
}
```

<br>

`size()` 方法事實上並不總是這樣執行，他會先以無鎖的方式求和，失敗了才會嘗試加鎖方法求和。

只有在類似 `size()` 這種需要調用全局鎖方法 __使用不頻繁__ 時，這種減小鎖粒度的方法才能真正意義上提高系統吞吐量。


