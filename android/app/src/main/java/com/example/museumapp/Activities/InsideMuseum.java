package com.example.museumapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.museumapp.R;
import com.example.museumapp.Service.BeaconService;
import com.example.museumapp.SharedData;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

public class InsideMuseum extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private boolean isRouteSelected = false;
    SharedData data = SharedData.getInstance();

    public String mapSource;
    private Button buttonQuitarRecorrido;
    private static final int BUTTON_SALIR_ID = View.generateViewId();
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.insidemuseum);

        constraintLayout = findViewById(R.id.constraint_layout);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mapSource = intent.getStringExtra("map");
        double[] location = intent.getDoubleArrayExtra("location");
        isRouteSelected = intent.getBooleanExtra("routeSelected",false);

        if (location != null && location.length == 2) {
            // Invertir
            double temp = location[0];
            location[0] = location[1];
            location[1] = temp;
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                InsideMuseum.this.mapboxMap = mapboxMap;

                mapboxMap.setStyle(new Style.Builder().fromUri(mapSource), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Define las coordenadas de la ruta de ejemplo
                        List<Point> routeCoordinates = new ArrayList<>();
                        routeCoordinates.add(Point.fromLngLat(-4.4141808491612, 36.71894427343516)); // Longitud, Latitud
                        routeCoordinates.add(Point.fromLngLat(-4.412477632486366, 36.71865740871274)); // Longitud, Latitud
                        routeCoordinates.add(Point.fromLngLat(-4.4126331624193, 36.718831019893)); // Nuevo punto
                        routeCoordinates.add(Point.fromLngLat(-4.4126331624191, 36.7188310198921)); // Nuevo punto

                        enableLocationComponent(style);

                        if (location != null && location.length == 2) {
                            LatLng center = new LatLng(location[0], location[1]);
                            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 18));
                        }
                        // Verificar si hay una ruta seleccionada
                        if (isRouteSelected) {
                            // Llama al método para dibujar la ruta en el mapa si está seleccionada
                            drawRoute(routeCoordinates);
                            setButtonQuitarRuta();
                        }

                        // Ajusta la cámara para hacer zoom en el primer punto de la ruta
                        if (routeCoordinates.size() > 0) {
                            LatLng firstCoordinate = new LatLng(routeCoordinates.get(0).latitude(), routeCoordinates.get(0).longitude());
                            CameraPosition position = new CameraPosition.Builder()
                                    .target(firstCoordinate)
                                    .zoom(15.0) // Ajusta el nivel de zoom según sea necesario
                                    .build();
                            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);
                        } else if (location != null && location.length == 2) {
                            LatLng center = new LatLng(location[0], location[1]);
                            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 12.0));
                        }
                    }
                });

                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        // Buscar características en el punto clicado
                        PointF screenPoint = mapboxMap.getProjection().toScreenLocation(point);
                        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint);
                        Log.e("INSIDE", "ALGO HAS CLICKADO");
                        Log.e("INSIDE", features.toString());
                        Log.e("INSIDE", screenPoint.toString());
                        // Verificar si alguna característica fue clicada
                        if (!features.isEmpty()) {

                            Feature feature = features.get(0);
                            String id = feature.getStringProperty("id"); // Obtener ID del ícono
                            // Manejar el clic según el ID u otra propiedad
                            handleIconClick(id);
                            return true;
                        }

                        return false;
                    }
                });
            }
        });
    }

    private void handleIconClick(String id) {
        // Reaccionar según el ID del ícono clicado
        if (id != null) {
            Toast.makeText(this, "Icono clicado con ID: " + id, Toast.LENGTH_SHORT).show();
            // Realizar otras acciones según el ID
            // Por ejemplo, puedes iniciar una nueva actividad, mostrar un diálogo, etc.
        }
    }

    // Método para dibujar la ruta en el mapa
    private void drawRoute(List<Point> routeCoordinates) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    // Remueve la capa anterior si existe
                    style.removeLayer("route-layer");
                    style.removeSource("route-source");

                    // Agrega una fuente GeoJSON con la lista de coordenadas de la ruta
                    style.addSource(new GeoJsonSource("route-source",
                            LineString.fromLngLats(routeCoordinates)));

                    // Agrega una capa de línea para mostrar la ruta
                    style.addLayer(new LineLayer("route-layer", "route-source")
                            .withProperties(
                                    com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor(Color.parseColor("#3bb2d0")),
                                    com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth(4f) // Ajusta el grosor de la línea aquí
                            ));
                }
            });
        }
    }

    private void setButtonQuitarRuta() {
        if (buttonQuitarRecorrido != null) return;

        buttonQuitarRecorrido = new Button(this);
        buttonQuitarRecorrido.setText("Salir");
        buttonQuitarRecorrido.setId(BUTTON_SALIR_ID);
        buttonQuitarRecorrido.setTextSize(13);
        buttonQuitarRecorrido.setBackgroundResource(R.drawable.button_rounded);
        buttonQuitarRecorrido.setTextColor(Color.parseColor("#FFFFFF"));

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 16, 16); // Margen derecho y margen inferior
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = R.id.mapView;
        buttonQuitarRecorrido.setLayoutParams(params);

        buttonQuitarRecorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para volver a la actividad principal
                Intent intent = new Intent(InsideMuseum.this, InsideMuseum.class);
                intent.putExtra("map", data.getMuseo().getMap());
                intent.putExtra("location", data.getMuseo().getLocation().getCoordinates());
                startActivity(intent);
            }
        });

        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        constraintLayout.addView(buttonQuitarRecorrido);
    }

    // Método para manejar la selección de la ruta
    private void handleRouteSelection(boolean selected) {
        isRouteSelected = selected;
        if (selected) {
            // Simplemente llama al método para dibujar la ruta si está seleccionada
            drawRoute(getSelectedRouteCoordinates()); // Aquí obtén las coordenadas de la ruta seleccionada
        } else {
            // Si no está seleccionada, puedes remover la ruta del mapa si es necesario
            if (mapboxMap != null && mapboxMap.getStyle() != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    style.removeLayer("route-layer");
                    style.removeSource("route-source");
                }
            }
        }
    }

    // Método ficticio para obtener las coordenadas de la ruta seleccionada
    private List<Point> getSelectedRouteCoordinates() {
        // Aquí deberías implementar la lógica para obtener las coordenadas de la ruta seleccionada
        List<Point> selectedRouteCoordinates = new ArrayList<>();
        // Por ahora, devuelve un conjunto de coordenadas de ejemplo
        selectedRouteCoordinates.add(Point.fromLngLat(-4.4141808491612, 36.71894427343516)); // Longitud, Latitud
        selectedRouteCoordinates.add(Point.fromLngLat(-4.412477632486366, 36.71865740871274)); // Longitud, Latitud
        selectedRouteCoordinates.add(Point.fromLngLat(-4.4126331624193, 36.718831019893)); // Nuevo punto
        selectedRouteCoordinates.add(Point.fromLngLat(-4.4126331624191, 36.7188310198921)); // Nuevo punto
        return selectedRouteCoordinates;
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(this);
            locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    Location location = result.getLastLocation();
                    if (location != null) {
                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(location.getLatitude(), location.getLongitude()), 18));
                    }
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("LocationEngine", exception.getLocalizedMessage());
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull MapboxMap mapboxMap) {
                        mapboxMap.setStyle(new Style.Builder().fromUri(mapSource), new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                enableLocationComponent(style);
                            }
                        });
                    }
                });
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        startBeaconService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        stopBeaconService();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void startBeaconService() {
        Intent intent = new Intent(this, BeaconService.class);
        startService(intent);
    }

    private void stopBeaconService() {
        Intent intent = new Intent(this, BeaconService.class);
        stopService(intent);
    }

    //---------------- Métodos botones
    public void verObras(View view){
        Intent intent = new Intent(this, Obras.class);
        intent.putExtra("museum_id", data.getMuseo().getId());
        intent.putExtra("museum_name",data.getMuseo().getName());
        startActivity(intent);
    }

    public void verRecorridos(View view){
        Intent intent = new Intent(this, Recorridos.class);
        intent.putExtra("tipo",1);
        intent.putExtra("idMuseum", data.getMuseo().getId());
        startActivity(intent);
    }
}



