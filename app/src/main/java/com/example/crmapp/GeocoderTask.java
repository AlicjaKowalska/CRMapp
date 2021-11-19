package com.example.crmapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.crmapp.interfaces.OnGeocoderTaskCompleted;
import com.example.crmapp.models.Client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class GeocoderTask extends AsyncTask<String, Void, Client> {

    Context context;
    private OnGeocoderTaskCompleted listener;

    GeocoderTask(OnGeocoderTaskCompleted listener, Context context){
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Client doInBackground(String... strings) {

        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(strings[0]+ " "+strings[5]+" "+strings[1]+" "+strings[2],1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addressList.size()>0) {
            Address geolocation = addressList.get(0);

            Float[] coordinates = new Float[2];

            coordinates[0] = (float) geolocation.getLatitude();
            coordinates[1] = (float) geolocation.getLongitude();

            Client client = new Client(strings[3], strings[4], Integer.valueOf(strings[5]), strings[0], strings[1], strings[2], coordinates[0], coordinates[1], null, 1);

            return client;
        }
        else {
            return null;
        }
    }

    protected void onPostExecute(Client client) {
        listener.onGeocoderTaskCompleted(client);
    }
}
