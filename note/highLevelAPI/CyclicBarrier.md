# 循環柵欄：CyclicBarrier

<br>

-------

<br>

CyclicBarrier 與 CountDownLatch 很像，他也可以實現 Thread 之間的計數等待，但功能更加強大。

想像一下，士兵作戰時，一個一個上就是送死，最好在前線集結後再一起衝鋒陷陣。二戰時期的俄國士兵就是一批一批的送上火車趕往前線抵禦德國，但是沒有等待大集結就上了前線的後果就是一直節節敗退。

以下示範一個 CyclicBarrier 使用情境，每 10 個士兵完成集結後，直接出發執行任務。

CyclicBarrier 可以接收一個參數作為 barrierAction，barrierAction 就是當計數器一次完成計數後，系統會執行的動作。如下，其中 parties 表示計數總數，也就是參與的 Thread 總數。

<br>

```java
public CyclicBarrier(int parties, Runnable barrierAction)
```

<br>

```java
public class CyclicBarrierDemo {

    public static class Soldier implements Runnable{
        private String soldierName;
        private final CyclicBarrier barrier;

        public Soldier(CyclicBarrier barrier, String soldierName){
            this.barrier = barrier;
            this.soldierName = soldierName;
        }

        @Override
        public void run() {
            try {
                // wait for assemble.
                barrier.await();
                doWork();
                // wait for soldier finish work.
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        private void doWork() {
            try {
                Thread.sleep(Math.abs(new Random().nextInt() % 10000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(this.soldierName + " : mission complete.");
        }
    }

    public static class BarrierRun implements Runnable {
        boolean flag;
        int N;

        public BarrierRun(boolean flag, int N){
            this.flag = flag;
            this.N = N;
        }

        @Override
        public void run() {
            if (flag) {
                System.out.println("Commander: [Soldier " + N + ", mission complete.]");
            } else {
                System.out.println("Commander: [Soldier " + N + ", assemble complete.]");
                flag = true;
            }
        }
    }

    public static void main(String[] args) {
        final int N = 10;
        Thread[] allSoldier = new Thread[N];
        boolean flag = false;
        CyclicBarrier barrier = new CyclicBarrier(N, new BarrierRun(flag, N)); // #1
        System.out.println("Commander: [assemble army!]");
        for (int i = 0; i < N; ++i){
            System.out.println("Soldier " + i + ": yeah sir.");
            allSoldier[i] = new Thread(new Soldier(barrier, "Soldier " + i));
            allSoldier[i].start();
        }
    }
}
```

#1 處建立了 CyclicBarrier，並將計數器設定為 10，並要求在計數器達標時執行 BarrierRun 定義的方法。


CyclicBarrier 只要使用了 `await()` 方法，那就必須等到計數完成才會繼續執行或者被中斷，再次使用 `await()`，那就要再等一次計數完成。

<br>

印出結果：

<br>

```java
Commander: [assemble army!]
Soldier 0: yeah sir.
Soldier 1: yeah sir.
Soldier 2: yeah sir.
Soldier 3: yeah sir.
Soldier 4: yeah sir.
Soldier 5: yeah sir.
Soldier 6: yeah sir.
Soldier 7: yeah sir.
Soldier 8: yeah sir.
Soldier 9: yeah sir.
Commander: [Soldier 10, assemble complete.]
Soldier 7 : mission complete.
Soldier 2 : mission complete.
Soldier 6 : mission complete.
Soldier 5 : mission complete.
Soldier 8 : mission complete.
Soldier 4 : mission complete.
Soldier 3 : mission complete.
Soldier 1 : mission complete.
Soldier 0 : mission complete.
Soldier 9 : mission complete.
Commander: [Soldier 10, mission complete.]
```

<br>

`CyclicBarrier.await()` 會拋出 2 種異常，一種是`InterruptedException` 另一種是 `BrokenBarrierException`。第一種是在等待過程中 Thread 被中斷，另一種則是 CyclicBarrier 特有的 `BrokenBarrierException`。一旦遇到這個異常則表示當前 CyclicBarrier 已經破損了，系統沒辦法等到所有 Thread 到齊。

<br>

如果我們嘗試讓第五個士兵中斷：

<br>

```java
if (i == 5){
    allSoldier[0].interrupt();
}
```

<br>

這樣做我們會得到 1 個 `InterruptedException`，與 9 個 `BrokenBarrierException`。`BrokenBarrierException` 可以避免其餘 9 個 Thread 進行永久無意義等待（因為 005 已經中斷了，隊伍永遠不會 10 人到齊）。