package com.example.museumapp.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.museumapp.Models.Museum;
import com.example.museumapp.R;
import com.example.museumapp.Service.MuseumService;
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
    private LocationManager locationManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedData sharedData = SharedData.getInstance();
    private Museum museoActual;
    private Double[] userLocation = {};
    private ConstraintLayout containerLayout;
    private Button buttonEntrar;
    private static final int BUTTON_ENTRAR_ID = View.generateViewId();
    private TextView estas;
    private MuseumService museumService;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            setUserLocation(location.getLatitude(), location.getLongitude());
            Log.e("UBICACION", "LAT: " + location.getLatitude() + "LON: " + location.getLongitude());
            locationManager.removeUpdates(this);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private final ActivityResultLauncher<Intent> enableBluetoothLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != Activity.RESULT_OK) {
                    enableBluetooth();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.home);

        museumService = new MuseumService(this);

        initViews();
        setupMapView(savedInstanceState);
        setupDrawer();
        locate(mapView);
        getLocation();
    }

    private void initViews() {
        containerLayout = findViewById(R.id.container_layout);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        estas = findViewById(R.id.estas);
    }

    private void setupMapView(Bundle savedInstanceState) {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {
            Home.this.mapboxMap = mapboxMap;
            mapboxMap.addOnMapClickListener(point -> {
                locateMuseum(2, point.getLatitude(), point.getLongitude());
                return true;
            });

            mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/luisruiz11/clw3f43jf02ip01qvcs4hcdv7"), style -> {
                enableLocationComponent(style);
                enableBluetooth();
            });
        });
    }

    private void setupDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_item1) {
                intent = new Intent(Home.this, Cuenta.class);
            } else if (itemId == R.id.nav_item2) {
                intent = new Intent(Home.this, Museos.class);
            } else if (itemId == R.id.nav_item3) {
                intent = new Intent(Home.this, Obras.class);
            } else {
                intent = new Intent(Home.this, Recorridos.class);
            }

            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    public void onClickButton(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void setButtonMuseo() {
        if (buttonEntrar != null) return;
        buttonEntrar = new Button(this);
        buttonEntrar.setText("Entrar al Museo");
        buttonEntrar.setId(BUTTON_ENTRAR_ID);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 0, 0, 20);
        params.bottomToTop = R.id.mapView;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.horizontalBias = 0.95f;
        buttonEntrar.setLayoutParams(params);

        buttonEntrar.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Entrando al Museo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Home.this, InsideMuseum.class);
            intent.putExtra("Map", museoActual.getMap());
            intent.putExtra("location", museoActual.getLocation().getCoordinates());
            startActivity(intent);
        });

        containerLayout.addView(buttonEntrar);
    }

    private void destroyButtonMuseo() {
        if (buttonEntrar != null) {
            containerLayout.removeView(buttonEntrar);
            buttonEntrar = null;
        }
    }

    private void locateMuseum(int tipo, double lat, double lon) {
        museumService.getMuseumFromCoords(lat, lon, tipo, new MuseumService.MuseumCallback() {
            @Override
            public void onSuccess(Museum result, int tipo, List<Museum> museos, Context context) {
                setButtonMuseo();
                if (tipo == 2) {
                    Intent intent = new Intent(Home.this, Obras.class);
                    intent.putExtra("museum_id", result.getId());
                    intent.putExtra("museum_name", result.getName());
                    startActivity(intent);
                } else if (tipo == 1){
                    museoActual = result;
                    estas.setText("Estas en " + result.getName());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                destroyButtonMuseo();
                //estas.setText("");
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            // Solicitar última ubicación conocida
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                setUserLocation(location.getLatitude(), location.getLongitude());
                // Procesar la ubicación inicial (opcional)
            } else {
                // No hay ubicación reciente, el usuario puede activar GPS
                Toast.makeText(this, "Habilita el GPS para obtener tu ubicación", Toast.LENGTH_SHORT).show();
            }

            // Solicitar actualizaciones de ubicación (opcional para rastreo continuo)
            if (PermissionsManager.areLocationPermissionsGranted(Home.this)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else {
                permissionsManager = new PermissionsManager(this);
                permissionsManager.requestLocationPermissions(this);
            }
        }
    }

    public void locate(View view) {
        if (mapboxMap != null && PermissionsManager.areLocationPermissionsGranted(Home.this)) {
            enableLocationComponent(mapboxMap.getStyle());
            getLocation();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void enableLocationComponent(Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponentOptions options = LocationComponentOptions.builder(this).build();
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .locationComponentOptions(options)
                    .build());
            locationComponent.setLocationComponentEnabled(true);

            LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(this);
            locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    if (result.getLastLocation() != null) {
                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude()), 12));
                    }
                    getLocation();
                }

                @Override
                public void onFailure(Exception exception) {}
            });
        } else {
            permissionsManager = new PermissionsManager(this);
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
    public void onExplanationNeeded(List<String> permissionsToExplain) {}

    @Override
    public void onPermissionResult(boolean granted) {}

    public void setUserLocation(double lat, double lon) {
        userLocation = new Double[]{lat, lon};
        locateMuseum(1, userLocation[0],userLocation[1]);
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
