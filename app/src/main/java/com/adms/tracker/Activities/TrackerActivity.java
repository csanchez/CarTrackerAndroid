package com.adms.tracker.Activities;

import com.adms.tracker.Activities.util.SystemUiHider;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.adms.tracker.R;
import com.adms.tracker.services.RegistrationIntentService;
import com.adms.tracker.services.location.LocationService;
import com.adms.tracker.services.location.LocationServiceConnection;
import com.adms.tracker.utils.Constants;
import de.greenrobot.event.EventBus;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class TrackerActivity extends Activity {
    //TODO Save instance of activities
    //TODO cambiar a event bus, parece ser mejor




    //protected GoogleCloudMessaging gcm;
    protected Context context;
    //protected AtomicInteger msgId;



    //private LocationServiceConnection localServiceConn;

    //cosas finales
    private RegistrationBroadcastReceiver registrationBroadcastReceiver;
    //private LocationBroadcastReceiver locationBroadcastReceiver;
    private SharedPreferences sharedPreferences;
    private LocationServiceConnection localServiceConn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(Constants.TRACKER, "Creando Tracker");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        registrationBroadcastReceiver= new RegistrationBroadcastReceiver();


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.w(Constants.TRACKER, "Empezando Tracker");
        //EventBus.getDefault().register(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(Constants.TRACKER, "Resumiendo Tracker");
        registerReceiver(this.registrationBroadcastReceiver, new IntentFilter(Constants.REGISTRATION_RECEIVER));
        //registerReceiver(this.locationBroadcastReceiver, new IntentFilter(Constants.LOCATION_RECEIVER));
        //SI el usuario no está registrado, registrarlo al inicio de la Actividad
        if(!this.sharedPreferences.getBoolean(Constants.APP_REGISTER_TO_GCM, false))
            registerGoogleCloudMessaging();
        Log.w(Constants.TRACKER, "A quedado registrado");

        Log.i(Constants.TRACKER, "Start tracking GPS");
        this.startService(new Intent(this, LocationService.class));
        localServiceConn = new LocationServiceConnection(null);
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        bindService(locationServiceIntent, this.localServiceConn, Context.BIND_AUTO_CREATE);
    }



    @Override
    protected void onPause() {
        Log.w(Constants.TRACKER, "Pausando Tracker");
        super.onPause();
        unregisterReceiver(this.registrationBroadcastReceiver);
        //unregisterReceiver(this.locationBroadcastReceiver);
        try{
            unbindService(this.localServiceConn);
        } catch (Throwable t) {
            // catch any issues, typical for destroy routines
            // even if we failed to destroy something, we need to continue destroying
            Log.w(Constants.TAG_GCM, "Failed to unbind from the service", t);
        }


    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.w(Constants.TRACKER, "Destruyendo Tracker");
        super.onDestroy();

    }

    private void registerGoogleCloudMessaging() {
        //Vamos a registrar con la Nube de Google y al servidor
        Log.w(Constants.TRACKER, "Va a registrar en la nube");
        startService(new Intent(this, RegistrationIntentService.class));
        Log.w(Constants.TRACKER, "Quedó registrado en la nube");
    }



    /*public static class ErrorDialogFragment extends DialogFragment{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);

        }

        @Override
        public void onDismiss(DialogInterface dialog){
            ((TrackerActivity)getActivity()).onDialogDismissed();
        }

    }*/


    /*private void showErrorDialog(int errorCode){
        //ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        //Bundle args = new Bundle();
        //args.putInt(DIALOG_ERROR,errorCode);
        //dialogFragment.setArguments(args);
        //dialogFragment.show(getSupportFragmentManager(),"errordialog");

        GoogleApiAvailability service =     GoogleApiAvailability.getInstance();
        int status = service.isGooglePlayServicesAvailable(context);
        service.getErrorDialog(this, errorCode,REQUEST_RESOLVE_ERROR ).show();


    }*/

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
