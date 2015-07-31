package com.adms.tracker.services.location;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adms.tracker.Activities.SystemRequirementsActivity;
import com.adms.tracker.Activities.TrackerActivity;
import com.adms.tracker.networking.Connection;
import com.adms.tracker.utils.ConnectionResultBean;
import com.adms.tracker.utils.Constants;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Si esl servicio es llamado es porque la aplicación tiene los servicios necesarios
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final String TAG = Constants.TAG_LOCATION_SERVICE;

    private IBinder binder;

    //TODO guardar datos de conexion en Shared Preferences
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String  DIALOG_ERROR= "dialog_error";
    private boolean resolvingError = false;
    // Flag that indicates if a request is underway.
    private boolean mInProgress;


    private LocationBroadcastReceiver locationBroadcastReceiver;

    private  GoogleApiClient googleApliCliente;
    private Location lastKnowLocation;
    private LocationRequest locationRequest;

    private SharedPreferences sharedPreferences ;



    public LocationService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(this.TAG, "Service creating");
        mInProgress = false;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        buildGoogleApiClient();
        createLocationRequest();
        this.locationBroadcastReceiver = new LocationBroadcastReceiver();
        //if(!resolvingError)
        //    this.googleApliCliente.connect();

    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(this.TAG, "Service Start Command");
        registerReceiver(this.locationBroadcastReceiver, new IntentFilter(Constants.LOCATION_RECEIVER));
        //if(!resolvingError)
        if(this.googleApliCliente == null)
            buildGoogleApiClient();
        if(!this.googleApliCliente.isConnected())
            this.googleApliCliente.connect();

        //if(this.googleApliCliente.isConnected() || this.googleApliCliente.isConnecting() )
        return START_STICKY;
    }




    @Override
    public IBinder onBind(Intent intent) {
        Log.i(this.TAG, "Service bind");
        binder = new LocationServiceBinder(this);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        Log.i(this.TAG, "Service Unbind");
        //this.googleApliCliente.disconnect();
        //unregisterReceiver(this.locationBroadcastReceiver);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(this.TAG, "Service rebind");
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    @Override
    public void onDestroy() {
        Log.i(this.TAG, "Service destroying");
        this.googleApliCliente.disconnect();
        unregisterReceiver(this.locationBroadcastReceiver);
        super.onDestroy();
    }


    protected synchronized  void buildGoogleApiClient() {
        Log.d(this.TAG, "Conectando a Google play");
        googleApliCliente = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        this.locationRequest = new LocationRequest()
                .setInterval(Constants.LOCATION_INTERVAL)
                .setFastestInterval(Constants.FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocation(){
        if(this.googleApliCliente.isConnected())
            this.lastKnowLocation = LocationServices.FusedLocationApi.getLastLocation(this.googleApliCliente);

        if (this.lastKnowLocation != null) {
            logLastLocationKnow();
            //TODO enviar location al server
        } else {
            if(!this.googleApliCliente.isConnecting())
                Toast.makeText(this, "No se decetto location", Toast.LENGTH_LONG).show();
        }
        /*Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        String locProv = this.locationManager.getBestProvider(criteria, true);
        this.locationManager.requestLocationUpdates(locProv,2000,0,this);
        Location location = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        this.printLocation(location);*/
    }

    protected void startLocationUpdates() {
        Log.d(this.TAG, "Location updates");
        if(this.googleApliCliente.isConnected() )
            LocationServices.FusedLocationApi.requestLocationUpdates(this.googleApliCliente, this.locationRequest, this);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(this.TAG, "Conectado a la API de Location services");
        //call this fot the very first time at the beggining of the program
        this.startLocation();
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(this.TAG, "Connection suspended");
        this.googleApliCliente.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(this.TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

        /*Intent intent =  new Intent(this,TrackerActivity.class);
        Bundle extras = new Bundle();
        try {
            extras.putByteArray("CONNECTION_RESULT", ConnectionResultBean.SerializeConnectionResult(new ConnectionResultBean(connectionResult)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtras(extras);
        intent.setAction(Constants.LOCATION_RECEIVER);
        sendBroadcast(intent);*/

    }

    @Override
    public void onLocationChanged(Location location) {
        this.lastKnowLocation = location;
        logLastLocationKnow();
    }



    /*public void onDialogDismissed(){
        this.resolvingError= false;
    }*/


    private void logLastLocationKnow(){
        Log.d(this.TAG, "Se detecto ubicacion ");
        Log.d(this.TAG, "lat "+String.valueOf(this.lastKnowLocation.getLatitude()));
        Log.d(this.TAG, "long " + String.valueOf(this.lastKnowLocation.getLongitude()));

    }




    class LocationBroadcastReceiver extends BroadcastReceiver {
        public  LocationBroadcastReceiver(){
            Log.w(TAG, "Creando location receiver");
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w(Constants.TRACKER, "onReceive location receiver SOLICITANDO UBICACION");
            startLocation();
        }

    }

        /*
    //TODO MOVER A EVENT BUS
    private void sendLocationUpdateToServer(){

        Connection connection = Connection.getInstance(this);
        JSONObject params = new JSONObject();
        try {
            params.put("auth_token",this.sharedPreferences.getString(Constants.AUTH_TOKEN, ""));
            params.put("password",user_password.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Constants.USERS_SERVICE_URL+Constants.USERS_AUTH_URL,params ,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(Constants.LOGIN_TAG, "Response: " + response.toString());
                        Log.w(Constants.LOGIN_TAG, "Lanzara actividad SystemRequirementsActivity");
                        progressBar.setVisibility(View.GONE);

                        try {
                            if(response.getInt("user_status") == Constants.USER_TEST_MODE || response.getInt("user_status") == Constants.USER_ACTIVE){
                                sharedPreferences.edit().putString(Constants.AUTH_TOKEN,  response.getString("auth_token")).apply();
                                sharedPreferences.edit().putString(Constants.USER_NAME, response.getString("user_name")).apply();
                                sharedPreferences.edit().putInt(Constants.USER_STATUS, response.getInt("user_status")).apply();
                                sharedPreferences.edit().putInt(Constants.USER_PLAN, response.getInt("user_package")).apply();
                                //TODO TRAER DEL SERVIDOR
                                sharedPreferences.edit().putInt(Constants.UPDATE_RATE, 60000).apply();
                                sharedPreferences.edit().putBoolean(Constants.LOGED_IN, true).apply();
                                //TODO ir checando esta parte
                                startActivity(new Intent(context, SystemRequirementsActivity.class));

                            }else{
                                Toast.makeText(context, response.getString("error"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch(Exception e){
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)  {
                        try {
                            Log.w(Constants.LOGIN_TAG, "Error en respuesta "+error);
                            Log.w(Constants.LOGIN_TAG, "Tipo Error en respuesta class name " + error.getClass().getName());
                            Log.w(Constants.LOGIN_TAG, "error mensaje "+error.getMessage());

                            if( error instanceof NetworkError) {
                            } else if( error instanceof ServerError) {
                            } else if( error instanceof AuthFailureError) {
                            } else if( error instanceof ParseError) {
                            } else if( error instanceof NoConnectionError) {
                            } else if( error instanceof TimeoutError) {
                            }


                            Log.w(Constants.LOGIN_TAG, "erro "+ new String(error.networkResponse.data));
                            //TODO marca error cuando no hay red, checar este error.
                            //TODO manage SSL
                            NetworkResponse response = error.networkResponse;
                            JSONObject obj=new JSONObject(new String(response.data));
                            //((TextView) findViewById(R.id.user_password_error)).setText(obj.getString("error"));
                            Toast.makeText(context, obj.getString("error"), Toast.LENGTH_SHORT).show();

                        }

                        catch (JSONException e) {
                            //TODO Pasar esto  strings.xml
                            //((TextView) findViewById(R.id.user_password_error)).setText("No fue posible procesar tu solicitud");
                            Toast.makeText(context, "ERROR DE JSON", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }catch (Exception e) {
                            //TODO Pasar esto  strings.xml
                            //((TextView) findViewById(R.id.user_password_error)).setText("No fue posible procesar tu solicitud");
                            Toast.makeText(context, "Ocurrio un error en la red y no fue posible procesar tu solicitud", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }


                        progressBar.setVisibility(View.GONE);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "My useragent");
                return headers;
            }
        };
        progressBar.setVisibility(View.VISIBLE);
        connection.addToRequestQueue(jsObjRequest);
    }
    */

}
