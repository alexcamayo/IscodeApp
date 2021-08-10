package com.proyecto.iscodeapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.iscodeapp.R;
import com.proyecto.iscodeapp.UpdateStickNoteActivity;

import java.util.ArrayList;

public class RecordatoriosAdapter extends RecyclerView.Adapter<RecordatoriosAdapter.MyViewHolder> {

    private Context context;
    private ArrayList   COLUMNA_ID, TITULO_REC, CONTENIDO_REC, COLOR_REC, FECHA_REC,HORA_REC,IMPORTANCIA_REC;
    Activity activity;



    public RecordatoriosAdapter(Activity activity, Context context, ArrayList COLUMNA_ID, ArrayList TITULO_REC, ArrayList CONTENIDO_REC,
                                ArrayList COLOR_REC, ArrayList FECHA_REC, ArrayList HORA_REC, ArrayList IMPORTANCIA_REC){

        this.activity=activity;
        this.context=context;
        this.COLUMNA_ID=COLUMNA_ID;
        this.TITULO_REC=TITULO_REC;
        this.CONTENIDO_REC=CONTENIDO_REC;
        this.COLOR_REC=COLOR_REC;
        this.FECHA_REC=FECHA_REC;
        this.HORA_REC=HORA_REC;
        this.IMPORTANCIA_REC=IMPORTANCIA_REC;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recordatorio_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.layout_myrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, UpdateStickNoteActivity.class);
                intent.putExtra("COLUMNA",String.valueOf(COLUMNA_ID.get(position)));
                /*intent.putExtra("TITULO",String.valueOf(TITULO_REC.get(position)));
                intent.putExtra("CONTENIDO",String.valueOf(CONTENIDO_REC.get(position)));
                intent.putExtra("COLOR",String.valueOf(COLOR_REC.get(position)));
                intent.putExtra("FECHA",String.valueOf(FECHA_REC.get(position)));
                intent.putExtra("HORA",String.valueOf(HORA_REC.get(position)));
                intent.putExtra("IMPORTANCIA",String.valueOf(IMPORTANCIA_REC.get(position)));*/
                activity.startActivity(intent);
                activity.finish();


            }
        });


        //Objetos en la nota:
        holder.TITULO_REC.setText(String.valueOf(TITULO_REC.get(position)));
        holder.FECHA_REC.setText(String.valueOf(FECHA_REC.get(position)));
        holder.HORA_REC.setText(String.valueOf(HORA_REC.get(position)));

        //Adaptar el color
        String color=String.valueOf(COLOR_REC.get(position));
        switch (Integer.parseInt(color)){
            case 0:
                holder.recordatorio_item.getBackground().setTint(activity.getColor(R.color.Nota1));
                break;
            case 1:
                holder.recordatorio_item.getBackground().setTint(activity.getColor(R.color.Nota2));
                break;
            case 2:
                holder.recordatorio_item.getBackground().setTint(activity.getColor(R.color.Nota3));
                break;
            case 3:
                holder.recordatorio_item.getBackground().setTint(activity.getColor(R.color.Nota4));
                break;
        }

        //Adaptar la importancia
        String importancia=String.valueOf(IMPORTANCIA_REC.get(position));
        if(importancia.equals("SI")){
            holder.importancia_nota.setVisibility(View.VISIBLE);
        }else if(importancia.equals("NO")){
            holder.importancia_nota.setVisibility(View.GONE);
        }

        Log.d("tag550","Color: "+color);

    }

    @Override
    public int getItemCount() {
        return TITULO_REC.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView TITULO_REC ,FECHA_REC, HORA_REC;
        LinearLayout layout_myrow;
        CardView recordatorio_item;
        ImageView importancia_nota;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layout_myrow=itemView.findViewById(R.id.layout_myrow);
            TITULO_REC=itemView.findViewById(R.id.tvw_titulo_myrow);
            FECHA_REC=itemView.findViewById(R.id.tvw_fecha_myrow);
            HORA_REC=itemView.findViewById(R.id.tvw_hora_myrow);
            importancia_nota=itemView.findViewById(R.id.ivw_importancia);
            recordatorio_item=itemView.findViewById(R.id.recordatorio_item);

            importancia_nota.setVisibility(View.GONE);

        }
    }
}
