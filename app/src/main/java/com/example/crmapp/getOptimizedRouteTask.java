package com.example.crmapp;

import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.crmapp.interfaces.OnOptimizedRouteTaskCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class getOptimizedRouteTask  extends AsyncTask<String, Void, String[]> {

    private OnOptimizedRouteTaskCompleted listener;

    public getOptimizedRouteTask(OnOptimizedRouteTaskCompleted listener){
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
    protected String[] doInBackground(String... strings) {
        String[] str = new String[2];

        //optimized route
        str[0] = getURLString(strings[0]);

        try {
            //optimized route
                JSONObject jsonObjectOR = new JSONObject(str[0]);
                JSONObject routeOR = jsonObjectOR.getJSONObject("route");
                String sorID = routeOR.getString("sessionId");

                String url2OR = "https://open.mapquestapi.com/directions/v2/routeshape?key=VBIRcPsPXNsywLQOzknMJwaus13ueGVm&sessionId="+sorID+"&fullShape=true";
                str[1] = getURLString(url2OR);

            return str;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(String[] str){
        listener.onOptimizedRouteTaskCompleted(str);
    }
}
