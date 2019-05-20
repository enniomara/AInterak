package com.example.ainterak;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    private LocationProvider mLocationProvider;
    View mapView;
    private BuskeRepository buskeRepository;
    private HashMap<Marker, InfoWindow> infoWindowMap;
    private InfoWindowManager infoWindowManager;
    private final InfoWindow.MarkerSpecification OFFSET = new InfoWindow.MarkerSpecification(0,100);
    LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapInfoWindowFragment mapFragment = (MapInfoWindowFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        infoWindowManager = mapFragment.infoWindowManager();
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        buskeRepository = new BuskeRepository(getApplicationContext());
        MenuSlider menuSlider = new MenuSlider(this, buskeRepository);
        menuSlider.initSlider();

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
        buskeRepository.findAll().observe(this, new Observer<List<Buske>>() {
            @Override
            public void onChanged(@Nullable List<Buske> buskes) {
                infoWindowMap = new HashMap<>();
                mMap.clear();
                if (buskes != null) {
                    for (Buske buske: buskes) {
                        updateMarkers(buske);
                    }
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(MapsActivity.this);

        // There is a custom button that handles it
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        ((ImageView) findViewById(R.id.myLocation)).setOnClickListener((View view) -> {
            onMyLocationButtonClick();
        });
        addBuskarToMap();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        mLocationProvider.getLocation().addOnSuccessListener(this, (Location location) -> {
            if (location == null) return;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        });
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    public void openAddMarker(View view) {
        Intent intent = new Intent(this, AddMarkerActivity.class);
        startActivity(intent);
    }

    /**
     * Populates the map with saved Buskar markers. If none are found, the map is centered to
     * user's location.
     */
    private void addBuskarToMap() {
        buskeRepository.findAll().observe(this, (List<Buske> buskar) -> {
            if (buskar == null) {
                return;
            }
            infoWindowMap = new HashMap<>();
            // If there are no saved bushes, center map to current location of user
            if (buskar.isEmpty()) {
                mLocationProvider.getLocation().addOnSuccessListener(this, (Location location) -> {
                    if (location == null) return;
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLng(new LatLng(location.getLatitude(), location.getLongitude()))
                    );
                });
                return;
            }

            // Populate latLngBounds with the different buskar
//            LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
            for (Buske buske : buskar) {
                updateMarkers(buske);
            }

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (height * 0.2); // offset from edges of the map 10% of screen

            // Add the user's current location to latLngBounds. That way the user sees where its location is
            // relative to the other buskar.
            mLocationProvider.getLocation().addOnSuccessListener(this, (Location location) -> {
                if (location == null) return;
                latLngBuilder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngBounds(latLngBuilder.build(), width, height, padding)
                );
            });

            // Limit zoom to 16. If it's higher then it is hard to find where on the map the user is.
            if (mMap.getCameraPosition().zoom > 16) {
                mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        InfoWindow infoWindow = infoWindowMap.get(marker);

        if (infoWindow != null) {
            infoWindowManager.toggle(infoWindow, true);
        }

        return true;
    }

    public void openEditMarker(View view) {
        Intent intent = new Intent(this, AddMarkerActivity.class);
        startActivity(intent);
    }

    private void updateMarkers(Buske buske) {
        LatLng latLng = new LatLng(buske.latitude, buske.longitude);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(buske.name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.buska_marker_140))
        );
        InfoFragment infoFragment = InfoFragment.newInstance(buske);
        InfoWindow infowindow = new InfoWindow(marker,OFFSET,infoFragment);
        infoWindowMap.put(marker, infowindow);
        latLngBuilder.include(latLng);
    }
}
