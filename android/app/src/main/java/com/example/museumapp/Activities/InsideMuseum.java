package com.example.museumapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
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

import java.util.List;

public class InsideMuseum extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private String mapSource;
    private double[] location;
    SharedData data;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.insidemuseum);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mapSource = intent.getStringExtra("map");
        location = intent.getDoubleArrayExtra("location");

        if (location != null && location.length == 2) {
            // Invertir
            double temp = location[0];
            location[0] = location[1];
            location[1] = temp;
        }

        data = SharedData.getInstance();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                InsideMuseum.this.mapboxMap = mapboxMap;

                mapboxMap.setStyle(new Style.Builder().fromUri(mapSource), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);

                        if (location != null && location.length == 2) {
                            LatLng center = new LatLng(location[0], location[1]);
                            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 18));
                        }
                    }
                });

                // Agregar listener de clics en el mapa
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

    public void verObras(View view){
        Intent intent = new Intent(InsideMuseum.this, Obras.class);
        intent.putExtra("museum_id", data.getMuseo().getId());
        intent.putExtra("museum_name",data.getMuseo().getName());
        startActivity(intent);
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
    protected void onSaveInstanceState(Bundle outState) {
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
}
