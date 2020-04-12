package com.alephlabs.whitenoise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class mixer_playlist extends AppCompatActivity {
    private RecyclerView mRecylerView;
    private MixerRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    MediaPlayer mediaPlayer1,mediaPlayer2,mediaPlayer3,mediaPlayer4;
    SeekBar seekBar1,seekBar2,seekBar3,seekBar4;
    private ArrayList<MixerPlayListItems> mixerPlayListItems ;
    private List<Integer> audioFileArrayChill;
    private final static int MAX_VOLUME = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixer_playlist);
        mixerPlayListItems = new ArrayList<MixerPlayListItems>();
        seekBar1 = findViewById(R.id.seekbar1);

        audioFileArrayChill = new ArrayList<Integer>();
        audioFileArrayChill.add(R.raw.aac_airplane_white_noise1);
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.air_plane, "Air Plane"));
        mixerPlayListItems.add(new MixerPlayListItems(R.drawable.adult_ancient, "Adult Bed Child"));

        mRecylerView = (RecyclerView) findViewById(R.id.mixer_recycler_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecylerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MixerRecyclerAdapter(mixerPlayListItems);
        mRecylerView.setAdapter(mAdapter);

    }
}
