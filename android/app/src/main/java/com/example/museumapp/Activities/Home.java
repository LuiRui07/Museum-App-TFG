package com.example.museumapp.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.museumapp.Api.ApiClient;
import com.example.museumapp.Api.ApiService;
import com.example.museumapp.Api.LoginRequest;
import com.example.museumapp.Api.Response;
import com.example.museumapp.Models.Museum;
import com.example.museumapp.Models.Obra;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Home extends AppCompatActivity implements PermissionsListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;

    private LocationManager locationManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button btnMenu;

    private SharedData sharedData = SharedData.getInstance();

    private Double[] userLocation = {};

    TextView estas ;

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
        // Obtén una referencia al botón de ubicación
        ImageButton btnLocate = findViewById(R.id.btn_locate);
        //mapView.getMapAsync((OnMapReadyCallback) this);

        TextView userText = findViewById(R.id.userText);
        userText.setText(sharedData.user);
        estas = findViewById(R.id.estas);


        // Use MapView
        mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    Home.this.mapboxMap = mapboxMap;
                    mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public boolean onMapClick(@NonNull LatLng point) {
                            // Se ejecuta cuando el usuario hace clic en el mapa
                            // Aquí puedes obtener las coordenadas del punto donde se ha hecho clic
                            double latitude = point.getLatitude();
                            double longitude = point.getLongitude();
                            Log.e("HA CLICADO EN-----"," LAT: " + latitude + "LON: " + longitude);
                            locateMuseum(2,latitude,longitude);
                            return true; // Devuelve true si has consumido el evento y no deseas que se propague más
                        }
                    });

                    mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/luisruiz11/clw3f43jf02ip01qvcs4hcdv7"), new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(Style style) {
                            // Habilita el componente de ubicación
                            enableLocationComponent(style);
                            enableBluetooth();

                        }
                    });
                }
        });

        // Configurar Drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.nav_item1) {
                    intent = new Intent(Home.this, Cuenta.class);
                }
                if (item.getItemId() == R.id.nav_item2) {
                    intent = new Intent(Home.this, Museos.class);
                } else if (item.getItemId() == R.id.nav_item2) {
                    intent = new Intent(Home.this, Obra.class);
                } else {
                    intent = new Intent(Home.this, Recorridos.class);
                }
                startActivity(intent);

                // Cierra el drawer layout después de que se haya seleccionado un elemento
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            });
    }

    private void locateMuseum(int tipo,double lat, double lon ){
        Retrofit retrofit = ApiClient.addHeader(this);
        ApiService apiService = retrofit.create(ApiService.class);

        Call<Museum> call = apiService.getMuseumFromCoords(lat,lon);

        call.enqueue(new Callback<Museum>() {
            @Override
            public void onResponse(Call<Museum> call, retrofit2.Response<Museum> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {  ///Otra condición
                        Log.e("Respuesta Museo", response.message());
                        Museum museo = response.body();
                        Log.e("Museo Encontrado----", museo.toString());
                        estas.setText("Estas en "+ museo.getName());
                        if (tipo == 1){
                            sharedData.setMuseumLocalizado(museo);
                            Log.e("ESTA EN EL MUSEO", museo.toString());
                        } else {
                            sharedData.setMuseumSeleccionado(museo);
                            Log.e("CLICKO EN EL MUSEO", museo.toString());
                        }
                    } else {
                        if (tipo == 1){
                            toast("No está en ningún museo");
                        }
                        Log.e("Respuesta Museo NULA", response.message());
                    }
                }}
            @Override
            public void onFailure(Call<Museum> call, Throwable t) {
                        Log.e("Respuesta Museo", t.getMessage());
            }
        });
    }

    public void locate(View view){
        // Solicita la ubicación del usuario y centra el mapa en esa ubicación
        if (mapboxMap != null) {
            // Verifica si se tienen los permisos de ubicación
            if (PermissionsManager.areLocationPermissionsGranted(Home.this)) {
                // Activa y configura el componente de ubicación si los permisos están otorgados
                enableLocationComponent(mapboxMap.getStyle());
                getLocation();
                //locateMuseum(1,userLocation[0],userLocation[1]);
            } else {
                // Solicita los permisos de ubicación si no están otorgados
                permissionsManager = new PermissionsManager(Home.this);
                permissionsManager.requestLocationPermissions(Home.this);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        // Inicializar LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Verificar si el proveedor de ubicación está habilitado
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Si el proveedor de ubicación está habilitado, solicitar actualizaciones de ubicación
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            // Si el proveedor de ubicación no está habilitado, mostrar un mensaje al usuario
            Toast.makeText(this, "Habilita el proveedor de ubicación", Toast.LENGTH_SHORT).show();
        }
    }

    // LocationListener para escuchar los cambios en la ubicación del usuario
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            // Obtener las coordenadas de ubicación del usuario
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            setUserLocation(latitude,longitude);
            Log.e("UBICACION", "LAt: " + latitude + "LON: " + longitude);

            // Hacer lo que necesites con las coordenadas (por ejemplo, almacenarlas en MongoDB)
            // Aquí puedes llamar a tus métodos para almacenar las coordenadas en tu base de datos MongoDB

            // Detener las actualizaciones de ubicación una vez que las coordenadas se obtienen
            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };



    // Método invocado cuando se hace clic en el botón del menú
    public void onMenuButtonClick(View view) {
        // Abrir el cajón de navegación
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /// METODOS PARA PEDIR PERMISO BLUETHOOT Y LOCALIZACIÓN

    private ActivityResultLauncher<Intent> enableBluetoothLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // El usuario habilitó el Bluetooth
                    // Aquí puedes realizar cualquier acción adicional que necesites
                } else {
                    // El usuario canceló la solicitud de habilitar Bluetooth
                    // Aquí puedes manejar este caso, por ejemplo, mostrando un mensaje al usuarioe
                    enableBluetooth();
                }
            }
    );

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // El dispositivo no soporta Bluetooth
            // Aquí puedes manejar este caso, por ejemplo, mostrando un mensaje al usuario
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
        } else {
            // El Bluetooth ya está habilitado
            // Aquí puedes realizar cualquier acción adicional que necesites
        }
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
                getLocation();
            }

            @Override
            public void onFailure(Exception exception) {
                // Manejar el caso en que no se pueda obtener la ubicación actual del usuario
            }
        });
        } else {
        // Si los permisos de ubicación no están otorgados, se solicitan
        permissionsManager = new PermissionsManager((PermissionsListener) this);
        permissionsManager.requestLocationPermissions(this);
        }
    }


    // MÉTODOS DEFAULT MAPA
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

    public void setUserLocation(double lat, double lon) {
        userLocation = new Double[]{lat, lon};
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}