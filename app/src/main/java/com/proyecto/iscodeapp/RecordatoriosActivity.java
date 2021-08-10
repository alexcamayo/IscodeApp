package com.proyecto.iscodeapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyecto.iscodeapp.Adapters.RecordatoriosAdapter;
import com.proyecto.iscodeapp.Databases.MyDataBaseHelper;
import com.proyecto.iscodeapp.Databases.SubirDb;
import com.proyecto.iscodeapp.Funciones.RevisarConexion;
import com.proyecto.iscodeapp.Notificaciones.AlarmasRecordatorios;

import java.util.ArrayList;

public class RecordatoriosActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageButton nuevo_recordatorio;
    MyDataBaseHelper myDB;
    ArrayList<String> COLUMNA_ID,TITULO_REC,CONTENIDO_REC,COLOR_REC,FECHA_REC,HORA_REC,IMPORTANCIA_REC;
    RecordatoriosAdapter customadapter;
    RelativeLayout layout_default;

    SharedPreferences preferences_archivos;


    //SUBIR DB
    String path_db="";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    String primeravez="";
    SwipeRefreshLayout refrescar_lista;
    String hayDB="";

    AlarmasRecordatorios crearAlarma;
    Boolean statusCrearAlarma=false;

    //Programar las alrmas al descargarse el db
    String columna,titulo_nota,contenido_nota,valor_color,t_fecha,t_hora,importancia_estado="";

    /*Vibrator vibrator =(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
      vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));*/

    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorios);

        RevisarConexion revisarConexion=new RevisarConexion(RecordatoriosActivity.this);
        connected=revisarConexion.isConnected();

        myDB = new MyDataBaseHelper(RecordatoriosActivity.this);

        //Guardar cosas en firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();

        SubirDb updb=new SubirDb(RecordatoriosActivity.this,this);
        preferences_archivos=getSharedPreferences("ARCHIVOS_USUARIO", MODE_PRIVATE);

        refrescar_lista=findViewById(R.id.refrescar_lista);
        recyclerView = findViewById(R.id.lista_recordatorios);
        nuevo_recordatorio = findViewById(R.id.nuevo_recordatorio);
        layout_default=findViewById(R.id.layout_default);

        primeravez=preferences_archivos.getString("PrimeraVez","");
        hayDB=preferences_archivos.getString("HayDBrec","");

        refrescar_lista.setVisibility(View.GONE);
        layout_default.setVisibility(View.VISIBLE);

        crearAlarma=new AlarmasRecordatorios(getApplicationContext(),RecordatoriosActivity.this);

        nuevo_recordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordatoriosActivity.this, CreateStickNoteActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //Leer los recordatorios

        if (hayDB.equals("SI")){
            if(primeravez.equals("SI")){
                Cursor cursor=myDB.readAllData();
                if (cursor.getCount()!=0){
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setTitle("Importar recordatorios");
                    builder.setMessage("¿Desea importar las alarmas de sus recordatorios a este dispositivo?");
                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            statusCrearAlarma=true;
                            leerRecordatorios();
                            statusCrearAlarma=false;

                            preferences_archivos.edit().putString("PrimeraVez","NO").apply();
                            primeravez="NO";
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            statusCrearAlarma=false;
                            leerRecordatorios();

                            preferences_archivos.edit().putString("PrimeraVez","NO").apply();
                            primeravez="NO";
                        }
                    });
                    builder.create().show();
                }
                else {
                    preferences_archivos.edit().putString("PrimeraVez","NO").apply();
                    primeravez="NO";
                }

            }

            else{
                leerRecordatorios();
            }
        }
        else if(hayDB.equals("NO")){
            refrescar_lista.setVisibility(View.GONE);
            layout_default.setVisibility(View.VISIBLE);
        }




        String Actualizar= preferences_archivos.getString("Actualizar","NO");
        if (Actualizar.equals("SI")){
            if(primeravez.equals("NO")){

                if (connected){
                    updb.actualizardb();
                    preferences_archivos.edit().putString("Actualizar","NO").apply();
                }
                else{
                    Toast.makeText(RecordatoriosActivity.this,"Sin conexión a internet para sincronizar información", Toast.LENGTH_SHORT).show();
                }

            }
        }

        refrescar_lista.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(hayDB.equals("SI")){

                    leerRecordatorios();
                    if (connected){
                        updb.actualizardb();
                    }
                    else{
                        Toast.makeText(RecordatoriosActivity.this,"Sin conexión a internet para sincronizar información", Toast.LENGTH_SHORT).show();
                    }



                    refrescar_lista.setVisibility(View.VISIBLE);
                    layout_default.setVisibility(View.GONE);
                }
                else{
                    refrescar_lista.setVisibility(View.GONE);
                    layout_default.setVisibility(View.VISIBLE);
                }
                refrescar_lista.setRefreshing(false);

            }
        });

        if(getIntent().hasExtra("ORIGEN")){
            if (getIntent().getStringExtra("ORIGEN").equals("mensajes")){
                crearRecordatorio();
            }
        }


    }

    private void leerRecordatorios(){
        COLUMNA_ID = new ArrayList<>();
        TITULO_REC = new ArrayList<>();
        CONTENIDO_REC = new ArrayList<>();
        COLOR_REC = new ArrayList<>();
        FECHA_REC = new ArrayList<>();
        HORA_REC = new ArrayList<>();
        IMPORTANCIA_REC = new ArrayList<>();

        leerData();

        customadapter=new RecordatoriosAdapter(RecordatoriosActivity.this,this,COLUMNA_ID, TITULO_REC,CONTENIDO_REC,
                COLOR_REC, FECHA_REC,HORA_REC,IMPORTANCIA_REC);
        recyclerView.setAdapter(customadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager( RecordatoriosActivity.this));
    }

    void leerData(){
        Cursor cursor=myDB.readAllData();
        if(cursor.getCount()==0){
            refrescar_lista.setVisibility(View.GONE);
            layout_default.setVisibility(View.VISIBLE);
        }
        else{
            while (cursor.moveToNext()){
                COLUMNA_ID.add(cursor.getString(0));
                TITULO_REC.add(cursor.getString(1));
                CONTENIDO_REC.add(cursor.getString(2));
                COLOR_REC.add(cursor.getString(3));
                FECHA_REC.add(cursor.getString(4));
                HORA_REC.add(cursor.getString(5));
                IMPORTANCIA_REC.add(cursor.getString(6));

                if(statusCrearAlarma){
                    crearAlarma.crearAlarma("recordActivity",
                            cursor.getString(0),
                            cursor.getString(4),
                            cursor.getString(5),
                            "repetir"+cursor.getString(7));
                }
            }

            refrescar_lista.setVisibility(View.VISIBLE);
            layout_default.setVisibility(View.GONE);

        }
    }


    //Origen de mensajes
    public void crearRecordatorio(){
        String recordatorio="";
        recordatorio=getIntent().getStringExtra("NUEVOREC");

        Intent intent=new Intent(RecordatoriosActivity.this , CreateStickNoteActivity.class);
        intent.putExtra("ORIGEN","mensajes");
        intent.putExtra("NUEVOREC",recordatorio);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}