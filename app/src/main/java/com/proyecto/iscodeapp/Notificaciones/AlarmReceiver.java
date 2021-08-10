package com.proyecto.iscodeapp.Notificaciones;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.proyecto.iscodeapp.Databases.MyDataBaseHelper;
import com.proyecto.iscodeapp.R;


public class AlarmReceiver extends BroadcastReceiver {

    String CHANNEL_ID="";

    int notificacionId;
    String titulo="";
    String contenido="";
    String importancia="NO";
    String impotanciaIcono="";

    @Override
    public void onReceive(Context context, Intent intent) {

        notificacionId=intent.getIntExtra("COLUMNA",0);
        Log.d("TAG700","LLegó al alarm: "+notificacionId);

        MyDataBaseHelper myDb=new MyDataBaseHelper(context);
        Cursor cursor=myDb.readOneRow(String.valueOf(notificacionId));
        if(cursor.getCount()==0){

        }
        else {
            while (cursor.moveToNext()) {
                titulo = cursor.getString(1);
                contenido = cursor.getString(2);
                importancia = cursor.getString(6);
            }
        }
        /*String titulo=intent.getStringExtra("TITULO");
        String contenido=intent.getStringExtra("CONTENIDO");
        int color=intent.getIntExtra("COLOR",0);
        String fecha=intent.getStringExtra("FECHA");
        String hora=intent.getStringExtra("HORA");
        String importancia=intent.getStringExtra("IMPORTANCIA");*/

        if (importancia.equals("SI")){
            impotanciaIcono="Recordatorios \uD83D\uDEA9";
        }
        else{
            impotanciaIcono="Recordatorios";
        }


        //Cuando se le de clicK a la notificacion
        Intent UpdateStickIntent=new Intent(context, MostrarRecordatorio.class);
        UpdateStickIntent.putExtra("COLUMNA",""+notificacionId);
        /*UpdateStickIntent.putExtra("TITULO",titulo);
        UpdateStickIntent.putExtra("CONTENIDO",contenido);
        UpdateStickIntent.putExtra("COLOR",""+color);
        UpdateStickIntent.putExtra("FECHA",fecha);
        UpdateStickIntent.putExtra("HORA",hora);
        UpdateStickIntent.putExtra("IMPORTANCIA",importancia);*/
        PendingIntent contentIntent=PendingIntent.getBroadcast(context,notificacionId,UpdateStickIntent,PendingIntent.FLAG_CANCEL_CURRENT);




        //Crear la notificación
        //CHANNEL_ID="CHANNEL_ID_"+notificacionId;
        CHANNEL_ID="RECORDATORIOS";
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorios",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        //Prepare notification
        Bitmap bitmap = null;
        int drawable_def=R.drawable.pic2;
        bitmap= BitmapFactory.decodeResource(context.getResources(), drawable_def);
        Notification.Builder notification=new Notification.Builder(context,CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(contenido)
                .setSmallIcon(R.drawable.logo_app)
                .setLargeIcon(bitmap)
                .setContentIntent(contentIntent)
                .setStyle(new Notification.InboxStyle().addLine(contenido).setSummaryText(impotanciaIcono))
                .setAutoCancel(true);

        notificationManager.notify(notificacionId,notification.build());


    }
}
