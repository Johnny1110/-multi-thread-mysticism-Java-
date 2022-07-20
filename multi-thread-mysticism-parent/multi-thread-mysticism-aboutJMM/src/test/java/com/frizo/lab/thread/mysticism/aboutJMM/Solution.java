package com.frizo.lab.thread.mysticism.aboutJMM;

import java.util.*;

import static com.frizo.lab.thread.mysticism.aboutJMM.SortingAlgorithm.bubbleSort;

class Solution {

    public int solution(int[] A) {
        int theNum = 1;
        Set<Integer> positiveNums = collectAllpositiveNum(A);
        if (positiveNums.size() == 0) {
            return theNum;
        }
        System.out.println(positiveNums);

        while (true){
            if (positiveNums.contains(theNum)) {
                theNum++;
            }else{
                break;
            }
        }

        return theNum;
    }



    private Set<Integer> collectAllpositiveNum(int[] a) {
        Set<Integer> buf = new HashSet<>();
        for (int i = 0; i < a.length; ++i) {
            if (a[i] > 0) {
                buf.add(a[i]);
            }
        }
        return buf;
    }


    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int[] array = new int[nums1.length + nums2.length];
        for(int i = 0; i < nums1.length; i++){
            array[i] = nums1[i];
        }
        for(int i = 0; i < nums2.length; i++){
            array[nums1.length+i] = nums2[i];
        }
        Arrays.sort(array);
        if(array.length%2 == 1){
            return array[array.length/2];
        }else{
            int mid = array.length/2;
            double a = (array[mid-1]);
            double b = (array[mid]);
            return (a+b)/2;
        }
    }



    public static void main(String[] args) {
        int[] nums1 = new int[]{1, 2};
        int[] nums2 = new int[]{3, 4};
        System.out.println(findMedianSortedArrays(nums1, nums2));
    }

}
