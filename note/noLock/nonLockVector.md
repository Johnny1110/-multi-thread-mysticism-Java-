# 實作練習：無鎖 __Vector__ 實現挑戰

<br>

---

<br>

無鎖的好處：

1. 高併發情形下，他比有所有著更好的性能。

2. 天生鎖問題免疫，不用考慮死鎖。

<br>

這裡實現的無鎖版本 __Vector__ 稱作 __LockFreeVector__，他可以根據需求動態擴展內部空間，我們使用二維陣列表示其內部儲存。

<br>

```java
private final AtomicReferenceArray<AtomicReferenceArray<E>> buckets;
```