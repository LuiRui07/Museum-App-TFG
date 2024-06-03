package com.example.museumapp.Activities;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museumapp.Adapters.RutasAdapter;
import com.example.museumapp.Models.Route;
import com.example.museumapp.R;
import com.example.museumapp.Service.RouteService;
import com.example.museumapp.SharedData;

import java.util.List;

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
        String user = sharedData.getUser().getUser();

        getRecorridos(sharedData.user.getId());
    }

    public void getRecorridos(String userId) {

        Context context = this;

        routeService.getRoutesFromUser(userId, new RouteService.RouteCallback() {
            @Override
            public void onSuccess(List<Route> rutas) {
                rutasAdapter = new RutasAdapter(rutas,context);
                recyclerView.setAdapter(rutasAdapter);
            }
        });
    }
}
