package com.example.ainterak;

import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class CompassActivity extends FragmentActivity implements
        OnMapReadyCallback,
        SensorEventListener {

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation, buskeLocation;
    private TextView textDistance;
    private ImageView imgArrow;
    private SensorManager sensorManager;
    private Sensor[] sensors;
    private GeomagneticField geoField;
    private float currentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        Buske buske = (Buske) getIntent().getParcelableExtra("com.example.ainterak.BUSKE");
        buskeLocation = new Location("");
        buskeLocation.setLongitude(buske.longitude);
        buskeLocation.setLatitude(buske.latitude);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        textDistance = (TextView) findViewById(R.id.textDistance);
        imgArrow = (ImageView) findViewById(R.id.imgArrow);
        getCurrentLocation();
        checkSensorExist();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float degree = 0;
        getCurrentLocation();
        // the difference between true north and magnetic north
        setGeoField();

        // Check if we are using the orientation sensor.
        if (sensors.length == 1) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                // your degree from the hardware compass. This is in degrees east of magnetic north
                degree = Math.round(sensorEvent.values[0]);
            }
        }

        //adjust the degree with the declination
        degree += geoField.getDeclination();

        // the bearing from your location to the destination location. This is in degrees east of true north.
        float bearing = currentLocation.bearingTo(buskeLocation);
        // offset the direction in which the phone is facing (degree) from the buske destination rather than true north
        degree = bearing - (bearing + degree);
        // convert from degrees east of true north (-180 to +180) into normal degrees (0 to 360)
        degree = normalizeDegree(degree);

        updateUi(degree);
    }

    private float normalizeDegree(float value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {
            return 180 + (180 + value);
        }
    }

    private void updateUi(float degree) {
        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        imgArrow.startAnimation(ra);

        // Set distance to buske
        float distance = currentLocation.distanceTo(buskeLocation);
        textDistance.setText(getString(R.string.buske_distance, distance));

        // Used for animation
        currentDegree = degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSensorExist();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    }


    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLocation = location;
            }
        });
    }

    private void setGeoField() {
        geoField = new GeomagneticField(
                Double.valueOf(currentLocation.getLatitude()).floatValue(),
                Double.valueOf(currentLocation.getLongitude()).floatValue(),
                Double.valueOf(currentLocation.getAltitude()).floatValue(),
                System.currentTimeMillis()
        );
    }

    private void checkSensorExist() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null) {
            sensors = new Sensor[1];
            sensors[0] = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            registerSensor(sensors[0]);
        }
    }

    private void registerSensor(Sensor sensor) {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void unregisterSensors() {
        for (Sensor sensor : sensors) {
            sensorManager.unregisterListener(this, sensor);
        }
    }
}
