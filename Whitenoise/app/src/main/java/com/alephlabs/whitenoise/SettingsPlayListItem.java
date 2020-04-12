package com.alephlabs.whitenoise;

public class SettingsPlayListItem {
    private int mImageView;
    private String mText1;



    private String mText2;


    public SettingsPlayListItem(int mImageView, String mText1, String mText2) {
        this.mImageView = mImageView;
        this.mText1 = mText1;
        this.mText2 = mText2;
    }

    public int getmImageView() {
        return mImageView;
    }

    public String getmText1() {
        return mText1;
    }
    public String getmText2() {
        return mText2;
    }

}


