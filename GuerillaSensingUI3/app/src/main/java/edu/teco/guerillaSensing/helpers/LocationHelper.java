package edu.teco.guerillaSensing.helpers;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Singleton for managing user location.
 * TODO: Currently not in use.
 */
public class LocationHelper implements LocationListener, GpsStatus.Listener {

    // The instance of the singleton. Will be null until it is first used.
    private static LocationHelper mInstance = null;

    // The location manager, needed to request the user location.
    private LocationManager mLocationManager;

    // The current user location
    private Location mLocation;

    /**
     * Returns the location helper singleton object.
     * @return  The location helper singleton object.
     */
    public static LocationHelper getInstance(){
        // If the singleton object does not exist yet, create it.
        if(mInstance == null)
        {
            mInstance = new LocationHelper();
        }
        return mInstance;
    }

    /**
     * Returns the current position. also registers for location updates.
     * @param context The context.
     * @return Current (last known) location of device or null, if GPS is disabled.
     */
    public Location getLocation(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return mLocation;
    }

    /**
     * Stops the location updates.
     */
    public void stopLocationUpdates() {
        mLocationManager.removeUpdates(this);
    }

    /**
     * This is called when we get the first "fresh" location.
     * We can now disable location updates.
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        mLocationManager.removeUpdates(this);
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

    @Override
    public void onGpsStatusChanged(int i) {

    }
}
