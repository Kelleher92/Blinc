package com.eyes.blinc;

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
    Button back;
    EditText scoreDisp;
    MediaMetadataRetriever mediaMetadataRetriever;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score);

        mediaMetadataRetriever = new MediaMetadataRetriever();
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
            int score = 0;

            mediaMetadataRetriever.setDataSource(picturePath);

            for (int i = 1; i <= 10; i++) {
                image = mediaMetadataRetriever.getFrameAtTime(i * 1000000); //unit in microsecond
                try {
                    score += processFrame.processFrame(image, radius);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            scoreDisp = (EditText) this.findViewById(R.id.editText);
            scoreDisp.setText(String.valueOf(score));
        }
    }

    public void Back(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}


