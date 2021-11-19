package com.example.crmapp;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.crmapp.databases.DbHandler;
import com.example.crmapp.interfaces.OnOptimizedRouteTaskCompleted;
import com.example.crmapp.interfaces.OnRouteTaskCompleted;
import com.example.crmapp.models.Client;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RouteMap extends AppCompatActivity implements OnMapReadyCallback, BottomInfoDialog.BottomInfoListener, OnRouteTaskCompleted, OnOptimizedRouteTaskCompleted, LocationListener {

    MapView mapAllClients;
    ArrayList<Integer> selectedIDs;
    ArrayList<Client> selectedClients = new ArrayList<>();
    GoogleMap Map;
    TextView distance;
    ImageView optimizeRoute;

    LocationManager locationManager;

    Dialog dialog;

    DbHandler DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);

        getLocation();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }

        distance = findViewById(R.id.distance);
        optimizeRoute = findViewById(R.id.optimizeRoute);

        mapAllClients = findViewById(R.id.mapRoute);
        mapAllClients.onCreate(savedInstanceState);
        mapAllClients.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbarRouteMap);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Bundle bundle = getIntent().getExtras();
        selectedIDs = (ArrayList<Integer>) bundle.getSerializable("clientList");

        DB = new DbHandler(this);
        for (int i=0; i<selectedIDs.size(); i++) {
            int id = selectedIDs.get(i);
            selectedClients.add(DB.getClient(id));
        }

        dialog = new Dialog(this, R.style.LoadingDialog);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(false);
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;
        LatLng localization;

        if (selectedClients.size() > 0) {
            for (int i = 0; i < selectedClients.size(); i++) {
                Client client = selectedClients.get(i);
                localization = new LatLng(client.getGeoLat(), client.getGeoLon());
                googleMap.addMarker(new MarkerOptions().position(localization).title(client.getName() + " " + client.getLastName() + " " + client.getStreet() + " " + client.getPostalCode() + " " + client.getCity())
                        .icon(vectorToBitmap()));
            }
        }
        LatLng Poland = new LatLng(52.065162, 19.252522);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(Poland));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Poland, 5.8f));

        String loc = "53.47057908236347,15.333281598852398";
        StringBuilder cities = new StringBuilder();
        for(int i=0;i<selectedClients.size();i++) {
            cities.append("&to=").append(selectedClients.get(i).getGeoLat()).append(",").append(selectedClients.get(i).getGeoLon());
        }
        String url = "https://open.mapquestapi.com/directions/v2/route?key=VBIRcPsPXNsywLQOzknMJwaus13ueGVm&from="+loc+cities+"&outFormat=json&unit=k";

        StringBuilder citiesOptimizedRoute = new StringBuilder();
        for(int i=0;i<selectedClients.size();i++) {
            if(i==selectedClients.size()-1){
                citiesOptimizedRoute.append("\"").append(selectedClients.get(i).getGeoLat()).append(",").append(selectedClients.get(i).getGeoLon()).append("\"");
            }
            else {
                citiesOptimizedRoute.append("\"").append(selectedClients.get(i).getGeoLat()).append(",").append(selectedClients.get(i).getGeoLon()).append("\"").append(",");
            }
        }
        String urlOptimizedRoute = "https://www.mapquestapi.com/directions/v2/optimizedroute?key=VBIRcPsPXNsywLQOzknMJwaus13ueGVm&json={'locations':['"+loc+"',"+citiesOptimizedRoute+"]}";
        getRouteTask task = new getRouteTask(RouteMap.this);
        dialog.show();
        task.execute(url);

        getOptimizedRouteTask ORtask = new getOptimizedRouteTask(RouteMap.this);
        optimizeRoute.setOnClickListener(v-> {
                    dialog.show();
                    ORtask.execute(urlOptimizedRoute);
                });

        googleMap.setOnMarkerClickListener(marker -> {
            BottomInfoDialog bottomDialog = new BottomInfoDialog();
            Bundle args = new Bundle();
            int id = selectedClients.get(Integer.parseInt(marker.getId().substring(1))).get_ID();
            args.putString("id", String.valueOf(id));
            bottomDialog.setArguments(args);
            bottomDialog.show((this).getSupportFragmentManager(), bottomDialog.getTag());
            return false;
        });
    }

    private BitmapDescriptor vectorToBitmap() {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_location_on_24, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        //DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onStart(){
        super.onStart();
        mapAllClients.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapAllClients.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapAllClients.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mapAllClients.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mapAllClients.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState){
        super.onSaveInstanceState(outState, outPersistentState);
        mapAllClients.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapAllClients.onLowMemory();
    }

    @Override
    public void onButtonClicked(PolylineOptions polyline) {
        Map.addPolyline(polyline);
    }

    @Override
    public void onTaskCompleted(String[] str) {
        dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(str[0]);
            JSONObject rou = jsonObject.getJSONObject("route");
            String dist = rou.getString("distance");

            distance.setText(dist);

            JSONObject jsonObject1 = new JSONObject(str[1]);
            JSONObject rt = jsonObject1.getJSONObject("route");
            JSONObject shape = rt.getJSONObject("shape");
            JSONArray shapePoints = shape.getJSONArray("shapePoints");

            PolylineOptions polyLine = new PolylineOptions();
            for (int j = 0; j < shapePoints.length(); j+=2) {
                polyLine.add(new LatLng(Double.parseDouble(shapePoints.getString(j)),
                        Double.parseDouble(shapePoints.getString(j + 1))));
            }
            polyLine.color(0xFFEA0909);
            Map.addPolyline(polyLine);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOptimizedRouteTaskCompleted(String[] str) {
        dialog.dismiss();
            try {
                JSONObject jsonObjectOR = new JSONObject(str[0]);
                JSONObject routeOR = jsonObjectOR.getJSONObject("route");

                DecimalFormat df = new DecimalFormat("#.####");
                double distOR = routeOR.getDouble("distance");
                double distanceOR = distOR * 1.609344;
                distance.setText(df.format(distanceOR));

                JSONObject jsonObject2OR = new JSONObject(str[1]);
                JSONObject rtOR = jsonObject2OR.getJSONObject("route");
                JSONObject shapeOR = rtOR.getJSONObject("shape");
                JSONArray shapePointsOR = shapeOR.getJSONArray("shapePoints");

                PolylineOptions polyLineOR = new PolylineOptions();
                for (int j = 0; j < shapePointsOR.length(); j += 2) {
                    polyLineOR.add(new LatLng(Double.parseDouble(shapePointsOR.getString(j)),
                            Double.parseDouble(shapePointsOR.getString(j + 1))));
                }
                polyLineOR.color(0xFF1E88E5);
                Map.addPolyline(polyLineOR);

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        String loc = location.getLatitude() +","+location.getLongitude();

        /*StringBuilder cities = new StringBuilder();
        for(int i=0;i<selectedClients.size();i++) {
            cities.append("&to=").append(selectedClients.get(i).getGeoLat()).append(",").append(selectedClients.get(i).getGeoLon());
        }
        String url = "https://open.mapquestapi.com/directions/v2/route?key=VBIRcPsPXNsywLQOzknMJwaus13ueGVm&from="+loc+""+cities+"&outFormat=json&unit=k";

        StringBuilder citiesOptimizedRoute = new StringBuilder();
        for(int i=0;i<selectedClients.size();i++) {
            if(i==selectedClients.size()-1){
                citiesOptimizedRoute.append("\"").append(selectedClients.get(i).getGeoLat()).append(",").append(selectedClients.get(i).getGeoLon()).append("\"");
            }
            else {
                citiesOptimizedRoute.append("\"").append(selectedClients.get(i).getGeoLat()).append(",").append(selectedClients.get(i).getGeoLon()).append("\"").append(",");
            }
        }
        String urlOptimizedRoute = "https://www.mapquestapi.com/directions/v2/optimizedroute?key=VBIRcPsPXNsywLQOzknMJwaus13ueGVm&json={'locations':['"+loc+"',"+citiesOptimizedRoute+"]}";
        getRouteTask task = new getRouteTask(RouteMap.this);
        task.execute(url, urlOptimizedRoute);*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}


