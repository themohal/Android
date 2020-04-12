package com.alephlabs.whitenoise;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MixerIntentService extends IntentService {

    public static final String FILTER_ACTION_KEY = "any_key";

    public MixerIntentService() {
        super("MixerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            String message = intent.getStringExtra("message");
            intent.setAction(FILTER_ACTION_KEY);
            String echoMessage = "" + message;
            float vol1=intent.getFloatExtra("value1",0);
            Log.v("Vol1::", ""+vol1);
            float vol2=intent.getFloatExtra("value2",0);
            Log.v("Vol2::", ""+vol2);
            float vol3=intent.getFloatExtra("value3",0);
            Log.v("Vol3::", ""+vol3);
            float vol4=intent.getFloatExtra("value4",0);
            Log.v("Vol4::", ""+vol4);
            String echoMessage2 = ""+vol1;
            String echoMessage3 = ""+vol2;
            String echoMessage4 = ""+vol3;
            String echoMessage5 = ""+vol4;

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent.putExtra("broadcastMessage", echoMessage));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent.putExtra("broadcastMessage2", echoMessage2));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent.putExtra("broadcastMessage3", echoMessage3));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent.putExtra("broadcastMessage4", echoMessage4));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent.putExtra("broadcastMessage5", echoMessage5));


        }

    }


}
