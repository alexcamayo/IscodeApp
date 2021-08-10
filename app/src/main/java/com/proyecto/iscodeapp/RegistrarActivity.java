package com.proyecto.iscodeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyecto.iscodeapp.Funciones.RevisarConexion;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistrarActivity extends AppCompatActivity {

    Button registrar,goIngresar;
    EditText nombre,correo,contraseña1,contraseña2,fecha;
    ProgressBar pb_signup;
    LottieAnimationView check_animation;
    ImageButton sel_fecha;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    String id_usuario="";

    private int dia, mes,año;
    Calendar calendar=Calendar.getInstance();

    SharedPreferences preferences,preferences_archivos;

    String cantidadUsuarios="";
    int nuevaCantidad=0;

    //Archivos
    File localFile,localFile_db,localFile_dbper;

    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        RevisarConexion revisarConexion=new RevisarConexion(RegistrarActivity.this);
        connected= revisarConexion.isConnected();

        //inicializamos el objeto firebaseAuth, los textos y los botones
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();



        Log.d("TAG975", firebaseAuth.toString());
        Log.d("TAG975", database.toString());

        //Preferencias
        preferences = getSharedPreferences("DATOS_LOGIN", MODE_PRIVATE);
        preferences_archivos = getSharedPreferences("ARCHIVOS_USUARIO", MODE_PRIVATE);

        registrar = findViewById(R.id.registrar);
        goIngresar = findViewById(R.id.goIngresar);
        nombre = findViewById(R.id.nombre);
        correo = findViewById(R.id.correo);
        contraseña1 = findViewById(R.id.contraseña1);
        contraseña2 = findViewById(R.id.contraseña2);
        fecha = findViewById(R.id.fecha);
        pb_signup = findViewById(R.id.pb_signup);
        check_animation = findViewById(R.id.check_animation);
        sel_fecha = findViewById(R.id.sel_fecha);

        pb_signup.setVisibility(View.GONE);
        check_animation.setVisibility(View.GONE);

        //Fecha
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH);
        año = calendar.get(Calendar.YEAR);
        sel_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrarActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, año, mes, dia);
                datePickerDialog.show();
            }
        });


        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(nombre.getText().toString().trim())) {
                    Toast.makeText(RegistrarActivity.this, "Se debe ingresar el nombre", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(correo.getText().toString().trim())) {
                    Toast.makeText(RegistrarActivity.this, "Se debe ingresar el correo", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(contraseña1.getText().toString().trim()) || TextUtils.isEmpty(contraseña2.getText().toString().trim())) {
                    Toast.makeText(RegistrarActivity.this, "Se debe ingresar la contraseña", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(fecha.getText().toString().trim())) {
                    Toast.makeText(RegistrarActivity.this, "Se debe ingresar la fecha", Toast.LENGTH_SHORT).show();
                } else if (!contraseña1.getText().toString().trim().equals(contraseña2.getText().toString().trim())) {
                    Toast.makeText(RegistrarActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                } else {
                    pb_signup.setVisibility(View.VISIBLE);
                    check_animation.setVisibility(View.GONE);

                    revisarConexion();
                }
            }
        });

        goIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goIng = new Intent(RegistrarActivity.this, IngresarActivity.class);
                startActivity(goIng);
                finish();
            }
        });
    }


    private void revisarConexion(){
        if(connected){
            crearArchivos();
        }
        else{
            Toast.makeText(RegistrarActivity.this,"No hay conexión a internet",Toast.LENGTH_LONG).show();
        }
    }

    public void crearArchivos(){
        //Crear archivo de foto

        File carpeta_db = new File("/data/data/com.proyecto.iscodeapp/databases");
        //comprobar si la carpeta no existe, entonces crearla
        if(!carpeta_db.exists()) {
            //carpeta.mkdir() creará la carpeta en la ruta indicada al inicializar el objeto File
            if(carpeta_db.mkdir())
                //se ha creado la carpeta;
                Log.d("tag907","Creada");
        }
        else {
            //la carpeta ya existe
            Log.d("tag907", "Existe");
        }

        File carpeta_ch = new File("/data/data/com.proyecto.iscodeapp/cache");
        //comprobar si la carpeta no existe, entonces crearla
        if(!carpeta_ch.exists()) {
            //carpeta.mkdir() creará la carpeta en la ruta indicada al inicializar el objeto File
            if(carpeta_ch.mkdir())
                //se ha creado la carpeta;
                Log.d("tag907","Creada");
        }
        else {
            //la carpeta ya existe
            Log.d("tag907", "Existe");
        }


        File path=new File("/data/data/com.proyecto.iscodeapp/cache/");
        localFile = null;
        try {
            localFile = File.createTempFile("fotopersonas", ".jpeg",path);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //Crear database
        File path_db=new File("/data/data/com.proyecto.iscodeapp/databases/");
        localFile_db = null;
        try {
            localFile_db = File.createTempFile("DATOS_USUARIO", ".db",path_db);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //Crear database personas
        File path_dbper=new File("/data/data/com.proyecto.iscodeapp/databases/");
        localFile_dbper = null;
        try {
            localFile_dbper = File.createTempFile("DATOS_PERSONAS", ".db",path_dbper);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        assert localFile != null;
        assert localFile_db != null;
        assert localFile_dbper !=null;

        verificar();
    }

    private void verificar(){

        firebaseAuth.createUserWithEmailAndPassword(correo.getText().toString().trim(), contraseña1.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            id_usuario = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                            RenombrarArchivos();
                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {//si se presenta una colisión
                                Toast.makeText(RegistrarActivity.this, "Ese usuario ya existe", Toast.LENGTH_SHORT).show();
                                pb_signup.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(RegistrarActivity.this, "No se pudo registrar el usuario: ID 00", Toast.LENGTH_SHORT).show();
                                pb_signup.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    private void RenombrarArchivos(){
        File name_db=new File("/data/data/com.proyecto.iscodeapp/databases/DATOS_USUARIO.db");
        File name_dbper=new File("/data/data/com.proyecto.iscodeapp/databases/DATOS_PERSONAS.db");
        File name_foto=new File("/data/data/com.proyecto.iscodeapp/cache/fotopersonas"+id_usuario+".jpeg");
        localFile_db.renameTo(name_db);
        localFile_dbper.renameTo(name_dbper);
        localFile.renameTo(name_foto);

        preferences_archivos.edit().putString("PathFoto",name_foto.getAbsolutePath()).apply();
        preferences_archivos.edit().putString("PathDB",name_db.getAbsolutePath()).apply();
        preferences_archivos.edit().putString("PathDBPer",name_dbper.getAbsolutePath()).apply();

        infoGeneral();
    }

    public void infoGeneral(){
        database.child("InfoGeneral").child("RefreshPersonas").setValue("SI").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {

                usuarioPreferencias();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(RegistrarActivity.this, "Error al registrar este usuario ID:03", Toast.LENGTH_SHORT).show();
                pb_signup.setVisibility(View.GONE);
            }
        });
    }

    public void usuarioPreferencias(){
        Map<String,Object> map= new HashMap<>();
        map.put("Nombre",nombre.getText().toString().trim());
        map.put("Correo",correo.getText().toString().trim());
        map.put("Fecha",fecha.getText().toString().trim());
        map.put("ID",id_usuario);

        database.child("Usuarios").child(id_usuario).child("InfoUsuario").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                preferences.edit().putString("Nombre",nombre.getText().toString().trim()).apply();
                preferences.edit().putString("Correo",correo.getText().toString().trim()).apply();
                preferences.edit().putString("Fecha",fecha.getText().toString().trim()).apply();
                preferences.edit().putString("ID",id_usuario).apply();
                preferences.edit().putString("PrimeraVezSesion","SI").apply();

                archivosPreferencias();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(RegistrarActivity.this, "Error al registrar este usuario ID:04", Toast.LENGTH_SHORT).show();
                pb_signup.setVisibility(View.GONE);
            }
        });
    }

    public void archivosPreferencias(){
        Map<String,Object> map= new HashMap<>();
        map.put("HayFoto","NO");
        map.put("HayDBrec","NO");

        database.child("Usuarios").child(id_usuario).child("InfoArchivos").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                preferences_archivos.edit().putString("HayFoto","NO").apply();
                preferences_archivos.edit().putString("HayDBrec","NO").apply();
                preferences_archivos.edit().putString("PrimeraVez","SI").apply();
                preferences_archivos.edit().putString("Actualizar","NO").apply();
                preferences_archivos.edit().putString("PersonasDescargadas","NO").apply();

                aumentarUsuarios();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(RegistrarActivity.this, "Error al registrar este usuario ID:05", Toast.LENGTH_SHORT).show();
                pb_signup.setVisibility(View.GONE);
            }
        });
    }



    private void aumentarUsuarios(){
        database.child("InfoGeneral").child("CantidadUsuarios").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(RegistrarActivity.this,"Error al ingresar ID:06",Toast.LENGTH_SHORT).show();
                    pb_signup.setVisibility(View.GONE);
                }
                else {
                    cantidadUsuarios=task.getResult().getValue().toString();
                    nuevaCantidad=Integer.parseInt(cantidadUsuarios)+1;

                    database.child("InfoGeneral").child("CantidadUsuarios").setValue(String.valueOf(nuevaCantidad)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            RegistrarUsuario();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(RegistrarActivity.this,"Error al ingresar ID:07",Toast.LENGTH_SHORT).show();
                            pb_signup.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

    private void RegistrarUsuario(){
        pb_signup.setVisibility(View.GONE);
        check_animation.setVisibility(View.VISIBLE);

        Intent goMain=new Intent(RegistrarActivity.this,MainActivity.class);
        goMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        goMain.putExtra("ORIGEN","registrar");
        startActivity(goMain);
        finish();
    }
}