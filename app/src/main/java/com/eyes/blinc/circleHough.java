package com.eyes.blinc;

/**
 * Created by Ian on 03-Feb-16.
 */

public class circleHough {

    int[] input;
    int[] output;
    int width;
    int height;
    int[] acc;
    int accSize = 2;
    int[] results, resultsRight, resultsLeft;
    int r;
    int threshold;

    public void init(int[] inputIn, int widthIn, int heightIn, int radius) {
        r = radius;
        width = widthIn;
        height = heightIn;
        input = new int[width * height];
        output = new int[width * height];
        input = inputIn;
        threshold = width / 2;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                output[x + (width * y)] = 0xff000000;
            }
        }

    }

    // hough transform for lines (polar), returns the accumulator array

    public int[] process() {

        // for polar we need accumulator of 180degrees * the longest length in the image
        int rmax = (int) Math.sqrt(width * width + height * height);
        acc = new int[width * height];

        // zero accumulator array
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                acc[x * height + y] = 0;
            }
        }

        int x0, y0;
        double t;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if ((input[y * width + x] & 0xff) == 255) {
                    for (int theta = 0; theta < 360; theta++) {
                        t = (theta * 3.14159265) / 180;
                        x0 = (int) Math.round(x - r * Math.cos(t));
                        y0 = (int) Math.round(y - r * Math.sin(t));
                        if (x0 < width && x0 > 0 && y0 < height && y0 > 0) {
                            acc[x0 + (y0 * width)] += 1;
                        }
                    }
                }
            }
        }

        // now normalise to 255 and put in format for a pixel array
        int max = 0;

        // Find max accumulator value (only 124)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (acc[x + (y * width)] > max) {
                    max = acc[x + (y * width)];
                }
            }
        }

        // Normalise all the values
        int value;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                value = (int) (((double) acc[x + (y * width)] / (double) max) * 255.0);
                acc[x + (y * width)] = 0xff000000 | (value << 16 | value << 8 | value);
            }
        }

        findMaxima();

        return results;
    }

    private int[] findMaxima() {
        resultsRight = new int[(accSize * 3)];
        resultsLeft = new int[(accSize * 3)];
        results = new int[6];

        int[] output = new int[width * height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int value = (acc[x + (y * width)] & 0xff);

                // if its higher than lowest value add it and then sort
                if ((value > resultsRight[(accSize - 1) * 3]) && (x > threshold)) {

                    // add to bottom of array
                    resultsRight[(accSize - 1) * 3] = value;
                    resultsRight[(accSize - 1) * 3 + 1] = x;
                    resultsRight[(accSize - 1) * 3 + 2] = y;

                    // shift up until its in right place
                    int i = (accSize - 2) * 3;
                    while ((i >= 0) && (resultsRight[i + 3] > resultsRight[i])) {
                        for (int j = 0; j < 3; j++) {
                            int temp = resultsRight[i + j];
                            resultsRight[i + j] = resultsRight[i + 3 + j];
                            resultsRight[i + 3 + j] = temp;
                        }
                        i = i - 3;
                        if (i < 0) break;
                    }
                } else if ((value > resultsLeft[(accSize - 1) * 3] && (x < threshold))) {

                    // add to bottom of array
                    resultsLeft[(accSize - 1) * 3] = value;
                    resultsLeft[(accSize - 1) * 3 + 1] = x;
                    resultsLeft[(accSize - 1) * 3 + 2] = y;

                    // shift up until its in right place
                    int i = (accSize - 2) * 3;
                    while ((i >= 0) && (resultsLeft[i + 3] > resultsLeft[i])) {
                        for (int j = 0; j < 3; j++) {
                            int temp = resultsLeft[i + j];
                            resultsLeft[i + j] = resultsLeft[i + 3 + j];
                            resultsLeft[i + 3 + j] = temp;
                        }
                        i = i - 3;
                        if (i < 0) break;
                    }
                }
            }
        }

        // Take top results from left & right half of image
        results[0] = resultsRight[0];
        results[1] = resultsRight[1];
        results[2] = resultsRight[2];
        results[3] = resultsLeft[0];
        results[4] = resultsLeft[1];
        results[5] = resultsLeft[2];

        for (int i = accSize - 1; i >= 0; i--) {
            //Log.i("results", " " + results[i * 3] + " " + results[i * 3 + 1] + " " + results[i * 3 + 2]);
            drawCircle(results[i * 3], results[i * 3 + 1], results[i * 3 + 2]);
        }

        return results;
    }

    private void setCentre(int xPos, int yPos) {
        output[(yPos * width) + xPos] = -1;
    }

    // draw circle at x y
    private void drawCircle(int pix, int xCenter, int yCenter) {
        setCentre(xCenter, yCenter);
    }
}