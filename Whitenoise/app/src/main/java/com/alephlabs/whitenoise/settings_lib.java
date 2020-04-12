package com.alephlabs.whitenoise;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.Touch;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class settings_lib extends AppCompatActivity {
        private AdView mAdView;
        private Button rateUs,shareUs;
        private SwitchCompat sLockSwtich;
        private ImageButton mImageBackButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_lib);

        BottomNavigationView ss = (BottomNavigationView) findViewById(R.id.bottom_nav);
        Menu menu = ss.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
        ss.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_home) {
                    Intent my_home = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(my_home);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_music) {
                    Intent my_noises = new Intent(getApplicationContext(), Noises.class);
                    startActivity(my_noises);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_mixer) {
                    Intent my_mixer = new Intent(getApplicationContext(),Mixer.class);
                    startActivity(my_mixer);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_record) {
                    Intent my_rec = new Intent(getApplicationContext(), recording.class);
                    startActivity(my_rec);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_library) {


                }
                return false;
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("287BB39D093D5E59376400796EDFCC57").build();
        mAdView.loadAd(adRequest);

        rateUs  = findViewById(R.id.rate_us);
        shareUs = findViewById(R.id.share_lib_settings);
        sLockSwtich = findViewById(R.id.screen_lock);


        mImageBackButton = findViewById(R.id.settings_lib_back_button);
        final SharedPreferences settings = getSharedPreferences("checked_state", MODE_PRIVATE);
        final boolean silent = settings.getBoolean("switchkey", false);
        sLockSwtich.setChecked(silent);
        final boolean[] state = new boolean[1];

                sLockSwtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked&&!silent) {

                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("switchkey", isChecked);
                    editor.apply();
                   // sLockSwtich.setText("Switch is currently ON");

                    state[0] = settings.getBoolean("switchkey",false);
                    Toast.makeText(getApplicationContext(),"Screen:"+state[0],Toast.LENGTH_SHORT).show();

                }
                else {
                    getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("switchkey",false);
                    editor.apply();
                    state[0] = settings.getBoolean("switchkey",false);
                    Toast.makeText(getApplicationContext(),"Screen will get locked by default"+state[0],Toast.LENGTH_SHORT).show();

                }
                }
        });
        shareUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              setShareUs();
            }
                });
        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRateUs();
            }
        });

        mImageBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onBackPressed();
            }
        });


    }

    private void setRateUs(){
        final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
    private void setShareUs(){
        try {
            // Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.air_plane);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //String path = MediaStore.Images.Media.insertImage(getContentResolver(), b,"Title", null);
            //Uri imageUri = Uri.parse(path);
            //shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("text/plain,*");
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name+" - A great relaxing music app");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch(Exception e) {
            //e.toString();
        }
    }


}