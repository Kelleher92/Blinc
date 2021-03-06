package com.eyes.blinc;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Ian on 08-Feb-16.
 */

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private final Resources resources;
    private Dot dot;
    int height;
    int width;

    public TestSurfaceView(Context context, Resources resources) {
        super(context);
        this.resources = resources;
        dot = null;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        dot = new Dot(holder, resources, getContext(), height, width);
        dot.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (dot != null) {
            try {
                dot.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dot = null;
    }
}
