package com.example.crmapp;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.crmapp.databases.DbHandler;
import com.example.crmapp.interfaces.OnWeatherTaskCompleted;
import com.example.crmapp.models.Client;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClientDetails extends AppCompatActivity implements OnMapReadyCallback/*, OnWeatherTaskCompleted*/ {

    TextView Name, LastName, Building, Street, City, PostalCode;
    ImageButton ClientEdit, ClientDelete;
    ImageView Image;
    int id;
    DbHandler DB;
    MapView clientMapView;
    String name, lastname, city, street, postalcode;
    float lon, lat;

    TextView weatherCity, weatherDegrees, weatherDescription;
    ImageView weatherIcon;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchActivity;
    TextView clientActivity;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientdetails);

        clientMapView = findViewById(R.id.clientMapView);
        clientMapView.onCreate(savedInstanceState);
        clientMapView.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ClientDetails.this, ClientList.class);
            startActivity(intent);
        });

        Name = findViewById(R.id.ClientDetailsName);
        LastName = findViewById(R.id.ClientDetailsLastName);
        Building = findViewById(R.id.ClientDetailsBuilding);
        Street = findViewById(R.id.ClientDetailsStreet);
        City = findViewById(R.id.ClientDetailsCity);
        PostalCode = findViewById(R.id.ClientDetailsPostalCode);
        Image = findViewById(R.id.ClientImage);

        id = getIntent().getIntExtra("clientID", 0);

        DB = new DbHandler(this);
        Client client = DB.getClient(id);

        Name.setText(client.getName());
        LastName.setText(client.getLastName());
        Building.setText(String.valueOf(client.getBuilding()));
        Street.setText(client.getStreet());
        City.setText(client.getCity());
        PostalCode.setText(client.getPostalCode());
        Image.setImageBitmap(client.getImage());

        lat = client.getGeoLat(); lon = client.getGeoLon();
        name = client.getName(); lastname = client.getLastName();
        street = client.getStreet(); city = client.getCity(); postalcode = client.getPostalCode();

        ////////////////////////////////////weather/////////////////////////////////////////////////
        weatherCity = findViewById(R.id.weatherCity);
        weatherDegrees = findViewById(R.id.weatherDegrees);
        weatherDescription = findViewById(R.id.weatherDescription);
        weatherIcon = findViewById(R.id.weatherIcon);

        weatherCity.setText(city);

        String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=0b31c9eeb4841826abcd1a77fd03ab7f&units=Metric";
        //getWeatherTask weatherTask = new getWeatherTask(ClientDetails.this);
        //weatherTask.execute(url);

        Handler handlerUi = new Handler(Looper.getMainLooper());
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            Pair<String[], Bitmap> weatherResult;
            @Override
            public void run() {
                String contents = getURLString(url);
                try {
                    JSONObject jsonObject = new JSONObject(contents);
                    JSONObject main = jsonObject.getJSONObject("main");
                    JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
                    String degrees = String.valueOf(Math.round(main.getDouble("temp")));
                    String description = weather.getString("description");
                    String nameIcon = weather.getString("icon");

                    String urlIcon = "https://openweathermap.org/img/wn/"+ nameIcon +"@2x.png";
                    URL url = new URL(urlIcon);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    String[] weatherStrings = new String[2];
                    weatherStrings[0] = degrees;
                    weatherStrings[1] = description;
                    weatherResult = new Pair<>(weatherStrings, bitmap);

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                handlerUi.post(new Runnable() {
                    @Override
                    public void run() {
                        //wątek główny
                        String dg = weatherResult.first[0];
                        String descr = weatherResult.first[1];
                        Bitmap icon = weatherResult.second;

                        weatherDegrees.setText(dg);
                        weatherDescription.setText(descr);
                        weatherIcon.setImageBitmap(icon);
                    }
                });
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////switch//////////////////////////////////////////
        clientActivity = findViewById(R.id.clientActivity);
        switchActivity = findViewById(R.id.switchActivity);
        if(client.getActivity()==1) {
            switchActivity.setChecked(true);
            clientActivity.setTextColor(getColor(R.color.secondary));
            clientActivity.setText("AKTYWNY");
        }
        else {
            switchActivity.setChecked(false);
            clientActivity.setTextColor(getColor(R.color.lightg));
            clientActivity.setText("NIEAKTYWNY");
        }
        switchActivity.setOnClickListener( v -> {
            if(switchActivity.isChecked()){
                clientActivity.setTextColor(getColor(R.color.secondary));
                clientActivity.setText("AKTYWNY");
                client.setActivity(1);
                DB.updateClient(client);
            }
            else{
                clientActivity.setTextColor(getColor(R.color.lightg));
                clientActivity.setText("NIEAKTYWNY");
                client.setActivity(0);
                DB.updateClient(client);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////


        ClientEdit = findViewById(R.id.ClientDetailsEdit);
        ClientEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ClientDetails.this, EditClient.class);
            intent.putExtra("editID", id);
            startActivity(intent);
        });

        ClientDelete = findViewById(R.id.ClientDelete);
        ClientDelete.setOnClickListener(v -> {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Usunąć klienta?");
            alertDialogBuilder.setPositiveButton("tak",
                    (arg0, arg1) -> {
                        DB.deleteClient(id);
                        //DB.deleteAll();
                        Intent intent = new Intent(ClientDetails.this, ClientList.class);
                        startActivity(intent);
                    });

            alertDialogBuilder.setNegativeButton("Nie", (dialog, which) -> {

            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng localization = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions().position(localization).title(name+" "+lastname)).setIcon(vectorToBitmap());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(localization));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localization, 15f));
    }

    @Override
    protected void onStart(){
        super.onStart();
        clientMapView.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        clientMapView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        clientMapView.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
        clientMapView.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        clientMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState){
        super.onSaveInstanceState(outState, outPersistentState);
        clientMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        clientMapView.onLowMemory();
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

    /*@Override
    public void onWeatherTaskCompleted(String degrees, String description, Bitmap iconBitmap) {

        weatherDegrees.setText(degrees);
        weatherDescription.setText(description);
        weatherIcon.setImageBitmap(iconBitmap);
    }*/


    private String getURLString(String url){
        String contents ="";

        try {
            URLConnection conn = new URL(url).openConnection();

            InputStream in = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(in, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            contents = sb.toString();
        } catch (IOException e) {
            Log.e(e.getMessage(), e.getMessage());
        }

        return contents;
    }
}
