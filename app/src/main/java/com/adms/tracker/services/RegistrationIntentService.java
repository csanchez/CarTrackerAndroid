package com.adms.tracker.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adms.tracker.Activities.SystemRequirementsActivity;
import com.adms.tracker.R;
import com.adms.tracker.networking.Connection;
import com.adms.tracker.utils.Constants;
import com.adms.tracker.utils.DeviceUUID;
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
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationIntentService extends IntentService {
    private final String TAG = Constants.TAG_REGISTRATION_INTENT_SERVICE;


    private static final String[] TOPICS = {"global"};
    private SharedPreferences sharedPreferences;

    public RegistrationIntentService() {
        super(Constants.TAG_REGISTRATION_INTENT_SERVICE);
        Log.w(this.TAG, "Se genero el servicio de Reegistro de ID");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            synchronized (this.TAG) {
                // Initially a network call, to retrieve the token, subsequent calls are local.
                Log.i(this.TAG, "Peticion por IntanceID");
                InstanceID instanceID = InstanceID.getInstance(this);
                Log.i(this.TAG, "IntanceID generado: "+instanceID.toString());
                Log.i(this.TAG, "IntanceID generado: "+instanceID.getId());
                Log.i(this.TAG, "IntanceID generado: "+instanceID.getId());
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(this.TAG, "GCM Registration Token: " + token);

                // TODO: send any registration to my app's servers, if applicable.
                sendRegistrationToServer(instanceID.getId(), token);

                // TODO: Subscribe to topic channels, if applicable.
                // e.g. for (String topic : TOPICS) {
                //          GcmPubSub pubSub = GcmPubSub.getInstance(this);
                //          pubSub.subscribe(token, "/topics/" + topic, null);
                //       }
                sharedPreferences.edit().putString(Constants.INSTANCE_ID, instanceID.toString()).apply();
                sharedPreferences.edit().putString(Constants.GCM_TOKEN, token).apply();

            }
        } catch (IOException e) {
            Log.d(this.TAG, "Failed to complete token refresh");
            //sharedPreferences.edit().putBoolean(getString(R.string.registering_message), false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
       //LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(getString(R.string.intent_name_REGISTRATION_COMPLETE)));
        sharedPreferences.edit().putBoolean(Constants.APP_REGISTER_TO_GCM, true).apply();
        sendBroadcast(new Intent().setAction(Constants.REGISTRATION_RECEIVER));
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    private void sendRegistrationToServer(String instnceID, String token){
        Connection connection = Connection.getInstance(this);

        JSONObject params = new JSONObject();
        try {
            params.put("gcm_token", token.trim());
            params.put("reg_id", instnceID.trim());
            //params.put("auth_token",sharedPreferences.getString(Constants.AUTH_TOKEN, ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Constants.USERS_SERVICE_URL+Constants.DEVICE_GCM_URL,params ,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(TAG, "Register complete on device: " + response.toString());
                        Log.i(TAG, "Response: " + response.toString());
                        sharedPreferences.edit().putBoolean(Constants.TOKEN_SENT_TO_SERVER, true).apply();
                        /*try {
                            //TODO añadir cheque en statuses??



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch(Exception e){
                            e.printStackTrace();
                        }*/


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)  {
                        try {
                            Log.w(TAG, "Error en respuesta "+error);
                            Log.w(TAG, "Tipo Error en respuesta class name " + error.getClass().getName());
                            Log.w(TAG, "error mensaje "+error.getMessage());

                            if( error instanceof NetworkError) {
                            } else if( error instanceof ServerError) {
                            } else if( error instanceof AuthFailureError) {
                            } else if( error instanceof ParseError) {
                            } else if( error instanceof NoConnectionError) {
                            } else if( error instanceof TimeoutError) {
                            }


                            Log.w(TAG, "erro "+ new String(error.networkResponse.data));
                            //TODO marca error cuando no hay red, checar este error.
                            //TODO manage SSL
                            NetworkResponse response = error.networkResponse;
                            JSONObject obj=new JSONObject(new String(response.data));
                            //((TextView) findViewById(R.id.user_password_error)).setText(obj.getString("error"));
                            //Toast.makeText(context, obj.getString("error"), Toast.LENGTH_SHORT).show();

                        }

                        catch (JSONException e) {
                            //TODO Pasar esto  strings.xml
                            //((TextView) findViewById(R.id.user_password_error)).setText("No fue posible procesar tu solicitud");
                            //Toast.makeText(context, "ERROR DE JSON", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }catch (Exception e) {
                            //TODO Pasar esto  strings.xml
                            //((TextView) findViewById(R.id.user_password_error)).setText("No fue posible procesar tu solicitud");
                            //Toast.makeText(context, "Ocurrio un error en la red y no fue posible procesar tu solicitud", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }



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
                headers.put("Authorization", "Bearer "+sharedPreferences.getString(Constants.AUTH_TOKEN, ""));
                return headers;
            }
        };

        connection.addToRequestQueue(jsObjRequest);
    }



}
