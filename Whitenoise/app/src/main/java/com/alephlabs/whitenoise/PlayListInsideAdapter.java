package com.alephlabs.whitenoise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlayListInsideAdapter extends RecyclerView.Adapter<PlayListInsideAdapter.MyPlayListInsideViewHolder> {
    public ArrayList<PlayListInsideItems> mixerRecyclerArrayList;
    public PlayListInsideAdapter.OnItemClickListner mListener;



    public interface OnItemClickListner{
        void OnItemClicked(int position);
    }

    public void setOnItemClickListener(PlayListInsideAdapter.OnItemClickListner listener){
        mListener =listener;
    }

    public static class MyPlayListInsideViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextview1;
        public ImageView mImageView;

        public MyPlayListInsideViewHolder(@NonNull final View itemView, final PlayListInsideAdapter.OnItemClickListner listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.mixer_recycler_Image);
            mTextview1 = itemView.findViewById(R.id.mixer_text);
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
    public PlayListInsideAdapter(ArrayList<PlayListInsideItems> mixerRecyclerItem) {
        mixerRecyclerArrayList = mixerRecyclerItem;
    }

    @NonNull
    @Override
    public PlayListInsideAdapter.MyPlayListInsideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlistinsideitems, parent, false);
        PlayListInsideAdapter.MyPlayListInsideViewHolder evh = new PlayListInsideAdapter.MyPlayListInsideViewHolder(v,mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayListInsideAdapter.MyPlayListInsideViewHolder holder, int position) {
        PlayListInsideItems currentItem = mixerRecyclerArrayList.get(position);
        holder.mImageView.setImageResource(currentItem.getmImageView());
        holder.mTextview1.setText(currentItem.getmText1());

    }

    @Override
    public int getItemCount() {
        return mixerRecyclerArrayList.size();
    }
}