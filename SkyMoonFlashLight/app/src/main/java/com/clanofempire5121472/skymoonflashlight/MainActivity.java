package com.clanofempire5121472.skymoonflashlight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {
    //widgets
    private Button btn;
    private ImageView btnOn;
    private ImageView btnOff;


    //var
    private android.hardware.Camera camera;
    private boolean Flash;
    private int flashonhai;
    private boolean bannerNheloadhowa=true;
    private boolean interstatialNheloadhowa=true;



    private InterstitialAd mInterstitial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // flash switch button
        //casting
        btnOn = (ImageView) findViewById(R.id.btnon);
        btnOff = (ImageView) findViewById(R.id.btnoff);


        //  if (ContextCompat.checkSelfPermission(getApplicationContext(),
        //        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

        //      ActivityCompat.requestPermissions( this,
        //            new String[]{Manifest.permission.CAMERA}, 1);
        //}

//Ads
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        //put your own app id in above line
        //prepare Banner
        final AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
                                  @Override
                                  public void onAdLoaded() {
                                      super.onAdLoaded();
                                      mAdView.setVisibility(View.VISIBLE);
                                  }
                              });
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                if(bannerNheloadhowa){
                    mAdView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
                    bannerNheloadhowa=false;
                }
            }
        });

// Prepare the Interstitial Ad
        mInterstitial = new InterstitialAd(MainActivity.this);
// Insert the Ad Unit ID
        mInterstitial.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //request ad
        AdRequest request = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();

        mInterstitial.loadAd(request);
// Prepare an Interstitial Ad Listener
        mInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                if(interstatialNheloadhowa){
                    mInterstitial.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
                    interstatialNheloadhowa=false;
                }
            }
        });

        /*
         * First check if device is supporting flashlight or not
         */

        Flash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!Flash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }

        /*
         * Switch button click event to toggle flash on/off
         */
        btnOn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //interstatial load kro
                if (mInterstitial.isLoaded()) {
                    mInterstitial.show();
                }
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        android.hardware.Camera cam = android.hardware.Camera.open();
                        android.hardware.Camera.Parameters p = cam.getParameters();
                        p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
                        cam.setParameters(p);
                        cam.startPreview();
                        flashonhai = 1;

                        Log.d("Ok! : ","Flash on ho gya");

                    } catch (Exception e) {
                        Log.e("ERROR! : ",e.getMessage());
                    }
                } else if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 2);
                }
            }
        });
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    android.hardware.Camera cam = android.hardware.Camera.open();
                    android.hardware.Camera.Parameters p = cam.getParameters();
                    p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
                    Log.d("Ok! : ","Flash off ho gya");
                    cam.stopPreview();
                    flashonhai = 0;
                } catch (Exception e) {
                    Log.e("ERROR! : ",e.getMessage());
                }
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

}