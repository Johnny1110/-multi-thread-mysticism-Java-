# Java Pointer：__Unsafe__


<br>

---

<br>

__sun.misc.Unsafe__ 類別封裝了一些不安全的操作，裡面大多都是指針操作，這也是 Java 極力避免 Java 使用者去觸碰的點。如果指針只錯位置，或計算指針偏移量出錯，結果會很慘，可能會覆蓋別人的內存，嚴重的話直接系統崩潰。

<br>

看一下 __Unsafe__ 類的一個方法：`compareAndSwapInt()`，他是一個原生方法（`native`）。

<br>

```
public final native boolean compareAndSwapInt(Object o, long offset, int expected, int x);
```

<br>

第一個參數 `o` 為給定的物件，`offset` 是物件內的偏移量（其實就是一個字段到物件頭部的偏移量，通過這個偏移量可以快速定位字段），`expected` 表示期望值，`x` 表示要設定的新值，如果指定的字段值等於期望值，就把他設定為 `x`。

<br>

偏移量 `offset` 我們直接用一個例子說明：

<br>

```java
public static void main(String[] args) throws NoSuchFieldException {
        Student s = new Student("Johnny", 24);
        long offset = Unsafe.getUnsafe().objectFieldOffset(UnsafeDemo.Student.class.getDeclaredField("age"));
        int age = Unsafe.getUnsafe().getInt(s, offset);
        System.out.println(age);
    }
```

<br>

一個物件內部有許多 __Field__，就拿 __Student__物件來說，記憶體中的位置關係如下：

<br>

```
Student_Head --- name --- age
      0            1       2
```

<br>

 `age` 在記憶體中位置距離物件頭部的距離就是偏移量，在這個範例中就是 偏移 2 個單位，透過偏移量，我們可以快速在物件中定位我們要修改的值。C++ 中常用這個偏移量概念。

<br>

可以看出 `compareAndSwapInt()` 是完全按照 CAS 機制來設計的。

此外 __Unsafe__ 類還提供一些方法，主要有以下幾種：

<br>

```java
// 取得給定偏移量上的 int 值。
public native int getInt(Object o, long offset);
// 設置給定物件偏移量上的 int 值。
public native void putInt(Object o, long offset, int x);
// 獲得字段在物件中的偏移量。
public native long objectFieldOffset(Field f);
// 設置給定物件偏移量上的 int 值，使用 volatile。
public native void putIntVolatile(Object o, long offset, int x);
// 和 putIntVolatile() 一樣，但是他要球被操作的字段就是 volatile。
public native void putOrderedInt(Object o, long offset, int x);
```

<br>

講了這麼多，JDK 開發人員還是不允許我們使用指針，所以以上方法我們根本不能用。如果我們嘗試取的 __Unsafe__ 物件就會噴錯：

<br>

```
Exception in thread "main" java.lang.SecurityException: Unsafe
	at sun.misc.Unsafe.getUnsafe(Unsafe.java:90)
	at com.firzo.mysticism.nonLock.UnsafeDemo.main(UnsafeDemo.java:11)
```

<br>

看一下取得 __Unsafe__ 的靜態工廠方法：

<br>

```java
@CallerSensitive
public static Unsafe getUnsafe() {
    Class<?> caller = Reflection.getCallerClass();
    if (!VM.isSystemDomainLoader(caller.getClassLoader()))
        throw new SecurityException("Unsafe");
    return theUnsafe;
}
```

<br>

他會檢查調用 `getUnsafe()` 方法的類，如果這個類的 ClassLoader 不是 null，就拋出異常。因次這使得我們自己的應用無法直接使用 Unsafe 類，他是 JDK 內部專屬類。

<br>

---

<br>

__Tips__：

根據 Java 類加載器原理，應用程式的 class 由 App Loader 加載（AppLoader 由 Java 編寫而成），系統核心類，如 rt.jar 中的內由 Bootstrap 類加載器加載。Bootstrap 類加載器是由 C/C++ 編寫而成，所以試圖在 Java 中取得這個 Bootstrap 類會返回 null，因為他根本就不是 Java 能辨別的東西。

換句話說，如果一個類的類加載器是 null，那表明他是由 Bootstrap 加載的，而這個類極有可能是 rt.jar 中的類。

<br>

---
