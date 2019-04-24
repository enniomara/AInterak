package com.example.ainterak;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

class LocationProvider {
    private FusedLocationProviderClient fusedLocationProviderClient;
    public LocationProvider(FusedLocationProviderClient fusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
    }

    public LocationProvider(Activity activity) {
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    @SuppressLint("MissingPermission")
    public Task<Location> getLocation() {
        return fusedLocationProviderClient.getLastLocation();
    }
}

