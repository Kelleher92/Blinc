package com.eyes.blinc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.eyes.blinc.processFrame;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.Format;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by Ian on 02-Mar-16.
 */
public class ProcessRequests {
    ProgressDialog progressDialog;
    MediaMetadataRetriever mediaMetadataRetriever;
    Resources r;

    public ProcessRequests(Context context, Resources resources) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        mediaMetadataRetriever = new MediaMetadataRetriever();
        r = resources;
    }

    public void processFramesInBackground(String inputSource, GetScoreCallback scoreCallback) {
        progressDialog.show();
        new processFramesAsyncTask(inputSource, scoreCallback).execute();
    }

    public class processFramesAsyncTask extends AsyncTask<Void, Void, Integer> {
        GetScoreCallback scoreCallback;
        String inputSource;

        public processFramesAsyncTask(String inputSource, GetScoreCallback scoreCallback) {
            this.inputSource = inputSource;
            this.scoreCallback = scoreCallback;
            mediaMetadataRetriever.setDataSource(inputSource);

        }

        @Override
        protected Integer doInBackground(Void... params) {
            Bitmap image = null;
            int[] results;
            float score = 0;

            for (int i = 0; i < 100; i++) {
                image = mediaMetadataRetriever.getFrameAtTime(1000000 + i*100000); //unit in microsecond
                try {
                    results = processFrame.processFrame(image);
                    score += calculate(i, results);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return (int) (100 - score/100);
        }

        @Override
        protected void onPostExecute(Integer score) {
            progressDialog.dismiss();
            scoreCallback.done(score);
            super.onPostExecute(score);
        }
    }

    public float calculate(int i, int[] results){
        float diff = 0;
        int[] coordinates = r.getIntArray(R.array.f001+i);

        float one = results[1] - coordinates[0];
        float two = results[2] - coordinates[1];
        float three = results[4] - coordinates[2];
        float four = results[5] - coordinates[3];

        diff = (abs(one/results[1]) + abs(two/results[2]) + abs(three/results[4]) + abs(four/results[5]));

        Log.i("i", "" + i + " " + diff);

        return diff;
    }
}

