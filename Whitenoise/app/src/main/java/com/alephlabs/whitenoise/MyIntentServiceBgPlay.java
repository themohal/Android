package com.alephlabs.whitenoise;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentServiceBgPlay extends IntentService {
    public int i;
    int position;
    int next=0;
    int prev =0;
    MediaPlayer mediaPlayer;
    MyReceiver myReceiver;
    public static final String FILTER_ACTION_KEY = "any_key";
    RemoteViews playerView, playerView2;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    int NOTIFICATION_ID = 001;
    int isPause = 0;
    int duration;
    int mProgressStatus = 0;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    Notification notification = new Notification();
    private List<Integer> audioFileArrayChill=new ArrayList<Integer>();
    int check=0;
    int tempNext;
    public MyIntentServiceBgPlay() {
        super("MyIntentServiceBgPlay");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    @Override
    protected void onHandleIntent(Intent intent) {


    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            addSongstoList();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                i = Objects.requireNonNull(intent.getIntExtra("media", 0));
                position=intent.getIntExtra("position",0);

                Log.v("id is", "" + i);
                Log.v("Position is", "" + position);
                next=position;
                prev=position-1;
                if(position==audioFileArrayChill.size()-1){
                    audioFileArrayChill.addAll(audioFileArrayChill);
                }
                mediaPlayer = MediaPlayer.create(getApplicationContext(), i);
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    Log.v("MEDIA NOT NULL:", "Service started");
                    duration = mediaPlayer.getDuration();
                    @SuppressLint("DefaultLocale")
                    String time = String.format("%02d min, %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                    mediaPlayer.start();
                    onCompleteListener();
                    displayNotification();
                }
            }
            Log.v("ON START Command", "Service started");
            setReceiver();


        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        mediaPlayer.pause();
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                notificationManager.cancel(001);
                if (mHandler != null) {
                    mHandler.removeCallbacks(mRunnable);

                }
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
        Intent playButton = new Intent("button_clicked");
        playButton.putExtra("id", playButtonId1);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 10001, playButton, 0);
        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationPlayButton, pendingIntent1);
        //PauseButton
        Intent pauseButton = new Intent("button_clicked");
        pauseButton.putExtra("id", pauseButtonId1);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 10002, pauseButton, 0);
        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationPauseButton, pendingIntent2);
        //nextButton
        Intent nextButton = new Intent("button_clicked");
        nextButton.putExtra("id", nextButtonId1);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(this, 10003, nextButton, 0);
        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationNextButton, pendingIntent3);
        //prevButton
        Intent prevButton = new Intent("button_clicked");
        prevButton.putExtra("id", prevButtonId1);
        PendingIntent pendingIntent4 = PendingIntent.getBroadcast(this, 10004, prevButton, 0);

        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationPreviousButton, pendingIntent4);

//////////for playerview2

        //playbutton2
        Intent playButton2 = new Intent("button_clicked");
        playButton2.putExtra("id", playButtonId2);
        PendingIntent pendingIntent5 = PendingIntent.getBroadcast(this, 10005, playButton2, 0);
        //biinding to remoteview
        playerView2.setOnClickPendingIntent(R.id.notificationPlayButton, pendingIntent5);
        //nextButton2
        Intent nextButton2 = new Intent("button_clicked");
        nextButton2.putExtra("id", nextButtonId2);
        PendingIntent pendingIntent6 = PendingIntent.getBroadcast(this, 10006, nextButton2, 0);
        //biinding to remoteview
        playerView2.setOnClickPendingIntent(R.id.notificationNextButton, pendingIntent6);
        //prevButton
        Intent prevButton2 = new Intent("button_clicked");
        prevButton2.putExtra("id", prevButtonId2);
        PendingIntent pendingIntent7 = PendingIntent.getBroadcast(this, 10007, prevButton2, 0);
        //biinding to remoteview
        playerView2.setOnClickPendingIntent(R.id.notificationPreviousButton, pendingIntent7);
        //update progresss
        notification.contentView = playerView2;
        //setupProgress();



        //Now make and define broadcast receiver class from manifest then creat class on the same name and extend with broadcast receiver
        builder = new NotificationCompat.Builder(this);

        // NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        //notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());
        //to go to the activity which has sent notification
        Intent notificationIntent = new Intent(this, Noises.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100212, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //delete event
        Intent onCancelIntent = new Intent(this, notificationplayButton.class);
        onCancelIntent.putExtra("id", 2121);
        PendingIntent onDismissPendingIntent = PendingIntent.getBroadcast(this, 100021, onCancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setCustomContentView(playerView2); //smaller layout or collapsed
      //  builder.setCustomBigContentView(playerView); //big layout or expanded
        builder.setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT);

        int current_progress = 0;
        builder.setProgress(mediaPlayer.getDuration() / 1000, current_progress, false);
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

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("broadcastMessage");
            Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();
            Log.v("RECEIVED:", "" + message);
            if (message != null) {
                int reqcode = Integer.parseInt(message);
                Log.v("Parsed:", "" + reqcode);
                //FROM NOTIFICATION CODE
                switch (reqcode) {
                    case 20:
                        if (mediaPlayer.isPlaying() && isPause == 0) {
                            mediaPlayer.pause();
                            isPause = 1;
                            Log.v("Notification Button:", "Pause");
                            if (notificationManager != null) {
                                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                                notificationManager.notify(NOTIFICATION_ID, builder.build());
                                if (mHandler != null) {
                                    mHandler.removeCallbacks(mRunnable);

                                }
                            }
                        } else if (!mediaPlayer.isPlaying() && isPause == 1) {
                            mediaPlayer.start();
                            isPause = 0;
                            Log.v("Notification Button:", "Play");
                            if (notificationManager != null) {
                                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                                notificationManager.notify(NOTIFICATION_ID, builder.build());
                                //setupProgress();
                            }
                        }
                        break;
                    case 22:


                        break;
                    case 23:{
                        if (notificationManager != null) {
                            //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                            playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                            if(prev>=0){
                                next=next-1;
                                prevFromNotificationBg();
                                prev=prev-1;
                            }
                        }
                        }

                        break;
                    case 25 :{
                        if (mediaPlayer.isPlaying() && isPause == 0) {
                            mediaPlayer.pause();
                            isPause = 1;
                            Log.v("Notification Button:", "Pause");
                            if (notificationManager != null) {
                                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                                notificationManager.notify(NOTIFICATION_ID, builder.build());
                                if (mHandler != null) {
                                    mHandler.removeCallbacks(mRunnable);
                                }
                            }
                        } else if (!mediaPlayer.isPlaying() && isPause == 1) {
                            mediaPlayer.start();
                            isPause = 0;
                            Log.v("Notification Button:", "Play");
                            if (notificationManager != null) {
                                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                                notificationManager.notify(NOTIFICATION_ID, builder.build());
                                //setupProgress();
                            }
                        }
                    }
                    break;
                    case 27:{
                        if (notificationManager != null) {
                            //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                            playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                            if(next>=0) {
                                next += 1;
                                prev = next - 1;
                                nextFromNotificationBg();
                                onCompleteListener();
                            }
                        }
                        }

                        break;
                    case 28:{
                        if(prev>=0){
                            next=next-1;
                            prevFromNotificationBg();
                            onCompleteListener();
                            prev=prev-1;
                        }
                    }
                        break;
                    case 2121: {
                        onDestroy();
                    }
                }

            }
        }
    }

    private void setupProgress(){
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (notificationManager != null) {
                    notification.contentView.setProgressBar(R.id.progressNotification, mediaPlayer.getDuration() / 1000, mediaPlayer.getCurrentPosition() / 1000, false);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
                mHandler.postDelayed(mRunnable,3000);
            }
        };
        mHandler.postDelayed(mRunnable,3000);
    }
    private void addSongstoList(){

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
    }

    private void nextFromNotificationBg() {
        if(next<audioFileArrayChill.size()) {
            if(next==audioFileArrayChill.size()-1){
                audioFileArrayChill.addAll(audioFileArrayChill);
                Log.v("NEXT:","NEXT");
            }
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(next));
                mediaPlayer.start();
        }
    }
    private void prevFromNotificationBg() {
        if (prev >= 0) {
            if(prev>=audioFileArrayChill.size()){
                audioFileArrayChill.addAll(audioFileArrayChill);
                Log.v("PREV:","PREV");
            }
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), audioFileArrayChill.get(prev));
                    mediaPlayer.start();
            }
    }
    private void onCompleteListener(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
    }
}

