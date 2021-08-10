package com.proyecto.iscodeapp.Funciones;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class RevisarConexion {

    //Conex
    ConnectivityManager cm;
    NetworkInfo nInfo;
    boolean connected;

    Context context;

    public RevisarConexion(Context context) {
        this.context = context;
    }

    public boolean isConnected(){
        //Conexion
        cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        nInfo = cm.getActiveNetworkInfo();
        connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
        return connected;
    }
}
