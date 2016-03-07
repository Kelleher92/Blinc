package com.eyes.blinc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
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
            float[] results;
            float score = 0;
            FaceDetector myFaceDetect;
            FaceDetector.Face[] myFace;

            Bitmap frame = mediaMetadataRetriever.getFrameAtTime(1000000);
            myFace = new FaceDetector.Face[1];
            myFaceDetect = new FaceDetector(frame.getWidth(), frame.getHeight(), 1);
            myFaceDetect.findFaces(frame, myFace);
            PointF myMidPoint = new PointF();
            myFace[0].getMidPoint(myMidPoint);
            float myEyesDistance = myFace[0].eyesDistance();

            for (int i = 1; i < 101; i++) {
                image = mediaMetadataRetriever.getFrameAtTime(1000000 + i*100000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC); //unit in microsecond
                try {
                    results = processFrame.processFrame(image, i, myMidPoint, myEyesDistance);
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

    public float calculate(int i, float[] results){
        float diff = 0;
        int[] coordinates = r.getIntArray(R.array.f001+i-1);

        float one = results[0] - coordinates[0];
        float two = results[1] - coordinates[1];
        float three = results[2] - coordinates[2];
        float four = results[3] - coordinates[3];

        diff = (abs(one/coordinates[0]) + abs(two/coordinates[1]) + abs(three/coordinates[2]) + abs(four/coordinates[3]));

        return diff;
    }
}

