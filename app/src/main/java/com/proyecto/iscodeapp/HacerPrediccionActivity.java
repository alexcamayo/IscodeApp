package com.proyecto.iscodeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyecto.iscodeapp.Adapters.HistoricosPrediccionAdapter;
import com.proyecto.iscodeapp.Adapters.UserAdapter;
import com.proyecto.iscodeapp.Databases.MyDataBaseHelper;
import com.proyecto.iscodeapp.Databases.MyDataBasePersonas;
import com.proyecto.iscodeapp.Databases.SubirDb;
import com.proyecto.iscodeapp.Funciones.DescargarPersonas;
import com.proyecto.iscodeapp.Funciones.RevisarConexion;
import com.proyecto.iscodeapp.Models.User;
//import com.chaquo.python.PyObject;
//import com.chaquo.python.Python;
//import com.chaquo.python.android.AndroidPlatform;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class HacerPrediccionActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    Button predecir_btn;
    TextView prediccion,fecha;
    EditText titulo;
    LottieAnimationView pensando,resultado_animacionBien,resultado_animacionMal;
    ImageButton enviar,guardar;
    RelativeLayout pensando_layout;
    ScrollView resultado_layout;
    MaterialButton historicos_btn;

    //Dial
    Dialog lista_personas;
    RecyclerView recyclerView;
    List<User> mUsers;
    SharedPreferences preferences_archivos,preferences;
    String[] datosRegistro=new String[4];
    UserAdapter userAdapter;

    //Tiempo actual
    Calendar calendar;
    int dia, mes, año, hora, minutos, segundos;
    String diaStr, mesStr, horaStr, minutoStr,segundoStr;

    Spinner spin_gen,spin_chestpain,spin_azucar, spin_electro,spin_ejercicio,spin_stpendiente,spin_fluor,spin_thal;
    EditText atr1, atr4, atr5, atr8, atr10;
    String atr2, atr3, atr6, atr7, atr9, atr11, atr12, atr13;
    int atr2_pos, atr3_pos, atr6_pos, atr7_pos, atr9_pos, atr11_pos, atr12_pos, atr13_pos =0;

    //Cambiar modelo
    CardView atributo1,atributo2,atributo3,atributo4,atributo5,atributo6,
            atributo7,atributo8,atributo9,atributo10,atributo11,atributo12,
            atributo13;
    ImageButton cambiarlayout;
    String estadoLayout="modeloA";
    ScrollView layoutscrolldata;
    RelativeLayout cargandolayout;

    int datos_completos=0; //Para comprobar que se no hayan campos vacíos

    //Mostrar PopUp con lista
    ListView listView;
    Button b_elegirFC;
    Dialog FCList;
    RelativeLayout layout_defaultFClist,layout_listFC;

    Interpreter interpreter;

    //Guardar registro
    MyDataBaseHelper myDb;


    Dialog PrediccionDialog;


    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    String id_usuario;
    boolean connected;
    String PrimeraVezSesion="";
    Dialog PersonasDialog;

    String HayDb="";


    //Seleccionar registro
    List<String> myListaFC=new ArrayList<>();
    List<String> myListaFCPresentar=new ArrayList<>();
    ArrayAdapter<String> myAdapterFC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hacer_prediccion);

        RevisarConexion revisarConexion=new RevisarConexion(HacerPrediccionActivity.this);
        connected=revisarConexion.isConnected();

        preferences_archivos = getSharedPreferences("ARCHIVOS_USUARIO", MODE_PRIVATE);
        preferences=getSharedPreferences("DATOS_LOGIN",MODE_PRIVATE);
        PrimeraVezSesion=preferences.getString("PrimeraVezSesion","");
        HayDb=preferences_archivos.getString("HayDBrec","");

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();


        PrediccionDialog = new Dialog(this);
        FCList = new Dialog(this);
        lista_personas=new Dialog(this);


        //Database
        myDb=new MyDataBaseHelper(HacerPrediccionActivity.this);

        //Cardviews
        layoutscrolldata=findViewById(R.id.layoutscrolldata);
        cambiarlayout=findViewById(R.id.cambiarlayout);
        cargandolayout=findViewById(R.id.cargandolayout);cargandolayout.setVisibility(View.GONE);
        atributo1=findViewById(R.id.atributo1);
        atributo2=findViewById(R.id.atributo2);
        atributo3=findViewById(R.id.atributo3);
        atributo4=findViewById(R.id.atributo4);
        atributo5=findViewById(R.id.atributo5);
        atributo6=findViewById(R.id.atributo6);
        atributo7=findViewById(R.id.atributo7);
        atributo8=findViewById(R.id.atributo8);
        atributo9=findViewById(R.id.atributo9);
        atributo10=findViewById(R.id.atributo10);
        atributo11=findViewById(R.id.atributo11);
        atributo12=findViewById(R.id.atributo12);
        atributo13=findViewById(R.id.atributo13);

        //Datos
        atr1=(EditText) findViewById(R.id.data1);
        spin_gen=findViewById(R.id.spinner_gen);
        spin_chestpain= (Spinner)findViewById(R.id.spinner_chestpain);
        atr4=(EditText) findViewById(R.id.data4);
        atr5=(EditText) findViewById(R.id.data5);
        spin_azucar= (Spinner)findViewById(R.id.spinner_azucar);
        spin_electro =(Spinner) findViewById(R.id.spinner_electro);
        atr8=(EditText) findViewById(R.id.data8);
        b_elegirFC=findViewById(R.id.button_elegirFC);
        spin_ejercicio=(Spinner)findViewById(R.id.spinner_ejercicio);
        atr10=(EditText) findViewById(R.id.data10);
        spin_stpendiente=(Spinner)findViewById(R.id.spinner_stpendiente);
        spin_fluor=(Spinner)findViewById(R.id.spinner_fluor);
        spin_thal=(Spinner)findViewById(R.id.spinner_thal);
        predecir_btn = findViewById(R.id.predecir_btn);

        String [] opcion_gen={"Masculino","Femenino"};
        ArrayAdapter <String> adapter_gen = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,opcion_gen);
        spin_gen.setAdapter(adapter_gen);

        String [] opcion_chestpain={"Típica angina","Atípica angina","No angina","Asintomático"};
        ArrayAdapter <String> adapter_chestpain = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,opcion_chestpain);
        spin_chestpain.setAdapter(adapter_chestpain);

        String [] opcion_azucar={"Sí","No"};
        ArrayAdapter <String> adapter_azucar = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,opcion_azucar);
        spin_azucar.setAdapter(adapter_azucar);

        String [] opcion_electro={"Normal","Anomalía en la onda ST-T","Hipertrofia ventricular izquierda probable o definitiva"};
        ArrayAdapter <String> adapter_electro = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,opcion_electro);
        spin_electro.setAdapter(adapter_electro);

        String [] opcion_ejercicio={"Sí","No"};
        ArrayAdapter <String> adapter_ejercicio = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,opcion_ejercicio);
        spin_ejercicio.setAdapter(adapter_ejercicio);

        String [] opcion_stpendiente={"Ascendiendo","Constante","Descendiendo"};
        ArrayAdapter <String> adapter_stpendiente = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,opcion_stpendiente);
        spin_stpendiente.setAdapter(adapter_stpendiente);

        String [] opcion_fluor={"0","1","2","3"};
        ArrayAdapter <String> adapter_fluor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,opcion_fluor);
        spin_fluor.setAdapter(adapter_fluor);

        String [] opcion_thal={"Normal","Defecto fijo","Defecto reversible"};
        ArrayAdapter <String> adapter_thal = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,opcion_thal);
        spin_thal.setAdapter(adapter_thal);

        //Leer los datos de una predicción anterior
        leerDatosAntiguos();




        predecir_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datos_completos=datos();
                if (datos_completos==1){
                    ShowResultado();
                }
            }
        });

        b_elegirFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowListFC();
            }
        });

        cambiarlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layoutscrolldata.setVisibility(View.GONE);
                predecir_btn.setVisibility(View.GONE);
                cargandolayout.setVisibility(View.VISIBLE);

                new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {

                        if (estadoLayout.equals("modeloA")){
                            atributo7.setVisibility(View.GONE);
                            atributo10.setVisibility(View.GONE);
                            atributo11.setVisibility(View.GONE);
                            atributo12.setVisibility(View.GONE);
                            atributo13.setVisibility(View.GONE);

                            estadoLayout="modeloB";
                        }
                        else if (estadoLayout.equals("modeloB")){
                            atributo7.setVisibility(View.VISIBLE);
                            atributo10.setVisibility(View.VISIBLE);
                            atributo11.setVisibility(View.VISIBLE);
                            atributo12.setVisibility(View.VISIBLE);
                            atributo13.setVisibility(View.VISIBLE);
                            estadoLayout="modeloA";
                        }
                        cargandolayout.setVisibility(View.GONE);
                        layoutscrolldata.setVisibility(View.VISIBLE);
                        predecir_btn.setVisibility(View.VISIBLE);

                    }
                }.start();

            }
        });

        cambiarlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator =(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                Toast.makeText(HacerPrediccionActivity.this,"Cambiar la cantidad de atributos",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    private void leerDatosAntiguos(){
        //Colocar los atributos del paciente
        Cursor cursor =myDb.readAllDataPRED();
        int cont=0;
        if(cursor.getCount()==0){

        }
        else {
            while (cursor.moveToNext()) {
                cont=cont+1;
                if (cursor.getCount()==cont){
                    atr1.setText(cursor.getString(6));
                    spin_gen.setSelection(Integer.parseInt(cursor.getString(7)));
                    spin_chestpain.setSelection(Integer.parseInt(cursor.getString(8)));
                    atr4.setText(cursor.getString(9));
                    atr5.setText(cursor.getString(10));
                    spin_azucar.setSelection(Integer.parseInt(cursor.getString(11)));
                    spin_electro.setSelection(Integer.parseInt(cursor.getString(12)));
                    atr8.setText(cursor.getString(13));
                    spin_ejercicio.setSelection(Integer.parseInt(cursor.getString(14)));
                    atr10.setText(cursor.getString(15));
                    spin_stpendiente.setSelection(Integer.parseInt(cursor.getString(16)));
                    spin_fluor.setSelection(Integer.parseInt(cursor.getString(17)));
                    spin_thal.setSelection(Integer.parseInt(cursor.getString(18)));
                }

            }
        }
        calendar=Calendar.getInstance();
        String nacimiento=preferences.getString("Fecha","");
        int dia_fecha=Integer.parseInt(nacimiento.substring(0,nacimiento.indexOf('/')));
        int mes_fecha=Integer.parseInt(nacimiento.substring(nacimiento.indexOf('/')+1,nacimiento.lastIndexOf('/')));
        int año_fecha=Integer.parseInt(nacimiento.substring(nacimiento.lastIndexOf('/')+1,nacimiento.length()));
        LocalDate birthday = LocalDate.of(año_fecha, mes_fecha, dia_fecha);
        LocalDate now = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        Period period = Period.between(birthday, now);
        atr1.setText(""+period.getYears());
    }


    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = null;
        if (estadoLayout.equals("modeloA")){
            assetFileDescriptor = this.getAssets().openFd("modelC.tflite");    
        }
        else if (estadoLayout.equals("modeloB")){
            assetFileDescriptor = this.getAssets().openFd("modelE.tflite");
        }
        
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset=assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,length);

    }

    @SuppressLint("SetTextI18n")
    public String doInference(){

        try {
            interpreter = new Interpreter(loadModelFile(),null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        float[] mean = new float[0];
        float[] std = new float[0];
        float[] muestra = new float[0];
        float[] inputs=new float[0];

        if (estadoLayout.equals("modeloA")){
            mean = new float[]{54.542088f, 0.676768f, 3.158249f, 131.693603f, 247.350168f, 0.144781f, 0.996633f,
                    149.599327f, 0.326599f, 1.055556f, 1.602694f, 0.676768f, 4.730640f};
            std = new float[]{9.049736f, 0.468500f, 0.964859f, 17.762806f, 51.997583f, 0.352474f, 0.994914f,
                    22.941562f, 0.469761f, 1.166123f, 0.618187f, 0.938965f, 1.938629f};
            muestra=new float[13];
            muestra[0] = Float.parseFloat( atr1.getText().toString().trim());
            muestra[1] =         Float.parseFloat( atr2.trim());
            muestra[2] =        Float.parseFloat( atr3.trim());
            muestra[3] =         Float.parseFloat( atr4.getText().toString().trim());
            muestra[4] =        Float.parseFloat( atr5.getText().toString().trim());
            muestra[5] =         Float.parseFloat( atr6.trim());
            muestra[6] =         Float.parseFloat( atr7.trim());
            muestra[7] =        Float.parseFloat( atr8.getText().toString().trim());
            muestra[8] =        Float.parseFloat( atr9.trim());
            muestra[9] =         Float.parseFloat( atr10.getText().toString().trim());
            muestra[10] =         Float.parseFloat( atr11.trim());
            muestra[11] =        Float.parseFloat( atr12.trim());
            muestra[12] =        Float.parseFloat( atr13.trim());

            inputs=new float[13];
            for (int i=0;i<muestra.length;i++){
                inputs[i]=(muestra[i] - mean[i]) / (std[i]);
            }
        }
        else if (estadoLayout.equals("modeloB")){
            mean = new float[]{52.634586f, 0.742857f, 3.166917f, 132.769925f,
                    246.380451f, 0.151880f, 141.270677f,  0.375940f};
            std = new float[]{9.435434f, 0.437388f, 0.960415f, 17.805585f,
                    57.549281f, 0.359174f, 25.028757f, 0.484729f};
            muestra=new float[8];
            muestra[0] = Float.parseFloat( atr1.getText().toString().trim());
            muestra[1] =         Float.parseFloat( atr2.trim());
            muestra[2] =        Float.parseFloat( atr3.trim());
            muestra[3] =         Float.parseFloat( atr4.getText().toString().trim());
            muestra[4] =        Float.parseFloat( atr5.getText().toString().trim());
            muestra[5] =         Float.parseFloat( atr6.trim());
            muestra[6] =        Float.parseFloat( atr8.getText().toString().trim());
            muestra[7] =        Float.parseFloat( atr9.trim());

            inputs=new float[8];
            for (int i=0;i<muestra.length;i++){
                inputs[i]=(muestra[i] - mean[i]) / (std[i]);
            }
        }

        float[][] output = new  float[1][1];// Output tensor shape is [3, 2].

        interpreter.run(inputs,output);

        Log.d("tag20","Este es el texto: "+ Arrays.deepToString(output));

        float val_ML=output[0][0];
        String pred="";
        if (val_ML>=0.5){
            pred="SÍ";
        }
        else{
            pred="NO";
        }

        return pred;
    }

    public int datos(){
        String sel_gen=spin_gen.getSelectedItem().toString();
        if (sel_gen.equals("Masculino")){
            atr2="1.0";
            atr2_pos=0;
        }
        else if(sel_gen.equals("Femenino")){
            atr2="0.0";
            atr2_pos=1;
        }

        String sel_chestpain=spin_chestpain.getSelectedItem().toString();
        if (sel_chestpain.equals("Típica angina")){
            atr3="1.0";
            atr3_pos=0;
        }
        else if(sel_chestpain.equals("Atípica angina")){
            atr3="2.0";
            atr3_pos=1;
        }
        else if(sel_chestpain.equals("No angina")){
            atr3="3.0";
            atr3_pos=2;
        }
        else if(sel_chestpain.equals("Asintomático")){
            atr3="4.0";
            atr3_pos=3;
        }

        String sel_azucar=spin_azucar.getSelectedItem().toString();
        if (sel_azucar.equals("Sí")){
            atr6="1.0";
            atr6_pos=0;
        }
        else if(sel_azucar.equals("No")){
            atr6="0.0";
            atr6_pos=1;
        }

        String sel_electro=spin_electro.getSelectedItem().toString();
        if (sel_electro.equals("Normal")){
            atr7="0.0";
            atr7_pos=0;
        }
        else if(sel_electro.equals("Anomalía en la onda ST-T")){
            atr7="1.0";
            atr7_pos=1;
        }
        else if(sel_electro.equals("Hipertrofia ventricular izquierda probable o definitiva")){
            atr7="2.0";
            atr7_pos=2;
        }

        String sel_ejercicio=spin_ejercicio.getSelectedItem().toString();
        if (sel_ejercicio.equals("Sí")){
            atr9="1.0";
            atr9_pos=0;
        }
        else if(sel_ejercicio.equals("No")){
            atr9="0.0";
            atr9_pos=1;
        }

        String sel_stpendiente=spin_stpendiente.getSelectedItem().toString();
        if (sel_stpendiente.equals("Ascendiendo")){
            atr11="1.0";
            atr11_pos=0;
        }
        else if(sel_stpendiente.equals("Constante")){
            atr11="2.0";
            atr11_pos=1;
        }
        else if(sel_stpendiente.equals("Descendiendo")){
            atr11="3.0";
            atr11_pos=2;
        }

        String sel_fluor=spin_fluor.getSelectedItem().toString();
        if (sel_fluor.equals("0")){
            atr12="0.0";
            atr12_pos=0;
        }
        else if(sel_fluor.equals("1")){
            atr12="1.0";
            atr12_pos=1;
        }
        else if(sel_fluor.equals("2")){
            atr12="2.0";
            atr12_pos=2;
        }
        else if(sel_fluor.equals("3")){
            atr12="3.0";
            atr12_pos=3;
        }

        String sel_thal=spin_thal.getSelectedItem().toString();
        if (sel_thal.equals("Normal")){
            atr13="3.0";
            atr13_pos=0;
        }
        else if(sel_thal.equals("Defecto fijo")){
            atr13="6.0";
            atr13_pos=1;
        }
        else if(sel_thal.equals("Defecto reversible")){
            atr13="7.0";
            atr13_pos=2;
        }



        if(TextUtils.isEmpty(atr1.getText().toString().trim())){
            Toast.makeText(this,"Ingrese el atributo N° 1",Toast.LENGTH_SHORT).show();
            return 0;
        }

        if(TextUtils.isEmpty(atr2.trim())){
            Toast.makeText(this,"Ingrese el atributo N° 2",Toast.LENGTH_SHORT).show();
            return 0;
        }

        if(TextUtils.isEmpty(atr3.trim())){
            Toast.makeText(this,"Ingrese el atributo N° 3",Toast.LENGTH_SHORT).show();
            return 0;
        }

        if(TextUtils.isEmpty(atr4.getText().toString().trim())){
            Toast.makeText(this,"Ingrese el atributo N° 4",Toast.LENGTH_SHORT).show();
            return 0;
        }

        if(TextUtils.isEmpty(atr5.getText().toString().trim())){
            Toast.makeText(this,"Ingrese el atributo N° 5",Toast.LENGTH_SHORT).show();
            return 0;
        }

        if(TextUtils.isEmpty(atr6.trim())){
            Toast.makeText(this,"Ingrese el atributo N° 6",Toast.LENGTH_SHORT).show();
            return 0;
        }

        if(TextUtils.isEmpty(atr8.getText().toString().trim())){
            Toast.makeText(this,"Ingrese el atributo N° 8",Toast.LENGTH_LONG).show();
            return 0;
        }

        if(TextUtils.isEmpty(atr9.trim())){
            Toast.makeText(this,"Ingrese el atributo N° 9",Toast.LENGTH_SHORT).show();
            return 0;
        }

        if (estadoLayout.equals("modeloA")){
            if(TextUtils.isEmpty(atr7.trim())){
                Toast.makeText(this,"Ingrese el atributo N° 7",Toast.LENGTH_SHORT).show();
                return 0;
            }
            if(TextUtils.isEmpty(atr10.getText().toString().trim())){
                Toast.makeText(this,"Ingrese el atributo N° 10",Toast.LENGTH_SHORT).show();
                return 0;
            }
            if(TextUtils.isEmpty(atr11.trim())){
                Toast.makeText(this,"Ingrese el atributo N° 11",Toast.LENGTH_SHORT).show();
                return 0;
            }
            if(TextUtils.isEmpty(atr12.trim())){
                Toast.makeText(this,"Ingrese el atributo N° 12",Toast.LENGTH_SHORT).show();
                return 0;
            }
            if(TextUtils.isEmpty(atr13.trim())){
                Toast.makeText(this,"Ingrese el atributo N° 13",Toast.LENGTH_SHORT).show();
                return 0;
            }
        }


        

        return 1;
    }

    public void ShowResultado(){
        PrediccionDialog.setContentView(R.layout.prediccion_dialog);
        PrediccionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        PrediccionDialog.show();

        titulo=PrediccionDialog.findViewById(R.id.titulo);
        prediccion=PrediccionDialog.findViewById(R.id.prediccion);
        fecha=PrediccionDialog.findViewById(R.id.fecha);
        resultado_animacionBien=PrediccionDialog.findViewById(R.id.resultado_animacionBien);
        resultado_animacionMal=PrediccionDialog.findViewById(R.id.resultado_animacionMal);
        pensando=PrediccionDialog.findViewById(R.id.pensando);
        guardar=PrediccionDialog.findViewById(R.id.guardar);
        enviar=PrediccionDialog.findViewById(R.id.enviar);
        historicos_btn=PrediccionDialog.findViewById(R.id.historicos_btn);

        pensando_layout=PrediccionDialog.findViewById(R.id.pensando_layout);
        resultado_layout=PrediccionDialog.findViewById(R.id.resultado_layout);

        pensando_layout.setVisibility(View.VISIBLE);
        resultado_layout.setVisibility(View.GONE);

        String[] primer_guardar = {"SI"};
        long[] id = new long[1];


        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {

                pensando_layout.setVisibility(View.GONE);
                resultado_layout.setVisibility(View.VISIBLE);

            }
        }.start();

        String resultado_previo=doInference();
        prediccion.setText("Usted "+resultado_previo+" tiene riesgo de sufrir una isquemia cardiaca");

        if(resultado_previo.equals("SÍ")){
            resultado_animacionBien.setVisibility(View.GONE);
            resultado_animacionMal.setVisibility(View.VISIBLE);
            resultado_animacionMal.playAnimation();
        }
        else if(resultado_previo.equals("NO")){
            resultado_animacionBien.setVisibility(View.VISIBLE);
            resultado_animacionMal.setVisibility(View.GONE);
            resultado_animacionBien.playAnimation();
        }
        obtenerTiempo();
        fecha.setText(diaStr+"/"+mesStr+"/"+año+"  "+horaStr+":"+minutoStr+":"+segundoStr);


        historicos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gohistoricos=new Intent(HacerPrediccionActivity.this,HistoricosActivity.class);
                startActivity(gohistoricos);
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(titulo.getText().toString().trim().equals("")){
                    Toast.makeText(HacerPrediccionActivity.this,"Debe ingresar un título",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(primer_guardar[0].equals("SI")){
                        id[0] =myDb.agregar_prediccion(titulo.getText().toString().trim(),resultado_previo +" hay riesgo",
                                diaStr+"/"+mesStr+"/"+año,horaStr+":"+minutoStr,
                                estadoLayout,
                                atr1.getText().toString().trim(),
                                String.valueOf(atr2_pos),
                                String.valueOf(atr3_pos),
                                atr4.getText().toString().trim(),
                                atr5.getText().toString().trim(),
                                String.valueOf(atr6_pos),
                                String.valueOf(atr7_pos),
                                atr8.getText().toString().trim(),
                                String.valueOf(atr9_pos),
                                atr10.getText().toString().trim(),
                                String.valueOf(atr11_pos),
                                String.valueOf(atr12_pos),
                                String.valueOf(atr13_pos));
                        primer_guardar[0] ="NO";
                    }
                    else if(primer_guardar[0].equals("NO")){
                        myDb.updateDataPRED(""+id[0],titulo.getText().toString().trim(),resultado_previo+ " hay riesgo",
                                diaStr+"/"+mesStr+"/"+año,horaStr+":"+minutoStr,
                                estadoLayout,
                                atr1.getText().toString().trim(),
                                String.valueOf(atr2_pos),
                                String.valueOf(atr3_pos),
                                atr4.getText().toString().trim(),
                                atr5.getText().toString().trim(),
                                String.valueOf(atr6_pos),
                                String.valueOf(atr7_pos),
                                atr8.getText().toString().trim(),
                                String.valueOf(atr9_pos),
                                atr10.getText().toString().trim(),
                                String.valueOf(atr11_pos),
                                String.valueOf(atr12_pos),
                                String.valueOf(atr13_pos));
                    }


                    SubirDb subirDb=new SubirDb(HacerPrediccionActivity.this,HacerPrediccionActivity.this);
                    subirDb.actualizardb();

                    preferences_archivos = getSharedPreferences("ARCHIVOS_USUARIO", MODE_PRIVATE);
                    preferences_archivos.edit().putString("HayDBrec","SI").apply();
                }


            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(titulo.getText().toString().trim().equals("")){
                    Toast.makeText(HacerPrediccionActivity.this,"Debe ingresar un título",Toast.LENGTH_SHORT).show();
                }
                else{
                    compartirNota(resultado_previo+" hay riesgo",titulo.getText().toString().trim(),
                            diaStr+"/"+mesStr+"/"+año+"  "+horaStr+":"+minutoStr);
                }

            }
        });


    }

    public void ShowListFC(){
        FCList.setContentView(R.layout.fclista);
        listView=FCList.findViewById(R.id.lista_datosFC);
        layout_defaultFClist=FCList.findViewById(R.id.layout_defaultFClist);
        layout_listFC=FCList.findViewById(R.id.layout_listFC);
        Button b_goregistrarFC=FCList.findViewById(R.id.button_GoRegistrarFC);

        layout_defaultFClist.setVisibility(View.GONE);
        layout_listFC.setVisibility(View.GONE);

        b_goregistrarFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_goregistroFC= new Intent(HacerPrediccionActivity.this,BluetoothActivity.class);
                startActivity(intent_goregistroFC);
            }
        });

        FCList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        FCList.show();


        if (HayDb.equals("SI")){
            int i=0;
            Cursor cursor=myDb.readAllDataFC();
            if(cursor.getCount()==0){
                layout_defaultFClist.setVisibility(View.VISIBLE);
                layout_listFC.setVisibility(View.GONE);
            }
            else {
                layout_defaultFClist.setVisibility(View.GONE);
                layout_listFC.setVisibility(View.VISIBLE);

                myListaFC.clear();
                myListaFCPresentar.clear();
                while (cursor.moveToNext()) {
                    i=i+1;
                    myListaFCPresentar.add(i+") "+cursor.getString(2)+" lpm");
                    myListaFC.add(cursor.getString(2));
                    myAdapterFC = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,myListaFCPresentar);
                    listView.setAdapter(myAdapterFC);
                }
            }
            listView.setOnItemClickListener(this);
        }
        else if(HayDb.equals("NO")){
            layout_defaultFClist.setVisibility(View.VISIBLE);
            layout_listFC.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {}

    public void obtenerTiempo(){
        //Hora actual
        calendar= Calendar.getInstance();
        dia=calendar.get(Calendar.DAY_OF_MONTH);
        mes =calendar.get(Calendar.MONTH)+1;
        año=calendar.get(Calendar.YEAR);
        hora=calendar.get(Calendar.HOUR_OF_DAY);
        minutos=calendar.get(Calendar.MINUTE);
        segundos=calendar.get(Calendar.SECOND);

        diaStr = dia < 10 ? "0"+dia : String.valueOf(dia);
        mesStr = mes < 10 ? "0"+mes : String.valueOf(mes);
        horaStr = hora < 10 ? "0"+hora : String.valueOf(hora);
        minutoStr = minutos < 10 ? "0"+minutos : String.valueOf(minutos);
        segundoStr = segundos < 10 ? "0"+segundos : String.valueOf(segundos);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String datoFC=myListaFC.get(position);

        Log.d("TAG745","Valor: "+datoFC);

        //SharedPreferences preferencesReadFC = getSharedPreferences("DATOS_FC", MODE_PRIVATE);
        //String key="RFC"+(position+1);
        //String datoFC=preferencesReadFC.getString(key,"");
        atr8.setText(datoFC);
        FCList.dismiss();
    }

    public void compartirNota(String registro, String titulo, String fecha){

        if (connected){
            database.child("InfoGeneral").child("RefreshPersonas").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        //Error
                    }
                    else {
                        if (task.getResult().getValue().toString().equals("SI") || PrimeraVezSesion.equals("SI")){

                            preferences.edit().putString("PrimeraVezSesion","NO").apply();
                            preferences_archivos.edit().putString("PersonasDescargadas","NO").apply();
                            PrimeraVezSesion="NO";

                            PersonasDialog=new Dialog(HacerPrediccionActivity.this);
                            PersonasDialog.setContentView(R.layout.personas_dialog);
                            DescargarPersonas descargarPersonas=new DescargarPersonas(HacerPrediccionActivity.this,HacerPrediccionActivity.this);
                            descargarPersonas.actualizar(PersonasDialog,"prediccion");

                            PersonasDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if(preferences_archivos.getString("PersonasDescargadas","NO").equals("SI")){
                                        enviarRegistro(registro,titulo,fecha);
                                    }
                                }
                            });

                        }
                        else if(task.getResult().getValue().toString().equals("NO")){
                            enviarRegistro(registro,titulo,fecha);
                        }

                    }
                }
            });
        }
        else {
            Toast.makeText(HacerPrediccionActivity.this,"Sin conexión a internet para compartir", Toast.LENGTH_SHORT).show();
        }

    }

    public void enviarRegistro(String registro, String titulo, String fecha){

        lista_personas.setContentView(R.layout.lista_personas);
        recyclerView=lista_personas.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsers=new ArrayList<>();

        MyDataBasePersonas myDataBasePersonas=new MyDataBasePersonas(HacerPrediccionActivity.this);
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
                String fechausr=cursor.getString(4);
                String imageurl=cursor.getString(5);

                User user=new User();
                user.setId(personasid);
                user.setUsername(nombre);
                user.setCorreo(correo);
                user.setFecha(fechausr);
                user.setImageurl(imageurl);
                mUsers.add(user);
            }
        }
        datosRegistro[0]=registro;
        datosRegistro[1]=titulo;
        datosRegistro[2]=fecha;

        userAdapter=new UserAdapter(this,mUsers,"historicos_pred",datosRegistro);
        recyclerView.setAdapter(userAdapter);

        lista_personas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Perfil.setCanceledOnTouchOutside(false);
        lista_personas.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

