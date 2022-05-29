# 多實作 Runnable 少繼承 Thread

<br>

-----------

<br>

建立 Thread 的方法之一可以使用繼承 Thread 類別，@Override `run()` 方法實現。

<br>

```java
Thread t = new Thread(){
    @Override
    public void run() {
        // TOD
    }
}
```

<br>

這種方法可以達到建構 Thread 的目的，但是 Java 是一個單繼承語言，`extend` 是非常寶貴的資源，__因此我們盡量不要繼承，而是使用 `Runnable` 介面實現建構。__`Runnable` 介面如下：

<br>

```java
public interface Runnable {

    void run();

}
```

小 tips : Java 的 interface 定義的方法預設都一定是 `public abstract` 無法改變（除非是 1.8 後新增的 default 方法不一定要是 `abstract`）所以可以忽略不寫。

<br>


Thread 有一個建構方法：

```java
public Thread(Runnable target){
    this.target = target;
}
```

<br>

Thread 的 `run()` 方法默認是這樣做的：

```java
public void run(){
    if (target != null){
        target.run();
    }
}
```