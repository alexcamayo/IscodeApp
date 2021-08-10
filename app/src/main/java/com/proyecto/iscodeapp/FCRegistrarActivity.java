package com.proyecto.iscodeapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyecto.iscodeapp.Adapters.UserAdapter;
import com.proyecto.iscodeapp.Databases.MyDataBaseHelper;
import com.proyecto.iscodeapp.Databases.MyDataBasePersonas;
import com.proyecto.iscodeapp.Funciones.DescargarPersonas;
import com.proyecto.iscodeapp.Funciones.RevisarConexion;
import com.proyecto.iscodeapp.Models.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class FCRegistrarActivity extends AppCompatActivity {

    private String deviceName = null;
    private String deviceAddress;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;

    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int handlerState = 0;

    Button beat;
    LottieAnimationView beat_animation;
    CardView cdvw_iniciarFC,cdw_mostrarFC;
    ScrollView pantalla_registro;
    Button go_historicos_btn,nueva_medicion_btn;
    ImageButton nueva_medicion_2_btn,guardar_btn;
    int count_beat=0;
    Spinner spin_fc;
    AutoCompleteTextView estado;
    EditText titulo_registro;
    Button btnDesconectar;
    TextView txtView_FR,txtView_Info,textViewFR_mostrar,informacion;
    String nuevo_dato="";
    //Button ONsensor,OFFsensor;

    //Line chart
    LineChart chart;
    Thread thread;
    Button ln_feed;
    float[] datos_fc = new float[] {0f, 0f, 0f, 0f, 0f, 0f};

    String MyDataIn="";
    String Info="";
    int val1,val2=0;
    int val3,val5=-1;

    //Hora actual
    Calendar calendar;
    int dia=0;
    int mes =0;
    int año=0;
    int hora=0;
    int minutos=0;
    int segundos=0;

    String diaStr = "";
    String mesStr = "";
    String horaStr = "";
    String minutoStr = "";
    String segundoStr = "";

    //Enviar registro a personas
    ImageButton enviar_btn;
    Dialog lista_personas;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> mUsers;
    SharedPreferences preferences_personas;
    SharedPreferences preferences,preferences_archivos;
    String[] datosRegistro=new String[4];

    //Internet
    boolean connected;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    String PrimeraVezSesion="";
    Dialog PersonasDialog;
    boolean falloBtConexion=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcregistrar);


        RevisarConexion revisarConexion=new RevisarConexion(FCRegistrarActivity.this);
        connected=revisarConexion.isConnected();

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myData = FirebaseDatabase.getInstance();
        database = myData.getReference();

        preferences=getSharedPreferences("DATOS_LOGIN",MODE_PRIVATE);
        PrimeraVezSesion=preferences.getString("PrimeraVezSesion","");

        lista_personas=new Dialog(this);
        beat_animation=(LottieAnimationView)findViewById(R.id.beat_animation);
        cdvw_iniciarFC=findViewById(R.id.cdvw_iniciarFC);
        cdw_mostrarFC=findViewById(R.id.cdw_mostrarFC);
        nueva_medicion_btn=findViewById(R.id.nueva_medicion_btn);
        nueva_medicion_2_btn=findViewById(R.id.nueva_medicion_2_btn);
        enviar_btn=findViewById(R.id.enviar_btn);
        go_historicos_btn=findViewById(R.id.go_historicos_btn);
        guardar_btn=findViewById(R.id.guardar_btn);
        pantalla_registro=findViewById(R.id.pantalla_registro);
        pantalla_registro.setVisibility(View.GONE);
        cdvw_iniciarFC.setVisibility(View.VISIBLE);
        cdw_mostrarFC.setVisibility(View.GONE);
        txtView_FR=findViewById(R.id.textViewFR);
        txtView_Info=findViewById(R.id.textViewInfo);
        textViewFR_mostrar=findViewById(R.id.textViewFR_mostrar);
        titulo_registro=findViewById(R.id.titulo_registro);
        informacion=findViewById(R.id.informacion);

        nueva_medicion_btn.setVisibility(View.GONE);

        //Spinner
        estado=findViewById(R.id.customerTextView);
        String [] opcion_fc={"En reposo","Actividad física"};
        ArrayAdapter<String> adapter_fc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,opcion_fc);
        estado.setAdapter(adapter_fc);


        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null){
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show progree and connection status
            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }

        /*
        Second most important piece of Code. GUI Handler
         */
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                informacion.setText("Conectado a: "+deviceName);
                                nueva_medicion_btn.setVisibility(View.VISIBLE);
                                falloBtConexion=false;

                                break;
                            case -1:
                                informacion.setText("Falló al conectar con el sensor");
                                nueva_medicion_btn.setVisibility(View.GONE);
                                falloBtConexion=true;
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        MyDataIn= (String) msg.obj;
                        Log.d("Tag920",MyDataIn+" MyDataIN: "+MyDataIn.length());
                        Info=MyDataIn;
                        if(MyDataIn.length()<=17) {
                            Log.d("Tag920","SI ENTRO");
                            PresentarInfo(Info);
                        }
                        else{
                            PresentarInfo("");
                        }
                        break;
                }
            }
        };

        inicializarchart();

        nueva_medicion_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdvw_iniciarFC.setVisibility(View.GONE);
                pantalla_registro.setVisibility(View.VISIBLE);
                connectedThread.write("S");
            }
        });

        nueva_medicion_2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdvw_iniciarFC.setVisibility(View.GONE);
                pantalla_registro.setVisibility(View.VISIBLE);
                txtView_FR.setText("");

                //Borrar la data del chart
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);
                chart.setData(data);
                datos_fc = new float[]{0f, 0f, 0f, 0f, 0f, 0f};

                //Borrar los datos del sensor
                connectedThread.write("N");

                new CountDownTimer(500, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        connectedThread.write("S");

                    }
                }.start();

                cdw_mostrarFC.setVisibility(View.GONE);
                pantalla_registro.setVisibility(View.VISIBLE);
            }
        });

        go_historicos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_historicos=new Intent(FCRegistrarActivity.this,HistoricosActivity.class);
                startActivity(go_historicos);
                finish();
            }
        });



        guardar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerTiempo();
                MyDataBaseHelper myDb=new MyDataBaseHelper(FCRegistrarActivity.this);
                long resultado= myDb.agregar_registrofc(titulo_registro.getText().toString().trim(),
                        textViewFR_mostrar.getText().toString().trim(),
                        estado.getText().toString().trim(),
                        ""+diaStr+"/"+mesStr+"/"+año,
                        ""+horaStr+":"+minutoStr+":"+segundoStr);
                SharedPreferences preferences_archivos = getSharedPreferences("ARCHIVOS_USUARIO", MODE_PRIVATE);
                preferences_archivos.edit().putString("HayDBrec","SI").apply();
            }
        });

        enviar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartirRegistro();
            }
        });
    }

    public void PresentarInfo(String info){
        val1= info.indexOf('+');
        val2=info.lastIndexOf('+');
        val3=info.indexOf('T');
        val5=info.indexOf('Y');
        Log.d("TAG930","Indices: "+val1+","+val2);

        if (val1!=-1 && val2!=-1 && val3==-1 && val5==-1 && info.length()<=17){
            txtView_Info.setText("Leyendo...");
            String Informacion = info;
            Log.d("Tag920",Informacion+"Informacion");
            if (val1>=0 && val2>=0 && !info.equals("")){
                int inicio=val1;
                int fnal=val2;
                String new1string="";
                String new2string="";
                String new3string="";
                new1string= Informacion.substring(inicio+1,fnal);
                int val4=new1string.lastIndexOf('+');
                new2string=new1string.substring(0,val4);
                new3string=new1string.substring(val4+1,fnal-1);
                txtView_FR.setText(new3string);

                if(!nuevo_dato.equals(txtView_FR.getText().toString())){
                    nuevo_dato=txtView_FR.getText().toString();
                    addEntry();
                    beat_animation.playAnimation();
                    count_beat=count_beat+1;
                    if (count_beat==10){
                        connectedThread.write("N");
                        new CountDownTimer(500, 1000) {
                            public void onTick(long millisUntilFinished) {
                            }
                            public void onFinish() {
                                cdw_mostrarFC.setVisibility(View.VISIBLE);
                                pantalla_registro.setVisibility(View.GONE);
                            }
                        }.start();

                        count_beat=0;
                        textViewFR_mostrar.setText(new3string);

                    }

                }
            }

        }
        else if (val3==-1 && val2==-1 && val1==-1  && val5>=0){
            txtView_Info.setText("Leyendo...");
        }
        else if (val3>=0 && val2==-1 && val1==-1  && val5==-1){
            txtView_Info.setText("No hay dedo...");
            txtView_FR.setText("");
        }
        else{
            txtView_Info.setText("");
        }
    }

    public void obtenerTiempo(){
        //Hora actual
        calendar=Calendar.getInstance();
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

    public void compartirRegistro(){

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

                            PersonasDialog=new Dialog(FCRegistrarActivity.this);
                            PersonasDialog.setContentView(R.layout.personas_dialog);
                            DescargarPersonas descargarPersonas=new DescargarPersonas(FCRegistrarActivity.this,FCRegistrarActivity.this);
                            descargarPersonas.actualizar(PersonasDialog,"updateAct");

                            PersonasDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if(preferences_archivos.getString("PersonasDescargadas","NO").equals("SI")){
                                        mostrarPersonas();
                                    }
                                }
                            });

                        }
                        else if(task.getResult().getValue().toString().equals("NO")){
                            mostrarPersonas();
                        }

                    }
                }
            });
        }
        else {
            Toast.makeText(FCRegistrarActivity.this,"Sin conexión a internet para compartir", Toast.LENGTH_SHORT).show();
        }

    }

    public void mostrarPersonas(){
        lista_personas.setContentView(R.layout.lista_personas);
        recyclerView=lista_personas.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FCRegistrarActivity.this));
        mUsers=new ArrayList<>();

        MyDataBasePersonas myDataBasePersonas=new MyDataBasePersonas(FCRegistrarActivity.this);
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
                String fecha=cursor.getString(4);
                String imageurl=cursor.getString(5);

                User user=new User();
                user.setId(personasid);
                user.setUsername(nombre);
                user.setCorreo(correo);
                user.setFecha(fecha);
                user.setImageurl(imageurl);
                mUsers.add(user);
            }
        }

        datosRegistro[0]=textViewFR_mostrar.getText().toString();
        datosRegistro[1]=titulo_registro.getText().toString().trim();
        datosRegistro[2]=estado.getText().toString().trim();
        obtenerTiempo();
        datosRegistro[3]= diaStr+"/"+mesStr+"/"+año+"  "+horaStr+":"+minutoStr+":"+segundoStr;

        userAdapter=new UserAdapter(FCRegistrarActivity.this,mUsers,"FCregistro",datosRegistro);
        recyclerView.setAdapter(userAdapter);

        lista_personas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Perfil.setCanceledOnTouchOutside(false);
        lista_personas.show();
    }

    public void inicializarchart(){
        chart = findViewById(R.id.grafica_fc);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText("Frecuencia cardiaca");
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = chart.getXAxis();
        xl.setTextColor(Color.GRAY);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(120f);
        leftAxis.setAxisMinimum(30f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void addEntry() {
        float dato=Float.parseFloat(txtView_FR.getText().toString());

        //Revisar el mayor
        System.arraycopy(datos_fc, 0, datos_fc, 1, datos_fc.length - 1);
        datos_fc[0]= dato;
        float dato_mayor=0f;
        float dato_menor=300f;
        for (int i=0;i<datos_fc.length;i++){
            if(datos_fc[i]>dato_mayor){
                dato_mayor=datos_fc[i];
            }
            if(datos_fc[i]<dato_menor){
                dato_menor=datos_fc[i];
            }

        }

        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            data.addEntry(new Entry(set.getEntryCount(), (float) dato), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(10);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());


            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setAxisMaximum(dato_mayor+10f);
            leftAxis.setAxisMinimum(dato_menor-10f);


            //chart.setAutoScaleMinMaxEnabled(true);

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.GRAY);
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(2f);
        set.setCircleRadius(3f);
        set.setFillAlpha(65);
        set.setFillColor(Color.GRAY);
        set.setHighLightColor(Color.GRAY);
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        StringBuffer sb;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] byte_in = new byte[1];
            sb = new StringBuffer();

            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    //isReader = new InputStreamReader(mmInStream);
                    //reader = new BufferedReader(isReader);
                    mmInStream.read(byte_in);
                    char ch = (char) byte_in[0];
                    Log.d("TAG14", "Datos es: "+ch);
                    sb.append(ch);
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    handler.obtainMessage(MESSAGE_READ, readMessage).sendToTarget();
                }
                catch (IOException e) {
                    break;
                }

            }

        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            if(!falloBtConexion){
                connectedThread.write("N");
            }
            createConnectThread.cancel();

        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            if(!falloBtConexion){
                connectedThread.write("N");
            }
            createConnectThread.cancel();
        }
    }
}