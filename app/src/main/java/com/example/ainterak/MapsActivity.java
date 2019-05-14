package com.example.ainterak;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private LocationProvider mLocationProvider;
    View mapView;
    private BuskeRepository buskeRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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

        addBuskarToMap();

        // Move go-to-my-location to bottom-right-corner
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
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
            LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
            for (Buske buske : buskar) {
                LatLng latLng = new LatLng(buske.latitude, buske.longitude);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(buske.name)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.buska_marker_tiny))
                );
                latLngBuilder.include(latLng);
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
}
