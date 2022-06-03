# HashMap 也不安全

<br>

----------

<br>

HashMap 在 JDK 1.7 版本階段有一個詭異的 bug，在 JDK 1.8 之後針對 HashMap 做了大規模調整，已經不附存在了，但是這個問題還是可以在這裡拿出來討論一下：

<br>

```java
public class HashMapMultiThread {

    private static HashMap<String, String> map = new HashMap<String, String>();

    public static class AddThread implements Runnable{

        private int start = 0;

        public AddThread(int start){
            this.start = start;
        }

        @Override
        public void run() {
            for (int i = start; i < 100000; i+=2) {
                map.put(String.valueOf(i), Integer.toBinaryString(i));
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new AddThread(0));  // 新增雙數
        Thread t2 = new Thread(new AddThread(1));  // 新增單數
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(map.size());
    }
}
```

<br>

在 JDK 1.7 環境執行以上程式，會有 3 種情況發生：

1. 正常結束，`map.size()` 結果等於 100000。

2. 正常結束，`map.size()` 結果小於 100000。

3. 永遠無法結束。

<br>

前兩種結果跟 `ArrayList` 遇到的問題差不多，不多做解釋，重點是永遠無法的情形。

使用 jstack 工具可以發現，`t1` 與 `t2` 兩個 Thread 都處於 RUNNING 狀態，並且卡在執行 `put()` 方法。

以下是 JDK 1.7 的 HashMap 的 `put()` 方法部分 source code：

<br>

```java
for (Entry<K, V> e = table[i]; e != null, e = e.next()) {
    Object k;
    // 如果 key 重複就新值換舊新
    if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
        V oldValue = e.value;
        e.value = value;
        e.recordAccess(this);
        return oldValue;
    }
}
```

<br>

可以看到兩個 Thread 正在做遍歷 HashMap 內部資料，就好像在遍歷一個 LinkedList，與此同時不巧，由於多 Thread 間的衝突問題破壞了它的結構，導致這個 LinkedList 型成閉合環了。key1 與 key2 的 `next()` 方法互相指向對方。

這就是 HashMap 的死循環問題，這個問題在 JDK 1.8 中得到解決，但是在多執行緒開發時，還是不要用 HashMap，最簡單的解法就是改用 `ConcurrentHashMap`。