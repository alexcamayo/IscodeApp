package com.proyecto.iscodeapp.Funciones;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.proyecto.iscodeapp.Databases.MyDataBasePersonas;
import com.proyecto.iscodeapp.Models.User;
import com.proyecto.iscodeapp.R;
import com.proyecto.iscodeapp.ShowImageActivity;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MostrarPersonas {
    Context context;
    Dialog PersonasPerfil;
    Calendar calendar;


    public MostrarPersonas(Context context) {
        this.context = context;
    }

    public void mostrar(String id){
        User user=new User();
        MyDataBasePersonas myDataBasePersonas=new MyDataBasePersonas(context);
        Cursor cursor=myDataBasePersonas.readOneUser(id);
        if(cursor.getCount()==0){

        }
        else {
            while (cursor.moveToNext()) {
                user.setId(cursor.getString(1));
                user.setUsername(cursor.getString(2));
                user.setCorreo(cursor.getString(3));
                user.setFecha(cursor.getString(4));
                user.setImageurl(cursor.getString(5));
            }
        }



        TextView nombre,correo,edad;
        CircleImageView fotoperfil;

        PersonasPerfil=new Dialog(context);
        PersonasPerfil.setContentView(R.layout.personas_perfil);
        nombre=PersonasPerfil.findViewById(R.id.nombre);
        correo=PersonasPerfil.findViewById(R.id.correo);
        edad=PersonasPerfil.findViewById(R.id.edad);
        fotoperfil=PersonasPerfil.findViewById(R.id.fotoperfil);

        nombre.setText(user.getUsername());

        if (user.getImageurl().equals("default") || user.getImageurl().equals("waiting") ){
            fotoperfil.setImageResource(R.drawable.icon_user);
        }
        else {
            Uri fotoUri=Uri.parse("/data/data/com.proyecto.iscodeapp/cache/fotopersonas"+user.getId()+".jpeg");
            fotoperfil.setImageURI(fotoUri);
        }


        correo.setText(user.getCorreo());

        String nacimiento=user.getFecha();
        calendar=Calendar.getInstance();
        int dia_fecha=Integer.parseInt(nacimiento.substring(0,nacimiento.indexOf('/')));
        int mes_fecha=Integer.parseInt(nacimiento.substring(nacimiento.indexOf('/')+1,nacimiento.lastIndexOf('/')));
        int año_fecha=Integer.parseInt(nacimiento.substring(nacimiento.lastIndexOf('/')+1,nacimiento.length()));
        LocalDate birthday = LocalDate.of(año_fecha, mes_fecha, dia_fecha);
        LocalDate now = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        Period period = Period.between(birthday, now);

        edad.setText(period.getYears() + " años");


        fotoperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goMostrar=new Intent(context, ShowImageActivity.class);
                goMostrar.putExtra("ORIGEN","notificaciones");
                goMostrar.putExtra("ID",user.getId());
                context.startActivity(goMostrar);
            }
        });

        PersonasPerfil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        PersonasPerfil.show();
    }
}
