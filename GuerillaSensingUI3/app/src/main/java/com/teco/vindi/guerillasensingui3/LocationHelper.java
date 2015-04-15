package com.teco.vindi.guerillasensingui3;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Singleton for managing user location.
 */
public class LocationHelper implements LocationListener, GpsStatus.Listener {
    private static LocationHelper mInstance = null;

    private String mString;

    private LocationManager mLocationManager;
    private Location location;

    private LocationHelper(){
       // Called when singleton is first created.
    }

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

    public Location getLocation(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.d("location_old", location.getLatitude() + " " + location.getLongitude());

        return location;

    }

    public void stopLocationUpdates() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("location", location.getLatitude() + " " + location.getLongitude());
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
