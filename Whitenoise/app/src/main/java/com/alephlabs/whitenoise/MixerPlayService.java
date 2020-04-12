package com.alephlabs.whitenoise;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.media.ToneGenerator.MAX_VOLUME;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MixerPlayService extends IntentService {
    int i1=0;
    int i2=0;
    int i3=0;
    int i4=0;
    int checkMp1,checkMp2,checkMp3,checkMp4=0;
    MediaPlayer mediaPlayer1,mediaPlayer2,mediaPlayer3,mediaPlayer4;
    MixerPlayService.MyReceiver myReceiver;
    public static final String FILTER_ACTION_KEY = "any_key";
    RemoteViews playerView, playerView2;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    int NOTIFICATION_ID = 001;
    int isPause1,isPause2,isPause3,isPause4 = 0;
    int pauseTime1,pauseTime2,pauseTime3,pauseTime4;
    float seekprogress1,seekprogress2,seekprogress3,seekprogress4;

    Notification notification = new Notification();

    public MixerPlayService() {
        super("MixerPlayService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {


        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                i1 = Objects.requireNonNull(intent.getIntExtra("media", 0));
                i2 = Objects.requireNonNull(intent.getIntExtra("media2", 0));
                i3 = Objects.requireNonNull(intent.getIntExtra("media3", 0));
                i4 = Objects.requireNonNull(intent.getIntExtra("media4", 0));
                if(i1!=0) {
                    mediaPlayer1 = MediaPlayer.create(getApplicationContext(), i1);
                    seekprogress1= Objects.requireNonNull(intent.getExtras()).getFloat("seekbar1progress",0);
                    if(seekprogress1>0.0){
                        mediaPlayer1.setVolume(seekprogress1,seekprogress1);
                    }
                }
                if(i2!=0) {
                    mediaPlayer2 = MediaPlayer.create(getApplicationContext(), i2);
                    seekprogress2= Objects.requireNonNull(intent.getExtras()).getFloat("seekbar2progress",0);
                    if(seekprogress2>0.0){
                        mediaPlayer2.setVolume(seekprogress2,seekprogress2);
                    }
                }
                if(i3!=0) {
                    mediaPlayer3 = MediaPlayer.create(getApplicationContext(), i3);
                    seekprogress3= Objects.requireNonNull(intent.getExtras()).getFloat("seekbar3progress",0);
                    if(seekprogress3>0.0){
                        mediaPlayer3.setVolume(seekprogress3,seekprogress3);
                    }
                }
                if(i4!=0) {
                    mediaPlayer4 = MediaPlayer.create(getApplicationContext(), i4);
                    seekprogress4= Objects.requireNonNull(intent.getExtras()).getFloat("seekbar4progress",0);
                    if(seekprogress4>0.0){
                        mediaPlayer4.setVolume(seekprogress4,seekprogress4);
                    }
                }

                if (mediaPlayer1 != null && !mediaPlayer1.isPlaying()&&checkMp1==0) {
                    Log.v("MEDIA NOT NULL:", "Service started");

                    mediaPlayer1.start();
                    displayNotification();
                    checkMp1=1;
                }
                if(mediaPlayer2 != null && !mediaPlayer2.isPlaying()&&checkMp2==0){
                    mediaPlayer2.start();

                    displayNotification();
                    checkMp1=1;
                }
                if(mediaPlayer3 != null && !mediaPlayer3.isPlaying()&&checkMp3==0){
                    mediaPlayer3.start();
                    displayNotification();
                    checkMp3=1;
                }
                if(mediaPlayer4!= null && !mediaPlayer4.isPlaying()&&checkMp4==0){
                    mediaPlayer4.start();
                    displayNotification();
                    checkMp4=1;
                }
                allOnCompleteListeners();
            }
            Log.v("ON START Command", "Service started");
            setReceiver();


        }
        return START_STICKY;
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
        Intent playButton = new Intent("mixer_clicked");
        playButton.putExtra("id", playButtonId1);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 10001, playButton, 0);
        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationPlayButton, pendingIntent1);
        //PauseButton
        Intent pauseButton = new Intent("mixer_clicked");
        pauseButton.putExtra("id", pauseButtonId1);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 10002, pauseButton, 0);
        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationPauseButton, pendingIntent2);
        //nextButton
        Intent nextButton = new Intent("mixer_clicked");
        nextButton.putExtra("id", nextButtonId1);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(this, 10003, nextButton, 0);
        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationNextButton, pendingIntent3);
        //prevButton
        Intent prevButton = new Intent("mixer_clicked");
        prevButton.putExtra("id", prevButtonId1);
        PendingIntent pendingIntent4 = PendingIntent.getBroadcast(this, 10004, prevButton, 0);

        //biinding to remoteview
        playerView.setOnClickPendingIntent(R.id.notificationPreviousButton, pendingIntent4);

//////////for playerview2

        //playbutton2
        Intent playButton2 = new Intent("mixer_clicked");
        playButton2.putExtra("id", playButtonId2);
        PendingIntent pendingIntent5 = PendingIntent.getBroadcast(this, 10005, playButton2, 0);
        //biinding to remoteview
        playerView2.setOnClickPendingIntent(R.id.notificationPlayButton, pendingIntent5);
        //nextButton2
        Intent nextButton2 = new Intent("mixer_clicked");
        nextButton2.putExtra("id", nextButtonId2);
        PendingIntent pendingIntent6 = PendingIntent.getBroadcast(this, 10006, nextButton2, 0);
        //biinding to remoteview
        playerView2.setOnClickPendingIntent(R.id.notificationNextButton, pendingIntent6);
        //prevButton
        Intent prevButton2 = new Intent("mixer_clicked");
        prevButton2.putExtra("id", prevButtonId2);
        PendingIntent pendingIntent7 = PendingIntent.getBroadcast(this, 10007, prevButton2, 0);
        //biinding to remoteview
        playerView2.setOnClickPendingIntent(R.id.notificationPreviousButton, pendingIntent7);
        //update progresss
        notification.contentView = playerView2;



        //Now make and define broadcast receiver class from manifest then creat class on the same name and extend with broadcast receiver
        builder = new NotificationCompat.Builder(this);

        // NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        //notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());
        //to go to the activity which has sent notification
        Intent notificationIntent = new Intent(this, Mixer.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100212, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //delete event
        Intent onCancelIntent = new Intent(this, MixerBroadcastService.class);
        onCancelIntent.putExtra("id", 2121);
        PendingIntent onDismissPendingIntent = PendingIntent.getBroadcast(this, 100021, onCancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);

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



    @Override
    public void onDestroy() {
        if(mediaPlayer1!=null&&mediaPlayer1.isPlaying()) {
            mediaPlayer1.pause();
            pauseTime1=mediaPlayer1.getCurrentPosition();
        }
        if(mediaPlayer2!=null&&mediaPlayer2.isPlaying()) {
            mediaPlayer2.pause();
            pauseTime2=mediaPlayer2.getCurrentPosition();
        }
        if(mediaPlayer3!=null&&mediaPlayer3.isPlaying()) {
            mediaPlayer3.pause();
            pauseTime3=mediaPlayer3.getCurrentPosition();
        }
        if(mediaPlayer4!=null&&mediaPlayer4.isPlaying()) {
            mediaPlayer4.pause();
            pauseTime4=mediaPlayer4.getCurrentPosition();
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
            String vol1= intent.getStringExtra("broadcastMessage2");
            String vol2= intent.getStringExtra("broadcastMessage3");
            String vol3= intent.getStringExtra("broadcastMessage4");
            String vol4= intent.getStringExtra("broadcastMessage5");

            Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();
            Log.v("Mixer Service RECEIVED:", "" + message);
            if (!message.equals("null")) {
                int reqcode = Integer.parseInt(message);
                Log.v("Mixer Service Parsed:", "" + reqcode);
                //FROM NOTIFICATION CODE
                switch (reqcode) {
                    case 20:{

                        }
                        break;
                    case 22:


                        break;
                    case 23:{
                        if (notificationManager != null) {
                            //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                            playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                    }
                    }

                    break;
                    case 25 :{
                        playpauseMp1();
                        playpauseMp2();
                        playpauseMp3();
                        playpauseMp4();

                    }
                    break;
                    case 27:{
                     //   if (notificationManager != null) {
                            //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                       //     playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                        //    notificationManager.notify(NOTIFICATION_ID, builder.build());
                    //}
                    }

                    break;
                    case 28:{

                    }
                    break;
                    case 2121: {
                        releaseAll();
                    }
                    break;
                    case 2002:{

                        Log.v("RECEIVED VOL1:", "" + Float.parseFloat(vol1));
                        float parsedVol1=   Float.parseFloat(vol1);

                            mediaPlayer1.setVolume(parsedVol1, parsedVol1);
                    }
                    break;
                    case 2003:{
                        Log.v("RECEIVED VOL2:", "" + Float.parseFloat(vol2));
                        float parsedVol2=   Float.parseFloat(vol2);

                        mediaPlayer2.setVolume(parsedVol2, parsedVol2);
                    }
                    break;
                    case 2004:{
                        Log.v("RECEIVED VOL3:", "" + Float.parseFloat(vol3));
                        float parsedVol3=   Float.parseFloat(vol3);

                        mediaPlayer3.setVolume(parsedVol3, parsedVol3);
                    }
                    break;
                    case 2005:{
                        Log.v("RECEIVED VOL4:", "" + Float.parseFloat(vol4));
                        float parsedVol4=   Float.parseFloat(vol4);

                        mediaPlayer4.setVolume(parsedVol4, parsedVol4);
                    }
                    break;
                }

            }
        }
    }
    private void releaseAll(){
        if(mediaPlayer1!=null){
            mediaPlayer1.stop();
            mediaPlayer1.release();
            mediaPlayer1=null;
        }if(mediaPlayer2!=null){
            mediaPlayer2.stop();
            mediaPlayer2.release();
            mediaPlayer2=null;
        }
        if(mediaPlayer3!=null){
            mediaPlayer3.stop();
            mediaPlayer3.release();
            mediaPlayer3=null;
        }
        if(mediaPlayer4!=null){
            mediaPlayer4.stop();
            mediaPlayer4.release();
            mediaPlayer4=null;
        }
    }
    private void playpauseMp1(){
        if (mediaPlayer1!=null&&mediaPlayer1.isPlaying() && isPause1 == 0) {
            mediaPlayer1.pause();
            pauseTime1=mediaPlayer1.getCurrentPosition();
            isPause1 = 1;
            Log.v("Notification Button:", "Pause");
            if (notificationManager != null) {
                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                notificationManager.notify(NOTIFICATION_ID, builder.build());

            }
        } else if (mediaPlayer1!=null&&!mediaPlayer1.isPlaying()&& isPause1 == 1) {
            mediaPlayer1.start();
            if(pauseTime1>0){
                mediaPlayer1.seekTo(pauseTime1);
            }
            isPause1 = 0;
            Log.v("Notification Button:", "Play");
            if (notificationManager != null) {
                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }
    private void playpauseMp2(){
        if (mediaPlayer2!=null&&mediaPlayer2.isPlaying() && isPause2 == 0) {
            mediaPlayer2.pause();
            pauseTime2=mediaPlayer2.getCurrentPosition();
            isPause2 = 1;
            Log.v("Notification Button:", "Pause");
            if (notificationManager != null) {
                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                notificationManager.notify(NOTIFICATION_ID, builder.build());

            }
        } else if (mediaPlayer2!=null&&!mediaPlayer2.isPlaying()&& isPause2 == 1) {
            mediaPlayer2.start();
            if(pauseTime2>0){
                mediaPlayer2.seekTo(pauseTime2);
            }
            isPause2 = 0;
            Log.v("Notification Button:", "Play");
            if (notificationManager != null) {
                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }
    private void playpauseMp3(){
        if (mediaPlayer3!=null&&mediaPlayer3.isPlaying() && isPause3 == 0) {
            mediaPlayer3.pause();
            pauseTime3=mediaPlayer3.getCurrentPosition();
            isPause3 = 1;
            Log.v("Notification Button:", "Pause");
            if (notificationManager != null) {
                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                notificationManager.notify(NOTIFICATION_ID, builder.build());

            }
        } else if (mediaPlayer3!=null&&!mediaPlayer3.isPlaying()&& isPause3 == 1) {
            mediaPlayer3.start();
            if(pauseTime3>0){
                mediaPlayer3.seekTo(pauseTime3);
            }
            isPause3 = 0;
            Log.v("Notification Button:", "Play");
            if (notificationManager != null) {
                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }
    private void playpauseMp4(){
        if (mediaPlayer4!=null&&mediaPlayer4.isPlaying() && isPause4 == 0) {
            mediaPlayer4.pause();
            pauseTime4=mediaPlayer4.getCurrentPosition();
            isPause4 = 1;
            Log.v("Notification Button:", "Pause");
            if (notificationManager != null) {
                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.play_button_white);
                notificationManager.notify(NOTIFICATION_ID, builder.build());

            }
        } else if (mediaPlayer4!=null&&!mediaPlayer4.isPlaying()&& isPause4 == 1) {
            mediaPlayer4.start();
            if(pauseTime4>0){
                mediaPlayer4.seekTo(pauseTime4);
            }
            isPause4 = 0;
            Log.v("Notification Button:", "Play");
            if (notificationManager != null) {
                //playerView.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                playerView2.setImageViewResource(R.id.notificationPlayButton, R.drawable.pause_symbol_whitex);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }
private void allOnCompleteListeners(){
        if(mediaPlayer1!=null){
            mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer1.start();
                }
            });
        }
    if(mediaPlayer2!=null){
        mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer2.start();
            }
        });
    }
    if(mediaPlayer3!=null){
        mediaPlayer3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer3.start();
            }
        });
    }
    if(mediaPlayer4!=null){
        mediaPlayer4.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer4.start();
            }
        });
    }
}
}
