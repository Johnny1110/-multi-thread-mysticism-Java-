package com.frizo.lab.thread.mysticism.aboutJMM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test3 {

    public static void main(String[] args) {
        System.out.println(solution("...xxx..x....xxx.", 7));
    }

    public static int solution(String S, int B) {
        int fixedCount = 0;
        List<Integer> potholes = new ArrayList<>();
        String[] box = S.split("");
        int continueX = 0;
        for (int i = 0; i < box.length; ++i){
            if (box[i].equals("x")) {
                continueX++;
            }if(box[i].equals(".") || i == box.length-1){
                if (continueX != 0)
                    potholes.add(continueX);
                continueX = 0;
            }
        }
        Collections.sort(potholes);
        System.out.println(potholes);

        for (int i = potholes.size()-1; i >= 0; i--){
            if (B > 1){
                int Xcount = potholes.get(i);
                B--;
                if (Xcount <= B) {
                    fixedCount += Xcount;
                    B = B - Xcount;
                }else{
                    fixedCount += B;
                    B = 0;
                }
            }
        }
        return fixedCount;
    }

}
