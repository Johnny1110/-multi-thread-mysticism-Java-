# 並發情況下不要用 ArrayList，改用 Vector

<br>

---

<br>

`ArrayList` 不是一個 Thread Safe 容器，在多執行緒程式中使用它會導致一些隱蔽的錯誤（不會噴錯，但運算結果不正確）。例如以下範例：

<br>

```java
public class ArrayListMultiThread {

    private static ArrayList<Integer> list = new ArrayList<>(10);

    private static class AddTask implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i <1000000; ++i){
                list.add(i);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new AddTask());
        Thread t2 = new Thread(new AddTask());
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(list.size());
    }
}
```

<br>

執行以上範例，我們預期得到 `list` 長度應該是 2000000 才對，但實際上，執行出來的結果幾乎永遠小於預期值，且會出現 `ArrayIndexOutOfBoundsException` 錯誤。

<br>

如果了解 `ArrayList` 內部構造的話，你應該不難推理出為甚麼會出現這種情況，`ArrayList` 內部維護了一個 `elementData` Object 陣列，這個陣列的長度是動態改變的，當快要裝不下的時候，就會將它以原大小的次方倍數擴張，同時還維護一個 `size` 變數來記錄目前陣列大小。所以當使用 `list.add()` 方法時，不僅要更新 `elementData` 這個鎮列，同時還要跟新 `size`，同時檢查是否 `size` 要滿了，滿了的話要及時擴張。

一個 add 動作就要細分成這麼多步驟，想也知道多 Thread 不加限制的存取 `ArrayList` 會發生多可怕的衝突。`ArrayIndexOutOfBoundsException` 問題就是在 Thread-1 在擴張陣列時，Thrad-2 就直接寫進去資料造成的。這就是原子性問題造成的。

<br>

改進方法就是使用 `Vector` 取代 `ArrayList`，儘管它的效能不算優秀，但是可以最低修改成本解決問題。之後會介紹更出色的並發陣列選擇。