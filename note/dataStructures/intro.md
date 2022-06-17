# 好用的工具：多執行緒集合類別簡介

<br>

---

<br>

JDK 提供了很多好用的容器在 java.util.concurrent 裡面。這邊先介紹一下他們的作用，具體實現會在後面小節做介紹。

<br>

<br>

### __ConcurrentHashMap__

<br>

這是一個高效的多執行緒 __HashMap__，可以理解為 Thread Safe 的 __HashMap__。

<br>

### __CopyOnWriteArrayList__

<br>

這是一個 __List__，從名字上看就是 __ArrayList__ 一族。在 __讀多寫少__ 的場合，這個 __List__ 性能非常好，遠遠大於 __Vector__。

<br>

### __ConcurrentLinkedQueue__ 

<br>

高效的多執行緒隊列，使用鍊表實現。可以把它看作 Thread Safe 的 __LinkedList__。

<br>

### __BlockingQueue__ 

<br>

這是一個 interface，JDK 內部透過鍊表，陣列等方式實現這個 interface。表示阻塞隊列，非常適合用於作為多執行緒間資料共享通道。

<br>

### __ConcurrentSkipListMap__ 

<br>

SkipList 實現，這是一個 __Map__，使用 SkipList 的資料結構進行快速查找。

<br>
<br>

__Vector__ 是 Thread Safe 的，但是性能跟上述這些專用工具完全比不了。另外，__Collections__ 工具類可以把任意集合包裝成 Thread Safe 的版本。

<br>

```java
List<Integer> aList = new ArrayList<>();
List<Integer> threadSafeList = Collections.synchronizedList(aList);
```
