package com.proyecto.iscodeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.Objects;

public class CreateStickNoteActivity extends AppCompatActivity {
    Window window;

    CardView stick_note;
    ImageButton guardar_nota;
    ImageButton importancia_nota,compartir_nota;

    CardView barra_opciones;
    ImageButton cambiar_color_nota;

    //Crear primera vez
    String columna="";

    //Cambiar colores
    int posicion_color=0;
    int valor_color=0;

    //Fecha
    ImageButton cambiar_fecha;
    private int sdia, smes,saño,shora,sminuto;
    Calendar calendar=Calendar.getInstance();
    static final int DATE_ID=0;
    TextView t_fecha,t_hora;

    //Datos
    EditText titulo_nota,contenido_nota;
    String importancia_estado="NO";
    ImageView ivw_importancia;
    RelativeLayout layout_repetiralarma;
    TextView repeticion_tiempo, intervalo_tiempo;
    String serepite="";

    //Para regresar a mensajes
    String origen="";
    String PrimeraVezSesion="";
    Dialog PersonasDialog;

    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    String id_usuario;
    MyDataBaseHelper myDb;
    AlarmasRecordatorios crearAlarma;

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
    SharedPreferences preferences_archivos,preferences;
    String[] datosNota=new String[6];

    boolean connected;





    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_stick_note);

        RevisarConexion revisarConexion=new RevisarConexion(CreateStickNoteActivity.this);
        connected=revisarConexion.isConnected();

        window= getWindow();
        window.setStatusBarColor(Color.parseColor(Nota1));

        lista_personas=new Dialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();
        id_usuario = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        preferences_archivos = getSharedPreferences("ARCHIVOS_USUARIO", MODE_PRIVATE);
        preferences=getSharedPreferences("DATOS_LOGIN",MODE_PRIVATE);
        myDb=new MyDataBaseHelper(CreateStickNoteActivity.this);
        crearAlarma=new AlarmasRecordatorios(getApplicationContext(),CreateStickNoteActivity.this);

        stick_note=findViewById(R.id.stick_note);
        guardar_nota=findViewById(R.id.guardar_nota);
        importancia_nota=findViewById(R.id.importancia_nota);
        ivw_importancia=findViewById(R.id.ivw_importancia);
        ivw_importancia.setVisibility(View.INVISIBLE);
        barra_opciones=findViewById(R.id.barra_opciones);
        cambiar_color_nota=findViewById(R.id.cambiar_color_nota);
        titulo_nota=findViewById(R.id.titulo_nota);
        contenido_nota=findViewById(R.id.up_contenido_nota);
        compartir_nota=findViewById(R.id.compartir_nota);
        cambiar_fecha=findViewById(R.id.sel_fecha_record);
        t_fecha=findViewById(R.id.t_fecha);
        t_hora=findViewById(R.id.t_hora);

        layout_repetiralarma=findViewById(R.id.layout_repetiralarma);
        repeticion_tiempo=findViewById(R.id.repeticion_tiempo);;
        intervalo_tiempo=findViewById(R.id.intervalo_tiempo);
        repeticion_tiempo.setVisibility(View.GONE);
        intervalo_tiempo.setVisibility(View.GONE);
        layout_repetiralarma.setVisibility(View.GONE);


        guardar_nota.setVisibility(View.INVISIBLE);

        PrimeraVezSesion=preferences.getString("PrimeraVezSesion","");

        titulo_nota.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                guardar_nota.setVisibility(View.VISIBLE);
                return false;
            }
        });

        contenido_nota.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                guardar_nota.setVisibility(View.VISIBLE);
                return false;
            }
        });

        guardar_nota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar_nota.setVisibility(View.INVISIBLE);

                Log.d("TAG450","Importancia: "+importancia_estado);
                Log.d("TAG450","Color: "+valor_color);

                if(titulo_nota.getText().toString().equals("") || t_fecha.getText().toString().equals("")){
                    Toast.makeText(CreateStickNoteActivity.this,"Debe colocar al menos el título y la fecha",Toast.LENGTH_SHORT).show();
                }
                else{

                    if(layout_repetiralarma.getVisibility()==View.VISIBLE){
                        serepite="SI";
                    }
                    else {
                        serepite="NO";
                    }

                    long resultado= myDb.agregar_recordatorio(
                            titulo_nota.getText().toString().trim(),
                            contenido_nota.getText().toString().trim(),
                            ""+valor_color,
                            t_fecha.getText().toString(),
                            t_hora.getText().toString(),
                            importancia_estado,
                            serepite,
                            repeticion_tiempo.getText().toString().trim(),
                            intervalo_tiempo.getText().toString().trim());

                    columna=""+resultado;
                    Log.d("TAG700","El id es: "+columna);

                    crearAlarma.crearAlarma("createActivity",
                            String.valueOf(resultado),
                            t_fecha.getText().toString(),
                            t_hora.getText().toString(),
                            "repetir"+serepite);


                    //Actualizar el estado en firebase
                    database.child("Usuarios").child(id_usuario).child("InfoArchivos").child("HayDBrec").setValue("SI");
                    preferences_archivos.edit().putString("HayDBrec","SI").apply();
                    preferences_archivos.edit().putString("Actualizar","SI").apply();
                    preferences_archivos.edit().putString("PrimeraVez","NO").apply();

                    Intent intent=new Intent(CreateStickNoteActivity.this,RecordatoriosActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        importancia_nota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar_nota.setVisibility(View.VISIBLE);
                if (importancia_estado.equals("NO")){
                    importancia_estado="SI";
                    ivw_importancia.setVisibility(View.VISIBLE);

                }else if(importancia_estado.equals("SI")){
                    importancia_estado="NO";
                    ivw_importancia.setVisibility(View.INVISIBLE);
                }
            }
        });

        cambiar_color_nota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar_nota.setVisibility(View.VISIBLE);
                posicion_color=posicion_color+1;
                valor_color=color_nota();
            }
        });

        //Fecha
        cambiar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FechaSelector fechaSelector=new FechaSelector(CreateStickNoteActivity.this,CreateStickNoteActivity.this,"createAct");
                fechaSelector.adaptarFecha(t_fecha,t_hora,layout_repetiralarma,repeticion_tiempo,intervalo_tiempo);
                guardar_nota.setVisibility(View.VISIBLE);

                /*calendar=Calendar.getInstance();
                sminuto=calendar.get(Calendar.MINUTE);
                shora=calendar.get(Calendar.HOUR_OF_DAY);
                sdia=calendar.get(Calendar.DAY_OF_MONTH);
                smes=calendar.get(Calendar.MONTH);
                saño=calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog= new DatePickerDialog(CreateStickNoteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        TimePickerDialog timePickerDialog= new TimePickerDialog(CreateStickNoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                String diaStr = dayOfMonth < 10 ? "0"+dayOfMonth : String.valueOf(dayOfMonth);
                                String mesStr = (month+1) < 10 ? "0"+(month+1) : String.valueOf((month+1));
                                String horaStr = hourOfDay < 10 ? "0"+hourOfDay : String.valueOf(hourOfDay);
                                String minutoStr = minute < 10 ? "0"+minute : String.valueOf(minute);

                                t_hora.setText(horaStr+":"+minutoStr);
                                t_fecha.setText(diaStr+"/"+mesStr+"/"+year);
                                Toast.makeText(getApplicationContext(), "Se cambió la fecha",Toast.LENGTH_SHORT).show();
                            }
                        },shora,sminuto,false);
                        timePickerDialog.show();
                    }
                },saño,smes,sdia);
                datePickerDialog.show();*/

            }
        });

        compartir_nota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(t_fecha.getText().toString().equals("")
                        || t_hora.getText().toString().equals("")
                        || titulo_nota.getText().toString().trim().equals("")){
                    Toast.makeText(CreateStickNoteActivity.this,"Debe colocar al menos un título y una fecha",Toast.LENGTH_SHORT).show();
                }
                else{
                    compartirNota();
                }
            }
        });

        //Leer la información que llega desde mensajes
        if(getIntent().hasExtra("ORIGEN")){
            if (getIntent().getStringExtra("ORIGEN").equals("mensajes")){
                aceptarRecordatorio();
            }
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

                        PersonasDialog=new Dialog(CreateStickNoteActivity.this);
                        PersonasDialog.setContentView(R.layout.personas_dialog);
                        DescargarPersonas descargarPersonas=new DescargarPersonas(CreateStickNoteActivity.this,CreateStickNoteActivity.this);
                        descargarPersonas.actualizar(PersonasDialog,"createAct");

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
            Toast.makeText(CreateStickNoteActivity.this,"Sin conexión a internet para compartir", Toast.LENGTH_SHORT).show();
        }




    }

    private void mostrarPersonas(){
        lista_personas.setContentView(R.layout.lista_personas);
        recyclerView=lista_personas.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CreateStickNoteActivity.this));

        mUsers=new ArrayList<>();

        MyDataBasePersonas myDataBasePersonas=new MyDataBasePersonas(CreateStickNoteActivity.this);
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

        datosNota[0]=titulo_nota.getText().toString().trim();
        datosNota[1]=contenido_nota.getText().toString().trim();
        datosNota[2]=""+valor_color;
        datosNota[3]=t_fecha.getText().toString();
        datosNota[4]=t_hora.getText().toString();
        datosNota[5]=importancia_estado;

        userAdapter=new UserAdapter(CreateStickNoteActivity.this,mUsers,"createnote",datosNota);
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
                stick_note.getBackground().setTint(getColor(R.color.Nota1));
                window.setStatusBarColor(getColor(R.color.Nota1));
                break;
            case 1:
                stick_note.getBackground().setTint(getColor(R.color.Nota2));
                window.setStatusBarColor(getColor(R.color.Nota2));
                break;
            case 2:
                stick_note.getBackground().setTint(getColor(R.color.Nota3));
                window.setStatusBarColor(getColor(R.color.Nota3));
                break;
            case 3:
                stick_note.getBackground().setTint(getColor(R.color.Nota4));
                window.setStatusBarColor(getColor(R.color.Nota4));
                break;
        }
        return valor;
    }

    public void aceptarRecordatorio(){
        String mensaje="";
        String titulo="";String contenido="";String color="";
        String fecha="";String hora="";String importancia="";
        String tiemporep,intervalorep="";

        //Obtener información
        guardar_nota.setVisibility(View.VISIBLE);

        mensaje=getIntent().getStringExtra("NUEVOREC");
        origen=getIntent().getStringExtra("ORIGEN");


        titulo=mensaje.substring(1,mensaje.indexOf('^'));
        contenido=mensaje.substring(mensaje.indexOf('^')+1,mensaje.lastIndexOf('¬'));
        color=mensaje.substring(mensaje.lastIndexOf('¬')+1,mensaje.lastIndexOf('?'));
        fecha=mensaje.substring(mensaje.lastIndexOf('?')+1,mensaje.lastIndexOf('{'));
        hora=mensaje.substring(mensaje.lastIndexOf('{')+1,mensaje.lastIndexOf('}'));
        importancia=mensaje.substring(mensaje.lastIndexOf('}')+1,mensaje.lastIndexOf('~'));
        serepite=mensaje.substring(mensaje.lastIndexOf('~')+1,mensaje.lastIndexOf('$'));
        tiemporep=mensaje.substring(mensaje.lastIndexOf('$')+1,mensaje.lastIndexOf('&'));
        intervalorep=mensaje.substring(mensaje.lastIndexOf('&')+1,mensaje.length());

        Log.d("TAG902","Mensaje Button: " +titulo+'@'+contenido+'@'+color+'@'+fecha+'@'+hora+'@'+importancia);

        titulo_nota.setText(titulo);
        contenido_nota.setText(contenido);
        valor_color=Integer.parseInt(color); posicion_color=valor_color;

        t_fecha.setText(fecha);
        t_hora.setText(hora);

        if(serepite.equals("SI")){
            layout_repetiralarma.setVisibility(View.VISIBLE);
        }
        else{
            layout_repetiralarma.setVisibility(View.GONE);
        }
        repeticion_tiempo.setText(tiemporep);repeticion_tiempo.setVisibility(View.GONE);
        intervalo_tiempo.setText(intervalorep);intervalo_tiempo.setVisibility(View.GONE);

        importancia_estado=importancia;


        //Colocar la información en el stick note
        switch (valor_color){
            case 0:
                stick_note.getBackground().setTint(getColor(R.color.Nota1));
                valor_color=0;
                posicion_color=0;
                window.setStatusBarColor(getColor(R.color.Nota1));
                //window.setStatusBarColor(Color.parseColor(Nota1));
                break;
            case 1:
                stick_note.getBackground().setTint(getColor(R.color.Nota2));
                valor_color=1;
                posicion_color=1;
                window.setStatusBarColor(getColor(R.color.Nota2));
                //window.setStatusBarColor(Color.parseColor(Nota2));
                break;
            case 2:
                stick_note.getBackground().setTint(getColor(R.color.Nota3));
                valor_color=2;
                posicion_color=2;
                window.setStatusBarColor(getColor(R.color.Nota3));
                //window.setStatusBarColor(Color.parseColor(Nota3));
                break;
            case 3:
                stick_note.getBackground().setTint(getColor(R.color.Nota4));
                valor_color=3;
                posicion_color=3;
                window.setStatusBarColor(getColor(R.color.Nota4));
                //window.setStatusBarColor(Color.parseColor(Nota4));
                break;
        }
        if(importancia_estado.equals("SI")){
            ivw_importancia.setVisibility(View.VISIBLE);
        }
        else if (importancia_estado.equals("NO")){
            ivw_importancia.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!origen.equals("mensajes")){
            Intent intent = new Intent(CreateStickNoteActivity.this,RecordatoriosActivity.class);
            startActivity(intent);
        }
        finish();

    }
}