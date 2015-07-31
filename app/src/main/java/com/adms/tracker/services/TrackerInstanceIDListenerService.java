package com.adms.tracker.services;

import android.content.Intent;
import android.util.Log;

import com.adms.tracker.utils.Constants;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

public class TrackerInstanceIDListenerService extends InstanceIDListenerService {




    public void onTokenRefresh() {
        Log.d(Constants.TRACKER, "En onTokenRefresh Callback");
        startService(new Intent(this, RegistrationIntentService.class));
        //refreshAllTokens();
    }

    private void refreshAllTokens() {

    }
}
