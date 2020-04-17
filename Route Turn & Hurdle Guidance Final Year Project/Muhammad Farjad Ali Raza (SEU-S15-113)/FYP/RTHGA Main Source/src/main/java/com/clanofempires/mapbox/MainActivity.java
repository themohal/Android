package com.clanofempires.mapbox;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.matrix.v1.MapboxMatrix;
import com.mapbox.api.matrix.v1.models.MatrixResponse;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener;
import com.mapbox.mapboxsdk.plugins.places.common.PlaceConstants;
import com.mapbox.mapboxsdk.plugins.places.common.utils.KeyboardUtils;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.android.navigation.ui.v5.NavigationContract;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.api.directions.v5.DirectionsCriteria.IMPERIAL;
import static com.mapbox.api.directions.v5.DirectionsCriteria.PROFILE_DRIVING;
import static com.mapbox.geojson.Point.fromLngLat;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.backgroundColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class MainActivity extends AppCompatActivity implements PermissionsListener, LocationEngineCallback<LocationEngineResult>, Style.OnStyleLoaded, MapboxMap.OnMapClickListener, MapboxMap.OnMapLongClickListener, OnMapReadyCallback, MapView.OnDidFinishLoadingStyleListener, NavigationListener, LifecycleOwner
        , android.support.design.widget.NavigationView.OnNavigationItemSelectedListener, Callback<DirectionsResponse> {
    private PermissionsManager permissionsManager;
    public static MapboxMap map;
    public static MapView mapView;
    private MapView navMapView;
    private LocationEngine locationEngine;
    LocationEngineRequest request;
    private LocationEngineProvider locationEngineProvider;
    private Location orignLocation;
    public static Location LoctoNavView;
    private LocationLayerPlugin locationLayerPlugin;
    private Button naviB;
    private ImageButton locB;
    private com.mapbox.geojson.Point originPostion;
    private com.mapbox.geojson.Point destPostion;
    private Marker destinationMarker;
    private NavigationMapRoute navigationMapRoute;
    private com.mapbox.services.android.navigation.ui.v5.NavigationView navigationView;
    private MapboxNavigation mapboxNavigation;
    private static final String TAG = "MainActivity:";
    private DirectionsRoute directionsRoute;
    public static DirectionsRoute transferToNavigationView;
    private DirectionsRoute currentRoute;
    private NavigationContract.View navC;
    private NavigationMapboxMap navigationMapboxMap;
    private Polyline polyline;
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    //forREALTIME
    public static int count = 1;
    int Intensity = 0;
    int i = 1;
    DatabaseReference ref1, ref2, ref3;
    String s;
    android.os.Handler customHandler = new android.os.Handler();

    //
    private List<LatLng> realtimepoints = new ArrayList<>();
    private List<Point> breakerlatlngs = new ArrayList<>();
    private HashMap markerMap = new HashMap();
    MarkerOptions markerOptions;
    private TextView navHeader;
    private FirebaseAuth auth;
    private FloatingActionButton fab;
    private PlaceOptions placeOptions;
    private FragmentTransaction transaction;
    String p = "33.5642787,73.1413983";
    private Location dest = new Location(p);
    //
    private MediaPlayer playerThoumeter;
    private MediaPlayer playerfHundmeter;
    MapboxDirections client;
    //
    private Handler handler;
    private Runnable runnable;
    //datatobottomsheet
    public static Point destP;
    public static Point origP;
    private AlertDialog buil;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        Mapbox.getInstance(this, "pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA");

        setContentView(R.layout.drawer_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        android.support.design.widget.NavigationView navigationViewMenu = (android.support.design.widget.NavigationView) findViewById(R.id.nav_view);

        navigationViewMenu.setNavigationItemSelectedListener(this);


        View header = navigationViewMenu.getHeaderView(0);
        navHeader = (TextView) header.findViewById(R.id.textView1);
            navHeader.setText(String.valueOf(auth.getCurrentUser().getEmail()));


        /////////////////
        //naviB = (Button) findViewById(R.id.naviB);
        locB = (ImageButton) findViewById(R.id.currentLoc);
        fab = (FloatingActionButton) findViewById(R.id.fabFullScreen);
        buil = new AlertDialog.Builder(this).create();
// An Android Location object

        mapView = findViewById(R.id.mapView);
        navMapView = findViewById(R.id.navigationLayout);
        mapView.onCreate(savedInstanceState);
        navigationView = findViewById(R.id.navigationView);
        playerThoumeter = MediaPlayer.create(MainActivity.this, R.raw.meter_1000_slow);
        playerfHundmeter = MediaPlayer.create(MainActivity.this, R.raw.meter_500_slow);
        handler = new Handler();


        mapView.getMapAsync(this);

        mapView.addOnDidFinishLoadingStyleListener(this);


        //naviB.setOnClickListener(new View.OnClickListener() {
        //  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        //@Override
        //public void onClick(View view) {
        //  Intent i = new Intent(MainActivity.this, navigation_view.class);
        //startActivity(i);
        //  }
        //});
        locB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isLocationEnabled();
                if (orignLocation != null) {
                    setCameraPostion(orignLocation);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.d("Clicked", "Search");
                //Intent intent = new PlaceAutocomplete.IntentBuilder()
                //   .accessToken("pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA")
                //.placeOptions(placeOptions)
                //.build(MainActivity.this);
                //startActivityForResult(intent, 1002);

                if(bottom_sheet_direction.polyline!=null){
                    map.removePolyline(bottom_sheet_direction.polyline);
                }
                final PlaceAutocompleteFragment autocompleteFragment;

                if (savedInstanceState == null) {

                    placeOptions = PlaceOptions.builder().backgroundColor(Color.WHITE).toolbarColor(Color.WHITE).build();
                    autocompleteFragment = PlaceAutocompleteFragment.newInstance("pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA", placeOptions);

                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_container, autocompleteFragment, TAG);
                    transaction.commit();
                    fab.hide();

                } else {
                    autocompleteFragment = (PlaceAutocompleteFragment)
                            getSupportFragmentManager().findFragmentByTag(TAG);
                }
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(CarmenFeature carmenFeature) {
                        String json = carmenFeature.toJson();
                            originPostion = fromLngLat(orignLocation.getLongitude(), orignLocation.getLatitude());

                            destPostion = fromLngLat(carmenFeature.center().longitude(), carmenFeature.center().latitude());
                            origP = originPostion;
                            destP = destPostion;
                            //getRoute(originPostion, destPostion);

                            if (destinationMarker != null) {
                                map.removeMarker(destinationMarker);
                            }
                            destinationMarker = map.addMarker(new MarkerOptions().position(new LatLng(carmenFeature.center().latitude(), carmenFeature.center().longitude())));
                            // naviB.setEnabled(true);

                        Intent returningIntent = new Intent();
                        returningIntent.putExtra(PlaceConstants.RETURNING_CARMEN_FEATURE, json);
                        setResult(AppCompatActivity.RESULT_OK, returningIntent);
                        if (map != null) {
                            Style style = map.getStyle();
                            if (style != null) {
                                GeoJsonSource source = style.getSourceAs("geojsonSourceLayerId");
                                if (source != null) {
                                    source.setGeoJson(FeatureCollection.fromFeatures(
                                            new Feature[]{Feature.fromJson(json)}));
                                }

                                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(destPostion.latitude(),destPostion.longitude(),destPostion.altitude()),14));
                                // Move map camera to the selected location
                                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                        .include(new LatLng(new LatLng(originPostion.latitude(), originPostion.longitude(), originPostion.altitude()))) // Northeast
                                        .include(new LatLng(new LatLng(destPostion.latitude(), destPostion.longitude(), destPostion.altitude()))) // Southwest
                                        .build();
                                map.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 250), 5000);
                                getSupportFragmentManager().beginTransaction().
                                        remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
                                KeyboardUtils.hideKeyboard(view);
                                fab.show();
                            }
                                // Draw the route on the map


                        }

                        openBottomSheet();
                    }

                    @Override
                    public void onCancel() {
                        setResult(AppCompatActivity.RESULT_CANCELED);
                        fab.show();
                        getSupportFragmentManager().beginTransaction().
                                remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();

                    }
                });

            }
        });
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS, this);
        map = mapboxMap;

        navigationView = new com.mapbox.services.android.navigation.ui.v5.NavigationView(MainActivity.this);


        mapboxMap.addOnMapClickListener(MainActivity.this);
        mapboxMap.getUiSettings().setAllGesturesEnabled(true);
        mapboxMap.getUiSettings().setCompassEnabled(true);
        mapboxMap.getUiSettings().setAttributionEnabled(false);
        mapboxMap.getUiSettings().setLogoEnabled(false);
        mapboxMap.getUiSettings().setZoomGesturesEnabled(true);

        //alertDialougeforMethod();


        lifecycleRegistry.addObserver(new LifecycleObserver() {

        });


        //Icon icon = IconFactory.getInstance(MainActivity.this).fromResource(R.drawable.map_marker_dark);
        // mapboxMap.setStyle(new Style.Builder().withLayer(new BackgroundLayer("bg").withProperties(backgroundColor(rgb(120, 161, 226)))
        //)
                                /*.withLayer(
                                        new SymbolLayer("symbols-layer", "symbols-source")
                                                .withProperties(iconImage("test-icon"))
                                )
                                .withSource(
                                        new GeoJsonSource("symbols-source", testPoints)
                                )

                                .withImage("test-icon", icon)
                        );*/


// Map is set up and the style has loaded. Now you can add data or make other map adjustments

    }

    @Override
    public void onDidFinishLoadingStyle() {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onStyleLoaded(@NonNull Style style) {
        new taskLoadingData().execute();
        enableLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();

        if (locationEngine != null) {

            locationEngine.requestLocationUpdates(request, this, getMainLooper());

        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
        startService(new Intent(this, locationTrackingService.class));


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mapView.getMapAsync(this);

        startService(new Intent(this, locationTrackingService.class));
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        handler.removeCallbacks(runnable);
        stopService(new Intent(this, locationTrackingService.class));

    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(this);
            if (locationLayerPlugin != null) {
                locationLayerPlugin.onStop();
            }
        }
        mapView.onStop();
        stopService(new Intent(this, locationTrackingService.class));
        handler.removeCallbacks(runnable);


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(this);
        }
        mapView.onDestroy();
        stopService(new Intent(this, locationTrackingService.class));
        handler.removeCallbacks(runnable);


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

    private void setCameraPostion(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14), 4000);
    }

    @Override
    public void onSuccess(LocationEngineResult result) {
        Location lastLocation = result.getLastLocation();
        if (lastLocation != null) {
            orignLocation = lastLocation;
            LoctoNavView = lastLocation;


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
        //getAllIntesities();

        destPostion = fromLngLat(point.getLongitude(), point.getLatitude());
        originPostion = fromLngLat(orignLocation.getLongitude(), orignLocation.getLatitude());
        origP = originPostion;
        destP = destPostion;
        //getRoute(originPostion, destPostion);
        //navigation_view ss=new navigation_view();
        //fetchRoute(originPostion,destPostion);
        // buildDirectApiReq(originPostion,destPostion);
        //newRoute(originPostion, destPostion, null);
        distanceHandler();
        //Intent disServ=new Intent(MainActivity.this,breaker_service.class);
        //startService(disServ);
        //naviB.setEnabled(true);
        //naviB.setBackgroundResource(R.color.mapbox_blue);
        if(bottom_sheet_direction.polyline!=null){
            map.removePolyline(bottom_sheet_direction.polyline);
        }
        openBottomSheet();
        return true;
    }


    public void getRoute(com.mapbox.geojson.Point origin, com.mapbox.geojson.Point destination) {
        NavigationRoute.builder(this)
                .accessToken("pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA")
                .origin(origin)
                .destination(destination)
                .profile(PROFILE_DRIVING)
                .voiceUnits(IMPERIAL)
                .alternatives(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found ,check again");
                            return;
                        } else if (response.body().routes().size() == 0) {
                            Log.e(TAG, "No routes found ");
                            return;
                        }
                        directionsRoute = response.body().routes().get(0);
                        transferToNavigationView = response.body().routes().get(0);

                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            mapboxNavigation = new MapboxNavigation(MainActivity.this, "pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA");

                            //mNMp=new NavigationMapboxMap(mapView,map);
                            //com.mapbox.geojson.Point s = fromLngLat(73.1449533, 33.560878);

                            //mNMp.addMarker(MainActivity.this, s);
                            //myNview=new NavigationView(mapView)

                            navigationMapRoute = new NavigationMapRoute(null, mapView, map);

                            //placeName=response.body().waypoints().get(0).name();
                            //duration=response.body().routes().get(0).duration();


                        }
                        rRoute(directionsRoute);
                        Log.e("Routes:", "" + response.body().routes().get(0).distance());


                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.d(TAG, "Error:" + t.getMessage());
                    }
                });
    }

    //
    private void getAllIntesities() {

        ref1 = FirebaseDatabase.getInstance().getReference().child("Speed Breaker Realtime");
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                Log.d("NumberofSpeedB:", "" + count);

                //
                for (; i <= count; i++) {
                    ref2 = FirebaseDatabase.getInstance().getReference().child("Speed Breaker Realtime").child("SB" + i).child("Intensity:");
                    ref2.addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Intensity = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue(String.class)).substring(0, 2));
                            Log.d("Intensity :", "" + Intensity);
                            if (Intensity >= 20) {
                                s = String.valueOf(dataSnapshot.getRef().getParent().getPath().getBack());
                                s = s.replace("ChildKey(", "");
                                s = s.replace(")", "");
                                s = s.replace("\"", "");

                                ref3 = FirebaseDatabase.getInstance().getReference().child("Speed Breaker Realtime").child(s).child("LatLng:");

                                Log.d("intent", s);

                                Log.d("Count :", "" + count);


                                ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String latLng = String.valueOf(dataSnapshot.getValue());

                                        Log.d("LatlngFromReal :", "" + latLng);


                                        Double lat = Double.parseDouble(latLng.substring(0, 8));
                                        Double lng = Double.parseDouble(String.valueOf(latLng.substring(latLng.indexOf(",") + 1)).substring(0, 8));
                                        LatLng realtimebreakers = new LatLng(lat, lng);

                                        com.mapbox.geojson.Point real = fromLngLat(lng, lat);
                                        // Create an Icon object for the marker to use
                                        Icon icon = IconFactory.getInstance(MainActivity.this).fromResource(R.drawable.map_marker_dark);
                                        MarkerOptions realtimebreaker = new MarkerOptions().setPosition(realtimebreakers).title("RealTimeMarker").icon(icon);
                                        map.addMarker(realtimebreaker);
                                        realtimepoints.add(realtimebreakers);
                                        markerMap.put("realtimebreaker", realtimebreaker);
                                        Log.d("size", "" + realtimepoints.size());

                                        breakerlatlngs.add(fromLngLat(realtimebreakers.getLongitude(), realtimebreakers.getLatitude()));

                                        Log.d("Distance Matrix:", "" + breakerlatlngs.toString());


                                        Log.d("LatitudeFromReal :", "" + lat);

                                        Log.d("LongitudeFromReal :", "" + lng);

                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }//IF INTENSITYENDS

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }//FORLOOP END
            }//ref2ends

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });//ref1 ends

    }//function ends here

    @Override
    public void onCancelNavigation() {

    }

    @Override
    public void onNavigationFinished() {

    }

    @Override
    public void onNavigationRunning() {


    }

    //////////////////MapBoxDirectionApi
    private void buildDirectApiReq(com.mapbox.geojson.Point originPostion, com.mapbox.geojson.Point destPostion) {
        MapboxDirections client = MapboxDirections.builder()
                .origin(originPostion)
                .destination(destPostion)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .steps(true)
                .alternatives(true)
                .voiceInstructions(true)
                .voiceUnits(IMPERIAL)
                .bannerInstructions(true)
                .annotations(DirectionsCriteria.IMPERIAL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken("pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA")
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                if (response.body() == null) {
                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e(TAG, "No routes found");
                    return;
                }

// Retrieve the directions route from the API response
                currentRoute = response.body().routes().get(0);
                if (navigationMapRoute != null) {
                    navigationMapRoute.removeRoute();
                } else {
                    navigationMapRoute = new NavigationMapRoute(mapboxNavigation, mapView, map);

                }
                navigationMapRoute.addRoute(currentRoute);
                getAllIntesities();


                // Draw the route on the map
                drawRoute(currentRoute);
                Log.d("DirectionAPI :", "" + currentRoute);


            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {

                Timber.e("Error: " + throwable.getMessage());

            }
        });
    }


    private void drawRoute(DirectionsRoute route) {
        // Convert List<Waypoint> into LatLng[]

        //   String waypoints = route.routeOptions().waypointTargets();
        // Log.d("Targets:",""+waypoints);
    }


    private void rRoute(DirectionsRoute route) {
        List<LatLng> points = new ArrayList<>();
        List<com.mapbox.geojson.Point> coords = LineString.fromPolyline(route.geometry(), Constants.PRECISION_6).coordinates();

        for (com.mapbox.geojson.Point point : coords) {
            points.add(new LatLng(point.latitude(), point.longitude()));
        }

        Log.d("Polyline Latlngs :", "" + points);
        Log.d("SpeedBreaker Latlngs :", "" + realtimepoints);
        if (points.equals(realtimepoints)) {
            Log.d("\nEqual Points :", "" + realtimepoints);

        }
        System.out.println(realtimepoints.equals(points));

        if (!points.isEmpty()) {
            if (polyline != null) {
                map.removePolyline(polyline);

            }
            polyline = map.addPolyline(new PolylineOptions()
                    .addAll(points)
                    .color(R.color.mapbox_navigation_route_layer_blue)
                    .width(5));
        }


        ////////////////


        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //  @Override
        // public void onClick(View view) {
        //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //         .setAction("Action", null).show();
        //}
        //});

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;//to disable three dotted menu return false else return true
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_satelite) {
            // Handle the camera action
            map.setStyle(Style.SATELLITE);
        } else if (id == R.id.nav_hybrid) {
            map.setStyle(Style.SATELLITE_STREETS);
        } else if (id == R.id.nav_terrian) {
            map.setStyle(Style.OUTDOORS);
        } else if (id == R.id.nav_road) {
            map.setStyle(Style.TRAFFIC_DAY);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth auth;
            auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                auth.signOut();
                ;
                Log.d(TAG, "Logout successful");
            }
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setCameratoSearchPlace(Point placePoint) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(placePoint.latitude(), placePoint.longitude()), 12), 4000);

    }

    private void distanceHandler() {

        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                // ------- code for task to run
                fetchDistance();

                // ------- ends here
                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(runnable, 10000);
    }


    int c = 0;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getDistance() {


        client = MapboxDirections.builder()
                .origin(originPostion)

                .destination(fromLngLat(73.234980,33.571253))
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .alternatives(true)
                .accessToken("pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA")
                .build();
        Log.d("C:", "" + c);


        if (c != realtimepoints.size()) {
            client.enqueueCall(this);
            c = c + 1;
        }
        if (c >= realtimepoints.size()) {

            client.cloneCall();
            c = 0;
        } else {
            client.cancelCall();
        }

    }


    @Override
    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

        if (response.body() == null) {
            Log.e("SpeedBreakerService:", "No routes found, make sure you set the right user and access token.");
            return;
        } else if (response.body().routes().size() < 1) {

            Log.e("SpeedBreakerService", "No routes found");
            return;
        }


        //1000 meter
        if (response.body().routes().get(0).distance() <= 1000) {
            Log.d("Response", "1000meter");

            final MediaPlayer playert = MediaPlayer.create(this, R.raw.meter_1000_slow);
            playert.start();

            playert.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //playert.seekTo(0);
                    playert.stop();
                   // playert.reset();
                    playert.release();
                }
            });

        }
        //500 meter
//        else if (response.body().routes().get(0).distance() <= 500) {
  //          Log.d("Response", "Body");
    //        final MediaPlayer playerf = MediaPlayer.create(this, R.raw.meter_500_slow);
      //      playerf.start();

        //    playerf.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
          //      @Override
            //    public void onCompletion(MediaPlayer mediaPlayer) {
              //     playerf.seekTo(0);
                //    playerf.stop();
                  //  playerf.reset();
                   // playerf.release();

                //}
            //});

        //}
        //100 meter
        // if (response.body().routes().get(0).distance() <=100 && response.body().routes().get(0).distance() >50) {
        //   Log.d("Response", "Body");
        // mediaPlayer.start();
        //}


    }

    @Override
    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
        Timber.e("Error: " + t.getMessage());

    }

    private void openBottomSheet() {


        bottom_sheet_direction merafragment = new bottom_sheet_direction();
        merafragment.show(getSupportFragmentManager(), "BottomSheetlatlong");
    }


    @Override
    public boolean onMapLongClick(@NonNull LatLng point) {
        if (destinationMarker != null) {
            map.removeMarker(destinationMarker);

        }
        destinationMarker = map.addMarker(new MarkerOptions().position(point));

        return true;
    }

    public Point getOrignP() {
        return origP;
    }

    public Point getDestP() {
        return destP;
    }

    public MapboxMap getMap() {
        return map;
    }

    public MapView getMapView() {
        return mapView;
    }

    public void isLocationEnabled() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsProviderEnabled, isNetworkProviderEnabled;
                isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGpsProviderEnabled && !isNetworkProviderEnabled) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Enable Location");
                    builder.setMessage("The app needs location please Turn On Location.");
                    builder.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            dialogInterface.cancel();
                        }
                    });
                    builder.setNegativeButton(android.R.string.no, null);
                    builder.show();
                    // displayLocationSettingsRequest(this);

                }
                else if (isGpsProviderEnabled && isNetworkProviderEnabled) {
                    enableLocation();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private void displayLocationSettingsRequest(Context context) {
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (googleApiClient.isConnected()) {

                            // check if the device has OS Marshmellow or greater than
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                } else {
                                    // get Location
                                }
                            } else {
                                // get Location
                            }

                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, 0x1);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case 0x1:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");



                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    private void addmarkersonMap() {
        //ss.onDestinationSet(com.mapbox.geojson.Point.fromLngLat(73.1413983, 33.5642787));

        List<Feature> markerCoordinates = new ArrayList<>();
        markerCoordinates.add(Feature.fromGeometry(
                com.mapbox.geojson.Point.fromLngLat(73.1413983, 33.5642787))); // Boston Common Park
        markerCoordinates.add(Feature.fromGeometry(
                com.mapbox.geojson.Point.fromLngLat(-71.097293, 42.346645))); // Fenway Park
        markerCoordinates.add(Feature.fromGeometry(
                com.mapbox.geojson.Point.fromLngLat(-71.053694, 42.363725))); // The Paul Revere House
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(markerCoordinates);

        Source geoJsonSource = new GeoJsonSource("marker-source", featureCollection);
        map.getStyle().addSource(geoJsonSource);

        Bitmap icon = BitmapFactory.decodeResource(
                MainActivity.this.getResources(), R.drawable.map_marker_dark);

        // Add the marker image to map
        map.getStyle().addImage("my-marker-image", icon);

        SymbolLayer markers = new SymbolLayer("marker-layer", "marker-source")
                .withProperties(PropertyFactory.iconImage("my-marker-image"));
        map.getStyle().addLayer(markers);

        // Add the selected marker source and layer
        FeatureCollection emptySource = FeatureCollection.fromFeatures(new Feature[]{});
        Source selectedMarkerSource = new GeoJsonSource("selected-marker", emptySource);
        map.getStyle().addSource(selectedMarkerSource);

//////////////////////////
        Source circlejson = new GeoJsonSource("circle-source", featureCollection);
        map.getStyle().addSource(circlejson);

        CircleLayer circleLayer = new CircleLayer("circle-layer", "circle-source");

        circleLayer.setProperties(
                visibility(VISIBLE),
                circleRadius(50.0f),
                circleColor(0x40ff0000),
                PropertyFactory.circleStrokeColor(Color.TRANSPARENT),
                PropertyFactory.circleStrokeWidth(2.0f));
        circleLayer.withSourceLayer("marker-source");
        String s = String.valueOf(map.getMaxZoomLevel());
        float maxs = Float.parseFloat(s);
        circleLayer.setMaxZoom(maxs);
        circleLayer.setMinZoom(14);
        map.getStyle().addLayer(circleLayer);

    }
    private void alertDialougeforMethod() {
        buil.setTitle("Enable Internet");
        buil.setMessage("Slow internet connection or internet connection not available please enable internet to load data and restart app.");
        buil.setButton(AlertDialog.BUTTON_POSITIVE, "Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new taskLoadingData().execute();
                dialogInterface.cancel();
                dialogInterface.dismiss();
            }
        });
        //buil.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
        //  @Override
        //public void onClick(DialogInterface dialogInterface, int i) {

        //}
        //});

        buil.setButton(AlertDialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "Problem in loading data.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reload map", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                recreate();
                                Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content), "Map reloaded!", Snackbar.LENGTH_SHORT);
                                snackbar1.show();

                            }
                        });

                snackbar.show();
                dialogInterface.cancel();
                dialogInterface.dismiss();
            }
        });
        if(buil != null && !buil.isShowing()) {
            buil.show();
        }
    }

    /**
     * Checking for all possible internet providers
     **/
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;

    }
    List<Point> br = new ArrayList<>();

    private boolean VerifyIfMarkerExistsAndShowDetails(String title,boolean check) {
        boolean chkS=check;


        if(chkS){
            MarkerOptions marker = (MarkerOptions) markerMap.get(title);
            if (marker == null) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "Problem in loading data.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reload map", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                recreate();
                                Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content), "Map reloaded!", Snackbar.LENGTH_SHORT);
                                snackbar1.show();

                            }
                        });

                snackbar.show();
            }
            return false;
        }

        return true;
    }

    private class taskLoadingData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (!isConnectingToInternet()) {
                VerifyIfMarkerExistsAndShowDetails("realtimebreaker", false);
                alertDialougeforMethod();
            }
            else {
                VerifyIfMarkerExistsAndShowDetails("realtimebreaker", true);

//fetchDistance(); to start on start
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            if (isConnectingToInternet()) {
                try {
                    getAllIntesities();

                } catch (NullPointerException e) {
                    e.fillInStackTrace();
                }
            }
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private void fetchDistance() {

List<Point> nn=new ArrayList<>();
            nn=breakerlatlngs.subList(0,breakerlatlngs.size());

        Log.i(TAG,"Distance API call start");
for(int q=0;q<nn.size();q++) {
    String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + orignLocation.getLatitude() + "," + orignLocation.getLongitude() + "&destinations=" + nn.get(q).latitude() + "," + nn.get(q).longitude() + "&mode=driving&avoid=tolls&key=" + "AIzaSyDf8n3gedQzaIc9kwVjyUzVBA_v2HPwjCQ";
    new distanceMatrix(MainActivity.this).execute(url);

}
    }


    }


