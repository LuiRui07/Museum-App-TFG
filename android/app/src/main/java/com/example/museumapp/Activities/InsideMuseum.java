package com.example.museumapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.museumapp.R;
import com.example.museumapp.SharedData;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class InsideMuseum extends AppCompatActivity {


    private MapView mapView;
    private MapboxMap mapboxMap;
    SharedData data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.insidemuseum);

        Intent intent = getIntent();
        String geojson = intent.getStringExtra("Geojson");
        double[] location = intent.getDoubleArrayExtra("location");

        data = SharedData.getInstance();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                InsideMuseum.this.mapboxMap = mapboxMap;
                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        double latitude = point.getLatitude();
                        double longitude = point.getLongitude();
                        Log.e("HA CLICADO EN-----"," LAT: " + latitude + "LON: " + longitude);
                        return true;
                    }
                });

                mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/luisruiz11/clwi1xige00gb01pc5a4j3zvv"), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(Style style) {


                    }
                });
                if (location != null && location.length == 2) {
                    LatLng center = new LatLng(location[0], location[1]);
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
                }
            }
        });
    }
}
