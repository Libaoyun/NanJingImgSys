package com.rdexpense.test;

import java.util.Scanner;

/**
 * @description: 002
 * @author: Libaoyun
 * @date: 2022-10-15 13:44
 **/
public class Test02 {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int N = input.nextInt();
        int x = input.nextInt();
        int y = input.nextInt();
        int [] arr = new int[N];
        for (int i = 0; i < N; i++) {
            arr[i] = input.nextInt();
        }
        int times = 0;
        if (x < y){
            for (int i = 0; i < N-1; i++) {
                if (arr[i] != arr[i+1]){
                    for (int j = 0; j <= i; j++) {
                        arr[j] = arr[i+1];
                    }
                    times++;
                }
            }
            times *= x;
        }
        else {
            for (int i = N-1; i >= 1; i--) {
                if (arr[i] != arr[i-1]){
                    for (int j = N-1; j >= i; j--) {
                        arr[j] = arr[i-1];
                    }
                    times++;
                }
            }
            times *= y;
        }
        System.out.println(times);
    }
}
