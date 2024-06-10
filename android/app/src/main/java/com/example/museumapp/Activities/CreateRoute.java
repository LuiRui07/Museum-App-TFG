package com.example.museumapp.Activities;

import android.content.Context;
import android.os.Bundle;
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

public class CreateRoute extends AppCompatActivity {

    private Spinner spinnerMuseum;
    private Spinner spinnerArtworks;
    private Button btnCreateRoute;
    private MuseumService museumService;
    private ObraService obraService;
    private Map<String, List<Obra>> artworksByMuseum = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_route);

        spinnerMuseum = findViewById(R.id.spinner_museum);
        spinnerArtworks = findViewById(R.id.spinner_artworks);
        btnCreateRoute = findViewById(R.id.btn_create_route);

        museumService = new MuseumService(this);
        obraService = new ObraService(this);

        // Configurar el adaptador para el Spinner de museos
        ArrayAdapter<String> museumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        museumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMuseum.setAdapter(museumAdapter);

        // Configurar listener para el Spinner de museos
        spinnerMuseum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMuseum = spinnerMuseum.getSelectedItem().toString();
                updateArtworksSpinner(selectedMuseum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerArtworks.setVisibility(View.GONE);
            }
        });

        // Configurar listener para el botón de crear ruta
        btnCreateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedMuseum = spinnerMuseum.getSelectedItem().toString();
                String selectedArtwork = spinnerArtworks.getSelectedItem().toString();
                Toast.makeText(CreateRoute.this, "Crear ruta para " + selectedArtwork + " en " + selectedMuseum, Toast.LENGTH_SHORT).show();
            }
        });

        // Inicializar los datos de los museos y obras
        initializeData();
    }

    private void initializeData() {
        museumService.getAllMuseums(new MuseumService.MuseumCallback() {
            @Override
            public void onSuccess(Museum museum, int tipo, List<Museum> museums, Context context) {
                // Limpiar el mapa y lista antes de añadir datos nuevos
                artworksByMuseum.clear();

                // Preparar nombres de museos para el adaptador
                List<String> museumNames = new ArrayList<>();
                for (Museum m : museums) {
                    museumNames.add(m.getName());
                }
                ArrayAdapter<String> museumAdapter = new ArrayAdapter<>(CreateRoute.this, android.R.layout.simple_spinner_item, museumNames);
                museumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Actualizar el Spinner de museos en el hilo principal de la UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinnerMuseum.setAdapter(museumAdapter);
                    }
                });

                // Obtener las obras para cada museo
                for (Museum m : museums) {
                    obraService.getArtFromMuseum(m.getId(), new ObraService.ObraCallback() {
                        @Override
                        public void onSuccess(List<Obra> obras, Obra obra) {
                            artworksByMuseum.put(m.getName(), obras);

                            // Mostrar las obras del primer museo en el inicio
                            if (m == museums.get(0)) {
                                updateArtworksSpinner(m.getName());
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CreateRoute.this, "Error al obtener museos: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateArtworksSpinner(String selectedMuseum) {
        if (artworksByMuseum.containsKey(selectedMuseum)) {
            List<Obra> artworks = artworksByMuseum.get(selectedMuseum);
            List<String> artworksString = new ArrayList<>();
            for (Obra obra : artworks) {
                artworksString.add(obra.getName());
            }
            ArrayAdapter<String> artworkAdapter = new ArrayAdapter<>(CreateRoute.this, android.R.layout.simple_spinner_item, artworksString);
            artworkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerArtworks.setAdapter(artworkAdapter);
            spinnerArtworks.setVisibility(View.VISIBLE);
        } else {
            spinnerArtworks.setVisibility(View.GONE);
        }
    }
}
