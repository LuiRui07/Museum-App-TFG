package com.example.museumapp.Activities;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museumapp.Adapters.MuseosAdapter;
import com.example.museumapp.Models.Museum;
import com.example.museumapp.R;
import com.example.museumapp.Service.MuseumService;

import java.util.List;
public class Museos extends AppCompatActivity {

    private RecyclerView recyclerView;
    public MuseosAdapter museosAdapter;

    public MuseumService museumService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.museos);

        museumService = new MuseumService(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getMuseos();
    }

    public void getMuseos() {

        museumService.getAllMuseums(new MuseumService.MuseumCallback() {
            @Override
            public void onSuccess(Museum museum, int tipo, List<Museum> museos, Context context) {
                museosAdapter = new MuseosAdapter(museos,context);
                recyclerView.setAdapter(museosAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });



        }

    }
