package com.eyes.blinc;

/**
 * Created by Ian on 03-Feb-16.
 */

public class sobel {
    int[] input;
    int[] output;
    float[] template = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
    int width;
    int height;
    double[] direction;

    public void init(int[] original, int widthIn, int heightIn) {
        width = widthIn;
        height = heightIn;
        input = new int[width * height];
        output = new int[width * height];
        direction = new double[width * height];
        input = original;
    }

    public int[] process() {
        float[] GY = new float[width * height];
        float[] GX = new float[width * height];
        int[] total = new int[width * height];
        int sum;
        int max = 0;

        for (int x = 1; x < width - 2; x++) {
            for (int y = 1; y < height - 2; y++) {
                sum = 0;
                for (int x1 = 0; x1 < 3; x1++) {
                    for (int y1 = 0; y1 < 3; y1++) {
                        int x2 = (x - 1 + x1);
                        int y2 = (y - 1 + y1);
                        float value = (input[y2 * width + x2] & 0xff) * (template[y1 * 3 + x1]);
                        sum += value;
                    }
                }

                GY[y * width + x] = sum;

                for (int x1 = 0; x1 < 3; x1++) {
                    for (int y1 = 0; y1 < 3; y1++) {
                        int x2 = (x - 1 + x1);
                        int y2 = (y - 1 + y1);
                        float value = (input[y2 * width + x2] & 0xff) * (template[x1 * 3 + y1]);
                        sum += value;
                    }
                }
                GX[y * width + x] = sum;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                total[y * width + x] = (int) Math.sqrt(GX[y * width + x] * GX[y * width + x] + GY[y * width + x] * GY[y * width + x]);
                direction[y * width + x] = Math.atan2(GX[y * width + x], GY[y * width + x]);
                if (max < total[y * width + x])
                    max = total[y * width + x];
            }
        }

        float ratio = (float) max / 255;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                sum = (int) (total[y * width + x] / ratio);
                output[y * width + x] = 0xff000000 | ( sum << 16 | sum << 8 | sum);
            }
        }
        return output;
    }

    public double[] getDirection() {
        return direction;
    }

}