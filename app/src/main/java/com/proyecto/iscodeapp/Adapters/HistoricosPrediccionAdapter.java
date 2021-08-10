package com.proyecto.iscodeapp.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyecto.iscodeapp.Databases.MyDataBaseHelper;
import com.proyecto.iscodeapp.Databases.MyDataBasePersonas;
import com.proyecto.iscodeapp.Funciones.DescargarPersonas;
import com.proyecto.iscodeapp.Funciones.RevisarConexion;
import com.proyecto.iscodeapp.MessageActivity;
import com.proyecto.iscodeapp.Models.Prediccion;
import com.proyecto.iscodeapp.Models.User;

import java.util.ArrayList;
import java.util.List;

import com.proyecto.iscodeapp.R;

public class HistoricosPrediccionAdapter extends RecyclerView.Adapter<HistoricosPrediccionAdapter.MyViewHolder> {

    private Context context;
    private ArrayList   COLUMNA_ID_PRED, TITULO_PRED, VALOR_PRED, FECHA_PRED,HORA_PRED,MODELO;
    private  ArrayList ATR1,ATR2,ATR3,ATR4,ATR5,ATR6,ATR7,ATR8,ATR9,ATR10,ATR11,ATR12,ATR13;
    Activity activity;

    //Opciones de cada registro
    Dialog opciones,lista_personas;
    RecyclerView recyclerView,lista_atributos;
    TextView fecha,prediccion;
    EditText titulo;
    ImageButton enviarpred,editarpred,borrarpred,guardarpred;
    List<User> mUsers;
    SharedPreferences preferences_personas;
    SharedPreferences preferences_archivos;
    String[] datosRegistro=new String[4];
    UserAdapter userAdapter;

    boolean connected;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    SharedPreferences preferences;
    String PrimeraVezSesion="";
    Dialog PersonasDialog;

    List<Prediccion> mPrediccion;
    AtributosAdapter atributosAdapter;


    public HistoricosPrediccionAdapter(Activity activity, Context context,
                                       ArrayList COLUMNA_ID_PRED,
                                       ArrayList TITULO_PRED, ArrayList VALOR_PRED,
                                       ArrayList FECHA_PRED, ArrayList HORA_PRED, ArrayList MODELO_PRED,
                                       Dialog opciones,
                                       ArrayList atr1, ArrayList atr2, ArrayList atr3,
                                       ArrayList atr4, ArrayList atr5, ArrayList atr6,
                                       ArrayList atr7, ArrayList atr8, ArrayList atr9,
                                       ArrayList atr10, ArrayList atr11, ArrayList atr12,
                                       ArrayList atr13){

        this.activity=activity;
        this.context=context;
        this.COLUMNA_ID_PRED=COLUMNA_ID_PRED;
        this.TITULO_PRED=TITULO_PRED;
        this.VALOR_PRED=VALOR_PRED;
        this.FECHA_PRED=FECHA_PRED;
        this.HORA_PRED=HORA_PRED;
        this.MODELO=MODELO_PRED;
        this.opciones=opciones;

        this.ATR1 = atr1;
        this.ATR2 = atr2;
        this.ATR3 = atr3;
        this.ATR4 = atr4;
        this.ATR5 = atr5;
        this.ATR6 = atr6;
        this.ATR7 = atr7;
        this.ATR8 = atr8;
        this.ATR9 = atr9;
        this.ATR10 = atr10;
        this.ATR11 = atr11;
        this.ATR12 = atr12;
        this.ATR13 = atr13;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.historicopred,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        lista_personas=new Dialog(context);

        holder.layout_historicopred.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                prediccion=opciones.findViewById(R.id.prediccion);
                titulo=opciones.findViewById(R.id.titulo);
                fecha=opciones.findViewById(R.id.fecha);
                editarpred=opciones.findViewById(R.id.editarpred);
                guardarpred=opciones.findViewById(R.id.guardarpred);
                enviarpred=opciones.findViewById(R.id.enviarpred);
                borrarpred=opciones.findViewById(R.id.borrarpred);

                lista_atributos=opciones.findViewById(R.id.lista_atributos);
                lista_atributos.setHasFixedSize(true);
                lista_atributos.setLayoutManager(new LinearLayoutManager(context));

                guardarpred.setVisibility(View.GONE);

                titulo.setFocusable(false);
                titulo.setEnabled(false);
                titulo.setCursorVisible(false);


                titulo.setText(String.valueOf(TITULO_PRED.get(position)).trim());
                prediccion.setText(String.valueOf(VALOR_PRED.get(position)));
                fecha.setText(String.valueOf(FECHA_PRED.get(position))+" "+String.valueOf(HORA_PRED.get(position)));

                enviarpred.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        compartirNota(String.valueOf(VALOR_PRED.get(position)),
                                titulo.getText().toString().trim(),
                                String.valueOf(FECHA_PRED.get(position))+" "+String.valueOf(HORA_PRED.get(position)));
                    }
                });

                editarpred.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guardarpred.setVisibility(View.VISIBLE);
                        editarpred.setVisibility(View.GONE);
                        titulo.setFocusable(true);
                        titulo.setEnabled(true);
                        titulo.setCursorVisible(true);
                        titulo.setFocusableInTouchMode(true);
                    }
                });

                guardarpred.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guardarpred.setVisibility(View.GONE);
                        editarpred.setVisibility(View.VISIBLE);
                        titulo.setFocusable(false);
                        titulo.setEnabled(false);
                        titulo.setCursorVisible(false);
                        MyDataBaseHelper myDB = new MyDataBaseHelper(context);
                        myDB.updateDataPRED (String.valueOf(COLUMNA_ID_PRED.get(position)),
                                titulo.getText().toString().trim(),
                                String.valueOf(VALOR_PRED.get(position)),
                                String.valueOf(FECHA_PRED.get(position)),
                                String.valueOf(HORA_PRED.get(position)),
                                String.valueOf(MODELO.get(position)),
                                String.valueOf(ATR1.get(position)),String.valueOf(ATR2.get(position)),
                                String.valueOf(ATR3.get(position)),String.valueOf(ATR4.get(position)),
                                String.valueOf(ATR5.get(position)),String.valueOf(ATR6.get(position)),
                                String.valueOf(ATR7.get(position)),String.valueOf(ATR8.get(position)),
                                String.valueOf(ATR9.get(position)),String.valueOf(ATR10.get(position)),
                                String.valueOf(ATR11.get(position)),String.valueOf(ATR12.get(position)),
                                String.valueOf(ATR13.get(position)));

                    }
                });

                borrarpred.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                        builder.setTitle("Eliminar");
                        builder.setMessage("¿Desea eliminar esta predicción?");
                        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyDataBaseHelper myDB = new MyDataBaseHelper(context);
                                myDB.deleteOneRowPRED(String.valueOf(COLUMNA_ID_PRED.get(position)));
                                opciones.dismiss();
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


                //Mostrar atributos
                mPrediccion=new ArrayList<>();

                int i=1;
                while(i<=13){
                    Prediccion prediccion=new Prediccion();

                    if(String.valueOf(MODELO.get(position)).equals("modeloB")){
                        if (i==7){
                            i=8;
                        }
                        else if(i==10 || i==11 || i==12 || i==13){
                            i=14;
                        }
                    }

                    switch (i){
                        case 1:
                            prediccion.setAtributo("Edad");
                            prediccion.setValor_atributo(String.valueOf(ATR1.get(position)));
                            prediccion.setIcono(R.drawable.back_atr1);
                            break;
                        case 2:
                            prediccion.setAtributo("Género");
                            if(String.valueOf(ATR2.get(position)).equals("0")){
                                prediccion.setValor_atributo("Masculino");
                            }
                            else {
                                prediccion.setValor_atributo("Femenino");
                            }
                            prediccion.setIcono(R.drawable.back_atr2);
                            break;
                        case 3:
                            prediccion.setAtributo("Tipo de dolor de pecho");
                            if(String.valueOf(ATR3.get(position)).equals("0")){
                                prediccion.setValor_atributo("Típica angina");
                            }
                            else if(String.valueOf(ATR3.get(position)).equals("1")){
                                prediccion.setValor_atributo("Atípica angina");
                            }
                            else if(String.valueOf(ATR3.get(position)).equals("2")){
                                prediccion.setValor_atributo("No angina");
                            }
                            else {
                                prediccion.setValor_atributo("Asintomático");
                            }
                            prediccion.setIcono(R.drawable.back_atr3);
                            break;
                        case 4:
                            prediccion.setAtributo("Presión arterial");
                            prediccion.setValor_atributo(String.valueOf(ATR4.get(position)));
                            prediccion.setIcono(R.drawable.back_atr4);
                            break;
                        case 5:
                            prediccion.setAtributo("Colesterol sérico en mg/dl");
                            prediccion.setValor_atributo(String.valueOf(ATR5.get(position)));
                            prediccion.setIcono(R.drawable.back_atr5);
                            break;
                        case 6:
                            prediccion.setAtributo("¿Glucemia en ayunas?");
                            if(String.valueOf(ATR6.get(position)).equals("0")){
                                prediccion.setValor_atributo("SI");
                            }
                            else {
                                prediccion.setValor_atributo("NO");
                            }
                            prediccion.setIcono(R.drawable.back_atr6);
                            break;
                        case 7:
                            prediccion.setAtributo("Resultados electrocardiograma");
                            if(String.valueOf(ATR7.get(position)).equals("0")){
                                prediccion.setValor_atributo("Normal");
                            }
                            else if(String.valueOf(ATR7.get(position)).equals("1")){
                                prediccion.setValor_atributo("Anomalía en la onda ST-T");
                            }
                            else {
                                prediccion.setValor_atributo("Hipertrofia ventricular izquierda...");
                            }
                            prediccion.setIcono(R.drawable.back_atr7);
                            break;
                        case 8:
                            prediccion.setAtributo("Frecuencia cardiaca");
                            prediccion.setValor_atributo(String.valueOf(ATR8.get(position)));
                            prediccion.setIcono(R.drawable.back_atr8);
                            break;
                        case 9:
                            prediccion.setAtributo("Dolor de pecho al hacer ejercicio");
                            if(String.valueOf(ATR9.get(position)).equals("0")){
                                prediccion.setValor_atributo("SI");
                            }
                            else {
                                prediccion.setValor_atributo("NO");
                            }
                            prediccion.setIcono(R.drawable.back_atr9);
                            break;
                        case 10:
                            prediccion.setAtributo("Depresión del ST");
                            prediccion.setValor_atributo(String.valueOf(ATR10.get(position)));
                            prediccion.setIcono(R.drawable.back_atr10);
                            break;
                        case 11:
                            prediccion.setAtributo("Pendiente del segmento ST");
                            if(String.valueOf(ATR11.get(position)).equals("0")){
                                prediccion.setValor_atributo("Ascendiendo");
                            }
                            else if(String.valueOf(ATR11.get(position)).equals("1")){
                                prediccion.setValor_atributo("Constante");
                            }
                            else {
                                prediccion.setValor_atributo("Descendiendo");
                            }
                            prediccion.setIcono(R.drawable.back_atr11);
                            break;
                        case 12:
                            prediccion.setAtributo("Vasos coloreados fluoroscopia");
                            if(String.valueOf(ATR12.get(position)).equals("0")){
                                prediccion.setValor_atributo("0");
                            }
                            else if(String.valueOf(ATR12.get(position)).equals("1")){
                                prediccion.setValor_atributo("1");
                            }
                            else if(String.valueOf(ATR12.get(position)).equals("2")){
                                prediccion.setValor_atributo("2");
                            }
                            else {
                                prediccion.setValor_atributo("3");
                            }
                            prediccion.setIcono(R.drawable.back_atr12);
                            break;
                        case 13:
                            prediccion.setAtributo("Thallium scan");
                            if(String.valueOf(ATR13.get(position)).equals("0")){
                                prediccion.setValor_atributo("Normal");
                            }
                            else if(String.valueOf(ATR13.get(position)).equals("1")){
                                prediccion.setValor_atributo("Defecto fijo");
                            }
                            else {
                                prediccion.setValor_atributo("Defecto reversible");
                            }
                            prediccion.setIcono(R.drawable.back_atr13);
                            break;
                    }
                    Log.d("tag948","I: "+i );
                    if(i!=14){
                        mPrediccion.add(prediccion);
                    }
                    i=i+1;
                }


                atributosAdapter=new AtributosAdapter(activity,context,mPrediccion);
                lista_atributos.setAdapter(atributosAdapter);

                opciones.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //Perfil.setCanceledOnTouchOutside(false);
                opciones.show();


            }
        });


        //Objetos en la nota:
        holder.titulopred.setText(String.valueOf(TITULO_PRED.get(position)));
        holder.prediccion.setText(String.valueOf(VALOR_PRED.get(position)));
        holder.fechapred.setText(String.valueOf(FECHA_PRED.get(position)));
        holder.horapred.setText(String.valueOf(HORA_PRED.get(position)));


    }

    @Override
    public int getItemCount() {
        return TITULO_PRED.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titulopred ,prediccion,fechapred, horapred;
        LinearLayout layout_historicopred;
        CardView historicopred_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layout_historicopred=itemView.findViewById(R.id.layout_historicopred);
            titulopred=itemView.findViewById(R.id.titulopred);
            prediccion=itemView.findViewById(R.id.prediccion);
            fechapred=itemView.findViewById(R.id.fechapred);
            horapred=itemView.findViewById(R.id.horapred);
            historicopred_item=itemView.findViewById(R.id.historicofc_item);

        }
    }

    public void compartirNota(String registro, String titulo, String fecha) {
        RevisarConexion revisarConexion = new RevisarConexion(context);
        connected = revisarConexion.isConnected();

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();

        preferences = activity.getSharedPreferences("DATOS_LOGIN", Context.MODE_PRIVATE);
        preferences_archivos = activity.getSharedPreferences("ARCHIVOS_USUARIO", Context.MODE_PRIVATE);
        PrimeraVezSesion = preferences.getString("PrimeraVezSesion", "");


        if (connected) {
            database.child("InfoGeneral").child("RefreshPersonas").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        //Error
                    } else {
                        if (task.getResult().getValue().toString().equals("SI") || PrimeraVezSesion.equals("SI")) {

                            preferences.edit().putString("PrimeraVezSesion", "NO").apply();
                            preferences_archivos.edit().putString("PersonasDescargadas", "NO").apply();
                            PrimeraVezSesion = "NO";

                            PersonasDialog = new Dialog(context);
                            PersonasDialog.setContentView(R.layout.personas_dialog);
                            DescargarPersonas descargarPersonas = new DescargarPersonas(activity, context);
                            descargarPersonas.actualizar(PersonasDialog, "fcfragment");

                            PersonasDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if (preferences_archivos.getString("PersonasDescargadas", "NO").equals("SI")) {
                                        enviarRegistro(registro,titulo,fecha);
                                    }
                                }
                            });

                        } else if (task.getResult().getValue().toString().equals("NO")) {
                            enviarRegistro(registro,titulo,fecha);
                        }

                    }
                }
            });
        } else {
            Toast.makeText(context, "Sin conexión a internet para compartir", Toast.LENGTH_SHORT).show();
        }

    }

    public void enviarRegistro(String registro, String titulo, String fecha){

        lista_personas.setContentView(R.layout.lista_personas);
        recyclerView=lista_personas.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        mUsers=new ArrayList<>();

        MyDataBasePersonas myDataBasePersonas=new MyDataBasePersonas(context);
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
                String fechauser=cursor.getString(4);
                String imageurl=cursor.getString(5);

                User user=new User();
                user.setId(personasid);
                user.setUsername(nombre);
                user.setCorreo(correo);
                user.setFecha(fechauser);
                user.setImageurl(imageurl);
                mUsers.add(user);
            }
        }
        datosRegistro[0]=registro;
        datosRegistro[1]=titulo;
        datosRegistro[2]=fecha;

        userAdapter=new UserAdapter(context,mUsers,"historicos_pred",datosRegistro);
        recyclerView.setAdapter(userAdapter);

        lista_personas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Perfil.setCanceledOnTouchOutside(false);
        lista_personas.show();

    }
}
