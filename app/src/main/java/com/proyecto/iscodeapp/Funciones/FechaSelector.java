package com.proyecto.iscodeapp.Funciones;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.proyecto.iscodeapp.R;

import java.util.Calendar;

public class FechaSelector {
    Activity activity;
    Context context;
    String origen;

    //Seleccionar fecha
    AutoCompleteTextView spinner_tiempo;
    Dialog seleccionarFecha;
    SwitchCompat repetir;
    RelativeLayout repetir_layout;
    TextView fecha,hora;
    ImageButton sel_fecha,guardar_fecha;
    Calendar calendar;
    int sdia, smes,saño,shora,sminuto;
    EditText tiempo;

    SharedPreferences preferences_varias;

    public FechaSelector(Activity activity, Context context,String origen) {
        this.activity = activity;
        this.context = context;
        this.origen=origen;
    }

    public void adaptarFecha(TextView t_fecha,TextView t_hora,RelativeLayout layout_repetiralarma,
                             TextView repeticion_tiempo,TextView intervalo_tiempo){

        preferences_varias=activity.getSharedPreferences("OTRAS_PREFERENCIAS",Context.MODE_PRIVATE);
        seleccionarFecha=new Dialog(context);

        seleccionarFecha.setContentView(R.layout.seleccionar_fecha);
        spinner_tiempo=seleccionarFecha.findViewById(R.id.spinner_tiempo);
        repetir=seleccionarFecha.findViewById(R.id.repetir);
        repetir_layout=seleccionarFecha.findViewById(R.id.repetir_layout);
        sel_fecha=seleccionarFecha.findViewById(R.id.sel_fecha);
        fecha=seleccionarFecha.findViewById(R.id.fecha);
        hora=seleccionarFecha.findViewById(R.id.hora);
        guardar_fecha=seleccionarFecha.findViewById(R.id.guardar_fecha);
        tiempo=seleccionarFecha.findViewById(R.id.tiempo);
        repetir_layout.setVisibility(View.GONE);

        String [] opcion_tiempo={"Minuto(s)","Hora(s)","Día(s)","Semana(s)","Mes(es)"};
        ArrayAdapter<String> adapter_tiempo = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item,opcion_tiempo);
        spinner_tiempo.setAdapter(adapter_tiempo);

        if (!t_fecha.getText().toString().equals("") || !t_hora.getText().toString().equals("")){
            fecha.setText(t_fecha.getText().toString());
            hora.setText(t_hora.getText().toString());
        }

        if(layout_repetiralarma.getVisibility()==View.VISIBLE){
            repetir.setChecked(true);
            repetir_layout.setVisibility(View.VISIBLE);
            tiempo.setText(repeticion_tiempo.getText().toString().trim());

            switch (intervalo_tiempo.getText().toString().trim()){
                case "Minuto(s)":
                    spinner_tiempo.setText("Minuto(s)",false);
                    break;
                case "Hora(s)":
                    spinner_tiempo.setText("Hora(s)",false);
                    break;
                case "Día(s)":
                    spinner_tiempo.setText("Día(s)",false);
                    break;
                case "Semana(s)":
                    spinner_tiempo.setText("Semana(s)",false);
                    break;
                case "Mes(es)":
                    spinner_tiempo.setText("Mes(es)",false);
                    break;
            }

        }
        else{
            repetir.setChecked(false);
            repetir_layout.setVisibility(View.GONE);
        }





        repetir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(repetir.isChecked()){
                    repetir_layout.setVisibility(View.VISIBLE);
                }
                else{
                    repetir_layout.setVisibility(View.GONE);
                }
            }
        });

        sel_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar=Calendar.getInstance();
                sminuto=calendar.get(Calendar.MINUTE);
                shora=calendar.get(Calendar.HOUR_OF_DAY);
                sdia=calendar.get(Calendar.DAY_OF_MONTH);
                smes=calendar.get(Calendar.MONTH);
                saño=calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        TimePickerDialog timePickerDialog= new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String diaStr = dayOfMonth < 10 ? "0"+dayOfMonth : String.valueOf(dayOfMonth);
                                String mesStr = (month+1) < 10 ? "0"+(month+1) : String.valueOf((month+1));
                                String horaStr = hourOfDay < 10 ? "0"+hourOfDay : String.valueOf(hourOfDay);
                                String minutoStr = minute < 10 ? "0"+minute : String.valueOf(minute);

                                hora.setText(horaStr+":"+minutoStr);
                                fecha.setText(diaStr+"/"+mesStr+"/"+year);


                            }
                        },shora,sminuto,false);
                        timePickerDialog.show();
                    }
                },saño,smes,sdia);
                datePickerDialog.show();

            }
        });

        guardar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hora.getText().toString().equals("Hora") || fecha.getText().toString().equals("Fecha")){
                    Toast.makeText(context,"Debe colocar una fecha y una hora",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(repetir.isChecked()){
                        if(tiempo.getText().toString().trim().equals("") || spinner_tiempo.getText().toString().trim().equals("")){
                            Toast.makeText(context,"Debe colocar un tiempo de repetición",Toast.LENGTH_SHORT).show();
                        }
                        else{

                            layout_repetiralarma.setVisibility(View.VISIBLE);
                            t_fecha.setText(fecha.getText().toString());
                            t_hora.setText(hora.getText().toString());

                            repeticion_tiempo.setText(tiempo.getText().toString().trim());
                            intervalo_tiempo.setText(spinner_tiempo.getText().toString().trim());
                            repeticion_tiempo.setVisibility(View.GONE);
                            intervalo_tiempo.setVisibility(View.GONE);

                            if (origen.equals("updateAct")){
                                preferences_varias.edit().putString("CambioAlarma","SI").apply();
                            }
                            else{
                                preferences_varias.edit().putString("CambioAlarma","NO").apply();
                            }


                            seleccionarFecha.dismiss();
                        }
                    }
                    else {
                        layout_repetiralarma.setVisibility(View.GONE);
                        t_fecha.setText(fecha.getText().toString());
                        t_hora.setText(hora.getText().toString());

                        if (origen.equals("updateAct")){
                            preferences_varias.edit().putString("CambioAlarma","SI").apply();
                        }
                        else{
                            preferences_varias.edit().putString("CambioAlarma","NO").apply();
                        }
                        seleccionarFecha.dismiss();
                    }
                }



            }
        });




        seleccionarFecha.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Perfil.setCanceledOnTouchOutside(false);
        seleccionarFecha.show();
    }


}
