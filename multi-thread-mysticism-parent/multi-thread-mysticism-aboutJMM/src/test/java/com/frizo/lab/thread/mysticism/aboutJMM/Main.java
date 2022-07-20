package com.frizo.lab.thread.mysticism.aboutJMM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int[] prices = new int[]{8, 11, 9, 5, 7, 10};
        int result = sol(prices);
        System.out.println(result);
    }

    public static int sol(int[] prices){
        int minPrice = 0;
        int max = 0;
        minPrice = prices[0];
        for (int i = 0; i < prices.length-1; ++i) {
            int profit = prices[i+1] - minPrice;
            if (profit > max) {
                max = profit;
            }
            minPrice = Math.min(prices[i], prices[i + 1]);
        }
        return max;
    }

}
