package com.example.farjad.dbpro;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    EditText firstedittext,lastedittext;
    Button saveButton;
    Button retreieveButton;
    TextView resultText;
    Button updateButton;
    Button deleteButton;
    MyDatabaseHelper myDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstedittext=(EditText) findViewById(R.id.mytext1);
        lastedittext=(EditText) findViewById(R.id.mytext2);
        saveButton=(Button) findViewById(R.id.sbutton);
        resultText=(TextView)findViewById(R.id.tview);
        retreieveButton=(Button)findViewById(R.id.reterievebutton);
        updateButton=(Button) findViewById(R.id.button3);
        deleteButton=(Button) findViewById(R.id.button4);

        myDbHelper=new MyDatabaseHelper(getApplicationContext(),"mydb",null,1);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values=new ContentValues();
                values.put("fname",firstedittext.getText().toString());
                values.put("lname",lastedittext.getText().toString());
                SQLiteDatabase db= myDbHelper.getWritableDatabase();
                final long info = db.insert("info", null, values);
                Toast.makeText(getApplicationContext(),"Succcessfully saved",Toast.LENGTH_SHORT).show();


            }
        });
        retreieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resultText.setText(" ");
                SQLiteDatabase db = myDbHelper.getReadableDatabase();
                Cursor cr=db.query("info", new String[]{"fname","lname"},null,null,null,null,null);

                if(cr!=null&&cr.getCount()>=1) {
                    cr.moveToFirst();
                    do {
                        resultText.append(cr.getString(0)+" ");
                        resultText.append(cr.getString(1)+"\n");
                    }
                    while (cr.moveToNext());
                }
                else
                    {
                        Toast.makeText(getApplicationContext(),"No Record Found",Toast.LENGTH_SHORT).show();
                    }



            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = myDbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("fname",firstedittext.getText().toString());
                values.put("lname",lastedittext.getText().toString());
                db.update("info",  values , "fname='"+firstedittext.getText().toString()+"'",null);
                Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();

            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDbHelper.getWritableDatabase();
                db.delete("info","fname='"+firstedittext.getText().toString()+"'",null);
                Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
            }
        });


    }
}
