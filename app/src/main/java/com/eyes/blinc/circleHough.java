package com.eyes.blinc;

import java.util.Arrays;

/**
 * Created by Ian on 03-Feb-16.
 */

public class circleHough {
    int[] input;
    int[] output;
    int width;
    int height;
    int[] acc;
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

        Arrays.fill(output, 0xff000000);
    }

    // hough transform for lines (polar), returns the accumulator array
    public int[] process() {
        acc = new int[width * height];

        Arrays.fill(acc, 0);

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
        resultsRight = new int[3];
        resultsLeft = new int[3];
        results = new int[6];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int value = (acc[x + (y * width)] & 0xff);

                if ((value > resultsRight[0]) && (x > threshold)) {
                    resultsRight[0] = value;
                    resultsRight[1] = x;
                    resultsRight[2] = y;
                }
                else if ((value > resultsLeft[0] && (x < threshold))) {
                    resultsLeft[0] = value;
                    resultsLeft[1] = x;
                    resultsLeft[2] = y;
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

        return results;
    }

//    public int[] draw() {
//        output[(results[2] * width) + results[1]] = -1;
//        output[(results[5] * width) + results[4]] = -1;
//
//        return output;
//    }
}