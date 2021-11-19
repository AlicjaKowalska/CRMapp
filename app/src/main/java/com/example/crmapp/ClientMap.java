package com.example.crmapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.example.crmapp.databases.DbHandler;

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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class ClientMap extends AppCompatActivity implements OnMapReadyCallback, BottomInfoDialog.BottomInfoListener{

    MapView mapAllClients;
    DbHandler DB;
    ArrayList<Client> mapClients;
    GoogleMap Map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_map);

        mapAllClients = findViewById(R.id.mapAllClients);
        mapAllClients.onCreate(savedInstanceState);
        mapAllClients.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbarMap);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        DB = new DbHandler(this);
        mapClients = DB.getAllClientsData();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;
        LatLng localization;
        if (mapClients.size() > 0) {
            for (int i = 0; i < mapClients.size(); i++) {
                Client client = mapClients.get(i);
                localization = new LatLng(client.getGeoLat(), client.getGeoLon());
                googleMap.addMarker(new MarkerOptions().position(localization).title(client.getName() + " " + client.getLastName() + " " + client.getStreet() + " " + client.getPostalCode() + " " + client.getCity())
                        .icon(vectorToBitmap()));
            }
        }
        LatLng Poland = new LatLng(52.065162, 19.252522);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(Poland));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Poland, 5.8f));


        googleMap.setOnMarkerClickListener(marker -> {
            BottomInfoDialog bottomDialog = new BottomInfoDialog();
            Bundle args = new Bundle();
            int id = mapClients.get(Integer.parseInt(marker.getId().substring(1))).get_ID();
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
}