package com.proyecto.iscodeapp.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.proyecto.iscodeapp.Adapters.HistoricosFCAdapter;
import com.proyecto.iscodeapp.Databases.MyDataBaseHelper;

import java.util.ArrayList;


import com.proyecto.iscodeapp.Databases.SubirDb;
import com.proyecto.iscodeapp.R;

public class HistoricosFCFragment extends Fragment {

    RecyclerView recyclerView;
    MyDataBaseHelper myDB;
    ArrayList<String> COLUMNA_ID_FC,TITULO_FC,VALOR_FC,ESTADO_FC,FECHA_FC,HORA_FC;
    HistoricosFCAdapter customadapter;
    SwipeRefreshLayout refrescar_lista;
    RelativeLayout layout_default;

    Dialog opciones;
    SharedPreferences preferences_archivos;
    String hayDb="";

    //Line chart
    LineChart chart;
    float[] datos_fc = new float[] {30f, 30f, 30f, 30f, 30f, 30f};
    float count=0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_historicos_f_c, container, false);

        preferences_archivos= getActivity().getSharedPreferences("ARCHIVOS_USUARIO", Context.MODE_PRIVATE);
        hayDb=preferences_archivos.getString("HayDBrec","");

        //
        opciones=new Dialog(getContext());
        opciones.setContentView(R.layout.opcioneshistoricosfc);
        recyclerView = view.findViewById(R.id.recycler_view);
        refrescar_lista=view.findViewById(R.id.refrescar_lista);
        layout_default=view.findViewById(R.id.layout_default);
        chart=view.findViewById(R.id.graficafc);


        if (hayDb.equals("SI")){
            //Subir descargar db
            layout_default.setVisibility(View.GONE);
            refrescar_lista.setVisibility(View.VISIBLE);
            SubirDb updb=new SubirDb(getActivity(),getContext());
            inicializarchart();
            leerDataFC();

            refrescar_lista.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    leerDataFC();
                    updb.actualizardb();
                    refrescar_lista.setRefreshing(false);
                }
            });

            opciones.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    datos_fc = new float[]{30f, 30f, 30f, 30f, 30f, 30f};
                    count=0f;
                    LineData data = new LineData();
                    data.setValueTextColor(Color.WHITE);
                    chart.setData(data);
                    leerDataFC();
                    updb.actualizardb();
                }
            });
        }

        else {
            layout_default.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            chart.setVisibility(View.GONE);
            refrescar_lista.setVisibility(View.GONE);
        }


        return view;
    }

    public void leerDataFC(){
        myDB = new MyDataBaseHelper(getContext());
        COLUMNA_ID_FC = new ArrayList<>();
        TITULO_FC = new ArrayList<>();
        VALOR_FC = new ArrayList<>();
        ESTADO_FC = new ArrayList<>();
        FECHA_FC = new ArrayList<>();
        HORA_FC = new ArrayList<>();

        Cursor cursor=myDB.readAllDataFC();

        if(cursor.getCount()==0){
            layout_default.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            chart.setVisibility(View.GONE);
            refrescar_lista.setVisibility(View.GONE);
        }
        else {
            while (cursor.moveToNext()) {
                COLUMNA_ID_FC.add(cursor.getString(0));
                TITULO_FC.add(cursor.getString(1));
                VALOR_FC.add(cursor.getString(2));
                ESTADO_FC.add(cursor.getString(3));
                FECHA_FC.add(cursor.getString(4));
                HORA_FC.add(cursor.getString(5));

                //Grafico
                float dato=Float.parseFloat(cursor.getString(2));
                String fecha=cursor.getString(4);

                count=count+1;
                addEntry(dato,count);

            }
        }

        customadapter=new HistoricosFCAdapter(getActivity(),getContext(),COLUMNA_ID_FC, TITULO_FC,VALOR_FC,
                ESTADO_FC, FECHA_FC,HORA_FC, opciones);

        recyclerView.setAdapter(customadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager( getContext()));
    }

    public void inicializarchart(){

        // enable description text
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText("Históricos FC");


        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        //chart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        chart.setData(data);
        chart.setExtraLeftOffset(20);
        chart.setExtraRightOffset(20);

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
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setAxisMinimum(0);


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(120f);
        leftAxis.setAxisMinimum(30f);
        leftAxis.setDrawGridLines(true);


        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void addEntry(float dato,float count) {

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
            data.addEntry(new Entry(count, (float) dato), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(5);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());


            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setAxisMaximum(dato_mayor+10f);
            leftAxis.setAxisMinimum(dato_menor-10f);
            chart.getXAxis().setAxisMaximum(count+1);


            //chart.setAutoScaleMinMaxEnabled(true);

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Frecuecia cardiaca en lpm");
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

}