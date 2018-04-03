package msoe.supermileage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

/**
 * Handles getting location information from the Android device.
 *
 * @author braithwaitec
 */
public class LocationUtility {

    public static final int INTERVAL = 10000;
    public static final int FASTEST_INTERVAL = 5000;
    private final double MPS_TO_MPH = 2.237;

    private final App app;

    private final LocationManager locationManager;
    private final Criteria providerCriteria;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (locationInputHandler != null) {
                String locationText = Arrays.toString(
                        new Double[]{
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getSpeed() * MPS_TO_MPH
                        }
                );
                locationInputHandler.onInputReceived(locationText);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private LocationInputHandler locationInputHandler;

    public interface LocationInputHandler {
        void onInputReceived(String text);
    }

    public LocationUtility(App app, LocationManager locationManager) {
        assert app != null;
        assert locationManager != null;

        this.app = app;
        this.locationManager = locationManager;

        this.providerCriteria = new Criteria();
        this.providerCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        this.providerCriteria.setAltitudeRequired(false);
        this.providerCriteria.setBearingRequired(false);
        this.providerCriteria.setCostAllowed(true);
        this.providerCriteria.setPowerRequirement(Criteria.POWER_LOW);
    }

    private void modifyLocationAccuracySettings() {
        // prepare the request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // get location settings of the device
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this.app);

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build())
                .addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        System.out.println(task);
                    }
                })
                .addOnSuccessListener((Activity) this.app.getCurrentActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //TODO All location settings are satisfied. The settingsClient can initialize location requests here.
                        System.out.println(locationSettingsResponse);
                    }
                })
                .addOnFailureListener((Activity) this.app.getCurrentActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                        System.out.println(e);

                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case CommonStatusCodes.RESOLUTION_REQUIRED: {
                                // TODO Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                                break;
                            }
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: {
                                // TODO Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                                break;
                            }
                        }

//                        if (e instanceof ResolvableApiException) {
//                            // Location settings are not satisfied, but this can be fixed
//                            // by showing the user a dialog.
//                            try {
//                                // Show the dialog by calling startResolutionForResult(),
//                                // and check the result in onActivityResult().
//                                ResolvableApiException resolvable = (ResolvableApiException) e;
//                                resolvable.startResolutionForResult(MainActivity.this,
//                                        REQUEST_CHECK_SETTINGS);
//                            } catch (IntentSender.SendIntentException sendEx) {
//                                // Ignore the error.
//                            }
//                        }
                    }
                });
    }

    public void handleLocationInput(LocationInputHandler locationInputHandler) {
        this.locationInputHandler = locationInputHandler;
    }

    @SuppressLint("MissingPermission")
    public void startMonitoringLocation() {
        modifyLocationAccuracySettings();

        String provider = locationManager.getBestProvider(this.providerCriteria, true);
        if (provider == null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.locationListener);
        } else {
            locationManager.requestLocationUpdates(provider, 0, 0, this.locationListener);
        }
    }

    public void stopMonitoringLocation() {
        locationManager.removeUpdates(this.locationListener);
    }
}
