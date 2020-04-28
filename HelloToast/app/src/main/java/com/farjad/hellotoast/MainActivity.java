package com.farjad.hellotoast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int mCount=0;
    private TextView mShowcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShowcount = findViewById(R.id.show_count);
    }

    public void countUp(View view) {
        mCount++;
        if(mShowcount!=null){
            mShowcount.setText(Integer.toString(mCount));
        }
    }

    public void showToast(View view) {
        Toast toast =Toast.makeText(this,R.string.toast_message,Toast.LENGTH_LONG);
                toast.show();
    }
}
