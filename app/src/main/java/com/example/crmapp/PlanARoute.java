package com.example.crmapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crmapp.adapters.PlanARouteAdapter;
import com.example.crmapp.databases.DbHandler;
import com.example.crmapp.models.Client;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;


public class PlanARoute extends AppCompatActivity{

    private RecyclerView ClientsRV;
    private PlanARouteAdapter clientsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planaroute);

        Toolbar toolbar = findViewById(R.id.toolbarPlanARoute);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ClientsRV = findViewById(R.id.ClientRoute);
        DbHandler DB = new DbHandler(this);

        clientsRVAdapter=new PlanARouteAdapter(DB.getAllClientsData(),this);
        ClientsRV.setHasFixedSize(true);
        ClientsRV.setLayoutManager(new LinearLayoutManager(this));
        ClientsRV.setAdapter(clientsRVAdapter);


        SearchView searchview = findViewById(R.id.SearchClientForRoute);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                PlanARoute.this.ClientsRV.getFilterTouchesWhenObscured();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                clientsRVAdapter.getFilter().filter(newText);
                return false;
            }
        });

        FloatingActionButton planARouteButton = findViewById(R.id.PlanARouteButton);
        planARouteButton.setOnClickListener( v -> {
                Bundle bundle = new Bundle();
                ArrayList<Client> Clients = clientsRVAdapter.getSelectedClients();
                ArrayList<Integer> selectedClients = new ArrayList<>();
                for (int i=0; i<Clients.size();i++){
                    selectedClients.add(Clients.get(i).get_ID());
                }
                bundle.putSerializable("clientList", (Serializable) selectedClients);
                Intent intent = new Intent(PlanARoute.this, RouteMap.class);
                intent.putExtras(bundle);
                startActivity(intent);
        });
    }


}
