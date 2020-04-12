package com.alephlabs.whitenoise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Noises extends AppCompatActivity implements SeekBar.OnKeyListener {
    private ViewPager2 viewPager2;
    TextView changingCaption;
    int postion;
    AudioManager audioManager;
    int sliderSize;
    int sliderPosition;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private List<Integer> audioFileArrayChill;
    private ArrayList<String> texts = new ArrayList<>();
    BroadcastReceiver updateUIReciver;
    int Requestcodefromnotificaion;
    int checkService = 0;
    int isPause=0;
    MyReceiver myReceiver;
    public static final String FILTER_ACTION_KEY = "any_key";
    ImageButton playBtn,pauseBtn;
    Intent mediaIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noises);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        audioFileArrayChill = new ArrayList<Integer>();

        BottomNavigationView ss = findViewById(R.id.bottom_nav);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("287BB39D093D5E59376400796EDFCC57").build();
        mAdView.loadAd(adRequest);
        final Menu menu = ss.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        ImageButton nextBtn = findViewById(R.id.nextBtn);
        ImageButton prevBtn = findViewById(R.id.prev_btn);
        playBtn = findViewById(R.id.play_btn_white);
        pauseBtn = findViewById(R.id.pause_btn);
        ImageButton plusPlaylist = findViewById(R.id.plus_playlist_button);
        changingCaption = findViewById(R.id.changing_captionTextview);
        seekBar = findViewById(R.id.seekBar);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        try {
            seekBar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            seekBar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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


        viewPager2 = findViewById(R.id.viewPagerImageSlider);
        final List<SliderItem> sliderItems = new ArrayList<>();

////////////////////////////add new data here..................


        texts.add("Camp Fire");
        texts.add("Clock Ticiking");
        texts.add("Ney");
        texts.add("Fire");
        texts.add("Fire Works");
        texts.add("Bamboo Water Drops");
        texts.add("Rain");
        texts.add("Travelling Starship");
        texts.add("Rocket Launch");
        texts.add("Air Conditioner");
        texts.add("Birds");
        texts.add("Jungle");
        texts.add("Shower");
        texts.add("Rain on Tent");
        texts.add("Native American Flute");
        texts.add("Studying");
        texts.add("Fan");
        texts.add("Mental Relaxation");
        texts.add("Air Plane");
        texts.add("Relaxation For Babies");
        texts.add("Hair Dryer");

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
        audioFileArrayChill.add(R.raw.aac_airplane_white_noise1);
        audioFileArrayChill.add(R.raw.aac_soothe_your_crying_baby_8_hours);
        audioFileArrayChill.add(R.raw.aac_white_noise_for_babies_blow_dryer_10_hours_relaxing_video_sleep_aide_hair_dryer);

        sliderItems.add(new SliderItem(R.drawable.calm_waters_camp_camping));
        sliderItems.add(new SliderItem(R.drawable.thomas_bormans_unsplash));
        sliderItems.add(new SliderItem(R.drawable.adult_architecture_dome));
        sliderItems.add(new SliderItem(R.drawable.michael_shannon_unsplash));
        sliderItems.add(new SliderItem(R.drawable.zuza_galczynska_unsplash));
        sliderItems.add(new SliderItem(R.drawable.tomoko_uji_unsplash));
        sliderItems.add(new SliderItem(R.drawable.inge_maria_unsplash));
        sliderItems.add(new SliderItem(R.drawable.astronomy_cosmos_exploration));
        sliderItems.add(new SliderItem(R.drawable.astronomy_earth_explosion));
        sliderItems.add(new SliderItem(R.drawable.air_conditioner_architecture_building));
        sliderItems.add(new SliderItem(R.drawable.animals_avian_birds));
        sliderItems.add(new SliderItem(R.drawable.cascade_creek_environment));
        sliderItems.add(new SliderItem(R.drawable.bath_bathroom_chrome));
        sliderItems.add(new SliderItem(R.drawable.background_blue_clouds));
        sliderItems.add(new SliderItem(R.drawable.back_light_dark_face_paint));
        sliderItems.add(new SliderItem(R.drawable.black_background_brain_close_up));
        sliderItems.add(new SliderItem(R.drawable.black_black_and_white_close_up));
        sliderItems.add(new SliderItem(R.drawable.adult_ancient));
        sliderItems.add(new SliderItem(R.drawable.air_plane));
        sliderItems.add(new SliderItem(R.drawable.adult_baby_bed));
        sliderItems.add(new SliderItem(R.drawable.barber_bathrobe_bathroom));
        viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2));
        freeMemory();


        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        //int i=viewPager2.getCurrentItem();
        //viewPager2.setCurrentItem(i+1,true);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.20f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        //intial text

        changingCaption.setText(texts.get(postion));
//start playing song on created
        startPlayingSong();


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
                try {
                  //  if (mediaPlayer != null) {
                    //    mediaPlayer.reset();
                      //  mediaPlayer = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(postion));
                       // mediaPlayer.start();
                        //playBtn.setVisibility(View.GONE);
                        //pauseBtn.setVisibility(View.VISIBLE);
                    //}
                    if (checkService==1) {
                        isPause=0;
                        getApplicationContext().stopService(mediaIntent);
                        mediaIntent = new Intent(getApplicationContext(),MyIntentServiceBgPlay.class);
                        mediaIntent.putExtra("media",audioFileArrayChill.get(postion));
                        mediaIntent.putExtra("position",postion);
                        getApplicationContext().startService(mediaIntent);
                        playBtn.setVisibility(View.GONE);
                        pauseBtn.setVisibility(View.VISIBLE);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.i("Message:", "" + e.getMessage());
                }
                //     try {
                //       changingCaption.setText(texts[viewPager2.getCurrentItem() ]);

                // }
                //catch(ArrayIndexOutOfBoundsException exception) {
                //  Log.i("Message:",""+exception.getMessage());
                //}

            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
                try {
                 //   if (mediaPlayer != null) {
                   //     mediaPlayer.reset();

                     //   mediaPlayer = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(postion));
                       // mediaPlayer.start();
                        //playBtn.setVisibility(View.GONE);
                        //pauseBtn.setVisibility(View.VISIBLE);
                    //}
                    if (checkService==1) {
                        isPause=0;
                        getApplicationContext().stopService(mediaIntent);
                        mediaIntent = new Intent(getApplicationContext(),MyIntentServiceBgPlay.class);
                        mediaIntent.putExtra("media",audioFileArrayChill.get(postion));
                        mediaIntent.putExtra("position",postion);
                        getApplicationContext().startService(mediaIntent);
                        playBtn.setVisibility(View.GONE);
                        pauseBtn.setVisibility(View.VISIBLE);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.i("Message:", "" + e.getMessage());
                }

                //try {
                //  changingCaption.setText(texts[viewPager2.getCurrentItem() ]);

                //}
                //catch(ArrayIndexOutOfBoundsException exception) {
                //Log.i("Message:",""+exception.getMessage());
                //}
            }

        });
        ss.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_home) {
                    onBackPressed();
                    finish();
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
                if (menuItem.getItemId() == R.id.navigation_music) {
                }
                if (menuItem.getItemId() == R.id.navigation_mixer) {
                    Intent my_mixer = new Intent(getApplicationContext(), Mixer.class);
                    my_mixer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_mixer);
                    finish();
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
                if (menuItem.getItemId() == R.id.navigation_record) {
                    Intent my_rec = new Intent(getApplicationContext(), recording.class);
                    my_rec.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_rec);
                    finish();
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
                if (menuItem.getItemId() == R.id.navigation_library) {
                    Intent my_lib = new Intent(getApplicationContext(), library.class);
                    my_lib.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_lib);
                    finish();
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
                return false;
            }
        });

        // Toast.makeText(getApplicationContext(), "" + ss.getMaxItemCount(), Toast.LENGTH_LONG).show();

        ///

        final int[] ms = {0};
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(viewPager2.getCurrentItem()));
                mediaIntent = new Intent(getApplicationContext(),MyIntentServiceBgPlay.class);
                mediaIntent.putExtra("media",audioFileArrayChill.get(viewPager2.getCurrentItem()));
                mediaIntent.putExtra("position",viewPager2.getCurrentItem());
                //if (mediaPlayer.isPlaying() && mediaPlayer != null) {
                  //  mediaPlayer.seekTo(ms[0]);
                    //mediaPlayer.start();
                    //getApplicationContext().startService(mediaIntent);

                //} else
                //if (mediaPlayer != null) {
                    //mediaPlayer.start();

                    if(checkService==0) {
                        getApplicationContext().startService(mediaIntent);
                        checkService = 1;
                        pauseBtn.setVisibility(View.VISIBLE);
                        playBtn.setVisibility(View.INVISIBLE);
                    }
                    else if(checkService==1){
                        isPause=0;
                        getApplicationContext().stopService(mediaIntent);
                        getApplicationContext().startService(mediaIntent);
                        pauseBtn.setVisibility(View.VISIBLE);
                        playBtn.setVisibility(View.INVISIBLE);
                    }
                else {
                    Toast.makeText(getApplicationContext(), "No Media found !", Toast.LENGTH_LONG).show();
                }

            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBtn.setVisibility(View.VISIBLE);
                pauseBtn.setVisibility(View.GONE);
                //mediaPlayer.pause();
                getApplicationContext().stopService(mediaIntent);
                ms[0] = mediaPlayer.getCurrentPosition();

            }
        });

        plusPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = changingCaption.getText().toString();
                Log.v("Text Got:", text);
                Intent fromNoises = new Intent(Noises.this, playListSettings.class);
                fromNoises.putExtra("fromnoises", text);
                startActivity(fromNoises);
            }
        });

        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playBtn.setVisibility(View.VISIBLE);
                    pauseBtn.setVisibility(View.GONE);
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            });
        }


       sliderSize = sliderItems.size();
        ////
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                sliderPosition=position;
                try {
                    if (position ==audioFileArrayChill.size()-1) {
                        texts.addAll(texts);
                        audioFileArrayChill.addAll(audioFileArrayChill);
                        freeMemory();
                        changingCaption.setText(texts.get(position));
                        Log.v("ADDEDACTIVITY::","ADDED");

                    } else if (position <= sliderItems.size()&&position<=audioFileArrayChill.size()-1) {
                        changingCaption.setText(texts.get(position));
                    }
                    try {

                       // if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                         //   mediaPlayer.stop();
                           // mediaPlayer.reset();
                            //mediaPlayer = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(position));
                            //mediaPlayer.start();
                            //playBtn.setVisibility(View.GONE);
                            //pauseBtn.setVisibility(View.VISIBLE);
                        //}
                        if (checkService==1) {
                            isPause=0;
                            getApplicationContext().stopService(mediaIntent);
                            mediaIntent = new Intent(getApplicationContext(),MyIntentServiceBgPlay.class);
                            mediaIntent.putExtra("media",audioFileArrayChill.get(position));
                            mediaIntent.putExtra("position",position);
                            getApplicationContext().startService(mediaIntent);
                            playBtn.setVisibility(View.GONE);
                            pauseBtn.setVisibility(View.VISIBLE);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.i("Message:", "" + e.getMessage());
                    }

                } catch (ArrayIndexOutOfBoundsException exception) {
                    Log.i("Message:", "" + exception.getMessage());
                }
            }
        });
///////////////////////////////////////////////


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.v("KeyDown Pressed", event.toString());
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            try {
                seekBar.setMax(audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                seekBar.setProgress(audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC));


                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                seekBar.setMax(audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                seekBar.setProgress(audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC));


                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    public boolean onKeyUp(int keyCode, KeyEvent event) {

        Log.v("KeyUpPressed", event.toString());
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            try {
                seekBar.setMax(audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                seekBar.setProgress(audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC));


                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                seekBar.setMax(audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                seekBar.setProgress(audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC));


                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        return false;
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
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("broadcastMessage");
            Toast.makeText(context,""+message,Toast.LENGTH_LONG).show();
            Log.v("NOISES RECEIVED:",""+message);
            if (message != null) {
                int reqcode = Integer.parseInt(message);
                Log.v("NOISES Parsed:",""+reqcode);
                //FROM NOTIFICATION CODE
                switch (reqcode){
                    case 20:
                        if(isPause==0&&checkService==1) {
                            isPause=1;
                            playBtn.setVisibility(View.VISIBLE);
                            pauseBtn.setVisibility(View.INVISIBLE);
                        }
                        else if(isPause==1&&checkService==1){
                            isPause=0;
                            pauseBtn.setVisibility(View.VISIBLE);
                            playBtn.setVisibility(View.INVISIBLE);

                        }
                        break;
                    case 22:
                        nextfromNotification();
                        break;
                    case 23:
                        prevfromNotification();
                        break;
                    case 25:{
                        if(isPause==0&&checkService==1) {
                            isPause=1;
                            playBtn.setVisibility(View.VISIBLE);
                            pauseBtn.setVisibility(View.INVISIBLE);
                        }
                        else if(isPause==1&&checkService==1){
                            isPause=0;
                            pauseBtn.setVisibility(View.VISIBLE);
                            playBtn.setVisibility(View.INVISIBLE);

                        }
                        break;
                    }
                    case 27:{
                        nextfromNotification();
                        break;
                    }
                    case 28:{
                        prevfromNotification();
                        break;
                    }
                    case 2121:{
                        //on cancel event
                        stopService(mediaIntent);
                        playBtn.setVisibility(View.VISIBLE);
                        pauseBtn.setVisibility(View.INVISIBLE);
                    }
                }

            }
        }
    }
private void nextfromNotification(){

    if (checkService==1) {
        isPause=0;
        viewPager2.setCurrentItem(viewPager2.getCurrentItem() +1);
        getApplicationContext().stopService(mediaIntent);
        mediaIntent = new Intent(getApplicationContext(),MyIntentServiceBgPlay.class);
        mediaIntent.putExtra("media",audioFileArrayChill.get(postion));
        getApplicationContext().startService(mediaIntent);
        playBtn.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.VISIBLE);
    }
}
private void prevfromNotification(){
    if (checkService==1) {
        isPause=0;
        viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
        getApplicationContext().stopService(mediaIntent);
        mediaIntent = new Intent(getApplicationContext(),MyIntentServiceBgPlay.class);
        mediaIntent.putExtra("media",audioFileArrayChill.get(postion));
        getApplicationContext().startService(mediaIntent);
        playBtn.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.VISIBLE);
    }
}
private void startPlayingSong(){
    mediaPlayer = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(viewPager2.getCurrentItem()));
    mediaIntent = new Intent(getApplicationContext(),MyIntentServiceBgPlay.class);
    mediaIntent.putExtra("media",audioFileArrayChill.get(viewPager2.getCurrentItem()));
//if (mediaPlayer.isPlaying() && mediaPlayer != null) {
    //  mediaPlayer.seekTo(ms[0]);
    //mediaPlayer.start();
    //getApplicationContext().startService(mediaIntent);

    //} else
    //if (mediaPlayer != null) {
    //mediaPlayer.start();

    if(checkService==0) {
        getApplicationContext().startService(mediaIntent);
        checkService = 1;
        pauseBtn.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.INVISIBLE);
    }
    else if(checkService==1){
        isPause=0;
        getApplicationContext().stopService(mediaIntent);
        getApplicationContext().startService(mediaIntent);
        pauseBtn.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.INVISIBLE);
    }
    else {
        Toast.makeText(getApplicationContext(), "No Media found !", Toast.LENGTH_LONG).show();
    }
}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mediaIntent);
    }

}


