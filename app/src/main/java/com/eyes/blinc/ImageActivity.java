package com.eyes.blinc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by Ian on 04-Feb-16.
 */

public class ImageActivity extends AppCompatActivity {

    int RESULT_LOAD_IMAGE = 1;
    int radius = 15;
    EditText scoreDisp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score);

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap image = null;
            final int score = 0;
            scoreDisp = (EditText) this.findViewById(R.id.editText);

            ProcessRequests processRequests = new ProcessRequests(this);
            processRequests.processFramesInBackground(picturePath, radius, new GetScoreCallback() {
                @Override
                public void done(int returnedScore) {
                    if (returnedScore == 0) {
                        scoreDisp.setText(String.valueOf(score));
                        Log.i("MyActivity", "No returned score");
                    } else {
                        scoreDisp.setText(String.valueOf(returnedScore));
                        Log.i("MyActivity", "Returned score");
                    }
                }
            });

            scoreDisp.setText(String.valueOf(score));
        }
    }

    public void Back(View view) {
        Intent i = new Intent(this, SplashActivity.class);
        startActivity(i);
        finish();
    }
}


