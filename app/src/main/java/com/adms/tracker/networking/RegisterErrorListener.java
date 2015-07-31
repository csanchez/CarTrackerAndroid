package com.adms.tracker.networking;

import android.util.Log;

import com.adms.tracker.utils.Constants;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by adms on 17/11/14.
 */
public class RegisterErrorListener implements  Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        System.out.println("ERROR: " + error.toString());
        System.out.println("ERROR: "+ error.networkResponse.statusCode);
        System.out.println("ERROR: "+ error.networkResponse.headers.toString());

        Log.i(Constants.TAG_GCM, "ERROR al enviar datos");


        error.printStackTrace();

        NetworkResponse response = error.networkResponse;
        String json = new String(response.data);
        System.out.println("ERROR: "+json);
    }
}