# 合理計算並決定 ThreadPool 的 Thread 數量

<br>

-------

<br>

前人總結計算公式：

<br>

> Nthreads = Ncpu * Ucpu * (1+ W / C)

<br>

Nthreads：計算結果，使用多少 Threads 合理。

Ncpu：主機 CPU 數量。

Ucpu：目前 CPU 使用率，0 < Ucpu < 1。

W/C：等待時間／計算時間


<br>

Java 實現計算：

<br>

```java
public class ComputeSuitableThreadCount {

    public static void main(String[] args) throws Exception {
        System.out.println(computeNThreads(1, 1));
    }


    private static int getAvailableCpu(){
        return Runtime.getRuntime().availableProcessors();
    }

    private static double getProcessCpuLoad() throws Exception {

        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

        if (list.isEmpty())     return Double.NaN;

        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)      return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        return ((int)(value * 1000) / 10.0);
    }

    public static int computeNThreads(int W, int C) throws Exception {
        int Ncup = getAvailableCpu();
        double Ucpu = getProcessCpuLoad();
        return (int) (Ncup * Ucpu * (1 + W/C));
    }
}
```

<br>


