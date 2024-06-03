package com.example.museumapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museumapp.Models.Obra;
import com.example.museumapp.Adapters.ObrasAdapter;
import com.example.museumapp.R;
import com.example.museumapp.Service.ObraService;

import java.util.List;

public class Obras extends AppCompatActivity {
    private RecyclerView recyclerView;
    public ObrasAdapter obrasAdapter;
    public ObraService obraService;
    public String museumId ;
    public String museumName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        obraService = new ObraService(this);

        Intent intent = getIntent();
        museumId = intent.getStringExtra("museum_id");
        museumName = intent.getStringExtra("museum_name");
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
        Context context = this;
        if (id == null) {
            obraService.getAllArt(new ObraService.ObraCallback() {
                @Override
                public void onSuccess(List<Obra> obras, Obra obra) {
                    obrasAdapter = new ObrasAdapter(obras,museumName, context);
                    recyclerView.setAdapter(obrasAdapter);
                }
            });

        } else {
            obraService.getArtFromMuseum(id, new ObraService.ObraCallback() {
                @Override
                public void onSuccess(List<Obra> obras, Obra obra) {
                    obrasAdapter = new ObrasAdapter(obras,museumName,context);
                    recyclerView.setAdapter(obrasAdapter);
                }
            });
        }

    }
}
