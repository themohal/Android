package com.empireclan.farjad.myrmappin;

import android.Manifest;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.PlaceInfo;

import static com.empireclan.farjad.myrmappin.MainActivity.main;
import static com.empireclan.farjad.myrmappin.lat_long_fragment.lat;
import static com.empireclan.farjad.myrmappin.lat_long_fragment.lineOptions;
import static com.empireclan.farjad.myrmappin.lat_long_fragment.longi;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static java.lang.Thread.sleep;

public class Users_Map extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMyLocationChangeListener, ResultCallback<Status> {

    private static final String TAG = "USERSAMAP";
    Intent serviceIntent;
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.reconnect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleApiClient.clearDefaultAccountAndReconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.reconnect();
    }


    public static GoogleMap mMap;


    private FusedLocationProviderClient mLocationProvider;


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
    private Location mLocation;


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;

    private SupportMapFragment mMapFragment;
    //for direction saving
    ArrayList<LatLng> mMarkerPoints;


    //for accuracy
    LocationManager locationManager;
    FragmentManager myfragment;
    //database readings
    public static String email = null;
    Double dlat;
    Double dlong;
    LatLng dbLatLng;
    static LatLng location;
    static LatLng currentUserLoc;

    static ArrayList<String> Userlist;
    static List<LatLng> latLngs;
    static LatLng livelocationmarker;
    Marker liveMark;
    private List<Marker> markers = new ArrayList<Marker>();
    //geofencing
    LatLng speedBreakerLatLngs;
    Marker spdMarker;
    GeofencingClient mGeofencingClient;
    ArrayList mGeofenceList;
    CircleOptions circleOptions;
    static Circle circle;
    Intent n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myfragment = getFragmentManager();
        n=new Intent(Users_Map.this,locationService.class);
        ///
      //  getAllSpeedBreakerData();

        ////
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

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
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_act);
                View main = (View) findViewById(R.id.map);
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


        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getLocationPermission();
                mylocation();

                if (ActivityCompat.checkSelfPermission(Users_Map.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Users_Map.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        //addLiveMarker();

    }

    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(Users_Map.this)
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
        // Create a GoogleApiClient instance

        // ...
        mSearchText.setOnItemClickListener(mAutocompleteClickListener);
//
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);
        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override

            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                Log.d("MMMM", "init CALLED");
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER ||
                        keyEvent.getAction() == KeyEvent.KEYCODE_BACKSLASH ||
                        keyEvent.getAction() == KeyEvent.KEYCODE_1 || keyEvent.getAction() == KeyEvent.KEYCODE_DEL)
                    geoLocate();
                //excute method for searching


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
                    startActivityForResult(builder.build(Users_Map.this), PLACE_PICKER_REQUEST);
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

    private void geoLocate() {
        Log.d("MMMM", "GEOFUCNTION CALLED");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(Users_Map.this);
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
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
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
                            }
                            if (currentLocation != null) {
                                lt = currentLocation.getLatitude();
                            }
                            LatLng l = new LatLng(lt, lg);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lt, lg), 15));


                            //
                            //moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(Users_Map.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }


    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18), 4000, null);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(Users_Map.this));
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

            } catch (NullPointerException e) {
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }


    }

    //
    private void moveCamera(LatLng latLng, float zoom, String title) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18), 4000, null);
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
        mapFragment.getMapAsync(Users_Map.this);
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
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

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


                                //////
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lt, lg), 15));


                                mMap.setMyLocationEnabled(true);
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
                recreate();
                // do you click actions for the third selection
                break;
            case R.id.nav_logout:
                FirebaseAuth auth;
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                stopService(n);
                startActivity(new Intent(Users_Map.this, LoginActivity.class));
                finish();

                main.finish();
                break;


        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        int i = mDrawerLayout.getNextFocusUpId();
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


    }

    @Override
    protected void onPause() {
        super.onPause();

        //stopService(new Intent(getApplicationContext(),locationService.class));


        // on pause turn off the flash

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onStart() {
        mMapFragment.getMapAsync(Users_Map.this);
        super.onStart();
        startService(n);
        // on starting the app get the camera params
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopService(new Intent(getApplicationContext(),locationService.class));
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMyLocationChange(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

    }


    //////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        mylocation();
        if (mLocationPermissionGrannted) {
            getDeviceCurrentLocation();


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                livelocationmarker = marker.getPosition();

                drawpoly(currentUserLoc, marker.getPosition());
                marker.showInfoWindow();


                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                readAll();
                closeKeyboard();

            }
        });


//set all speed breaker markers
        onFirebaseDatachanged();

    }





    //to get back the current focus after drawer open
    public void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }


    //read all
    private void readAll() {
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference ref2, ref3, ref4;
        //ref2 = ref1.child("Users");

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //HashMap<String,String> all=new HashMap<>();

                //looping through all users and points and adding them to map
                //forEach() method can also be used
                for (final DataSnapshot mmp : dataSnapshot.getChildren()) {
                    //  all.put(String.valueOf(mmp.getKey()), String.valueOf(mmp.getValue()));

                    String latlong1 = String.valueOf(mmp.getValue()).replace("lat/lng: (", "");
                    String latlong2 = latlong1.replace(")", "");
                    String[] latlong3 = latlong2.split(",");

                    Log.d("LATLONG :", "" + latlong3[0]);
                    Log.d("LATLONG :", "" + latlong3[1]);
                    final double latitude = Double.parseDouble(latlong3[0]);
                    double longitude = Double.parseDouble(latlong3[1]);

                    location = new LatLng(latitude, longitude);
                    //liveMark = mMap.addMarker(new MarkerOptions().position(location));
                    //liveMark.setVisible(true);


                    liveMark = mMap.addMarker(new MarkerOptions().position(location).title(mmp.getKey()).icon(BitmapDescriptorFactory.defaultMarker()));
                    liveMark.setVisible(true);

                    FirebaseAuth auth;
                    auth = FirebaseAuth.getInstance();

                    if (auth.getCurrentUser() != null) {
                        email = auth.getCurrentUser().getEmail();
                        email = email.replace(".", ",");
                        if (liveMark.getTitle().equals(email)) {
                            liveMark.setVisible(false);
                        }
                    }


                    if (auth.getCurrentUser() != null) {
                        email = auth.getCurrentUser().getEmail();
                        email = email.replace(".", ",");
                        final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Users").child(email);
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String s = String.valueOf(dataSnapshot.getValue());
                                Log.d("SINGLE VALUE :", s);
                                String latlong1 = String.valueOf(s.replace("lat/lng: (", ""));
                                String latlong2 = latlong1.replace(")", "");
                                String[] latlong3 = latlong2.split(",");

                                Log.d("currentUserLoc :", "" + latlong3[0]);
                                Log.d("currentUserLoc :", "" + latlong3[1]);
                                final double latitude = Double.parseDouble(latlong3[0]);
                                double longitude = Double.parseDouble(latlong3[1]);
                                currentUserLoc = new LatLng(latitude, longitude);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
//

                    }

                    String key = mmp.getKey();
                    String value = String.valueOf(mmp.getValue());
                  //  System.out.println(key + " " + value);
                }


                Userlist = new ArrayList<String>();
                // Result will be holded Here
               // for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                 //   Userlist.add(String.valueOf(dsp.getKey())); //add result into array list

                //}
                //for (int m = 0; m < Userlist.size(); m++) {

                  //  Log.d("USER :", "" + Userlist.get(m));


                //}
                latLngs = new ArrayList<LatLng>();

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String latlong1 = String.valueOf(dsp.getValue());
                    String latlong2 = latlong1.replace("lat/lng: (", "");
                    String latlong3 = latlong2.replace(")", "");
                    String[] latlong4 = latlong3.split(",");
                    final double latitude = Double.parseDouble(latlong4[0]);
                    double longitude = Double.parseDouble(latlong4[1]);
                    LatLng list = new LatLng(latitude, longitude);
                    latLngs.add(list); //add result into array list

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //whenever a new entry is added update the interface realtime
    private void onFirebaseDatachanged() {
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Users");
        ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                readAll();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //    mMap.clear();

                mMap.clear();
                readAll();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                //  mMap.clear();

                mMap.clear();
                readAll();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mMap.clear();


                readAll();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //fencingggg

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = "errorrrrrrrrr";
        }

    }
    Circle geoFenceLimits;


    private void drawGeofence() {
        if(geoFenceLimits!=null){
            geoFenceLimits.remove();
        }

        //CircleOptions circleOptions=new CircleOptions().center(speedBreakerLatLngs)
          //      .strokeColor(Color.argb(50,70,70,70))
            //    .fillColor(Color.argb(100,150,150,150))
              //  .radius(100f);
        //geoFenceLimits=mMap.addCircle(circleOptions);
    }




    /////////////////////////////////////////

    // Fetches data from url passed
    class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());


            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    static String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";
        //api key
        String directionApi = "AIzaSyDf8n3gedQzaIc9kwVjyUzVBA_v2HPwjCQ";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + sensor + "&waypoints:optimize" + "&mode=driving" + "&alternatives=true" + "&departure_time=now" + "&arrival_time" + "&maneuver" + "enc:lexeF{~wsZejrPjtye@:" + "&key=" + directionApi;

        return url;
    }


    private static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());


                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());


            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;


            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                lat = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    longi = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);


                    points.add(position);
                    //parsing


                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);

                //
                lineOptions.width(12);
                lineOptions.geodesic(true);

                lineOptions.color(Color.parseColor("#24ace2"));


                Log.d("onPostExecute", "onPostExecute line options decoded");
                lineOptions.visible(true);
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {

                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    private void drawpoly(LatLng origin, LatLng dest) {
        try {


            String url = getDirectionsUrl(origin, dest);

            FetchUrl downloadTask = new FetchUrl();
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

// Start downloading json data from Google Directions API
            downloadTask.execute(url);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

