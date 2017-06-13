package com.radio.fm.jlm.jlmfm2;

import android.content.Intent;
//import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView im =(ImageView)findViewById(R.id.splashImg);
        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_2);
        im.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //load main activity whan anime finish
               new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        Intent t = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(t);
                        finish();
                    }
                },1500);
              /* try {
                   Thread.sleep(1000);
               }catch (InterruptedException e){
                   e.printStackTrace();
               }
                finish();
                Intent t = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(t);*/

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
