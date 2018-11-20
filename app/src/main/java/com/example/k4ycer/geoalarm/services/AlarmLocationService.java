package com.example.k4ycer.geoalarm.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.k4ycer.geoalarm.App;
import com.example.k4ycer.geoalarm.MainActivity;
import com.example.k4ycer.geoalarm.R;

public class AlarmLocationService extends Service {
    IBinder mBinder = new CustomBinder();

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
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
