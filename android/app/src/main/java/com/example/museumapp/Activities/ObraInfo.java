package com.example.museumapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.museumapp.Models.Obra;
import com.example.museumapp.R;
import com.example.museumapp.Service.ObraService;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ObraInfo extends AppCompatActivity {

    ObraService obraService;
    Obra obraActual;
    String museumName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obra_info);
        obraService = new ObraService(this);

        Intent intent = getIntent();
        String obraId = intent.getStringExtra("obra_id");
        museumName = intent.getStringExtra("museum_name");

        getObra(obraId);
    }

    public void getObra(String id) {
        obraService.getArtFromId(id, new ObraService.ObraCallback() {
            @Override
            public void onSuccess(List<Obra> obras, Obra obra) {
                obraActual = obra;
                runOnUiThread(() -> setObraInfo(obraActual));
            }
        });
    }

    private void setObraInfo(Obra obra) {
        TextView titleTextView = findViewById(R.id.detail_title_text_view);
        TextView descriptionTextView = findViewById(R.id.detail_description_text_view);
        ImageView imageView = findViewById(R.id.detail_image_view);
        TextView museumTextView = findViewById(R.id.detail_museum_name_text_view);
        TextView yearTextView = findViewById(R.id.detail_year_text_view);

        titleTextView.setText(obra.getName());
        descriptionTextView.setText(obra.getDescription());
        museumTextView.setText(museumName);
        if (obra.getCentury() != null) {
            yearTextView.setText("Siglo: " + obra.getCentury());
        } else {
            yearTextView.setText("Año: " + obra.getDate());
        }


        Picasso.get()
                .load(obra.getImage())
                .fit() // Ajusta la imagen al tamaño del ImageView
                .centerInside() // Asegura que toda la imagen se vea dentro del ImageView
                .error(R.drawable.default_art_icon)
                .into(imageView);
    }
}
