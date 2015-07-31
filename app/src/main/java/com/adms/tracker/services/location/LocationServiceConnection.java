package com.adms.tracker.services.location;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;


/**
 * Created by adms on 22/11/14.
 */
public class LocationServiceConnection implements ServiceConnection {

    private LocationServiceBinder binder;
    public LocationServiceConnection (LocationServiceBinder binder){
        if (binder != null) {
            this.binder = binder;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        LocationServiceBinder serviceBinder = (LocationServiceBinder) service;

        if (serviceBinder != null) {
            this.binder = serviceBinder;
            this.binder.setIsBound(true);
            // raise the service bound event
           /*
            this.ServiceConnected(this, new ServiceConnectedEventArgs () {

                Binder= service ;
            });

            */
            // begin updating the location in the Service
            this.binder.getService().startLocationUpdates();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        this.binder.setIsBound(false);

    }
}
