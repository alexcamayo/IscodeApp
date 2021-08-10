package com.proyecto.iscodeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.DialogCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.proyecto.iscodeapp.Databases.MyDataBaseHelper;
import com.proyecto.iscodeapp.Funciones.DescargarPersonas;
import com.proyecto.iscodeapp.Funciones.RevisarConexion;
import com.proyecto.iscodeapp.Notificaciones.AlarmasRecordatorios;
import com.proyecto.iscodeapp.Pruebas.PruebaActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ImageButton perfil,notificacion,goPrediccion,goRecordatorios,goRegistrarFC,goRecomendaciones,goHistoricos,editar,guardar;

    Button cerrarsesion;
    ImageView fotoperfil;
    TextView correo,edad;
    EditText nombre;
    Dialog PerfilDialog,PersonasDialog;

    SharedPreferences preferences,preferences_archivos;
    String HayFoto="";
    String PathFoto="";

    Calendar calendar=Calendar.getInstance();

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    String id_usuario="";

    String PrimeraVezSesion="";

    boolean connected;

    Button prueba;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RevisarConexion revisarConexion=new RevisarConexion(MainActivity.this);
        connected=revisarConexion.isConnected();



        perfil=findViewById(R.id.perfil);
        notificacion=findViewById(R.id.notificacion);
        goHistoricos=findViewById(R.id.goHistoricos);
        goRecordatorios=findViewById(R.id.goRecordatorios);
        goRegistrarFC=findViewById(R.id.goRegistrarFC);
        goPrediccion=findViewById(R.id.goPrediccion);
        goRecomendaciones=findViewById(R.id.goRecomendaciones);
        prueba=findViewById(R.id.prueba);


        //inicializamos el objeto firebaseAuth, los textos y los botones
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();
        id_usuario= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        //Shared
        preferences=getSharedPreferences("DATOS_LOGIN",MODE_PRIVATE);
        preferences_archivos=getSharedPreferences("ARCHIVOS_USUARIO",MODE_PRIVATE);
        HayFoto=preferences_archivos.getString("HayFoto","");
        PathFoto=preferences_archivos.getString("PathFoto","");
        PrimeraVezSesion=preferences.getString("PrimeraVezSesion","");

        //Colocar foto
        if(HayFoto.equals("NO")) {
            perfil.setImageResource(R.drawable.icon_user);
        }
        else if(HayFoto.equals("SI")){
            Uri fotoUri=Uri.parse(PathFoto);
            perfil.setImageURI(fotoUri);
        }
        else{
            perfil.setImageResource(R.drawable.icon_user_error);
        }

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showperfil();
            }
        });

        notificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarPersonas();
            }
        });

        notificacion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                 Vibrator vibrator =(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                 vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                 Toast.makeText(MainActivity.this,"Mensajes",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        goPrediccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goPred=new Intent(MainActivity.this,HacerPrediccionActivity.class);
                startActivity(goPred);
            }
        });

        goRecordatorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goRec=new Intent(MainActivity.this,RecordatoriosActivity.class);
                startActivity(goRec);
            }
        });

        goRegistrarFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBlue=new Intent(MainActivity.this,BluetoothActivity.class);
                startActivity(goBlue);
            }
        });

        goRecomendaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goRecom=new Intent(MainActivity.this,RecomendacionesActivity.class);
                startActivity(goRecom);
            }
        });

        goHistoricos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goHis=new Intent(MainActivity.this,HistoricosActivity.class);
                startActivity(goHis);
            }
        });

        prueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goPrueba=new Intent(MainActivity.this, PruebaActivity.class);
                startActivity(goPrueba);
            }
        });prueba.setVisibility(View.GONE);

        subirToken();

    }

    /*private void mostrarPerfil(){
        PerfilDialog=new BottomSheetDialog(MainActivity.this,R.style.BottomSheetDialog);
        View perfilview= LayoutInflater.from(getApplicationContext()).inflate(R.layout.perfil_dialog, findViewById(R.id.contenedorperfil));

        cerrarsesion=perfilview.findViewById(R.id.cerrarsesion);

        cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent goSplash=new Intent(MainActivity.this,SplashScreen.class);
                goSplash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(goSplash);
                finish();
            }
        });

        PerfilDialog.setContentView(perfilview);
        PerfilDialog.show();


    }*/

    public void showperfil(){
        PerfilDialog=new Dialog(this);
        PerfilDialog.setContentView(R.layout.perfil_dialog);

        fotoperfil=PerfilDialog.findViewById(R.id.fotoperfil);
        nombre=PerfilDialog.findViewById(R.id.nombre);
        correo=PerfilDialog.findViewById(R.id.correo);
        edad=PerfilDialog.findViewById(R.id.edad);
        cerrarsesion=PerfilDialog.findViewById(R.id.cerrarsesion);
        editar=PerfilDialog.findViewById(R.id.editar);
        guardar=PerfilDialog.findViewById(R.id.guardar);

        guardar.setVisibility(View.GONE);
        editar.setVisibility(View.VISIBLE);

        nombre.setFocusable(false);
        nombre.setEnabled(false);
        nombre.setCursorVisible(false);

        nombre.setText(preferences.getString("Nombre",""));
        correo.setText(preferences.getString("Correo",""));

        String nacimiento=preferences.getString("Fecha","");
        int dia_fecha=Integer.parseInt(nacimiento.substring(0,nacimiento.indexOf('/')));
        int mes_fecha=Integer.parseInt(nacimiento.substring(nacimiento.indexOf('/')+1,nacimiento.lastIndexOf('/')));
        int año_fecha=Integer.parseInt(nacimiento.substring(nacimiento.lastIndexOf('/')+1,nacimiento.length()));
        LocalDate birthday = LocalDate.of(año_fecha, mes_fecha, dia_fecha);
        LocalDate now = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        Period period = Period.between(birthday, now);

        edad.setText(period.getYears() + " años");


        PerfilDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Perfil.setCanceledOnTouchOutside(false);
        PerfilDialog.show();


        //Colocar foto
        if(HayFoto.equals("NO")) {
            fotoperfil.setImageResource(R.drawable.icon_user);
        }
        else if(HayFoto.equals("SI")){
            Uri fotoUri=Uri.parse(PathFoto);
            fotoperfil.setImageURI(fotoUri);
        }
        else{
            fotoperfil.setImageResource(R.drawable.icon_user_error);
        }

        cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("CERRAR SESIÓN");
                builder.setMessage("¿Está seguro que desea salir?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (!task.isSuccessful()) {

                                }
                                else{
                                    FirebaseAuth.getInstance().signOut();
                                    borrar_todos_recordatorios();
                                    Intent goSplash=new Intent(MainActivity.this,SplashScreen.class);
                                    goSplash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(goSplash);
                                    finish();
                                    PerfilDialog.dismiss();
                                    Toast.makeText(MainActivity.this,"¡Adiós!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();

            }
        });

        fotoperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goFoto=new Intent(MainActivity.this,ShowImageActivity.class);
                startActivity(goFoto);
            }
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editar.setVisibility(View.GONE);
                guardar.setVisibility(View.VISIBLE);

                nombre.setFocusable(true);
                nombre.setEnabled(true);
                nombre.setCursorVisible(true);
                nombre.setFocusableInTouchMode(true);
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (connected){
                    database.child("Usuarios").child(id_usuario).child("InfoUsuario").child("Nombre").setValue(nombre.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            preferences.edit().putString("Nombre",nombre.getText().toString().trim()).apply();
                            database.child("InfoGeneral").child("RefreshPersonas").setValue("SI");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(MainActivity.this,"Error al guardar la información",Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this,"Sin conexión a internet para actulizar el nombre",Toast.LENGTH_SHORT).show();
                }



                editar.setVisibility(View.VISIBLE);
                guardar.setVisibility(View.GONE);
                nombre.setFocusable(false);
                nombre.setEnabled(false);
                nombre.setCursorVisible(false);
            }
        });

    }

    private void actualizarPersonas(){
        if(connected){
            database.child("InfoGeneral").child("RefreshPersonas").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        //Error
                    }
                    else {
                        if (task.getResult().getValue().toString().equals("SI") || PrimeraVezSesion.equals("SI")){

                            preferences.edit().putString("PrimeraVezSesion","NO").apply();
                            PrimeraVezSesion="NO";

                            PersonasDialog=new Dialog(MainActivity.this);
                            PersonasDialog.setContentView(R.layout.personas_dialog);
                            DescargarPersonas descargarPersonas=new DescargarPersonas(MainActivity.this,MainActivity.this);
                            descargarPersonas.actualizar(PersonasDialog,"mainAct");
                        }
                        else if(task.getResult().getValue().toString().equals("NO")){
                            Intent goNoti=new Intent(MainActivity.this, NotificacionesActivity.class);
                            startActivity(goNoti);
                        }

                    }
                }
            });
        }
        else {
            Toast.makeText(MainActivity.this,"Sin conexión a internet para actulizar mensajes",Toast.LENGTH_SHORT).show();
            Intent goNoti=new Intent(MainActivity.this, NotificacionesActivity.class);
            startActivity(goNoti);
        }


    }

    private void subirToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("TAG676", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        database.child("Tokens").child(id_usuario).child("Token").setValue(token);

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TAG676", msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void borrar_todos_recordatorios(){
        String hayDb=preferences_archivos.getString("HayDBrec","");

        if (hayDb.equals("SI")){
            MyDataBaseHelper myDb=new MyDataBaseHelper(MainActivity.this);
            Cursor cursor=myDb.readAllData();
            if(cursor.getCount()==0){

            }
            else{
                AlarmasRecordatorios alarmasRecordatorios=new AlarmasRecordatorios(getApplicationContext(),MainActivity.this);

                while (cursor.moveToNext()){
                    String ID=cursor.getString(0);
                    alarmasRecordatorios.borrarAlarma("mainAct",ID);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }
}