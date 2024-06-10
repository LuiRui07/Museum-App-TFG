package com.example.museumapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.museumapp.Models.Museum;
import com.example.museumapp.Models.Obra;
import com.example.museumapp.R;
import com.example.museumapp.Service.MuseumService;
import com.example.museumapp.Service.ObraService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateRoute extends AppCompatActivity {

    private Spinner spinnerMuseum;
    private LinearLayout layoutArtworks;
    private Button btnAddArtwork;
    private Button btnCreateRoute;

    private MuseumService museumService;
    private ObraService obraService;
    private String[] museumNames = {};
    private Map<Museum, List<Obra>> artworksByMuseum = new HashMap<>();
    private int artworkSpinnerCount = 1; // Contador para los spinners de obras
    private Set<String> selectedArtworks = new HashSet<>(); // Para mantener obras seleccionadas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_route);

        spinnerMuseum = findViewById(R.id.spinner_museum);
        layoutArtworks = findViewById(R.id.layout_artworks);
        btnAddArtwork = findViewById(R.id.btn_add_artwork);
        btnCreateRoute = findViewById(R.id.btn_create_route);
        museumService = new MuseumService(this);
        obraService = new ObraService(this);

        // Inicializar la lista de museos al iniciar la actividad
        initializeMuseums();

        // Listener para el spinner de museos
        spinnerMuseum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Museum selectedMuseum = null;
                for (Museum museum : artworksByMuseum.keySet()) {
                    if (museum.getName().equals(museumNames[position])) {
                        selectedMuseum = museum;
                        break;
                    }
                }
                if (selectedMuseum != null) {
                    List<Obra> obras = artworksByMuseum.get(selectedMuseum);
                    updateArtworkSpinner(obras, (Spinner) layoutArtworks.getChildAt(1));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Botón para agregar más spinners de obras de arte
        btnAddArtwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (artworkSpinnerCount < 3) { // Limitar a máximo 3 obras
                    addNewArtworkSpinner();
                } else {
                    Toast.makeText(CreateRoute.this, "No puedes añadir más de 3 obras", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Manejar clic del botón para crear la ruta
        btnCreateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedMuseum = spinnerMuseum.getSelectedItem().toString();
                List<String> selectedArtworks = new ArrayList<>();
                for (int i = 1; i < layoutArtworks.getChildCount(); i++) {
                    View view = layoutArtworks.getChildAt(i);
                    if (view instanceof Spinner) {
                        selectedArtworks.add(((Spinner) view).getSelectedItem().toString());
                    }
                }

                // Aquí puedes implementar la lógica para crear la ruta con los datos seleccionados
                Toast.makeText(CreateRoute.this, "Crear ruta para " + selectedArtworks + " en " + selectedMuseum, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeMuseums() {
        museumService.getAllMuseums(new MuseumService.MuseumCallback() {
            @Override
            public void onSuccess(Museum museum, int tipo, List<Museum> museos, Context context) {
                museumNames = new String[museos.size()];
                for (int i = 0; i < museos.size(); i++) {
                    Museum museoActual = museos.get(i);
                    museumNames[i] = museoActual.getName();
                    obraService.getArtFromMuseum(museoActual.getId(), new ObraService.ObraCallback() {
                        @Override
                        public void onSuccess(List<Obra> obras, Obra obra) {
                            artworksByMuseum.put(museoActual, obras);

                            // Llenar el adaptador del spinner de museos después de obtener los datos
                            ArrayAdapter<String> museumAdapter = new ArrayAdapter<>(CreateRoute.this, android.R.layout.simple_spinner_item, museumNames);
                            museumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerMuseum.setAdapter(museumAdapter);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CreateRoute.this, "Error al cargar los museos: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateArtworkSpinner(List<Obra> obras, Spinner spinner) {
        List<String> obraNames = new ArrayList<>();
        for (Obra obra : obras) {
            if (!selectedArtworks.contains(obra.getName())) { // Filtrar obras seleccionadas
                obraNames.add(obra.getName());
            }
        }

        // Convertir la lista de nombres de obras a un array
        String[] artworks = obraNames.toArray(new String[0]);

        // Adaptador para el spinner de obras de arte
        ArrayAdapter<String> artworkAdapter = new ArrayAdapter<>(CreateRoute.this, android.R.layout.simple_spinner_item, artworks);
        artworkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(artworkAdapter);

        // Listener para actualizar la lista de obras seleccionadas
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedArtwork = artworks[position];
                selectedArtworks.add(selectedArtwork);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void addNewArtworkSpinner() {
        // Añadir nuevo TextView y Spinner
        TextView textView = new TextView(this);
        textView.setText("Selecciona una obra");
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setPadding(0, 16, 0, 8);
        layoutArtworks.addView(textView);

        Spinner newSpinner = new Spinner(this);
        newSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        newSpinner.setId(View.generateViewId());
        layoutArtworks.addView(newSpinner);

        // Copiar el adaptador del spinner de la primera obra de arte
        Spinner firstSpinner = findViewById(R.id.spinner_artwork_1);
        newSpinner.setAdapter(firstSpinner.getAdapter());

        // Listener para actualizar la lista de obras seleccionadas
        newSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedArtwork = firstSpinner.getAdapter().getItem(position).toString();
                selectedArtworks.add(selectedArtwork);
                // Actualizar todos los spinners de obras para excluir la obra seleccionada
                for (int i = 1; i < layoutArtworks.getChildCount(); i++) {
                    View child = layoutArtworks.getChildAt(i);
                    if (child instanceof Spinner) {
                        Spinner spinner = (Spinner) child;
                        updateArtworkSpinner(artworksByMuseum.get(getSelectedMuseum()), spinner);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        artworkSpinnerCount++;
    }

    private Museum getSelectedMuseum() {
        String selectedMuseumName = spinnerMuseum.getSelectedItem().toString();
        for (Museum museum : artworksByMuseum.keySet()) {
            if (museum.getName().equals(selectedMuseumName)) {
                return museum;
            }
        }
        return null;
    }
}
