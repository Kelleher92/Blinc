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
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by Ian on 04-Feb-16.
 */

public class ImageActivity extends AppCompatActivity {

    int RESULT_LOAD_IMAGE = 1;
    int radius = 15;
    ImageView imageView;
    Button back;
    MediaMetadataRetriever mediaMetadataRetriever;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        back = (Button) findViewById(R.id.Backbutton);

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
            Bitmap returnedImage = null;

            mediaMetadataRetriever.setDataSource(picturePath);

            for (int i = 1; i <= 3; i++) {
                image = mediaMetadataRetriever.getFrameAtTime(i * 1000000); //unit in microsecond

                try {
                    returnedImage = processFrame.processFrame(image, radius);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(returnedImage);

//
//            ProcessRequests processRequests = new ProcessRequests(this);
//            processRequests.processFrameInBackground(input, radius, new GetImageCallback() {
//                @Override
//                public void done(Bitmap returnedImage) {
//                    if (returnedImage == null) {
//                        imageView.setImageBitmap(input);
//                        Log.i("MyActivity", "No returned image");
//                    } else {
//                        imageView.setImageBitmap(returnedImage);
//                        Log.i("MyActivity", "Returned image");
//                    }
//                }
//            });
        }
    }

    public void Back(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}

