package com.example.k4ycer.geoalarm.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.k4ycer.geoalarm.App;
import com.example.k4ycer.geoalarm.MainActivity;
import com.example.k4ycer.geoalarm.R;
import com.example.k4ycer.geoalarm.model.Element;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class AlarmLocationService extends Service{
    IBinder mBinder = new CustomBinder();
    private LocationManager lm = null;
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER)
    };
    String [] myPermissions;
    List<Element> alarms;
    NotificationManagerCompat nm;

    private class LocationListener implements android.location.LocationListener{
        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            searchForMatches(new LatLng(location.getLatitude(), location.getLongitude()), alarms, 10);
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

        nm = NotificationManagerCompat.from(this);

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

    public void loadAlarms(List<Element> alarms){
        this.alarms = alarms;
    }

    public void searchForMatches(LatLng currentLocation, List<Element> alarms, double radius){
        if(alarms == null){
            return;
        }
        for (Element alarm: alarms) {
            double distance = distance(currentLocation.latitude, alarm.getLatLng().latitude, currentLocation.longitude, alarm.getLatLng().longitude, 0, 0);
            if(distance <= radius){
                sendNotification(alarm);
            }
        }
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public void sendNotification(Element alarm){
        NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ALARM)
                .setContentTitle(alarm.getName())
                .setContentText(alarm.getDescription())
                .setSmallIcon(R.drawable.ic_access_alarm_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
        nm.notify(3, notification);
        /*
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "ALARM")
                .setSmallIcon(R.drawable.ic_access_alarm_black_24dp)
                .setContentTitle(alarm.getName())
                .setContentText(alarm.getDescription())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[] { 0, 1000, 500, 1000 })
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        nm.notify(2, mBuilder.build());*/
    }
}
