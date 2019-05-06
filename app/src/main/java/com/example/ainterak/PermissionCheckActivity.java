package com.example.ainterak;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class PermissionCheckActivity extends AppCompatActivity {

    // Needed permissions list
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int REQUEST_CODE = 1240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_check_activity);
        if(checkPermissions()){
            startMapsActivity();
        }
    }

    /***
     * Starts next maps activity
     */
    private void startMapsActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void checkLocationPermission(View view){
        if(checkPermissions()){
            Toast.makeText(view.getContext(), R.string.all_permission_granted,
                    Toast.LENGTH_LONG).show();
            startMapsActivity();
        } else{
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (checkPermissions()) {
                        Toast.makeText(
                                PermissionCheckActivity.this,
                                R.string.permission_granted,
                                Toast.LENGTH_LONG)
                                .show();
                        startMapsActivity();
                    }
                    else {
                        Toast.makeText(
                                PermissionCheckActivity.this,
                                R.string.permission_denied,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
                break;
        }
    }

    /***
     * Requests the required permissions.
     */
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                PermissionCheckActivity.this,
                PERMISSIONS,
                REQUEST_CODE
        );
    }

    /***
     * Checks if we have the permissions we need.
     * @return True if we have, false otherwise.
     */
    private boolean checkPermissions(){
        int permissionFineLocation = ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        );
        int permissionCoarseLocation = ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
        );
        return permissionFineLocation == PackageManager.PERMISSION_GRANTED &&
                permissionCoarseLocation == PackageManager.PERMISSION_GRANTED;
    }
}
