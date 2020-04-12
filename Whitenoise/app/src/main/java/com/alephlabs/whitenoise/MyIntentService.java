package com.alephlabs.whitenoise;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Objects;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MyIntentService extends IntentService {
    public static final String FILTER_ACTION_KEY = "any_key";
    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String message = intent.getStringExtra("message");

        intent.setAction(FILTER_ACTION_KEY);
        //SystemClock.sleep(100);
        //String echoMessage = "IntentService after a pause of 3 seconds echoes " + message;
        String echoMessage = "" + message;

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent.putExtra("broadcastMessage", echoMessage));


    }
    }