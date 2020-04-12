package com.alephlabs.whitenoise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class lib_recordings extends AppCompatActivity {
    private AdView mAdView;
    private RecyclerView mRecylerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton mBackButton;
    public final static int REQUEST_CODE = 10101;

    ArrayList<ExampleItem> item = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_recordings);
        BottomNavigationView ss = (BottomNavigationView) findViewById(R.id.bottom_nav);
        Menu menu = ss.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
        ss.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_home) {
                    Intent my_home = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(my_home);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_music) {
                    Intent my_noises = new Intent(getApplicationContext(), Noises.class);
                    startActivity(my_noises);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_mixer) {
                    Intent my_mixer = new Intent(getApplicationContext(),Mixer.class);
                    startActivity(my_mixer);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_record) {
                    Intent my_rec = new Intent(getApplicationContext(), recording.class);
                    startActivity(my_rec);
                    finish();
                }
                if (menuItem.getItemId() == R.id.navigation_library) {


                }
                return false;
            }
        });


        mBackButton =(ImageButton) findViewById(R.id.recording_lib_back_button);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("287BB39D093D5E59376400796EDFCC57").build();
        mAdView.loadAd(adRequest);

            boolean permission1 = requestAudioPermissions();
            boolean permission2 = checkDrawOverlayPermission();
            if(permission1&&permission2){
                fetchData();
            }


//////////////////////////////////////////////////////////////////////////////
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
            mAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListner() {
                @Override
                public void OnItemClicked(int position) {
                    Toast.makeText(getApplicationContext(),"Recording Clicked",Toast.LENGTH_LONG).show();
                    FragmentManager fm = getSupportFragmentManager();

                   RecordingPlayFragment editNameDialogFragment = RecordingPlayFragment.newInstance(item.get(position).getmText1());
                    editNameDialogFragment.show(fm, "fragment_edit_name");
                }
            });

    }
    private boolean requestAudioPermissions() {

        if (Build.VERSION.SDK_INT >= 23) {

            if ( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                Log.v("Permission :","Permission is granted");
                return true;
            } else {

                Log.v("Permission :","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission :","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                boolean isPerpermissionForAllGranted = false;
                if (grantResults.length > 0 && permissions.length == grantResults.length) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isPerpermissionForAllGranted = true;
                        } else {
                            isPerpermissionForAllGranted = false;
                        }
                    }

                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    isPerpermissionForAllGranted = true;
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                if (isPerpermissionForAllGranted) {

                }
                break;
        }
    }
    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                if(requestAudioPermissions()){
                    fetchData();
                }
            }
        }
    }
private void fetchData(){
    File folder = new File(Environment.getExternalStorageDirectory() +
            File.separator + "Whitenoise Recordings");
    boolean success = true;
    if (!folder.exists()) {
        success = folder.mkdirs();
    } else {
        String path = Environment.getExternalStorageDirectory().toString() + File.separator + "Whitenoise Recordings";

        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Date lastModified;
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            lastModified = new Date(files[i].lastModified());
            Log.d("Files", "FileName:" + files[i].getName());

            item.add(new ExampleItem(R.drawable.compact_disc_white, files[i].getName(), "" + lastModified));

        }
        //item.add(new ExampleItem(R.drawable.compact_disc,"Air Plane","Date:"));
        //item.add(new ExampleItem(R.drawable.compact_disc,"Air Plane","Date:"));
        //item.add(new ExampleItem(R.drawable.compact_disc,"Air Plane","Date:"));


        mRecylerView = (RecyclerView) findViewById(R.id.recyclerView_lib_recording);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecylerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecyclerAdapter(item);
        mRecylerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
}
