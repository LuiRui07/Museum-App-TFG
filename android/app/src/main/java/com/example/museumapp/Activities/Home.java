package com.example.museumapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.museumapp.R;
import com.example.museumapp.SharedData;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;


public class Home extends AppCompatActivity implements PermissionsListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button btnMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.home);

        // Obtener referencias a las vistas
        drawerLayout = findViewById(R.id.drawer_layout);
        btnMenu = findViewById(R.id.btn_menu);
        navigationView = findViewById(R.id.navigation_view);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        //mapView.getMapAsync((OnMapReadyCallback) this);

        // SharedData
        SharedData sharedData = SharedData.getInstance();
        TextView userText = findViewById(R.id.userText);
        userText.setText(sharedData.user);

        // Use MapView
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    Home.this.mapboxMap = mapboxMap;
                    mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(Style style) {
                            // Habilita el componente de ubicación
                            enableLocationComponent(style);
                        }
                    });
                }
            });
        // Configurar Drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_item1) {
                    Intent intent1 = new Intent(Home.this, Cuenta.class);
                    startActivity(intent1);
                } else if (item.getItemId() == R.id.nav_item2) {
                    Intent intent2 = new Intent(Home.this, Obras.class);
                    startActivity(intent2);
                } else {
                    Intent intent3 = new Intent(Home.this, Recorridos.class);
                    startActivity(intent3);
                }

                // Cierra el drawer layout después de que se haya seleccionado un elemento
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        }

        // Método invocado cuando se hace clic en el botón del menú
        public void onMenuButtonClick(View view) {
            // Abrir el cajón de navegación
            drawerLayout.openDrawer(GravityCompat.START);
    }


    @SuppressWarnings("MissingPermission")
    private void enableLocationComponent(Style loadedMapStyle) {
        // Verifica los permisos de ubicación
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Opciones de configuración del componente de ubicación
            LocationComponentOptions options = LocationComponentOptions.builder(this)
                    .build();

            // Configura y activa el componente de ubicación
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .locationComponentOptions(options)
                    .build());

            // Habilita la visualización de la ubicación del usuario en el mapa
            locationComponent.setLocationComponentEnabled(true);

            // Mueve la cámara a la ubicación actual del usuario
            LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(this);
            locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    if (result.getLastLocation() != null) {
                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude()), 12));
                    }
                }

                @Override
                public void onFailure(Exception exception) {
                    // Manejar el caso en que no se pueda obtener la ubicación actual del usuario
                }
            });
        } else {
            // Si los permisos de ubicación no están otorgados, solicítalos
            permissionsManager = new PermissionsManager((PermissionsListener) this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
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


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        
    }

    @Override
    public void onPermissionResult(boolean granted) {

    }
}