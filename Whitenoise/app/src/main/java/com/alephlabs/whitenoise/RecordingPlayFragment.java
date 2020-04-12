package com.alephlabs.whitenoise;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingPlayFragment extends DialogFragment  {
    private TextView mRecordingTitle;
    private SeekBar recordingSeekBar;
    private Handler mHandler;
    private Runnable mRunnable;
    private ImageButton playButton,pauseButton;
    private MediaPlayer recPlayer;



    public RecordingPlayFragment() {
        // Required empty public constructor
    }
    public static RecordingPlayFragment newInstance(String title) {

        RecordingPlayFragment frag = new RecordingPlayFragment();

        Bundle args = new Bundle();

        args.putString("title", title);

        frag.setArguments(args);

        return frag;

    }

  //  @Override
    //public View onCreateView(LayoutInflater inflater, ViewGroup container,
      //                       Bundle savedInstanceState) {
        //// Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_recording_play, container, false);


    //}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mHandler = new Handler();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_recording_play, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.create();
        mRecordingTitle = dialogView.findViewById(R.id.recordingNameTitle);
        recordingSeekBar = dialogView.findViewById(R.id.recordingSeekbar);
        playButton = dialogView.findViewById(R.id.recordingPlayBtn);
        pauseButton = dialogView.findViewById(R.id.recordingPauseBtn);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title");
        mRecordingTitle.setText(title);
        //alertDialogBuilder.setCustomTitle(mRecordingTitle);
            prepareRecording();
        pauseButton.setVisibility(View.INVISIBLE);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.INVISIBLE);
                recPlayer.start();
                initializeSeekBar();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                pausePlaying();
            }
        });
        recPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                if(recordingSeekBar.getProgress()==100) {
                    pausePlaying();
                }
            }
        });

        return alertDialogBuilder.show();
    }
    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

           // getDialog().show();

        //getDialog().getWindow().setLayout(280, 120);
        // Get field from view


        //String title = getArguments().getString("title", "hello this");

        //getDialog().setTitle(title);

        // Show soft keyboard automatically and request focus to field

        //mEditText.requestFocus();

        // getDialog().getWindow().setSoftInputMode(

        //WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //}
//}
    }
    private void prepareRecording() {
        String title = getArguments().getString("title");
        String path = Environment.getExternalStorageDirectory().toString() + File.separator + "Whitenoise Recordings"+"/"+title;
        File file = new File(path);
       if(file.exists()){
              Log.v("STATUS:","FILE EXISTS......")  ;
              recPlayer = MediaPlayer.create(getContext(), Uri.fromFile(file));

       }
}
    protected void getAudioStats(){
        int duration  = recPlayer.getDuration()/1000; // In milliseconds
        int due = (recPlayer.getDuration() - recPlayer.getCurrentPosition())/1000;
        int pass = duration - due;


    }
    protected void initializeSeekBar(){
        recordingSeekBar.setMax(recPlayer.getDuration()/1000);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(recordingSeekBar!=null){
                    int mCurrentPosition = recPlayer.getCurrentPosition()/1000; // In milliseconds
                    recordingSeekBar.setProgress(mCurrentPosition);
                    getAudioStats();
                }
                mHandler.postDelayed(mRunnable,1000);
            }
        };
        mHandler.postDelayed(mRunnable,1000);
    }
    protected void pausePlaying() {
        // If media player is not null then try to stop it
        if (recPlayer != null) {
            recPlayer.pause();
            Toast.makeText(getContext(), "Pause playing.", Toast.LENGTH_SHORT).show();
            if (mHandler != null) {
                mHandler.removeCallbacks(mRunnable);
            }
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (recPlayer != null) {
            recPlayer.stop();
            recPlayer=null;
            if (mHandler != null) {
                mHandler.removeCallbacks(mRunnable);
            }
        }
    }
}