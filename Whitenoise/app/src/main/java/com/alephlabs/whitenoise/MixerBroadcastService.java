package com.alephlabs.whitenoise;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Objects;

public class MixerBroadcastService extends BroadcastReceiver {
    int getId;
    float vol1,vol2,vol3,vol4;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getId = Objects.requireNonNull(intent.getExtras()).getInt("id");
            Intent playlist=new Intent(context,MixerIntentService.class);
            playlist.setAction("intentAction");
            playlist.putExtra("message", ""+getId);
            vol1=intent.getExtras().getFloat("value1",0);
            vol2=intent.getExtras().getFloat("value2",0);
            vol3=intent.getExtras().getFloat("value3",0);
            vol4=intent.getExtras().getFloat("value4",0);
            Log.v("MixerVOL1::",""+vol1);
            Log.v("MixerVOL2::",""+vol2);
            Log.v("MixerVOL3::",""+vol3);
            Log.v("MixerVOL4::",""+vol4);
            playlist.putExtra("value1",vol1);
            playlist.putExtra("value2",vol2);
            playlist.putExtra("value3",vol3);
            playlist.putExtra("value4",vol4);
            context.startService(playlist);

        }
    }
}
