package com.alephlabs.whitenoise;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MixerRecyclerAdapter extends RecyclerView.Adapter<MixerRecyclerAdapter.MyMixerViewHolder> {
    public ArrayList<MixerPlayListItems> mixerRecyclerArrayList;
    public OnItemClickListner mListener;
    public interface OnItemClickListner{
        void OnItemClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListner listener){
        mListener =listener;
    }

    public static class MyMixerViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextview1;
        public ImageView mImageView;

        public MyMixerViewHolder(@NonNull final View itemView, final OnItemClickListner listener) {
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
    public MixerRecyclerAdapter(ArrayList<MixerPlayListItems> mixerRecyclerItem) {
        mixerRecyclerArrayList = mixerRecyclerItem;
    }

    @NonNull
    @Override
    public MyMixerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mixer_recycler_item, parent, false);
        MyMixerViewHolder evh = new MyMixerViewHolder(v,mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyMixerViewHolder holder, int position) {
        MixerPlayListItems currentItem = mixerRecyclerArrayList.get(position);
        holder.mImageView.setImageResource(currentItem.getmImageView());
        holder.mTextview1.setText(currentItem.getmText1());

    }

    @Override
    public int getItemCount() {
        return mixerRecyclerArrayList.size();
    }
}