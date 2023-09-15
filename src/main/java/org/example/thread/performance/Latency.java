package org.example.thread.performance;

import org.example.util.Format;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Latency {
    public void exec() throws IOException, InterruptedException {
        Format.printSeparator();
        final String SOURCE_FILE = "./src/main/resources/many-flowers.jpg";
        final String DEST_FILE = "./src/main/resources/out/many-flowers_purple.jpg";

        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        // single-threaded run
        long start = System.currentTimeMillis();
        recolorSingleThreaded(originalImage, resultImage);
        long end = System.currentTimeMillis();
        long duration = end - start;
        System.out.println("Single-threaded duration = " + duration + " ms");

        // multi-threaded run
        start = System.currentTimeMillis();

        int noOfThreads = 6;
        recolorMultiThreaded(originalImage, resultImage, noOfThreads);

        end = System.currentTimeMillis();
        duration = end - start;
        System.out.println("Multi-threaded duration = " + duration + " ms");

        // save image to file
        ImageIO.write(resultImage, "jpg", new File(DEST_FILE));
    }


    private void recolorMultiThreaded (BufferedImage origImg, BufferedImage resImg, int noOfThreads) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        int leftCorner = 0;
        int width = origImg.getWidth();
        int height = origImg.getHeight() / noOfThreads;

        for (int i = 0; i < noOfThreads; i++) {
            int topCorner = i * height;

            Thread t = new Thread(() -> {
                recolorImageChunk(origImg, resImg, leftCorner, topCorner, width, height);
            });

            threads.add(t);
        }

        for (Thread t: threads) {
            t.start();
        }

        for (Thread t: threads) {
            t.join();
        }
    }


    private void recolorSingleThreaded (BufferedImage origImg, BufferedImage resImg) {
        recolorImageChunk(origImg, resImg, 0, 0, origImg.getWidth(), origImg.getHeight());
    }


    private void recolorImageChunk (BufferedImage origImg, BufferedImage resImg, int leftCorner, int topCorner, int width, int height) {
        for (int x = leftCorner; x < leftCorner + width; x++) {
            for (int y = topCorner; y < topCorner + height; y++) {
                recolorPixel(origImg, resImg, x, y);
            }
        }
    }

    // My logic for recoloring white to purple
    private void recolorPixel (BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed, newGreen, newBlue;
        if (isShadeOfGrey(red, green, blue)) {
            newRed = Math.min(red + 10, 255);
            newGreen = Math.max(green - 80, 0);
            newBlue = Math.max(blue - 5, 0);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        int newRgb = createRGBFromColors(newRed, newGreen, newBlue);

        setRgb(resultImage, x, y, newRgb);
    }

    private void setRgb (BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    private boolean isShadeOfGrey (int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(green - blue) < 30 && Math.abs(blue - red) < 30;
    }

    private int createRGBFromColors (int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;
        rgb |= 0xFF000000;
        return rgb;
    }

    private int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }
    private int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }
    private int getBlue(int rgb) {
        return (rgb & 0x000000FF);
    }
}
