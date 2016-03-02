package com.eyes.blinc;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.SurfaceHolder;

/**
 * Created by Ian on 08-Feb-16.
 */

public class Dot {
    private Thread thread;
    private Anim anim;
    float x, y, vx, vy;
    int i;

    public Dot(){
        x = 887;
        y = 467;
        vx = 7;
        vy = 7;
        i = 0;
    }

    public Dot(SurfaceHolder holder, Resources resources, Context context) {
        anim = new Anim(holder, resources, context);
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

        if (x < 1 || x > 1773)
            vx = -vx;
        if (y < 1 || y > 933)
            vy = -vy;
    }

    public void stepNE() {
        x -= (float) (1.898287) * vx;
        y += vy;

        if (x < 1 || x > 1773)
            vx = -vx;
        if (y < 1 || y > 933)
            vy = -vy;
    }

    public void stepSW() {
        x += (float) (1.898287) * vx;
        y -= vy;

        if (x < 1 || x > 1773)
            vx = -vx;
        if (y < 1 || y > 933)
            vy = -vy;
    }

    public void stepNW() {
        x -= (float) (1.898287) * vx;
        y -= vy;

        if (x < 1 || x > 1773)
            vx = -vx;
        if (y < 1 || y > 933)
            vy = -vy;
    }
}

