package com.alephlabs.whitenoise;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{
    public ArrayList<com.alephlabs.whitenoise.ExampleItem> mRecyclerArrayList;
    public RecyclerAdapter.OnItemClickListner mListener;
    public interface OnItemClickListner{
        void OnItemClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListner listener){
        mListener =listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextview1;
        public TextView mTextview2;
        public ImageView mImageView;
        public Button mMenuButton;


        public MyViewHolder(@NonNull View itemView,final OnItemClickListner listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.recyclerImage);
            mTextview1 = itemView.findViewById(R.id.textTitlee);
            mTextview2 = itemView.findViewById(R.id.textDescription);
            mMenuButton = itemView.findViewById(R.id.buttonOptions);
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
    public RecyclerAdapter(ArrayList<ExampleItem> recyclerItem){
            mRecyclerArrayList =recyclerItem;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item,parent,false);
        MyViewHolder evh = new MyViewHolder(v,mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        ExampleItem currentItem = mRecyclerArrayList.get(position);
        holder.mImageView.setImageResource(currentItem.getmImageView());
        holder.mTextview1.setText(currentItem.getmText1());
        holder.mTextview2.setText(currentItem.getmText2());
        final Button button = holder.mMenuButton;
        holder.mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(holder.itemView.getContext(),R.style.menu_style);
                //PopupMenu popup = new PopupMenu(holder.itemView.getContext(),button);
                PopupMenu popup = new PopupMenu(wrapper,button);
                popup.inflate(R.menu.recordings_menu_recycler);


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.recycler_delete:
                                File folder = new File(Environment.getExternalStorageDirectory() +
                                        File.separator + "Whitenoise Recordings");
                                File file = new File(folder,holder.mTextview1.getText().toString());
                                boolean deleted = file.delete();
                                if(deleted){
                                    Log.v("Deleted:","File Deleted");
                                }
                                notifyItemRemoved(holder.getAdapterPosition());
                                mRecyclerArrayList.remove(position);
                                //integerList.remove(holder.getAdapterPosition());
                                //notifyItemRemoved(holder.getAdapterPosition());
                                //return true;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecyclerArrayList.size();
    }
}