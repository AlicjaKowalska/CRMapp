package com.example.crmapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.crmapp.databases.DbHandler;
import com.example.crmapp.interfaces.OnRouteFromDialogTaskCompleted;
import com.example.crmapp.models.Client;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BottomInfoDialog extends BottomSheetDialogFragment implements OnRouteFromDialogTaskCompleted {

    private BottomInfoListener mListener;
    DbHandler DB;

    TextView Name, LastName, Street, PostalCode, City, Activity;
    LinearLayout navigationButton;
    ImageView Image;

    @Override public int getTheme() {
        return R.style.MyTransparentBottomSheetDialogTheme;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_info_layout, container, false);

        Name = view.findViewById(R.id.bottomInfoName);
        LastName = view.findViewById(R.id.bottomInfoLastName);
        Street = view.findViewById(R.id.bottomInfoStreet);
        PostalCode = view.findViewById(R.id.bottomInfoPostalCode);
        City = view.findViewById(R.id.bottomInfoCity);
        Image = view.findViewById(R.id.imageView);
        Activity = view.findViewById(R.id.activity);

        Bundle mArgs = getArguments();

        int id = Integer.parseInt(mArgs.getString("id"));
        DB = new DbHandler(view.getContext());
        Client client = DB.getClient(id);

        if( mArgs != null) {
            Name.setText(client.getName());
            LastName.setText(client.getLastName());
            Street.setText(client.getStreet());
            PostalCode.setText(client.getPostalCode());
            City.setText(client.getCity());
            Image.setImageBitmap(client.getImage());

            if(client.getActivity()==1) {
                Activity.setTextColor(getContext().getColor(R.color.secondary));
                Activity.setBackground(getContext().getDrawable(R.drawable.activity_shape));
                Activity.setText("AKTYWNY");
            }
            else {
                Activity.setTextColor(getContext().getColor(R.color.lightg));
                Activity.setBackground(getContext().getDrawable(R.drawable.activity_shape_grey));
                Activity.setText("NIEAKTYWNY");
            }

            float destlat = client.getGeoLat();
            float destlon = client.getGeoLon();

            navigationButton = view.findViewById(R.id.navigationButton);
            navigationButton.setOnClickListener( v -> {
                String url = "https://open.mapquestapi.com/directions/v2/route?key=VBIRcPsPXNsywLQOzknMJwaus13ueGVm&from=53.47057908236347,15.333281598852398&to="+destlat+","+destlon+"&outFormat=json";
                RouteFromDialogTask routeFromDialogTask = new RouteFromDialogTask(BottomInfoDialog.this);
                routeFromDialogTask.execute(url);
            });

            return view;
        }
        else{
            return null;
        }
    }

    @Override
    public void onRouteFromDialogTaskCompleted(String s) {
        try {
            JSONObject jsonObject1 = new JSONObject(s);
            JSONObject rt = jsonObject1.getJSONObject("route");
            JSONObject shape = rt.getJSONObject("shape");
            JSONArray shapePoints = shape.getJSONArray("shapePoints");

            PolylineOptions polyLine = new PolylineOptions();
            if (shapePoints.length() > 0) {
                for (int i = 0; i < shapePoints.length(); i = i + 2) {
                    polyLine.add(new LatLng(Double.parseDouble(shapePoints.getString(i)),
                            Double.parseDouble(shapePoints.getString(i + 1))));
                }
                mListener.onButtonClicked(polyLine);
            } else {
                Log.e("points", "puste shapePoints");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface BottomInfoListener{
        void onButtonClicked(PolylineOptions polyline);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomInfoListener) context;
        }
        catch(Exception e){
            Log.e("Bottom Sheet", e.getMessage());
        }
    }
}
