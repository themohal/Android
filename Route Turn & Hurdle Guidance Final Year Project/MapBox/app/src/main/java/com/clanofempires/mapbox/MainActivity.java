package com.clanofempires.mapbox;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.storage.Resource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.ThemeSwitcher;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.api.directions.v5.DirectionsCriteria.IMPERIAL;
import static com.mapbox.mapboxsdk.location.modes.CameraMode.*;

public class MainActivity extends AppCompatActivity implements PermissionsListener, LocationEngineCallback<LocationEngineResult>,MapboxMap.OnMapClickListener {

    private PermissionsManager permissionsManager;
    private MapboxMap map;
    private MapView mapView;
    private LocationEngine locationEngine;
    LocationEngineRequest request;
    private LocationEngineProvider locationEngineProvider;
    private Location orignLocation;
    private LocationLayerPlugin locationLayerPlugin;
    private Button naviB;
    private com.mapbox.geojson.Point originPostion;
    private com.mapbox.geojson.Point destPostion;
    private Marker destinationMarker;
    private NavigationMapRoute navigationMapRoute;
    private static final String TAG="MainActivity:";
    private DirectionsRoute directionsRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MapboxNavigation navigation = new MapboxNavigation(this, "pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA");
        Mapbox.getInstance(this, "pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA");
        setContentView(R.layout.activity_main);
        naviB=(Button)findViewById(R.id.naviB);
// An Android Location object

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        map = mapboxMap;
                        map.addOnMapClickListener(MainActivity.this);
                        enableLocation();
// Map is set up and the style has loaded. Now you can add data or make other map adjustments


                    }
                });
            }
        });
        naviB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationLauncherOptions options=NavigationLauncherOptions.builder()
                        .directionsRoute(directionsRoute)
                        //.shouldSimulateRoute(true)
                        .build();
                NavigationLauncher.startNavigation(MainActivity.this,options);

            }
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();
        if(locationEngine!=null){

            locationEngine.requestLocationUpdates(request,this,getMainLooper());
        }
        if(locationLayerPlugin!=null){
            locationLayerPlugin.onStart();
        }
        mapView.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(locationEngine!=null){
            locationEngine.removeLocationUpdates(this);
            if (locationLayerPlugin!=null){
                locationLayerPlugin.onStop();
            }
        }
        mapView.onStop();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationEngine!=null){
            locationEngine.removeLocationUpdates(this);
        }
        mapView.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //permission
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            intializeLocationLayer();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    //
    @SuppressLint("MissingPermission")
    private void initializeLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        request = new LocationEngineRequest.Builder(1000).setFastestInterval(500)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(1000).build();
        locationEngine.requestLocationUpdates(request, this, getMainLooper());


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("WrongConstant")
    private void intializeLocationLayer() {
        LocationComponent locationComponent = map.getLocationComponent();
        // Activate with options
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
        locationComponent.activateLocationComponent(this, Objects.requireNonNull(map.getStyle()));

        // Enable to make component visible
        locationComponent.setLocationComponentEnabled(true);

        // Set the component's camera mode
        locationComponent.setCameraMode(CameraMode.TRACKING);
        // Set the component's render mode

        locationComponent.setRenderMode(RenderMode.NORMAL);
    }
private void setCameraPostion(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),14),4000);
}
    @Override
    public void onSuccess(LocationEngineResult result) {
        Location lastLocation=result.getLastLocation();
        if(lastLocation!=null){
            orignLocation=lastLocation;
            setCameraPostion(lastLocation);
        }
    }

    @Override
    public void onFailure(@NonNull Exception exception) {

    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        if (destinationMarker != null) {
            map.removeMarker(destinationMarker);
        }
            destinationMarker = map.addMarker(new MarkerOptions().position(point));
            destPostion = com.mapbox.geojson.Point.fromLngLat(point.getLongitude(), point.getLatitude());
            originPostion = com.mapbox.geojson.Point.fromLngLat(orignLocation.getLongitude(), orignLocation.getLatitude());
            getRoute(originPostion,destPostion);
            naviB.setEnabled(true);
            naviB.setBackgroundResource(R.color.mapbox_blue);

        return true;
    }
    private void getRoute(com.mapbox.geojson.Point origin, com.mapbox.geojson.Point destination){
        NavigationRoute.builder(this)
                .accessToken("pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA")
                .origin(origin)
                .destination(destination)
                .voiceUnits(IMPERIAL)
                .alternatives(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                    if(response.body()==null){
                        Log.e(TAG,"No routes found ,check again");
                        return;
                        }else if(response.body().routes().size()==0){
                        Log.e(TAG,"No routes found ");
                        return;

                                                }
                        directionsRoute= response.body().routes().get(0);
                    if(navigationMapRoute!=null){
                        navigationMapRoute.removeRoute();
                    }else {
                        navigationMapRoute=new NavigationMapRoute(null,mapView,map);

                    }
                        navigationMapRoute.addRoute(directionsRoute);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    Log.d(TAG,"Error:"+t.getMessage());
                    }
                });
    }
}