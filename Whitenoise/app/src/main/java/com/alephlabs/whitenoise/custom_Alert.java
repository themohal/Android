package com.alephlabs.whitenoise;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class custom_Alert extends Dialog implements
        android.view.View.OnClickListener {
    public Activity c;
    public Dialog d;
    public String text;
    public  int check;
    public EditText playListName;
    public Button yes, no;
    int checkAlready;

    public playListSettings ins =new playListSettings();
   public ArrayList<SettingsPlayListItem> item ;
   public SettingsPlayListAdapter getAdapter ;

    public custom_Alert(Activity a,ArrayList<SettingsPlayListItem> item,SettingsPlayListAdapter getAdapter) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.item = item;
        this.getAdapter=getAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom__alert);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        playListName = findViewById(R.id.play_list_textEdit);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    public String getPlayListName() {
        return playListName.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                if(!playListName.getText().toString().matches("[a-zA-Z]+") &&playListName.getText().toString().length()<1&&playListName.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(),"Playlist Name Should Only Contain Characters Or Can't Be Empty",Toast.LENGTH_LONG).show();
                } else {
                    for (int j = 0; j < item.size(); j++) {
                        if (item.get(j).getmText1().equals(playListName.getText().toString())) {
                            Toast.makeText(getContext(), "Name Already Exists", Toast.LENGTH_LONG).show();
                            checkAlready = 1;
                        }
                    }
                }
                if(checkAlready==0&&playListName.getText().toString().matches("[a-zA-Z]+")  ){

                    ins.setTextPlaylist(playListName.getText().toString().trim());
                    item.add(0,new SettingsPlayListItem(R.drawable.playlist_cover,playListName.getText().toString().trim(),"0 songs"));
                    getAdapter.notifyDataSetChanged();
                    calltoShared();
                }

                //Toast.makeText(getContext(),""+ins.checkfromalert,Toast.LENGTH_LONG).show();
                //c.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
    public int intialize(){
        check=1;
        return check;
    }
    public void calltoShared() {
        ArrayList<String> read = new ArrayList<>();
        if (!item.isEmpty()) {
            read.clear();
            for (int j = 0; j < item.size(); j++) {
                read.add(j,item.get(j).getmText1());
                Log.v("Readed: ", "" + read.get(j));

            }

            Set<String> set = new HashSet<>();
            set.addAll(read);
            SharedPreferences keyValues = getContext().getSharedPreferences("list_play_list", Context.MODE_PRIVATE);
            SharedPreferences.Editor keyValuesEditor = keyValues.edit();
            keyValuesEditor.clear();
            keyValuesEditor.putStringSet("list_play_list", set);
            keyValuesEditor.apply();
            SharedPreferences readkeyValues = getContext().getSharedPreferences("list_play_list", Context.MODE_PRIVATE);
            Set<String> readSet;
            readSet = readkeyValues.getStringSet("list_play_list", null);
            ArrayList<String> sharedPrefList = new ArrayList<>();
            sharedPrefList.addAll(readSet);
            String s = "";

           for (int i = 0; i < sharedPrefList.size(); i++) {
                Log.v("Readed From Shared:", sharedPrefList.get(i));
                s = sharedPrefList.get(i);
                if (item.isEmpty()) {
                    item.add(i,new SettingsPlayListItem(R.drawable.playlist_cover, s, ""));
                    getAdapter.notifyDataSetChanged();
                } else if(!item.isEmpty()){
                    Toast.makeText(getContext(), "Ok added", Toast.LENGTH_LONG).show();
                }

           }

        }
    }

}

