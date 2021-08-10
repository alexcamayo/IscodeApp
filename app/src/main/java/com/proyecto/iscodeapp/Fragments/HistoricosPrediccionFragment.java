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

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.proyecto.iscodeapp.Adapters.HistoricosPrediccionAdapter;
import com.proyecto.iscodeapp.Databases.MyDataBaseHelper;

import java.util.ArrayList;
import java.util.List;

import com.proyecto.iscodeapp.Databases.SubirDb;
import com.proyecto.iscodeapp.Models.Chat;
import com.proyecto.iscodeapp.Models.Prediccion;
import com.proyecto.iscodeapp.R;

public class HistoricosPrediccionFragment extends Fragment {
    RecyclerView recyclerView;
    MyDataBaseHelper myDB;
    ArrayList<String> COLUMNA_ID_PRED,TITULO_PRED,VALOR_PRED,FECHA_PRED,HORA_PRED,MODELO;
    ArrayList<String> ATR1,ATR2,ATR3,ATR4,ATR5,ATR6,ATR7,ATR8,ATR9,ATR10,ATR11,ATR12,ATR13;
    HistoricosPrediccionAdapter customadapter;
    SwipeRefreshLayout refrescar_lista;

    Dialog opciones;
    SharedPreferences preferences_archivos;
    String hayDb="";

    List<Prediccion> mPrediccion;

    RelativeLayout layout_default;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_historicos_prediccion, container, false);

        preferences_archivos= getActivity().getSharedPreferences("ARCHIVOS_USUARIO", Context.MODE_PRIVATE);
        hayDb=preferences_archivos.getString("HayDBrec","");

        //
        opciones=new Dialog(getContext());
        opciones.setContentView(R.layout.opcioneshistoricospred);
        recyclerView = view.findViewById(R.id.recycler_view);
        refrescar_lista=view.findViewById(R.id.refrescar_lista);
        layout_default=view.findViewById(R.id.layout_default);

        if(hayDb.equals("SI")){
            layout_default.setVisibility(View.GONE);
            refrescar_lista.setVisibility(View.VISIBLE);
            SubirDb updb=new SubirDb(getActivity(),getContext());
            leerDataPRED();
            refrescar_lista.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    leerDataPRED();
                    updb.actualizardb();
                    refrescar_lista.setRefreshing(false);
                }
            });

            opciones.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    leerDataPRED();
                    updb.actualizardb();
                }
            });
        }
        else{
            layout_default.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            refrescar_lista.setVisibility(View.GONE);
        }




        return view;
    }

    public void leerDataPRED(){
        myDB = new MyDataBaseHelper(getContext());
        COLUMNA_ID_PRED = new ArrayList<>();
        TITULO_PRED = new ArrayList<>();
        VALOR_PRED = new ArrayList<>();
        FECHA_PRED = new ArrayList<>();
        HORA_PRED = new ArrayList<>();
        MODELO=new ArrayList<>();
        ATR1=new ArrayList<>();
        ATR2=new ArrayList<>();
        ATR3=new ArrayList<>();
        ATR4=new ArrayList<>();
        ATR5=new ArrayList<>();
        ATR6=new ArrayList<>();
        ATR7=new ArrayList<>();
        ATR8=new ArrayList<>();
        ATR9=new ArrayList<>();
        ATR10=new ArrayList<>();
        ATR11=new ArrayList<>();
        ATR12=new ArrayList<>();
        ATR13=new ArrayList<>();



        Cursor cursor=myDB.readAllDataPRED();


        if(cursor.getCount()==0){
            layout_default.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            refrescar_lista.setVisibility(View.GONE);
        }
        else {
            while (cursor.moveToNext()) {
                COLUMNA_ID_PRED.add(cursor.getString(0));
                TITULO_PRED.add(cursor.getString(1));
                VALOR_PRED.add(cursor.getString(2));
                FECHA_PRED.add(cursor.getString(3));
                HORA_PRED.add(cursor.getString(4));
                MODELO.add(cursor.getString(5));
                ATR1.add(cursor.getString(6));
                ATR2.add(cursor.getString(7));
                ATR3.add(cursor.getString(8));
                ATR4.add(cursor.getString(9));
                ATR5.add(cursor.getString(10));
                ATR6.add(cursor.getString(11));
                ATR7.add(cursor.getString(12));
                ATR8.add(cursor.getString(13));
                ATR9.add(cursor.getString(14));
                ATR10.add(cursor.getString(15));
                ATR11.add(cursor.getString(16));
                ATR12.add(cursor.getString(17));
                ATR13.add(cursor.getString(18));


            }
        }

        customadapter=new HistoricosPrediccionAdapter(getActivity(),getContext(),COLUMNA_ID_PRED, TITULO_PRED,VALOR_PRED,
                FECHA_PRED,HORA_PRED,MODELO,opciones,
                ATR1,ATR2,ATR3,ATR4,ATR5,ATR6,ATR7,ATR8,ATR9,ATR10,ATR11,ATR12,ATR13);

        recyclerView.setAdapter(customadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager( getContext()));
    }

}