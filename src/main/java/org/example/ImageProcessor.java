package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImageProcessor{
    private BufferedImage image;
    private int height, width;

    public void load(String path) throws IOException {
        File file = new File(path);
        image = ImageIO.read(file);
        height = image.getHeight();
        width = image.getWidth();
    }
    public static int truncate(int value)
    {
        if (value < 0) value = 0;
        else if (value > 255) value = 255;
        return value;
    }


    public void adjustBrightness(int brightnessValue)
    {

        int rgb[];

        // Outer loop for width of image
        for (int i = 0; i < image.getWidth(); i++) {

            // Inner loop for height of image
            for (int j = 0; j < image.getHeight(); j++) {

                rgb = image.getRaster().getPixel(i, j, new int[3]);

                // Using(calling) method 1
                int red
                        = Math.min(0, rgb[0] + brightnessValue);
                int green
                        = Math.min(0, rgb[1] + brightnessValue);
                int blue
                        = Math.min(0, rgb[2] + brightnessValue);

                int arr[] = { red, green, blue };

                // Using setPixel() method
                image.getRaster().setPixel(i, j, arr);

            }
        }
    }
    public void addBrightnessWithThreads(int brightnessValue) throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        int chunk = height / cores;

        Thread threads[] = new Thread[cores];

        for (int i = 0; i < cores; ++i) {
            int threadIndex = i;
            threads[i] = new Thread(() -> {
                int startline = threadIndex * chunk;
                int endline = (threadIndex == cores - 1) ? height : startline + chunk;

                for (int y = startline; y < endline; y++) {
                    for (int x = 0; x < width; x++) {
                        int rgb[] = image.getRaster().getPixel(x, y, new int[3]);

                        // Using(calling) method 1
                        int red
                                = truncate(rgb[0] + brightnessValue);
                        int green
                                = truncate(rgb[1] + brightnessValue);
                        int blue
                                = truncate(rgb[2] + brightnessValue);

                        int arr[] = { red, green, blue };

                        // Using setPixel() method
                        image.getRaster().setPixel(x, y, arr);
                    }
                }
            });
        }
        for (int j = 0; j < cores; ++j) {
            threads[j].start();
        }
        for (var thread : threads) {
            thread.join();
        }
    }

    public void calculateHistogram() throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        int chunk = height / cores;
        int[][] histogram = new int[cores][256];

        Thread threads[] = new Thread[cores];
        for (int i = 0; i < cores; ++i) {
            int threadIndex = i;
            threads[i] = new Thread(() -> {
                Arrays.fill(histogram[threadIndex], 0);
                int startline = threadIndex * chunk;
                int endline = (threadIndex == cores - 1) ? height : startline + chunk;

                for (int y = startline; y < endline; y++) {
                    for (int x = 0; x < width; x++) {
                        int rgb = image.getRGB(x, y);
                        int r = (0xFF0000 & rgb) >> 16;
                        ++histogram[threadIndex][r];
                    }
                }
            });
        }
        for (int i = 0; i < cores; ++i)
            threads[i].start();

        for (int i = 0; i < cores; ++i)
            threads[i].join();
        int[] finalHistogram = new int[256];
        for (int i = 0; i < cores; ++i)
            for (int j = 0; j < 256; ++j)
                finalHistogram[j] += histogram[i][j];

        for (int i = 0; i < 256; ++i)
            System.out.println(i + ", " + finalHistogram[i] + " ");
}

    public void saveImage(String path) throws IOException {
        ImageIO.write(image, "png", new File(path));
    }

}
