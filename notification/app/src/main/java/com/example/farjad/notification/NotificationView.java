package com.example.farjad.notification;

import android.app.NotificationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NotificationView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);

        NotificationManager nm =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1);


    }
}
