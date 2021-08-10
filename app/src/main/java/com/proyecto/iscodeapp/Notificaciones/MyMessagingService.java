package com.proyecto.iscodeapp.Notificaciones;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.proyecto.iscodeapp.IngresarActivity;
import com.proyecto.iscodeapp.MessageActivity;
import com.proyecto.iscodeapp.R;
import com.proyecto.iscodeapp.RecomendacionesActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MyMessagingService extends FirebaseMessagingService {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    SharedPreferences login_preferences;
    String tipo="";
    SharedPreferences preferences_archivos;
    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        super.onNewToken(token);
        Log.d("TAG564","New: "+token);
        subirToken(token);

    }

    private void subirToken(String token){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseDatabase myData = FirebaseDatabase.getInstance();
            database = myData.getReference();
            String id_usuario= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            database.child("Tokens").child(id_usuario).child("Token").setValue(token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();

        Log.d("tag876","Se recibió mensaje "+remoteMessage.getData());
        tipo=remoteMessage.getData().get("tipo");

        if (tipo.equals("mensaje")){
            procesarMensaje(remoteMessage);
        }
        else if(tipo.equals("recomendacion")){
            procesarRecomendacion(remoteMessage);
        }

        /*String title = remoteMessage.getData().get("title"); //Quien recibe el mensaje
        String text = remoteMessage.getData().get("body"); //Quien envía el mensaje
        String identificador=text.substring(0,1);

        final String CHANNEL_ID="NOTIFICACIONES";

        NotificationChannel channel=new NotificationChannel(
                CHANNEL_ID,
                "Canal Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification=new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.logo_app)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1,notification.build());
        */
    }

    private void procesarMensaje(RemoteMessage remoteMessage){
        login_preferences=getSharedPreferences("DATOS_LOGIN",MODE_PRIVATE);

        String currentuser=login_preferences.getString("ID","");
        String destinatario = remoteMessage.getData().get("destinatario"); //Quien recibe el mensaje
        String enviador = remoteMessage.getData().get("enviador"); //Quien envía el mensaje

        String nombre_enviador=remoteMessage.getData().get("title");
        String mensaje=remoteMessage.getData().get("body");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && destinatario.equals(firebaseUser.getUid())){
            if(!currentuser.equals(enviador)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //showOreoNotification(remoteMessage);

                    database.child("Usuarios").child(enviador).child("InfoArchivos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                //Error
                            }
                            else {
                                String HayFoto=task.getResult().child("HayFoto").getValue().toString();
                                if (HayFoto.equals("SI")){

                                    preferences_archivos = getSharedPreferences("ARCHIVOS_USUARIO", MODE_PRIVATE);
                                    String notifotodescarga=preferences_archivos.getString("notifotodescarga"+enviador,"NO");

                                    if(notifotodescarga.equals("SI")){
                                        mostrarNotificacionMensaje("sihayfoto",nombre_enviador,mensaje,enviador);
                                    }
                                    else if(notifotodescarga.equals("NO")) {

                                        File carpeta_ch = new File("/data/data/com.proyecto.iscodeapp/cache");
                                        if (!carpeta_ch.exists()) {
                                            if (carpeta_ch.mkdir())
                                                Log.d("tag907", "Creada");
                                        } else {
                                            Log.d("tag907", "Existe");
                                        }

                                        File path = new File("/data/data/com.proyecto.iscodeapp/cache/");
                                        File localFile = null;
                                        try {
                                            localFile = File.createTempFile("fotopersonas", ".jpeg", path);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        assert localFile != null;
                                        File name_foto = new File("/data/data/com.proyecto.iscodeapp/cache/fotopersonas" + enviador + ".jpeg");
                                        localFile.renameTo(name_foto);

                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference gsReference = storage.getReferenceFromUrl("gs://app-iscode.appspot.com/" + enviador + "/FotoUsuario/fotoperfil");
                                        File fotoimagen = new File("/data/data/com.proyecto.iscodeapp/cache/fotopersonas" + enviador + ".jpeg");
                                        gsReference.getFile(fotoimagen).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                preferences_archivos.edit().putString("notifotodescarga"+enviador,"SI").apply();
                                                mostrarNotificacionMensaje("sihayfoto", nombre_enviador, mensaje, enviador);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {

                                            }
                                        });
                                    }
                                }
                                else if(HayFoto.equals("NO")){
                                    mostrarNotificacionMensaje("nohayfoto",nombre_enviador,mensaje,enviador);
                                }
                            }
                        }
                    });
                }
            }



        }
    }

    private void procesarRecomendacion(RemoteMessage remoteMessage){
        login_preferences=getSharedPreferences("DATOS_LOGIN",MODE_PRIVATE);

        String currentuser=login_preferences.getString("ID","");
        String destinatario = remoteMessage.getData().get("destinatario"); //Quien recibe el mensaje
        String enviador = remoteMessage.getData().get("enviador"); //Quien envía el mensaje

        String nombre_enviador=remoteMessage.getData().get("title");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && destinatario.equals(firebaseUser.getUid())){
            if(!currentuser.equals(enviador)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mostrarNotificacionRecomendacion(nombre_enviador,enviador);
                }
            }
        }
    }



    private void mostrarNotificacionMensaje(String origen,String nombre,String mensaje,String enviador){
        final String CHANNEL_ID="MENSAJES";

        NotificationChannel channel=new NotificationChannel(
                CHANNEL_ID,
                "Mensajes",
                NotificationManager.IMPORTANCE_HIGH
        );

        Bitmap bitmap = null;
        if(origen.equals("nohayfoto")){
            int drawable_def=R.drawable.icon_user;
            bitmap=BitmapFactory.decodeResource(getResources(), drawable_def);
        }
        else if(origen.equals("sihayfoto")){
            bitmap= BitmapFactory.decodeFile("/data/data/com.proyecto.iscodeapp/cache/fotopersonas"+enviador+".jpeg");
        }

        int j = Integer.parseInt(enviador.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("userid",enviador);
        intent.putExtra("nombre",nombre);
        intent.putExtra("ORIGEN","notificacion");
        if (origen.equals("sihayfoto")){
            intent.putExtra("imageurl","/data/data/com.proyecto.iscodeapp/cache/fotopersonas"+enviador+".jpeg");
        }
        else if(origen.equals("nohayfoto")){
            intent.putExtra("imageurl","default");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);


        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification=new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle(nombre)
                .setContentText(mensaje)
                .setSmallIcon(R.drawable.logo_app)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.InboxStyle().
                        addLine(mensaje).
                        setBigContentTitle("Mensajes").
                        setSummaryText("Notificaciones"))
                .setAutoCancel(true);

        int i = 0;
        if (j > 0){
            i = j;
        }

        NotificationManagerCompat.from(this).notify(i,notification.build());
    }

    private void mostrarNotificacionRecomendacion(String nombre_enviador,String enviador) {

        final String CHANNEL_ID="RECOMENDACIONES";
        NotificationChannel channel=new NotificationChannel(
                CHANNEL_ID,
                "Recomendaciones",
                NotificationManager.IMPORTANCE_HIGH
        );

        Bitmap bitmap = null;
        int drawable_def=R.drawable.pic4;
        bitmap=BitmapFactory.decodeResource(getResources(), drawable_def);

        String recomIcono="Recomendaciones \uD83C\uDFC3";

        int j = Integer.parseInt(enviador.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, RecomendacionesActivity.class);
        intent.putExtra("ORIGEN","notificaciones");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);


        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification=new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle("Recomendaciones")
                .setContentText(nombre_enviador +" te ha enviado una nueva recomendación")
                .setSmallIcon(R.drawable.logo_app)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.InboxStyle().
                        addLine(nombre_enviador+ "te ha enviado una nueva recomendación").
                        setSummaryText(recomIcono))
                .setAutoCancel(true);

        int i = 0;
        if (j > 0){
            i = j;
        }

        NotificationManagerCompat.from(this).notify(i,notification.build());
    }
}
