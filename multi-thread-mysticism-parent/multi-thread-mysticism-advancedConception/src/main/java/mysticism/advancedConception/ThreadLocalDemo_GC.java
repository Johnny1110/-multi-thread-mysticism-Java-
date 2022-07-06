package mysticism.advancedConception;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalDemo_GC {

    static volatile ThreadLocal<SimpleDateFormat> local = new ThreadLocal<SimpleDateFormat>() {
        protected void finalize() {
            System.out.println(this.toString() + "has been GC(ThreadLocal).");
        }
    };

    static volatile CountDownLatch cd = new CountDownLatch(10000);

    public static class ParseDate implements Runnable {

        int i = 0;

        public ParseDate(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            try {
                if (local.get() == null) {
                    local.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {
                        protected void finalize() {
                            System.out.println("SimpleDateFormat has been GC.");
                        }
                    });
                    System.out.println(Thread.currentThread().getId() + " created SimpleDateFormat.");
                }
                Date t = local.get().parse("2022-06-31 11:34:" + i % 60);
            } catch (ParseException e) {
                e.printStackTrace();
            }finally {
                cd.countDown();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10000; i++){
            es.execute(new ParseDate(i));
        }
        cd.await();
        System.out.println("mission-1 done!");
        local = null;
        System.gc();
        System.out.println("first GC done.");

        local = new ThreadLocal<SimpleDateFormat>();
        cd = new CountDownLatch(10000);

        for (int i = 0; i < 10000; i++){
            es.execute(new ParseDate(i));
        }
        cd.await();
        System.out.println("mission-2 done!");
        es.shutdown();
        Thread.sleep(1000);
        System.gc();
        System.out.println("sec GC done.");
    }
}
