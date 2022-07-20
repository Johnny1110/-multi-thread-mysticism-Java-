package com.firzo.mysticism.nonLock;

import sun.misc.Unsafe;

import java.util.concurrent.atomic.AtomicInteger;

public class UnsafeDemo {

    public static void main(String[] args) throws NoSuchFieldException {
        Student s = new Student("Johnny", 24);
        long offset = Unsafe.getUnsafe().objectFieldOffset(UnsafeDemo.Student.class.getDeclaredField("age"));
        int age = Unsafe.getUnsafe().getInt(s, offset);
        System.out.println(age);
    }

    public static class Student{
        private String name;

        private int age;

        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

}
