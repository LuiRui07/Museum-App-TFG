package com.example.museumapp.Service;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.museumapp.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class BeaconService extends AppCompatActivity implements BeaconConsumer {
    private static final String TAG = "BeaconService";
    private BeaconManager beaconManager;
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obra_info);

        // Inicializar BeaconManager
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // Establecer un parser para los beacons AltBeacon
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));

        // Configurar la región a escanear (puedes ajustar esto según tus necesidades)
        region = new Region("myUniqueRegionId", null, null, null);

        // Configurar listener para manejar los beacons detectados
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.d(TAG, "Número de beacons detectados: " + beacons.size());
                    // Aquí puedes manejar los beacons detectados
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Iniciar escaneo de beacons cuando la actividad se reanuda
        beaconManager.bind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Detener escaneo de beacons cuando la actividad se pausa
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (Exception e) {
            Log.e(TAG, "No se pudo iniciar el escaneo de beacons: " + e.getMessage());
        }
    }

    @Override
    public Context getApplicationContext() {
        return this.getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection connection) {
        // Aquí no es necesario hacer nada en este método
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection connection, int mode) {
        // Aquí no es necesario hacer nada en este método
        return false;
    }
}
