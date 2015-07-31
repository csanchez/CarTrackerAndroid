package com.adms.tracker.Activities;

import com.adms.tracker.Activities.util.SystemUiHider;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.adms.tracker.R;
import com.adms.tracker.utils.Constants;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class WelcomeActivity extends Activity {

    private RelativeLayout loginButtonLayout;
    private Context context;
    private SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        this.context = this;
        loginButtonLayout = (RelativeLayout) findViewById(R.id.login_button_layout);
        loginButtonLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(Constants.WELCOME_TAG, "To login");
                Log.w(Constants.WELCOME_TAG, "Lanzara actividad login");
                Intent intent = null;
                if(sharedPreferences.getBoolean(Constants.LOGED_IN,false))
                    intent= new Intent(context, TrackerActivity.class);
                else
                    intent= new Intent(context, LoginActivity.class);

                startActivity(intent);
            }
        });

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }



}
