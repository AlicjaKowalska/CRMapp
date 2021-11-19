package com.example.crmapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crmapp.adapters.ClientsRVAdapter;
import com.example.crmapp.databases.DbHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;


public class ClientList extends AppCompatActivity{

    private RecyclerView ClientsRV;
    private ClientsRVAdapter clientsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientlist);

        Toolbar toolbar = findViewById(R.id.toolbarList);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ClientList.this, Home.class);
            startActivity(intent);
        });


        ClientsRV = findViewById(R.id.ClientList);
        DbHandler DB = new DbHandler(this);

        clientsRVAdapter=new ClientsRVAdapter(DB.getAllClientsData(),this);
        ClientsRV.setHasFixedSize(true);
        ClientsRV.setLayoutManager(new LinearLayoutManager(this));
        ClientsRV.setAdapter(clientsRVAdapter);


        ImageView planARoute = findViewById(R.id.planARouteButton);
        planARoute.setOnClickListener( v -> {
            Intent intent = new Intent(ClientList.this, PlanARoute.class);
            startActivity(intent);
        });

        SearchView searchview = findViewById(R.id.SearchClient);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ClientList.this.ClientsRV.getFilterTouchesWhenObscured();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                clientsRVAdapter.getFilter().filter(newText);
                return false;
            }
        });

        ImageView button = findViewById(R.id.ListAddButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(ClientList.this, AddClient.class);
            startActivity(intent);
        });

        ImageView mapAllClients = findViewById(R.id.mapAllClients);
        mapAllClients.setOnClickListener( v -> {
            Intent intent = new Intent(ClientList.this, ClientMap.class);
            startActivity(intent);
        });

    }
}
