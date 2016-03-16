package com.eyes.blinc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Ian on 04-Feb-16.
 */

public class ImageActivity extends AppCompatActivity {
    EditText scoreDisp;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_score);

        String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4";

        ProcessRequests processRequests = new ProcessRequests(this, this.getResources());
        processRequests.processFramesInBackground(picturePath, new GetScoreCallback() {
            @Override
            public void done(int returnedScore) {
                Log.i("MyActivity", "Returned score");
                setContentView(new CircularCountdown(context, returnedScore));
            }
        });
    }

    private class CircularCountdown extends View {
        private final Paint backgroundPaint;
        private final Paint progressPaint;
        private final Paint circlePaint;
        private final Paint textPaint;

        private long startTime;
        private long currentTime;
        private long maxTime;

        private long progressMillisecond;
        private double progress;

        private RectF circleBounds;
        private float radius;
        private float handleRadius;
        private float textHeight;
        private float textOffset;

        private final Handler viewHandler;
        private final Runnable updateView;

        public CircularCountdown(final Context context, final int score) {
            super(context);
            View.OnTouchListener touch = new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Intent i = new Intent(context, SplashActivity.class);
                    startActivity(i);
                    finish();
                    return true;
                }
            };
            this.setOnTouchListener(touch);

            // used to fit the circle into
            circleBounds = new RectF();

            // size of circle and handle
            radius = 300;
            handleRadius = 20;

            // limit the counter to go up to maxTime ms
            maxTime = 10000;

            // start and current time
            startTime = System.currentTimeMillis();
            currentTime = startTime;

            // the style of the background
            backgroundPaint = new Paint();
            backgroundPaint.setStyle(Paint.Style.STROKE);
            backgroundPaint.setAntiAlias(true);
            backgroundPaint.setStrokeWidth(20);
            backgroundPaint.setStrokeCap(Paint.Cap.SQUARE);
            backgroundPaint.setColor(Color.parseColor("#4D4D4D"));

            // the style of the 'progress'
            progressPaint = new Paint();
            progressPaint.setStyle(Paint.Style.STROKE);
            progressPaint.setAntiAlias(true);
            progressPaint.setStrokeWidth(20);
            progressPaint.setStrokeCap(Paint.Cap.SQUARE);
            progressPaint.setColor(Color.parseColor("#FFFFFF"));

            // the style of the 'progress'
            circlePaint = new Paint();
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setAntiAlias(true);
            circlePaint.setStrokeWidth(30);
            circlePaint.setStrokeCap(Paint.Cap.SQUARE);
            circlePaint.setColor(Color.parseColor("#FFFFFF"));

            // the style for the text in the middle
            textPaint = new TextPaint();
            textPaint.setTextSize(radius / 2);
            textPaint.setColor(Color.parseColor("#FFFFFF"));
            textPaint.setTextAlign(Paint.Align.CENTER);

            // text attributes
            textHeight = textPaint.descent() - textPaint.ascent();
            textOffset = (textHeight / 2) - textPaint.descent();

            // This will ensure the animation will run periodically
            viewHandler = new Handler();
            updateView = new Runnable() {
                @Override
                public void run() {
                    // update current time
                    currentTime = System.currentTimeMillis();

                    if (progressMillisecond <= score * 100) {
                        // get elapsed time in milliseconds and clamp between <0, maxTime>
                        progressMillisecond = (currentTime - startTime) % maxTime;

                        // get current progress on a range <0, 1>
                        progress = (double) progressMillisecond / maxTime;

                        CircularCountdown.this.invalidate();

                        viewHandler.postDelayed(updateView, 1000 / 60);
                    }
                }
            };
            viewHandler.post(updateView);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawColor(Color.parseColor("#D96E70"));

            // get the center of the view
            float centerWidth = canvas.getWidth()/2;
            float centerHeight = (float) (canvas.getHeight() / 1.75);


            // set bound of our circle in the middle of the view
            circleBounds.set(centerWidth - radius,
                    centerHeight - radius,
                    centerWidth + radius,
                    centerHeight + radius);

            // draw background circle
            canvas.drawCircle(centerWidth, centerHeight, radius, backgroundPaint);

            // we want to start at -90°, 0° is pointing to the right
            canvas.drawArc(circleBounds, -90, (float) (progress * 360), false, progressPaint);

            // display text inside the circle
            canvas.drawText("YOUR SCORE",
                    centerWidth,
                    centerHeight - radius - textOffset - 20,
                    textPaint);

            // display text inside the circle
            canvas.drawText(progressMillisecond / 100 + "%",
                    centerWidth,
                    centerHeight + textOffset,
                    textPaint);

            // draw handle or the circle
            canvas.drawCircle((float) (centerWidth + (Math.sin(progress * 2 * Math.PI) * radius)),
                    (float) (centerHeight - (Math.cos(progress * 2 * Math.PI) * radius)),
                    handleRadius,
                    circlePaint);
        }
    }
}


