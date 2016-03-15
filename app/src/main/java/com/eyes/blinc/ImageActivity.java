package com.eyes.blinc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Ian on 04-Feb-16.
 */

public class ImageActivity extends AppCompatActivity {
    EditText scoreDisp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score);

        String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4";

        scoreDisp = (EditText) this.findViewById(R.id.editText);

        ProcessRequests processRequests = new ProcessRequests(this, this.getResources());
        processRequests.processFramesInBackground(picturePath, new GetScoreCallback() {
            @Override
            public void done(int returnedScore) {
                if (returnedScore < 0 || returnedScore > 100) {
                    scoreDisp.setText("ERROR");
                    Log.i("MyActivity", "No returned score");
                } else {
                    scoreDisp.setText(String.valueOf(returnedScore) + "%");
                    Log.i("MyActivity", "Returned score");
                }
            }
        });
    }

    public void Back(View view) {
        Intent i = new Intent(this, SplashActivity.class);
        startActivity(i);
        finish();
    }
}


