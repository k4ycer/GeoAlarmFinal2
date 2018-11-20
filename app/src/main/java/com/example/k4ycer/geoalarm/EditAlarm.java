package com.example.k4ycer.geoalarm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.k4ycer.geoalarm.data.SQLUtilities;
import com.example.k4ycer.geoalarm.model.Element;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditAlarm extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private MapView mapView;
    private GoogleMap gmap;
    private Button btnGuardar, btnCancelar;
    private EditText edtTitulo, edtDescripcion;
    private String ubicacion, titulo, descripcion;
    private int latitud, longitud;
    private LatLng currentLatLng;
    PlaceAutocompleteFragment autocompleteFragment;

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
        }

        db.close();

        edtDescripcion.setText(descripcion);

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                placeOptionSelected(place);
            }

            @Override
            public void onError(Status status) {
                Log.e("k4ycer15", "An error occurred: " + status);
            }
        });
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
        LatLng marcador = new LatLng(latitud, longitud);
        gmap.addMarker(new MarkerOptions().position(marcador));
        gmap.moveCamera(CameraUpdateFactory.newLatLng(marcador));
        gmap.setMinZoomPreference(15);
        //setMapPosition(gmap, 18, marcador);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnGuardar){
            createAlarm();
        }else if (v.getId() == R.id.btnCancelar){
            finish();
        }
    }

    private void createAlarm(){
        if(currentLatLng == null){
            Toast.makeText(EditAlarm.this, "Por favor ingresa la ubicacion", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            //guardar
            SQLUtilities conexion = new SQLUtilities(EditAlarm.this, "Alarm",null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();
            titulo = edtTitulo.getText().toString();
            descripcion = edtDescripcion.getText().toString();

            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("name",titulo);
            nuevoRegistro.put("descrition",descripcion);
            nuevoRegistro.put("latitude", currentLatLng.latitude);
            nuevoRegistro.put("longitude", currentLatLng.longitude);
            nuevoRegistro.put("status", true);
            db.insert("Alarm", null, nuevoRegistro);
            db.close();

            // Alarm created successfully
            Toast.makeText(EditAlarm.this, "Alarma creada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(EditAlarm.this, "Lo sentimos, surgió un error", Toast.LENGTH_SHORT).show();
        }
    }

    private void placeOptionSelected(Place place){
        currentLatLng = place.getLatLng();
        addMarker(gmap, place.getLatLng(), place.getName().toString());
        setMapPosition(gmap, 18, place.getLatLng());
    }

    private void setMapPosition(GoogleMap gmap, Integer zoom, LatLng latLng){
        gmap.setMinZoomPreference(zoom);
        LatLng ny = latLng;
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }

    private void addMarker(GoogleMap gmap, LatLng latLng, String title){
        gmap.addMarker(new MarkerOptions().position(latLng).title(title));
    }
}
