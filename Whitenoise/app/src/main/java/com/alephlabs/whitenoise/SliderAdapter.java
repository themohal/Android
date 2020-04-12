package com.alephlabs.whitenoise;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder>  {
    private List<SliderItem> sliderItems;
    private ViewPager2 viewPager2;
    TextView textView;
    Noises s= new Noises();

    private String[] texts = {"Air Plane", "Relaxation For Babies", "Hair Dryer"};

    SliderAdapter(List<SliderItem> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_container_viewpager
                        , parent
                        , false

                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(sliderItems.get(position));

    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (position == sliderItems.size() - 2) {

           viewPager2.post(runnable);

        }
    }



    @Override
    public int getItemCount() {
        return sliderItems.size();
    }


    static class SliderViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imageView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        void setImage(SliderItem sliderItem) {
            imageView.setImageResource(sliderItem.getImage());
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

           sliderItems.addAll(sliderItems);

                // sliderItems.remove(sliderItems.size() - 1);
                //int scrollPositionRecreate =sliderItems.size();
                //int currentSize = scrollPositionRecreate;
                //int nextLimit = currentSize + 10;

                //while (currentSize - 1 < nextLimit) {
                  //sliderItems.addAll(sliderItems);
                //currentSize++;
                //}
            notifyDataSetChanged();
        }
    };


}