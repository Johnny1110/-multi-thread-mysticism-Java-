package mysticism.advancedConception;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalDemo {

    static ThreadLocal<SimpleDateFormat> local = new ThreadLocal<>();


    public static class ParseDate implements Runnable {

        int i = 0;

        public ParseDate(int i) {
            this.i = i;
            local.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        }

        @Override
        public void run() {
            try {
                //if (local.get() == null)
                    //local.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                Date date = local.get().parse("2022-06-30 11:50:" + i%60);
                System.out.println(i + ":" + date);
            } catch (ParseException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++){
            es.execute(new ParseDate(i));
        }
        es.shutdown();
    }
}
