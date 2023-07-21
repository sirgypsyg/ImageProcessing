package org.example;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        ImageProcessor imageProcessor = new ImageProcessor();

        long start = System.currentTimeMillis();
        imageProcessor.load("C:\\Users\\Jakub\\Desktop\\ImageProcessing\\src\\Photo on 12-06-2023 at 13.32.jpg");
        imageProcessor.adjustBrightness(100);
        long finish = System.currentTimeMillis();
        long time1 = finish - start;

        start = System.currentTimeMillis();
        imageProcessor.addBrightnessWithThreads(100);
        imageProcessor.saveImage("C:\\Users\\Jakub\\Desktop\\ImageProcessing\\output.png");
        finish = System.currentTimeMillis();
        long time2 = finish - start;

        System.out.println(String.format("withoutThreads = %d,  with threads = %d", time1, time2));

        imageProcessor.calculateHistogram();

    }
}