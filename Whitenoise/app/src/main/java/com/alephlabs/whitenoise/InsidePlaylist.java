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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class InsidePlaylist extends AppCompatActivity {
    private String playlistName;
    private TextView playlistText, songTitle;
    private ImageButton playInsidePlaylist,pauseInsidePlaylist,nextPlaylist,prevPlaylist;
    private Intent insideplaylistIntent;
    private ImageView coverImage;
    private int[] chknxt = {0};
    private int[] chkprev = {0};
/////////////////////////////
    private RecyclerView mRecylerView;
    private PlayListInsideAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<PlayListInsideItems> mixerPlayListItems ;
    private List<Integer> audioFileArrayChill;
    //////////////////////////
    private RecyclerView mRecylerView2;
    private MixerRecyclerAdapter mAdapter2;
    private RecyclerView.LayoutManager mLayoutManager2;
    private ArrayList<MixerPlayListItems> mixerPlayListItems2 ;
    private List<Integer> audioFileArrayChill2;
    private List <Integer> imageList;
    //////////////////////////////
private SeekBar seekInsidePlayList;

private String shuffleName="";
    private MediaPlayer mPlayer;
    private Handler mHandler;
    private Runnable mRunnable;
    String str;
    int checkService =0;
    MyReceiver myReceiver;
    int isPause=0;
    public static final String FILTER_ACTION_KEY = "any_key";
    private Intent seekProgressIntent;
    private PendingIntent seekProgressPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_playlist);
        final RelativeLayout seekLayout= findViewById(R.id.seekLayout);
        // Initialize the handler
        mHandler = new Handler();
        ImageButton backFromInsidePlaylist = findViewById(R.id.play_list_inside_back_button);
        playlistText = findViewById(R.id.inside_playlist_name);
        coverImage = findViewById(R.id.cover_Image);
        songTitle = findViewById(R.id.song_title_inside);
        playInsidePlaylist= findViewById(R.id.play_button_insideplaylist);
        pauseInsidePlaylist = findViewById(R.id.pause_button_insideplaylist);
        nextPlaylist =findViewById(R.id.next_Btn_insidePlaylist);
        prevPlaylist = findViewById(R.id.prev_btn_insidePlaylist);
        Button shufflePlay= findViewById(R.id.shuffle_play);
        seekInsidePlayList = findViewById(R.id.inside_playlist_seekbar);

        playlistText.setText(playlistName);
        ImageButton addSongToPlayListt =findViewById(R.id.play_list_add);
       ScrollView scrollView =findViewById(R.id.scroll_view);
        Log.v("Clicked:",""+playlistName);
        Intent intent = getIntent();
        str= intent.getStringExtra("clicked");
        String noises= intent.getStringExtra("fromNoises");
        if (noises != null) {
            Log.v("Noises to Inside Plist:",noises);
        }

        playlistText.setText(str);

        scrollView.pageScroll(ScrollView.FOCUS_UP);
/////////////////////////////////////////Menu
        BottomNavigationView ss = (BottomNavigationView) findViewById(R.id.bottom_nav);
        Menu menu = ss.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
        ss.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_home) {
                    stopPlaying();
                    Intent my_home = new Intent(getApplicationContext(), MainActivity.class);
                    my_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_home);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_music) {
                    stopPlaying();
                    Intent my_noises = new Intent(getApplicationContext(), Noises.class);
                    my_noises.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_noises);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_mixer) {
                    stopPlaying();
                    Intent my_mixer = new Intent(getApplicationContext(),Mixer.class);
                    my_mixer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(my_mixer);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_record) {
                    stopPlaying();
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
        //////////////////////add new data here
        imageList =new ArrayList<>();
        imageList.add(R.drawable.air_plane);
        imageList.add(R.drawable.adult_baby_bed);
        imageList.add(R.drawable.barber_bathrobe_bathroom);
        imageList.add(R.drawable.calm_waters_camp_camping);
        imageList.add(R.drawable.thomas_bormans_unsplash);
        imageList.add(R.drawable.adult_architecture_dome);
        imageList.add(R.drawable.michael_shannon_unsplash);
        imageList.add(R.drawable.zuza_galczynska_unsplash);
        imageList.add(R.drawable.tomoko_uji_unsplash);
        imageList.add(R.drawable.inge_maria_unsplash);
        imageList.add(R.drawable.astronomy_cosmos_exploration);
        imageList.add(R.drawable.astronomy_earth_explosion);
        imageList.add(R.drawable.air_conditioner_architecture_building);
        imageList.add(R.drawable.animals_avian_birds);
        imageList.add(R.drawable.cascade_creek_environment);
        imageList.add(R.drawable.bath_bathroom_chrome);
        imageList.add(R.drawable.background_blue_clouds);
        imageList.add(R.drawable.back_light_dark_face_paint);
        imageList.add(R.drawable.black_background_brain_close_up);
        imageList.add(R.drawable.black_black_and_white_close_up);
        imageList.add(R.drawable.adult_ancient);
/////////////////////////////////////add new data here
        mixerPlayListItems = new ArrayList<PlayListInsideItems>();
        audioFileArrayChill = new ArrayList<Integer>();
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

        mRecylerView = (RecyclerView) findViewById(R.id.recyclerView_playlist_Inside);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecylerView.setLayoutManager(mLayoutManager);
        mAdapter = new PlayListInsideAdapter(mixerPlayListItems);
        mRecylerView.setAdapter(mAdapter);
        InitializeListInside();
        ////////////////////////////////////////////////////////Add new data here
        mixerPlayListItems2 = new ArrayList<MixerPlayListItems>();
        audioFileArrayChill2 = new ArrayList<Integer>();
        audioFileArrayChill2.add(R.raw.aac_airplane_white_noise1);
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.air_plane,"Air Plane"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.adult_baby_bed,"Relaxation For Babies"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.barber_bathrobe_bathroom,"Hair Dryer"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.calm_waters_camp_camping,"Camp Fire"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.thomas_bormans_unsplash,"Clock Ticking"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.adult_architecture_dome,"Ney"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.michael_shannon_unsplash,"Fire"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.zuza_galczynska_unsplash,"Fire Works"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.tomoko_uji_unsplash,"Bamboo Water Drops"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.inge_maria_unsplash,"Rain"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.astronomy_cosmos_exploration,"Travelling Starship"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.astronomy_earth_explosion,"Rocket Launch"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.air_conditioner_architecture_building,"Air Conditioner"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.animals_avian_birds,"Birds"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.cascade_creek_environment,"Jungle"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.bath_bathroom_chrome,"Shower"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.background_blue_clouds,"Rain On Tent"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.back_light_dark_face_paint,"Native American Flute"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.black_background_brain_close_up,"Studying"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.black_black_and_white_close_up,"Fan"));
        mixerPlayListItems2.add(new MixerPlayListItems(R.drawable.adult_ancient,"Mental Relaxation"));
        mRecylerView2 = (RecyclerView) findViewById(R.id.recyclerView_playlist_Inside2);
        // use a linear layout manager
        mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        mRecylerView2.setLayoutManager(mLayoutManager2);
        // specify an adapter (see also next example)
        mAdapter2 = new MixerRecyclerAdapter(mixerPlayListItems2);
        mRecylerView2.setAdapter(mAdapter2);
        if(noises!=null) {
            int checkAlready=0;
            for (int o = 0; o < mixerPlayListItems.size(); o++) {
                if (mixerPlayListItems.get(o).getmText1().equals(noises)) {
                    Toast.makeText(getApplicationContext(), "Already in playlist...", Toast.LENGTH_LONG).show();
                    checkAlready = 1;
                }
            }
            for (int i = 0; i < mixerPlayListItems2.size(); i++) {

                if(mixerPlayListItems2.get(i).getmText1().equals(noises)&& checkAlready==0) {
                    mixerPlayListItems.add(new PlayListInsideItems(mixerPlayListItems2.get(i).getmImageView(), noises));
                    mAdapter.notifyDataSetChanged();
                    calltoShared();
                }
            }
        }
        //
      ////////////////////////////////
        addSongToPlayListt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             mRecylerView2.setVisibility(View.VISIBLE);
                seekLayout.setVisibility(View.INVISIBLE);

            }
        });
        mAdapter2.setOnItemClickListener(new MixerRecyclerAdapter.OnItemClickListner() {
            @Override
            public void OnItemClicked(int position) {
                int checkAlready=0;
                if(mixerPlayListItems.isEmpty()||!mixerPlayListItems.isEmpty()) {
                    for (int y = 0; y < mixerPlayListItems.size(); y++) {
                        if (mixerPlayListItems.get(y).getmText1().equals(mixerPlayListItems2.get(position).getmText1())) {
                            Toast.makeText(getApplicationContext(), "Already in playlist...", Toast.LENGTH_LONG).show();
                            checkAlready=1;
                        }
                    }
                    if(checkAlready==0) {
                        mixerPlayListItems
                                .add(new PlayListInsideItems(mixerPlayListItems2.get(position).getmImageView(),
                                        mixerPlayListItems2.get(position).getmText1()));
                        mAdapter.notifyDataSetChanged();
                        if(mAdapter.getItemCount()>0) {
                            coverImage.setImageResource(mixerPlayListItems.get(0).getmImageView());
                            songTitle.setText(mixerPlayListItems.get(0).getmText1());
                        }
                        calltoShared();
                    }

                }

                mRecylerView2.setVisibility(View.INVISIBLE);
                seekLayout.setVisibility(View.VISIBLE);

            }
        });
        ////////////////////////
        mAdapter.setOnItemClickListener(new PlayListInsideAdapter.OnItemClickListner() {
            @Override
            public void OnItemClicked(int position) {
                pauseInsidePlaylist.setVisibility(View.VISIBLE);
                playInsidePlaylist.setVisibility(View.INVISIBLE);
                coverImage.setImageResource(mixerPlayListItems.get(position).getmImageView());
                songTitle.setText(mixerPlayListItems.get(position).getmText1());
                chknxt[0] = position+1;
                chkprev[0] = position-1;
                String name;
                name=mixerPlayListItems.get(position).getmText1();
                if(name.equals("Air Plane")) {
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(0));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Air Plane");
                }
                else if (name.equals("Relaxation For Babies")){
                    //   stopPlaying();
                    //  mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(1));
                    // mPlayer.start();
                    intializeSongOnPlayButton("Relaxation For Babies");
                }
                else if (name.equals("Hair Dryer")){
                    //  stopPlaying();
                    // mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(2));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Hair Dryer");
                }
                else if (name.equals("Camp Fire")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(3));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Camp Fire");
                }
                else if (name.equals("Clock Ticking")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(4));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Clock Ticking");
                }
                else if (name.equals("Ney")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(5));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Ney");
                }
                else if (name.equals("Fire")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(6));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Fire");
                }
                else if (name.equals("Fire Works")){
                    // stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(7));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Fire Works");
                }
                else if (name.equals("Bamboo Water Drops")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(8));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Bamboo Water Drops");
                }
                else if (name.equals("Rain")){
                    // stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(9));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Rain");
                }
                else if (name.equals("Travelling Starship")){
                    //  stopPlaying();
                    // mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(10));
                    // mPlayer.start();
                    intializeSongOnPlayButton("Travelling Starship");
                }
                else if (name.equals("Rocket Launch")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(11));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Rocket Launch");

                }
                else if (name.equals("Air Conditioner")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(12));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Air Conditioner");
                }
                else if (name.equals("Birds")){
                    //  stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(13));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Birds");
                }
                else if (name.equals("Jungle")){
                    // stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(14));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Jungle");
                }
                else if (name.equals("Shower")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(15));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Shower");
                }
                else if (name.equals("Rain On Tent")){
                    //  stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(16));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Rain On Tent");
                }
                else if (name.equals("Native American Flute")){
                    // stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(17));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Native American Flute");
                }
                else if (name.equals("Studying")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(18));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Studying");
                }
                else if (name.equals("Fan")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(19));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Fan");
                }
                else if (name.equals("Mental Relaxation")){
                    // stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(20));
                    // mPlayer.start();
                    intializeSongOnPlayButton("Mental Relaxation");
                }


                // Get the current audio stats
                getAudioStats();
                // Initialize the seek bar
                initializeSeekBar();
            }
        });
        ////////////////
        if(mAdapter.getItemCount()>0) {
            coverImage.setImageResource(mixerPlayListItems.get(0).getmImageView());
            songTitle.setText(mixerPlayListItems.get(0).getmText1());
        }
        /////////////
        playInsidePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If media player another instance already running then stop it first
                stopPlaying();
                if(mAdapter.getItemCount()>0) {
                    pauseInsidePlaylist.setVisibility(View.VISIBLE);
                    playInsidePlaylist.setVisibility(View.INVISIBLE);
                    // Initialize media player
                    intializeSongOnPlayButton(mixerPlayListItems.get(0).getmText1());
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            pauseInsidePlaylist.setVisibility(View.INVISIBLE);
                            playInsidePlaylist.setVisibility(View.VISIBLE);

                            if (seekInsidePlayList.getProgress() == 100) {
                              //  mPlayer.pause();
                                if (mHandler != null) {
                                    mHandler.removeCallbacks(mRunnable);
                                }
                            }
                        }
                    });

                }
            }
        });
        /////////////////////////////
        pauseInsidePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playInsidePlaylist.setVisibility(View.VISIBLE);
                pauseInsidePlaylist.setVisibility(View.INVISIBLE);
                stopPlaying();
                stopService(insideplaylistIntent);
                if (mHandler != null) {
                    mHandler.removeCallbacks(mRunnable);
                }

            }
        });

        //////////////////////////////////

        nextPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAdapter.getItemCount()>0) {


                    if (chknxt[0] < mixerPlayListItems.size()) {
                        if(chknxt[0]==0){
                            chkprev[0]=mixerPlayListItems.size();
                        }
                        pauseInsidePlaylist.setVisibility(View.VISIBLE);
                        playInsidePlaylist.setVisibility(View.INVISIBLE);
                        // Initialize media player
                        intializeSongOnPlayButton(mixerPlayListItems.get(chknxt[0]).getmText1());
                        coverImage.setImageResource(mixerPlayListItems.get(chknxt[0]).getmImageView());
                        songTitle.setText(mixerPlayListItems.get(chknxt[0]).getmText1());
                        chkprev[0] = chknxt[0] - 1;
                        chknxt[0] += 1;
                        if (mHandler != null) {
                            mHandler.removeCallbacks(mRunnable);
                            seekInsidePlayList.setProgress(0);
                        }
                        if(chknxt[0]==mixerPlayListItems.size()){
                            chknxt[0]=0;
                        }
                    }
                }
            }
        });
        prevPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getItemCount() > 0) {

                    if (chkprev[0] >= 0) {
                        pauseInsidePlaylist.setVisibility(View.VISIBLE);
                        playInsidePlaylist.setVisibility(View.INVISIBLE);
                        // Initialize media player
                        intializeSongOnPlayButton(mixerPlayListItems.get(chkprev[0]).getmText1());
                        coverImage.setImageResource(mixerPlayListItems.get(chkprev[0]).getmImageView());
                        songTitle.setText(mixerPlayListItems.get(chkprev[0]).getmText1());

                        chknxt[0] = chkprev[0] + 1;
                        chkprev[0] -= 1;
                        if (mHandler != null) {
                            mHandler.removeCallbacks(mRunnable);
                            seekInsidePlayList.setProgress(0);
                        }

                        if(chknxt[0]==0){
                            chkprev[0]=mixerPlayListItems.size();
                        }
                    }


                }
            }
        });
        shufflePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rand = new Random();
                PlayListInsideItems select =mixerPlayListItems.get(rand.nextInt(mixerPlayListItems.size()));
                Log.v("Random Select:",""+select.getmText1());
                    pauseInsidePlaylist.setVisibility(View.VISIBLE);
                    playInsidePlaylist.setVisibility(View.INVISIBLE);
                    coverImage.setImageResource(select.getmImageView());
                    songTitle.setText(select.getmText1());
                if (mHandler != null) {
                    mHandler.removeCallbacks(mRunnable);
                    seekInsidePlayList.setProgress(0);
                }
                    String name;
                    name = select.getmText1();
                if(name.equals("Air Plane")) {
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(0));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Air Plane");
                }
                else if (name.equals("Relaxation For Babies")){
                 //   stopPlaying();
                  //  mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(1));
                   // mPlayer.start();
                    intializeSongOnPlayButton("Relaxation For Babies");
                }
                else if (name.equals("Hair Dryer")){
                  //  stopPlaying();
                   // mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(2));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Hair Dryer");
                }
                else if (name.equals("Camp Fire")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(3));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Camp Fire");
                }
                else if (name.equals("Clock Ticking")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(4));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Clock Ticking");
                }
                else if (name.equals("Ney")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(5));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Ney");
                }
                else if (name.equals("Fire")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(6));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Fire");
                }
                else if (name.equals("Fire Works")){
                   // stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(7));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Fire Works");
                }
                else if (name.equals("Bamboo Water Drops")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(8));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Bamboo Water Drops");
                }
                else if (name.equals("Rain")){
                   // stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(9));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Rain");
                }
                else if (name.equals("Travelling Starship")){
                  //  stopPlaying();
                   // mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(10));
                   // mPlayer.start();
                    intializeSongOnPlayButton("Travelling Starship");
                }
                else if (name.equals("Rocket Launch")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(11));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Rocket Launch");

                }
                else if (name.equals("Air Conditioner")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(12));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Air Conditioner");
                }
                else if (name.equals("Birds")){
                  //  stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(13));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Birds");
                }
                else if (name.equals("Jungle")){
                   // stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(14));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Jungle");
                }
                else if (name.equals("Shower")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(15));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Shower");
                }
                else if (name.equals("Rain On Tent")){
                  //  stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(16));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Rain On Tent");
                }
                else if (name.equals("Native American Flute")){
                   // stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(17));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Native American Flute");
                }
                else if (name.equals("Studying")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(18));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Studying");
                }
                else if (name.equals("Fan")){
                    //stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(19));
                    //mPlayer.start();
                    intializeSongOnPlayButton("Fan");
                }
                else if (name.equals("Mental Relaxation")){
                   // stopPlaying();
                    //mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(20));
                   // mPlayer.start();
                    intializeSongOnPlayButton("Mental Relaxation");
                }

                // Get the current audio stats
                getAudioStats();
                // Initialize the seek bar
                initializeSeekBar();

            }
        });
        //////////////////////////////////
        seekInsidePlayList.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mPlayer!=null && fromUser){
                    /*
                        void seekTo (int msec)
                            Seeks to specified time position. Same as seekTo(long, int)
                            with mode = SEEK_PREVIOUS_SYNC.

                        Parameters
                            msec int: the offset in milliseconds from the start to seek to

                        Throws
                            IllegalStateException : if the internal player engine has not been initialized
                    */
                    //mPlayer.seekTo(progress*1000);
                    seekInsidePlayList.setProgress(progress);
                    seekProgressIntent = new Intent("playlist_clicked");
                    seekProgressIntent.putExtra("id", 70004);
                    seekProgressIntent.putExtra("seekBarChange",progress*1000);
                    seekProgressPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100100, seekProgressIntent, 0);
                    try {
                        seekProgressPendingIntent.send(getApplicationContext(), 100100, seekProgressIntent);
                        //need to cancel otherwise value will not be updated
                        seekProgressPendingIntent.cancel();
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
    backFromInsidePlaylist.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    });

    }
    /////////////////////////////////////////////////////////////////////////////////////////////
    public void calltoShared() {
        ArrayList<String> read = new ArrayList<>();
        if (!mixerPlayListItems.isEmpty()) {
            read.clear();
            for (int j = 0; j < mixerPlayListItems.size(); j++) {
                read.add(j,mixerPlayListItems.get(j).getmText1());
                Log.v("Readed: ", "" + read.get(j));

            }

            Set<String> set = new HashSet<>();
            set.addAll(read);
            SharedPreferences keyValues = getApplicationContext().getSharedPreferences("playlist_"+str, Context.MODE_PRIVATE);
            SharedPreferences.Editor keyValuesEditor = keyValues.edit();
            keyValuesEditor.clear();
            keyValuesEditor.putStringSet("playlist_"+str, set);
            keyValuesEditor.apply();
            SharedPreferences readkeyValues = getApplicationContext().getSharedPreferences("playlist_"+str, Context.MODE_PRIVATE);
            Set<String> readSet;
            readSet = readkeyValues.getStringSet("playlist_"+str, null);
            ArrayList<String> sharedPrefList = new ArrayList<>();
            sharedPrefList.addAll(readSet);


        }
    }
    private void InitializeListInside() {
        SharedPreferences readkeyValues = getApplicationContext().getSharedPreferences("playlist_"+str, Context.MODE_PRIVATE);
        Set<String> readSet;
        readSet = readkeyValues.getStringSet("playlist_"+str, null);

        ArrayList<String> sharedPrefList = new ArrayList<>();
        if(readSet!=null) {
            sharedPrefList.addAll(readSet);
            Collections.sort(sharedPrefList);

        }
        String s = "";
////Add new data here
        for (int i = 0; i < sharedPrefList.size(); i++) {
            Log.v("Readed From Shared:", sharedPrefList.get(i));
            s = sharedPrefList.get(i);
            if (mixerPlayListItems.isEmpty()||!mixerPlayListItems.isEmpty()) {
                if(s.equals("Air Plane")) {
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.air_plane, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Relaxation For Babies")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.adult_baby_bed, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Hair Dryer")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.barber_bathrobe_bathroom, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Camp Fire")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.calm_waters_camp_camping, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Clock Ticking")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.thomas_bormans_unsplash, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Ney")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.adult_architecture_dome, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Fire")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.michael_shannon_unsplash, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Fire Works")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.zuza_galczynska_unsplash, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Bamboo Water Drops")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.tomoko_uji_unsplash, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Rain")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.inge_maria_unsplash, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Travelling Starship")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.astronomy_cosmos_exploration, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Rocket Launch")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.astronomy_earth_explosion, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Air Conditioner")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.air_conditioner_architecture_building, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Birds")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.animals_avian_birds, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Jungle")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.cascade_creek_environment, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Shower")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.bath_bathroom_chrome, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Rain On Tent")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.background_blue_clouds, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Native American Flute")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.back_light_dark_face_paint, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Studying")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.black_background_brain_close_up, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Fan")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.black_black_and_white_close_up, s));
                    mAdapter.notifyDataSetChanged();
                }
                else if (s.equals("Mental Relaxation")){
                    mixerPlayListItems.add(i, new PlayListInsideItems(R.drawable.adult_ancient, s));
                    mAdapter.notifyDataSetChanged();
                }
                ///////////////////////is empty ends here

            }



        }
    }
    protected void stopPlaying() {
        // If media player is not null then try to stop it
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            Toast.makeText(this, "Stop playing.", Toast.LENGTH_SHORT).show();
            if (mHandler != null) {
                mHandler.removeCallbacks(mRunnable);
            }
        }
    }
    protected void getAudioStats(){
        int duration  = mPlayer.getDuration()/1000; // In milliseconds
        int due = (mPlayer.getDuration() - mPlayer.getCurrentPosition())/1000;
        int pass = duration - due;


    }
    protected void initializeSeekBar(){
        seekInsidePlayList.setMax(mPlayer.getDuration()/1000);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mPlayer!=null){
                    int mCurrentPosition = mPlayer.getCurrentPosition()/1000; // In milliseconds
                    seekInsidePlayList.setProgress(mCurrentPosition);
                    getAudioStats();
                }
                mHandler.postDelayed(mRunnable,1000);
            }
        };
        mHandler.postDelayed(mRunnable,1000);
    }
    private void intializeSongOnPlayButton(String playName){
        String name=playName;
        if(name.equals("Air Plane")) {
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(0));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(0));
            insideplaylistIntent.putExtra("clicked",str);
            startService(insideplaylistIntent);
            checkService=1;
        }

        else if (name.equals("Relaxation For Babies")){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(1));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(1));
            insideplaylistIntent.putExtra("clicked",str);
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Hair Dryer")){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(2));
            //mPlayer.start();
                insideplaylistIntent = new Intent(this, PlaylistService.class);
                insideplaylistIntent.putExtra("media", audioFileArrayChill.get(2));
            insideplaylistIntent.putExtra("clicked",str);
                startService(insideplaylistIntent);

            checkService=1;
        }
        else if (name.equals("Camp Fire")){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(3));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(3));
            insideplaylistIntent.putExtra("clicked",str);
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Clock Ticking")){
            if(checkService==1) {
                stopService(insideplaylistIntent);
                checkService = 0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(4));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(4));
            insideplaylistIntent.putExtra("clicked",str);
            startService(insideplaylistIntent);
            checkService=1;
        }
       else if (name.equals("Ney")){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(5));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(5));
            insideplaylistIntent.putExtra("clicked",str);
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Fire")){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(6));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(6));
            insideplaylistIntent.putExtra("clicked",str);
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Fire Works")){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(7));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(7));
            insideplaylistIntent.putExtra("clicked",str);
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Bamboo Water Drops")){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(8));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(8));
            insideplaylistIntent.putExtra("clicked",str);
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Rain")){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(9));
            //mPlayer.start();
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(9));
            insideplaylistIntent.putExtra("clicked",str);
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Travelling Starship")){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(10));
            //mPlayer.start();
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(10));
            insideplaylistIntent.putExtra("clicked",str);
            startService(insideplaylistIntent);
            checkService=1;
        }
       else if (name.equals("Rocket Launch")){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(11));
            //mPlayer.start();
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("clicked",str);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(11));
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Air Conditioner")){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(12));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("clicked",str);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(12));
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Birds") ){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(13));
            //mPlayer.start();
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("clicked",str);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(13));
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Jungle") ){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(14));
            //mPlayer.start();
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("clicked",str);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(14));
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Shower") ){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(15));
            //mPlayer.start();
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("clicked",str);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(15));
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Rain On Tent") ){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(16));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("clicked",str);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(16));
            startService(insideplaylistIntent);
            checkService=1;
        }
        else if (name.equals("Native American Flute") ){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(17));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("clicked",str);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(17));
            startService(insideplaylistIntent);
            checkService=1;
        }
      else if (name.equals("Studying") ){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(18));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("clicked",str);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(18));
            startService(insideplaylistIntent);
           checkService=1;
       }
      else if (name.equals("Fan") ){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
            stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(19));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("clicked",str);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(19));
            startService(insideplaylistIntent);
          checkService=1;
      }
       else if (name.equals("Mental Relaxation") ){
            if(checkService==1){
                stopService(insideplaylistIntent);
                checkService=0;
            }
           stopPlaying();
            mPlayer = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(20));
            //mPlayer.start();
            insideplaylistIntent =new Intent(this,PlaylistService.class);
            insideplaylistIntent.putExtra("clicked",str);
            insideplaylistIntent.putExtra("media",audioFileArrayChill.get(20));
            startService(insideplaylistIntent);
           checkService=1;
       }

        // Get the current audio stats
        getAudioStats();
        // Initialize the seek bar
        initializeSeekBar();


    }

    @Override
    public void onBackPressed() {
        stopPlaying();
        if(checkService==1) {
            stopService(insideplaylistIntent);
        }
        super.onBackPressed();
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
            String progressChange = intent.getStringExtra("broadcastMessage2");
            //Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();
            Log.v("Playlist Activity::", "" + message);
            if (message != null && !message.equals("null")) {
                int reqcode = Integer.parseInt(message);
                Log.v("Playlist Activity::", "" + reqcode);
                //FROM NOTIFICATION CODE
                switch (reqcode) {
                    case 20: {

                    }
                    break;
                    case 22:


                        break;
                    case 23: {
                    }

                    break;
                    case 25: {
                        if (isPause == 0) {
                            playInsidePlaylist.setVisibility(View.VISIBLE);
                            pauseInsidePlaylist.setVisibility(View.INVISIBLE);
                            isPause = 1;
                            if (mHandler != null) {
                                mHandler.removeCallbacks(mRunnable);
                            }
                        } else {
                            playInsidePlaylist.setVisibility(View.INVISIBLE);
                            pauseInsidePlaylist.setVisibility(View.VISIBLE);
                            isPause = 0;
                        }
                    }
                    break;
                    case 27: {
                        if(mAdapter.getItemCount()>0) {


                            if (chknxt[0] < mixerPlayListItems.size()) {
                                if(chknxt[0]==0){
                                    chkprev[0]=mixerPlayListItems.size();
                                }
                                pauseInsidePlaylist.setVisibility(View.VISIBLE);
                                playInsidePlaylist.setVisibility(View.INVISIBLE);
                                coverImage.setImageResource(mixerPlayListItems.get(chknxt[0]).getmImageView());
                                songTitle.setText(mixerPlayListItems.get(chknxt[0]).getmText1());
                                chkprev[0] = chknxt[0] - 1;
                                chknxt[0] += 1;
                                if (mHandler != null) {
                                    mHandler.removeCallbacks(mRunnable);
                                    seekInsidePlayList.setProgress(0);
                                }
                                if(chknxt[0]==mixerPlayListItems.size()){
                                    chknxt[0]=0;
                                }
                            }
                        }
                    }

                    break;
                    case 28: {
                        if (mAdapter.getItemCount() > 0) {

                            if (chkprev[0] >= 0) {
                                pauseInsidePlaylist.setVisibility(View.VISIBLE);
                                playInsidePlaylist.setVisibility(View.INVISIBLE);
                                coverImage.setImageResource(mixerPlayListItems.get(chkprev[0]).getmImageView());
                                songTitle.setText(mixerPlayListItems.get(chkprev[0]).getmText1());

                                chknxt[0] = chkprev[0] + 1;
                                chkprev[0] -= 1;
                                if (mHandler != null) {
                                    mHandler.removeCallbacks(mRunnable);
                                    seekInsidePlayList.setProgress(0);
                                }

                                if(chknxt[0]==0){
                                    chkprev[0]=mixerPlayListItems.size();
                                }
                            }
                        }

                    }
                    break;
                    case 2121: {
                        getApplicationContext().stopService(insideplaylistIntent);
                    }
                    break;
                    case 7003: {

                        final int progress = intent.getExtras().getInt("progressToActivity");
                        Log.v("ProgressActivity::", "" + progress);
                        mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (mPlayer != null) {
                                    seekInsidePlayList.setProgress(progress);
                                }
                                mHandler.postDelayed(mRunnable, 1000);
                            }
                        };
                        mHandler.postDelayed(mRunnable, 1000);

                    }
                    break;
                    case 70004: {
                        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        //   int seekProgress = Objects.requireNonNull(intent.getExtras()).getInt("seekBarChange");
                        // seekInsidePlayList.setProgress(seekProgress);
                        //}
                    }
                    break;
                }
            }
        }}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaying();
        stopService(insideplaylistIntent);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }

    @Override
    protected void onStart() {
        setReceiver();
        super.onStart();
    }
}
