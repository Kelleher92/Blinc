package com.eyes.blinc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static java.lang.Math.abs;

/**
 * Created by Ian on 02-Mar-16.
 */
public class ProcessRequests {
    ProgressDialog progressDialog;
    MediaMetadataRetriever mediaMetadataRetriever;
    Resources r;
    Context context;

    public ProcessRequests(Context c, Resources resources) {
        context = c;
        mediaMetadataRetriever = new MediaMetadataRetriever();
        r = resources;
    }

    public void processFramesInBackground(String inputSource, GetScoreCallback scoreCallback) {
        new processFramesAsyncTask(inputSource, scoreCallback).execute();
    }

    public class processFramesAsyncTask extends AsyncTask<Void, Integer, Integer> {
        GetScoreCallback scoreCallback;
        String inputSource;

        public processFramesAsyncTask(String inputSource, GetScoreCallback scoreCallback) {
            this.inputSource = inputSource;
            this.scoreCallback = scoreCallback;
            mediaMetadataRetriever.setDataSource(inputSource);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Processing");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Bitmap image;
            float[] results;
            float[] first = {0,0,0,0};
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

            image = mediaMetadataRetriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC); //unit in microsecond
            try {
                first = processFrame.processFrame(image, myMidPoint, myEyesDistance);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 1; i < 101; i++) {
                publishProgress(i);
                image = mediaMetadataRetriever.getFrameAtTime(1000000 + i * 100000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC); //unit in microsecond
                try {
                    results = processFrame.processFrame(image, myMidPoint, myEyesDistance);
                    score += calculate(i, results, first);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return (int) (100 - score);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //set the current progress of the progress dialog
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer score) {
            progressDialog.dismiss();
            scoreCallback.done(score);
            super.onPostExecute(score);
        }
    }

    public int calculate(int i, float[] results, float[] first) {
        float diff;
        int[] coordinates = r.getIntArray(R.array.f001 + i - 1);

//        float one = abs(coordinates[0] - (100*abs(results[0] - first[0])));
//        float two = abs(coordinates[1] - (100*abs(results[1] - first[1])));
//        float three = abs(coordinates[2] - (100*abs(results[2] - first[2])));
//        float four = abs(coordinates[3] - (100*abs(results[3] - first[3])));

        float one = results[0] - first[0];
        float two = results[1] - first[1];
        float three = results[2] - first[2];
        float four = results[3] - first[3];

        diff = abs(one) + abs(two) + abs(three) + abs(four);

        Log.i("score", "" + one + " " + two + " " + three + " " + four);

        if (diff > 10)
            return 1;
        else
            return 0;

    }
}

