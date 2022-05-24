package com.frizo.lab.thread.mysticism.aboutJMM;

import org.junit.Test;

public class LongToBinTest {

    @Test
    public void testLongToBin(){
        String a =  Long.toBinaryString(111);
        String b =  Long.toBinaryString(-999);
        String c =  Long.toBinaryString(333);
        String d =  Long.toBinaryString(-444);
        String e =  Long.toBinaryString(4294966852L);
        String f =  Long.toBinaryString(-4294966963L);
        System.out.println("111 = " + a);
        System.out.println("-999 = " + b);
        System.out.println("333 = " + c);
        System.out.println("-444 = " + d);
        System.out.println("4294966852L = " + e);
        System.out.println("-4294966963L = " + f);
    }

}
