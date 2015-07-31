package com.adms.tracker.networking;

/**
 * Created by adms on 17/11/14.
 */


import android.content.Context;
import android.util.Log;

import com.adms.tracker.utils.Constants;
import com.android.volley.Response;

/**
 * Created by adms on 12/10/14.
 */
public class RegisterListener   implements Response.Listener {
    //private Context context;
    //private String imageName;
    public RegisterListener(Context context,String imageName){
       // this.context = context;
       // this.imageName = imageName;
        Log.i(Constants.TAG_GCM,"Enviando datos");
    }
    @Override
    public void onResponse(Object response) {
        try {
            System.out.println("Response: " + response.toString());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


}
