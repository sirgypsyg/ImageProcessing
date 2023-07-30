package org.example;

import java.awt.color.ColorSpace;
import java.awt.image.ColorConvertOp;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageProcessor{
    private BufferedImage image;
    private int height, width;

    public ImageProcessor(BufferedImage image) {
        this.image = image;
    }
    public ImageProcessor() {}

    public void load(String path) throws IOException {
        File file = new File(path);
        image = ImageIO.read(file);
        height = image.getHeight();
        width = image.getWidth();

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
                        = Math.max(0,Math.min(255,(rgb[0] + brightnessValue)));
                int green
                        = Math.max(0,Math.min(255,(rgb[1] + brightnessValue)));
                int blue
                        = Math.max(0,Math.min(255,(rgb[2] + brightnessValue)));


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
                                = Math.max(0,Math.min(255,(rgb[0] + brightnessValue)));
                        int green
                                = Math.max(0,Math.min(255,(rgb[1] + brightnessValue)));
                        int blue
                                = Math.max(0,Math.min(255,(rgb[2] + brightnessValue)));

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

    public void equalize() throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        int chunk = height / cores;

        int[][] rhistogram = new int[cores][256];
        int[][] ghistogram = new int[cores][256];
        int[][] bhistogram = new int[cores][256];

        Thread threads[] = new Thread[cores];
        for (int i = 0; i < cores; ++i) {
            int threadIndex = i;
            threads[i] = new Thread(() -> {
                Arrays.fill(rhistogram[threadIndex], 0);
                Arrays.fill(ghistogram[threadIndex], 0);
                Arrays.fill(bhistogram[threadIndex], 0);

                int startline = threadIndex * chunk;
                int endline = (threadIndex == cores - 1) ? height : startline + chunk;

                for (int y = startline; y < endline; y++) {
                    for (int x = 0; x < width; x++) {
                        int rgb = image.getRGB(x, y);

                        int r = (rgb >> 16) & 0xFF;
                        ++rhistogram[threadIndex][r];

                        int g = (rgb >> 8) & 0xFF;
                        ++ghistogram[threadIndex][g];

                        int b = rgb & 0xFF;
                        ++bhistogram[threadIndex][b];
                    }
                }
            });
        }
        for (int i = 0; i < cores; ++i)
            threads[i].start();
        for (int i = 0; i < cores; ++i)
            threads[i].join();

        int[] finalRedHistogram = new int[256];
        int[] finalGreenHistogram = new int[256];
        int[] finalBlueHistogram = new int[256];

        for (int i = 0; i < cores; ++i)
            for (int j = 0; j < 256; ++j){
                finalRedHistogram[j] += rhistogram[i][j];
                finalGreenHistogram[j] += ghistogram[i][j];
                finalBlueHistogram[j] += bhistogram[i][j];
            }

        //count distributors
        int[] rLut = getLut(finalRedHistogram);
        int[] gLut = getLut(finalGreenHistogram);
        int[] bLut = getLut(finalBlueHistogram);


        for(int i=0; i<image.getWidth(); i++) {
            for(int j=0; j<image.getHeight(); j++) {

                // Get pixels by R, G, B
                int alpha = new Color(image.getRGB (i, j)).getAlpha();
                int red = new Color(image.getRGB (i, j)).getRed();
                int green = new Color(image.getRGB (i, j)).getGreen();
                int blue = new Color(image.getRGB (i, j)).getBlue();

                // Set new pixel values using the histogram lookup table
                red = rLut[red];
                green = gLut[green];
                blue = bLut[blue];

                // Return back to original format
                int newPixel = colorToRGB(alpha, red, green, blue);

                // Write pixels into image
                image.setRGB(i, j, newPixel);

            }
        }

        for (int i = 0; i < 256; ++i){
            System.out.println(i + ", Red: " + rLut[i] + ", Green: " + gLut[i] + ", Blue: " + bLut[i] + "." );
        }

}


    public void equalizeToCIEXYZ() throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        int chunk = height / cores;

        ColorSpace RGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorSpace CIEXYZ = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);

        ColorConvertOp op = new ColorConvertOp(RGB, CIEXYZ, null);
        image = op.filter(image, null);

        int[][] rhistogram = new int[cores][256];
        int[][] ghistogram = new int[cores][256];
        int[][] bhistogram = new int[cores][256];

        Thread threads[] = new Thread[cores];
        for (int i = 0; i < cores; ++i) {
            int threadIndex = i;
            threads[i] = new Thread(() -> {
                Arrays.fill(rhistogram[threadIndex], 0);
                Arrays.fill(ghistogram[threadIndex], 0);
                Arrays.fill(bhistogram[threadIndex], 0);

                int startline = threadIndex * chunk;
                int endline = (threadIndex == cores - 1) ? height : startline + chunk;

                for (int y = startline; y < endline; y++) {
                    for (int x = 0; x < width; x++) {
                        int rgb = image.getRGB(x, y);

                        int r = (rgb >> 16) & 0xFF;
                        ++rhistogram[threadIndex][r];

                        int g = (rgb >> 8) & 0xFF;
                        ++ghistogram[threadIndex][g];

                        int b = rgb & 0xFF;
                        ++bhistogram[threadIndex][b];
                    }
                }
            });
        }
        for (int i = 0; i < cores; ++i)
            threads[i].start();
        for (int i = 0; i < cores; ++i)
            threads[i].join();

        int[] finalRedHistogram = new int[256];
        int[] finalGreenHistogram = new int[256];
        int[] finalBlueHistogram = new int[256];

        for (int i = 0; i < cores; ++i)
            for (int j = 0; j < 256; ++j){
                finalRedHistogram[j] += rhistogram[i][j];
                finalGreenHistogram[j] += ghistogram[i][j];
                finalBlueHistogram[j] += bhistogram[i][j];
            }

        //count distributors
        int[] rLut = getLut(finalRedHistogram);
        int[] gLut = getLut(finalGreenHistogram);
        int[] bLut = getLut(finalBlueHistogram);


        for(int i=0; i<image.getWidth(); i++) {
            for(int j=0; j<image.getHeight(); j++) {

                // Get pixels by R, G, B
                int alpha = new Color(image.getRGB (i, j)).getAlpha();
                int red = new Color(image.getRGB (i, j)).getRed();
                int green = new Color(image.getRGB (i, j)).getGreen();
                int blue = new Color(image.getRGB (i, j)).getBlue();

                // Set new pixel values using the histogram lookup table
                red = rLut[red];
                green = gLut[green];
                blue = bLut[blue];

                // Return back to original format
                int newPixel = colorToRGB(alpha, red, green, blue);

                // Write pixels into image
                image.setRGB(i, j, newPixel);

            }
        }
        ColorConvertOp toRGB = new ColorConvertOp(CIEXYZ, RGB, null);
        image = toRGB.filter(image, null);


    }
    private static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha; newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }
    private int[] getLut(int[] channel){

        int totalPix = height * width;
        double[] distributor = new double[256];
        Arrays.fill(distributor, 0);
        distributor[0] = channel[0];
        for (int i = 1; i < 256; ++i){
            distributor[i] = distributor[i-1] + channel[i];
        }

        int temp = 0;
        while (distributor[temp] == 0){
            ++temp;
        }
        double firstNotNull = distributor[temp];

        //LUT table
        int[] lut = new int[256];
        for (int i = 0; i < 256 ; ++i){
            double v = ((distributor[i] - firstNotNull) / (totalPix - firstNotNull)) * 255;
            lut[i] = (int) Math.round(v);
        }
        return lut;
    }

    public static BufferedImage getGrayScale(BufferedImage image){
        BufferedImage gImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = image.getRaster();
        WritableRaster gr = gImg.getRaster();

        for(int i=0;i<wr.getWidth();i++){
            for(int j=0;j<wr.getHeight();j++){
                gr.setSample(i, j, 0, wr.getSample(i, j, 0));
            }
        }
        gImg.setData(gr);
        return gImg;
    }


    public void saveImage(String path) throws IOException {
        ImageIO.write(image, "png", new File(path));
    }

}
