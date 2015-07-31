package com.adms.tracker.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.adms.tracker.utils.Constants;

/**
 * Created by adms on 23/11/14.
 */
public class GpsTracker implements LocationListener {
    private Context mContext;

    private LocationManager locationManager;
    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude

    public GpsTracker(Context context) {
        this.mContext = context;
        this.locationManager = (LocationManager) this.mContext.getSystemService(this.mContext.LOCATION_SERVICE);

    }


    //TODO Aqui es donde se registrara un cambio de posición cuando se activa la alarma.
    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        return this.latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        return this.longitude;
    }

    //Usamos unicamente GPS, ya veremos si lo cambiamos
    public Location getLocation() {
        try {
            Log.i(Constants.TAG_GCM, "IN getLocation");
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.NO_REQUIREMENT);
            criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
            String locProv = this.locationManager.getBestProvider(criteria, true);
           // this.locationManager.requestLocationUpdates(locProv,Constants.MIN_TIME_BW_UPDATES_GPS,Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES_GPS,this);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            this.printLocation(location);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }



    private void printLocation(Location location){
        if (location != null) {
            Log.i(Constants.TAG_GCM, "Latitude is {0}" + location.getLatitude());
            Log.i(Constants.TAG_GCM, "Longitude is {0}" + location.getLongitude());
            Log.i(Constants.TAG_GCM, "Altitud is {0}" + location.getAltitude());
            Log.i(Constants.TAG_GCM, "Speed is {0}" + location.getSpeed());
            Log.i(Constants.TAG_GCM, "Accuracy is {0}" + location.getAccuracy());
            Log.i(Constants.TAG_GCM, "Bearing is {0}" + location.getBearing());
        }else{
            Log.i(Constants.TAG_GCM, "NULL LOCATON");
        }
    }

}
