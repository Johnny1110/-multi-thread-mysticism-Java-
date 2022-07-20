package com.frizo.lab.thread.mysticism.aboutJMM;

public class Test1 {

    public static void main(String[] args) {
        int[] nums = new int[]{4, 2, 5, 8 ,7 ,3, 7};
        System.out.println(solution(nums));
    }

    public static int solution(int[] A) {
        int a = process(A);
        sortReverse(A);
        int b = process(A);
        return Math.max(a, b);
    }

    private static int process(int[] a) {
        int count = 0;
        boolean isFirstUsed = false;
        for (int i = 0; i < a.length; i++){
            int num1 = a[i];
            int num2;
            if ( i+1 < a.length){
                num2 = a[i+1];
            }else if (!isFirstUsed){
                num2 = a[0];
            }else{
                return count;
            }

            int sum = num1+num2;
            if (sum%2==0){
                count++;
                i++;
                if (i == 0){
                    isFirstUsed = true;
                }
            }
        }
        return count;
    }

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
}
