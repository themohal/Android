package com.empireclan.farjad.myrmappin;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static android.support.constraint.Constraints.TAG;
import static com.empireclan.farjad.myrmappin.MainActivity.speedBreakerMarker;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class locationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SensorEventListener {
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private long UPDATE_INTERVAL = 5 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5 * 1000; /* 2 sec */
    MainActivity ss = new MainActivity();
    LatLng fromfusedApi;
    boolean DisplayNotification = false;
    Intent intent;
    Handler handler;
    Runnable runnable;
    GoogleMap mMap = ss.getMapObj();
    LatLng currentLatlngs;
    LatLng fromGps;
    FusedLocationProviderClient mFusedLocationClient;
    GoogleApiClient mGoogleApiClient;
    FusedLocationProviderClient client;
    //sensor
    private SensorManager mSensorManager;
    private final float[] mAccelerometerReading = new float[3];
    LocationListener locationListener;
    int countsp = 1;
    LocationCallback forthislocation;
    @Override
    public void onCreate() {
        super.onCreate();

       // mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        client=LocationServices.getFusedLocationProviderClient(this);

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

        //GpsLocation();
        //buildGoogleApiClient();
        //requestLocationUpdated();
    }

    //this location change is for google fused lcoatin api
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
                ss.currentLatLng = latLng;
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

                    FirebaseRx my = new FirebaseRx();
                    Date currentTime = Calendar.getInstance().getTime();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Users").child(email);

                    Log.d("Location :", ""+location);
                    myRef.setValue("" + latLng);



                }


            }
//     distanctocircle();


    //}
    ///thhis one is ussedd
    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

//Specify how often your app should request the device’s location//

        request.setInterval(5000);

//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//
                forthislocation=new LocationCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

//Get a reference to the database, so your app can perform read and write operations//

                        Location location = locationResult.getLastLocation();
                        if (location != null) {

//Save the location data to the database//

                            Log.d("new hai bhai :" + location.getProvider(), "" + location);
                            onLocationChanged(location);


                            //Marker asa;
                            //asa=mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
                            //asa.setVisible(true);
                            //asa.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location));
                        }
                    }
                };


            client.requestLocationUpdates(request, forthislocation,null) ;
        }
    }


    public void requestLocationUpdated() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());


        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                Log.i("Google API :", "Location: " + location.getLatitude() + " " + location.getLongitude());

                onLocationChanged(locationResult.getLastLocation());


            }
        }
    };
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
                MediaPlayer player = MediaPlayer.create(this, R.raw.speedbreakerrealtime);
                player.start();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Speed Breaker Realtime").child("SB" + countsp);
                countsp = countsp + 1;

                myRef.setValue("Intensity:"+String.valueOf(sensorEvent.values[2])+"" + fromfusedApi);
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
        if (mFusedLocationClient != null||client != null) {
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //requestLocationUpdates();
        //fused();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void GpsLocation() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        //2nd Approach
        android.location.Criteria criteria = new android.location.Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(true);
        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);
        //criteria.setPowerRequirement(Criteria.POWER_LOW);
        // Getting the name of the best provider
        String provider = null;
        if (locationManager != null) {
            provider = locationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }

// Define a listener that responds to location updates
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                // New location has now been determined
                if (location.getAccuracy() <= 10) {
                    String msg = "Updated Location: " +
                            Double.toString(location.getLatitude()) + "," +
                            Double.toString(location.getLongitude());
                    Toast.makeText(locationService.this, msg, Toast.LENGTH_SHORT).show();
                    // You can now create a LatLng Object for use with maps


                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    ss.currentLatLng = latLng;

                    //updating firebase with current user latlong
                    //Get Firebase auth instance
                    FirebaseAuth auth;
                    String email = null;
                    auth = FirebaseAuth.getInstance();

                    if (auth.getCurrentUser() != null) {
                        email = auth.getCurrentUser().getEmail();
                        //replace due to reserved char in firebase
                        email = email.replace(".", ",");
                    }
                    FirebaseRx my = new FirebaseRx();
                    Date currentTime = Calendar.getInstance().getTime();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("User :" + email);
                    Log.d("" + location.getProvider(), "" + location);

                    myRef.setValue(latLng);
                    //Marker asa;
                    //asa = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
                    //asa.setVisible(true);
                    //asa.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location));

                }
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
// Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (locationManager != null) {

            // locationManager.requestLocationUpdates(provider,3000, 5, locationListener,Looper.myLooper());
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener, Looper.myLooper());
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener, Looper.myLooper());
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener, Looper.myLooper());

        }

    }


    public void distanctocircle() {
        final MainActivity ss = new MainActivity();
        final Circle[] mCircle = new Circle[1];


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                mCircle[0] = ss.getCirlcle();
                Log.d("Circle At Service :", "" + mCircle[0].getCenter().latitude + "" + mCircle[0].getCenter().longitude);
                LatLng ssa = new LatLng(mCircle[0].getCenter().latitude, mCircle[0].getCenter().longitude);
                CalculationByDistance(fromfusedApi, ssa);
                float[] distance = new float[2];

                Location.distanceBetween(fromfusedApi.latitude, fromfusedApi.longitude,
                        mCircle[0].getCenter().latitude, mCircle[0].getCenter().longitude, distance);
                if (distance[0] > mCircle[0].getRadius()) {
                    //Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance[0] + " radius: " + mCircle[0].getRadius(), Toast.LENGTH_LONG).show();
                    removeNotification(locationService.this, 001);
                } else {
                    //Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance[0] + " radius: " + mCircle[0].getRadius(), Toast.LENGTH_LONG).show();
                }
                if (distance[0] > 500) {


                    //sendNotification();
                    //current location is within circle
                    //start new activity
                    // 5 seconds
                }
            }
        };
        handler.postDelayed(runnable, 5000);
    }


    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    public void cancelNotification() {
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notifManager != null) {
            notifManager.cancelAll();
        }
    }

    public void removeNotification(Context context, int notificationId) {
        NotificationManager nMgr = (NotificationManager) context.getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (nMgr != null) {
            nMgr.cancel(notificationId);
        }
    }





    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        if (!mGoogleApiClient.isConnecting() &&
                !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            Log.e("Api Client", "Connection Successful");
        } else {
            Log.e("Api Client", "Connection not Successful");

        }

    }

    ////using api client
    private void fused() {
        LocationRequest mLocationRequest;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5*1000);
        mLocationRequest.setFastestInterval(5*1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        Log.d( "Firing onLocation :"+location.getProvider(),""+location);

                    }
                });
    }


}