package com.example.crmapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.example.crmapp.interfaces.OnWeatherTaskCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class getWeatherTask extends AsyncTask<String, Void, Pair<String[], Bitmap>> {

    private OnWeatherTaskCompleted listener;

    public getWeatherTask(OnWeatherTaskCompleted listener){
        this.listener=listener;
    }

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

    @Override
    protected Pair<String[], Bitmap> doInBackground(String... strings) {
        String contents = getURLString(strings[0]);

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
            //Bitmap iconBitmap = downloadImage.execute(urlIcon).get();
            //weatherIcon.setImageBitmap(iconBitmap);

            String[] weatherStrings = new String[2];
            weatherStrings[0] = degrees;
            weatherStrings[1] = description;
            Pair<String[], Bitmap> weatherResult = new Pair<>(weatherStrings, bitmap);

            return weatherResult;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Pair<String[], Bitmap> weather) {
        String degrees = weather.first[0];
        String description = weather.first[1];
        Bitmap iconBitmap = weather.second;
        listener.onWeatherTaskCompleted(degrees, description, iconBitmap);
    }
}
