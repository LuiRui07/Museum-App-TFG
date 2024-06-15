package com.example.museumapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
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

import com.example.museumapp.Models.Obra;
import com.example.museumapp.Models.Route;
import com.example.museumapp.R;
import com.example.museumapp.Service.BeaconService;
import com.example.museumapp.Service.ObraService;
import com.example.museumapp.Service.RouteService;
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
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

public class InsideMuseum extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap mapboxMap;
    private boolean isRouteSelected = false;
    public String mapSource;
    public String museumId;
    private Button buttonSalir;
    private ObraService obraService;
    private List<Obra> obrasMuseo;
    private List<Point> puntosMuseo;
    private double[] location;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    SharedData data = SharedData.getInstance();
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.insidemuseum);

        constraintLayout = findViewById(R.id.constraint_layout);
        mapView = findViewById(R.id.mapView);
        buttonSalir = findViewById(R.id.button_salir);
        mapView.onCreate(savedInstanceState);
        obraService = new ObraService(this);
        puntosMuseo = new ArrayList<>();

        Intent intent = getIntent();
        mapSource = intent.getStringExtra("map");
        museumId = intent.getStringExtra("museum");
        location = intent.getDoubleArrayExtra("location");
        isRouteSelected = intent.getBooleanExtra("routeSelected", false);

        if (location != null && location.length == 2) {
            double temp = location[0];
            location[0] = location[1];
            location[1] = temp;
        }

        getObras(location);
    }

    private void getObras(double[] location) {
        obraService.getArtFromMuseum(museumId, new ObraService.ObraCallback() {
            @Override
            public void onSuccess(List<Obra> obras, Obra obra) {
                setObras(obras);
                initMap(location);
            }
        });
    }

    public void setObras(List<Obra> obras) {
        obrasMuseo = obras;
        for (Obra o : obrasMuseo) {
            double[] cord = o.getLocation().getCoordinates();
            puntosMuseo.add(Point.fromLngLat(cord[0], cord[1]));
        }

        Log.e("OBRAS", obras.toString());
        Log.e("PUNTOSMUSEO", puntosMuseo.toString());
    }

    private void initMap(double[] location) {
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
                            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 18.1));
                        }

                        drawObras(obrasMuseo);

                        if (isRouteSelected) {
                            buttonSalir.setText("Salir de Recorrido");
                            List<Point> routeCoordinates = getObrasRoute();
                            drawRoute(routeCoordinates);
                        }
                    }
                });

                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        return false;
                    }
                });
            }
        });
    }

    private List<Point> getObrasRoute() {
        List<Point> routeCoordinates = new ArrayList<>();
        Route route = data.getRoute();
        List<String> rutas = route.getArts();

        for (String artId : rutas) {
            for (Obra obra : obrasMuseo) {
                if (obra.getId().equals(artId)) {
                    double[] coords = obra.getLocation().getCoordinates();
                    routeCoordinates.add(Point.fromLngLat(coords[0], coords[1]));
                    break;
                }
            }
        }
        Log.e("ROUTECORD",routeCoordinates.toString());

        return routeCoordinates;
    }

    private void drawObras(List<Obra> obras) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    style.addImage("artwork-icon", BitmapFactory.decodeResource(getResources(), R.drawable.artwork_icon));

                    for (Obra obra : obras) {
                        Point point = Point.fromLngLat(obra.getLocation().getCoordinates()[0], obra.getLocation().getCoordinates()[1]);
                        Feature feature = Feature.fromGeometry(point);
                        feature.addStringProperty("title", obra.getName());

                        GeoJsonSource geoJsonSource = new GeoJsonSource("artwork-source-" + obra.getId(), feature);
                        style.addSource(geoJsonSource);

                        SymbolLayer symbolLayer = new SymbolLayer("artwork-layer-" + obra.getId(), "artwork-source-" + obra.getId());
                        symbolLayer.setProperties(
                                PropertyFactory.iconImage("artwork-icon"),
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconSize(0.3f),
                                PropertyFactory.textField("{title}"),
                                PropertyFactory.textSize(12f),
                                PropertyFactory.textOffset(new Float[]{0f, 1.5f}),
                                PropertyFactory.textAnchor(Property.TEXT_ANCHOR_TOP)
                        );
                        style.addLayer(symbolLayer);
                    }
                }
            });
        }
    }

    private void drawRoute(List<Point> routeCoordinates) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    style.removeLayer("route-layer");
                    style.removeSource("route-source");

                    style.addSource(new GeoJsonSource("route-source",
                            LineString.fromLngLats(routeCoordinates)));

                    style.addLayer(new LineLayer("route-layer", "route-source")
                            .withProperties(
                                    PropertyFactory.lineColor(Color.parseColor("#3bb2d0")),
                                    PropertyFactory.lineWidth(4f)
                            ));
                }
            });
        }
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

    public void salir(View view){
        Intent intent;
        if (isRouteSelected == true){
            intent = new Intent(InsideMuseum.this, InsideMuseum.class);
            intent.putExtra("map", data.getMuseo().getMap());
            intent.putExtra("museum", museumId);
            intent.putExtra("location", data.getMuseo().getLocation().getCoordinates());
            startActivity(intent);
        } else {
           intent = new Intent(InsideMuseum.this, Home.class);
           intent.putExtra("map", mapSource);
           intent.putExtra("museum", museumId);
           intent.putExtra("location", location);
        }
        startActivity(intent);
    }
}
