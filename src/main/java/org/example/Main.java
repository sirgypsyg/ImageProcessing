package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        ImageProcessor imageProcessor = new ImageProcessor();
        imageProcessor.load("C:\\Users\\Jakub\\Desktop\\ImageProcessing\\src\\Photo on 12-06-2023 at 13.32.jpg");
        imageProcessor.addBrightnessWithThreads(-100);
        imageProcessor.saveImage("C:\\Users\\Jakub\\Desktop\\ImageProcessing\\output.png");

    }
}