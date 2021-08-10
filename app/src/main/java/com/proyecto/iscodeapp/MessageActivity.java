package com.proyecto.iscodeapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.proyecto.iscodeapp.Adapters.MessageAdapter;
import com.proyecto.iscodeapp.Funciones.FechaSelector;
import com.proyecto.iscodeapp.Funciones.MostrarPersonas;
import com.proyecto.iscodeapp.Funciones.RevisarConexion;
import com.proyecto.iscodeapp.Models.Chat;
import com.proyecto.iscodeapp.Notificaciones.ApiClient;
import com.proyecto.iscodeapp.Notificaciones.ApiService;
import com.proyecto.iscodeapp.Notificaciones.Data;
import com.proyecto.iscodeapp.Notificaciones.RequestNotificaton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    ImageButton atras_btn,btn_send,opciones_btn,recomendacion_btn_msg,recordatorio_btn_msg;
    EditText text_send;

    //Obtener datos
    String USERID="";
    String NOMBRE="";
    String IMAGEURL="";

    //
    String mensaje="";
    String id_usuario="";
    String token="";

    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private DatabaseReference database;

    //Adpatador de mensajes
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    int position=0;

    //Crear recordatorio
    Dialog Recordatorio;
    ImageButton sel_fecha_record,importancia_record,color_record,enviar_record;
    CardView stick_note;
    int posicion_color=0,valor_color=0;
    String importancia_estado="NO";
    ImageView importanciaIcono_record;
    TextView t_fecha,t_hora;
    RelativeLayout layout_repetiralarma;
    TextView repeticion_tiempo, intervalo_tiempo;
    String serepite="";


    int sdia, smes,saño,shora,sminuto;
    String diaStr, mesStr, horaStr, minutoStr="";
    String CdiaStr, CmesStr, CañoStr, ChoraStr,CminutoStr="";

    Calendar calendar;
    EditText titulo_record,contenido_record;

    //Enviar registro FC
    Dialog FCRegistro;
    String registrofc="";
    String titulofc="";
    String estadofc="";
    String fechafc="";

    TextView fc_registro;
    TextView titulo_registro;
    TextView estado_registro;
    TextView hora_registro;

    ImageButton enviar_registro_btn;

    //Enviar prediccion
    Dialog Prediccion;
    String valorpred;
    String titulopred;
    String fechapred;

    TextView titulo_prediccion,valor_prediccion,fecha_prediccion;

    ImageButton enviarpred;

    //layout fecha
    int diaPrev=1;
    int mesPrev=1;
    int añoPrev=1910;


    boolean notify=false;
    FirebaseUser fuser;

    SharedPreferences preferences;

    boolean connected;

    //Enviar recomendaciones
    Dialog Recomendaciones;
    String tipo_recom ="lectura";
    ImageView imagen_recom;
    String url_recom="";


    //Opciones icon
    PopupWindow OpcionesChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //Conexion
        RevisarConexion revisarConexion=new RevisarConexion(MessageActivity.this);
        connected = revisarConexion.isConnected();

        preferences=getSharedPreferences("DATOS_LOGIN",MODE_PRIVATE);

        Recordatorio=new Dialog(this);
        FCRegistro=new Dialog(this);
        Prediccion=new Dialog(this);
        Recomendaciones=new Dialog(this);


        // create the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.chat_opciones, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        OpcionesChat = new PopupWindow(popupView, width, height, true);

        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        atras_btn=findViewById(R.id.atras_btn);
        btn_send=findViewById(R.id.btn_send);
        text_send=findViewById(R.id.text_send);
        opciones_btn=findViewById(R.id.opciones_btn);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView=findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);

        USERID = getIntent().getStringExtra("userid");
        NOMBRE=getIntent().getStringExtra("nombre");
        IMAGEURL= getIntent().getStringExtra("imageurl");
        Uri foto=Uri.parse(IMAGEURL);

        //inicializamos el objeto firebaseAuth, los textos y los botones
        firebaseAuth = FirebaseAuth.getInstance();
        id_usuario = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();


        username.setText(NOMBRE);
        if (IMAGEURL.equals("default") || IMAGEURL.equals("waiting") ){
            profile_image.setImageResource(R.drawable.icon_user);
        }
        else {
            profile_image.setImageURI(foto);
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarPersonas mostrarPersonas=new MostrarPersonas(MessageActivity.this);
                mostrarPersonas.mostrar(USERID);
            }
        });

        atras_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().hasExtra("ORIGEN")){
                    if (getIntent().getStringExtra("ORIGEN").equals("notificacion")){
                        Intent goMain=new Intent(MessageActivity.this,MainActivity.class);
                        goMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        goMain.putExtra("ORIGEN","mensajes");
                        startActivity(goMain);
                    }
                }
                finish();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connected){
                    notify=true;
                    String msg=text_send.getText().toString().trim();
                    if(!msg.equals("")) {

                        fechaActual();
                        String hora=horaStr+":"+minutoStr;
                        String fecha=diaStr+"/"+mesStr+"/"+saño;
                        sendMessage(id_usuario,USERID,"M"+msg,fecha,hora);
                        text_send.setText("");
                    }
                }
                else{
                    Toast.makeText(MessageActivity.this,"Sin conexión a internet para enviar el mensaje",Toast.LENGTH_SHORT).show();
                }


            }
        });

        opciones_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                int orientation = MessageActivity.this.getResources().getConfiguration().orientation;
                Log.d("TAG9012","W: "+size.x+" H: "+ size.y+" or: "+orientation);

                if (orientation==2){
                    OpcionesChat.showAtLocation(v, Gravity.BOTTOM,-size.x/2,100);
                }
                else{
                    OpcionesChat.showAtLocation(v, Gravity.BOTTOM,-size.x/2,190);
                }

                recordatorio_btn_msg=OpcionesChat.getContentView().findViewById(R.id.recordatorio);
                recomendacion_btn_msg=OpcionesChat.getContentView().findViewById(R.id.recomendacion);

                recordatorio_btn_msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarRecordatorio();
                        OpcionesChat.dismiss();
                    }
                });

                recomendacion_btn_msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarRecomendacion();
                        OpcionesChat.dismiss();
                    }
                });



                //mostrarRecomendacion();
                //mostrarRecordatorio();



            }
        });



        readMessages(getApplicationContext(), id_usuario,USERID,IMAGEURL);


        //Leer datos desde Registro FC
        if(getIntent().hasExtra("registrofc") &&
                getIntent().hasExtra("titulofc") &&
                getIntent().hasExtra("estadofc") &&
                getIntent().hasExtra("fechafc")) {

            registrofc = getIntent().getStringExtra("registrofc");
            titulofc = getIntent().getStringExtra("titulofc");
            estadofc = getIntent().getStringExtra("estadofc");
            fechafc = getIntent().getStringExtra("fechafc");

            mostrarFCregistro();
        }

        //Enviar recordatorio
        if(getIntent().hasExtra("TITULO") &&
                getIntent().hasExtra("CONTENIDO") &&
                getIntent().hasExtra("COLOR") &&
                getIntent().hasExtra("FECHA") &&
                getIntent().hasExtra("HORA") &&
                getIntent().hasExtra("IMPORTANCIA")){

            recibirRecordatorio();

        }

        //Enviar prediccion
        if(getIntent().hasExtra("valorpred") &&
                getIntent().hasExtra("titulopred") &&
                getIntent().hasExtra("fechapred")){
            valorpred = getIntent().getStringExtra("valorpred");
            titulopred = getIntent().getStringExtra("titulopred");
            fechapred = getIntent().getStringExtra("fechapred");

            mostrarPrediccion();
        }



    }

    private void sendMessage(String sender, String receiver, String message, String fecha, String hora){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("fecha",fecha);
        hashMap.put("hora",hora);

        reference.child("Chats").push().setValue(hashMap);

        database.child("Usuarios").child(id_usuario).child("InfoChats").child(receiver).setValue(receiver);
        database.child("Usuarios").child(receiver).child("InfoChats").child(id_usuario).setValue(id_usuario);

        final String msg = message;

        if (notify) {
            sendNotification(msg);

        }
        notify = false;
    }

    private void sendNotification(String mensaje) {

        String indicador=mensaje.substring(0,1);
        Data data=new Data();
        RequestNotificaton requestNotificaton = new RequestNotificaton();
        String sender=preferences.getString("Nombre","User");

        Log.d("tag907","INDICADOR: "+indicador);

        database.child("Tokens").child(USERID).child("Token").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MessageActivity.this,"Error al enviar",Toast.LENGTH_SHORT).show();
                }
                else {
                    token = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getValue()).toString();

                    if (!token.equals("")){
                        requestNotificaton.setTo(token);

                        data.setTitle(sender);
                        data.setTipo("mensaje");

                        if(indicador.equals("R")){
                            data.setBody("\u23F0 Recordatorio...");
                        }
                        else if (indicador.equals("F")){
                            data.setBody("\u2764 Registro de frecuencia cardiaca...");
                        }
                        else if(indicador.equals("P")){
                            data.setBody("\uD83C\uDFAF Predicción...");
                        }

                        else if(indicador.equals("D")){
                            data.setTipo("recomendacion");
                            data.setBody("\uD83C\uDFC3 Recomedación...");
                        }
                        else{
                            data.setBody(mensaje.substring(1,mensaje.length()));
                        }

                        data.setDestinatario(USERID);
                        data.setEnviador(id_usuario);
                        data.setIcon(String.valueOf(R.drawable.logo_app));
                        requestNotificaton.setData(data);

                        ApiService apiService =  ApiClient.getClient().create(ApiService.class);
                        retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendChatNotification(requestNotificaton);

                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.d("TAG675","done");
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }


                }
            }
        });




    }

    public void readMessages(Context context, String myid, String userid, String imageurl){
        mChat=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mChat.clear();
                position=0;
                //layout fecha
                diaPrev=1;
                mesPrev=1;
                añoPrev=1910;
                for (DataSnapshot snapshot:datasnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid)
                            || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){

                        String fechaMensaje=chat.getFecha();
                        agregarVistaFecha(fechaMensaje,chat);

                        mChat.add(chat);
                        position=position+1;

                    }
                }

                messageAdapter=new MessageAdapter(MessageActivity.this,context, mChat,imageurl);
                recyclerView.setAdapter(messageAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(MessageActivity.this);
                layoutManager.setReverseLayout(false);
                layoutManager.setStackFromEnd(true);
                layoutManager.scrollToPosition(mChat.size()-1);
                recyclerView.setLayoutManager(layoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void agregarVistaFecha(String fechaMensaje, Chat chat){
        int val1= fechaMensaje.indexOf('/');
        int val2= fechaMensaje.lastIndexOf('/');
        int dia=Integer.parseInt( fechaMensaje.substring(0,val1));
        int mes=Integer.parseInt(fechaMensaje.substring(val1+1,val2));
        int año=Integer.parseInt( fechaMensaje.substring(val2+1,fechaMensaje.length()));

        Log.d("tag570","dia: "+dia+" mes: "+mes+" año: "+año);

        //calendar.set(año,mes,dia,hora,minutos,0);
        Calendar tiempoActual=Calendar.getInstance();
        Calendar tiempoPrevio=Calendar.getInstance();

        tiempoActual.set(Calendar.YEAR,año); tiempoPrevio.set(Calendar.YEAR,añoPrev);
        tiempoActual.set(Calendar.MONTH,(mes-1)); tiempoPrevio.set(Calendar.MONTH,(mesPrev));
        tiempoActual.set(Calendar.DAY_OF_MONTH,dia); tiempoPrevio.set(Calendar.DAY_OF_MONTH,diaPrev);
        tiempoActual.set(Calendar.HOUR_OF_DAY,0); tiempoPrevio.set(Calendar.HOUR_OF_DAY,0);
        tiempoActual.set(Calendar.MINUTE,0); tiempoPrevio.set(Calendar.MINUTE,0);
        tiempoActual.set(Calendar.SECOND,0); tiempoPrevio.set(Calendar.SECOND,0);

        long tiempoA=tiempoActual.getTimeInMillis();
        long tiempoB=tiempoPrevio.getTimeInMillis();

        if(tiempoA>tiempoB){
            Log.d("TAG532","SI");

            Chat newChat=new Chat();
            newChat.setMessage("X");
            newChat.setFecha(chat.getFecha());
            newChat.setHora(chat.getHora());
            newChat.setReceiver(chat.getReceiver());
            newChat.setSender(chat.getSender());

            mChat.add(newChat);
            position=position+1;

            añoPrev=año;
            mesPrev=mes-1;
            diaPrev=dia;

        }
        else{
            Log.d("TAG532","NO");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void mostrarRecordatorio(){
        Recordatorio.setContentView(R.layout.recordatorio_popup);
        sel_fecha_record=Recordatorio.findViewById(R.id.sel_fecha_record);
        importancia_record=Recordatorio.findViewById(R.id.importancia_record);
        color_record=Recordatorio.findViewById(R.id.color_record);
        enviar_record=Recordatorio.findViewById(R.id.enviar_record);
        stick_note=Recordatorio.findViewById(R.id.stick_note);
        t_fecha=Recordatorio.findViewById(R.id.t_fecha);
        t_hora=Recordatorio.findViewById(R.id.t_hora);
        titulo_record=Recordatorio.findViewById(R.id.titulo_record);
        contenido_record=Recordatorio.findViewById(R.id.contenido_record);
        importanciaIcono_record=Recordatorio.findViewById(R.id.importanciaIcono_record);
        importanciaIcono_record.setVisibility(View.GONE);
        enviar_record.setVisibility(View.GONE);

        //Repetir alarma
        layout_repetiralarma=Recordatorio.findViewById(R.id.layout_repetiralarma);
        repeticion_tiempo=Recordatorio.findViewById(R.id.repeticion_tiempo);;
        intervalo_tiempo=Recordatorio.findViewById(R.id.intervalo_tiempo);
        repeticion_tiempo.setVisibility(View.GONE);
        intervalo_tiempo.setVisibility(View.GONE);
        layout_repetiralarma.setVisibility(View.GONE);


        color_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviar_record.setVisibility(View.VISIBLE);
                posicion_color=posicion_color+1;
                valor_color=color_nota();
            }

        });

        importancia_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviar_record.setVisibility(View.VISIBLE);
                if (importancia_estado.equals("SI")){
                    importancia_estado="NO";
                    importanciaIcono_record.setVisibility(View.GONE);
                }else {
                    importancia_estado="SI";
                    importanciaIcono_record.setVisibility(View.VISIBLE);
                }

            }
        });

        sel_fecha_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FechaSelector fechaSelector=new FechaSelector(MessageActivity.this,MessageActivity.this,"mensajesAct");
                fechaSelector.adaptarFecha(t_fecha,t_hora,layout_repetiralarma,repeticion_tiempo,intervalo_tiempo);
            }
        });



        titulo_record.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                enviar_record.setVisibility(View.VISIBLE);
                return false;
            }
        });

        contenido_record.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                enviar_record.setVisibility(View.VISIBLE);
                return false;
            }
        });

        enviar_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connected){
                    if(titulo_record.getText().toString().trim().equals("")
                            || t_fecha.getText().toString().equals("") ||
                            t_hora.getText().toString().equals("")){
                        Toast.makeText(MessageActivity.this,"Debe Agregar al menos un título y la fecha",Toast.LENGTH_SHORT).show();

                    }
                    else {

                        if(layout_repetiralarma.getVisibility()==View.VISIBLE){
                            serepite="SI";
                        }
                        else {
                            serepite="NO";
                        }

                        String mensaje_recordatorio="R"+titulo_record.getText().toString().trim()+"^"+
                                contenido_record.getText().toString().trim()+"¬"+
                                valor_color+"?"+
                                t_fecha.getText().toString()+"{"+
                                t_hora.getText().toString()+"}"+
                                importancia_estado+"~"+
                                serepite +"$"+
                                repeticion_tiempo.getText().toString().trim()+"&"+
                                intervalo_tiempo.getText().toString().trim();

                        fechaActual();
                        String hora=horaStr+":"+minutoStr;
                        String fecha=diaStr+"/"+mesStr+"/"+saño;
                        notify=true;
                        sendMessage(id_usuario,USERID,mensaje_recordatorio,fecha,hora);
                        Recordatorio.dismiss();
                    }
                }
                else{
                    Toast.makeText(MessageActivity.this,"Sin conexion a internet para enviar recordatorio",Toast.LENGTH_SHORT).show();
                }


            }
        });

        Recordatorio.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Perfil.setCanceledOnTouchOutside(false);
        Recordatorio.show();
    }

    public void recibirRecordatorio(){
        mostrarRecordatorio();
        String titulo=""; String contenido=""; String color=""; String fecha=""; String hora=""; String importancia="";
        titulo=getIntent().getStringExtra("TITULO");
        contenido=getIntent().getStringExtra("CONTENIDO");
        color=getIntent().getStringExtra("COLOR");
        fecha=getIntent().getStringExtra("FECHA");
        hora=getIntent().getStringExtra("HORA");
        importancia=getIntent().getStringExtra("IMPORTANCIA");

        titulo_record.setText(titulo);
        contenido_record.setText(contenido);
        t_fecha.setText(fecha);
        t_hora.setText(hora);

        switch (Integer.parseInt(color)){
            case 0:
                stick_note.getBackground().setTint(getColor(R.color.Nota1));
                valor_color=0;
                posicion_color=0;
                break;
            case 1:
                stick_note.getBackground().setTint(getColor(R.color.Nota2));
                valor_color=1;
                posicion_color=1;
                break;
            case 2:
                stick_note.getBackground().setTint(getColor(R.color.Nota3));
                valor_color=2;
                posicion_color=2;
                break;
            case 3:
                stick_note.getBackground().setTint(getColor(R.color.Nota4));
                valor_color=3;
                posicion_color=3;
                break;
        }
        importancia_estado=importancia;

        if (importancia_estado.equals("SI")){
            importanciaIcono_record.setVisibility(View.VISIBLE);
        }
        else {
            importanciaIcono_record.setVisibility(View.GONE);
        }

    }

    public void mostrarFCregistro(){
        FCRegistro.setContentView(R.layout.registrofcenviar);

        fc_registro=FCRegistro.findViewById(R.id.fc_registro);
        titulo_registro=FCRegistro.findViewById(R.id.titulo_registro);
        estado_registro=FCRegistro.findViewById(R.id.estado_registro);
        hora_registro=FCRegistro.findViewById(R.id.hora_registro);

        enviar_registro_btn=FCRegistro.findViewById(R.id.enviar_registro_btn);

        fc_registro.setText(registrofc);
        titulo_registro.setText(titulofc);
        estado_registro.setText(estadofc);
        hora_registro.setText(fechafc);


        enviar_registro_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje_registrofc="F"+fc_registro.getText().toString()+"^"+
                        titulo_registro.getText().toString().trim()+"¬"+
                        estado_registro.getText().toString()+"?"+
                        hora_registro.getText().toString();

                fechaActual();
                String hora=horaStr+":"+minutoStr;
                String fecha=diaStr+"/"+mesStr+"/"+saño;
                notify=true;
                sendMessage(id_usuario,USERID,mensaje_registrofc,fecha,hora);
                FCRegistro.dismiss();
            }
        });

        FCRegistro.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Perfil.setCanceledOnTouchOutside(false);
        FCRegistro.show();


    }

    public void mostrarPrediccion(){
        Prediccion.setContentView(R.layout.prediccionenviar);

        titulo_prediccion=Prediccion.findViewById(R.id.titulo);
        valor_prediccion=Prediccion.findViewById(R.id.prediccion);
        fecha_prediccion=Prediccion.findViewById(R.id.fecha);
        enviarpred=Prediccion.findViewById(R.id.enviarpred);

        titulo_prediccion.setText(titulopred);
        valor_prediccion.setText(valorpred);
        fecha_prediccion.setText(fechapred);

        enviarpred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje_pred="P"+valor_prediccion.getText().toString()+"^"+
                        titulo_prediccion.getText().toString().trim()+"¬"+
                        fecha_prediccion.getText().toString();

                fechaActual();
                String hora=horaStr+":"+minutoStr;
                String fecha=diaStr+"/"+mesStr+"/"+saño;
                notify=true;
                sendMessage(id_usuario,USERID,mensaje_pred,fecha,hora);
                Prediccion.dismiss();
            }
        });

        Prediccion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Perfil.setCanceledOnTouchOutside(false);
        Prediccion.show();
    }

    private void mostrarRecomendacion(){
        Recomendaciones.setContentView(R.layout.recomendacion_enviar);

        EditText titulo,contenido;
        TextView fecha,hora;
        ImageButton agregar_url,enviar,agregar_tipo;
        ProgressBar pb_enviar_recom;

        titulo=Recomendaciones.findViewById(R.id.titulo_recom);
        contenido=Recomendaciones.findViewById(R.id.contenido_recom);
        fecha=Recomendaciones.findViewById(R.id.fecha_recom);
        hora=Recomendaciones.findViewById(R.id.hora_recom);
        agregar_url=Recomendaciones.findViewById(R.id.agregar_url);
        agregar_tipo=Recomendaciones.findViewById(R.id.agregar_tipo);
        enviar=Recomendaciones.findViewById(R.id.enviar_recom);
        pb_enviar_recom=Recomendaciones.findViewById(R.id.pb_enviar_recom);
        pb_enviar_recom.setVisibility(View.GONE);


        agregar_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MessageActivity.this);
                alertDialog.setTitle("Dirección web");
                alertDialog.setMessage("Introduzca la dirección:");

                final EditText input = new EditText(MessageActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMarginEnd(50);lp.setMarginStart(50);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Listo",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                url_recom =input.getText().toString().trim();
                                dialog.dismiss();
                            }
                        });

                alertDialog.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                url_recom ="";
                                dialog.cancel();
                            }
                        });

                alertDialog.show();

            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connected){
                    pb_enviar_recom.setVisibility(View.VISIBLE);
                    enviar.setVisibility(View.GONE);

                    if (titulo.getText().toString().trim().equals("") || contenido.getText().toString().trim().equals("")){
                        Toast.makeText(MessageActivity.this,"Debe agregar un titulo y un contenido",Toast.LENGTH_SHORT).show();
                        pb_enviar_recom.setVisibility(View.GONE);
                        enviar.setVisibility(View.VISIBLE);
                    }
                    else{
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("titulo",titulo.getText().toString().trim());
                        hashMap.put("contenido",contenido.getText().toString().trim());
                        hashMap.put("tipo", tipo_recom);
                        fechaActual();
                        hashMap.put("fecha",diaStr+"/"+mesStr+"/"+saño+" "+horaStr+":"+minutoStr);

                        hashMap.put("url",url_recom);
                        reference.child("Usuarios").child(USERID).child("Recomendaciones").push().setValue(hashMap);

                        notify=true;
                        String msg="Recomendación";
                        fechaActual();
                        String hora=horaStr+":"+minutoStr;
                        String fecha=diaStr+"/"+mesStr+"/"+saño;
                        sendMessage(id_usuario,USERID,"D"+msg,fecha,hora);

                        Recomendaciones.dismiss();
                    }

                }
                else{
                    Toast.makeText(MessageActivity.this,"Sin conexión a internet para enviar el mensaje",Toast.LENGTH_SHORT).show();
                }



            }
        });

        Recomendaciones.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Recomendaciones.setCanceledOnTouchOutside(true);
        Recomendaciones.show();
    }

    public int color_nota(){
        if (posicion_color==4){
            posicion_color=0;
        }
        int valor=posicion_color;
        switch (valor){
            case 0:
                stick_note.getBackground().setTint(getColor(R.color.Nota1));
                break;
            case 1:
                stick_note.getBackground().setTint(getColor(R.color.Nota2));
                break;
            case 2:
                stick_note.getBackground().setTint(getColor(R.color.Nota3));
                break;
            case 3:
                stick_note.getBackground().setTint(getColor(R.color.Nota4));
                break;
        }
        return valor;
    }

    public void fechaActual(){

        //layout fecha
        diaPrev=1;
        mesPrev=1;
        añoPrev=1910;

        calendar=Calendar.getInstance();
        sminuto=calendar.get(Calendar.MINUTE);
        shora=calendar.get(Calendar.HOUR_OF_DAY);
        sdia=calendar.get(Calendar.DAY_OF_MONTH);
        smes=calendar.get(Calendar.MONTH);
        saño=calendar.get(Calendar.YEAR);

        diaStr = sdia < 10 ? "0"+sdia : String.valueOf(sdia);
        mesStr = (smes+1) < 10 ? "0"+(smes+1) : String.valueOf((smes+1));
        horaStr = shora < 10 ? "0"+shora : String.valueOf(shora);
        minutoStr = sminuto < 10 ? "0"+sminuto : String.valueOf(sminuto);
    }

    public void showMenuTipoRecom(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.alimentacion:
                        tipo_recom ="Alimentación";
                        Toast.makeText(MessageActivity.this,"Recomendación ajustada a tipo: Alimentación",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.ejercicio:
                        tipo_recom ="Ejercicio";
                        Toast.makeText(MessageActivity.this,"Recomendación ajustada a tipo: Ejercicio",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.entretenimiento:
                        tipo_recom ="Entretenimiento";
                        Toast.makeText(MessageActivity.this,"Recomendación ajustada a tipo: Entretenimiento",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.lectura:
                        tipo_recom ="Lectura";
                        Toast.makeText(MessageActivity.this,"Recomendación ajustada a tipo: Lectura",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.menu_tiporecom);
        popup.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().hasExtra("ORIGEN")){
            if (getIntent().getStringExtra("ORIGEN").equals("notificacion")){
                Intent goMain=new Intent(MessageActivity.this,MainActivity.class);
                goMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                goMain.putExtra("ORIGEN","mensajes");
                startActivity(goMain);
            }
        }
        finish();
    }
}