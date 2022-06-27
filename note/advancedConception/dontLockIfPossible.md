# 減小鎖持有時間，能不上鎖就不上鎖

<br>

-------

<br>

下面有一段 code：

<br>

```java
public synchronized void syncMethod() {
    func1();
    mutexFunc();  // 需要同步化
    func2();
} 
```

<br>
 
`syncMethod()` 方法中只有 `mutexFunc()` 需要上鎖，但是我們把 `func1()` 與 `func2()` 都鎖住了，這樣一來，當大量 Thread 執行此段程式時，就鎖住了不必要的區塊，造成不必要的時間浪費。

改進方法：

<br>

```java
public void syncMethod() {
    func1();
    synchronized(this) {
        mutexFunc();  // 需要同步化
    }
    func2();
} 
```

<br>

