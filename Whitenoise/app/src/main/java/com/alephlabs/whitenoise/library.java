package com.alephlabs.whitenoise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class library extends AppCompatActivity {
    private AdView mAdView;
    private Button setting,recording,playList,moreApps;
    private ImageButton mLibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.e("Width", "" + width);
        Log.e("height", "" + height);
        setContentView(R.layout.activity_library);

        BottomNavigationItemView bottomNavigationItemView;
        BottomNavigationView ss = (BottomNavigationView) findViewById(R.id.bottom_nav);
        mAdView = findViewById(R.id.adView);
        setting = findViewById(R.id.setting);
        mLibBack = findViewById(R.id.lib_back_button);
        recording = findViewById(R.id.recording);
        playList = findViewById(R.id.play_list);
        moreApps = findViewById(R.id.moreApps);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("287BB39D093D5E59376400796EDFCC57").build();
        mAdView.loadAd(adRequest);
        Menu menu = ss.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        ss.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_home) {
                    Intent my_home = new Intent(getApplicationContext(), MainActivity.class);
                    my_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_home);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_music) {
                    Intent my_noises = new Intent(getApplicationContext(), Noises.class);
                    my_noises.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_noises);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_mixer) {
                    Intent my_mixer = new Intent(getApplicationContext(),Mixer.class);
                    my_mixer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_mixer);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_record) {
                    Intent my_rec = new Intent(getApplicationContext(), recording.class);
                    my_rec.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_rec);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_library) {


                }
                return false;
            }
        });

        //Toast.makeText(getApplicationContext(), "" + ss.getMaxItemCount(), Toast.LENGTH_LONG).show();


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_settings = new Intent(getApplicationContext(), settings_lib.class);
                startActivity(my_settings);


            }
        });
        recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_lib_recordings = new Intent(getApplicationContext(), lib_recordings.class);
                startActivity(my_lib_recordings);
            }
        });
        mLibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        playList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_playlist=new Intent(getApplicationContext(),playListSettings.class);
                startActivity(my_playlist);
            }
        });
        moreApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String developerName ="Sweet+Sweet+Studios&hl=en"; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + developerName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + developerName)));
                }
            }
        });
    }


}

