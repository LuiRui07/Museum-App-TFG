package com.example.museumapp.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.museumapp.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class BeaconService extends Service implements BeaconConsumer, RangeNotifier {

    private static final String TAG = BeaconService.class.getSimpleName();
    private static final long DEFAULT_SCAN_PERIOD_MS = 6000L;
    private static final String ALL_BEACONS_REGION = "AllBeaconsRegion";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "BeaconServiceChannel";

    private BeaconManager mBeaconManager;
    private Region mRegion;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Servicio creado");

        initializeBeaconManager();

        // Mostrar notificación persistente
        showNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBeaconManager != null) {
            try {
                mBeaconManager.stopRangingBeaconsInRegion(mRegion);
                mBeaconManager.removeAllRangeNotifiers();
                mBeaconManager.unbind(this);
            } catch (RemoteException e) {
                Log.d(TAG, "Se ha producido una excepción al detener el escaneo de beacons: " + e.getMessage());
            }
        }
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            mBeaconManager.startRangingBeaconsInRegion(mRegion);
            Log.d(TAG, "Servicio de beacons conectado y escaneo iniciado");
        } catch (RemoteException e) {
            Log.d(TAG, "Se ha producido una excepción al empezar a buscar beacons: " + e.getMessage());
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons.size() > 0) {
            for (Beacon beacon : beacons) {
                Log.d(TAG, "Beacon detectado: UUID=" + beacon.getId1() +
                        ", Major=" + beacon.getId2() +
                        ", Minor=" + beacon.getId3() +
                        ", Distancia=" + beacon.getDistance());

                Intent intent = new Intent("com.example.museumapp.BEACON_DETECTED");
                intent.putExtra("UUID", beacon.getId1().toString());
                intent.putExtra("Major", beacon.getId2().toString());
                intent.putExtra("Minor", beacon.getId3().toString());
                intent.putExtra("Distance", beacon.getDistance());
                sendBroadcast(intent);

                // Mostrar un Toast mensaje en la interfaz de usuario
                new Handler(getMainLooper()).post(() -> {
                    Toast.makeText(BeaconService.this, "Beacon detectado: " + beacon.getId1(), Toast.LENGTH_SHORT).show();
                });
            }
        } else {
            Log.d(TAG, "No se detectaron beacons en esta región.");
        }
    }

    private void initializeBeaconManager() {
        mBeaconManager = BeaconManager.getInstanceForApplication(this);

        // Configurar el BeaconParser para detectar beacons Eddystone UID
        mBeaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        ArrayList<Identifier> identifiers = new ArrayList<>();
        mRegion = new Region(ALL_BEACONS_REGION, identifiers);

        // Configurar la región a escanear
        mBeaconManager.setForegroundScanPeriod(DEFAULT_SCAN_PERIOD_MS);
        mBeaconManager.addRangeNotifier(this);

        mBeaconManager.bind(this);
    }

    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Beacon Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Buscando beacons")
                .setContentText("La aplicación está buscando beacons cercanos")
                .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener este recurso
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void showToastMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
}