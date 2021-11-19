package com.example.crmapp;

import android.os.AsyncTask;
import android.util.Log;

import com.example.crmapp.interfaces.OnRouteFromDialogTaskCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class RouteFromDialogTask extends AsyncTask<String, Void, String> {

    private OnRouteFromDialogTaskCompleted listener;

    public RouteFromDialogTask(OnRouteFromDialogTaskCompleted listener){
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
    protected String doInBackground(String... strings) {
        String contents = getURLString(strings[0]);

        try {
            JSONObject jsonObject = new JSONObject(contents);
            JSONObject rou = jsonObject.getJSONObject("route");
            String sID = rou.getString("sessionId");

            String url2= "https://open.mapquestapi.com/directions/v2/routeshape?key=VBIRcPsPXNsywLQOzknMJwaus13ueGVm&sessionId="+sID+"&fullShape=true";
            String contents2 = getURLString(url2);

            return contents2;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onRouteFromDialogTaskCompleted(s);
    }
}
