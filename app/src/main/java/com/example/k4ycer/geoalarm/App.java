package com.example.k4ycer.geoalarm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL = "CanalLocalizacion";
    public static final String CHANNEL_ALARM = "CanalAlarma";

    @Override
    public void onCreate() {
        super.onCreate();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager nm = (NotificationManager) getSystemService(NotificationManager.class);
            NotificationChannel nc = new NotificationChannel(CHANNEL, "Canal de localizacion de alarma", NotificationManager.IMPORTANCE_DEFAULT);
            nc.setDescription("Canal que sigue la ubicaciond el usuario para activar la alarma");
            nc.enableLights(true);
            nc.setLightColor(Color.BLUE);
            nc.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            nc.enableVibration(true);
            nm.createNotificationChannel(nc);

            NotificationChannel ncAlarm = new NotificationChannel(CHANNEL_ALARM, "Canal de notificacion de alarma", NotificationManager.IMPORTANCE_HIGH);
            ncAlarm.setDescription("Canal que notifica al usuario cuando se activa una alarma");
            ncAlarm.enableLights(true);
            ncAlarm.setLightColor(Color.RED);
            ncAlarm.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            ncAlarm.enableVibration(true);
            nm.createNotificationChannel(ncAlarm);
        }
    }
}
