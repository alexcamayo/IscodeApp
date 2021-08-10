package com.proyecto.iscodeapp.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
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
import com.proyecto.iscodeapp.Models.User;

import java.util.ArrayList;
import java.util.List;

import com.proyecto.iscodeapp.R;
import com.proyecto.iscodeapp.UpdateStickNoteActivity;

public class HistoricosFCAdapter extends RecyclerView.Adapter<HistoricosFCAdapter.MyViewHolder> {

    private Context context;
    private ArrayList   COLUMNA_ID_FC, TITULO_FC, VALOR_FC, ESTADO_FC, FECHA_FC,HORA_FC;
    Activity activity;

    //Opciones de cada registro
    Dialog opciones,lista_personas;
    RecyclerView recyclerView;
    TextView fecha,registro;
    EditText titulo,estado;
    ImageButton enviarfc,editarfc,borrarfc,guardarfc;
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



    public HistoricosFCAdapter(Activity activity, Context context, ArrayList COLUMNA_ID_FC, ArrayList TITULO_FC, ArrayList VALOR_FC,
                               ArrayList ESTADO_FC, ArrayList FECHA_FC, ArrayList HORA_FC,
                               Dialog opciones){

        this.activity=activity;
        this.context=context;
        this.COLUMNA_ID_FC=COLUMNA_ID_FC;
        this.TITULO_FC=TITULO_FC;
        this.VALOR_FC=VALOR_FC;
        this.ESTADO_FC=ESTADO_FC;
        this.FECHA_FC=FECHA_FC;
        this.HORA_FC=HORA_FC;
        this.opciones=opciones;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.historicofc,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        lista_personas=new Dialog(context);

        holder.layout_historicofc.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                registro=opciones.findViewById(R.id.registro);
                titulo=opciones.findViewById(R.id.titulo);
                estado=opciones.findViewById(R.id.estado);
                fecha=opciones.findViewById(R.id.fecha);
                editarfc=opciones.findViewById(R.id.editarfc);
                guardarfc=opciones.findViewById(R.id.guardarfc);
                enviarfc=opciones.findViewById(R.id.enviarfc);
                borrarfc=opciones.findViewById(R.id.borrarfc);

                guardarfc.setVisibility(View.GONE);

                titulo.setFocusable(false);
                titulo.setEnabled(false);
                titulo.setCursorVisible(false);
                estado.setFocusable(false);
                estado.setEnabled(false);
                estado.setCursorVisible(false);


                titulo.setText(String.valueOf(TITULO_FC.get(position)).trim());
                registro.setText(String.valueOf(VALOR_FC.get(position)));
                estado.setText(String.valueOf(ESTADO_FC.get(position)).trim());
                fecha.setText(String.valueOf(FECHA_FC.get(position))+" "+String.valueOf(HORA_FC.get(position)));

                enviarfc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        compartirNota(String.valueOf(VALOR_FC.get(position)),
                                titulo.getText().toString().trim(),
                                estado.getText().toString().trim(),
                                String.valueOf(FECHA_FC.get(position))+" "+String.valueOf(HORA_FC.get(position)));
                    }
                });

                editarfc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guardarfc.setVisibility(View.VISIBLE);
                        editarfc.setVisibility(View.GONE);
                        titulo.setFocusable(true);
                        titulo.setEnabled(true);
                        titulo.setCursorVisible(true);
                        titulo.setFocusableInTouchMode(true);
                        estado.setFocusable(true);
                        estado.setEnabled(true);
                        estado.setCursorVisible(true);
                        estado.setFocusableInTouchMode(true);
                    }
                });

                guardarfc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guardarfc.setVisibility(View.GONE);
                        editarfc.setVisibility(View.VISIBLE);
                        titulo.setFocusable(false);
                        titulo.setEnabled(false);
                        titulo.setCursorVisible(false);
                        estado.setFocusable(false);
                        estado.setEnabled(false);
                        estado.setCursorVisible(false);
                        MyDataBaseHelper myDB = new MyDataBaseHelper(context);
                        myDB.updateDataFC (String.valueOf(COLUMNA_ID_FC.get(position)),
                                titulo.getText().toString().trim(),
                                String.valueOf(VALOR_FC.get(position)),
                                estado.getText().toString().trim(),
                                String.valueOf(FECHA_FC.get(position)),
                                String.valueOf(HORA_FC.get(position)));

                    }
                });

                borrarfc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                        builder.setTitle("Eliminar");
                        builder.setMessage("¿Desea eliminar este registro?");
                        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyDataBaseHelper myDB = new MyDataBaseHelper(context);
                                myDB.deleteOneRowFC(String.valueOf(COLUMNA_ID_FC.get(position)));
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

                opciones.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //Perfil.setCanceledOnTouchOutside(false);
                opciones.show();

                /*Intent intent=new Intent(context, UpdateStickNoteActivity.class);
                intent.putExtra("COLUMNA",String.valueOf(COLUMNA_ID.get(position)));
                intent.putExtra("TITULO",String.valueOf(TITULO_REC.get(position)));
                intent.putExtra("CONTENIDO",String.valueOf(CONTENIDO_REC.get(position)));
                intent.putExtra("COLOR",String.valueOf(COLOR_REC.get(position)));
                intent.putExtra("FECHA",String.valueOf(FECHA_REC.get(position)));
                intent.putExtra("HORA",String.valueOf(HORA_REC.get(position)));
                intent.putExtra("IMPORTANCIA",String.valueOf(IMPORTANCIA_REC.get(position)));*/


            }
        });


        //Objetos en la nota:
        holder.titulofc.setText(String.valueOf(TITULO_FC.get(position)));
        holder.valorfc.setText(String.valueOf(VALOR_FC.get(position)));
        holder.estadofc.setText(String.valueOf(ESTADO_FC.get(position)));
        holder.fechafc.setText(String.valueOf(FECHA_FC.get(position)));
        holder.horafc.setText(String.valueOf(HORA_FC.get(position)));


    }

    @Override
    public int getItemCount() {
        return TITULO_FC.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titulofc ,valorfc,estadofc, horafc, fechafc;
        LinearLayout layout_historicofc;
        CardView historicofc_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layout_historicofc=itemView.findViewById(R.id.layout_historicofc);
            titulofc=itemView.findViewById(R.id.titulofc);
            valorfc=itemView.findViewById(R.id.valorfc);
            estadofc=itemView.findViewById(R.id.estadofc);
            fechafc=itemView.findViewById(R.id.fechafc);
            horafc=itemView.findViewById(R.id.horafc);

            historicofc_item=itemView.findViewById(R.id.historicofc_item);

        }
    }

    public void compartirNota(String registro, String titulo,String estado, String fecha) {
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
                                        enviarRegistro(registro,titulo,estado,fecha);
                                    }
                                }
                            });

                        } else if (task.getResult().getValue().toString().equals("NO")) {
                            enviarRegistro(registro,titulo,estado,fecha);
                        }

                    }
                }
            });
        } else {
            Toast.makeText(context, "Sin conexión a internet para compartir", Toast.LENGTH_SHORT).show();
        }

    }

    public void enviarRegistro(String registro, String titulo,String estado, String fecha){

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
        datosRegistro[2]=estado;
        datosRegistro[3]=fecha;

        userAdapter=new UserAdapter(context,mUsers,"historicos",datosRegistro);
        recyclerView.setAdapter(userAdapter);

        lista_personas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Perfil.setCanceledOnTouchOutside(false);
        lista_personas.show();

    }
}
