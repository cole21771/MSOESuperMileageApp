package msoe.supermileage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import java.util.Arrays;

class LocationUtility implements LocationListener {
    private static final String LOCATION_POST_ARGUMENT = "newLocation";

    private final double SPEED_MULTIPLIER = 2.237;

    private final WebUtility webUtility;
    private final App app;

    private LocationManager locationManager;
    private Criteria providerCriteria;

    public LocationUtility(LocationManager locationManager, App app) {
        assert locationManager != null;
        assert app != null;

        this.locationManager = locationManager;
        this.app = app;

        this.webUtility = app.getWebUtility();

        this.providerCriteria = new Criteria();
        this.providerCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        this.providerCriteria.setAltitudeRequired(false);
        this.providerCriteria.setBearingRequired(false);
        this.providerCriteria.setCostAllowed(true);
        this.providerCriteria.setPowerRequirement(Criteria.POWER_LOW);
    }


    @Override
    public void onLocationChanged(Location location) {
        this.webUtility.post(
                LOCATION_POST_ARGUMENT,
                Arrays.toString(
                        new Double[]{
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getAltitude(),
                                location.getSpeed() * SPEED_MULTIPLIER
                        }
                )
        );
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

    /**
     * Start monitoring location
     */
    public void startMonitoringLocation() {
        if (ActivityCompat.checkSelfPermission(app.getCurrentActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(app.getCurrentActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String provider = locationManager.getBestProvider(this.providerCriteria, true);
            if (provider == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            } else {
                locationManager.requestLocationUpdates(provider, 0, 0, this);
            }
        }
    }

    /**
     * Stop monitoring the location
     */
    public void stopMonitoringLocation() {
        locationManager.removeUpdates(this);
    }
}
