package com.garfield.testjava;

public class MyClass {
    public static void main(String[] args) {
        System.out.println("start call main start");
        TestKotlin.INSTANCE.testException();
        System.out.println("start call main end ");
    }

}