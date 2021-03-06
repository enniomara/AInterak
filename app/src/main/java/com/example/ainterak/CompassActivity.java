package com.example.ainterak;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.os.Vibrator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class CompassActivity extends AppCompatActivity implements
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
    private boolean isVibrating;
    private Vibrator vibrator;
    private float distance;
    private long[] pattern;

    // For the flashlight
    private ShakeDetector mShakeDetector;
    private boolean isFlashLightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        Toolbar myToolbar = findViewById(R.id.toolbar_item);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Buske buske = (Buske) getIntent().getParcelableExtra("com.example.ainterak.BUSKE");
        buskeLocation = new Location("");
        buskeLocation.setLongitude(buske.longitude);
        buskeLocation.setLatitude(buske.latitude);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        textDistance = (TextView) findViewById(R.id.textDistance);
        imgArrow = (ImageView) findViewById(R.id.imgArrow);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        isVibrating = false;
        pattern = new long[] {0,300,1000};
        getCurrentLocation();
        checkSensorExist();
        startShakeDetector();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        getCurrentLocation();
        if (currentLocation == null) {
            return;
        }
        // the difference between true north and magnetic north
        setGeoField();
        float degree = 0;
        // Check if we are using the orientation sensor.
        if (sensors.length == 1) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                // your degree from the hardware compass. This is in degrees east of magnetic north
                degree = Math.round(sensorEvent.values[0]);
            }
        }

        //adjust the degree with the declination
        degree -= geoField.getDeclination();

        // the bearing from your location to the destination location. This is in degrees east of true north.
        float bearing = currentLocation.bearingTo(buskeLocation);
        // offset the direction in which the phone is facing (degree) from the buske destination rather than true north
        if (bearing < 0) {
            bearing += 360;
        }

        degree = bearing - degree;

        // convert from degrees east of true north (-180 to +180) into normal degrees (0 to 360)
        degree = normalizeDegree(degree);

        updateUi(degree);
        
        if(!isVibrating){
            if(distance <= 10){
                vibrator.vibrate(pattern, 1);
                isVibrating = true;
            }
        } else {
            if(distance > 10) {
                vibrator.cancel();
                isVibrating = false;
            }
        }
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
        distance = currentLocation.distanceTo(buskeLocation);
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
        vibrator.cancel();
        isVibrating = false;
        unregisterSensors();
        mShakeDetector.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSensorExist();
        mShakeDetector.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    /**
     * Starts the shake detector and at 2 shakes the flashlight turns on.
     */
    private void startShakeDetector() {
        mShakeDetector = new ShakeDetector(this);
        mShakeDetector.setOnShakeListener(count -> {
            if (count >= 2) {
                try {
                    toggleTorch();
                } catch (CameraAccessException e) {
                    Log.d("toggleD", "Torch not available");
                }
            }
        });
    }

    /**
     * Toggles the torch.
     *
     * @throws CameraAccessException
     */
    private void toggleTorch() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId = cameraManager.getCameraIdList()[0];
        if (isFlashLightOn) {
            cameraManager.setTorchMode(cameraId, false);
            isFlashLightOn = false;
        } else {
            cameraManager.setTorchMode(cameraId, true);
            isFlashLightOn = true;
        }
        vibrator.vibrate(75);
    }
}
