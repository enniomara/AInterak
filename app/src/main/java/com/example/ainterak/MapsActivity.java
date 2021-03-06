package com.example.ainterak;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.example.ainterak.MenuSlider.MenuSlider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LocationProvider mLocationProvider;
    View mapView;
    private BuskeRepository buskeRepository;
    private Map<Marker, InfoWindow> infoWindowMap;
    private Map<Buske, Marker> buskeMarkerMap;
    private InfoWindowManager infoWindowManager;
    private final InfoWindow.MarkerSpecification OFFSET = new InfoWindow.MarkerSpecification(0, 100);
    private MenuSlider menuSlider;
    private InfoWindow prevWindow = null;

    // For the flashlight
    private ShakeDetector mShakeDetector;
    private boolean isFlashLightOn = false;

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
        menuSlider = new MenuSlider(this, buskeRepository);
        initMenuSlider();

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

        startShakeDetector();
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

        // Collapse the menuSlider if a click on the map is registered
        infoWindowManager.setOnMapClickListener(v -> menuSlider.collapseSlider());

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
            mMap.clear();
            if (prevWindow != null) {
                infoWindowManager.toggle(prevWindow);
            }
            infoWindowMap = new HashMap<>();
            buskeMarkerMap = new HashMap<>();
            menuSlider.setNewDataset(buskar.toArray(new Buske[0]));

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
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(buske.name)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.buska_marker_140))
                );
                InfoFragment infoFragment = InfoFragment.newInstance(buske);
                InfoWindow infowindow = new InfoWindow(marker, OFFSET, infoFragment);
                buskeMarkerMap.put(buske, marker);
                infoWindowMap.put(marker, infowindow);
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

                // If panel is set to it's anchor point, show markers above it
                if (menuSlider.getSlidingPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
                    int bottomPadding = (int) (menuSlider.getSlidingPanelLayout().getAnchorPoint() * height);
                    mMap.setPadding(0, 0, 0, bottomPadding);
                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngBounds(latLngBuilder.build(), width, height, padding)
                    );
                    mMap.setPadding(0, 0, 0, 0);
                } else {
                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngBounds(latLngBuilder.build(), width, height, padding)
                    );
                }

                // Limit zoom to 16. If it's higher then it is hard to find where on the map the user is.
                if (mMap.getCameraPosition().zoom > 16) {
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                }
            });

        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        InfoWindow infoWindow = infoWindowMap.get(marker);
        toggleInfoWindow(marker, infoWindow);
        return true;
    }

    /**
     * Open a marker's infowindow
     *
     * @param marker
     * @param infoWindow
     */
    private void toggleInfoWindow(Marker marker, InfoWindow infoWindow) {
        prevWindow = infoWindow;
        mMap.animateCamera(CameraUpdateFactory
                .newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom), 200, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                if (infoWindow != null) {
                    infoWindowManager.toggle(infoWindow, true);
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    public void openEditMarker(View view) {
        Intent intent = new Intent(this, AddMarkerActivity.class);
        startActivity(intent);
    }

    /**
     * Initialize the menu slider and make changes to the activity so that it fits the menu slider
     */
    private void initMenuSlider() {
        menuSlider.initSlider();

        // Make floating action buttons appear over menu slider
        RelativeLayout relativeLayout = findViewById(R.id.mapContainer);
        LinearLayout layout = findViewById(R.id.buttonContainer);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        // RelativeLayout's dimensions are not set before render. Defer until render (and dimensions are set)
        // to modify margin
        relativeLayout.post(() -> {
            params.setMargins(
                    params.leftMargin,
                    params.topMargin,
                    params.rightMargin,
                    (int) (relativeLayout.getHeight() * MenuSlider.anchorPoint)
            );
            layout.setLayoutParams(params);
        });

        menuSlider.registerPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                // Restrict the buttons to stay under the anchor-point
                if (slideOffset > MenuSlider.anchorPoint) {
                    return;
                }

                LinearLayout layout = findViewById(R.id.buttonContainer);
                RelativeLayout relativeLayout = findViewById(R.id.mapContainer);
                // Defer execution to the
                Animation a = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                        params.setMargins(
                                params.leftMargin,
                                params.topMargin,
                                params.rightMargin,
                                (int) (relativeLayout.getHeight() * slideOffset)
                        );
                        layout.setLayoutParams(params);
                    }
                };
                a.setDuration(1);
                layout.startAnimation(a);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });

        // When a buske is clicked in the sliding window, open it in the map
        menuSlider.registerSlidingPanelItemClickListener((Buske buske) -> {
            if (buske == null) return;

            toggleInfoWindowFromBuske(buske);

            // Set panel state to anchored if it is expanded. Otherwise the infowindow is not shown.
            if (menuSlider.getSlidingPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                menuSlider.getSlidingPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        });
    }

    /**
     * Toggle an infowindow from a given buske
     *
     * @param buske
     */
    public void toggleInfoWindowFromBuske(Buske buske) {
        Marker marker = buskeMarkerMap.get(buske);
        InfoWindow infoWindow = infoWindowMap.get(marker);
        toggleInfoWindow(marker, infoWindow);
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
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(75);
    }

    @Override
    public void onResume() {
        super.onResume();
        mShakeDetector.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mShakeDetector.onPause();
    }
}
