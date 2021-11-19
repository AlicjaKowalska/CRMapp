package com.example.crmapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView listbutton = findViewById(R.id.listbutton);
        listbutton.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, ClientList.class);
            startActivity(intent);
        });

        TextView clientBase = findViewById(R.id.ClientBase);
        clientBase.setOnClickListener( v -> {
            Intent intent = new Intent(Home.this, ClientList.class);
            startActivity(intent);
        });

        FloatingActionButton addbutton = findViewById(R.id.addbutton);
        addbutton.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, AddClient.class);
            startActivity(intent);
        });

    }
}
