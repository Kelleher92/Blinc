package com.eyes.blinc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Ian on 29-Feb-16.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        EditText score = (EditText) this.findViewById(R.id.editText);
        score.setText("87");
    }

    public void Back(View view) {
        Intent i = new Intent(this, SplashActivity.class);
        startActivity(i);
        finish();
    }
}


