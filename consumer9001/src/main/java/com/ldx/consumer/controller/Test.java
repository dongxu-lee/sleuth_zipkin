package com.ldx.consumer.controller;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        File f = new File("C:\\Users\\tdky\\Desktop\\lib");
        File[] list = f.listFiles();
        System.out.print("javac -encoding UTF-8 -classpath yz-gatawayProject.jar  -classpath ");
        for (File fi : list) {
            System.out.print(fi.getName() + ";");
        }
        System.out.print(" CommServiceImpl.java");
    }

}
