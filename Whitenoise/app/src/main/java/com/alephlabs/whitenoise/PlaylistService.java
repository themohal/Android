package com.alephlabs.whitenoise;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class PlaylistService extends IntentService {
    int i1=0;
    int checkMp1;
    int next,prev=0;
    MediaPlayer mediaPlayer1;
    MyReceiver myReceiver;
    public static final String FILTER_ACTION_KEY = "any_key";
    RemoteViews playerView, playerView2;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    int NOTIFICATION_ID = 001;
    int isPause1=0;
    Notification notification = new Notification();
    private List<Integer> audioFileArrayChill=new ArrayList<Integer>();
    private List<String> playlistAddedSongs = new ArrayList<String>();
    private Handler mHandler;
    private Runnable mRunnable;
    private PendingIntent p1;
    private Intent progressToActivity;


    public PlaylistService() {
        super("PlaylistService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {

            addSongtoAudioList();
            String playlistname = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                playlistname = Objects.requireNonNull(intent).getStringExtra("clicked");
                if (playlistname != null) {
                    Log.v("PlaylistName:",playlistname);
                    InitializeListInside(playlistname);
                    for(int i=0;i<playlistAddedSongs.size();i++) {
                        Log.v("ServicePlaylist::", playlistAddedSongs.get(i));
                    }
                }
            }


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                i1 = Objects.requireNonNull(intent.getIntExtra("media", 0));
                if(i1!=0) {
                    Log.v("id is", "" + i1);
                    mediaPlayer1 = MediaPlayer.create(getApplicationContext(), i1);
                }
                if (mediaPlayer1 != null && !mediaPlayer1.isPlaying()&&checkMp1==0) {
                    Log.v("MEDIA NOT NULL:", "Service started");
                    mediaPlayer1.start();
                    setHandlerforSeekBar();
                    settingOnCompleteListner();
                    displayNotification();
                    checkMp1 = 1;
                }
                }
                Log.v("ON START Command", "Service started");
                setReceiver();


            }
            return START_STICKY;
        }

    @Override
    public void onDestroy() {
        if(mediaPlayer1!=null&&mediaPlayer1.isPlaying()) {
            stopPlaying();
        }
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                notificationManager.cancel(001);
            }
        }
        Log.v("ON Destroyed", "Service Destroyed");

    }

    private void displayNotification() {
        String CHANNEL_ID = "music_player";
        int playButtonId1 = 20;
        int pauseButtonId1 = 21;
        int nextButtonId1 = 22;
        int prevButtonId1 = 23;
        final int progressbarId = 24;

        int playButtonId2 = 25;
        int pauseButtonId2 = 26;
        int nextButtonId2 = 27;
        int prevButtonId2 = 28;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        playerView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        playerView2 = new RemoteViews(getPackageName(), R.layout.notification_collapsed);
        playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
        playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
        //FOR PLAYER VIEW 1 or BIG EXPANDED NOTIFICATION
        //playbutton
        Intent playButton = new Intent("playlist_clicked");
        playButton.putExtra("id", playButtonId1);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 100010, playButton, 0);
        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationPlayButton, pendingIntent1);
        //PauseButton
        Intent pauseButton = new Intent("playlist_clicked");
        pauseButton.putExtra("id", pauseButtonId1);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 100020, pauseButton, 0);
        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationPauseButton, pendingIntent2);
        //nextButton
        Intent nextButton = new Intent("playlist_clicked");
        nextButton.putExtra("id", nextButtonId1);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(this, 100030, nextButton, 0);
        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationNextButton, pendingIntent3);
        //prevButton
        Intent prevButton = new Intent("playlist_clicked");
        prevButton.putExtra("id", prevButtonId1);
        PendingIntent pendingIntent4 = PendingIntent.getBroadcast(this, 100040, prevButton, 0);

        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationPreviousButton, pendingIntent4);

//////////for playerview2

        //playbutton2
        Intent playButton2 = new Intent("playlist_clicked");
        playButton2.putExtra("id", playButtonId2);
        PendingIntent pendingIntent5 = PendingIntent.getBroadcast(this, 100050, playButton2, 0);
        //biinding to remoteview
        playerView2.setOnClickPendingIntent(R.id.notificationPlayButton, pendingIntent5);
        //nextButton2
        Intent nextButton2 = new Intent("playlist_clicked");
        nextButton2.putExtra("id", nextButtonId2);
        PendingIntent pendingIntent6 = PendingIntent.getBroadcast(this, 100060, nextButton2, 0);
        //biinding to remoteview
        playerView2.setOnClickPendingIntent(R.id.notificationNextButton, pendingIntent6);
        //prevButton
        Intent prevButton2 = new Intent("playlist_clicked");
        prevButton2.putExtra("id", prevButtonId2);
        PendingIntent pendingIntent7 = PendingIntent.getBroadcast(this, 100070, prevButton2, 0);
        //biinding to remoteview
        playerView2.setOnClickPendingIntent(R.id.notificationPreviousButton, pendingIntent7);
        //update progresss
        notification.contentView = playerView2;



        //Now make and define broadcast receiver class from manifest then creat class on the same name and extend with broadcast receiver
        builder = new NotificationCompat.Builder(this);

        // NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        //notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());
        //to go to the activity which has sent notification
        Intent notificationIntent = new Intent(this, InsidePlaylist.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1001122, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //delete event
        Intent onCancelIntent = new Intent(this, notificationplayButton.class);
        onCancelIntent.putExtra("id", 2121);
        PendingIntent onDismissPendingIntent = PendingIntent.getBroadcast(this, 1001123, onCancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setCustomContentView(playerView2); //smaller layout or collapsed
        //  builder.setCustomBigContentView(playerView); //big layout or expanded
        builder.setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT);

        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(onDismissPendingIntent);
        builder.setOnlyAlertOnce(true);//to stop from recreating
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
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
            //String vol1= intent.getStringExtra("broadcastMessage2");
            //String vol2= intent.getStringExtra("broadcastMessage3");

            //Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();
            Log.v("Playlist Service::", "" + message);
            if (message != null && !message.equals("null")) {
                int reqcode = Integer.parseInt(message);
                Log.v("Playlist Service::", "" + reqcode);
                //FROM NOTIFICATION CODE
                switch (reqcode) {
                    case 20: {

                    }
                    break;
                    case 22:


                        break;
                    case 23: {
                        if (notificationManager != null) {
                            //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                            playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                        }
                    }

                    break;
                    case 25: {
                        playPause();
                    }
                        break;
                    case 27: {
                        //next
                        if (notificationManager != null) {
                            //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                            playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                            if (next >= playlistAddedSongs.size()) {
                                next = next - 1;
                            }
                            if (next <= playlistAddedSongs.size() - 1) {
                                next += 1;
                                if (next == playlistAddedSongs.size()) {
                                    next = 0;
                                }

                                Log.v("Next::", "" + next);
                                intializeSongOnPlayButton(playlistAddedSongs.get(next));
                                settingOnCompleteListner();
                                prev = next - 1;
                            }
                        }
                        break;
                    }


                    case 28: {
                        if (prev <= playlistAddedSongs.size() - 1) {
                            if (prev < 0) {
                                prev = playlistAddedSongs.size() - 1;
                            }
                            intializeSongOnPlayButton(playlistAddedSongs.get(prev));
                            settingOnCompleteListner();
                            prev -= 1;
                            next = prev + 1;
                        }
                    }
                    break;
                    case 2121: {
                        releaseAll();
                    }
                    break;
                    case 2002: {

                        //Log.v("RECEIVED VOL1:", "" + Float.parseFloat(vol1));
                        //float parsedVol1=   Float.parseFloat(vol1);

                        //mediaPlayer1.setVolume(parsedVol1, parsedVol1);
                    }
                    break;
                    //case 2003:{
                    //  Log.v("RECEIVED VOL2:", "" + Float.parseFloat(vol2));
                    //float parsedVol2=   Float.parseFloat(vol2);

                    // mediaPlayer2.setVolume(parsedVol2, parsedVol2);
                    //}
                    //break;
                    case 70004: {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            int seekProgress = Objects.requireNonNull(intent.getExtras()).getInt("seekBarChange");
                            mediaPlayer1.seekTo(seekProgress);
                        }

                    }
                }

            }
        }}
    private void releaseAll(){
        if(mediaPlayer1!=null){
            mediaPlayer1.stop();
            mediaPlayer1.release();
            mediaPlayer1=null;
        }
    }

    private void intializeSongOnPlayButton(String playName){
        String name=playName;
        if(name.equals("Air Plane")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(0));
            mediaPlayer1.start();
        }

        else if (name.equals("Relaxation For Babies")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(1));
            mediaPlayer1.start();
        }
         else if (name.equals("Hair Dryer")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(2));
            mediaPlayer1.start();
        }
        else  if (name.equals("Camp Fire")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(3));
            mediaPlayer1.start();
        }
        else if (name.equals("Clock Ticking")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(4));
            mediaPlayer1.start();
        }
        else if (name.equals("Ney")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(5));
            mediaPlayer1.start();
        }
        else if (name.equals("Fire")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(6));
            mediaPlayer1.start();
        }
        else if (name.equals("Fire Works")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(7));
            mediaPlayer1.start();
        }
        else if (name.equals("Bamboo Water Drops")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(8));
            mediaPlayer1.start();
        }
        else if (name.equals("Rain")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(9));
            mediaPlayer1.start();
        }
        else if (name.equals("Travelling Starship")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(10));
            mediaPlayer1.start();
        }
        else if (name.equals("Rocket Launch")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(11));
            mediaPlayer1.start();
        }
        else if (name.equals("Air Conditioner")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(12));
            mediaPlayer1.start();
        }
        else if (name.equals("Birds")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(13));
            mediaPlayer1.start();

        }
        else  if (name.equals("Jungle")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(14));
            mediaPlayer1.start();
        }
        else  if (name.equals("Shower")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;

            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(15));
            mediaPlayer1.start();
        }
        else  if (name.equals("Rain On Tent")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(16));
            mediaPlayer1.start();
        }
        else if (name.equals("Native American Flute")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;

            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(17));
            mediaPlayer1.start();
        }
        else if (name.equals("Studying")){
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 =null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(),audioFileArrayChill.get(18));
            mediaPlayer1.start();
        }
        else if (name.equals("Fan")) {
            mediaPlayer1.stop();
            mediaPlayer1.reset();
            mediaPlayer1.release();
            mediaPlayer1 = null;
            mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(19));
            mediaPlayer1.start();
        }
            else if (name.equals("Mental Relaxation")) {
                mediaPlayer1.stop();
                mediaPlayer1.reset();
                mediaPlayer1.release();
                mediaPlayer1 = null;
                mediaPlayer1 = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(20));
                mediaPlayer1.start();

            }
        }
    private void InitializeListInside(String str) {
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
            if (playlistAddedSongs.isEmpty()||!playlistAddedSongs.isEmpty()) {
                if(s.equals("Air Plane")) {
                    playlistAddedSongs.add("Air Plane");

                }
                else if (s.equals("Relaxation For Babies")){
                    playlistAddedSongs.add("Relaxation For Babies");

                }
                else if (s.equals("Hair Dryer")){
                    playlistAddedSongs.add("Hair Dryer");

                }
                else if (s.equals("Camp Fire")){
                    playlistAddedSongs.add("Camp Fire");

                }
                else if (s.equals("Clock Ticking")){
                    playlistAddedSongs.add("Clock Ticking");

                }
                else if (s.equals("Ney")){
                    playlistAddedSongs.add("Ney");

                }
                else if (s.equals("Fire")){
                    playlistAddedSongs.add("Fire");

                }
                else if (s.equals("Fire Works")){
                    playlistAddedSongs.add("Fire Works");

                }
                else if (s.equals("Bamboo Water Drops")){
                    playlistAddedSongs.add("Bamboo Water Drops");

                }
                else if (s.equals("Rain")){
                    playlistAddedSongs.add("Rain");

                }
                else if (s.equals("Travelling Starship")){
                    playlistAddedSongs.add("Travelling Starship");

                }
                else if (s.equals("Rocket Launch")){
                    playlistAddedSongs.add("Rocket Launch");

                }
                else if (s.equals("Air Conditioner")){
                    playlistAddedSongs.add("Air Conditioner");

                }
                else if (s.equals("Birds")){
                    playlistAddedSongs.add("Birds");

                }
                else if (s.equals("Jungle")){
                    playlistAddedSongs.add("Jungle");

                }
                else if (s.equals("Shower")){
                    playlistAddedSongs.add("Shower");

                }
                else if (s.equals("Rain On Tent")){
                    playlistAddedSongs.add("Rain On Tent");

                }
                else if (s.equals("Native American Flute")){
                    playlistAddedSongs.add("Native American Flute");

                }
                else if (s.equals("Studying")){
                    playlistAddedSongs.add("Studying");

                }
                else if (s.equals("Fan")){
                    playlistAddedSongs.add("Fan");

                }
                else if (s.equals("Mental Relaxation")){
                    playlistAddedSongs.add("Mental Relaxation");


                }
                ///////////////////////is empty ends here

            }



        }
    }
    private void addSongtoAudioList(){
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
    }
    private void playPause(){
        if (isPause1 == 0) {
            isPause1=1;
            Log.v("Notification Button:", "Pause");
            if (notificationManager != null) {
                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
                mediaPlayer1.pause();
                if (mHandler != null) {
                    mHandler.removeCallbacks(mRunnable);

                }
            }
        }
        else if(isPause1==1){
            Log.v("Notification Button:", "Play");
            if (notificationManager != null) {
                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
                setHandlerforSeekBar();
                mediaPlayer1.start();
                isPause1=0;
            }
        }
    }
    private void setHandlerforSeekBar(){
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer1 != null) {
                    int mCurrentPosition = mediaPlayer1.getCurrentPosition() / 1000; // In milliseconds
                    progressToActivity = new Intent("playlist_clicked");
                    progressToActivity.putExtra("id", 7003);
                    progressToActivity.putExtra("progressToActivity", mCurrentPosition);
                    p1 = PendingIntent.getBroadcast(getApplicationContext(), 100099, progressToActivity, 0);
                    try {
                        p1.send(getApplicationContext(), 100099, progressToActivity);
                        //need to cancel otherwise value will not be updated
                        p1.cancel();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.postDelayed(mRunnable,1000);
            }
        };
        mHandler.postDelayed(mRunnable,1000);
    }
    private void settingOnCompleteListner(){
        mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mHandler != null) {
                    mHandler.removeCallbacks(mRunnable);
                }
                mediaPlayer1.start();
                setHandlerforSeekBar();

            }
        });
    }
    private void stopPlaying(){
        // If media player is not null then try to stop it
        if (mediaPlayer1 != null) {
            mediaPlayer1.stop();
            mediaPlayer1.release();
            mediaPlayer1 = null;
            Toast.makeText(this, "Stop playing.", Toast.LENGTH_SHORT).show();
            if (mHandler != null) {
                mHandler.removeCallbacks(mRunnable);
            }
    }}
    }
