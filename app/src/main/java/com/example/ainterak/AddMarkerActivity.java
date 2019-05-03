package com.example.ainterak;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class AddMarkerActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener{

    private LocationProvider mLocationProvider;
    private View mapView;
    private GoogleMap mMap;
    private Location currentLocation;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.marker_map);
        mapView = mapFragment.getView();

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

    public void saveMarker(View view) {

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation = mLocationProvider.getLocation().getResult();
        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        Location location = mLocationManager.getLastKnownLocation(LocationManager.KEY_LOCATION_CHANGED);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        CameraPosition cp = new CameraPosition.Builder()
                .target(latLng).zoom(17).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
    }
}
