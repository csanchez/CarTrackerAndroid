package com.adms.tracker.utils;

import com.google.android.gms.common.ConnectionResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by admin on 23/07/15.
 */
public class ConnectionResultBean implements Serializable {
    private ConnectionResult connectionResult;

    public  ConnectionResultBean(ConnectionResult connectionResult) {
        this.connectionResult = connectionResult;
    }

    public ConnectionResult getConnectionResult(){
        return this.connectionResult;
    }


    public static byte[] SerializeConnectionResult(ConnectionResultBean connectionResultBean) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(connectionResultBean);
        return baos.toByteArray();
    }
}
