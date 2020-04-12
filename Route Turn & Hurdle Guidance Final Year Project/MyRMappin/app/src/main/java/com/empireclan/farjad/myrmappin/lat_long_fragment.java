package com.empireclan.farjad.myrmappin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class lat_long_fragment extends BottomSheetDialogFragment implements DialogInterface.OnDismissListener {
    String startAddress;
    String endAddress;
    String distance;
    String duration;
    double mymarkerlat;
    double mymarkerlongi;
    MainActivity ss= new MainActivity();
    GoogleMap mMap=ss.getMapObj();
    LatLng currentLatLng;
    public static PolylineOptions lineOptions ;
   public static List<HashMap<String, String>> lat;
    public static HashMap<String, String> longi;
     HashMap<String, String> Main;
    public static boolean clicked=false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Button direction;
        Button navButton;
        final TextView tv1;
        final TextView tv2;
        mymarkerlat = ss.getlat();
        mymarkerlongi = ss.getlong();
        final LatLng destmark=new LatLng(mymarkerlat,mymarkerlongi);
        currentLatLng=ss.getCurrentLatLng();
//call this method in your onCreateMethod



        View v=inflater.inflate(R.layout.fragment_lat_long,container,false);

        direction = (Button) v.findViewById(R.id.directButton);
        tv1 = (TextView) v.findViewById(R.id.latV);
        tv2 = (TextView) v.findViewById(R.id.lonV);

        tv1.setText("" + mymarkerlat);
        tv2.setText("" + mymarkerlongi);

        direction.setOnClickListener(new View.OnClickListener() {
                      @Override
                    public void onClick(View view) {
                          Log.d("Fragment :", "" + mymarkerlat + "longi" + mymarkerlongi + "current: " + currentLatLng);

// Assign your origin and destination
// These points are your markers coordinates
// Getting URL to the Google Directions API
                          LatLng direc = new LatLng(mymarkerlat, mymarkerlongi);

drawpoly(currentLatLng,direc);




            }


            });
        return v;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /////////////////////////////////////////

        // Fetches data from url passed
        class FetchUrl extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... url) {

                // For storing data from web service
                String data = "";

                try {
                    // Fetching the data from web service
                    data = downloadUrl(url[0]);
                    Log.d("Background Task data", data.toString());
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                ParserTask parserTask = new ParserTask();

                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);

            }
        }


        private String downloadUrl (String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                Log.d("downloadUrl", data.toString());


                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }


        static String getDirectionsUrl(LatLng origin, LatLng dest){

            // Origin of route
            String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

            // Destination of route
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = str_origin + "&" + str_dest + "&" + sensor;

            // Output format
            String output = "json";
            //api key

            String directionApi = "AIzaSyDf8n3gedQzaIc9kwVjyUzVBA_v2HPwjCQ";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + sensor + "&waypoints:optimize" + "&mode=driving" + "&alternatives=true" + "&departure_time=now" + "&arrival_time" + "&maneuver" + "enc:lexeF{~wsZejrPjtye@:" + "&key=" + directionApi;

            return url;
        }


        private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jObject = new JSONObject(jsonData[0]);
                    Log.d("ParserTask", jsonData[0].toString());
                    DataParser parser = new DataParser();
                    Log.d("ParserTask", parser.toString());


                    // Starts parsing data
                    routes = parser.parse(jObject);
                    Log.d("ParserTask", "Executing routes");
                    Log.d("ParserTask", routes.toString());


                } catch (Exception e) {
                    Log.d("ParserTask", e.toString());
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                ArrayList<LatLng> points;


                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);
                    lat=result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        longi=path.get(j);


                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);


                        points.add(position);
                        //parsing


                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);

                    //
                    lineOptions.width(12);
                    lineOptions.geodesic(true);

                    lineOptions.color(Color.parseColor("#24ace2"));


                    Log.d("onPostExecute", "onPostExecute line options decoded");


                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    clicked=true;
                    mMap.clear();
                    mMap.addPolyline(lineOptions);

/////////////////problem here not getting values

                    LatLng dis = new LatLng(mymarkerlat, mymarkerlongi);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(dis);
                    markerOptions.draggable(true);
                    DataParser m = new DataParser();
                    startAddress = m.getStart();
                    endAddress = m.getEnd();
                    distance = m.getDistance();
                    duration = m.getDuration();
                    Log.d("End Address :", endAddress);
                    markerOptions.title("Time :" + duration);
                    markerOptions.snippet("Distance = " + distance);
                    mMap.addMarker(markerOptions);

                    //open new bottomsheet
                    distance_duration distance_duration = new distance_duration();
                    distance_duration.show(getChildFragmentManager(), "dist_duration_frag");
                    //to cancel the present dialoge

                    onDismiss(getDialog());
                    onDetach();
                    onCancelled();
                    onDestroyView();
                    onDestroy();

                } else {
                    Log.d("onPostExecute", "without Polylines drawn");
                }
            }
        }
        public List<HashMap<String, String>> getLatlist(){
        return lat;
        }
    public HashMap<String, String> getLonglist(){
        return longi;
    }


//save the context recievied via constructor in a local variable


    @Override
    public void onDismiss(DialogInterface dialog) {

        dialog.dismiss();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public void drawpoly(LatLng origin, LatLng dest){

        String url = getDirectionsUrl(origin, dest);

        FetchUrl downloadTask = new FetchUrl();
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

// Start downloading json data from Google Directions API
        downloadTask.execute(url);
//hide the window
    }

}