package com.proyecto.iscodeapp.Notificaciones;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.proyecto.iscodeapp.UpdateStickNoteActivity;

public class MostrarRecordatorio extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notificacionId=intent.getStringExtra("COLUMNA");
        /*String titulo=intent.getStringExtra("TITULO");
        String contenido=intent.getStringExtra("CONTENIDO");
        String color=intent.getStringExtra("COLOR");
        String fecha=intent.getStringExtra("FECHA");
        String hora=intent.getStringExtra("HORA");
        String importancia=intent.getStringExtra("IMPORTANCIA");*/

        Log.d("TAG700","Llego al mostrar: "+notificacionId);

        Intent go_updatesticknote=new Intent(context, UpdateStickNoteActivity.class);
        go_updatesticknote.putExtra("ORIGEN","notificacion");
        go_updatesticknote.putExtra("COLUMNA",notificacionId);
        /*go_updatesticknote.putExtra("TITULO",titulo);
        go_updatesticknote.putExtra("CONTENIDO",contenido);
        go_updatesticknote.putExtra("COLOR",color);
        go_updatesticknote.putExtra("FECHA",fecha);
        go_updatesticknote.putExtra("HORA",hora);
        go_updatesticknote.putExtra("IMPORTANCIA",importancia);*/
        context.startActivity(go_updatesticknote.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
