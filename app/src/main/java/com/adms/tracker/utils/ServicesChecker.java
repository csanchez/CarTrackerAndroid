package com.adms.tracker.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.adms.tracker.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by admin on 17/07/15.
 */
public class ServicesChecker {

    /**
     * Verifica si hay conexión a internet
     * @return
     */
    public  static boolean checkMobileNetwork (Context context,String error) {
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "checando red Mobil");
        ConnectivityManager connectivityMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        // Check if wifi or mobile network is available or not. If any of them is
        //    NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // available or connected then it will return true, otherwise false;
        /*if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }
        }*/
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "checando red Mobil -- "+ mobile.isConnected() + "  "+mobile.getDetailedState());
        if (mobile != null) {
            if (mobile.isConnected()) {
                return true;
            }else{
                Toast.makeText(context,error , Toast.LENGTH_SHORT).show();
                return false;

            }
        }

        Toast.makeText(context,"El dispositivo no tiene soporte para conectarse a Internet" , Toast.LENGTH_SHORT).show();
        return false;
    }


    /**
     * Verifica si hay conexión a internet por wifi
     * @return
     */
    public  static boolean checkWifiNetwork (Context context,String error) {
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "checando red WIFI");
        ConnectivityManager connectivityMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // available or connected then it will return true, otherwise false;
        if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }else{
                Toast.makeText(context,error , Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        Toast.makeText(context,"El dispositivo no tiene soporte para conectarse a Internet" , Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * Verifica si hay conexión a internet por wifi
     * @return
     */
    public  static boolean checkAnyNetwork (Context context,String error) {
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "checando red Mobil y wifi");
        ConnectivityManager connectivityMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobile != null || wifi != null) {
            if (mobile.isConnected() || wifi.isConnected()) {
                return true;
            } else {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                return false;

            }
        }
        Toast.makeText(context, "El dispositivo no tiene soporte para conectarse a Internet", Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean checkGPS(Context context,String error){
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "checando GPS");
        LocationManager locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE );
        if ( locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            return true;
        Toast.makeText(context,error , Toast.LENGTH_LONG).show();
        return false;

        //else
        //    showServiceMessageAlert(Settings.ACTION_LOCATION_SOURCE_SETTINGS,getString(R.string.system_requirements_no_gps_message),getString(R.string.system_requirements_gps_cancel_message));

    }


    public static boolean checkPlayServices(Context context) {
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "checando play");
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (status != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
        //return false;
        /*
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
                GoogleApiAvailability.getInstance().getErrorDialog(this, status, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            } else {
                Toast.makeText(this, getString(R.string.system_requirements_google_play_error) , Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
        */
    }
}
