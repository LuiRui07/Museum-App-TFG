package com.example.museumapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museumapp.Adapters.MuseosAdapter;
import com.example.museumapp.Adapters.ObrasAdapter;
import com.example.museumapp.Api.ApiClient;
import com.example.museumapp.Api.ApiService;
import com.example.museumapp.Models.Museum;
import com.example.museumapp.Models.Obra;
import com.example.museumapp.R;
import com.example.museumapp.SharedData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Museos extends AppCompatActivity {

    private RecyclerView recyclerView;

    public MuseosAdapter museosAdapter;
    public SharedData sharedData = SharedData.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.museos);

        SharedData data = SharedData.getInstance();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getMuseos();
    }

    public void getMuseos() {
        Retrofit retrofit = ApiClient.addHeader(this);
        ApiService apiService = retrofit.create(ApiService.class);
        Context context = this;

        Call<List<Museum>> call = apiService.getAllMuseum();

            call.enqueue(new Callback<List<Museum>>() {
                @Override
                public void onResponse(Call<List<Museum>> call, retrofit2.Response<List<Museum>> response) {
                    if (response.isSuccessful()) {
                        List<Museum> museos = response.body();
                        Log.d("GetObras", museos.toString());
                        museosAdapter = new MuseosAdapter(museos,context);
                        recyclerView.setAdapter(museosAdapter);
                    } else {
                        Log.d("GetObras", response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Museum>> call, Throwable t) {
                    Log.e("GetObrasFail", "Error en la solicitud: " + t.getMessage());
                }
            });
        }

    }
