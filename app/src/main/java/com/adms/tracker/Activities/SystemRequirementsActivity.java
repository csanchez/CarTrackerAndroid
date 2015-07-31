package com.adms.tracker.Activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.adms.tracker.R;
import com.adms.tracker.utils.Constants;
import com.adms.tracker.utils.ServicesChecker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 *
 */
public class SystemRequirementsActivity extends Activity {

    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    static final int REQUEST_CODE_RECOVER_GPS = 1002;

    // TODO  Añadir alert para encender red, gps, desconectar wifi y activar google play services
    private  Toast toast ;

    private Context context;

    private CheckBox isGooglePlayAvalaible;
    private CheckBox isGPSPlayAvalaible;
    private CheckBox isNetworkPlayAvalaible;


    private TextView buttonLabel;
    private RelativeLayout continueButtonLayout;
    private RelativeLayout buttonEnableGPS;
    private RelativeLayout buttonEnableGoogle;
    private RelativeLayout buttonEnableNetwork;
    private AlertDialog.Builder builder;

    private LocationManager locMan;

    private SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_requirements);
        context = this;

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();
        String message = this.sharedPreferences.getString(Constants.USER_NAME,"UNKNOW NAME");  //intent.getStringExtra(Constants.USER_NAME_KEY);
        TextView textView =  (TextView)findViewById(R.id.user_name);
        textView.setText(message);




        isGooglePlayAvalaible  = ((CheckBox) findViewById(R.id.google_play_services_checkBox));
        isGPSPlayAvalaible     = ((CheckBox) findViewById(R.id.gps_checkBox));
        isNetworkPlayAvalaible =  ((CheckBox) findViewById(R.id.network_checkBox));
        buttonLabel = (TextView)findViewById(R.id.button_label);


        continueButtonLayout   = (RelativeLayout) findViewById(R.id.continue_button_layout);
        buttonEnableGPS = (RelativeLayout)findViewById(R.id.enable_gps_service);
        buttonEnableGoogle = (RelativeLayout)findViewById(R.id.enable_google_play_service);
        buttonEnableNetwork = (RelativeLayout)findViewById(R.id.enable_network_service);

        continueButtonLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(Constants.SYSTEM_REQUERIMENTS_TAG, "Checking services");
                checkServices();
                if(isGooglePlayAvalaible.isChecked() && isNetworkPlayAvalaible.isChecked() && isGPSPlayAvalaible.isChecked() ){
                    Intent intent = new Intent(context, TrackerActivity.class);
                    startActivity(intent);
                }

            }
        });

        buttonEnableGoogle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(Constants.SYSTEM_REQUERIMENTS_TAG, "Checking services");
                GoogleApiAvailability service =     GoogleApiAvailability.getInstance();
                    int status = service.isGooglePlayServicesAvailable(context);
                    if (service.isUserResolvableError(status)) {
                        service.getErrorDialog(SystemRequirementsActivity.this, status, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
                    } else {
                        Toast.makeText(context, getString(R.string.system_requirements_google_play_error) , Toast.LENGTH_LONG).show();
                        finish();
                    }

            }
        });

        buttonEnableNetwork.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(Constants.SYSTEM_REQUERIMENTS_TAG, "Checking NEYWORK");
                showServiceMessageAlert(Settings.ACTION_DATA_ROAMING_SETTINGS, getString(R.string.system_requirements_no_network_message), getString(R.string.system_requirements_network_cancel_message));

            }
        });

        buttonEnableGPS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(Constants.SYSTEM_REQUERIMENTS_TAG, "Checking GPS");
                showServiceMessageAlert(Settings.ACTION_LOCATION_SOURCE_SETTINGS, getString(R.string.system_requirements_no_gps_message), getString(R.string.system_requirements_gps_cancel_message));

            }
        });

        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /*locMan.addGpsStatusListener(new GpsStatus.Listener() {
            public void onGpsStatusChanged(int event){
                Toast.makeText(context, "ocurio cambio en gps ", Toast.LENGTH_LONG).show();
                switch(event){
                    case GpsStatus.GPS_EVENT_STARTED:


                    case GpsStatus.GPS_EVENT_STOPPED:
                        Toast.makeText(context, "ocurio cambio en gps ", Toast.LENGTH_LONG).show();
                        checkServices();
                        break;
                }
            }
        });*/
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "En start");

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "En resumen");
        checkServices();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "En pausa");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "En stop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "En destroy");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "En onActivityResult req" + requestCode + " result " + resultCode);
        switch (requestCode) {
            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, getString(R.string.system_requirements_google_play), Toast.LENGTH_SHORT).show();
                }else
                    if(resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, getString(R.string.system_requirements_no_google_play), Toast.LENGTH_SHORT).show();
                        //finish();
                }
        }
        checkServices();
    }




    public void onProviderEnabled(String provider){


    }

    private void checkServices(){
        Log.w(Constants.SYSTEM_REQUERIMENTS_TAG, "checando servicios");

        if (ServicesChecker.checkPlayServices(this)) {
            isGooglePlayAvalaible.setChecked(true);
            buttonEnableGoogle.setVisibility(View.GONE);
        }else {
            isGooglePlayAvalaible.setChecked(false);
            buttonEnableGoogle.setVisibility(View.VISIBLE);
        }

        if (ServicesChecker.checkMobileNetwork(this, getString(R.string.system_requirements_network_error))){
            isNetworkPlayAvalaible.setChecked(true);
            buttonEnableNetwork.setVisibility(View.GONE);
        }else {
            isNetworkPlayAvalaible.setChecked(false);
            buttonEnableNetwork.setVisibility(View.VISIBLE);
        }

        if (ServicesChecker.checkGPS(this,getString(R.string.system_requirements_gps_error))) {
            isGPSPlayAvalaible.setChecked(true);
            buttonEnableGPS.setVisibility(View.GONE);
        }else {
            isGPSPlayAvalaible.setChecked(false);
            buttonEnableGPS.setVisibility(View.VISIBLE);
        }

        if (isGooglePlayAvalaible.isChecked() && isNetworkPlayAvalaible.isChecked() && isGPSPlayAvalaible.isChecked()) {
            continueButtonLayout.setVisibility(View.VISIBLE);
            //buttonLabel.setText(getString(R.string.system_requirements_button2));
        } else {
            continueButtonLayout.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.system_requirements_missed), Toast.LENGTH_LONG).show();
        }






    }



    private void showServiceMessageAlert(final String action,String message, final String cancelMessage){


         //String action = Settings.ACTION_DATA_ROAMING_SETTINGS; //ACTION_LOCATION_SOURCE_SETTINGS;
        //String message = "Tu GPS parece estar desactivado, deseas activarlo?";
        //final String action = actionRequest;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                                toast = Toast.makeText(context, cancelMessage, Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
        builder.create().show();
    }


}
