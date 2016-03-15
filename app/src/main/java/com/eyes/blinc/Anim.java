package com.eyes.blinc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

/**
 * Created by Ian on 08-Feb-16.
 */

public class Anim implements Runnable {
    private SurfaceHolder surfaceHolder;
    private boolean running = true;
    private Dot dot;
    private Paint paint;
    private Bitmap dotBitmap;
    int i;

    public Anim(SurfaceHolder holder, Resources resources, Context context, int h, int w) {
        dot = new Dot(h, w);
        this.surfaceHolder = holder;
        i = 0;

        paint = new Paint();

        dotBitmap = BitmapFactory.decodeResource(resources, R.drawable.red_dot);
    }

    @Override
    public void run() {
        while (running) {

            while (i < 100) {
                drawGraphics();
                i++;
            }

            i = 0;
            while (i < 134) {
                moveDotSE();
                drawGraphics();
                i++;
            }

            i = 0;
            while (i < 30) {
                drawGraphics();
                i++;
            }

            i = 0;
            while (i < 134) {
                moveDotNE();
                drawGraphics();
                i++;
            }

            i = 0;
            while (i < 30) {
                drawGraphics();
                i++;
            }

            i = 0;
            while (i < 134) {
                moveDotNW();
                drawGraphics();
                i++;
            }

            i = 0;
            while (i < 30) {
                drawGraphics();
                i++;
            }

            i = 0;
            while (i < 134) {
                moveDotSW();
                drawGraphics();
                i++;
            }

            i = 0;
            while (i < 30) {
                drawGraphics();
                i++;
            }

            end();
        }
    }

    private void drawGraphics() {
        Canvas canvas = surfaceHolder.lockCanvas();

        if (canvas == null) {
            return;
        }

        try {
            synchronized (surfaceHolder) {
                drawDot(canvas);
            }
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    private void drawDot(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#D96E70"));
        canvas.drawBitmap(dotBitmap, dot.x, dot.y, paint);
    }

    private void moveDotSE() {
        dot.stepSE();
    }

    private void moveDotNE() {
        dot.stepNE();
    }

    private void moveDotSW() {
        dot.stepSW();
    }

    private void moveDotNW() {
        dot.stepNW();
    }

    public void end() {
        running = false;
    }
}
