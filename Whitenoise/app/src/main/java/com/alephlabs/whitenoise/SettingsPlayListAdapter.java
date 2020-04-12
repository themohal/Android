package com.alephlabs.whitenoise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.alephlabs.whitenoise.playListSettings.context;
import static com.alephlabs.whitenoise.playListSettings.getContext;

public class SettingsPlayListAdapter extends RecyclerView.Adapter<SettingsPlayListAdapter.MyPlayListViewHolder>{
    public ArrayList<SettingsPlayListItem> mixerRecyclerArrayList;
    public SettingsPlayListAdapter.OnItemClickListner mListener;
    Context appContext = playListSettings.getContext();
    public interface OnItemClickListner{
        void OnItemClicked(int position);
    }

    public void setOnItemClickListener(SettingsPlayListAdapter.OnItemClickListner listener){
        mListener =listener;
    }

    public static class MyPlayListViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextview1;
        public ImageView mImageView;
        public TextView mTextview2;


        public MyPlayListViewHolder(@NonNull final View itemView, final SettingsPlayListAdapter.OnItemClickListner listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.setting_playlist_cover);
            mTextview1 = itemView.findViewById(R.id.play_list_name);
            mTextview2 = itemView.findViewById(R.id.play_list_song_count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!= null){
                        int postion = getAdapterPosition();
                        if(postion!= RecyclerView.NO_POSITION){
                            listener.OnItemClicked(postion);
                        }
                    }
                }
            });
        }
    }

    //constructor to pass data from main
    public SettingsPlayListAdapter(ArrayList<SettingsPlayListItem> mixerRecyclerItem) {
        mixerRecyclerArrayList = mixerRecyclerItem;
    }

    @NonNull
    @Override
    public SettingsPlayListAdapter.MyPlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_playlist_items, parent, false);
        SettingsPlayListAdapter.MyPlayListViewHolder evh = new SettingsPlayListAdapter.MyPlayListViewHolder(v,mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final SettingsPlayListAdapter.MyPlayListViewHolder holder, final int position) {
        SettingsPlayListItem currentItem = mixerRecyclerArrayList.get(position);
        holder.mImageView.setImageResource(currentItem.getmImageView());
        holder.mTextview1.setText(currentItem.getmText1());
        holder.mTextview2.setText(currentItem.getmText2());

    }

    @Override
    public int getItemCount() {
        return mixerRecyclerArrayList.size();
    }





    }