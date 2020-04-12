package com.alephlabs.whitenoise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


public class Mixer extends AppCompatActivity implements SeekBar.OnKeyListener {
    private AdView mAdView;
    private ImageButton mPlayer1, mPlayer2, mPlayer3, mPlayer4,lockedButton,playButton,pauseButton;
    private AudioManager audioManager;
    Intent service;
    ///////////////////////////In App Billing
    private static final String TAG = "InAppBilling";

   // private BillingClient mBillingClient;
/////////////////////////in app billing ends here
    ///
    private RecyclerView mRecyclerView;
    private MixerRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    MediaPlayer mediaPlayer1, mediaPlayer2, mediaPlayer3, mediaPlayer4;
    SeekBar seekBar1, seekBar2, seekBar3, seekBar4, seekBarMain;
    private ArrayList<MixerPlayListItems> mixerPlayListItems;
    private List<Integer> audioFileArrayChill;
    private final static int MAX_VOLUME = 100;
    int checkItem1 = 0;
    int checkItem2 = 0;
    int checkItem3 = 0;
    int checkItem4 = 0;
    float vol1 = 50;
    float vol2 = 50;
    float vol3 = 50;
    float vol4 = 50;
/////
MyReceiver myReceiver;
    Intent seekbar1,seekbar2,seekbar3,seekbar4;
    PendingIntent p1,p2,p3,p4;
    int isPause=0;
    int mediaIntent1,mediaIntent2,mediaIntent3,mediaIntent4=0;
    public static final String FILTER_ACTION_KEY = "any_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixer);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //////////////////////////////////////////////////////////////////////
        mixerPlayListItems = new ArrayList<MixerPlayListItems>();
        seekBar1 = findViewById(R.id.seekbar1);
        seekBar2 = findViewById(R.id.seekbar2);
        seekBar3 = findViewById(R.id.seekbar3);
        seekBar4 = findViewById(R.id.seekbar4);
        seekBarMain = findViewById(R.id.seekBar12);
        mRecyclerView = (RecyclerView) findViewById(R.id.mixer_recycler_view);
        audioFileArrayChill = new ArrayList<Integer>();



        //ADD NEW DATA HERE................
        audioFileArrayChill.add(R.raw.aac_airplane_white_noise1);
        audioFileArrayChill.add(R.raw.aac_soothe_your_crying_baby_8_hours);
        audioFileArrayChill.add(R.raw.aac_white_noise_for_babies_blow_dryer_10_hours_relaxing_video_sleep_aide_hair_dryer);
        audioFileArrayChill.add(R.raw.aac_campfire_outdoors_with_owls);
        audioFileArrayChill.add(R.raw.aac_electric_clock_ticking_make_baby_sleep_white_noise);
        audioFileArrayChill.add(R.raw.aac_25_dakika_huzur_sakinlestici_sesi);
        audioFileArrayChill.add(R.raw.aac_campfire_river_night_ambience_nature);
        audioFileArrayChill.add(R.raw.aac_0_hours_of_fireworks);
        audioFileArrayChill.add(R.raw.aac_bamboo);
        audioFileArrayChill.add(R.raw.aac_sleep_to_rain_thunder_sounds_relaxing_thunderstorm_white);
        audioFileArrayChill.add(R.raw.aac_starship_sleeping_quarters_sleep_sounds);
        audioFileArrayChill.add(R.raw.aac_extended_cut_the_incredible_sounds_of_the_falcon);
        audioFileArrayChill.add(R.raw.aac_air_conditioner_10_hours_of_relaxing_ambient_sound);
        audioFileArrayChill.add(R.raw.aac_singing_birds_nature_sounds);
        audioFileArrayChill.add(R.raw.aac_yagmur_ormani_ve);
        audioFileArrayChill.add(R.raw.hours_shower_best_tinnitus_masking);
        audioFileArrayChill.add(R.raw.aac_das_gerausch_von_regen_auf_einem);
        audioFileArrayChill.add(R.raw.aac_native_american_sleep_music_canyon_flute_nocturnal_canyon_sounds_sleep_meditation);
        audioFileArrayChill.add(R.raw.aac_study_power_focus_increase_concentration_calm);
        audioFileArrayChill.add(R.raw.aac_oscillating_fan_8hrs_sleep_sounds);
        audioFileArrayChill.add(R.raw.aac_remove_mental_blockages_subconscious_negativity_dissolve);

        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.air_plane,"Air Plane"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.adult_baby_bed,"Relaxation For Babies"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.barber_bathrobe_bathroom,"Hair Dryer"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.calm_waters_camp_camping,"Camp Fire"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.thomas_bormans_unsplash,"Clock Ticking"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.adult_architecture_dome,"Ney"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.michael_shannon_unsplash,"Fire"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.zuza_galczynska_unsplash,"Fire Works"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.tomoko_uji_unsplash,"Bamboo Water Drops"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.inge_maria_unsplash,"Rain"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.astronomy_cosmos_exploration,"Travelling Starship"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.astronomy_earth_explosion,"Rocket Launch"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.air_conditioner_architecture_building,"Air Conditioner"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.animals_avian_birds,"Birds"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.cascade_creek_environment,"Jungle"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.bath_bathroom_chrome,"Shower"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.background_blue_clouds,"Rain On Tent"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.back_light_dark_face_paint,"Native American Flute"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.black_background_brain_close_up,"Studying"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.black_black_and_white_close_up,"Fan"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.adult_ancient,"Mental Relaxation"));
        ////////////////////////
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MixerRecyclerAdapter(mixerPlayListItems);
        mRecyclerView.setAdapter(mAdapter);

        /////////////////////////////////////////////
        mPlayer1 = findViewById(R.id.music1);
        mPlayer2 = findViewById(R.id.music2);
        mPlayer3 = findViewById(R.id.music3);
        mPlayer4 = findViewById(R.id.music4);
        playButton = findViewById(R.id.play_btn_mixer);
        pauseButton = findViewById(R.id.pause_btn_mixer);
        lockedButton =findViewById(R.id.lockedPurchaseButton);
        //////////////////////////////////
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//////////////////////////////////////////////
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("287BB39D093D5E59376400796EDFCC57").build();
        List<String> testDeviceIds = Arrays.asList("287BB39D093D5E59376400796EDFCC57");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
        adRequest.isTestDevice(this);
        mAdView.loadAd(adRequest);

        BottomNavigationView ss = (BottomNavigationView) findViewById(R.id.bottom_nav);
        Menu menu = ss.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        ss.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_home) {
                    Intent my_home = new Intent(getApplicationContext(), MainActivity.class);
                    my_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_home);
                    if(mediaPlayer1!=null){
                        mediaPlayer1.stop();
                        mediaPlayer1.reset();
                        mediaPlayer1.release();
                        mediaPlayer1=null;
                    }
                    if(mediaPlayer2!=null){
                        mediaPlayer2.stop();
                        mediaPlayer2.reset();
                        mediaPlayer2.release();
                        mediaPlayer2=null;
                    }
                    if(mediaPlayer3!=null){
                        mediaPlayer3.stop();
                        mediaPlayer3.reset();
                        mediaPlayer3.release();
                        mediaPlayer3=null;
                    }
                    if(mediaPlayer4!=null){
                        mediaPlayer4.stop();
                        mediaPlayer4.reset();
                        mediaPlayer4.release();
                        mediaPlayer4=null;
                    }
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_music) {
                    Intent my_noises = new Intent(getApplicationContext(), Noises.class);
                    my_noises.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_noises);
                    if(mediaPlayer1!=null){
                        mediaPlayer1.stop();
                        mediaPlayer1.reset();
                        mediaPlayer1.release();
                        mediaPlayer1=null;
                    }
                    if(mediaPlayer2!=null){
                        mediaPlayer2.stop();
                        mediaPlayer2.reset();
                        mediaPlayer2.release();
                        mediaPlayer2=null;
                    }
                    if(mediaPlayer3!=null){
                        mediaPlayer3.stop();
                        mediaPlayer3.reset();
                        mediaPlayer3.release();
                        mediaPlayer3=null;
                    }
                    if(mediaPlayer4!=null){
                        mediaPlayer4.stop();
                        mediaPlayer4.reset();
                        mediaPlayer4.release();
                        mediaPlayer4=null;
                    }
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_mixer) {
                }
                if (menuItem.getItemId() == R.id.navigation_record) {
                    Intent my_rec = new Intent(getApplicationContext(), recording.class);
                    my_rec.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_rec);
                    if(mediaPlayer1!=null){
                        mediaPlayer1.stop();
                        mediaPlayer1.reset();
                        mediaPlayer1.release();
                        mediaPlayer1=null;
                    }
                    if(mediaPlayer2!=null){
                        mediaPlayer2.stop();
                        mediaPlayer2.reset();
                        mediaPlayer2.release();
                        mediaPlayer2=null;
                    }
                    if(mediaPlayer3!=null){
                        mediaPlayer3.stop();
                        mediaPlayer3.reset();
                        mediaPlayer3.release();
                        mediaPlayer3=null;
                    }
                    if(mediaPlayer4!=null){
                        mediaPlayer4.stop();
                        mediaPlayer4.reset();
                        mediaPlayer4.release();
                        mediaPlayer4=null;
                    }
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_library) {
                    Intent my_lib = new Intent(getApplicationContext(), library.class);
                    my_lib.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_lib);
                    if(mediaPlayer1!=null){
                        mediaPlayer1.stop();
                        mediaPlayer1.reset();
                        mediaPlayer1.release();
                        mediaPlayer1=null;
                    }
                    if(mediaPlayer2!=null){
                        mediaPlayer2.stop();
                        mediaPlayer2.reset();
                        mediaPlayer2.release();
                        mediaPlayer2=null;
                    }
                    if(mediaPlayer3!=null){
                        mediaPlayer3.stop();
                        mediaPlayer3.reset();
                        mediaPlayer3.release();
                        mediaPlayer3=null;
                    }
                    if(mediaPlayer4!=null){
                        mediaPlayer4.stop();
                        mediaPlayer4.reset();
                        mediaPlayer4.release();
                        mediaPlayer4=null;
                    }
                    finish();
                }
                return false;
            }
        });

       // Toast.makeText(getApplicationContext(), "" + ss.getMaxItemCount(), Toast.LENGTH_LONG).show();

        mPlayer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.setVisibility(View.VISIBLE);

                checkItem1 = 1;
            }
        });
        mAdapter.setOnItemClickListener(new MixerRecyclerAdapter.OnItemClickListner() {
            @Override
            public void OnItemClicked(int position) {
                if (checkItem1 == 1) {
                    //if (mediaPlayer1 == null) {
                    if(mediaIntent1==0){
                        isPause=1;
                        mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(position));
                        Toast.makeText(getApplicationContext(), "Media 1 Lock and Loaded... ", Toast.LENGTH_LONG).show();
                        //mediaPlayer1.start();
                        mediaPlayer1.setVolume(vol1, vol1);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        checkItem1 = 0;
                        mPlayer1.setImageResource(mixerPlayListItems.get(position).getmImageView());
                        playButton.setVisibility(View.INVISIBLE);
                        pauseButton.setVisibility(View.VISIBLE);
                        //putting int to sendit to service
                        mediaIntent1 = audioFileArrayChill.get(position);
                        sendtoService();
                    }
                    //if (mediaPlayer1 != null && mediaPlayer1.isPlaying() && checkItem1 == 1) {
                    if(mediaIntent1!=0){
                        isPause=1;
                        mediaPlayer1.stop();
                        mediaPlayer1.reset();
                        mediaPlayer1.release();
                        mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(position));
                        Toast.makeText(getApplicationContext(), "Media 1 ReLock and Loaded... ", Toast.LENGTH_LONG).show();
                        //mediaPlayer1.start();
                        mediaPlayer1.setVolume(vol1, vol1);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        checkItem1 = 0;
                        mPlayer1.setImageResource(mixerPlayListItems.get(position).getmImageView());
                        playButton.setVisibility(View.INVISIBLE);
                        pauseButton.setVisibility(View.VISIBLE);
                        //sending to service
                        mediaIntent1 = audioFileArrayChill.get(position);
                        sendtoService();
                    }
                }
                ///////////////////////////////////////////////////////////////
                if (checkItem2 == 1) {
                   // if (mediaPlayer2 == null) {
                    if (mediaIntent2==0){
                        isPause=1;
                        mediaPlayer2 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(position));
                        Toast.makeText(getApplicationContext(), "Media 2 Lock and Loaded... ", Toast.LENGTH_LONG).show();
                        //mediaPlayer2.start();
                        mediaPlayer2.setVolume(vol2, vol2);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        checkItem2 = 0;
                        mPlayer2.setImageResource(mixerPlayListItems.get(position).getmImageView());
                        playButton.setVisibility(View.INVISIBLE);
                        pauseButton.setVisibility(View.VISIBLE);
                        //sending to service
                        mediaIntent2 = audioFileArrayChill.get(position);
                        sendtoService();
                    }
                    //if (mediaPlayer2 != null && mediaPlayer2.isPlaying() && checkItem2 == 1) {
                    if(mediaIntent2!=0){
                        isPause=1;
                        mediaPlayer2.stop();
                        mediaPlayer2.reset();
                        mediaPlayer2.release();
                        mediaPlayer2 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(position));
                        Toast.makeText(getApplicationContext(), "Media 2 ReLock and Loaded... ", Toast.LENGTH_LONG).show();
                        mediaPlayer2.setVolume(vol2, vol2);
                        //mediaPlayer2.start();
                        mediaPlayer2.setVolume(vol2, vol2);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        checkItem2 = 0;
                        mPlayer2.setImageResource(mixerPlayListItems.get(position).getmImageView());
                        playButton.setVisibility(View.INVISIBLE);
                        pauseButton.setVisibility(View.VISIBLE);
                        //sending to service
                        mediaIntent2 = audioFileArrayChill.get(position);
                        sendtoService();
                    }
                }
                /////////////////////////////////////////////////////////////////////////
                if (checkItem3 == 1) {
                    //if (mediaPlayer3 == null) {
                    if(mediaIntent3==0){
                        isPause=1;
                        mediaPlayer3 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(position));
                        Toast.makeText(getApplicationContext(), "Media 3 Lock and Loaded... ", Toast.LENGTH_LONG).show();
                        //mediaPlayer3.start();
                        mediaPlayer3.setVolume(vol2, vol2);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        mPlayer3.setImageResource(mixerPlayListItems.get(position).getmImageView());
                        checkItem3 = 0;
                        playButton.setVisibility(View.INVISIBLE);
                        pauseButton.setVisibility(View.VISIBLE);
                        //sending to service
                        mediaIntent3 = audioFileArrayChill.get(position);
                        sendtoService();
                    }
                   // if (mediaPlayer3 != null && mediaPlayer3.isPlaying() && checkItem3 == 1) {
                    if(mediaIntent3!=0){
                        isPause=1;
                        mediaPlayer3.stop();
                        mediaPlayer3.reset();
                        mediaPlayer3.release();
                        mediaPlayer3 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(position));
                        Toast.makeText(getApplicationContext(), "Media 3 ReLock and Loaded... ", Toast.LENGTH_LONG).show();
                        mediaPlayer3.setVolume(vol3, vol3);
                        //mediaPlayer3.start();
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        checkItem3 = 0;
                        mPlayer3.setImageResource(mixerPlayListItems.get(position).getmImageView());
                        playButton.setVisibility(View.INVISIBLE);
                        pauseButton.setVisibility(View.VISIBLE);
                        //sending to service
                        mediaIntent3 = audioFileArrayChill.get(position);
                        sendtoService();
                    }
                }
                /////////////////////////////////////////////////////////////////////////
                if (checkItem4 == 1) {
                    //if (mediaPlayer4 == null) {
                    if(mediaIntent4==0){
                        isPause=1;
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(position));
                        Toast.makeText(getApplicationContext(), "Media 4 Lock and Loaded... ", Toast.LENGTH_LONG).show();
                        //mediaPlayer4.start();
                        mediaPlayer4.setVolume(vol4, vol4);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        mPlayer4.setImageResource(mixerPlayListItems.get(position).getmImageView());
                        checkItem4 = 0;
                        playButton.setVisibility(View.INVISIBLE);
                        pauseButton.setVisibility(View.VISIBLE);
                        //sending to service
                        mediaIntent4 = audioFileArrayChill.get(position);
                        sendtoService();
                    }
                    //if (mediaPlayer4 != null && mediaPlayer4.isPlaying() && checkItem4 == 1) {
                    if(mediaIntent4!=0){
                        isPause=1;
                        mediaPlayer4.stop();
                        mediaPlayer4.reset();
                        mediaPlayer4.release();
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(position));
                        Toast.makeText(getApplicationContext(), "Media 4 ReLock and Loaded... ", Toast.LENGTH_LONG).show();
                        mediaPlayer4.setVolume(vol4, vol4);
                        //mediaPlayer4.start();
                        mediaPlayer4.setVolume(vol4, vol4);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        checkItem4 = 0;
                        mPlayer4.setImageResource(mixerPlayListItems.get(position).getmImageView());
                        playButton.setVisibility(View.INVISIBLE);
                        pauseButton.setVisibility(View.VISIBLE);
                        //sending to service
                        mediaIntent4 = audioFileArrayChill.get(position);
                        sendtoService();
                    }
                }
                ///////////////////////////////////////////////////////////////////////////////////
            }
        });

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer1 != null) {
                    float volume = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                    //mediaPlayer1.setVolume(volume, volume);
                    vol1 = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                    seekbar1 = new Intent("mixer_clicked");
                    //seekbar1.setAction(FILTER_ACTION_KEY);
                    seekbar1.putExtra("id", 2002);
                    seekbar1.putExtra("value1",vol1);

                    p1 =PendingIntent.getBroadcast(getApplicationContext(), 100090, seekbar1, 0);
                    try {
                        p1.send(getApplicationContext(),100090,seekbar1);
                        //need to cancel otherwise value will not be updated
                        p1.cancel();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ////////////////////////////////////////
        mPlayer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.setVisibility(View.VISIBLE);

                checkItem2 = 1;

            }
        });
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer2 != null) {
                    float volume = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                    mediaPlayer2.setVolume(volume, volume);
                    vol2 = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                    seekbar2 = new Intent("mixer_clicked");
                    //seekbar1.setAction(FILTER_ACTION_KEY);
                    seekbar2.putExtra("id", 2003);
                    seekbar2.putExtra("value2",vol2);

                    p2 =PendingIntent.getBroadcast(getApplicationContext(), 100091, seekbar2, 0);
                    try {
                        p2.send(getApplicationContext(),100091,seekbar2);
                        //need to cancel otherwise value will not be updated
                        p2.cancel();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
/////////////////////////////////////////////////
        mPlayer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.setVisibility(View.VISIBLE);

                checkItem3 = 1;

            }
        });
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer3 != null) {
                    float volume = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                    mediaPlayer3.setVolume(volume, volume);
                    vol3 = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                    seekbar3 = new Intent("mixer_clicked");
                    //seekbar1.setAction(FILTER_ACTION_KEY);
                    seekbar3.putExtra("id", 2004);
                    seekbar3.putExtra("value3",vol3);

                    p3 =PendingIntent.getBroadcast(getApplicationContext(), 100092, seekbar3, 0);
                    try {
                        p3.send(getApplicationContext(),100092,seekbar3);
                        //need to cancel otherwise value will not be updated
                        p3.cancel();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
///////////////////////////////////////////////////////////////////////
        mPlayer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.setVisibility(View.VISIBLE);

                checkItem4 = 1;

            }
        });
        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer4 != null) {
                    float volume = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                    mediaPlayer4.setVolume(volume, volume);
                    vol4 = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                    seekbar4 = new Intent("mixer_clicked");
                    //seekbar1.setAction(FILTER_ACTION_KEY);
                    seekbar4.putExtra("id", 2005);
                    seekbar4.putExtra("value4",vol4);

                    p4 =PendingIntent.getBroadcast(getApplicationContext(), 100093, seekbar4, 0);
                    try {
                        p4.send(getApplicationContext(),100093,seekbar4);
                        //need to cancel otherwise value will not be updated
                        p4.cancel();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
/////////////////////////////////////////////////////////////////////////////////////////
        try {
            seekBarMain.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            seekBarMain.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            seekBarMain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
///////////////////////////////////////

        lockedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent =new Intent(Mixer.this,InApp_Billing.class);
                //startActivity(intent);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    playButton.setVisibility(View.INVISIBLE);
                    pauseButton.setVisibility(View.VISIBLE);
                   // if (mediaPlayer1 != null) {
                     //   mediaPlayer1.start();
                    //}
                    //if (mediaPlayer2 != null) {
                      //  mediaPlayer2.start();
                    //}
                   // if (mediaPlayer3 != null) {
                     //   mediaPlayer3.start();
                    //}
                    //if (mediaPlayer4 != null) {
                      //  mediaPlayer4.start();
                    //}
                    if(isPause==0) {
                       getApplicationContext().startService(service);
                        isPause = 1;
                    }else {
                        getApplicationContext().stopService(service);
                        getApplicationContext().startService(service);
                        isPause=0;
                    }
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                //if(mediaPlayer1!=null&&mediaPlayer1.isPlaying()){
                  //  mediaPlayer1.pause();
                //}
                //if(mediaPlayer2!=null&&mediaPlayer2.isPlaying()){
                  //  mediaPlayer2.pause();
                //}
               // if(mediaPlayer3!=null&&mediaPlayer3.isPlaying()){
                 //   mediaPlayer3.pause();
                //}
                //if(mediaPlayer4!=null&&mediaPlayer4.isPlaying()){
                  //  mediaPlayer4.pause();
                //}

               getApplicationContext().stopService(service);
            }

        });
        ///intialize on create
       intializeplayonStart();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            if(mediaPlayer1!=null){
                mediaPlayer1.stop();
                mediaPlayer1.reset();
                mediaPlayer1.release();
                mediaPlayer1=null;
            }
            if(mediaPlayer2!=null){
                mediaPlayer2.stop();
                mediaPlayer2.reset();
                mediaPlayer2.release();
                mediaPlayer2=null;
            }
            if(mediaPlayer3!=null){
                mediaPlayer3.stop();
                mediaPlayer3.reset();
                mediaPlayer3.release();
                mediaPlayer3=null;
            }
            if(mediaPlayer4!=null){
                mediaPlayer4.stop();
                mediaPlayer4.reset();
                mediaPlayer4.release();
                mediaPlayer4=null;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.v("KeyUpPressed", event.toString());
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            try {
                seekBarMain.setMax(audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                seekBarMain.setProgress(audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC));


                seekBarMain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                progress, 0);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            try {
                seekBarMain.setMax(audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                seekBarMain.setProgress(audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC));


                seekBarMain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                progress, 0);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        return true;
    }
    private void intializeplayonStart() {
        isPause=1;
    mediaPlayer1 = MediaPlayer.create(getApplicationContext(), R.raw.aac_sleep_to_rain_thunder_sounds_relaxing_thunderstorm_white);
    mediaPlayer2 = MediaPlayer.create(getApplicationContext(), R.raw.aac_25_dakika_huzur_sakinlestici_sesi);
    playButton.setVisibility(View.INVISIBLE);
    pauseButton.setVisibility(View.VISIBLE);
    if (mediaPlayer1 != null || mediaPlayer2 != null) {
        service = new Intent(getApplicationContext(), MixerPlayService.class);
        service.putExtra("media", R.raw.aac_sleep_to_rain_thunder_sounds_relaxing_thunderstorm_white);
        service.putExtra("media2", R.raw.aac_25_dakika_huzur_sakinlestici_sesi);
        startService(service);
    }
        if (mediaPlayer1 != null) {
            //mediaPlayer1.start();

        }
        if (mediaPlayer2 != null) {
            //mediaPlayer2.start();
        }
        if (mediaPlayer3 != null) {
            //mediaPlayer3.start();
        }
        if (mediaPlayer4 != null) {
            //mediaPlayer4.start();
        }

    }

    @Override
    protected void onStart() {
        setReceiver();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }



    private void setReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FILTER_ACTION_KEY);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intentFilter);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("broadcastMessage");



            Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();
            Log.v("Mixer RECEIVED:", "" + message);
            if (!message.equals("null")) {
                int reqcode = Integer.parseInt(message);
                Log.v("Mixer Parsed:", "" + reqcode);
                //FROM NOTIFICATION CODE
                switch (reqcode) {
                    case 20:{

                        }
                        break;
                    case 22:


                        break;
                    case 23:{
                    }

                    break;
                    case 25 :{
                        if(isPause==0) {
                            playButton.setVisibility(View.VISIBLE);
                            pauseButton.setVisibility(View.INVISIBLE);
                            isPause=1;
                        }else {
                            playButton.setVisibility(View.INVISIBLE);
                            pauseButton.setVisibility(View.VISIBLE);
                            isPause=0;
                        }
                    }
                    break;
                    case 27:{

                    }

                    break;
                    case 28:{

                    }
                    break;
                    case 2121: {
                        getApplicationContext().stopService(service);
                    }
                    break;
                    case 2002:{

                        Log.v("Mixer Activity VOL1:", "" + vol1);
                       // mediaPlayer1.setVolume(vol1,vol1);
                    }
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(service);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }
    private void sendtoService(){
        if(isPause==1){
            stopService(service);
            service =new Intent(this,MixerPlayService.class);
            if(mediaIntent1!=0) {
                service.putExtra("seekbar1progress",(float)seekBar1.getProgress());
                service.putExtra("media", mediaIntent1);
            }
            if(mediaIntent2!=0) {
                service.putExtra("seekbar2progress",(float)seekBar2.getProgress());
                service.putExtra("media2", mediaIntent2);
            }
            if(mediaIntent3!=0) {
                service.putExtra("seekbar3progress",(float)seekBar3.getProgress());
                service.putExtra("media3", mediaIntent3);
            }
            if(mediaIntent4!=0) {
                service.putExtra("seekbar4progress",(float)seekBar4.getProgress());
                service.putExtra("media4", mediaIntent4);
            }
            startService(service);
            isPause=0;
        }
    }
}