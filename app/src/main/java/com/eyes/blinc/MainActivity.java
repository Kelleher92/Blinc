package com.eyes.blinc;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

/**
 * Created by Ian on 04-Feb-16.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // we shall take the video in landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayout);

        relativeLayout.addView(new TestSurfaceView(this, this.getResources()));
        relativeLayout.addView(new MySurfaceView(this, this.getResources()));
    }
}


