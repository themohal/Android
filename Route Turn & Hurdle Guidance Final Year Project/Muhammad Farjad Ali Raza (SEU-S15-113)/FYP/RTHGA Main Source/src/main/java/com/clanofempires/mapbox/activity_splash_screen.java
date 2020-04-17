package com.clanofempires.mapbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewAnimator;

public class activity_splash_screen extends AppCompatActivity {
    private ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo= (ImageView)findViewById(R.id.devlogo);



        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );

        Animation myani= AnimationUtils.loadAnimation(this,R.anim.my_anim);

        myani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                final Animation slide_left = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.left_out);
                logo.startAnimation(slide_left);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        logo.startAnimation(myani);

        final Intent splashIntent = new Intent(getApplicationContext(), LoginActivity.class);


        Thread mythread= new Thread() {
            @Override
            public void run() {
                try {
                    sleep(4500);

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
        //
    }
}


