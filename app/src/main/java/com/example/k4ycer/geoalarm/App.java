package com.example.k4ycer.geoalarm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL = "CanalLocalizacion";

    @Override
    public void onCreate() {
        super.onCreate();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager nm = (NotificationManager) getSystemService(NotificationManager.class);
            NotificationChannel nc = new NotificationChannel(CHANNEL, "Cala de localizacion de alarma", NotificationManager.IMPORTANCE_DEFAULT);
            nc.setDescription("Canal que sigue la ubicaciond el usuario para activar la alarma");
            nc.enableLights(true);
            nc.setLightColor(Color.BLUE);
            nc.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            nc.enableVibration(true);
            nm.createNotificationChannel(nc);
        }
    }
}
