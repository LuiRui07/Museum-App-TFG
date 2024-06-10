package com.example.museumapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.museumapp.Models.Museum;
import com.example.museumapp.Models.Obra;
import com.example.museumapp.R;
import com.example.museumapp.Service.MuseumService;
import com.example.museumapp.Service.ObraService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateRoute extends AppCompatActivity {

    private Spinner spinnerMuseum;
    private Spinner spinnerArtworks;
    private Button btnCreateRoute;
    private MuseumService museumService;
    private ObraService obraService;
    private Map<Museum, List<Obra>> artworksByMuseum = new HashMap<>();
    private List<String> museumNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_route);

        spinnerMuseum = findViewById(R.id.spinner_museum);
        spinnerArtworks = findViewById(R.id.spinner_artworks);
        btnCreateRoute = findViewById(R.id.btn_create_route);

        museumService = new MuseumService(this);
        obraService = new ObraService(this);

        // Inicializar los datos de los museos y obras
        initializeData();
    }

    private void initializeData() {
        museumService.getAllMuseums(new MuseumService.MuseumCallback() {
            @Override
            public void onSuccess(Museum museum, int tipo, List<Museum> museos, Context context) {
                for (int i = 0; i < museos.size(); i++) {
                    final Museum m = museos.get(i);
                    museumNames.add(m.getName()); // Agregar el nombre del museo a la lista de nombres

                    obraService.getArtFromMuseum(m.getId(), new ObraService.ObraCallback() {
                        @Override
                        public void onSuccess(List<Obra> obras, Obra obra) {
                            artworksByMuseum.put(m, obras);

                            // Verificar si se han obtenido todas las obras para todos los museos
                            if (artworksByMuseum.size() == museos.size()) {
                                // Todos los datos han sido cargados correctamente, actualizar la UI
                                updateUI();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Manejar el fallo si ocurre
            }
        });
    }

    private void updateUI() {
        ArrayAdapter<String> museumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, museumNames);
        museumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMuseum.setAdapter(museumAdapter);

        // Configurar listener para el Spinner de museos
        spinnerMuseum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMuseum = (String) spinnerMuseum.getSelectedItem();
                List<String> artworks = new ArrayList<>();
                for (Museum museum : artworksByMuseum.keySet()) {
                    if (museum.getName().equals(selectedMuseum)) {
                        List<Obra> obras= artworksByMuseum.get(museum);
                        for (Obra o : obras){
                            artworks.add(o.getName());
                        }
                    }
                }
                if (artworks != null && !artworks.isEmpty()) {
                    ArrayAdapter<String> artworkAdapter = new ArrayAdapter<>(CreateRoute.this, android.R.layout.simple_spinner_item, artworks);
                    artworkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerArtworks.setAdapter(artworkAdapter);
                    spinnerArtworks.setVisibility(View.VISIBLE);
                } else {
                   // spinnerArtworks.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Botón de crear ruta
        btnCreateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Museum selectedMuseum = (Museum) spinnerMuseum.getSelectedItem();
                Obra selectedArtwork = (Obra) spinnerArtworks.getSelectedItem();
                Toast.makeText(CreateRoute.this, "Crear ruta para " + selectedArtwork.getName() + " en " + selectedMuseum.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
