package com.alephlabs.whitenoise;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity  {

    private AdView mAdView;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout frame;
    private float lastTranslate = 0.0f;
    private ImageButton mixerButton,recordButton;


    private AppBarConfiguration mAppBarConfiguration;
        private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("287BB39D093D5E59376400796EDFCC57").build();
        mAdView.loadAd(adRequest);
        drawerLayout =findViewById(R.id.drawer_layout);
        BottomNavigationView ss = (BottomNavigationView) findViewById(R.id.bottom_nav);

        final Button startRelaxing =(Button) findViewById(R.id.start_relaxing_btn);
        final Button featureMenu = findViewById(R.id.features);
        final Button shareBtn = (Button) findViewById(R.id.share);
        mixerButton = findViewById(R.id.img_home_b1);
        recordButton = findViewById(R.id.img_home_b2);

        Menu menu= ss.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        startRelaxing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_noises = new Intent(MainActivity.this,Noises.class);
                startActivity(my_noises);
            }
        });
        ss.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_home) {
                }
                if (menuItem.getItemId() == R.id.navigation_music) {
                    Intent my_noises = new Intent(getApplicationContext(),Noises.class);
                    my_noises.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_noises);
                }
                if (menuItem.getItemId() == R.id.navigation_mixer) {
                    Intent my_mixer = new Intent(getApplicationContext(),Mixer.class);
                    my_mixer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_mixer);
                }
                if (menuItem.getItemId() == R.id.navigation_record) {
                    Intent my_rec= new Intent(getApplicationContext(),recording.class);
                    my_rec.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_rec);
                }
                if (menuItem.getItemId() == R.id.navigation_library) {
                    Intent my_lib = new Intent(getApplicationContext(),library.class);
                    my_lib.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_lib);
                }
                return false;
            }
        });

        //Toast.makeText(getApplicationContext(), "" + ss.getMaxItemCount(), Toast.LENGTH_LONG).show();
    //////////
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        drawerLayout.setScrimColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawerLayout.setClipToOutline(true);
        }
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        featureMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        /////////////////////////////////////////////
        mixerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_mixer = new Intent(getApplicationContext(),Mixer.class);
                startActivity(my_mixer);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_rec= new Intent(getApplicationContext(),recording.class);
                startActivity(my_rec);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }
/////////////////////////////////////
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }else {
            super.onBackPressed();
        }
    }
}
