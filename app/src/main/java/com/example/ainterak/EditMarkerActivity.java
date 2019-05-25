package com.example.ainterak;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;


public class EditMarkerActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener {

    private LocationProvider mLocationProvider;
    private GoogleMap mMap;
    private BuskeRepository buskeRepository;
    private Buske buske;
    private TextView name;
    private EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_marker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.marker_map);
        mapFragment.getMapAsync(this);

        buskeRepository = new BuskeRepository(getApplicationContext());
        buske = (Buske) getIntent().getParcelableExtra("com.example.ainterak.BUSKE");
        name = (TextView) findViewById(R.id.edit_marker_name_field);
        description = (EditText) findViewById(R.id.edit_description_box);
        name.setText(buske.name);
        description.setText(buske.description);

        try {
            mLocationProvider = new LocationProvider(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editMarker(View view) {
        buske.name = name.getText().toString();
        buske.description = description.getText().toString();

        LatLng latLng = mMap.getCameraPosition().target;
        buske.latitude = latLng.latitude;
        buske.longitude = latLng.longitude;

        buskeRepository.update(buske);
        Toast.makeText(this, "Buske edited", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void cancelBtnClick(View view) {
        cancelConfirmation();
    }

    @Override
    public void onBackPressed() {
        cancelConfirmation();
    }

    /**
     * Handle confirmation of discarding the buske.
     */
    private void cancelConfirmation() {
        TextView name = findViewById(R.id.edit_marker_name_field);
        EditText description = findViewById(R.id.edit_description_box);

        // If name and description are not filled, no point to ask for confirmation. No data will
        // be lost
        if (name.getText().toString().isEmpty() && description.getText().toString().isEmpty()) {
            finish();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_buske_cancel_title)
                .setMessage(R.string.edit_buske_cancel_description)
                .setPositiveButton(R.string.yes, (DialogInterface dialog, int which) -> finish())
                .setNegativeButton(R.string.no, (DialogInterface dialog, int which) -> {
                });

        builder.show();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                getCurrentLocation();
            }
        }, 500);
    }

    private void fixateCamera(LatLng latLng) {
        CameraPosition cp = new CameraPosition.Builder()
                .target(latLng).zoom(18).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
    }

    private void getCurrentLocation() {
        mLocationProvider.getLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    fixateCamera(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        });
    }
}
