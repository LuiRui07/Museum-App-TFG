package com.example.museumapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.museumapp.Api.RouteBody;
import com.example.museumapp.Models.Museum;
import com.example.museumapp.Models.Obra;
import com.example.museumapp.Models.Route;
import com.example.museumapp.R;
import com.example.museumapp.Service.MuseumService;
import com.example.museumapp.Service.ObraService;
import com.example.museumapp.Service.RouteService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateRoute extends AppCompatActivity {
    private RouteBody routeBody;
    private Spinner spinnerMuseum;
    private Spinner spinnerArtworks;
    private Button btnCreateRoute;
    private MuseumService museumService;
    private ObraService obraService;
    private RouteService routeService;
    private Map<Museum, List<Obra>> artworksByMuseum = new HashMap<>();
    private List<String> museumNames = new ArrayList<>();
    public String selectedMuseum;

    public LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_route);

        spinnerMuseum = findViewById(R.id.spinner_museum);
        spinnerArtworks = findViewById(R.id.spinner_artworks);
        btnCreateRoute = findViewById(R.id.btn_create_route);
        linearLayout = findViewById(R.id.spinner_container);

        museumService = new MuseumService(this);
        obraService = new ObraService(this);
        routeBody = new RouteBody(null,null,null,null);

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

                Log.e("AEEEAEA", "es" + position);
                Log.e("SEEEEEET",artworksByMuseum.keySet().toArray().toString());
                if (getMuseumIdFromPosition(artworksByMuseum.keySet()) != null){
                    routeBody.setMuseum(getMuseumIdFromPosition(artworksByMuseum.keySet()));
                }
                selectedMuseum = (String) spinnerMuseum.getSelectedItem();
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
    }

    public void addSpinner(View view){
        // Crear un nuevo Spinner
        Spinner newSpinner = new Spinner(this);

        List<String> obras = getObrasFromMusem(selectedMuseum);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, obras);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newSpinner.setAdapter(adapter);

        // Configurar las propiedades del layout del Spinner
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 16; // Ajusta el margen superior según tu necesidad
        newSpinner.setLayoutParams(layoutParams);

        // Agregar el Spinner al LinearLayout
        linearLayout.addView(newSpinner);
    }

    public String getMuseumIdFromPosition(Set<Museum> museums){
        String res = null;
        List<Museum> museumList = new ArrayList<>(museums);
        for(Museum m : museumList){
            if (m.getName() == selectedMuseum){
                res = m.getId();
            }
        }
        return res;
    }

    public List<String> getObrasFromMusem(String name) {
        List<Obra> obras = artworksByMuseum.get(name);
        List<String> res = new ArrayList<>();
        if (obras != null) {
            for (Obra ob : obras) {
                res.add(ob.getName());
            }
        }
        return res;
    }

    public void createRoute(View view){
        Context context = this;
        routeService.createRoute(routeBody, new RouteService.RouteCallback() {
            @Override
            public void onSuccess(List<Route> rutas) {
                Intent intent = new Intent(context,Recorridos.class);
                startActivity(intent);
                Toast.makeText(CreateRoute.this, "Recorrido creado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
