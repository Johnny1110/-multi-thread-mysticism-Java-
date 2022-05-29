package com.frizo.lab.thread.mysticism.aboutJMM;

import java.util.Random;

public class ChangeObjectThread extends Thread {

    private User user = new User();
    private volatile boolean stopme = false;

    public void stopMe() {
        this.stopme = true;
    }

    @Override
    public void run() {
        while (true){

            if (stopme){
                System.out.println("exit by stopme().");
                break;
            }

            synchronized (user){
                int value = new Random().nextInt();
                user.setId(value);
                user.setName(String.valueOf(value));
            }
            Thread.yield();
        }
    }

    public static class User {
        private int id;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

}
