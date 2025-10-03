
package com.eduzeb.connect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {
    
    private Context context;
    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private LocationCallback callback;
    
    public interface LocationCallback {
        void onLocationDetected(String district, double latitude, double longitude);
        void onLocationError(String error);
    }
    
    public LocationHelper(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    
    public void getCurrentLocation(LocationCallback callback) {
        this.callback = callback;
        
        if (!checkLocationPermission()) {
            requestLocationPermission();
            return;
        }
        
        if (!isLocationEnabled()) {
            callback.onLocationError("Location services are disabled. Please enable GPS.");
            return;
        }
        
        fetchLocation();
    }
    
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }
    
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    
    private void fetchLocation() {
        try {
            // Try GPS first
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(context, 
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1000, 
                            10, 
                            locationListener
                    );
                    
                    // Get last known location as fallback
                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLocation != null) {
                        processLocation(lastLocation);
                    }
                }
            } 
            // Fallback to Network
            else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(context, 
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            10,
                            locationListener
                    );
                    
                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (lastLocation != null) {
                        processLocation(lastLocation);
                    }
                }
            }
        } catch (Exception e) {
            callback.onLocationError("Error fetching location: " + e.getMessage());
        }
    }
    
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            processLocation(location);
            // Stop updates after getting location
            locationManager.removeUpdates(this);
        }
        
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        
        @Override
        public void onProviderEnabled(String provider) {}
        
        @Override
        public void onProviderDisabled(String provider) {}
    };
    
    private void processLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            
            String district = getDistrictFromCoordinates(latitude, longitude);
            
            if (callback != null) {
                callback.onLocationDetected(district, latitude, longitude);
            }
        }
    }
    
    private String getDistrictFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                
                // Get locality (city/town)
                String locality = address.getLocality();
                String subAdminArea = address.getSubAdminArea(); // District
                String adminArea = address.getAdminArea(); // Province
                
                // Map to South African districts
                if (subAdminArea != null) {
                    return mapToSADistrict(subAdminArea, locality, adminArea);
                } else if (locality != null) {
                    return mapToSADistrict(locality, locality, adminArea);
                } else {
                    return adminArea != null ? adminArea : "Unknown District";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "Unknown District";
    }
    
    private String mapToSADistrict(String subAdminArea, String locality, String province) {
        // Map location to South African metropolitan/district municipalities
        String location = (subAdminArea + " " + locality + " " + province).toLowerCase();
        
        if (location.contains("pretoria") || location.contains("tshwane") || 
            location.contains("centurion")) {
            return "Tshwane Metropolitan";
        } else if (location.contains("johannesburg") || location.contains("joburg") || 
                   location.contains("sandton") || location.contains("soweto")) {
            return "Johannesburg Metropolitan";
        } else if (location.contains("ekurhuleni") || location.contains("benoni") || 
                   location.contains("boksburg") || location.contains("kempton park")) {
            return "Ekurhuleni Metropolitan";
        } else if (location.contains("cape town") || location.contains("kaapstad")) {
            return "Cape Town Metropolitan";
        } else if (location.contains("durban") || location.contains("ethekwini")) {
            return "eThekwini Metropolitan";
        } else if (location.contains("port elizabeth") || location.contains("gqeberha") || 
                   location.contains("nelson mandela")) {
            return "Nelson Mandela Bay";
        } else if (location.contains("bloemfontein") || location.contains("mangaung")) {
            return "Mangaung Metropolitan";
        } else if (location.contains("east london") || location.contains("buffalo city")) {
            return "Buffalo City Metropolitan";
        } else {
            // Return province-based district
            return subAdminArea != null ? subAdminArea : "Unknown District";
        }
    }
    
    public void stopLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
