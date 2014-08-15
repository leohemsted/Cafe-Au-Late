package com.aam.latecoffee;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Leo on 15/08/2014.
 */
public class LocationHandler extends Fragment {

    String lastLoc = null;
    long lastTimestamp = 0;
    Context context = null;

    public LocationHandler(Context context){
        this.context = context;
        /* Use the LocationManager class to obtain GPS locations */
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // inital set
        Location loc = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lastLoc = loc.getLatitude() + "," + loc.getLongitude();
        lastTimestamp = loc.getTime();

        Log.v("late", "LOCATION = " + lastLoc);

        LocationListener mlocListener = new MyLocationListener();
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("late", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        super.onCreate(savedInstanceState);
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public String getLastLoc() {
        return lastLoc;
    }

    public void setLastLoc(String lastLoc) {
        this.lastLoc = lastLoc;
    }

    /* Class My Location Listener */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Log.v("late", "LOCATION CHANGED!");
            lastLoc = loc.getLatitude() + "," + loc.getLongitude();
            lastTimestamp = loc.getTime();
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
    }
}
