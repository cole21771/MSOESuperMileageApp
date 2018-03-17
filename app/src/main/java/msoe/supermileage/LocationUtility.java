package msoe.supermileage;

import android.annotation.SuppressLint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Arrays;

/**
 * Handles getting location information from the Android device.
 *
 * @author braithwaitec
 */
public class LocationUtility {

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
                                location.getAltitude(),
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

    public void handleLocationInput(LocationInputHandler locationInputHandler) {
        this.locationInputHandler = locationInputHandler;
    }

    @SuppressLint("MissingPermission")
    public void startMonitoringLocation() {
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
