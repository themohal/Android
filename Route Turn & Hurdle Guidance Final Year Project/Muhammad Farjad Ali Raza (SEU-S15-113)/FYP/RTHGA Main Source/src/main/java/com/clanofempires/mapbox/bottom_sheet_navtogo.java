package com.clanofempires.mapbox;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.http.HttpRequestUrl;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.MetricsRouteProgress;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.services.android.navigation.v5.utils.DistanceFormatter;
import com.mapbox.services.android.navigation.v5.utils.time.TimeFormatter;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.api.directions.v5.DirectionsCriteria.IMPERIAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class bottom_sheet_navtogo extends BottomSheetDialogFragment {

    RelativeLayout layout;
    NavigationMapRoute navigationMapRoute;
    Button start;
    EditText durationAg;
    EditText distanceAg;
    public static double d;
    public static String p;
    private String durat;
    MainActivity s = new MainActivity();
    NavigationRoute navigationRoute;
    public static DirectionsRoute directionsRoute;
    public static Point destP;
    MapboxMap map ;
    MapView mapView;

    public bottom_sheet_navtogo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottom_sheet_navtogo, container, false);
        layout = (RelativeLayout) v.findViewById(R.id.startBottomsheet);
        start = (Button) v.findViewById(R.id.startb);
        durationAg = (EditText) v.findViewById(R.id.durationagain);
        distanceAg = (EditText) v.findViewById(R.id.distanceagain);
        mapView=s.getMapView();
        map=s.getMap();

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDurationDista(s.getOrignP(), s.getDestP());

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), navigation_view.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();

    }

    public void getDurationDista(final com.mapbox.geojson.Point origin, final com.mapbox.geojson.Point destination) {
        NavigationRoute.builder(getActivity())
                .accessToken("pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA")
                .origin(origin)
                .destination(destination)
                .voiceUnits(IMPERIAL)
                .alternatives(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Log.e("BottomSheet", "No routes found ,check again");
                            return;
                        } else if (response.body().routes().size() == 0) {
                            Log.e("BottomSheet", "No routes found ");
                            return;
                        } else {
                            String dzz = String.valueOf(TimeFormatter.formatTimeRemaining(getContext(), response.body().routes().get(0).duration()));
                            Log.e("Duration:", "" + dzz);

                            directionsRoute = response.body().routes().get(0);
                            durat = " " + dzz;
                            durationAg.setText(dzz);


                        }
                        if (response.body().routes().get(0).distance() != null) {
                            p = String.valueOf(response.body().routes().get(0));


                        } else {
                            p = "Dropped Pin";
                        }
                        Log.d("DU:", "" + p);
                        Log.e("Routes:", "" + response.body().routes().get(0).distance());

                        double distancec;
                        String dosdist;
                        if(response.body().routes().get(0).distance()>1000) {
                            distancec=(response.body().routes().get(0).distance())/(1000);
                            dosdist=String.valueOf(distancec);
                            dosdist=dosdist.substring(0,dosdist.indexOf(".") + 2);
                            distanceAg.setText("("+dosdist+" km)");

                        }
                        if(response.body().routes().get(0).distance()<=1000){

                            distancec=(response.body().routes().get(0).distance());
                            distanceAg.setText(distancec+" m");


                        }

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.d("BottomSheet", "Error:" + t.getMessage());
                    }
                });
    }
}
