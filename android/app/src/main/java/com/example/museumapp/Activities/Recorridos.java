package com.example.museumapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    public TextView emptyRoutes;
    public String idMuseo;

    public int tipo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorridos);

        routeService = new RouteService(this);

        emptyRoutes = findViewById(R.id.empty_routes);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedData sharedData = SharedData.getInstance();
        Log.e("shared", sharedData.toString());

        Intent intent = getIntent();
        tipo= intent.getIntExtra("tipo",0);
        idMuseo = intent.getStringExtra("idMuseum");
        if( sharedData.getUser() != null){
            String user = sharedData.getUser().getUser();
            getRecorridos(sharedData.user.getId(),tipo);
        }
    }

    public void getRecorridos(String userId, int tipo) {

        Context context = this;

        if (tipo != 1) {
            routeService.getRoutesFromUser(userId, new RouteService.RouteCallback() {
                @Override
                public void onSuccess(List<Route> rutas) {
                    if (rutas.isEmpty()) {
                        emptyRoutes.setVisibility(View.VISIBLE);
                        emptyRoutes.setText("Aún no tienes ningún recorrido.");
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyRoutes.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        rutasAdapter = new RutasAdapter(rutas, context, tipo);
                        recyclerView.setAdapter(rutasAdapter);
                    }
                }
            });
        } else {
            routeService.getRouteFromUserAndMuseum(userId,idMuseo, new RouteService.RouteCallback() {
                @Override
                public void onSuccess(List<Route> rutas) {
                    if (rutas.isEmpty()) {
                        emptyRoutes.setVisibility(View.VISIBLE);
                        emptyRoutes.setText("Aún no tienes ningún recorrido en este museo.");
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyRoutes.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        rutasAdapter = new RutasAdapter(rutas, context, tipo);
                        recyclerView.setAdapter(rutasAdapter);
                    }
                }
            });
        }
    }

    public void createRoute(View view){
        Intent intent = new Intent(this,CreateRoute.class);
        startActivity(intent);
    }
}
