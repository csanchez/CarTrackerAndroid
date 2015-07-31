package com.adms.tracker.cartracker;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.location.LocationListener;
import android.util.Log;

import com.adms.tracker.services.location.LocationServiceBinder;
import com.adms.tracker.utils.Constants;

public class LocationService extends Service implements LocationListener {
    //TODO añadir soporte par dispositivos sin google play services
    private IBinder binder;

    private LocationManager locationManager;
    private String locProv;
    private Location location;


    public LocationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.TAG_GCM, "Service creating");
        this.locationManager =  (LocationManager)  getSystemService(LOCATION_SERVICE);
        locProv = this.locationManager.getBestProvider(this.stablishCriteria(), true);
        //this.locationManager.requestLocationUpdates(locProv,2000,0,this);
    }

    @Override
    public void onDestroy() {
        Log.i(Constants.TAG_GCM, "Service destroying");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
       // binder = new LocationServiceBinder(this);
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdate();
        return START_STICKY;
    }

    public Criteria stablishCriteria(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        return criteria;
    }

    public void startLocationUpdate(){
        location = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        this.printLocation(location);
    }

    @Override
    public void onLocationChanged(Location location) {

        this.printLocation(location);
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
