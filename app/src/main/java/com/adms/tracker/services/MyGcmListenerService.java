package com.adms.tracker.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.adms.tracker.utils.Constants;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    public MyGcmListenerService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i("GCM RECEIVER", "Recibiendo mensaje");
        String message = data.getString("message");
        sendBroadcast(new Intent().setAction(Constants.LOCATION_RECEIVER));

    }
}
