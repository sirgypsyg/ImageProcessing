package org.example;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        ImageProcessor imageProcessor = new ImageProcessor();

        long start = System.currentTimeMillis();
        imageProcessor.load("/Users/kuba/IdeaProjects/ImageProcessing/src/main/java/org/example/Unequalized_Hawkes_Bay_NZ.jpg");
        //imageProcessor.addBrightnessWithThreads(10);
//        long finish = System.currentTimeMillis();
//        long time1 = finish - start;
//
//        start = System.currentTimeMillis();
//        imageProcessor.addBrightnessWithThreads(100);
        //imageProcessor.saveImage("/Users/kuba/IdeaProjects/ImageProcessing/src/main/java/output.png");
//        finish = System.currentTimeMillis();
//        long time2 = finish - start;
//
//        System.out.println(String.format("withoutThreads = %d,  with threads = %d", time1, time2));

        imageProcessor.equalizeToCIEXYZ();
        imageProcessor.saveImage("/Users/kuba/IdeaProjects/ImageProcessing/src/main/java/output.png");



    }
}