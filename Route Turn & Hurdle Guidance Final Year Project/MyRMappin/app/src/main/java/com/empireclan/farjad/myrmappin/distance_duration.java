package com.empireclan.farjad.myrmappin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.empireclan.farjad.myrmappin.DataParser;
import com.empireclan.farjad.myrmappin.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Timer;
import java.util.TimerTask;


public class distance_duration extends BottomSheetDialogFragment {
    DataParser mt = new DataParser();
    TextView time;
    String td;
    MainActivity ss=new MainActivity();
    GoogleMap mMap=ss.getMapObj();


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        td = "" + mt.getDuration() + "(" + mt.getDistance() + ")";
        Log.d("On Attach Activity :",""+td);

    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        String Distance;
        String Duration;


        Button navbutton;
        Button animateButton;
        final MainActivity ss = new MainActivity();
        final LatLng currentLatLng = ss.getCurrentLatLng();
        final double mymarkerlat = ss.getlat();
        final double mymarkerlongi = ss.getlong();
        Distance = mt.getDistance();
        Duration = mt.getDuration();
        View v = inflater.inflate(R.layout.time_distance, container, false);
        EditText dist;
        final Timer timer =new Timer();
        animateButton=(Button)v.findViewById(R.id.animate);
        navbutton = (Button) v.findViewById(R.id.navButton);
        //dist = (EditText) v.findViewById(R.id.distance);

        time= (TextView) v.findViewById(R.id.time);

        time.setText(td);

        Log.d("Distanc TIME :","D :"+Distance+"T :"+Duration);
        animateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDismiss(getDialog());
            }
        });

         navbutton.setOnClickListener(new View.OnClickListener() {
           @Override
        public void onClick(View view) {


        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + currentLatLng.latitude + "," + currentLatLng.longitude + "&destination=" + mymarkerlat + "," + mymarkerlongi + "&waypoints&travelmode=driving&dir_action=navigate");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
               mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK&Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
               mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(mapIntent);

        //Intent intent = new Intent(MainActivity.this, distance_duration.class);
        //startActivity(intent);
        //overridePendingTransition(R.anim.bottom_up, R.anim.nothing);


    }
        });

        return v;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    }