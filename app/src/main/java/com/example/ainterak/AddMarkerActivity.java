package com.example.ainterak;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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


public class AddMarkerActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener {

    private LocationProvider mLocationProvider;
    private GoogleMap mMap;
    private BuskeRepository buskeRepository;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        Toolbar myToolbar = findViewById(R.id.toolbar_item);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.marker_map);
        mapFragment.getMapAsync(this);

        buskeRepository = new BuskeRepository(getApplicationContext());

        saveButton = findViewById(R.id.marker_save_button);
        TextInputLayout nameInputLayout = findViewById(R.id.parent_marker_name_field);
        // Add asterisk to name hint to mark it required
        nameInputLayout.setHint(nameInputLayout.getHint() + "*");

        // Create listeners that enabled/disables save button based on name field
        TextInputEditText nameEditText = findViewById(R.id.marker_name_field);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                nameEdited(s);
            }
        });
        try {
            mLocationProvider = new LocationProvider(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Action fired when the name field is edited. Enables/disabled the save button
     *
     * @param s
     */
    private void nameEdited(Editable s) {
        if (s.toString().isEmpty()) {
            saveButton.setEnabled(false);
        } else {
            saveButton.setEnabled(true);
        }
    }

    public void saveMarker(View view) {
        TextView name = findViewById(R.id.marker_name_field);
        EditText description = findViewById(R.id.description_box);

        Buske buske = new Buske();
        buske.name = name.getText().toString();
        buske.description = description.getText().toString();

        LatLng latLng = mMap.getCameraPosition().target;
        buske.latitude = latLng.latitude;
        buske.longitude = latLng.longitude;

        buskeRepository.create(buske);
        Toast.makeText(this, R.string.buske_created, Toast.LENGTH_SHORT).show();
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
        TextView name = findViewById(R.id.marker_name_field);
        EditText description = findViewById(R.id.description_box);

        // If name and description are not filled, no point to ask for confirmation. No data will
        // be lost
        if (name.getText().toString().isEmpty() && description.getText().toString().isEmpty()) {
            finish();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_buske__cancel_title)
                .setMessage(R.string.add_buske__cancel_description)
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
