package com.alephlabs.whitenoise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import static android.os.SystemClock.*;

public class recording extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Chronometer myChronometer ;
    private TextView myFileName;
    private ImageView myRecordVisual,myRecordVisualStraight;
    private AnimationDrawable splashAnimation;
    public final static int REQUEST_CODE = 10102;

    /** Called when the activity is first created. */
    private String AudioSavePathInDevice = null;
    private MediaRecorder mediaRecorder ;
    private Random random ;
    private MediaPlayer mediaPlayer ;
    private ImageButton saveButton,recButton,closeButton,playButton,pauseButton,stopRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        recButton =  findViewById(R.id.record_btn);
        closeButton =  findViewById(R.id.close_btn);
        saveButton= findViewById(R.id.save_btn);
        playButton=  findViewById(R.id.play_btn);
        pauseButton =  findViewById(R.id.pause_btn);
        stopRec= findViewById(R.id.stop_rec);
        myChronometer = findViewById(R.id.chrono_meter);
        myFileName = findViewById(R.id.record_file_name);
        myRecordVisual = findViewById(R.id.spalsh_image);
        myRecordVisualStraight =findViewById(R.id.record_straight);
        splashAnimation = (AnimationDrawable) myRecordVisual.getBackground();

        BottomNavigationView ss = (BottomNavigationView) findViewById(R.id.bottom_nav);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("287BB39D093D5E59376400796EDFCC57").build();
        mAdView.loadAd(adRequest);
        Menu menu= ss.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        random = new Random();
////Record Button.///
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.logString("Start Recording");
                if(checkDrawOverlayPermission()) {

                    File folder = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "Whitenoise Recordings");
                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdirs();
                    }
                    if (success) {
                        // Do something on success
                        AudioSavePathInDevice =
                                Environment.getExternalStorageDirectory() +
                                        File.separator + "Whitenoise Recordings" + "/" +
                                        CreateRandomAudioFileName(5) + "AudioRecording.mp3";

                    } else {
                        // Do something else on failure
                    }
                    startRecording();
                    stopRec.setVisibility(View.VISIBLE);
                    recButton.setVisibility(View.INVISIBLE);
            }
            }
        });
        stopRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              playButton.setVisibility(View.VISIBLE);
              stopRec.setVisibility(View.INVISIBLE);
                AppLog.logString("stop Recording");
                stopRecording();
            }
        });





        // Because we call this from onTouchEvent, this code will be executed for both
        // normal touch events and for when the system calls this using Accessibility

        ss.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_home) {
                    Intent my_home = new Intent(getApplicationContext(),MainActivity.class);
                    my_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_home);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_music) {
                    Intent my_noises = new Intent(getApplicationContext(),Noises.class);
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
                }
                if (menuItem.getItemId() == R.id.navigation_library) {

                    Intent my_lib = new Intent(getApplicationContext(),library.class);
                    my_lib.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_lib);
                    finish();
                }
                return false;
            }
        });

        //Toast.makeText(getApplicationContext(), "" + ss.getMaxItemCount(), Toast.LENGTH_LONG).show();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_dialouge();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton.setVisibility(View.GONE);
                closeButton.setVisibility(View.GONE);
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.GONE);
                recButton.setVisibility(View.VISIBLE);
                myFileName.setText("");
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;

                Toast.makeText(getApplicationContext(),"File saved successfully ...",Toast.LENGTH_LONG).show();

            }
        });
    }
   // private String getFilename(){
     //   String filepath = Environment.getExternalStorageDirectory().getPath();
       // File file = new File(filepath,AUDIO_RECORDER_FOLDER);
       // if(!file.exists()){
         //   file.mkdirs();
        //}
        //return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
    //}

    private void startRecording(){
        mediaRecorder=new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(AudioSavePathInDevice);

        mediaRecorder.setOnErrorListener(errorListener);
        mediaRecorder.setOnInfoListener(infoListener);
        myChronometer.setBase(SystemClock.elapsedRealtime());
        myChronometer.start();
        String filename=AudioSavePathInDevice.substring(AudioSavePathInDevice.lastIndexOf("/")+1);
        myFileName.setText(filename);
        myRecordVisualStraight.setVisibility(View.GONE);
        splashAnimation.start();
        myRecordVisual.setVisibility(View.VISIBLE);


        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Warning: " + what + ", " + extra);
        }
    };

    private void stopRecording(){
        if(null != mediaRecorder) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            freeMemory();
            myChronometer.setBase(elapsedRealtime());
            myChronometer.stop();
            splashAnimation.stop();
            saveButton.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.VISIBLE);
            myRecordVisualStraight.setVisibility(View.VISIBLE);
            myRecordVisual.setVisibility(View.GONE);
            recButton.setVisibility(View.GONE);
            playButton.setVisibility(View.VISIBLE);
            mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(AudioSavePathInDevice);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            playButton.setVisibility(View.VISIBLE);
                            pauseButton.setVisibility(View.GONE);
                            myRecordVisualStraight.setVisibility(View.VISIBLE);
                            splashAnimation.stop();
                            myRecordVisual.setVisibility(View.GONE);
                            myChronometer.setBase(elapsedRealtime());
                            myChronometer.stop();
                            freeMemory();

                        }
                    });
                    playButton.setVisibility(View.GONE);
                    pauseButton.setVisibility(View.VISIBLE);
                    myRecordVisualStraight.setVisibility(View.GONE);
                    splashAnimation.start();
                    myRecordVisual.setVisibility(View.VISIBLE);


                }
            });
            pauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mediaPlayer.pause();
                    pauseButton.setVisibility(View.GONE);
                    playButton.setVisibility(View.VISIBLE);
                    myRecordVisualStraight.setVisibility(View.VISIBLE);
                    splashAnimation.stop();
                    myRecordVisual.setVisibility(View.GONE);
                }
            });


            Toast.makeText(this, "Recording Playing", Toast.LENGTH_LONG).show();

        }

    }

    private boolean requestAudioPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this);
        }
        if (Build.VERSION.SDK_INT >= 23) {

                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Permission :","Permission is granted");
                    return true;
                } else {

                    Log.v("Permission :","Permission is revoked");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
                    return false;
                }
            }
            else { //permission is automatically granted on sdk<23 upon installation
                Log.v("Permission :","Permission is granted");
                return true;
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            switch (requestCode) {
                case 0:
                    boolean isPerpermissionForAllGranted = false;
                    if (grantResults.length > 0 && permissions.length == grantResults.length) {
                        for (int i = 0; i < permissions.length; i++) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                isPerpermissionForAllGranted = true;
                            } else {
                                isPerpermissionForAllGranted = false;
                            }
                        }

                        Log.e("value", "Permission Granted, Now you can use local drive .");
                    } else {
                        isPerpermissionForAllGranted = true;
                        Log.e("value", "Permission Denied, You cannot use local drive .");
                    }
                    if (isPerpermissionForAllGranted) {
                        startRecording();
                    }
                    break;
            }
        }
        ////////////////////////////
        public boolean checkDrawOverlayPermission() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
                return false;
            } else {
                return true;
            }
        }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                if (requestAudioPermissions()) {
                    startRecording();
                }
            }
        }
    }
    ///////////////////////////////
    public String CreateRandomAudioFileName(int string){
    freeMemory();
        StringBuilder stringBuilder = new StringBuilder( string );

        int i = 0 ;
        while(i < string ) {

            String randomAudioFileName = "1234567890";
            stringBuilder.append(randomAudioFileName.charAt(random.nextInt(randomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();

    }
    private void alert_dialouge() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You wanted to make decision");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                File file = new File(AudioSavePathInDevice.substring(AudioSavePathInDevice.lastIndexOf("/")+1));
                                file.delete();
                                if(file.exists()){
                                    try {
                                        file.getCanonicalFile().delete();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if(file.exists()){
                                        getApplicationContext().deleteFile(file.getName());

                                    }

                                }
                                saveButton.setVisibility(View.GONE);
                                closeButton.setVisibility(View.GONE);
                                playButton.setVisibility(View.GONE);
                                pauseButton.setVisibility(View.GONE);
                                recButton.setVisibility(View.VISIBLE);
                                myFileName.setText("");
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.release();
                                mediaPlayer = null;
                                Toast.makeText(getApplicationContext(),"File successfully deleted..",Toast.LENGTH_LONG).show();
                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
           @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

}


