package com.example.museumapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museumapp.Adapters.RutasAdapter;
import com.example.museumapp.Api.ApiClient;
import com.example.museumapp.Api.ApiService;
import com.example.museumapp.Models.Route;
import com.example.museumapp.R;
import com.example.museumapp.Service.RouteService;
import com.example.museumapp.SharedData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Recorridos extends AppCompatActivity {

    private RecyclerView recyclerView;

    public RutasAdapter rutasAdapter;

    public RouteService routeService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorridos);

        routeService = new RouteService(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedData sharedData = SharedData.getInstance();
        String user = sharedData.user;

        getRecorridos(sharedData.user);
    }

    public void getRecorridos(String user) {

        Context context = this;

        routeService.getAllRoutes(new RouteService.RouteCallback() {
            @Override
            public void onSuccess(List<Route> rutas) {
                rutasAdapter = new RutasAdapter(rutas,context);
                recyclerView.setAdapter(rutasAdapter);
            }
        });
    }
}
