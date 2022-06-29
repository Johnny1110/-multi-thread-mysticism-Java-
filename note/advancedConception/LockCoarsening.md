# 注意！鎖粗化（大量分散上鎖會造成浪費），能一起上鎖就一起上鎖

<br>

---

<br>

如果對同一個鎖不停請求同步和釋放，其本身會消耗大量系統資源，非常不利於性能優化。

JVM 在遇到一連串連續對同一個鎖請求與釋放操作時，會把所有鎖操作整合成對鎖的一次請求，為的就是減少對鎖的請求同步次數，這就叫做 __鎖粗化__。

比如：

<br>

```java
public void demoMethod() {
    synchronized(lock) {
        // do sth.1
    }
    // do sth.2 (don't needs synchronized)
    synchronized(lock) {
        // do sth.3
    }
}
```

JVM 自動優化：

```java
public void demoMethod() {
    synchronized(lock) {
        // do sth.1
        // do sth.2 (don't needs synchronized)
        // do sth.3
    }
}
```

<br>
<br>

下面展示一個在循環內請求鎖的範例，在這種情況下意味著每次循環都有申請鎖與釋放鎖的動作。

<br>

```java
for (int i = 1; i < 100; i++) {
    synchronized(lock) {
        // do sth.
    }
}
```

合理修正：

```java
synchronized(lock) {
    for (int i = 1; i < 100; i++) {
        // do sth.
    }
}
```

<br>

鎖粗化與減少鎖持有時間的思想是相悖的，但目的都是優化性能，二者在不同情境下效果不同，可以根據實際情境靈活運用。