package com.example.k4ycer.geoalarm.services;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.k4ycer.geoalarm.App;
import com.example.k4ycer.geoalarm.MainActivity;
import com.example.k4ycer.geoalarm.R;

public class AlarmLocationService extends Service{
    IBinder mBinder = new CustomBinder();
    private LocationManager lm = null;
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER)
    };
    String [] myPermissions;

    private class LocationListener implements android.location.LocationListener{
        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public class CustomBinder extends Binder {
        public AlarmLocationService getService(){
            return AlarmLocationService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL)
                .setContentTitle("GeoAlarm tracking location")
                .setContentText("Iniciado desde bind")
                .setSmallIcon(R.drawable.ic_access_alarm_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String cadena = intent.getStringExtra("cadena");

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL)
                .setContentTitle("GeoAlarm tracking location")
                .setContentText(cadena)
                .setSmallIcon(R.drawable.ic_access_alarm_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        myPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(lm == null){
            lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        String myPermissions[] = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        boolean granted = true;
        for (String permission : myPermissions)
            granted &= ActivityCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;

        if (granted){
            try{
                lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        1,
                        mLocationListeners[0]);
            }catch (java.lang.SecurityException ex) {
                Log.i("k4ycer15", "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d("k4ycer15", "network provider does not exist, " + ex.getMessage());
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lm != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    lm.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i("k4ycer15", "fail to remove location listener, ignore", ex);
                }
            }
        }
    }
}
