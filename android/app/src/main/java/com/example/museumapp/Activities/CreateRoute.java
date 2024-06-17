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
import com.example.museumapp.SharedData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateRoute extends AppCompatActivity {
    private RouteBody routeBody;
    private Spinner spinnerMuseum;
    private Spinner spinnerArtworks;
    private MuseumService museumService;
    private ObraService obraService;
    private Map<Museum, List<Obra>> artworksByMuseum = new HashMap<>();
    private List<String> museumNames = new ArrayList<>();
    public String selectedMuseum;
    public LinearLayout linearLayout;
    private List<Spinner> spinners = new ArrayList<>();
    private Map<Spinner, String> spinnerSelections = new HashMap<>();
    public SharedData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_route);

        spinnerMuseum = findViewById(R.id.spinner_museum);
        spinnerArtworks = findViewById(R.id.spinner_artworks);
        linearLayout = findViewById(R.id.spinner_container);

        museumService = new MuseumService(this);
        obraService = new ObraService(this);
        routeBody = new RouteBody(null,null,null,null);

        selectedMuseum = "";
        data = SharedData.getInstance();
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
                if (getMuseumIdFromPosition(artworksByMuseum.keySet()) != null){
                    routeBody.setMuseum(getMuseumIdFromPosition(artworksByMuseum.keySet()));
                }
                selectedMuseum = spinnerMuseum.getSelectedItem().toString();
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

                    spinnerArtworks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selected = (String) parent.getItemAtPosition(position);
                            spinnerSelections.put(spinnerArtworks, selected);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else {
                   // spinnerArtworks.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void addSpinner(View view) {
        // Crear un nuevo LinearLayout horizontal para contener el Spinner y el botón de borrar
        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 16;
        horizontalLayout.setLayoutParams(layoutParams);

        // Crear un nuevo Spinner
        Spinner newSpinner = new Spinner(this);

        List<String> obras = getObrasFromMusem(selectedMuseum);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, obras);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newSpinner.setAdapter(adapter);

        // Configurar las propiedades del layout del Spinner
        LinearLayout.LayoutParams spinnerLayoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        newSpinner.setLayoutParams(spinnerLayoutParams);

        // Crear un botón de borrar
        Button btnDelete = new Button(this);
        btnDelete.setText("Borrar");
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Eliminar el LinearLayout horizontal que contiene el Spinner y el botón de borrar
                linearLayout.removeView(horizontalLayout);
                spinners.remove(newSpinner); // Eliminar el Spinner de la lista de Spinners
                spinnerSelections.remove(newSpinner); // Eliminar la selección del Spinner de spinnerSelections
            }
        });

        // Agregar el Spinner y el botón de borrar al layout horizontal
        horizontalLayout.addView(newSpinner);
        horizontalLayout.addView(btnDelete);

        // Agregar el LinearLayout horizontal al contenedor principal
        linearLayout.addView(horizontalLayout);

        // Agregar el Spinner a la lista de Spinners
        spinners.add(newSpinner);

        newSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                spinnerSelections.put(newSpinner, selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerSelections.remove(newSpinner);
            }
        });
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
        List<Obra> obras = artworksByMuseum.get(getMuseumFromName(name));
        if (obras != null){
            Log.e("MUM", obras.toString());
        }
        Log.e("MUM",artworksByMuseum.toString());
        List<String> res = new ArrayList<>();
        if (obras != null) {
            for (Obra ob : obras) {
                res.add(ob.getName());
            }
        }
        return res;
    }

    public Museum getMuseumFromName(String name){
        Set<Museum> museos = artworksByMuseum.keySet();
        Museum res = null;
        for (Museum m : museos){
            if (m.getName() == name){
                res = m;
            }
        }
        return res;
    }

    private void setRoutesFromStrings(){
        Collection<List<Obra>> obras = artworksByMuseum.values();
        List<Obra> res = new ArrayList<>();
        for (String obra: spinnerSelections.values()){
            for (List<Obra> list : obras){
                for (Obra obraValue : list){
                    if (obra == obraValue.getName()){
                        res.add(obraValue);
                    }
                }
            }
        }
        routeBody.setArts(res);
    }

    public void createRoute(View view){
        routeBody.setMuseum(getMuseumFromName(selectedMuseum).getId());
        routeBody.setUser(data.getUser().getId());
        setRoutesFromStrings();
        data.setRouteBody(routeBody);

        Log.e("ROUTE FINAL", routeBody.toString());

        Intent intent = new Intent(this, CreateRouteFinal.class);
        startActivity(intent);
    }
}
