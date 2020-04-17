package com.example.farjad.notification;

import android.app.NotificationChannel;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button showButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showButton=(Button) findViewById(R.id.show_button);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationView.class);

                PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
                //or intent can be written within pending intent

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                //NOTIFICATION CODE
                //NotificationCompat.Builder mbuilder=
                //      new NotificationCompat.Builder(getApplicationContext());
                // mbuilder.setSmallIcon(R.drawable.rocket);
                // mbuilder.setContentTitle("Reminder: Meeting Starts in 5 min.");
                // mbuilder.setContentText("Prepare For Meeting at 3 pm");
                // mbuilder.setContentIntent(pi);
                //nm.notify(1,mbuilder.build());
                NotificationChannel nmChannel= new NotificationChannel("my_channel_01", "myChannel", NotificationManager.IMPORTANCE_HIGH);
                nmChannel.setDescription("Alert: Meeting starts at 3pm ");
                nmChannel.enableLights(true);
                nmChannel.setLightColor(Color.RED);
                nmChannel.enableVibration(true);
                nmChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 600});
                nm.createNotificationChannel(nmChannel);

            }


        });
    }
}
