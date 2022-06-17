# 解析高效的讀寫隊列：__ConcurrentLinkedQueue__

<br>

---

<br>

JDK 中提供一個 __ConcurrentLinkedQueue__ 類用來實現多執行緒隊列。這個隊列使用鍊表作為其資料結構。事實上，__ConcurrentLinkedQueue__ 應該算是在多執行緒環境下，性能最好的隊列，他之所以能有出色的性能，是因為其內部複雜的實現。

這裡會來解析一下這個複雜的 __ConcurrentLinkedQueue__ 是如何實現的。

<br>

作為一個鍊表（__LinkedList__），當然會有鍊表內的節點（__Node__）。在 __ConcurrentLinkedQueue__ 中，定義的節點如下：

<br>

```java
private static class Node<E> {
    volatile E item;
    volatile Node<E> next;
}
```

<br>

`item` 是用來表示目標元素，比如說，當隊列表中存放 __String__ 時，`item` 就是 __String__ 類。`next` 表示當前 __Node__ 的下一個元素，這樣一來每個 __Node__ 就能環環相扣。

<br>

## __CAS__ 操作

<br>

對 __Node__ 進行操作時，使用 __CAS__ 操作。關於 __CAS__ 到後面鎖優化會詳細說明，這邊稍微簡單的說明一下甚麼是 __CAS__。

<br>

所謂 __CAS__ 就是指 Compare And Swap，使用 __CAS__ 其實就是不使用鎖，所有資源全開任意存取。但是這種肆意存取情況下肯定會出現衝突問題，__CAS__ 避免這個問題的方法是這樣做的：

__CAS__　有三個參數（V, E, N），V 表示要更新的變數，E 表示預期值，N 表示新值。只有當 V 等於 E 時，才會將 V 的值變成 N。如果 V 與 E 不同，則說明已經有其他 Thread 做了更新，最後 __CAS__ 返回當前 V 的真實值。

__CAS__ 操作是抱著樂觀態度進行的，他總是認為自己可以成功完成操作。當多 Thread 操作一個變數時，只會有一個會勝出，並成功更新，其餘均會失敗。失敗的 Thread 被告知失敗後，允許再次嘗試，當然也允許失敗的 Thread 放棄操作。基於這樣的原理，__CAS__ 即使沒有鎖，也可以發現其他 Thread 對當前 Thread 的干擾，並洽當處理。

<br>

## Node

<br>

對 __Node__ 進行操作時，使用 __CAS__ 操作如下：

<br>

```java
boolean castItem(E cmp, E val) {
    return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
}

void lazySetNext(Node<E> val) {
    UNSAFE.putOrderedObject(this, nextOffset, val);
}

boolean casNext(Node<E> cmp, Node<E> val) {
    return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
}
```

<br>

`castItem()` 與 `casNext()` 方法是採用一樣的 __CAS__ 方法，一個是設置當前 __Node__，另一個是設置 `next` 的 __Node__。他們都需要 2 個參數 `cmp`（期望值） 與 `val`（設置目標值），當目前值（`this`）等於 `cmp` 時，就會將目標設置為 `val`。