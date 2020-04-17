package com.clanofempires.mapbox;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.internal.BottomNavigationPresenter;
import android.support.design.internal.NavigationMenuItemView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.staticmap.v1.StaticMapCriteria;
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.android.navigation.ui.v5.MapboxNavigationActivity;
import com.mapbox.services.android.navigation.ui.v5.NavigationButton;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationUiOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewModel;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.instruction.InstructionLoader;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap;
import com.mapbox.services.android.navigation.ui.v5.summary.SummaryBottomSheet;
import com.mapbox.services.android.navigation.ui.v5.summary.SummaryModel;
import com.mapbox.services.android.navigation.v5.location.replay.ReplayRouteLocationEngine;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationEventListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.route.FasterRouteListener;
import com.mapbox.services.android.navigation.v5.utils.DistanceFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM_RIGHT;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;
import static java.security.AccessController.getContext;

public class navigation_view extends AppCompatActivity implements OnNavigationReadyCallback, NavigationListener, GoogleApiClient.ConnectionCallbacks{
    private static final double ORIGIN_LONGITUDE = -77.04012393951416;
    private static final double ORIGIN_LATITUDE = 38.9111117447887;
    private MapboxNavigation mapboxNavigation;
    private NavigationView navigationView;
    private DirectionsRoute directionsRoute;
    private ImageButton cancelBtn;
    private NavigationListener navigationListener;

    private MapboxNavigationActivity s=new MapboxNavigationActivity();
    private NavigationViewOptions navigationViewOptions;
    private Location location;

    private MapboxNavigationOptions navigationOptions;
    private NavigationMapboxMap navigationMapboxMap;
    GoogleApiClient mGoogleApiClient;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        navigationView = findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);
        MainActivity s=new MainActivity();
        directionsRoute=new bottom_sheet_direction().getDirectionRouteFromBottomSheet();
        location=s.LoctoNavView;
        SummaryBottomSheet summaryBottomSheet=(SummaryBottomSheet)findViewById(R.id.summaryBottomSheet);
        cancelBtn=(ImageButton)findViewById(R.id.cancelBtn);

        CameraPosition cameraPosition=new  CameraPosition.Builder()
                .target(new LatLng(directionsRoute.routeOptions().coordinates().get(0).latitude(),directionsRoute.routeOptions().coordinates().get(0).longitude()))
                //.bearing(Float.valueOf(location.getBearing()).doubleValue())
                .build();
        //NavigationLauncherOptions options=NavigationLauncherOptions.builder()
          //      .directionsRoute(directionsRoute)
            //    .directionsProfile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
              //  .initialMapCameraPosition(cameraPosition)
                //.shouldSimulateRoute(true)
                //.waynameChipEnabled(true)
                //.build();
        //NavigationLauncher.startNavigation(this,options);
        navigationView.initialize(this,cameraPosition);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

cancelBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    }
});
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        // If the navigation view didn't need to do anything, call super
        if (!navigationView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigationView.onDestroy();
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        addNavigationView();


    }



    @Override
    public void onCancelNavigation() {
        if (navigationView != null) {
            navigationView.stopNavigation();
        }
        finish();
    }

    @Override
    public void onNavigationFinished() {

    }

    @Override
    public void onNavigationRunning() {

    }
    //////
    private void setupGeoFence ()
    {

        //setup geofence for Times Square area
        String requestId = "geof1-timesSquare";
        double latitude = 33.56393;
        double longitude = 73.15088;

        float radius = 100.0f;

        Geofence geofence = new Geofence.Builder()
                .setRequestId(requestId)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT|Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(30000)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();
Log.d("Geofence:","Successful");
        Intent serviceIntent = new Intent(getApplicationContext(), geoFenceIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, 0);

        try {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,request,pendingIntent);
        }
        catch (SecurityException se)
        {
            Log.e("GeoTrigger","Permission not granted",se);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

            Log.d("GoogleAPi","Connection Successful");
            //setupGeoFence();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.clearDefaultAccountAndReconnect();
        

    }
private void addNavigationView(){
    NavigationViewOptions options = NavigationViewOptions.builder()
            .directionsRoute(directionsRoute)
            .directionsProfile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
            .navigationOptions(MapboxNavigationOptions.builder().enableFasterRouteDetection(true).isFromNavigationUi(true).build())
            .navigationListener(this)
            .shouldSimulateRoute(true)
            .waynameChipEnabled(true)
            .build();
    navigationView.startNavigation(options);

    navigationView.addMarker(com.mapbox.geojson.Point.fromLngLat(73.1413983, 33.5642787));
///
    List<Feature> markerCoordinates = new ArrayList<>();
    markerCoordinates.add(Feature.fromGeometry(
            com.mapbox.geojson.Point.fromLngLat(73.1413983, 33.5642787))); // Boston Common Park
    FeatureCollection featureCollection = FeatureCollection.fromFeatures(markerCoordinates);

    Source geoJsonSource = new GeoJsonSource("marker-source", featureCollection);
    navigationView.retrieveNavigationMapboxMap().retrieveMap().getStyle()
            .addSource(geoJsonSource);
    Source circlejson = new GeoJsonSource("circle-source", featureCollection);
    navigationView.retrieveNavigationMapboxMap().retrieveMap().getStyle().addSource(circlejson);

    CircleLayer circleLayer=new CircleLayer("circle-layer","circle-source");

    circleLayer.setProperties(
            visibility(VISIBLE),
            circleRadius(50.0f),
            circleColor(0x40ff0000),
            PropertyFactory.circleStrokeColor(Color.TRANSPARENT),
            PropertyFactory.circleStrokeWidth(2.0f));
    circleLayer.withSourceLayer("marker-source");
    String s= String.valueOf(navigationView.retrieveNavigationMapboxMap().retrieveMap().getMaxZoomLevel());
    float maxs=Float.parseFloat(s);
    circleLayer.setMaxZoom(maxs);
    circleLayer.setMinZoom(14);
}


}