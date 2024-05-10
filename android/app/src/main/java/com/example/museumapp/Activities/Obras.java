package com.example.museumapp.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        setContentView(R.layout.obras);

        SharedData data = SharedData.getInstance();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getObras();
    }

    public void getObras() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tfg-tkck.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

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
    }
}
