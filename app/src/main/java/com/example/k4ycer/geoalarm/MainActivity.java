package com.example.k4ycer.geoalarm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.k4ycer.geoalarm.data.SQLUtilities;
import com.example.k4ycer.geoalarm.model.Element;
import com.example.k4ycer.geoalarm.services.AlarmLocationService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {
    ListView lv;
    Button btnIniciar;
    AlarmLocationService alarmLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.alarmsList);

        //Para mostrar las alarmas agregadas
        SQLUtilities conexion = new SQLUtilities(MainActivity.this, "Alarm",null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        List<Element> list = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT name, descrition, status FROM Alarm", null);
        if (c.moveToFirst()) {
            do {
                list.add(new Element(c.getString(0),c.getString(1),c.getInt(2) > 0));
            } while(c.moveToNext());
        }

        db.close();


        if(list != null) {
            ArrayAdapter<Element> adaptador = new CustomAdapterAlarm(
                    MainActivity.this,
                    R.layout.custom_layout_alarm,
                    list
            );
            lv.setAdapter(adaptador);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle b = new Bundle();
                    Element ayuda = (Element) lv.getItemAtPosition(position);
                    b.putString("Name", ayuda.getName());
                    Intent i = new Intent(MainActivity.this, EditAlarm.class);
                    i.putExtra("bundle", b);
                    startActivity(i);
                }
            });
        }

        // Iniciar servicio de localizacion
        Intent i = new Intent(MainActivity.this, AlarmLocationService.class);
        ContextCompat.startForegroundService(MainActivity.this, i);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Bind to geolocation service
        Intent i = new Intent(this, AlarmLocationService.class);
        bindService(i, this, Context.BIND_AUTO_CREATE);

        // Get writable Database
        SQLUtilities conexion = new SQLUtilities(MainActivity.this, "Alarm",null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        // Get alarms from Database
        List<Element> list = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT name, descrition, status FROM Alarm", null);
        if (c.moveToFirst()) {
            do {
                list.add(new Element(c.getString(0),c.getString(1),c.getInt(2) > 0));
            } while(c.moveToNext());
        }
        db.close();

        // Show alarms in listview
        if(list != null) {
            ArrayAdapter<Element> adaptador = new CustomAdapterAlarm(
                    MainActivity.this,
                    R.layout.custom_layout_alarm,
                    list
            );
            lv.setAdapter(adaptador);

            /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MainActivity.this, "hola", Toast.LENGTH_SHORT).show();
                    Bundle b = new Bundle();
                    Element ayuda = (Element) lv.getItemAtPosition(position);
                    b.putString("Name", ayuda.getName());
                    Intent i = new Intent(MainActivity.this, EditAlarm.class);
                    i.putExtra("bundle", b);
                    startActivity(i);
                }
            });*/
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnAddAlarm:
                Intent i = new Intent(MainActivity.this, AddAlarm.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        AlarmLocationService.CustomBinder b = (AlarmLocationService.CustomBinder) service;
        alarmLocationService = b.getService();
        Toast.makeText(MainActivity.this, "Conectado al servicio", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        alarmLocationService = null;
        Toast.makeText(MainActivity.this, "Desconectado del servicio", Toast.LENGTH_SHORT).show();
    }
}
