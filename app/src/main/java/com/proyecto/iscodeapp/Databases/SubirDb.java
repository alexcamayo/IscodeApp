package com.proyecto.iscodeapp.Databases;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;

public class SubirDb {

    Activity activity;
    Context context;
    String path_db="";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;



    public SubirDb(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public void actualizardb(){

        //Guardar cosas en firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();

        SharedPreferences preferences_archivos = activity.getSharedPreferences("ARCHIVOS_USUARIO", Context.MODE_PRIVATE);
        path_db=preferences_archivos.getString("PathDB","");

        String id_usuario = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference();

        Uri file = Uri.fromFile(new File(path_db));
        StorageReference riversRef = storageReference.child(id_usuario).child("ArchivosUsuario/"+file.getLastPathSegment());
        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("TAG560","éxito");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG560","falló");
            }
        });
    }
}
