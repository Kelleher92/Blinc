package com.eyes.blinc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;

/**
 * Created by Ian on 08-Feb-16.
 */

public class Dot {
    private Thread thread;
    private Anim anim;
    float x, y, vx, vy;
    int height, width, heightLimit, widthLimit;

    public Dot(int h, int w){
        height = h;
        width = w;

        x = (float) (width/2.165);
        y = (float) (height/2.3125);

        heightLimit = (int) (height/1.157);
        widthLimit = (int) (width/1.0828);

        vx = 7;
        vy = 7;
    }

    public Dot(SurfaceHolder holder, Resources resources, Context context, int h, int w) {
        anim = new Anim(holder, resources, context, h, w);
        thread = new Thread(anim);
    }

    public void start() {
        thread.start();
    }


    public void stop() throws InterruptedException {
        anim.end();
        thread.join();
    }

    public void stepSE() {
        x += (float) (1.898287) * vx;
        y += vy;

        if (x < 1 || x > widthLimit)
            vx = -vx;
        if (y < 1 || y > heightLimit)
            vy = -vy;
    }

    public void stepNE() {
        x -= (float) (1.898287) * vx;
        y += vy;

        if (x < 1 || x > widthLimit)
            vx = -vx;
        if (y < 1 || y > heightLimit)
            vy = -vy;
    }

    public void stepSW() {
        x += (float) (1.898287) * vx;
        y -= vy;

        if (x < 1 || x > widthLimit)
            vx = -vx;
        if (y < 1 || y > heightLimit)
            vy = -vy;
    }

    public void stepNW() {
        x -= (float) (1.898287) * vx;
        y -= vy;

        if (x < 1 || x > widthLimit)
            vx = -vx;
        if (y < 1 || y > heightLimit)
            vy = -vy;
    }
}

