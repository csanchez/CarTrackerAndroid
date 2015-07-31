package com.adms.tracker.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.adms.tracker.Activities.util.SystemUiHider;
import com.adms.tracker.R;
import com.adms.tracker.services.location.LocationServiceConnection;
import com.adms.tracker.networking.Connection;
import com.adms.tracker.services.RegistrationIntentService;
import com.adms.tracker.utils.Constants;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class TrackerActivityOLD extends Activity {

    protected Button connectCGMButton;
    protected Toast toast ;




    protected GoogleCloudMessaging gcm;
    protected Context context;
    //protected AtomicInteger msgId;


    private int value = 1;
    private Timer timer;
    private Handler handler;
    //private GpsTracker gps;
    private String regId;
    private boolean isGpsConnected = false;
    private boolean isNetworkConnected = false;
    private boolean isGCMResgister = false;
    private LocationServiceConnection localServiceConn;

    //cosas finales
    private RegistrationBroadcastReceiver registrationBroadcastReceiver;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(Constants.TRACKER, "Creando Tracker");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        context = this;


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        registrationBroadcastReceiver= new RegistrationBroadcastReceiver();
        registerGoogleCloudMessaging();



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.w(Constants.TRACKER, "Resumiendo Tracker");
        registerReceiver(this.registrationBroadcastReceiver, new IntentFilter(Constants.REGISTRATION_RECEIVER));
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.w(Constants.TRACKER, "Empezando Tracker");

    }

    @Override
    protected void onPause() {
        Log.w(Constants.TRACKER, "Pausando Tracker");
        super.onPause();
        unregisterReceiver(this.registrationBroadcastReceiver);

    }

    @Override
    protected void onDestroy() {
        Log.w(Constants.TRACKER, "Destruyendo Tracker");
        super.onDestroy();
        /*try{
            unbindService(this.localServiceConn);
        } catch (Throwable t) {
            // catch any issues, typical for destroy routines
            // even if we failed to destroy something, we need to continue destroying
            Log.w(Constants.TAG_GCM, "Failed to unbind from the service", t);
        }*/
    }

    private void registerGoogleCloudMessaging() {
        //Vamos a registrar con la Nube de Google y al servidor
        Log.w(Constants.TRACKER, "Va a registrar en la nube");
        startService(new Intent(this, RegistrationIntentService.class));
        Log.w(Constants.TRACKER, "Quedó registrado en la nube");




        /*gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(this.context);


        if (regId.isEmpty()) {

            registerInBackground();
            //Guardar en DB
            return false;
        } else {
            Log.i(Constants.TRACKER, "Ya estaba registrado");
            return true;
        }*/
        //Solo si se ha resgistrado en la nube y guardado en la app RAILS,
        // TODO falta mejorar esta aparte
        /*if (!regId.isEmpty()) {
            Log.i(Constants.TRACKER, "Start tracking GPS");
            this.startService(new Intent(this, LocationService.class));
            localServiceConn = new LocationServiceConnection(null);
            Intent locationServiceIntent = new Intent(this, LocationService.class);
            bindService(locationServiceIntent, this.localServiceConn, Context.BIND_AUTO_CREATE);
        }*/

    }




    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(Constants.TRACKER, "Registro no encontrado");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Constants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(Constants.TRACKER, "Cmabio el id de la version ");
            return "";
        }
        Log.i(Constants.TRACKER, "Registrado " + registrationId);
        return registrationId;

    }








    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(Constants.TRACKER, "Guardando regID y appVersion " + appVersion+"  ID= "+regId);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PROPERTY_REG_ID, regId);
        editor.putInt(Constants.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {

        new RegisterBackGround().execute(null, null, null);


    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     * @return Application's {@code SharedPreferences}.
     */
    private   SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(TrackerActivityOLD.class.getSimpleName(),Context.MODE_PRIVATE);
    }






    private class RegisterBackGround extends AsyncTask<Void,String,String> {




        @Override
        protected String doInBackground(Void... params) {


            /*Log.i(Constants.TRACKER, "Registrara en BACK");
            String msg = "";

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }

                regId = gcm.register(Constants.SENDER_ID);
                Log.i(Constants.TRACKER, "Dispositivo registrado en GCM registration ID=" + regId);
                msg = "Device registered, registration ID=" + regId;


                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.

                //sendRegistrationIdToBackend();

                // For this demo: we don't need to send it because the device
                // will send upstream messages to a server that echo back the
                // message using the 'from' address in the message.

                // Persist the regID - no need to register again.
                storeRegistrationId(context, regId);
            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            Log.i(Constants.TRACKER, "Resultado "+msg);
            return msg;*/
            return null;
        }

        @Override
        protected void onPostExecute(String msg) {
            //mDisplay.append(msg + "\n");
        }

        /**
         * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
         * or CCS to send messages to your app. Not needed for this demo since the
         * device sends upstream messages to a server that echoes back the message
         * using the 'from' address in the message.
         */
        private void sendRegistrationIdToBackend()  {
            //toast = Toast.makeText(TrackerActivity.this, "Se mandaran datos al servidor",Toast.LENGTH_LONG );
            //toast.show();
            Log.i(Constants.TAG_GCM, "Sending data to web site");
            Connection connection = Connection.getInstance(context);
            //StringRequest stringRequest = new StringRequest(
            //Request.Method.POST, "https://lit-plains-2489.herokuapp.com/tracker/register", new RegisterListener(context,""), new RegisterErrorListener());

            StringRequest stringRequest = new StringRequest(Request.Method.POST,"https://lit-plains-2489.herokuapp.com/tracker/register", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //mPostCommentResponse.requestCompleted();
                    Log.i(Constants.TAG_GCM,"Enviando datos");
                    Log.i(Constants.TAG_GCM, "Respuesta "+response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        //mPostCommentResponse.requestEndedWithError(error);
                        Log.i(Constants.TAG_GCM, "Error " + error.getMessage());
                        Log.i(Constants.TAG_GCM, "ERROR: " + error.networkResponse.statusCode);
                        Log.i(Constants.TAG_GCM, "ERROR: " + error.networkResponse.headers.toString());
                    }catch(Exception ex){
                        Log.i(Constants.TAG_GCM, "EXCEPTION EN RED: "+ex.getMessage() );
                        ex.printStackTrace();

                    }
                }
            }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("register_id",regId);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };


            connection.addToRequestQueue(stringRequest);
        }



    }


    class RegistrationBroadcastReceiver extends BroadcastReceiver {
        public  RegistrationBroadcastReceiver(){
            Log.w(Constants.TRACKER, "Creando receiver");
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.w(Constants.TRACKER, "Recibiendo notificacion de registro. receiver");
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                //Preferences for just this app, very simple
                if (sharedPreferences.getBoolean(Constants.APP_REGISTER_TO_GCM, false)) {
                    Log.d(Constants.TRACKER, "Se recibio correctamente el TOekn");
                    Log.d(Constants.TRACKER, sharedPreferences.toString());
                } else {
                    Log.d(Constants.TRACKER, "NO se logro conectar qué hacemos?");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


}
