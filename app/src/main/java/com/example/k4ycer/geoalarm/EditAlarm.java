package com.example.k4ycer.geoalarm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.k4ycer.geoalarm.data.SQLUtilities;
import com.example.k4ycer.geoalarm.model.Element;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditAlarm extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private MapView mapView;
    private GoogleMap gmap;
    private Button btnGuardar, btnCancelar;
    private EditText edtUbicacion, edtTitulo, edtDescripcion;
    private String ubicacion, titulo, descripcion;
    private int latitud, longitud;

    private List<Address> address;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        Bundle mapViewBundle = null;
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        edtUbicacion = findViewById(R.id.edtUbicacion);

        btnGuardar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

        Bundle b = getIntent().getBundleExtra("bundle");
        titulo = b.getString("Name");

        SQLUtilities conexion = new SQLUtilities(EditAlarm.this, "Alarm",null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        List<Element> list = new ArrayList<>();
        String[] args = new String[]{titulo};
        Cursor c = db.rawQuery("SELECT descrition, latitude, longitude FROM Alarm WHERE name = ?", args);
        if (c.moveToFirst()) {
            edtTitulo.setText(titulo);
            descripcion = c.getString(0);
            latitud = c.getInt(1);
            longitud = c.getInt(2);
            edtDescripcion.setText(descripcion);
        }

        db.close();

        gmap.setMinZoomPreference(18);
        LatLng ny = new LatLng(latitud, longitud);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(18);
        LatLng ny = new LatLng(18.807951, -99.220916);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnGuardar){
            ubicacion = edtUbicacion.getText().toString();
            if(ubicacion.equals("")){
                Toast.makeText(EditAlarm.this, "No hay dirección para buscar", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(EditAlarm.this, "Buscando", Toast.LENGTH_SHORT).show();
                Geocoder coder = new Geocoder(getApplicationContext());

                try {
                    address = coder.getFromLocationName(ubicacion, 1);
                    Address location = address.get(0);
                    int lat = (int) (location.getLatitude()*1E6);
                    int lon = (int) (location.getLongitude()*1E6);

                    //guardar
                    SQLUtilities conexion = new SQLUtilities(EditAlarm.this, "Alarm",null, 1);
                    SQLiteDatabase db = conexion.getWritableDatabase();
                    titulo = edtTitulo.getText().toString();
                    descripcion = edtDescripcion.getText().toString();

                    ContentValues nuevoRegistro = new ContentValues();
                    nuevoRegistro.put("name",titulo);
                    nuevoRegistro.put("descrition",descripcion);
                    nuevoRegistro.put("latitude", lat);
                    nuevoRegistro.put("longitude", lon);
                    nuevoRegistro.put("status", true);
                    db.insert("Alarm", null, nuevoRegistro);
                    db.close();

                    gmap.setMinZoomPreference(18);
                    LatLng ny = new LatLng(lat, lon);
                    gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
                } catch (IOException e) {
                    Toast.makeText(EditAlarm.this, "No se encontro la dirección", Toast.LENGTH_SHORT).show();
                }
                finish();
            }

        }else if (v.getId() == R.id.btnCancelar){
            finish();
        }
    }
}
