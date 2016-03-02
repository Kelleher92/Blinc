package com.eyes.blinc;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;

import com.eyes.blinc.processFrame;

import java.io.IOException;

/**
 * Created by Ian on 02-Mar-16.
 */
public class ProcessRequests {
    ProgressDialog progressDialog;
    MediaMetadataRetriever mediaMetadataRetriever;

    public ProcessRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        mediaMetadataRetriever = new MediaMetadataRetriever();
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
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

    public void processFramesInBackground(String inputSource, int radius, GetScoreCallback scoreCallback) {
        progressDialog.show();
        new processFramesAsyncTask(inputSource, radius, scoreCallback).execute();
    }

    public class processFramesAsyncTask extends AsyncTask<Void, Void, Integer> {
        Bitmap input;
        GetScoreCallback scoreCallback;
        int radius;
        String inputSource;

        public processFramesAsyncTask(String inputSource, int radius, GetScoreCallback scoreCallback) {
            this.inputSource = inputSource;
            this.scoreCallback = scoreCallback;
            this.radius = radius;
            mediaMetadataRetriever.setDataSource(inputSource);

        }

        @Override
        protected Integer doInBackground(Void... params) {
            Bitmap image = null;
            int score = 0;

            for (int i = 1; i <= 10; i++) {
                image = mediaMetadataRetriever.getFrameAtTime(i * 1000000); //unit in microsecond
                try {
                    score += processFrame.processFrame(image, radius);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return score;
        }

        @Override
        protected void onPostExecute(Integer score) {
            progressDialog.dismiss();
            scoreCallback.done(score);
            super.onPostExecute(score);
        }
    }
}

