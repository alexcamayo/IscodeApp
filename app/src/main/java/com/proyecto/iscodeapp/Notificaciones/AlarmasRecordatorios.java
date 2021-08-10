package com.proyecto.iscodeapp.Notificaciones;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.proyecto.iscodeapp.Databases.MyDataBaseHelper;
import com.proyecto.iscodeapp.RecordatoriosActivity;
import com.proyecto.iscodeapp.UpdateStickNoteActivity;

import java.util.Calendar;

public class AlarmasRecordatorios {

    Context context;
    Activity activity;

    public AlarmasRecordatorios(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void crearAlarma(String origen, String ID,String Fecha,String Hora,String opcion){
        //Set notification id and message
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("COLUMNA",Integer.parseInt(ID));
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context, Integer.parseInt(ID), intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager=(AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);



        int val1= Fecha.indexOf('/');
        int val2= Fecha.lastIndexOf('/');
        int val3=Hora.indexOf(':');

        int dia=Integer.parseInt( Fecha.substring(0,val1));
        int mes=Integer.parseInt(Fecha.substring(val1+1,val2));
        int año=Integer.parseInt( Fecha.substring(val2+1,Fecha.length()));

        int hora=Integer.parseInt(Hora.substring(0,val3));
        int minutos=Integer.parseInt(Hora.substring(val3+1,Hora.length()));

        Log.d("tag570","dia: "+dia+" mes: "+mes+" año: "+año+" hora: "+hora+" minuto: "+minutos);

        //calendar.set(año,mes,dia,hora,minutos,0);
        Calendar startTime=Calendar.getInstance();
        startTime.set(Calendar.YEAR,año);
        startTime.set(Calendar.MONTH,(mes-1));
        startTime.set(Calendar.DAY_OF_MONTH,dia);
        startTime.set(Calendar.HOUR_OF_DAY,hora);
        startTime.set(Calendar.MINUTE,minutos);
        startTime.set(Calendar.SECOND,0);

        long tiempo_alarma=startTime.getTimeInMillis();

        if (origen.equals("recordActivity")){
            if (tiempo_alarma>System.currentTimeMillis()){

                if(opcion.equals("repetirSI")){

                    long reptiempo=tiempoRepetir(ID);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, tiempo_alarma,
                            reptiempo, pendingIntent);
                }
                else{
                    alarmManager.set(AlarmManager.RTC_WAKEUP,tiempo_alarma,pendingIntent);
                }


                Log.d("tag870","Creando recordatorios: "+ID);
            }
        }
        else{
            if(opcion.equals("repetirSI")){

                long reptiempo=tiempoRepetir(ID);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, tiempo_alarma,
                        reptiempo, pendingIntent);
            }
            else{
                alarmManager.set(AlarmManager.RTC_WAKEUP,tiempo_alarma,pendingIntent);
            }

        }
    }

    public void borrarAlarma(String origen,String ID){
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(
                context,
                Integer.parseInt(ID),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager=(AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public long tiempoRepetir(String columna){

        String tiemporep = "", intervalorep="";

        MyDataBaseHelper myDb=new MyDataBaseHelper(context);
        Cursor cursor=myDb.readOneRow(String.valueOf(columna));

        if(cursor.getCount()==0){

        }
        else {
            while (cursor.moveToNext()) {
                tiemporep = cursor.getString(8);
                intervalorep = cursor.getString(9);
            }
        }

        long tiempo_edit=Long.parseLong(tiemporep);
        long tiempo_intervalo=0;

        switch (intervalorep){
            case "Minuto(s)":
                tiempo_intervalo=60;
                break;
            case "Hora(s)":
                tiempo_intervalo=60*60;
                break;
            case "Día(s)":
                tiempo_intervalo=60*60*24;
                break;
            case "Semana(s)":
                tiempo_intervalo=60*60*24*7;
                break;
            case "Mes(es)":
                tiempo_intervalo=60*60*24*30;
                break;
        }
        long tiempo_final=tiempo_edit*tiempo_intervalo*1000;

        return tiempo_final;
    }
}
