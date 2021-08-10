package com.proyecto.iscodeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.proyecto.iscodeapp.Funciones.RevisarConexion;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class IngresarActivity extends AppCompatActivity {

    EditText correo,contraseña;
    Button ingresar,goRegistrar;
    ProgressBar pb_login;
    LottieAnimationView check_animation;
    Boolean isActivateRB;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;

    SharedPreferences preferences,preferences_archivos;
    String id_usuario="";
    String HayFoto="";
    String HayDb="";
    String cantidadUsuarios="";

    //Archivos
    File localFile,localFile_db,localFile_dbper;

    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar);

        RevisarConexion revisarConexion=new RevisarConexion(IngresarActivity.this);
        connected= revisarConexion.isConnected();

        //inicializamos el objeto firebaseAuth, los textos y los botones
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();

        //Shared preferences
        preferences = getSharedPreferences("DATOS_LOGIN", MODE_PRIVATE);
        preferences_archivos = getSharedPreferences("ARCHIVOS_USUARIO", MODE_PRIVATE);

        correo = findViewById(R.id.correo);
        contraseña = findViewById(R.id.contraseña);
        ingresar = findViewById(R.id.ingresar);
        goRegistrar = findViewById(R.id.goRegistrar);
        pb_login = findViewById(R.id.pb_login);
        check_animation = findViewById(R.id.check_animation);

        pb_login.setVisibility(View.GONE);
        check_animation.setVisibility(View.GONE);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(correo.getText().toString().trim())) {
                    Toast.makeText(IngresarActivity.this, "Se debe ingresar el correo", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(contraseña.getText().toString().trim())) {
                    Toast.makeText(IngresarActivity.this, "Se debe ingresar la contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    pb_login.setVisibility(View.VISIBLE);
                    check_animation.setVisibility(View.GONE);

                    revisarConexion();
                }


            }
        });

        goRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goReg = new Intent(IngresarActivity.this, RegistrarActivity.class);
                startActivity(goReg);
                finish();
            }
        });
    }

    private void revisarConexion(){

        if(connected){
            verificar();
        }
        else{
            Toast.makeText(IngresarActivity.this,"No hay conexión a internet",Toast.LENGTH_LONG).show();
            pb_login.setVisibility(View.GONE);
        }
    }

    private void verificar(){
        firebaseAuth.signInWithEmailAndPassword(correo.getText().toString().trim(),contraseña.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            id_usuario= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                            crearArchivos();
                        } else {
                            Toast.makeText(IngresarActivity.this, "Error al ingresar: ID 00", Toast.LENGTH_SHORT).show();
                            pb_login.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void crearArchivos(){
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
        assert localFile_dbper != null;

        RenombrarArchivos();
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

        loginpreferencias();

    }

    private void loginpreferencias(){
        preferences.edit().putString("PrimeraVezLogin","SI").apply();

        database.child("Usuarios").child(id_usuario).child("InfoUsuario").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(IngresarActivity.this,"Error al ingresar ID:01",Toast.LENGTH_SHORT).show();
                    pb_login.setVisibility(View.GONE);
                }
                else {
                    String nombre_usuario=task.getResult().child("Nombre").getValue().toString();
                    String correo_usuario=task.getResult().child("Correo").getValue().toString();
                    String fecha_usuario=task.getResult().child("Fecha").getValue().toString();

                    preferences.edit().putString("Nombre",nombre_usuario).apply();
                    preferences.edit().putString("Correo",correo_usuario).apply();
                    preferences.edit().putString("Fecha",fecha_usuario).apply();
                    preferences.edit().putString("ID",id_usuario).apply();
                    preferences.edit().putString("PrimeraVezSesion","SI").apply();

                    archivospreferencias();

                }
            }
        });
    }

    private void archivospreferencias(){
        database.child("Usuarios").child(id_usuario).child("InfoArchivos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(IngresarActivity.this,"Error al ingresar ID:02",Toast.LENGTH_SHORT).show();
                    pb_login.setVisibility(View.GONE);
                }
                else {
                    HayFoto=task.getResult().child("HayFoto").getValue().toString();
                    HayDb=task.getResult().child("HayDBrec").getValue().toString();
                    preferences_archivos.edit().putString("HayFoto",HayFoto).apply();
                    preferences_archivos.edit().putString("HayDBrec",HayDb).apply();
                    preferences_archivos.edit().putString("PrimeraVez","SI").apply();
                    preferences_archivos.edit().putString("Actualizar","NO").apply();
                    preferences_archivos.edit().putString("PersonasDescargadas","NO").apply();

                    descargarFoto();

                }
            }
        });
    }

    private void descargarFoto(){
        if (HayFoto.equals("SI")){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference gsReference = storage.getReferenceFromUrl("gs://app-iscode.appspot.com/"+id_usuario+"/FotoUsuario/fotoperfil.jpeg");
            File fotoimagen=new File("/data/data/com.proyecto.iscodeapp/cache/fotopersonas"+id_usuario+".jpeg");
            gsReference.getFile(fotoimagen).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    descargarDB();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(IngresarActivity.this,"Error al ingresar ID:03",Toast.LENGTH_SHORT).show();
                    pb_login.setVisibility(View.GONE);
                }
            });
        }
        else if (HayFoto.equals("NO")){
            descargarDB();
        }
    }

    private void descargarDB(){
        if (HayDb.equals("SI")){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference gsReference = storage.getReferenceFromUrl("gs://app-iscode.appspot.com/"+id_usuario+"/ArchivosUsuario/DATOS_USUARIO.db");
            File basedatos=new File("/data/data/com.proyecto.iscodeapp/databases/DATOS_USUARIO.db");
            gsReference.getFile(basedatos).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Iniciar();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(IngresarActivity.this,"Error al ingresar ID:04",Toast.LENGTH_SHORT).show();
                    pb_login.setVisibility(View.GONE);
                }
            });
        }
        else if(HayDb.equals("NO")){
            Iniciar();
        }
    }

    private void Iniciar(){
        pb_login.setVisibility(View.GONE);
        check_animation.setVisibility(View.VISIBLE);

        Intent goMain=new Intent(IngresarActivity.this,MainActivity.class);
        goMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        goMain.putExtra("ORIGEN","ingresar");
        startActivity(goMain);
        finish();
    }
}