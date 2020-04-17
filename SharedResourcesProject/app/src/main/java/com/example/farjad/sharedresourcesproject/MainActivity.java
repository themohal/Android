package com.example.farjad.sharedresourcesproject;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText eText;
        Button saveButton;
        eText =(EditText) findViewById(R.id.mytextedit);
        saveButton=(Button) findViewById(R.id.save_button);

        SharedPreferences prefs2=getSharedPreferences("mypref",MODE_PRIVATE);
        eText.setText(prefs2.getString("text1","First Time"));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saving data
                SharedPreferences prefs = getSharedPreferences("mypref", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("text1", eText.getText().toString());
                editor.commit();
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

