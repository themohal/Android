package com.empireclan.farjad.myrmappin;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * A IntentService subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * helper methods.
 */
public class GeofenceTransitionService extends IntentService {
    MediaPlayer player=new MediaPlayer();

    public GeofenceTransitionService() {
        super("GeofenceTransitionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                String errorMessage = String.valueOf(geofencingEvent.getErrorCode());
                Log.e(TAG, errorMessage);
                return;
            }

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

                // Get the transition details as a String.
                String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);

                // Send notification and log the transition details.
                EnteringNotification();


                Log.i(TAG, geofenceTransitionDetails);
            } else {
                // Log the error.
                Log.e(TAG, getString(R.string.geofence_transition_invalid_type,
                        geofenceTransition));
            }

        }
    }
    private String getGeofenceTransitionDetails( int transitionType, List<Geofence> triggeredGeoFence) {
        ArrayList<String > triggeredfenceList=new ArrayList<>();
        for (Geofence geofence:triggeredGeoFence ){
            triggeredfenceList.add(geofence.getRequestId());
        }
        String status=null;
        if(transitionType==Geofence.GEOFENCE_TRANSITION_ENTER){
            status="Entering";

            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.speedbreakercoming);
            mediaPlayer.start();
            EnteringNotification();


            Log.e(TAG, "FENCE : " + status);


        }
        else if(transitionType==Geofence.GEOFENCE_TRANSITION_EXIT){
            status="Existing";
            ExitingNotification();
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.exitingthecircle);
            mediaPlayer.start();
            Log.e(TAG, "FENCE : " + status);



        }
        return status;
    }
    private void EnteringNotification(){


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, null)
                .setSmallIcon(R.drawable.ic_action_location)
                .setContentTitle("Rider Guider")
                .setContentText("Speed Breaker Ahead ...!!!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager != null) {
            mNotificationManager.notify(001, mBuilder.build());

        }
    }
    private void ExitingNotification(){


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, null)
                .setSmallIcon(R.drawable.ic_action_location)
                .setContentTitle("Rider Guider")
                .setContentText("Heading Far Away ...!!!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager != null) {
            mNotificationManager.notify(001, mBuilder.build());

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        Log.d("MediaPlayer","Destroyed");
    }
}
