package com.alephlabs.whitenoise;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Objects;

public class PlaylistBroadcast extends BroadcastReceiver {
    public int getId;

    @Override
    public void onReceive(Context context, Intent intent) {
            //to cancel on button click
            //NotificationManager manager  =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //if (manager != null) {
            //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //    manager.cancel(Objects.requireNonNull(intent.getExtras()).getInt("id"));
            // }
            //}


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getId = Objects.requireNonNull(intent.getExtras()).getInt("id");
                Intent playlist=new Intent(context,IntentServicePlaylist.class);
                playlist.setAction("intentAction");
                playlist.putExtra("message", ""+getId);
                int progress = intent.getExtras().getInt("progressToActivity");
                int seekProgress =intent.getExtras().getInt("seekBarChange");
                playlist.putExtra("seekBarChange",seekProgress);
                Log.v("ProgressBroadCast::",""+progress);
                playlist.putExtra("progressToActivity",progress);
                context.startService(playlist);

            }
            if(getId ==20) {
                //Toast.makeText(context, "Play Button Clicked", Toast.LENGTH_LONG).show();

            }
            if(getId ==21) {
                //Toast.makeText(context, "Pause Button Clicked", Toast.LENGTH_LONG).show();
            }
            if(getId ==22) {
                //Toast.makeText(context, "Next Button Clicked", Toast.LENGTH_LONG).show();
            }
            if(getId ==23) {
                //Toast.makeText(context, "Previous Button Clicked", Toast.LENGTH_LONG).show();
            }
            if(getId == 24){
                //Toast.makeText(context, "Progress is happening", Toast.LENGTH_LONG).show();

            }
            if(getId == 30){
                //Toast.makeText(context, "Progress is happening", Toast.LENGTH_LONG).show();

            }
            if(getId == 31){
                //Toast.makeText(context, "Progress is happening", Toast.LENGTH_LONG).show();

            }
            if(getId == 32){
                //Toast.makeText(context, "Progress is happening", Toast.LENGTH_LONG).show();

            }
            if(getId==2121){
                //Toast.makeText(context, "Notification Cancelled", Toast.LENGTH_LONG).show();

            }
            if(getId==2002){

                //intent1.putExtra("value1", vol1);

                //Toast.makeText(context, "Volume Value set:"+vol1, Toast.LENGTH_LONG).show();
            }
            if(getId==2003){

                //intent1.putExtra("value1", vol1);

                //Toast.makeText(context, "Volume Value set:"+vol1, Toast.LENGTH_LONG).show();
            }

        }

}
