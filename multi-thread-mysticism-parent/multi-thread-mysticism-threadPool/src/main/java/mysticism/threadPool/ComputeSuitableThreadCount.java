package mysticism.threadPool;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

// formula：Nthreads = Ncpu *Ucpu * (1 + W/C)

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
