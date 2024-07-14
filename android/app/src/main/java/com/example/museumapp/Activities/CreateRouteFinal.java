package com.example.museumapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.museumapp.Api.RouteBody;
import com.example.museumapp.Models.Route;
import com.example.museumapp.R;
import com.example.museumapp.Service.MuseumService;
import com.example.museumapp.Service.ObraService;
import com.example.museumapp.Service.RouteService;
import com.example.museumapp.SharedData;

import java.util.List;

public class CreateRouteFinal extends AppCompatActivity {

    public SharedData data = SharedData.getInstance();
    private TextView routeName;
    private RouteBody routeBody;
    private RouteService routeService;

    private String museum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_route_final);
        routeBody = data.getRouteBody();
        routeName = findViewById(R.id.routeNameEditText);
        routeService = new RouteService(this);

        Intent intent = getIntent();
        museum = intent.getStringExtra("museumID");
        Log.e("Museo Recibido", museum + "0");
    }

    public void onClick(View view){
        String name = routeName.getText().toString();
        Context context = this;
        if (name == ""){
            Toast.makeText(this,"Escribe un nombre válido", Toast.LENGTH_SHORT).show();
        } else {
            routeBody.setName(name);
            Log.e("ROUTEBODY", routeBody.toString());
            routeService.createRoute(routeBody, new RouteService.RouteCallback() {
                @Override
                public void onSuccess(List<Route> rutas) {
                    Toast.makeText(context, "Recorrido creado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateRouteFinal.this,Recorridos.class);
                    intent.putExtra("tipo",1);
                    intent.putExtra("idMuseum",museum);
                    startActivity(intent);
                }
            });
        }
    }
}
