package com.frizo.lab.thread.mysticism.aboutJMM;

import java.util.Arrays;
import java.util.Collections;

public class SortingAlgorithm {

    //選擇數字正排序
    public static void sortPositive(int[] nums){
        for (int i = 0; i < nums.length; ++i) {
            for (int j = i+1; j < nums.length; ++j) {
                if (nums[i] > nums[j]) {
                    int buf = nums[i];
                    nums[i] = nums[j];
                    nums[j] = buf;
                }
            }
        }
    }

    //選擇數字反排序
    public static void sortReverse(int[] nums){
        for(int i = 0; i < nums.length; ++i) {
            for(int j = i+1; j < nums.length; ++j) {
                if (nums[i] < nums[j]) {
                    int buf = nums[i];
                    nums[i] = nums[j];
                    nums[j] = buf;
                }
            }
        }
    }

    // 泡泡排序法 0, 81, 4, 9, 7
    public static void bubbleSort(int[] nums){
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums.length - 1 - i; j++) {
                if(nums[j] > nums[j+1]){
                    int buf = nums[j];
                    nums[j] = nums[j+1];
                    nums[j+1] = buf;
                }
            }
        }
    }

    // 插入排序法
    public static int[] insertionSort(int[] array) {
        int current;
        for (int i = 0; i < array.length - 1; i++) {
            current = array[i+1];
            int preIndex = i;
            while(preIndex >= 0 && array[preIndex] > current){
                array[preIndex+1] = array[preIndex];
                preIndex--;
            }
            array[preIndex+1] = current;
        }
        return array;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{0, 81, 4, 9, 7};
        insertionSort(nums);
        for(int i = 0; i < nums.length; ++i){
            System.out.println(nums[i]);
        }
    }

}
