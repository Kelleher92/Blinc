package com.eyes.blinc;

/**
 * Created by Ian on 03-Feb-16.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;

import static java.lang.Math.abs;

public class processFrame {
    static sobel sobelObject;
    static nonmax nonMaxSuppressionObject;
    static circleHough circleHoughObject;
    static hystThresh hystThreshObject;
    static int[] results;

    public static float[] processFrame(Bitmap frame, PointF myMidPoint, float myEyesDistance) throws IOException {
        float [] score = {0,0,0,0};

        int radius = (int) (myEyesDistance/11);

        Bitmap image = toGrayscale(Bitmap.createBitmap(frame, (int) (myMidPoint.x - myEyesDistance / 1.5), (int) (myMidPoint.y - myEyesDistance / 3), (int) (myEyesDistance * 1.5), (int) myEyesDistance / 2));

        int height = image.getHeight();
        int width = image.getWidth();

        sobelObject = new sobel();
        nonMaxSuppressionObject = new nonmax();
        hystThreshObject = new hystThresh();
        circleHoughObject = new circleHough();

        int[] orig = new int[width * height];

        image.getPixels(orig, 0, width, 0, 0, width, height);

        sobelObject.init(orig, width, height);
        orig = sobelObject.process();
        double direction[];
        direction = sobelObject.getDirection();

        nonMaxSuppressionObject.init(orig, direction, width, height);
        orig = nonMaxSuppressionObject.process();

        // values going in here as upper and lower hysteresis limits (80 & 100)
        hystThreshObject.init(orig, width, height, 80, 100);
        orig = hystThreshObject.process();

        circleHoughObject.init(orig, width, height, radius);

        results = circleHoughObject.process();

        score[0] = myEyesDistance * abs(myMidPoint.x - results[1]);
        score[1] = myEyesDistance * abs(myMidPoint.y - results[2]);
        score[2] = myEyesDistance * abs(myMidPoint.x - results[4]);
        score[3] = myEyesDistance * abs(myMidPoint.y - results[5]);

//        score[0] = results[1];
//        score[1] = results[2];
//        score[2] = results[4];
//        score[3] = results[5];

//        orig = circleHoughObject.draw();
//
//        int[] array = overlayImage(orig, image, height, width);
//
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//
//        bitmap.setPixels(array, 0, width, 0, 0, width, height);
//
//        Paint paint = new Paint();
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawPoint(centreX, centreY, paint);

        return score;
    }

//    public static int[] overlayImage(int[] input, Bitmap image, int height, int width) {
//
//        int[] myImage = new int[width * height];
//
//        image.getPixels(myImage, 0, width, 0, 0, width, height);
//
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                if ((input[y * width + x] & 0xff) > 0)
//                    myImage[y * width + x] = 0x00ff0000;
//            }
//        }
//
//        return myImage;
//    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}