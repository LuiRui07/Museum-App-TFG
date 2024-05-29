package com.example.museumapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museumapp.Adapters.ObrasAdapter;
import com.example.museumapp.Adapters.RutasAdapter;
import com.example.museumapp.Api.ApiClient;
import com.example.museumapp.Api.ApiService;
import com.example.museumapp.Models.Obra;
import com.example.museumapp.Models.Route;
import com.example.museumapp.R;
import com.example.museumapp.SharedData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Recorridos extends AppCompatActivity {

    private RecyclerView recyclerView;

    public RutasAdapter rutasAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedData sharedData = SharedData.getInstance();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getRecorridos(sharedData.user);
    }

    public void getRecorridos(String user) {
        Retrofit retrofit = ApiClient.addHeader(this);
        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Route>> call = apiService.getAllRoutes();

            call.enqueue(new Callback<List<Route>>() {
                @Override
                public void onResponse(Call<List<Route>> call, retrofit2.Response<List<Route>> response) {
                    if (response.isSuccessful()) {
                        List<Route> rutas = response.body();
                        Log.d("GetRutas", rutas.toString());
                        rutasAdapter = new RutasAdapter(rutas);
                        recyclerView.setAdapter(rutasAdapter);
                    } else {
                        Log.d("GetRutas", response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Route>> call, Throwable t) {
                    Log.e("GetRutasFail", "Error en la solicitud: " + t.getMessage());
                }
            });

    }
}
