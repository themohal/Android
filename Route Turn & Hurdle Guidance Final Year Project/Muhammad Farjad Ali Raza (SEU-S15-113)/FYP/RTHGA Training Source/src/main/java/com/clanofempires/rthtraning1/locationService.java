package com.clanofempires.rthtraning1;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;

public class locationService extends Service implements LocationListener, SensorEventListener {
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private long UPDATE_INTERVAL = 5 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5 * 1000; /* 2 sec */
    MainActivity ss = new MainActivity();
    static Location lp;
    LatLng fromfusedApi;
    private GoogleMap mMap = ss.getMapObj();

    FusedLocationProviderClient mFusedLocationClient;
    FusedLocationProviderApi fusedLocationProviderApi;
    FusedLocationProviderClient client;
    //sensor
    private SensorManager mSensorManager;
    private final float[] mAccelerometerReading = new float[3];
    int countsp = 1;
    LocationCallback forthislocation;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mMap == null) {
            Log.d("mMap:", "NULLLL");
        }
        // mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        client = LocationServices.getFusedLocationProviderClient(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = null;
        if (mSensorManager != null) {
            accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (accelerometer != null) {
            mSensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

        requestLocationUpdates();


    }


    ///thhis one is ussedd
    private void requestLocationUpdates() {
        //LocationRequest rew=LocationRequest.create();

        LocationRequest request = new LocationRequest();

//Specify how often your app should request the device’s location//

        request.setInterval(1000);

//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
//If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//
            forthislocation = new LocationCallback() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onLocationResult(LocationResult locationResult) {

//Get a reference to the database, so your app can perform read and write operations//

                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                  float preLocAcc=location.getAccuracy();
//Save the location data to the database//


                        onLocationChanged(location);
                        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                            @Override
                            public void onMyLocationChange(Location location) {

                                    if(location.getAccuracy()<10) {
                                        location.set(location);

                                        Log.d("new hai bhai :" + location.getProvider(), "" + location);
                                    }

                            }
                        });

                    }}};


            client.requestLocationUpdates(request, forthislocation,Looper.myLooper()) ;
        }
    }

    //sensorfunctions
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(sensorEvent.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);


            if (sensorEvent.values[2] > 15) {
                Log.d("Speed Breaker:", "" + String.valueOf(sensorEvent.values[2]));

                MarkerOptions ss = new MarkerOptions().position(fromfusedApi).title("speed breaker").snippet("" + fromfusedApi).icon(BitmapDescriptorFactory.defaultMarker());

               mMap.addMarker(ss);
                final MediaPlayer player = MediaPlayer.create(this, R.raw.speedbreakerrealtime);
                player.start();

                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        player.release();
                    }
                });
                DatabaseReference ref1;
                ref1 = FirebaseDatabase.getInstance().getReference().child("Speed Breaker Realtime");
                ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        countsp = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                        countsp=countsp+1;
                        Log.d("NumberofSpeedB:", "" + countsp);}
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
                });

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Speed Breaker Realtime").child("SB" + countsp);

                HashMap<String, Object> result = new HashMap<>();




                                if(!String.valueOf(sensorEvent.values[2]).isEmpty()) {
                                    result.put("Intensity:", String.valueOf(sensorEvent.values[2]));
                                }
                                if(fromfusedApi!=null) {
                                    result.put("LatLng:",""+fromfusedApi.latitude+","+fromfusedApi.longitude);
                                    //result.put("Longitude",fromfusedApi.longitude);
                    }
                    LatLng search=new LatLng(33.5639989,73.1509953);
                                String in="15.594633";

                        myRef.child("Speed Breaker Realtime").child("SB" + countsp).child("LatLng:")
                        .equalTo(String.valueOf(search)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    //create new users
                                    Log.d("Search", "Exists");
                                }

                                }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                myRef.setValue(result);
                countsp = countsp + 1;


               // myRef.setValue("Intensity:"+String.valueOf(sensorEvent.values[2])+"" + fromfusedApi);
            }


                    //if (sensorEvent.values[0] >= 10 || sensorEvent.values[0] <= -10) {
            //  Log.d("Pothole Detected:", "" + String.valueOf(sensorEvent.values[0]));
            // FirebaseDatabase database = FirebaseDatabase.getInstance();
            //DatabaseReference myRef = database.getReference("Pothole Realtime:").child("PT" + countsp);

            // countsp = countsp + 1;
            //myRef.setValue("Intensity:"+String.valueOf(sensorEvent.values[0])+"" + fromfusedApi);

            //}


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    @Override
    public void onDestroy() {
        Toast.makeText(this, "service onDestroy", Toast.LENGTH_LONG).show();
        if (client != null) {
            //mGoogleApiClient.disconnect();

//           locationManager.removeUpdates(locationListener);
            //mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            client.removeLocationUpdates(forthislocation);
        }
        mSensorManager.unregisterListener(this);

    }

    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onLocationChanged(Location location) {
        // New location has now been determined
        //if(location.getAccuracy()<=20) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(locationService.this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        fromfusedApi = new LatLng(location.getLatitude(), location.getLongitude());
        //updating firebase with current user latlong
        //Get Firebase auth instance
        FirebaseAuth auth;
        String email = null;
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            email = auth.getCurrentUser().getEmail();
            //replace due to reserved char in firebase
            email = email.replace(".", ",");
            Date currentTime = Calendar.getInstance().getTime();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(email);

            Log.d("Location :", ""+location);
            myRef.setValue("" + latLng);

        }


    }

}
