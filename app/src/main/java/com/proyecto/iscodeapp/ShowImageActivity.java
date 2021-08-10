package com.proyecto.iscodeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.proyecto.iscodeapp.Funciones.RevisarConexion;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zolad.zoominimageview.ZoomInImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class ShowImageActivity extends AppCompatActivity {

    ZoomInImageView foto;
    String PathFoto;
    ImageButton opciones_foto_btn;
    ProgressBar pb_cargarfotoperfil;

    File imagen_resultante;
    String HayFoto="";
    ImageView default_foto;
    RelativeLayout foto_backgorund_layout;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;

    ActionBar actionBar;

    String white="FFFFFF";
    String black="000000";

    Boolean cambioFoto=false;

    boolean connected;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        RevisarConexion revisarConexion=new RevisarConexion(ShowImageActivity.this);
        connected=revisarConexion.isConnected();


        actionBar= getSupportActionBar();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        foto_backgorund_layout=findViewById(R.id.foto_backgorund_layout);
        foto=findViewById(R.id.foto_fullscreen);
        default_foto=findViewById(R.id.default_foto);
        default_foto.setVisibility(View.GONE);
        pb_cargarfotoperfil=findViewById(R.id.pb_cargandofoto);
        pb_cargarfotoperfil.setVisibility(View.INVISIBLE);

        //Guardar cosas en firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();

        SharedPreferences preferences_archivos = getSharedPreferences("ARCHIVOS_USUARIO",MODE_PRIVATE);
        PathFoto=preferences_archivos.getString("PathFoto","");
        HayFoto= preferences_archivos.getString("HayFoto","Nada");
        if (HayFoto.equals("SI")){
            Uri foto_uri=Uri.parse(PathFoto);
            foto.setImageURI(foto_uri);
        }
        else{
            foto_backgorund_layout.getBackground().setTint(getColor(R.color.white));
            default_foto.setVisibility(View.VISIBLE);
            actionBar.hide();
            default_foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CropImage.startPickImageActivity(ShowImageActivity.this);
                }
            });
        }

        if(getIntent().hasExtra("ORIGEN")){
            if(getIntent().getStringExtra("ORIGEN").equals("notificaciones")){
                String id=getIntent().getStringExtra("ID");
                actionBar.hide();
                default_foto.setVisibility(View.GONE);
                pb_cargarfotoperfil.setVisibility(View.GONE);
                Uri fotoUri=Uri.parse("/data/data/com.proyecto.iscodeapp/cache/fotopersonas"+id+".jpeg");
                foto.setImageURI(fotoUri);
                foto_backgorund_layout.getBackground().setTint(getColor(R.color.black));

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_foto,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.subir_foto:
                if(connected){
                    CropImage.startPickImageActivity(ShowImageActivity.this);
                }
                else{
                    Toast.makeText(ShowImageActivity.this,"Sin conexión a internet para actulizar la foto",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.eliminar_foto:
                if(connected){
                    eliminarfoto();
                }
                else{
                    Toast.makeText(ShowImageActivity.this,"Sin conexión a internet para eliminar la foto",Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            Uri imageuri=CropImage.getPickImageResultUri(this,data);

            //Recortar imagen
            CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON).setRequestedSize(800,800)
                    .setAspectRatio(2,2).start(ShowImageActivity.this);

        }

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            pb_cargarfotoperfil.setVisibility(View.VISIBLE);
            foto.setVisibility(View.INVISIBLE);
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){

                default_foto.setVisibility(View.GONE);
                foto_backgorund_layout.getBackground().setTint(getColor(R.color.black));

                Uri uri=result.getUri();
                Log.d("tag400",uri.getPath());

                imagen_resultante=new File(uri.getPath());

                String id_usuario = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                StorageReference storageReference= FirebaseStorage.getInstance().getReference();
                StorageReference filepath=storageReference.child(id_usuario).child("FotoUsuario/fotoperfil.jpeg");

                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference gsReference = storage.getReferenceFromUrl("gs://app-iscode.appspot.com/"+id_usuario+"/FotoUsuario/fotoperfil.jpeg");
                        File fotoimagen=new File(PathFoto);
                        gsReference.getFile(fotoimagen).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                database.child("Usuarios").child(id_usuario).child("InfoArchivos").child("HayFoto").setValue("SI");
                                database.child("InfoGeneral").child("RefreshPersonas").setValue("SI");

                                SharedPreferences preferences = getSharedPreferences("ARCHIVOS_USUARIO", Context.MODE_PRIVATE);
                                preferences.edit().putString("HayFoto","SI").apply();

                                foto.setVisibility(View.VISIBLE);
                                pb_cargarfotoperfil.setVisibility(View.INVISIBLE);

                                foto.setImageURI(uri);
                                imagen_resultante.delete();

                                default_foto.setVisibility(View.GONE);
                                foto_backgorund_layout.getBackground().setTint(getColor(R.color.black));

                                cambioFoto=true;
                                actionBar.show();

                                Toast.makeText(getApplicationContext(), "Se actualizó con éxito la foto",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                foto.setVisibility(View.GONE);
                                pb_cargarfotoperfil.setVisibility(View.INVISIBLE);
                                default_foto.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Error al actualizar la foto ID:00",Toast.LENGTH_SHORT).show();
                            }
                        });



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        foto.setVisibility(View.GONE);
                        pb_cargarfotoperfil.setVisibility(View.INVISIBLE);
                        default_foto.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Error al actualizar la foto ID:01",Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }

        Log.d("TAG410","Request: "+requestCode+" Result: "+resultCode);
        if(requestCode==203 && resultCode==0){
            pb_cargarfotoperfil.setVisibility(View.INVISIBLE);
            foto.setVisibility(View.VISIBLE);
        }

    }

    public void eliminarfoto(){
        pb_cargarfotoperfil.setVisibility(View.VISIBLE);
        foto.setVisibility(View.INVISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String id_usuario = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://app-iscode.appspot.com/"+id_usuario+"/FotoUsuario/fotoperfil.jpeg");
        gsReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                database.child("Usuarios").child(id_usuario).child("InfoArchivos").child("HayFoto").setValue("NO");
                database.child("InfoGeneral").child("RefreshPersonas").setValue("SI");
                SharedPreferences preferences_archivos = getSharedPreferences("ARCHIVOS_USUARIO",MODE_PRIVATE);
                preferences_archivos.edit().putString("HayFoto","NO").apply();

                foto.setVisibility(View.GONE);
                pb_cargarfotoperfil.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Se eliminó con éxito la foto",Toast.LENGTH_SHORT).show();

                Intent go_home=new Intent(ShowImageActivity.this,MainActivity.class);
                startActivity(go_home);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                foto.setVisibility(View.VISIBLE);
                pb_cargarfotoperfil.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Error al eliminar la foto",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(cambioFoto){
            Intent go_home=new Intent(ShowImageActivity.this,MainActivity.class);
            startActivity(go_home);
        }

        finish();
    }

}