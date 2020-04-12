package com.empireclan.farjad.myrmappin;

import android.Manifest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

import models.PlaceInfo;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, NavigationView.OnNavigationItemSelectedListener,GoogleMap.OnMyLocationChangeListener,ResultCallback<Status> {

    Marker myMarker;
    private static final String TAG = "MainActivity";
    Intent serviceIntent;


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.reconnect();

    }


    public static GoogleMap mMap;


    private FusedLocationProviderClient mLocationProvider;

    public static Activity main;

    private static final String FINE_LOCATON = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATON = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private static Double lg = 1.0;
    private static Double lt = 1.1;
    public LatLng currentLatLng;

    private static int PLACE_PICKER_REQUEST = 1;

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;
    private ImageView mPlaceInfo;
    private ImageView placePicker;


    //vars
    private Boolean mLocationPermissionGrannted = false;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private Marker mMarker;
    private LocationRequest mLocationRequest;
    private Location mLocation;
    com.google.android.gms.location.LocationListener mlocationListener;
    double mymarkerlat = 1;
    double mymarkerlongi = 2;

    //for fragmment latlng passing
    private static double marklati;
    private static double marklongi;
    private static LatLng myfragcurrentloc;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;

    private SupportMapFragment mMapFragment;
    //for direction saving
    ArrayList<LatLng> mMarkerPoints;

    //speedbreaker
    CircleOptions circleOptions;
    static Circle circle;
    LatLng speedBreakerLatLngs;
    public static Marker speedBreakerMarker;
    GeofencingRequest geoRequest;
    public static Circle circletoloction;

    private GeofencingClient mGeofencingClient;

    Geofence geofence;


    //current marker
    Marker ss;
    //forREALTIME
    public static int count=1;
    int Intensity = 0;
    int i = 1;
    DatabaseReference ref1, ref2, ref3;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //reference to finish this activity if logout from other
        main = this;

        serviceIntent = new Intent(this, locationService.class);

        //startService(serviceIntent);


        mGeofencingClient = LocationServices.getGeofencingClient(this);

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mPlaceInfo = (ImageView) findViewById(R.id.place_info);
        placePicker = (ImageView) findViewById(R.id.place_picker);
        mMarkerPoints = new ArrayList<LatLng>();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_m);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                mDrawerLayout.requestFocus();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                sendViewToBack(drawerView);

                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }

        };

        mDrawerLayout.addDrawerListener(mToggle);

        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);


        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        getDeviceCurrentLocation();
        getLocationPermission();
        buildGoogleApiClient();
        getAllIntesities();



        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getLocationPermission();
                mylocation();
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocation = FusedLocationApi.getLastLocation(mGoogleApiClient);

                LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
                mMap.animateCamera(cameraUpdate);

                //startService(serviceIntent);
            }
        });

    }

    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //.addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        if (!mGoogleApiClient.isConnecting() &&
                !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            Log.e("Api Client", "Connection Successful");

        } else {
            Log.e("Api Client", "Connection not Successful");

        }

    }

    private void inIt() {

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);
        mSearchText.setAdapter(mPlaceAutocompleteAdapter);


        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override

            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                Log.d("MMMM", "init CALLED");
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE) ;
                //excute method for searching
                geoLocate();


                return false;
            }
        });
        //mGps.setOnClickListener(new View.OnClickListener() {
        //  @Override
        //public void onClick(View view) {
        //  mMap.animateCamera(CameraUpdateFactory,1000,null));
        //}
        //});


        mPlaceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked place info");
                try {
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    } else {
                        Log.d(TAG, "onClick: place info: " + mPlace.toString());
                        mMarker.showInfoWindow();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage());
                }
            }
        });
        ///////////////////////////////////
        placePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getApplicationContext(), data);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }
    }

    //on search button click
    private void geoLocate() {
        Log.d("MMMM", "GEOFUCNTION CALLED");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d("", "geolocate:Found a location" + address.toString());
            //Toast.makeText(getApplicationContext(),address.toString(),Toast.LENGTH_SHORT).show();
            closeKeyboard();
            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(addresslatlng,10),address.getAddressLine(0));
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 14, mPlace);


        } else {
            Toast.makeText(getApplicationContext(), "No Place Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void getDeviceCurrentLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mLocationProvider = getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGrannted) {
                Task<Location> location = mLocationProvider.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {

                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {


                                lg = currentLocation.getLongitude();
                                lt = currentLocation.getLatitude();
                            }
                            LatLng l = new LatLng(lt, lg);
                            //parsing value to draw line
                            myfragcurrentloc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 14), 4000, null);
/// current locatin marker
                            //ss=mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())));
                            //ss.setDraggable(true);
                            //ss.setVisible(true);
                            //
                            //moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
    //on placelist name click

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 4000, null);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MainActivity.this));
        mMap.clear();


        if (placeInfo != null) {
            try {
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options);
                marklati = mMarker.getPosition().latitude;
                marklongi = mMarker.getPosition().longitude;
                closeKeyboard();

                lat_long_fragment merafragment = new lat_long_fragment();
                merafragment.show(getSupportFragmentManager(), "BottomSheetlatlong");


            } catch (NullPointerException e) {
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));

        }


    }

    //
    private void moveCamera(LatLng latLng, float zoom, String title) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14), 4000, null);
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
    }

    //
    public void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
    }

    private void getLocationPermission() {
        String[] permssions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATON) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATON) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGrannted = true;
                initMap();


            } else {
                ActivityCompat.requestPermissions(this, permssions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permssions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGrannted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                            mLocationPermissionGrannted = false;
                        return;
                    }
                    mLocationPermissionGrannted = true;
                    //intialize our map
                }
            }
        }
    }

    /*
    ------------------- gooogle places API autocomplete suggestion --------------
    */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
//                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id:" + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), 14, mPlace);


            places.release();
        }
    };

//////////////////////////

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void mylocation() {
        boolean isFirstTime = true;

        if (mMap != null)
            if (isFirstTime) {
                try {
                    Task<Location> locationResult = mLocationProvider.getLastLocation();
                    locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                // Set the map's camera position to the current location of the device.
                                Location location = task.getResult();
                                if (location != null) {
                                    currentLatLng = new LatLng(location.getLatitude(),
                                            location.getLongitude());

                                }

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14), 4000, null);


                                Log.d(TAG, "onResult: GPS CLICKED ");

                            }
                        }
                    });
                } catch (SecurityException e) {
                    Log.e("Exception: %s", e.getMessage());
                }
                isFirstTime = false;
            }

    }

    private void gpsclick() {
        if (mGps.isClickable()) {
            Log.d(TAG, "onResult: GPS CLICKED ");

            mylocation();
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
        mMapFragment.getMapAsync(this);
        startService(serviceIntent);
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {


        switch (menuItem.getItemId()) {
            case R.id.nav_mya:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                // do you click actions for the first selection
                mMap.getFocusedBuilding();
                break;
            case R.id.nav_terrian:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

                // do you click actions for the second selection
                break;
            case R.id.nav_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                // do you click actions for the third selection
                break;
            case R.id.users_map:
                Intent userMap = new Intent(this, Users_Map.class);
                startActivity(userMap);

                // do you click actions for the third selection
                break;
            case R.id.nav_logout:
                FirebaseAuth auth;
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                stopService(serviceIntent);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                finish();
                break;


        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mToggle.onDrawerClosed(mDrawerLayout);


        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            mDrawerLayout.bringToFront();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        stopService(serviceIntent);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash

    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }


    @Override
    protected void onStart() {
        super.onStart();
        startService(serviceIntent);
        // on starting the app get the camera params
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(serviceIntent);

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            View view=this.getCurrentFocus();
            sendViewToBack(view);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMyLocationChange(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

        // moveCamera(new LatLng(location.getLatitude(),location.getLongitude()),14,mPlace);

    }


    //////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        mylocation();
        getAllSpeedBreakerData();
        Intent intent=new Intent(this,GeofenceTransitionService.class);
        startService(intent);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 10sec

                geofence = createdGeofence(speedBreakerMarker.getPosition(), 100);
                geoRequest = createGeorequest(geofence);
                addGeofence(geofence);
            }
        }, 10000);

        if (mLocationPermissionGrannted) {
            getDeviceCurrentLocation();


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            inIt();
        }
        ////////
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {


                // First check if myMarker is null
                if (myMarker == null) {

                    // Marker was not set yet. Add marker:


                    myMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Your marker title")
                            .snippet(""));

                    LatLng position = myMarker.getPosition(); //
                    mymarkerlat = position.latitude;
                    mymarkerlongi = position.longitude;
                    //argue passing to fragment
                    marklati = position.latitude;
                    marklongi = position.longitude;
                    Toast.makeText(
                            MainActivity.this,
                            "Lat " + mymarkerlat + " "
                                    + "Long " + mymarkerlongi,
                            Toast.LENGTH_LONG).show();

                } else {

                    // Marker already exists, just update it's position
                    myMarker.setPosition(latLng);
                    final LatLng position = myMarker.getPosition(); //
                    mymarkerlat = position.latitude;
                    mymarkerlongi = position.longitude;
                    //argue passing to fragment
                    marklati = position.latitude;
                    marklongi = position.longitude;

                    Toast.makeText(
                            MainActivity.this,
                            "Lat " + mymarkerlat + " "
                                    + "Long " + mymarkerlongi,
                            Toast.LENGTH_LONG).show();


                }
                closeKeyboard();
                lat_long_fragment merafragment = new lat_long_fragment();
                merafragment.show(getSupportFragmentManager(), "BottomSheetlatlong");


            }
        });

//


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                closeKeyboard();
                getAllIntesities();
                getAllSpeedBreakerData();
            }
        });


    }

    public double getlat() {
        return marklati;
    }

    public double getlong() {
        return marklongi;
    }

    public LatLng getCurrentLatLng() {
        return myfragcurrentloc;
    }

    public GoogleMap getMapObj() {
        return mMap;
    }

    //to get back the current focus after drawer open
    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);

        }
    }

    //get All Speed breakers data
    private void getAllSpeedBreakerData() {


        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("SpeedBreakers Static:");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot mmp : dataSnapshot.getChildren()) {
                    //  all.put(String.valueOf(mmp.getKey()), String.valueOf(mmp.getValue()));

                    String latlong1 = String.valueOf(mmp.getValue()).replace("lat/lng: (", "");
                    String latlong2 = latlong1.replace(")", "");
                    String[] latlong3 = latlong2.split(",");

                    Log.d("SpeedBreaker LATLONG :", "" + latlong3[0]);
                    Log.d("Speed Breaker LATLONG :", "" + latlong3[1]);
                    final double latitude = Double.parseDouble(latlong3[0]);
                    double longitude = Double.parseDouble(latlong3[1]);
                    speedBreakerLatLngs = new LatLng(latitude, longitude);

                    speedBreakerMarker = mMap.addMarker(new MarkerOptions().title("" + speedBreakerLatLngs).position(speedBreakerLatLngs).draggable(true));

                    speedBreakerMarker.setVisible(true);


                    //Instantiates a new CircleOptions object +  center/radius
                    circleOptions = new CircleOptions()
                            .center(new LatLng(speedBreakerLatLngs.latitude, speedBreakerLatLngs.longitude))
                            .radius(100)
                            .fillColor(0x40ff0000)
                            .strokeColor(Color.TRANSPARENT)
                            .strokeWidth(2);

// Get back the mutable Circle
                    circle = mMap.addCircle(circleOptions);

                    circletoloction = circle;

// more operations on the circle...

                }
                List<LatLng> SpeedBreakerlatLngsList = new ArrayList<>();

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String latlong1 = String.valueOf(dsp.getValue());
                    String latlong2 = latlong1.replace("lat/lng: (", "");
                    String latlong3 = latlong2.replace(")", "");
                    String[] latlong4 = latlong3.split(",");
                    final double latitude = Double.parseDouble(latlong4[0]);
                    double longitude = Double.parseDouble(latlong4[1]);
                    LatLng list = new LatLng(latitude, longitude);
                    SpeedBreakerlatLngsList.add(list); //add result into array list


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //geofencing
    public void addGeofence(Geofence geofence) {

        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geoRequest, createGeofencingPendingIntent()).setResultCallback(MainActivity.this);
    }

    PendingIntent geoFencePendingIntent;

    private PendingIntent createGeofencingPendingIntent() {
        if (geoFencePendingIntent != null) {
            return geoFencePendingIntent;
        } else {
            // Create an Intent pointing to the IntentService
            Intent i = new Intent(this, GeofenceTransitionService.class);
            return PendingIntent.getService(MainActivity.this, 001, i, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    public GeofencingRequest createGeorequest(Geofence geofence) {
        return new GeofencingRequest.Builder().setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence).build();

    }

    public Geofence createdGeofence(LatLng position, float v) {
        return new Geofence.Builder().setRequestId("My GeoFence").setCircularRegion(position.latitude, position.longitude, v).setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT).build();


    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    public Circle getCirlcle() {
        return circletoloction;

    }

    public Marker getCurrentMark() {
        return ss;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {



    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    private void getAllIntesities() {

        ref1 = FirebaseDatabase.getInstance().getReference().child("Speed Breaker Realtime");
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                Log.d("NumberofSpeedB:", "" + count);

        //
            for(;i<=count;i++){
                ref2 = FirebaseDatabase.getInstance().getReference().child("Speed Breaker Realtime").child("SB"+i).child("Intensity:");
                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Intensity = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue(String.class)).substring(0, 2));
                        Log.d("Intensity :", "" + Intensity);
                        if (Intensity >= 20) {
                            s = String.valueOf(dataSnapshot.getRef().getParent().getPath().getBack());
                            s = s.replace("ChildKey(", "");
                            s = s.replace(")", "");
                            s=s.replace("\"","");

                            ref3 = FirebaseDatabase.getInstance().getReference().child("Speed Breaker Realtime").child(s).child("LatLng:");

                            Log.d("intent", s);

                            Log.d("Count :", "" + count);


                            ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String latLng = String.valueOf(dataSnapshot.getValue());

                                    Log.d("LatlngFromReal :", "" + latLng);


                                    Double lat = Double.parseDouble(latLng.substring(0, latLng.indexOf(",")));
                                    Double lng = Double.parseDouble(latLng.substring(latLng.indexOf(",") + 1));
                                    LatLng realtimebreakers = new LatLng(lat, lng);
                                    MarkerOptions realtimebreaker = new MarkerOptions().position(realtimebreakers).icon(BitmapDescriptorFactory.defaultMarker()).title("RealTimeMarker").visible(true);
                                    mMap.addMarker(realtimebreaker);


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
    }






