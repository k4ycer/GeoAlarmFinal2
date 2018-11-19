package com.example.k4ycer.geoalarm;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.k4ycer.geoalarm.data.SQLUtilities;
import com.example.k4ycer.geoalarm.model.Element;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ListView lv;
    Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.alarmsList);
        add = findViewById(R.id.btnAdd);

        add.setOnClickListener(this);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public void onClick(View v) {
        if(v.getId() == R.id.btnAdd){
            Intent i = new Intent(MainActivity.this, AddAlarm.class);
            startActivity(i);
        }
    }
}
