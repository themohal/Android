package com.example.farjad.filehandling;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    EditText myedittext;
    Button saveButton;
    Button retrieveButton;
    TextView mytextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myedittext =(EditText)findViewById(R.id.my_edittext);
        saveButton=(Button) findViewById(R.id.save_button);
        retrieveButton=(Button)findViewById(R.id.retrieve_Button);
        mytextView =(TextView)findViewById(R.id.textView);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    FileOutputStream fos=
                            openFileOutput("mytextfile.txt",MODE_PRIVATE);
                    OutputStreamWriter osw=new OutputStreamWriter(fos);
                    osw.write(myedittext.getText().toString());
                    osw.flush();
                    osw.close();

                    Toast.makeText(getApplicationContext(),"File Saved SuccessFully",Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });







        }
    }
