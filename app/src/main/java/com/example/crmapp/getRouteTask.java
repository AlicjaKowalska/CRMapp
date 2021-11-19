package com.example.crmapp;

import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.crmapp.interfaces.OnRouteTaskCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class getRouteTask extends AsyncTask<String, Void, String[]>{

    private OnRouteTaskCompleted listener;

    public getRouteTask(OnRouteTaskCompleted listener){
            this.listener=listener;
        }

    private String getURLString(String url){
            String contents ="";

            try {
                URLConnection conn = new URL(url).openConnection();

                long start = System.currentTimeMillis();
                InputStream in = conn.getInputStream();
                long elapsedTimeMillis = System.currentTimeMillis()-start;
                Log.e("time", "time = " + elapsedTimeMillis);

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

            //normal route
            str[0] = getURLString(strings[0]);

            try {
                //normal route
                JSONObject jsonObject = new JSONObject(str[0]);
                JSONObject rou = jsonObject.getJSONObject("route");
                String sID = rou.getString("sessionId");

                String url2 = "https://open.mapquestapi.com/directions/v2/routeshape?key=VBIRcPsPXNsywLQOzknMJwaus13ueGVm&sessionId="+sID+"&fullShape=true";
                str[1] = getURLString(url2);

                return str;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String[] str){
            listener.onTaskCompleted(str);
        }
}

