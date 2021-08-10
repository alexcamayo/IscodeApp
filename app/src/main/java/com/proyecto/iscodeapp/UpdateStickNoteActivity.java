package com.proyecto.iscodeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyecto.iscodeapp.Adapters.UserAdapter;
import com.proyecto.iscodeapp.Databases.MyDataBaseHelper;
import com.proyecto.iscodeapp.Databases.MyDataBasePersonas;
import com.proyecto.iscodeapp.Funciones.DescargarPersonas;
import com.proyecto.iscodeapp.Funciones.FechaSelector;
import com.proyecto.iscodeapp.Funciones.RevisarConexion;
import com.proyecto.iscodeapp.Models.User;
import com.proyecto.iscodeapp.Notificaciones.AlarmasRecordatorios;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateStickNoteActivity extends AppCompatActivity {

    Window window;

    EditText up_titulo_nota,up_contenido_nota;
    CardView up_stick_note;
    TextView up_t_fecha,up_t_hora;
    ImageView up_ivw_importancia; String up_importancia_estado="";
    RelativeLayout layout_repetiralarma;
    TextView repeticion_tiempo, intervalo_tiempo;
    String serepite,tiemporep,intervalorep="";

    //Datos recuperados
    String columna,titulo,contenido,color,fecha,hora,importancia;

    CardView up_barra_opciones;
    ImageButton up_cambiar_color_nota;
    ImageButton up_eliminar_nota;
    ImageButton up_guardar_nota,up_importancia_nota,up_compartir_nota;

    //Cambiar colores
    int posicion_color=0;
    int valor_color=0;

    //Fecha
    ImageButton up_cambiar_fecha;
    private int sdia, smes,saño,shora,sminuto;
    Calendar calendar=Calendar.getInstance();
    static final int DATE_ID=0;
    String nueva_fecha="NO";

    MyDataBaseHelper myDb;
    SharedPreferences preferences_archivos,preferences_varias;

    //Colores
    String Nota1="#FFCF37";
    String Nota2="#AFABAB";
    String Nota3="#ED83C2";
    String Nota4="#03DAC5";

    //Compartir nota
    Dialog lista_personas;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> mUsers;
    SharedPreferences preferences_personas;
    SharedPreferences preferences;
    String[] datosNota=new String[6];
    String PrimeraVezSesion="";
    Dialog PersonasDialog;

    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    String id_usuario;

    AlarmasRecordatorios alarmasRecordatorios;


    boolean connected;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_stick_note);
        window= getWindow();

        RevisarConexion revisarConexion=new RevisarConexion(UpdateStickNoteActivity.this);
        connected=revisarConexion.isConnected();

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();

        lista_personas=new Dialog(this);
        myDb=new MyDataBaseHelper(UpdateStickNoteActivity.this);
        alarmasRecordatorios=new AlarmasRecordatorios(getApplicationContext(),UpdateStickNoteActivity.this);

        preferences_archivos = getSharedPreferences("ARCHIVOS_USUARIO", MODE_PRIVATE);
        preferences=getSharedPreferences("DATOS_LOGIN",MODE_PRIVATE);
        preferences_varias=getSharedPreferences("OTRAS_PREFERENCIAS",MODE_PRIVATE);
        PrimeraVezSesion=preferences.getString("PrimeraVezSesion","");

        up_guardar_nota=findViewById(R.id.up_guardar_nota);
        up_importancia_nota=findViewById(R.id.importancia_record);
        up_ivw_importancia=findViewById(R.id.up_ivw_importancia);

        up_barra_opciones=findViewById(R.id.up_barra_opciones);
        up_cambiar_color_nota=findViewById(R.id.up_cambiar_color_nota);
        up_eliminar_nota=findViewById(R.id.up_eliminar_nota);
        up_cambiar_fecha=findViewById(R.id.up_cambiar_fecha);
        up_compartir_nota=findViewById(R.id.up_compartir_nota);

        //Valores a cambiar;
        up_titulo_nota=findViewById(R.id.up_titulo_nota);
        up_contenido_nota=findViewById(R.id.up_contenido_nota);
        up_stick_note=findViewById(R.id.up_stick_note);
        up_t_fecha=findViewById(R.id.up_t_fecha);
        up_t_hora=findViewById(R.id.up_t_hora);

        //Repetir alarma
        layout_repetiralarma=findViewById(R.id.layout_repetiralarma);
        repeticion_tiempo=findViewById(R.id.repeticion_tiempo);;
        intervalo_tiempo=findViewById(R.id.intervalo_tiempo);
        repeticion_tiempo.setVisibility(View.GONE);
        intervalo_tiempo.setVisibility(View.GONE);
        layout_repetiralarma.setVisibility(View.GONE);


        //Obtener la información del recordatorio
        obtener_informacion_nota();


        up_guardar_nota.setVisibility(View.INVISIBLE);

        up_titulo_nota.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                up_guardar_nota.setVisibility(View.VISIBLE);
                return false;
            }
        });

        up_contenido_nota.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                up_guardar_nota.setVisibility(View.VISIBLE);
                return false;
            }
        });

        up_guardar_nota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up_guardar_nota.setVisibility(View.INVISIBLE);

                if(up_titulo_nota.getText().toString().equals("") || up_t_fecha.getText().toString().equals("")){
                    Toast.makeText(UpdateStickNoteActivity.this,"Debe colocar al menos el título y la fecha",Toast.LENGTH_SHORT).show();
                }
                else {

                    if(layout_repetiralarma.getVisibility()==View.VISIBLE){
                        serepite="SI";
                    }
                    else {
                        serepite="NO";
                    }

                    myDb.updateData(columna,
                            up_titulo_nota.getText().toString().trim(),
                            up_contenido_nota.getText().toString().trim(),
                            "" + valor_color,
                            up_t_fecha.getText().toString(),
                            up_t_hora.getText().toString(),
                            up_importancia_estado,
                            serepite,
                            repeticion_tiempo.getText().toString().trim(),
                            intervalo_tiempo.getText().toString().trim());

                    Log.d("TAG700","El id es: "+columna);

                    if(nueva_fecha.equals("SI")){

                        Log.d("tag9111","entroaqui"+serepite);

                        alarmasRecordatorios.crearAlarma("updateActivity",
                                columna,
                                up_t_fecha.getText().toString(),
                                up_t_hora.getText().toString(),
                                "repetir"+serepite);
                    }

                    preferences_archivos.edit().putString("Actualizar","SI").apply();

                    if(getIntent().hasExtra("ORIGEN")){
                        if(getIntent().getStringExtra("ORIGEN").equals("notificacion")){
                            Intent goMain = new Intent(UpdateStickNoteActivity.this,MainActivity.class);
                            startActivity(goMain);
                            finish();
                        }
                    }
                    else{
                        Intent intent=new Intent(UpdateStickNoteActivity.this,RecordatoriosActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        up_importancia_nota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up_guardar_nota.setVisibility(View.VISIBLE);
                if (up_importancia_estado.equals("NO")){
                    up_importancia_estado="SI";
                    up_ivw_importancia.setVisibility(View.VISIBLE);

                }else if(up_importancia_estado.equals("SI")){
                    up_importancia_estado="NO";
                    up_ivw_importancia.setVisibility(View.INVISIBLE);
                }
            }
        });

        //Actualizar color nota
        up_cambiar_color_nota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up_guardar_nota.setVisibility(View.VISIBLE);
                posicion_color=posicion_color+1;
                valor_color=color_nota();
            }
        });

        up_eliminar_nota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmar_borrar();
            }
        });

        //Fecha
        up_cambiar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FechaSelector fechaSelector=new FechaSelector(UpdateStickNoteActivity.this,UpdateStickNoteActivity.this,"updateAct");
                fechaSelector.adaptarFecha(up_t_fecha,up_t_hora,layout_repetiralarma,repeticion_tiempo,intervalo_tiempo);
                nueva_fecha=preferences_varias.getString("CambioAlarma","NO");


                up_guardar_nota.setVisibility(View.VISIBLE);

                /*DatePickerDialog datePickerDialog= new DatePickerDialog(UpdateStickNoteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        TimePickerDialog timePickerDialog= new TimePickerDialog(    UpdateStickNoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                nueva_fecha="SI";

                                String diaStr = dayOfMonth < 10 ? "0"+dayOfMonth : String.valueOf(dayOfMonth);
                                String mesStr = (month+1) < 10 ? "0"+(month+1) : String.valueOf((month+1));
                                String horaStr = hourOfDay < 10 ? "0"+hourOfDay : String.valueOf(hourOfDay);
                                String minutoStr = minute < 10 ? "0"+minute : String.valueOf(minute);

                                up_t_hora.setText(horaStr+":"+minutoStr);
                                up_t_fecha.setText(diaStr+"/"+mesStr+"/"+year);

                                Toast.makeText(getApplicationContext(), "Se cambió la fecha",Toast.LENGTH_SHORT).show();
                            }
                        },shora,sminuto,false);
                        timePickerDialog.show();
                    }
                },saño,smes,sdia);
                datePickerDialog.show();*/
            }
        });

        up_compartir_nota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(up_t_fecha.getText().toString().equals("")
                        || up_t_hora.getText().toString().equals("")
                        || up_titulo_nota.getText().toString().trim().equals("")){
                    Toast.makeText(UpdateStickNoteActivity.this,"Debe colocar al menos un título y una fecha",Toast.LENGTH_SHORT).show();
                }
                else{
                    compartirNota();
                }
            }
        });

    }

    public void obtener_informacion_nota(){
        if(getIntent().hasExtra("COLUMNA")){
            //Obtener información
            columna=getIntent().getStringExtra("COLUMNA");

            MyDataBaseHelper myDb=new MyDataBaseHelper(UpdateStickNoteActivity.this);
            Cursor cursor=myDb.readOneRow(columna);

            if(cursor.getCount()==0){

            }
            else {
                while (cursor.moveToNext()) {

                    titulo = cursor.getString(1);
                    contenido = cursor.getString(2);
                    color = cursor.getString(3);
                    fecha = cursor.getString(4);
                    hora = cursor.getString(5);
                    importancia = cursor.getString(6);
                    serepite=cursor.getString(7);
                    tiemporep=cursor.getString(8);
                    intervalorep=cursor.getString(9);
                }
            }

            Log.d("TAG700","Llego al update: "+columna+" "+titulo+" "+contenido+" "+color+" "+fecha+" "+hora+" "+importancia+" ");


            //Colocar la información en el stick note
            up_titulo_nota.setText(titulo);
            up_contenido_nota.setText(contenido);
            switch (Integer.parseInt(color)){
                case 0:
                    up_stick_note.getBackground().setTint(getColor(R.color.Nota1));
                    valor_color=0;
                    posicion_color=0;
                    window.setStatusBarColor(getColor(R.color.Nota1));
                    break;
                case 1:
                    up_stick_note.getBackground().setTint(getColor(R.color.Nota2));
                    valor_color=1;
                    posicion_color=1;
                    window.setStatusBarColor(getColor(R.color.Nota2));
                    break;
                case 2:
                    up_stick_note.getBackground().setTint(getColor(R.color.Nota3));
                    valor_color=2;
                    posicion_color=2;
                    window.setStatusBarColor(getColor(R.color.Nota3));
                    break;
                case 3:
                    up_stick_note.getBackground().setTint(getColor(R.color.Nota4));
                    valor_color=3;
                    posicion_color=3;
                    window.setStatusBarColor(getColor(R.color.Nota4));
                    break;
            }

            up_t_fecha.setText(fecha);
            up_t_hora.setText(hora);

            if(serepite.equals("SI")){
                layout_repetiralarma.setVisibility(View.VISIBLE);
            }
            else{
                layout_repetiralarma.setVisibility(View.GONE);
            }
            repeticion_tiempo.setText(tiemporep);repeticion_tiempo.setVisibility(View.GONE);
            intervalo_tiempo.setText(intervalorep);intervalo_tiempo.setVisibility(View.GONE);

            up_importancia_estado=importancia;
            if(up_importancia_estado.equals("SI")){
                up_ivw_importancia.setVisibility(View.VISIBLE);
            }
            else if (up_importancia_estado.equals("NO")){
                up_ivw_importancia.setVisibility(View.INVISIBLE);
            }

        }
        else{
            Toast.makeText(getApplicationContext(),"No hay información",Toast.LENGTH_SHORT).show();
        }
    }

    public void compartirNota(){

        if (connected){
            database.child("InfoGeneral").child("RefreshPersonas").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        //Error
                    }
                    else {
                        if (task.getResult().getValue().toString().equals("SI") || PrimeraVezSesion.equals("SI")){

                            preferences.edit().putString("PrimeraVezSesion","NO").apply();
                            preferences_archivos.edit().putString("PersonasDescargadas","NO").apply();
                            PrimeraVezSesion="NO";

                            PersonasDialog=new Dialog(UpdateStickNoteActivity.this);
                            PersonasDialog.setContentView(R.layout.personas_dialog);
                            DescargarPersonas descargarPersonas=new DescargarPersonas(UpdateStickNoteActivity.this,UpdateStickNoteActivity.this);
                            descargarPersonas.actualizar(PersonasDialog,"updateAct");

                            PersonasDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if(preferences_archivos.getString("PersonasDescargadas","NO").equals("SI")){
                                        mostrarPersonas();
                                    }
                                }
                            });

                        }
                        else if(task.getResult().getValue().toString().equals("NO")){
                            mostrarPersonas();
                        }

                    }
                }
            });
        }
        else {
            Toast.makeText(UpdateStickNoteActivity.this,"Sin conexión a internet para compartir", Toast.LENGTH_SHORT).show();
        }

    }

    public void mostrarPersonas(){
        lista_personas.setContentView(R.layout.lista_personas);
        recyclerView=lista_personas.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(UpdateStickNoteActivity.this));
        mUsers=new ArrayList<>();

        MyDataBasePersonas myDataBasePersonas=new MyDataBasePersonas(UpdateStickNoteActivity.this);
        mUsers.clear();
        Cursor cursor=myDataBasePersonas.readAllData();

        if(cursor.getCount()==0){
            mUsers.clear();
        }
        else{
            while (cursor.moveToNext()){
                String personasid=cursor.getString(1);
                String nombre=cursor.getString(2);
                String correo=cursor.getString(3);
                String fecha=cursor.getString(4);
                String imageurl=cursor.getString(5);

                User user=new User();
                user.setId(personasid);
                user.setUsername(nombre);
                user.setCorreo(correo);
                user.setFecha(fecha);
                user.setImageurl(imageurl);
                mUsers.add(user);
            }
        }

        datosNota[0]=up_titulo_nota.getText().toString().trim();
        datosNota[1]=up_contenido_nota.getText().toString().trim();
        datosNota[2]=""+valor_color;
        datosNota[3]=up_t_fecha.getText().toString();
        datosNota[4]=up_t_hora.getText().toString();
        datosNota[5]=up_importancia_estado;

        userAdapter=new UserAdapter(UpdateStickNoteActivity.this,mUsers,"updatenote",datosNota);
        recyclerView.setAdapter(userAdapter);

        lista_personas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Perfil.setCanceledOnTouchOutside(false);
        lista_personas.show();
    }

    public int color_nota(){

        if (posicion_color==4){
            posicion_color=0;
        }
        int valor=posicion_color;
        switch (valor){
            case 0:
                up_stick_note.getBackground().setTint(getColor(R.color.Nota1));
                window.setStatusBarColor(getColor(R.color.Nota1));
                break;
            case 1:
                up_stick_note.getBackground().setTint(getColor(R.color.Nota2));
                window.setStatusBarColor(getColor(R.color.Nota2));
                break;
            case 2:
                up_stick_note.getBackground().setTint(getColor(R.color.Nota3));
                window.setStatusBarColor(getColor(R.color.Nota3));
                break;
            case 3:
                up_stick_note.getBackground().setTint(getColor(R.color.Nota4));
                window.setStatusBarColor(getColor(R.color.Nota4));
                break;
        }
        return valor;
    }

    void confirmar_borrar(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("BORRAR");
        builder.setMessage("¿Está seguro que desea borrar este recordatorio?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myDb.deleteOneRow(columna);

                alarmasRecordatorios.borrarAlarma("updateActivity",columna);

                Intent intent_go=new Intent(UpdateStickNoteActivity.this,RecordatoriosActivity.class);
                startActivity(intent_go);
                finish();


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getIntent().hasExtra("ORIGEN")){
            if(getIntent().getStringExtra("ORIGEN").equals("notificacion")){
                Intent goMain = new Intent(UpdateStickNoteActivity.this,MainActivity.class);
                startActivity(goMain);
                finish();
            }
        }
        else{
            Intent intent = new Intent(UpdateStickNoteActivity.this,RecordatoriosActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

