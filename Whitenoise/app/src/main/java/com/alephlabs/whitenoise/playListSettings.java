package com.alephlabs.whitenoise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class playListSettings extends AppCompatActivity {
    public static Context context;
    private AdView mAdView;
    private RecyclerView mRecylerView;
    private SettingsPlayListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton mBackButton,mCreatePlaylist;
    private String PlayListName;
    private TextView songCount;

    private ArrayList<String> sharedPrefList = new ArrayList<>();

    public void setTextPlaylist(String textPlaylist) {
        this.textPlaylist = textPlaylist;
    }


    public  String textPlaylist;
    ArrayList<SettingsPlayListItem> item = new ArrayList<>();

    public ArrayList<SettingsPlayListItem> getItem() {
        return item;
    }

    public SettingsPlayListAdapter getmAdapter() {
        return mAdapter;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context =getApplicationContext();

        setContentView(R.layout.activity_play_list_settings);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("287BB39D093D5E59376400796EDFCC57").build();
        mAdView.loadAd(adRequest);


        Intent intent = getIntent();
        int delPlaylist= intent.getIntExtra("deleted",0);
        if(delPlaylist==1){
            IntializeList();
        }

        Intent intentFromNoises = getIntent();
        final String fromNoises= intentFromNoises.getStringExtra("fromnoises");
        if (fromNoises != null) {
            Log.v("From Noises:",fromNoises);
        }


////////////menu
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
//////////////////

        mCreatePlaylist = findViewById(R.id.play_list_create_image);
        mBackButton = (ImageButton) findViewById(R.id.play_list_lib_back_button);
        songCount = findViewById(R.id.play_list_song_count);

        //item.add(new SettingsPlayListItem(R.drawable.playlist_cover, "Air Plane", "Song Count"));


        mRecylerView = (RecyclerView) findViewById(R.id.recyclerView_playlist_settings);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecylerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new SettingsPlayListAdapter(item);
        mRecylerView.setAdapter(mAdapter);

        IntializeList();


        mCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom_Alert cdd = new custom_Alert(playListSettings.this, item, mAdapter);
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();



            }
        });
        mAdapter.setOnItemClickListener(new SettingsPlayListAdapter.OnItemClickListner() {
            @Override
            public void OnItemClicked(int position) {
               PlayListName=item.get(position).getmText1();
                Intent insideIntent= new Intent(getApplicationContext(),InsidePlaylist.class);

                insideIntent.putExtra("clicked", PlayListName);
                insideIntent.putExtra("fromNoises",fromNoises);
               startActivity(insideIntent);

                //use preference or sqlite

            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    /////////////////////////////////////////////////////////////

    private void IntializeList() {
            /////////////////////////////////
            SharedPreferences readkeyValues2 = getApplicationContext().getSharedPreferences("list_play_list", Context.MODE_PRIVATE);
            Set<String> readSet2;
            readSet2 = readkeyValues2.getStringSet("list_play_list", null);
        List<Integer> list = new ArrayList<Integer>();

            ArrayList<String> sharedPrefList = new ArrayList<>();
            if (readSet2 != null) {
                sharedPrefList.addAll(readSet2);
                Collections.sort(sharedPrefList);

                if(sharedPrefList!=null) {
                    for (int m = 0; m < readSet2.size(); m++) {
                        ///reading size of inside list
                        SharedPreferences readkeySizeInsideList = getApplicationContext().getSharedPreferences("playlist_" + sharedPrefList.get(m), Context.MODE_PRIVATE);
                        Set<String> readSize1;

                        readSize1 = readkeySizeInsideList.getStringSet("playlist_" + sharedPrefList.get(m), null);
                        if (readSize1 != null) {

                            list.add(readSize1.size());
                        }
                        else {
                            list.add(0);
                        }


                    }
                }
                ////////

            }
            String s = "";
//add data here
            for (int i = 0; i < sharedPrefList.size(); i++) {
                Log.v("Readed From Shared:", sharedPrefList.get(i));
                s = sharedPrefList.get(i);
                if (item.isEmpty()) {

                    item.add(i, new SettingsPlayListItem(R.drawable.playlist_cover,sharedPrefList.get(i), list.get(i)+" songs"));
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Item List Empty", Toast.LENGTH_LONG).show();
                } else if (!item.isEmpty()) {

                    item.add(new SettingsPlayListItem(R.drawable.playlist_cover, s, ""+list.get(i)+" songs"));
                    mAdapter.notifyDataSetChanged();
                }

            }

        }
public static Context getContext() {
        return context;
    }
    @Override
    protected void onResume() {
        super.onResume();
        item.clear();
        IntializeList();
    }
}