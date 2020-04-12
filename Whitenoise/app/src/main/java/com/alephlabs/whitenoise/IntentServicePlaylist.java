package com.alephlabs.whitenoise;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Objects;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class IntentServicePlaylist extends IntentService {
    public static final String FILTER_ACTION_KEY = "any_key";
    String progressChange="";
    public IntentServicePlaylist() {
        super("IntentServicePlaylist");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String message = intent.getStringExtra("message");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                int progress = Objects.requireNonNull(intent.getExtras()).getInt("progressToActivity");
                int seekProgress = Objects.requireNonNull(intent.getExtras()).getInt("seekBarChange");
                Log.v("ProgressIntentService::",""+progress);
                Log.v("ProgressChangeIntent::",""+seekProgress);
                progressChange =""+seekProgress;
            }


            intent.setAction(FILTER_ACTION_KEY);
            String echoMessage = "" + message;
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent.putExtra("broadcastMessage", echoMessage));


        }
    }
}
