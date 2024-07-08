package com.example.museumapp.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class BeaconService extends Service implements BeaconConsumer {
    private static final String TAG = "BeaconService";
    private BeaconManager beaconManager;
    private Region region;

    @Override
    public void onCreate() {
        super.onCreate();

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
                    for (Beacon beacon : beacons) {
                        Log.d("AltBeacon", "UUID: " + beacon.getId1() +
                                " Major: " + beacon.getId2() +
                                " Minor: " + beacon.getId3() +
                                " Distancia: " + beacon.getDistance());

                        // Mostrar mensaje Toast con la información del beacon
                        // Debes usar un handler ya que Toast no se puede llamar directamente desde un servicio
                        new android.os.Handler(getMainLooper()).post(() ->
                                Toast.makeText(BeaconService.this, "Beacon detectado: " + beacon.getId1(), Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });

        beaconManager.bind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // No estamos proporcionando binding, así que retorna null
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
    public void unbindService(ServiceConnection connection) {
        // Aquí no es necesario hacer nada en este método
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection connection, int mode) {
        // Aquí no es necesario hacer nada en este método
        return false;
    }
}