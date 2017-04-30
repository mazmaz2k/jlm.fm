package com.radio.fm.jlm.jlmfm;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Intent t = new Intent(StartupActivity.this, MainActivity.class);
                startActivity(t);
                finish();
            }
        },3500);
    }
}
