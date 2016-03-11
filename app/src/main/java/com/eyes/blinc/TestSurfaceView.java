package com.eyes.blinc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ian on 08-Feb-16.
 */

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private final Resources resources;
    private Dot dot;
    private int count;
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
        count = 0;
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
