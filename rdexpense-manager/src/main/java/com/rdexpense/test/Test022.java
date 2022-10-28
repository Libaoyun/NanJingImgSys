package com.rdexpense.test;

import java.util.Scanner;

/**
 * @description: 2
 * @author: Libaoyun
 * @date: 2022-10-15 14:05
 **/
public class Test022 {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int n = input.nextInt();
        int m = input.nextInt();
        int [] arr = new int[n];
        int result = 0;
        for (int i = 0; i < n; i++) {
            arr[i] = input.nextInt();
        }
        int max=arr[0], min=arr[0];
        for (int i = 1; i < n; i++) {
            if (arr[i] > max){
                max = arr[i];
            }
            if (min > arr[i]){
                min = arr[i];
            }
        }
        int len = max - min;
        result = len % m == 0 ? len/m : (len/m+1);
        System.out.println(min);
        System.out.println(max);
        System.out.println(len);
        System.out.println(m);
        System.out.println(result);
    }
}
