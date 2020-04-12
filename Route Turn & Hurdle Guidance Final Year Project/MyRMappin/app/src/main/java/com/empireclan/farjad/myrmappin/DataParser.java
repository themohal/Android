package com.empireclan.farjad.myrmappin;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DataParser {

   public static String distance;
   public static String duration;
    public static String distancemilgaya;
    public static String durationmilgaya;

    JSONObject distOb;
    JSONObject timeOb;
    public static String forStartAddress =  ("");
    public static String forEndAddress =  ("");

    public static String startAddressMilgaya=("");
    public static String endAddressMilgaya=("");



    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject)  {

        List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;


        try {

            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();


                JSONObject newDisTimeOb = jLegs.getJSONObject(0);

                distOb = newDisTimeOb.getJSONObject("distance");
                 timeOb = newDisTimeOb.getJSONObject("duration");
                    distance= distOb.getString("text");
                    duration = timeOb.getString("text");

                JSONObject startAdd = jLegs.getJSONObject(0);
                forStartAddress = String.valueOf(startAdd.getString("start_address"));


                JSONObject endAdd = jLegs.getJSONObject(0);
                forEndAddress = String.valueOf(endAdd.getString("end_address"));
                endAddressMilgaya=forEndAddress;

                Log.i("Distance :", distOb.getString("text"));
                Log.i("Time :", timeOb.getString("text"));
                Log.i("Start Address :", forStartAddress);
                Log.i("End Address :", forEndAddress);
                getEnd();





                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");





                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude) );
                            hm.put("lng", Double.toString((list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }
            distance = distOb.getString("text");
            duration = timeOb.getString("text");

            Log.d("mDistance :", distance);
            Log.d("mTime :", duration);


        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }


        return routes;

    }

    public String getEnd() // time in milliseconds
    {
        endAddressMilgaya=forEndAddress;
        return endAddressMilgaya;
    }
    public String getStart() // time in milliseconds
    {
        startAddressMilgaya=forStartAddress;
        return startAddressMilgaya;
    }
    public static String getDistance() // time in milliseconds
    {
        distancemilgaya=distance;
        return distancemilgaya;
    }
    public static String getDuration() // time in milliseconds
    {
        durationmilgaya=duration;
        return durationmilgaya;
    }



    /**
     * Method to decode polyline points
     * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}

