package com.clanofempires.mapbox;

import android.arch.lifecycle.LifecycleObserver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.utils.time.TimeFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.api.directions.v5.DirectionsCriteria.IMPERIAL;


public class bottom_sheet_direction extends BottomSheetDialogFragment implements LifecycleObserver {
    RelativeLayout layout;
    private NavigationMapRoute navigationMapRoute;
    Button navbutton;
    private NavigationView navigationView;
    EditText durationTime;
    EditText placeName;
    public static double d;
    public static String p;
    private String durat;
    MainActivity s=new MainActivity();
    NavigationRoute navigationRoute;
    public static DirectionsRoute directionsRoute;
    private DirectionsRoute currentRoute;
    public static Point destP;
    MapboxMap map=s.getMap();
    MapView mapView=s.getMapView();
    static Polyline polyline;




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new LongOperation().execute();

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_direction, container, false);
        layout = (RelativeLayout) v.findViewById(R.id.directionBottomsheet);
        navbutton = (Button) v.findViewById(R.id.navButton);
        durationTime=(EditText) v.findViewById(R.id.duration);
        placeName=(EditText) v.findViewById(R.id.placeName);
        mapView=s.getMapView();
        map=s.getMap();
        navigationView = new com.mapbox.services.android.navigation.ui.v5.NavigationView(getContext());

        return v;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navbutton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
               drawline(s.getOrignP(),s.getDestP());
               if (navigationMapRoute != null) {
                   navigationMapRoute.removeRoute();
               }
               LatLng oLatlng=new LatLng(s.getOrignP().latitude(),s.getOrignP().longitude());
               LatLng dLatlng=new LatLng(s.getDestP().latitude(),s.getDestP().longitude());
               LatLngBounds latLngBounds = new LatLngBounds.Builder()
                       .include(new LatLng(oLatlng)) // Northeast
                       .include(new LatLng(dLatlng)) // Southwest
                       .build();
               map.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 250), 3000);
            openBottomSheet();

              //Intent i = new Intent(getContext(), navigation_view.class);
              //startActivity(i);
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();

            }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        }
dialog.cancel();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        }

    dialog.dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        }

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

public void getRoute(com.mapbox.geojson.Point origin, final com.mapbox.geojson.Point destination) {
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

                            directionsRoute=response.body().routes().get(0);
                            durat = " " + dzz;
                            durationTime.setText("Duration: "+dzz);


                        }
                            if(response.body().routes().get(0).legs().get(0).summary()!=null) {
                                p = response.body().routes().get(0).legs().get(0).summary();
                            }
                            else {
                                p="Dropped Pin";
                            }
                        placeName.setText(p);

                        }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.d("BottomSheet", "Error:" + t.getMessage());
                    }
                });
    }
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
            Point oP=s.getOrignP();
            Point dP=s.getDestP();
            Log.d("DUEo:",""+oP);
            Log.d("DUEd:",""+dP);
            getRoute(oP,dP);



        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    public DirectionsRoute getDirectionRouteFromBottomSheet(){
        return directionsRoute;
    }
    public void drawline(com.mapbox.geojson.Point origin, final com.mapbox.geojson.Point destination) {
        NavigationRoute.builder(getActivity())
                .accessToken("pk.eyJ1IjoidGhlbW9oYWwiLCJhIjoiY2pyMjNpcGFnMDFmazQzcWZibmo2cGpucyJ9.O59EXJDox8d5WLmM4qGRGA")
                .origin(origin)
                .destination(destination)
                .voiceUnits(IMPERIAL)
                .alternatives(true)
                .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
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
                            currentRoute=response.body().routes().get(0);
                            // Draw the route on the map
                            if (navigationMapRoute != null) {
                                navigationMapRoute.removeRoute();
                            } else {

                                //navigationMapRoute = new NavigationMapRoute(null, mapView, map);


                            }
                            rRoute(currentRoute);

                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.d("BottomSheet", "Error:" + t.getMessage());
                    }
                });
    }
    private void openBottomSheet() {
        bottom_sheet_navtogo merafragment = new bottom_sheet_navtogo();
        merafragment.show(getChildFragmentManager(), "BottomSheetlatlong");
    }

    private void rRoute(DirectionsRoute route) {


            List<LatLng> points = new ArrayList<>();
            List<com.mapbox.geojson.Point> coords = LineString.fromPolyline(route.geometry(), Constants.PRECISION_6).coordinates();

            for (com.mapbox.geojson.Point point : coords) {
                points.add(new LatLng(point.latitude(), point.longitude()));
            }


            if (!points.isEmpty()) {
                if (polyline != null) {
                    map.removePolyline(polyline);

                }
                polyline = map.addPolyline(new PolylineOptions()
                        .addAll(points)
                        .color(R.color.colorAccent)
                        .width(10));
            }
        }

}