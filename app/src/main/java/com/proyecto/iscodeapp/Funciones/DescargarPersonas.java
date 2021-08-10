package com.proyecto.iscodeapp.Funciones;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.DialogCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.proyecto.iscodeapp.Databases.MyDataBasePersonas;
import com.proyecto.iscodeapp.NotificacionesActivity;
import com.proyecto.iscodeapp.RegistrarActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DescargarPersonas {
    Activity activity;
    Context context;

    int cantidadusuarios=0;
    SharedPreferences preferences_personas;
    SharedPreferences preferences_archivos;

    String[] strids;
    String[] strnombres;
    String[] strcorreos;
    String[] strfechas;
    String[] strurls;

    String[] strhayfotos;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;

    public DescargarPersonas(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public void actualizar(Dialog Personas, String origen){

        Personas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Personas.setCanceledOnTouchOutside(false);
        Personas.show();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();

        preferences_personas = activity.getSharedPreferences("PERSONAS_DATOS", Context.MODE_PRIVATE);
        preferences_archivos = activity.getSharedPreferences("ARCHIVOS_USUARIO", Context.MODE_PRIVATE);

        database.child("InfoGeneral").child("CantidadUsuarios").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context,"Error al actualizar personas",Toast.LENGTH_SHORT).show();
                }
                else {
                    cantidadusuarios=Integer.parseInt(task.getResult().getValue().toString());
                    preferences_archivos.edit().putString("CantidadUsuarios",String.valueOf(cantidadusuarios)).apply();

                    strids= new String[cantidadusuarios-1]; //Initialization after declaration with specific size
                    strnombres= new String[cantidadusuarios-1]; //Initialization after declaration with specific size
                    strcorreos= new String[cantidadusuarios-1]; //Initialization after declaration with specific size
                    strfechas= new String[cantidadusuarios-1]; //Initialization after declaration with specific size
                    strurls= new String[cantidadusuarios-1]; //Initialization after declaration with specific size

                    strhayfotos= new String[cantidadusuarios-1]; //Initialization after declaration with specific size

                    Log.d("TAG324","ACT "+cantidadusuarios);

                    descargarUsuarios(Personas,origen);
                }
            }
        });
    }

    private void descargarUsuarios(Dialog Personas, String origen) {
        //inicializamos el objeto firebaseAuth, los textos y los botones
        int[] cont={0};
        String id_usuario = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        database.child("Usuarios").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context,"Error al actualizar personas",Toast.LENGTH_SHORT).show();
                }
                else {
                    for(DataSnapshot snapshot:task.getResult().getChildren()){
                        String personasid=snapshot.child("InfoUsuario").child("ID").getValue().toString();

                        Log.d("TAG325","IDS "+personasid);

                        if (!personasid.equals(id_usuario)){
                            String nombre=snapshot.child("InfoUsuario").child("Nombre").getValue().toString();
                            String HayFoto=snapshot.child("InfoArchivos").child("HayFoto").getValue().toString();
                            String correo=snapshot.child("InfoUsuario").child("Correo").getValue().toString();
                            String fecha=snapshot.child("InfoUsuario").child("Fecha").getValue().toString();

                            strids[cont[0]]=personasid;
                            strnombres[cont[0]]=nombre;
                            strcorreos[cont[0]]=correo;
                            strfechas[cont[0]]=fecha;
                            strurls[cont[0]]="default";


                            strhayfotos[cont[0]]=HayFoto;

                            cont[0]=cont[0]+1;
                        }
                    }

                    revisarFoto(Personas,origen);
                }
            }
        });
    }

    public void revisarFoto(Dialog Personas,String origen){

        int [] contA={0};
        int [] contB={0};

        boolean hayAlgunaFoto=false;

        for (int i=0;i<cantidadusuarios-1;i++){
            if (strhayfotos[i].equals("SI")) {
                hayAlgunaFoto = true;
                break;
            }
        }

        if (hayAlgunaFoto){
            for (int i=0;i<cantidadusuarios-1;i++){
                if(strhayfotos[i].equals("SI")){

                    File carpeta_ch = new File("/data/data/com.proyecto.iscodeapp/cache");
                    if(!carpeta_ch.exists()) {
                        if(carpeta_ch.mkdir())
                            Log.d("tag907","Creada");
                    }
                    else {
                        Log.d("tag907", "Existe");
                    }

                    File path=new File("/data/data/com.proyecto.iscodeapp/cache/");
                    File localFile = null;
                    try {
                        localFile = File.createTempFile("fotopersonas", ".jpeg",path);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert localFile != null;

                    File name_foto=new File("/data/data/com.proyecto.iscodeapp/cache/fotopersonas"+strids[i]+".jpeg");
                    localFile.renameTo(name_foto);

                    strurls[i]=name_foto.getAbsolutePath();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference gsReference = storage.getReferenceFromUrl("gs://app-iscode.appspot.com/"+strids[i]+"/FotoUsuario/fotoperfil.jpeg");
                    File fotopersonas=new File(name_foto.getAbsolutePath());

                    gsReference.getFile(fotopersonas).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            contB[0]=contB[0]+1;

                            if(contA[0]==contB[0]){
                                Log.d("tag435","Finalizado");
                                goNotificaciones(Personas,origen);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                        }
                    });

                    contA[0]=contA[0]+1;
                }
            }
        }
        else{
            goNotificaciones(Personas, origen);
        }
    }

    public void goNotificaciones(Dialog Personas, String origen){

        MyDataBasePersonas myDataBasePersonas=new MyDataBasePersonas(context);
        myDataBasePersonas.clearTable();

        for(int i=0;i<cantidadusuarios-1;i++){
            //preferences_personas.edit().putString("userid"+i,strids[i]).apply();
            //preferences_personas.edit().putString("nombre"+i,strnombres[i]).apply();
            //preferences_personas.edit().putString("imageurl"+i,strurls[i]).apply();

            myDataBasePersonas.agregar_usuario(strids[i],strnombres[i],strcorreos[i],strfechas[i],strurls[i]);

            Log.d("tag435","ID: "+strids[i]);
            Log.d("tag435","urls: "+strcorreos[i]);
            Log.d("tag435","urls: "+strurls[i]);
        }

        database.child("InfoGeneral").child("RefreshPersonas").setValue("NO");

        if(origen.equals("mainAct")){
            Intent go_chatlist=new Intent(context, NotificacionesActivity.class);
            activity.startActivity(go_chatlist);
        }
        //activity.finish();
        preferences_archivos.edit().putString("PersonasDescargadas","SI").apply();
        Personas.dismiss();

    }



}
