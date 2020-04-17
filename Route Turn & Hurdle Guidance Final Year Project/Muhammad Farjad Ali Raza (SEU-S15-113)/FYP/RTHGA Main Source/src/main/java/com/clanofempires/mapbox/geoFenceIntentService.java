package com.clanofempires.mapbox;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.mapbox.core.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static com.google.android.gms.common.GooglePlayServicesUtilLight.getErrorString;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class geoFenceIntentService extends IntentService {
    MediaPlayer player=new MediaPlayer();

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.clanofempires.mapbox.action.FOO";
    private static final String ACTION_BAZ = "com.clanofempires.mapbox.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.clanofempires.mapbox.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.clanofempires.mapbox.extra.PARAM2";

    public geoFenceIntentService() {
        super("geoFenceIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, geoFenceIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, geoFenceIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
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
                    Log.e( TAG,"Invalid transition:"+geofenceTransition);
                }

            }
        }
    }
        private String getGeofenceTransitionDetails ( int transitionType, List<
        Geofence > triggeredGeoFence){
            ArrayList<String> triggeredfenceList = new ArrayList<>();
            for (Geofence geofence : triggeredGeoFence) {
                triggeredfenceList.add(geofence.getRequestId());
            }
            String status = null;
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                status = "Entering";

                MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.speedbreakercoming);
                mediaPlayer.start();
                EnteringNotification();


                Log.e(TAG, "FENCE : " + status);


            } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                status = "Existing";
                ExitingNotification();
                MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.exitingthecircle);
                mediaPlayer.start();
                Log.e(TAG, "FENCE : " + status);


            }
            return status;
        }
        private void EnteringNotification () {


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, null)
                    .setSmallIcon(R.drawable.ic_maneuver_arrive)
                    .setContentTitle("Rider Guider")
                    .setContentText("Speed Breaker Ahead ...!!!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManager mNotificationManager =

                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (mNotificationManager != null) {
                mNotificationManager.notify(001, mBuilder.build());

            }
        }
        private void ExitingNotification () {


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, null)
                    .setSmallIcon(R.drawable.direction_depart)
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
        public void onDestroy () {
            super.onDestroy();
            player.stop();
            Log.d("MediaPlayer", "Destroyed");
        }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
