package com.example.k4ycer.geoalarm;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.k4ycer.geoalarm.data.SQLUtilities;
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
import java.util.List;

public class AddAlarm extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private MapView mapView;
    private GoogleMap gmap;
    private Button btnGuardar, btnCancelar;
    private EditText edtUbicacion, edtTitulo, edtDescripcion;
    private String ubicacion, titulo, descripcion;
    private LatLng currentLatLng;

    PlaceAutocompleteFragment autocompleteFragment;

    private List<Address> address;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

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
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        btnGuardar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

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
        setMapPosition(gmap, 18, new LatLng(18.807951, -99.220916));
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
            Toast.makeText(AddAlarm.this, "Por favor ingresa la ubicacion", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            //guardar
            SQLUtilities conexion = new SQLUtilities(AddAlarm.this, "Alarm",null, 1);
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
            Toast.makeText(AddAlarm.this, "Alarma creada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(AddAlarm.this, "Lo sentimos, surgió un error", Toast.LENGTH_SHORT).show();
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
