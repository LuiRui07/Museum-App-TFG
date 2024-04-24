package com.example.museumapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.Style;


public class Home extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap mapboxMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Mapbox


        // Configurar el diseño de la actividad
        setContentView(R.layout.home);

        // Crear el mapa
        mapView = new MapView(this);

        // Configurar la posición de la cámara inicial
        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(-98.0, 39.5))
                .zoom(2.0)
                .pitch(0.0)
                .bearing(0.0)
                .build();

        // Agregar el mapa a la actividad
        setContentView(mapView);

        // Inicializar el mapa asíncronamente
        mapView.getMapboxMap().getStyle(style -> {
            // Configurar el estilo del mapa


            // Establecer la cámara inicial
            mapboxMap.setCamera(cameraOptions);
        });
    }

}
