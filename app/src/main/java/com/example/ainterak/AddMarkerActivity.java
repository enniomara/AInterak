package com.example.ainterak;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

public class AddMarkerActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener{

    private LocationProvider mLocationProvider;
    private View mapView;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location markerLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.marker_map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        try {
            mLocationProvider = new LocationProvider(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLocationProvider.getLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) {
                    Log.d("locationD", "Location was null");
                    return;
                }
                Log.d("locationD", location.getLongitude() + " latitude " + location.getLatitude());
            }
        });


    }

    public void saveMarker() {
        TextView name = findViewById(R.id.marker_name_field);
        EditText description = findViewById(R.id.description_box);
        name.getText();
        description.getText();
        // Hämta information från databasen
        Buske buske = new Buske();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                getCurrentLocation();
            }
        }, 500);
    }

    private void fixateCamera(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        CameraPosition cp = new CameraPosition.Builder()
                .target(latLng).zoom(17).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
    }

    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    fixateCamera(new LatLng(location.getLatitude(),location.getLongitude()));
                    markerLocation = location;
                }
            }
        });
    }
}
