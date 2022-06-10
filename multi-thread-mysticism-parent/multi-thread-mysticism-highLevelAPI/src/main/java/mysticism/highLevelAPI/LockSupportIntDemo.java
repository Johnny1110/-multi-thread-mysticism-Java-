package mysticism.highLevelAPI;

import java.util.concurrent.locks.LockSupport;

public class LockSupportIntDemo {
    public static final Object U = new Object();
    public static ChangeObjectThread t1 = new ChangeObjectThread("t1");
    public static ChangeObjectThread t2 = new ChangeObjectThread("t2");

    public static class ChangeObjectThread extends Thread {
        public ChangeObjectThread(String name){
            super.setName(name);
        }

        @Override
        public void run(){
            synchronized (U) {
                System.out.println("in " + this.getName());
                LockSupport.park(this);
                if (Thread.interrupted()) {
                    System.out.println(getName() + " got interrupted.");
                }
                System.out.println(getName() + "job done.");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        t1.start();
        Thread.sleep(100);
        t2.start();
        t1.interrupt();
        LockSupport.unpark(t2);
    }
}
