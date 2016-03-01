package com.eyes.blinc;

/**
 * Created by Ian on 03-Feb-16.
 */

import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;

public class processFrame {
    static sobel sobelObject;
    static nonmax nonMaxSuppressionObject;
    static circleHough circleHoughObject;
    static hystThresh hystThreshObject;

    public static Bitmap processFrame(Bitmap image, int radius) throws IOException {
        Log.i("START", "now");

        sobelObject = new sobel();
        nonMaxSuppressionObject = new nonmax();
        hystThreshObject = new hystThresh();
        circleHoughObject = new circleHough();

        int height = image.getHeight();
        int width = image.getWidth();

        int[] orig = new int[width * height];

        image.getPixels(orig, 0, width, 0, 0, width, height);

        sobelObject.init(orig, width, height);
        orig = sobelObject.process();
        double direction[] = new double[width * height];
        direction = sobelObject.getDirection();

        nonMaxSuppressionObject.init(orig, direction, width, height);
        orig = nonMaxSuppressionObject.process();

        // values going in here as upper and lower hysteresis limits (80 & 100)
        hystThreshObject.init(orig, width, height, 80, 100);
        orig = hystThreshObject.process();

        circleHoughObject.init(orig, width, height, radius);
        orig = circleHoughObject.process();

        int[] array = overlayImage(orig, image, height, width);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        bitmap.setPixels(array, 0, width, 0, 0, width, height);

        return bitmap;
    }

    public static int[] overlayImage(int[] input, Bitmap image, int height, int width) {

        int[] myImage = new int[width * height];

        image.getPixels(myImage, 0, width, 0, 0, width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if ((input[y * width + x] & 0xff) > 0)
                    myImage[y * width + x] = 0x00ff0000;
            }
        }

        return myImage;
    }

}