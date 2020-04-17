package com.clanofempires.mapbox;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SRIVASTAVA on 1/9/2016.
 */
/*The instance of this class is called by "MainActivty",to get the time taken reach the destination from Google Distance Matrix API in background.
  This class contains interface "Geo" to call the function setDouble(String) defined in "MainActivity.class" to display the result.*/
public class distanceMatrix extends AsyncTask<String, Void, String> {
    ProgressDialog pd;
    Context mContext;
    Double duration;
    Double distance;
    public static String distanctomain;
    MainActivity mainActivity=new MainActivity();
    Double distacefinal;
    //constructor is used to get the context.
    public distanceMatrix(Context mContext) {
        this.mContext = mContext;
        Context main;

    }
    //This function is executed before before "doInBackground(String...params)" is executed to dispaly the progress dialog
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd=new ProgressDialog(mContext);
        pd.setMessage("Loading");
        pd.setCancelable(true);
       // pd.show();
    }
    //This function is executed after the execution of "doInBackground(String...params)" to dismiss the dispalyed progress dialog and call "setDouble(Double)" defined in "MainActivity.java"
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        pd.dismiss();
        Log.d("Dos:",""+s);
        distanctomain=s;
        try {
            distacefinal=Double.parseDouble(distanctomain);


        }
catch (NullPointerException e){
            e.fillInStackTrace();
}
finally {
            getDistancefromApi();

        }
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url=new URL(params[0]);
            HttpURLConnection con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int statuscode=con.getResponseCode();
            if(statuscode==HttpURLConnection.HTTP_OK)
            {
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb=new StringBuilder();
                String line=br.readLine();
                while(line!=null)
                {
                    sb.append(line);
                    line=br.readLine();
                }
                String json=sb.toString();
                Log.d("JSON",json);
                JSONObject root=new JSONObject(json);
                JSONArray array_rows=root.getJSONArray("rows");
                Log.d("JSON","array_rows:"+array_rows);
                JSONObject object_rows=array_rows.getJSONObject(0);
                Log.d("JSON","object_rows:"+object_rows);
                JSONArray array_elements=object_rows.getJSONArray("elements");
                Log.d("JSON","array_elements:"+array_elements);
                JSONObject  object_elements=array_elements.getJSONObject(0);
                Log.d("JSON","object_elements:"+object_elements);
                JSONObject object_duration=object_elements.getJSONObject("duration");
                JSONObject object_distance=object_elements.getJSONObject("distance");
                Log.d("ddd",""+object_distance);

                Log.d("JSON","object_duration:"+object_duration);
                return object_distance.getString("value");

            }
        } catch (MalformedURLException e) {
            Log.d("error", "error1");
        } catch (IOException e) {
            Log.d("error", "error2");
        } catch (JSONException e) {
            Log.d("error","error3");
        }


        return null;
    }


    public void getDistancefromApi() {
        AudioManager manager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        Log.d("Got it:", "" + distanctomain);
        if (distacefinal != null) {
            if (distacefinal < 1001 && distacefinal > 799) {
                final MediaPlayer playert = MediaPlayer.create(mContext, R.raw.meter_1000_slow);
                if (!manager.isMusicActive()) {
                    playert.start();
                }

                playert.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        playert.seekTo(0);
                        playert.stop();
                        playert.reset();
                        playert.release();
                    }
                });
            }
            //500 meter

            else if (distacefinal <= 500) {

                Log.d("Response", "500meter");


                final MediaPlayer playerft = MediaPlayer.create(mContext, R.raw.meter_500_slow);

                if (!manager.isMusicActive()) {
                    // do something - or do it not

                    playerft.start();
                }


                playerft.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.seekTo(0);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();


                    }
                });

            }
        } else {
            Toast.makeText(mContext, "There is some problem check internet and location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

    }
}


