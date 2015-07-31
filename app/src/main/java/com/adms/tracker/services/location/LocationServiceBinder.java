package com.adms.tracker.services.location;

import android.os.Binder;



/**
 * Created by adms on 22/11/14.
 */
public class LocationServiceBinder extends Binder {

    private LocationService service;
    private boolean isBound;

    public LocationServiceBinder (LocationService service) {

        this.service = service;
    }

    public void setIsBound(boolean isBound){

        this.isBound = isBound;
    }

    public boolean getIsBound(){

        return this.isBound;
    }

    public LocationService getService() {

        return this.service;
    }

}
