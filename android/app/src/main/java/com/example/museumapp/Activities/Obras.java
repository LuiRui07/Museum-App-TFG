package com.example.museumapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museumapp.Api.ApiClient;
import com.example.museumapp.Api.ApiService;
import com.example.museumapp.Models.Obra;
import com.example.museumapp.Adapters.ObrasAdapter;
import com.example.museumapp.R;
import com.example.museumapp.SharedData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Obras extends AppCompatActivity {

    private RecyclerView recyclerView;

    public ObrasAdapter obrasAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String museumId = intent.getStringExtra("museum_id");
        String museumName = intent.getStringExtra("museum_name");
        if (intent.hasExtra("museum_id")) {
            Log.d("ObrasActivity", "Received museum_id: " + museumId + " " + museumName);

            setContentView(R.layout.obras_museo);
            TextView museumNameView = findViewById(R.id.museum_title);
            museumNameView.setText(museumName);
        } else {
            setContentView(R.layout.obras);
            Log.e("ObrasActivity", "No museum_id provided in Intent");
        }
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getObras(museumId);
    }

    public void getObras(String id) {
        Retrofit retrofit = ApiClient.addHeader(this);
        ApiService apiService = retrofit.create(ApiService.class);

        if (id == null) {
            Call<List<Obra>> call = apiService.getAllArt();

            call.enqueue(new Callback<List<Obra>>() {
                @Override
                public void onResponse(Call<List<Obra>> call, retrofit2.Response<List<Obra>> response) {
                    if (response.isSuccessful()) {
                        List<Obra> obras = response.body();
                        Log.d("GetObras", obras.toString());
                        obrasAdapter = new ObrasAdapter(obras);
                        recyclerView.setAdapter(obrasAdapter);
                    } else {
                        Log.d("GetObras", response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Obra>> call, Throwable t) {
                    Log.e("GetObrasFail", "Error en la solicitud: " + t.getMessage());
                }
            });
        } else {
            Call<List<Obra>> call = apiService.getObrasFromMuseum(id);

            call.enqueue(new Callback<List<Obra>>() {
                @Override
                public void onResponse(Call<List<Obra>> call, retrofit2.Response<List<Obra>> response) {
                    if (response.isSuccessful()) {
                        List<Obra> obras = response.body();
                        Log.d("GetObrasDeMuseoA", obras.toString());
                        obrasAdapter = new ObrasAdapter(obras);
                        recyclerView.setAdapter(obrasAdapter);
                    } else {
                        Log.d("GetObrasDeMuseoB", response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<List<Obra>> call, Throwable t) {
                    Log.e("GetObrasdeMuseoFail", "Error en la solicitud: " + t.getMessage());
                }
            });

        }

    }
}
