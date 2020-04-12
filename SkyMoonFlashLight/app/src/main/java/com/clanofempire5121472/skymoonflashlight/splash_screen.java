package com.clanofempire5121472.skymoonflashlight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splash_screen extends AppCompatActivity {
private ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo= (ImageView)findViewById(R.id.devlogo);



        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );

        Animation myani= AnimationUtils.loadAnimation(this,R.anim.myanimation);
        logo.startAnimation(myani);
        final Intent splashIntent = new Intent(getApplicationContext(), MainActivity.class);


       Thread mythread= new Thread() {
           @Override
            public void run() {
               try {
                   sleep(5000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                   startActivity(splashIntent);
                   finish();
               }
            }
        };
        mythread.start();
    }
}
