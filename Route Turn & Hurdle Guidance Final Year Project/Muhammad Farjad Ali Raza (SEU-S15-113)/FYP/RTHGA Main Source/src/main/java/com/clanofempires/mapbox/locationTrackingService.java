package com.clanofempires.mapbox;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class locationTrackingService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    LatLng latLng;
    com.mapbox.geojson.Point lag;
    public locationTrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Service:", "Started");
        buildGoogleApiClient();

    }


    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("LocationTracking:", "Api Connection Successful");
        fused();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    ///using api client
    private void fused() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        String msg = "Updated Location: " +
                                Double.toString(location.getLatitude()) + "," +
                                Double.toString(location.getLongitude());
                        Toast.makeText(locationTrackingService.this, msg, Toast.LENGTH_SHORT).show();
                        // You can now create a LatLng Object for use with maps
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            lag= com.mapbox.geojson.Point.fromLngLat(latLng.longitude,latLng.latitude);
                        //updating firebase with current user latlong
                        //Get Firebase auth instance
                        FirebaseAuth auth;
                        String email = null;
                        auth = FirebaseAuth.getInstance();

                        if (auth.getCurrentUser() != null) {
                            email = auth.getCurrentUser().getEmail();
                            //replace due to reserved char in firebase
                            email = email.replace(".", ",");


                            //Date currentTime = Calendar.getInstance().getTime();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users").child(email);

                            Log.d("Location :", "" + location);
                            myRef.setValue("" + latLng);


                        }

                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Error:", "" + connectionResult);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        mGoogleApiClient.disconnect();
        Log.d("LocationService :", "Destroyed");


    }
public com.mapbox.geojson.Point getLatLng(){
        return lag;
}
}