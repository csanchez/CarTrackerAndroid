package com.adms.tracker.Activities;

import com.adms.tracker.Activities.util.SystemUiHider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adms.tracker.R;
import com.adms.tracker.networking.Connection;
import com.adms.tracker.utils.Constants;
import com.adms.tracker.utils.DeviceUUID;
import com.adms.tracker.utils.ServicesChecker;
import com.adms.tracker.utils.UiUtil;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class LoginActivity extends Activity {
    //TODO cachar excepciones y mandar a servidor

    private RelativeLayout loginButtonLayout;
    private EditText user_code;
    private EditText user_password;
    private TextView forgot_password;
    private ProgressBar progressBar;
    private Context context;
    private SharedPreferences sharedPreferences ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login_activity);
        Log.d(Constants.LOGIN_TAG, "EN login");

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        user_code = (EditText) findViewById(R.id.user_code);
        user_password = (EditText) findViewById(R.id.user_password);
        forgot_password = (TextView) findViewById(R.id.forgot_password);
        progressBar = (ProgressBar)findViewById(R.id.login_spinner);

        loginButtonLayout = (RelativeLayout) findViewById(R.id.login_button_layout);
        loginButtonLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(Constants.LOGIN_TAG, "Connect to server");
                connectToTracker();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    /**
     * Comprobará la identidad del usuario con Tracer Server
     * @param
     */
    public  void connectToTracker() {
        //TODO pasar a stringx.xml
        if(ServicesChecker.checkAnyNetwork(this, getString(R.string.login_error_no_network))) {


            String user_code = this.user_code.getText().toString().trim();
            String user_password = this.user_password.getText().toString().trim();


            if (this.user_code != null && user_code.length() == 0) {
                //showErrorMessage(getString(R.string.login_error), getString(R.string.error_enter_user_code));
                //((TextView) findViewById(R.id.user_code_error)).setText(getString(R.string.error_enter_user_code));
                Toast.makeText(context, getString(R.string.error_enter_user_code), Toast.LENGTH_SHORT).show();
                this.user_code.requestFocus();
            } else {
                if (this.user_password != null && user_password.length() == 0) {
                    //showErrorMessage(getString(R.string.login_error), getString(R.string.error_enter_password));
                    //((TextView) findViewById(R.id.user_password_error)).setText(getString(R.string.error_enter_password));
                    Toast.makeText(context, getString(R.string.error_enter_password), Toast.LENGTH_SHORT).show();
                    this.user_password.requestFocus();
                } else {
                    requestUser();
                }
            }
        }
    }

    public void forgotPasswordClick(View view){

        Log.d(Constants.LOGIN_TAG, "Olvido contraseña");
    }

    public void showTermsAndConditions(View view){
        Log.d(Constants.LOGIN_TAG, "Mostrara terminos y condiciones");
    }

    private void requestUser(){

        Connection connection = Connection.getInstance(this);
        JSONObject params = new JSONObject();
        try {
            params.put("user_name",user_code.getText().toString().trim());
            params.put("password",user_password.getText().toString().trim());
            params.put("device_uuid",DeviceUUID.getUUID(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.w(Constants.LOGIN_TAG, "UID del device "+ DeviceUUID.getUUID(this));
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
                        //TODO TRAER DEL SERVIDOR?
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
                    Toast.makeText(context, obj.getString("error"), Toast.LENGTH_LONG).show();

                }

                catch (JSONException e) {
                    //TODO Pasar esto  strings.xml
                    //((TextView) findViewById(R.id.user_password_error)).setText("No fue posible procesar tu solicitud");
                    Toast.makeText(context, "ERROR DE JSON", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }catch (Exception e) {
                    //TODO Pasar esto  strings.xml
                    //((TextView) findViewById(R.id.user_password_error)).setText("No fue posible procesar tu solicitud");
                    Toast.makeText(context, "Ocurrio un error en la red y no fue posible procesar tu solicitud", Toast.LENGTH_LONG).show();
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



}
